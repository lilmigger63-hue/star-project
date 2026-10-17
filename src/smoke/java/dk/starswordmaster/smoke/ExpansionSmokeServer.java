package dk.starswordmaster.smoke;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;
/** Isolated fixtures and native checks; excluded from distributed mod. */
public final class ExpansionSmokeServer implements ModInitializer {
 private final Set<UUID> prepared=new HashSet<>();private boolean nativeDone;
 private final Map<UUID,ZombieEntity> targets=new HashMap<>();
 @Override public void onInitialize(){
  if(!Boolean.getBoolean("starsmoke.expansion"))return;
  ServerLifecycleEvents.SERVER_STARTED.register(server->{prepared.clear();targets.clear();nativeDone=false;});
  CommandRegistrationCallback.EVENT.register((dispatcher,access,env)->{
   var command=literal("starexp");for(int i=0;i<4;i++){int rank=i;command.then(literal("rank"+i).executes(c->{prepare(c.getSource().getPlayerOrThrow(),rank);return 1;}));}
   command.then(literal("unequip").executes(c->{var p=c.getSource().getPlayerOrThrow();p.equipStack(EquipmentSlot.MAINHAND,ItemStack.EMPTY);p.equipStack(EquipmentSlot.OFFHAND,ItemStack.EMPTY);AbilityPresentationEvents.cancel(p);return 1;}));
   command.then(literal("offhand").executes(c->{var p=c.getSource().getPlayerOrThrow();p.equipStack(EquipmentSlot.MAINHAND,ItemStack.EMPTY);p.equipStack(EquipmentSlot.OFFHAND,new ItemStack(Items.IRON_SWORD));return 1;}));
   command.then(literal("dimension").executes(c->{var p=c.getSource().getPlayerOrThrow();var key=p.getEntityWorld().getRegistryKey()==net.minecraft.world.World.OVERWORLD?net.minecraft.world.World.NETHER:net.minecraft.world.World.OVERWORLD;var world=p.getEntityWorld().getServer().getWorld(key);world.getChunk(0,0);p.teleport(world,.5,150,.5,Set.of(),0,0,false);p.setNoGravity(true);return 1;}));
   command.then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}));dispatcher.register(command);
  });
  ServerTickEvents.END_SERVER_TICK.register(server->{for(var p:server.getPlayerManager().getPlayerList())if(p.isLoaded()&&prepared.add(p.getUuid())){
   p.getAbilities().invulnerable=true;
   if(Boolean.getBoolean("starsmoke.expansion.native")&&!nativeDone){PresentationCodecChecks.run(p);ExpansionCombatChecks.run(p);nativeDone=true;}
   if(!StarSwordmaster.progress(p).learned().contains("aura_sense"))prepare(p,0);else equip(p);
  }});
 }
 @SuppressWarnings("unchecked") private void install(ServerPlayerEntity p,PlayerProgress data){try{var sf=StarSwordmaster.class.getDeclaredField("store");sf.setAccessible(true);var pf=PlayerStore.class.getDeclaredField("players");pf.setAccessible(true);((Map<UUID,PlayerProgress>)pf.get(sf.get(null))).put(p.getUuid(),data);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 private void prepare(ServerPlayerEntity player,int rank){
  int[] level={10,15,25,100},skill={3,10,20,50};var props=new Properties();props.setProperty("class","SWORDSMAN");props.setProperty("xp",Integer.toString(10*(level[rank]-1)*(level[rank]+5)));props.setProperty("practice.SWORD",Integer.toString(25*(skill[rank]-1)*(skill[rank]-1)));props.setProperty("mana","9999");props.setProperty("stamina","9999");
  String learned="quick_cut,aura_sense";if(rank>=1)learned+=",aura_awaken";if(rank>=2)learned+=",sword_mastery";if(rank==3)learned+=",astral_sword_sovereign,sovereign_judgment,heavenly_sword_domain";
  props.setProperty("learned",learned);props.setProperty("slot.0",rank==3?"sovereign_judgment":"quick_cut");var data=PlayerProgress.fromProperties(props);if(data.rank()!=rank+1)throw new AssertionError("Bad aura fixture rank");install(player,data);equip(player);StarSwordmaster.sync(player);ServerVisualState.sync(player);
 }
 private void equip(ServerPlayerEntity p){
  boolean b=p.getGameProfile().name().endsWith("B");double x=b?3.5:.5,z=b?6.5:.5;float yaw=b?180:0;
  p.getEntityWorld().setTimeOfDay(6000);p.clearStatusEffects();p.setNoGravity(true);p.getAbilities().invulnerable=true;p.teleport(p.getEntityWorld(),x,100,z,Set.of(),yaw,0,false);p.setVelocity(0,0,0);p.getInventory().setSelectedSlot(0);p.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));p.equipStack(EquipmentSlot.OFFHAND,ItemStack.EMPTY);
  var old=targets.remove(p.getUuid());if(old!=null)old.discard();var mob=new ZombieEntity(EntityType.ZOMBIE,p.getEntityWorld());mob.refreshPositionAndAngles(x,100,z+(b?-2.8:2.8),0,0);mob.setAiDisabled(true);mob.setNoGravity(true);mob.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(1000);mob.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(0);mob.setHealth(1000);p.getEntityWorld().spawnEntity(mob);targets.put(p.getUuid(),mob);
  StatAttributes.apply(p,StarSwordmaster.progress(p));StarSwordmaster.sync(p);ServerVisualState.send(p,p);
 }
}

package dk.starswordmaster.smoke;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.effect.*;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.*;
import static net.minecraft.server.command.CommandManager.*;
/** Isolated runtime fixtures, not included in the distributed JAR. */
public class AnimationSmokeServer implements ModInitializer {
 private final Map<UUID,ZombieEntity> targets=new HashMap<>();private final Map<UUID,WolfEntity> pets=new HashMap<>();private final Set<UUID> prepared=new HashSet<>();
 @Override public void onInitialize(){
  if(!Boolean.getBoolean("starsmoke.animations"))return;
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{var p=handler.player;p.getAbilities().invulnerable=true;p.setNoGravity(true);p.getHungerManager().setFoodLevel(6);});
  ServerTickEvents.END_SERVER_TICK.register(server->{for(var p:server.getPlayerManager().getPlayerList())if(p.getGameProfile().name().startsWith("AnimationSmoke")&&prepared.add(p.getUuid()))prepare(p,"quick_cut","ok");});
  ServerLivingEntityEvents.AFTER_DAMAGE.register((e,s,b,a,blocked)->{if(s.isOf(DamageTypes.PLAYER_ATTACK)&&s.getAttacker() instanceof ServerPlayerEntity p&&a>0)System.out.println("ANIMATION_SERVER PASS: Better Combat ordinary attack hit for "+p.getGameProfile().name());});
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(literal("staranimtest")
   .then(literal("prepare").then(argument("ability",StringArgumentType.word()).then(argument("mode",StringArgumentType.word()).executes(c->{prepare(c.getSource().getPlayerOrThrow(),StringArgumentType.getString(c,"ability"),StringArgumentType.getString(c,"mode"));return 1;}))))
   .then(literal("damage").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.damage(p.getEntityWorld(),p.getDamageSources().magic(),2);p.getAbilities().invulnerable=true;return 1;}))
   .then(literal("weapon").executes(c->{c.getSource().getPlayerOrThrow().equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.STICK));return 1;}))
   .then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}))
   .then(literal("audit").executes(c->{var p=c.getSource().getPlayerOrThrow();var pet=pets.get(p.getUuid());if(pet==null||pet.getHealth()!=pet.getMaxHealth())throw new AssertionError("Friendly pet damaged by ability");System.out.println("ANIMATION_SERVER PASS: friendly pet preserved for "+p.getGameProfile().name());return 1;}))));
 }
 @SuppressWarnings("unchecked") private void prepare(ServerPlayerEntity player,String id,String mode){
  AbilityPresentationEvents.cancel(player);var a=Ability.ALL.get(id);if(a==null)throw new AssertionError("Unknown fixture ability");
  var properties=new Properties();properties.setProperty("schema","1");properties.setProperty("xp","200000");properties.setProperty("class",(a.exclusiveClass()==null?PlayerProgress.PlayerClass.SWORDSMAN:a.exclusiveClass()).name());
  properties.setProperty("mana",mode.equals("mana")?"0":"9999");properties.setProperty("stamina",mode.equals("stamina")?"0":"9999");
  for(var skill:Skill.values())properties.setProperty("practice."+skill,"250000");properties.setProperty("learned",String.join(",",Ability.ALL.keySet()));properties.setProperty("slot.0",id);
  if(mode.equals("cooldown"))properties.setProperty("cooldown."+id,"999");
  var data=PlayerProgress.fromProperties(properties);if(!data.learned().contains(id))throw new AssertionError("Invalid fixture prerequisites "+id);
  try{var sf=StarSwordmaster.class.getDeclaredField("store");sf.setAccessible(true);var store=sf.get(null);var pf=PlayerStore.class.getDeclaredField("players");pf.setAccessible(true);((Map<UUID,PlayerProgress>)pf.get(store)).put(player.getUuid(),data);}catch(ReflectiveOperationException e){throw new AssertionError(e);}
  if(Boolean.getBoolean("starsmoke.finishing")){var w=player.getEntityWorld();w.setTimeOfDay(6000);for(int x=-20;x<=34;x++)for(int z=-20;z<=20;z++)w.setBlockState(new net.minecraft.util.math.BlockPos(x,79,z),net.minecraft.block.Blocks.STONE.getDefaultState());}
  player.clearStatusEffects();StatAttributes.apply(player,data);player.setHealth(mode.equals("fullhealth")?player.getMaxHealth():10);player.getHungerManager().setFoodLevel(6);player.setNoGravity(true);player.getAbilities().invulnerable=true;
  boolean observer=player.getGameProfile().name().endsWith("B");boolean finishing=Boolean.getBoolean("starsmoke.finishing");double x=observer?(finishing?6.5:12.5):.5,z=observer&&finishing?-6.5:.5;float yaw=observer&&finishing?40:0;var world=player.getEntityWorld();player.teleport(world,x,80,z,Set.of(),yaw,0,false);player.setVelocity(0,0,0);
  player.equipStack(EquipmentSlot.MAINHAND,mode.equals("unarmed")?ItemStack.EMPTY:new ItemStack(mode.equals("weapon")?Items.STICK:Items.IRON_SWORD));
  var old=targets.remove(player.getUuid());if(old!=null)old.discard();
  // Prior local runs persist their AI-disabled targets. Clear only this fixture lane.
  for(var stale:world.getEntitiesByClass(ZombieEntity.class,new net.minecraft.util.math.Box(x-4,76,-20,x+4,84,20),ZombieEntity::isAiDisabled))stale.discard();
  if(!pets.containsKey(player.getUuid()))for(var stale:world.getEntitiesByClass(WolfEntity.class,new net.minecraft.util.math.Box(x-4,76,-20,x+4,84,20),WolfEntity::isAiDisabled))stale.discard();
  if(!mode.equals("target")){
   var mob=new ZombieEntity(EntityType.ZOMBIE,world);mob.refreshPositionAndAngles(x,80,z+2.8,0,0);mob.setAiDisabled(true);mob.setNoGravity(true);if(Boolean.getBoolean("starsmoke.finishing"))mob.equipStack(EquipmentSlot.HEAD,new ItemStack(Items.IRON_HELMET));mob.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(1000);mob.setHealth(1000);mob.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,10000));world.spawnEntity(mob);targets.put(player.getUuid(),mob);
  }
  var pet=pets.get(player.getUuid());if(pet==null||!pet.isAlive()){
   pet=new WolfEntity(EntityType.WOLF,world);pet.refreshPositionAndAngles(x+1.5,80,2,0,0);pet.setAiDisabled(true);pet.setNoGravity(true);pet.setOwner(player);pet.setTamed(true,true);world.spawnEntity(pet);pets.put(player.getUuid(),pet);
  }
  StarSwordmaster.sync(player);
 }
}

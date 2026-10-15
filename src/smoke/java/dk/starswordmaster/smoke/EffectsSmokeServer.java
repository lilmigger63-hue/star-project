package dk.starswordmaster.smoke;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.*;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;
/** Real server checks and fixtures, excluded from the distributed mod. */
public class EffectsSmokeServer implements ModInitializer {
 private record Sample(ServerPlayerEntity player,double mana,double rate,int ticks){}
 private final Map<UUID,Sample> samples=new HashMap<>();
 private final Set<UUID> prepared=new HashSet<>();
 private void check(boolean value,String message){if(!value)throw new AssertionError("STAT_EFFECTS_SERVER FAIL: "+message);System.out.println("STAT_EFFECTS_SERVER PASS: "+message);}
 private void equal(double expected,double actual,String message){check(Math.abs(expected-actual)<0.0001,message+" ("+actual+")");}
 @Override public void onInitialize(){
  if(!Boolean.getBoolean("starsmoke.effects"))return;
  ServerLifecycleEvents.SERVER_STARTING.register(server->{
   samples.clear();
   try{
    Path dir=server.getSavePath(WorldSavePath.ROOT).resolve("data/starswordmaster");Files.createDirectories(dir);
    for(String role:new String[]{"A","B"}){
     UUID id=UUID.nameUUIDFromBytes(("OfflinePlayer:PointsSmoke"+role).getBytes(StandardCharsets.UTF_8));if(server.isSingleplayer()&&role.equals("A"))id=server.getHostProfile().id();Path file=dir.resolve(id+".properties");
     if(Files.exists(file)&&!Boolean.getBoolean("starsmoke.effects.reset"))continue;
     var p=new Properties();p.setProperty("schema","1");p.setProperty("xp","200000");p.setProperty("class","MAGE");p.setProperty("style","HEAVY");p.setProperty("mana","0");
     for(var stat:PlayerProgress.Stat.values())p.setProperty("stat."+stat,role.equals("A")?"10":"100");for(var skill:Skill.values())p.setProperty("practice."+skill,"10000");
     p.setProperty("learned","spark,mana_control,arcane_mastery,fire_affinity,frost_touch,frost_affinity,mend,restorative_affinity,quick_cut,quick_dash,flow_training,heavy_cut,heavy_sweep,impact_training");
     try(var writer=Files.newBufferedWriter(file)){p.store(writer,"Stat effects test fixture; original schema");}
    }
   }catch(java.io.IOException e){throw new java.io.UncheckedIOException(e);}
  });
  ServerLifecycleEvents.SERVER_STARTED.register(server->StatAttributeChecks.run());
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{
   var p=handler.player;p.getAbilities().invulnerable=true;
   if(prepared.add(p.getUuid())){p.setHealth(10);p.getHungerManager().setFoodLevel(6);}
  });
  ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer,newPlayer,alive)->{newPlayer.getAbilities().invulnerable=true;newPlayer.getHungerManager().setFoodLevel(6);});
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(literal("stareffectstest")
   .then(literal("check").executes(c->{test(c.getSource().getPlayerOrThrow());return 1;}))
   .then(literal("regen").executes(c->{sample(c.getSource().getPlayerOrThrow(),false);return 1;}))
   .then(literal("combat").executes(c->{sample(c.getSource().getPlayerOrThrow(),true);return 1;}))
   .then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}))
   .then(literal("alive").executes(c->{var p=c.getSource().getPlayerOrThrow();p.setHealth(p.getMaxHealth()-0.5f);var next=p.getEntityWorld().getServer().getPlayerManager().respawnPlayer(p,true,net.minecraft.entity.Entity.RemovalReason.CHANGED_DIMENSION);next.networkHandler.player=next;next.networkHandler.syncWithPlayerPosition();equal(next.getMaxHealth()-0.5,next.getHealth(),"alive respawn preserves partial health above 20");return 1;}))
   .then(literal("partial").executes(c->{var p=c.getSource().getPlayerOrThrow();equal(p.getMaxHealth()-0.5,p.getHealth(),"reload preserves partial health above 20");return 1;}))
   .then(literal("health").executes(c->{var p=c.getSource().getPlayerOrThrow();equal(p.getMaxHealth(),p.getHealth(),p.getName().getString()+" current health preserved after lifecycle");check(p.getAttributeInstance(EntityAttributes.MAX_HEALTH).getPersistentModifiers().stream().anyMatch(m->m.id().equals(StatAttributes.HEALTH_ID)),"max health modifier is persistent");return 1;}))));
  ServerTickEvents.END_SERVER_TICK.register(server->{
   for(var entry:new ArrayList<>(samples.entrySet())){
    var s=entry.getValue();var p=StarSwordmaster.progress(s.player());
    if(s.ticks()==0)samples.put(entry.getKey(),new Sample(s.player(),p.mana(),s.rate(),1));
    else if(s.ticks()==20){equal(s.rate()*20,p.mana()-s.mana(),s.player().getName().getString()+" 20 authoritative regen ticks at "+s.rate());samples.remove(entry.getKey());}
    else samples.put(entry.getKey(),new Sample(s.player(),s.mana(),s.rate(),s.ticks()+1));
   }
  });
 }
 private void sample(ServerPlayerEntity player,boolean combat){if(combat)StarSwordmaster.combat(player);samples.put(player.getUuid(),new Sample(player,0,StarSwordmaster.progress(player).manaRegenPerTick(combat),0));}
 private void test(ServerPlayerEntity player){
  var p=StarSwordmaster.progress(player);var b=p.bonuses();String name=player.getName().getString();var before=p.toProperties();float health=player.getHealth();
  for(int i=0;i<200;i++)StatAttributes.apply(player,p);
  equal(1+b.miningSpeed(),player.getAttributeValue(EntityAttributes.BLOCK_BREAK_SPEED),name+" mining attribute");
  equal(0.1*(1+b.movementSpeed()),player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED),name+" movement attribute");
  equal(20+b.health(),player.getMaxHealth(),name+" max health");equal(health,player.getHealth(),"applying attributes does not heal");
  for(var attribute:List.of(EntityAttributes.BLOCK_BREAK_SPEED,EntityAttributes.MOVEMENT_SPEED,EntityAttributes.MAX_HEALTH))check(player.getAttributeInstance(attribute).getModifiers().stream().filter(m->Set.of(StatAttributes.MINING_ID,StatAttributes.SPEED_ID,StatAttributes.HEALTH_ID).contains(m.id())).count()==1,"one owned modifier after 200 updates: "+attribute);
  check(before.equals(p.toProperties()),"attribute updates do not modify progression/save fields");
  var held=player.getMainHandStack().copy();player.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_PICKAXE));var stone=Blocks.STONE.getDefaultState();var mining=player.getAttributeInstance(EntityAttributes.BLOCK_BREAK_SPEED);
  mining.removeModifier(StatAttributes.MINING_ID);float base=player.getBlockBreakingSpeed(stone);StatAttributes.apply(player,p);equal(1+b.miningSpeed(),player.getBlockBreakingSpeed(stone)/base,"normal iron pickaxe mining multiplier");
  player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE,100,0));mining.removeModifier(StatAttributes.MINING_ID);base=player.getBlockBreakingSpeed(stone);StatAttributes.apply(player,p);equal(1+b.miningSpeed(),player.getBlockBreakingSpeed(stone)/base,"mining combines with Haste");player.removeStatusEffect(StatusEffects.HASTE);
  check(player.canHarvest(Blocks.DIAMOND_ORE.getDefaultState()),"iron pickaxe retains harvest requirement");player.equipStack(EquipmentSlot.MAINHAND,ItemStack.EMPTY);check(!player.canHarvest(Blocks.DIAMOND_ORE.getDefaultState()),"bare hands still cannot harvest diamond ore");equal(0,Blocks.BEDROCK.getDefaultState().calcBlockBreakingDelta(player,player.getEntityWorld(),player.getBlockPos()),"bedrock remains unbreakable");player.equipStack(EquipmentSlot.MAINHAND,held);
  player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,100,0));player.setSprinting(true);equal(0.1*(1+b.movementSpeed())*1.2*1.3,player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED),"stat speed combines with sprint and potion");player.setSprinting(false);player.removeStatusEffect(StatusEffects.SPEED);
  player.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST,100,0));equal(24+b.health(),player.getMaxHealth(),"stat health combines with Health Boost");player.removeStatusEffect(StatusEffects.HEALTH_BOOST);
  check(p.rank()==3&&p.learned().size()==14,"class rank and passive fixture valid");equal(338+5*p.stat(PlayerProgress.Stat.SPIRIT),p.maxMana(),"rank perks plus Spirit mana");equal(199+5*p.stat(PlayerProgress.Stat.ENDURANCE),p.maxStamina(),"existing stamina plus Endurance");equal((1+0.025*p.stat(PlayerProgress.Stat.STRENGTH)+0.15)*1.15,p.damageMultiplier(Ability.ALL.get("quick_cut")),"existing heavy style, rank and Strength damage");equal(10.8,p.manaCost(Ability.ALL.get("spark")),"fire affinity mana cost unchanged");equal(1.25,p.healingMultiplier(),"restorative passive unchanged");
 }
}

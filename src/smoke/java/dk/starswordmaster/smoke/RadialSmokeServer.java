package dk.starswordmaster.smoke;

import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import java.math.BigInteger;
import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;

/** Isolated world fixtures and real Fabric server-event checks; excluded from the production jar. */
public final class RadialSmokeServer implements ModInitializer {
 private final Set<UUID> prepared=new HashSet<>();
 private final Map<UUID,Properties> beforeReset=new HashMap<>();
 private int checks;
 private void check(boolean ok,String message){if(!ok)throw new AssertionError("RADIAL_SERVER FAIL: "+message);checks++;System.out.println("RADIAL_SERVER PASS: "+message);}
 @Override public void onInitialize(){
  if(!Boolean.getBoolean("starsmoke.radial"))return;
  ServerTickEvents.END_SERVER_TICK.register(s->{for(var p:s.getPlayerManager().getPlayerList())if(p.isLoaded()&&prepared.add(p.getUuid())){
   p.getAbilities().invulnerable=true;p.setNoGravity(true);p.teleport(p.getEntityWorld(),p.getGameProfile().name().endsWith("B")?4.5:.5,150,.5,Set.of(),0,0,false);fixture(p);
  }});
  CommandRegistrationCallback.EVENT.register((d,a,e)->d.register(literal("starradial")
   .then(literal("native").executes(c->{nativeChecks(c.getSource().getPlayerOrThrow());fixture(c.getSource().getPlayerOrThrow());return 1;}))
   .then(literal("fixture").executes(c->{fixture(c.getSource().getPlayerOrThrow());return 1;}))
   .then(literal("verify").executes(c->{verifyReset(c.getSource().getPlayerOrThrow());return 1;}))
   .then(literal("persist").executes(c->{var p=c.getSource().getPlayerOrThrow();verifyReset(p);Properties old=StarSwordmaster.progress(p).toProperties();store().forget(p.getUuid());var loaded=StarSwordmaster.progress(p);check(old.equals(loaded.toProperties()),"save/forget/disk reload preserves exact reset character");StarSwordmaster.sync(p);System.out.println("RADIAL_SERVER COMPLETE checks="+checks);return 1;}))
   .then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}))));
 }
 private PlayerStore store(){try{var f=StarSwordmaster.class.getDeclaredField("store");f.setAccessible(true);return (PlayerStore)f.get(null);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 @SuppressWarnings("unchecked") private void install(ServerPlayerEntity p,PlayerProgress progress){try{var f=PlayerStore.class.getDeclaredField("players");f.setAccessible(true);((Map<UUID,PlayerProgress>)f.get(store())).put(p.getUuid(),progress);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 private PlayerProgress fresh(ServerPlayerEntity p,long xp){var props=new Properties();props.setProperty("xp",Long.toString(xp));props.setProperty("xpCurveVersion","2");var data=PlayerProgress.fromProperties(props);install(p,data);return data;}
 private void fixture(ServerPlayerEntity p){
  var props=new Properties();props.setProperty("xp","103950");props.setProperty("xpCurveVersion","2");props.setProperty("class","SWORDSMAN");props.setProperty("style","HEAVY");
  for(var skill:Skill.values())props.setProperty("practice."+skill,"250000");
  props.setProperty("learned",String.join(",",Ability.ALL.keySet()));props.setProperty("slot.0","quick_cut");props.setProperty("stat.STRENGTH","7");props.setProperty("stat.ENDURANCE","5");props.setProperty("stat.SPIRIT","3");props.setProperty("mana","999999");props.setProperty("stamina","999999");
  var data=PlayerProgress.fromProperties(props);check(data.rank()==4&&data.learned().contains("quick_cut"),"rank-four native fixture contains legal perks");install(p,data);StatAttributes.apply(p,data);beforeReset.put(p.getUuid(),data.toProperties());store().save(p.getUuid());StarSwordmaster.sync(p);ServerVisualState.sync(p);
 }
 private ZombieEntity zombie(ServerPlayerEntity p,double hp){var z=new ZombieEntity(EntityType.ZOMBIE,p.getEntityWorld());z.refreshPositionAndAngles(p.getX(),p.getY(),p.getZ()+3,0,0);z.setAiDisabled(true);z.setNoGravity(true);z.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(hp);z.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(0);z.setHealth((float)hp);check(p.getEntityWorld().spawnEntity(z),"spawn actual hostile HP="+hp);return z;}
 private void kill(ServerPlayerEntity p,LivingEntity target,DamageSource source){check(target.damage(p.getEntityWorld(),source,1000000),"actual lethal server damage");check(target.isDead(),"real death callback completed");}
 private void nativeChecks(ServerPlayerEntity p){
  var other=p.getEntityWorld().getServer().getPlayerManager().getPlayerList().stream().filter(q->q!=p).findFirst().orElse(null);var otherBefore=other==null?null:StarSwordmaster.progress(other).toProperties();
  int vanilla=p.experienceLevel;float vanillaFraction=p.experienceProgress;
  for(String kind:List.of("melee","projectile","magic")){
   var progress=fresh(p,0);var z=zombie(p,20);var arrow=new ArrowEntity(EntityType.ARROW,p.getEntityWorld());arrow.setOwner(p);
   var source=switch(kind){case "melee"->p.getDamageSources().playerAttack(p);case "projectile"->p.getDamageSources().arrow(arrow,p);default->p.getDamageSources().indirectMagic(p,p);};
   try{kill(p,z,source);check(progress.totalXp().equals(BigInteger.valueOf(60)),kind+" maxHP20 awards exactly 60 to real attacker");
    ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(z,source);check(progress.totalXp().equals(BigInteger.valueOf(60)),"duplicate "+kind+" death callback cannot award twice");
   }finally{z.discard();arrow.discard();}
  }
  var progress=fresh(p,910);check(progress.level().intValueExact()==8,"native level-eight boundary fixture");var boss=zombie(p,1000);boss.setHealth(1);
  try{kill(p,boss,p.getDamageSources().indirectMagic(p,p));check(progress.totalXp().equals(BigInteger.valueOf(5410)),"1000 maxHP boss at one remaining HP awards 3000 times 1.5 exactly once");check(progress.level().intValueExact()>9&&progress.points().equals(progress.level().subtract(BigInteger.ONE))&&progress.statPoints().equals(progress.points().multiply(BigInteger.valueOf(5))),"one actual kill crosses multiple levels with exact perk/stat entitlement");}finally{boss.discard();}
  var xp=progress.totalXp();clearPracticeThrottle(p);StarSwordmaster.practice(p,Skill.FIRE,20);check(progress.totalXp().equals(xp)&&progress.toProperties().getProperty("practice.FIRE").equals("20"),"actual practice improves proficiency without character XP");
  var environmental=zombie(p,20);try{kill(p,environmental,p.getDamageSources().generic());check(progress.totalXp().equals(xp),"uncredited environmental death gives no XP");}finally{environmental.discard();}
  var cow=new CowEntity(EntityType.COW,p.getEntityWorld());cow.refreshPositionAndAngles(p.getX(),p.getY(),p.getZ()+3,0,0);p.getEntityWorld().spawnEntity(cow);try{kill(p,cow,p.getDamageSources().playerAttack(p));check(progress.totalXp().equals(xp),"disabled passive creature gives no XP");}finally{cow.discard();}
  var wolf=new WolfEntity(EntityType.WOLF,p.getEntityWorld());wolf.refreshPositionAndAngles(p.getX(),p.getY(),p.getZ()+3,0,0);wolf.setOwner(p);wolf.setTamed(true,true);p.getEntityWorld().spawnEntity(wolf);try{kill(p,wolf,p.getDamageSources().playerAttack(p));check(progress.totalXp().equals(xp),"own pet death gives no XP");}finally{wolf.discard();}
  if(other!=null){ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(other,p.getDamageSources().playerAttack(p));check(progress.totalXp().equals(xp),"PvP death callback gives no XP");check(sameCharacter(otherBefore,StarSwordmaster.progress(other).toProperties()),"all kills and rewards leave other player's progression untouched");}
  var summonedProgress=fresh(p,0);var evoker=new EvokerEntity(EntityType.EVOKER,p.getEntityWorld());var vex=new VexEntity(EntityType.VEX,p.getEntityWorld());
  evoker.refreshPositionAndAngles(p.getX()+2,p.getY(),p.getZ()+3,0,0);evoker.setAiDisabled(true);evoker.setNoGravity(true);vex.refreshPositionAndAngles(p.getX(),p.getY(),p.getZ()+3,0,0);vex.setAiDisabled(true);vex.setNoGravity(true);vex.setOwner(evoker);p.getEntityWorld().spawnEntity(evoker);p.getEntityWorld().spawnEntity(vex);
  try{check(vex.getMaxHealth()==14,"native hostile summon retains vanilla max HP");kill(p,vex,p.getDamageSources().indirectMagic(p,p));check(summonedProgress.totalXp().equals(BigInteger.valueOf(42)),"hostile Evoker-owned Vex remains eligible for exact maxHP kill XP");}finally{vex.discard();evoker.discard();}
  check(p.experienceLevel==vanilla&&p.experienceProgress==vanillaFraction,"native kills leave vanilla player XP unchanged");
  var empty=new PlayerProgress();check(!empty.resetLearnedSkills()&&empty.points().signum()==0,"fresh reset grants no free points");System.out.println("RADIAL_NATIVE COMPLETE checks="+checks);
 }
 @SuppressWarnings("unchecked") private void clearPracticeThrottle(ServerPlayerEntity p){try{var f=StarSwordmaster.class.getDeclaredField("practiceAt");f.setAccessible(true);((Map<UUID,Long>)f.get(null)).remove(p.getUuid());}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 private boolean sameCharacter(Properties a,Properties b){for(String key:a.stringPropertyNames())if(!key.equals("mana")&&!key.equals("stamina")&&!key.startsWith("cooldown")&&!Objects.equals(a.getProperty(key),b.getProperty(key)))return false;return true;}
 private void verifyReset(ServerPlayerEntity p){
  var data=StarSwordmaster.progress(p);var old=beforeReset.get(p.getUuid());check(old!=null,"fixture baseline exists");
  check(data.learned().isEmpty()&&data.points().equals(data.level().subtract(BigInteger.ONE)),"all learned perks removed and exact level-minus-one refund");
  check(data.rank()==1,"rank immediately recalculated after reset");for(int i=0;i<5;i++)check(data.slot(i).isEmpty()&&!data.canCast(i),"hotbar slot "+i+" cleared and cannot cast");
  var now=data.toProperties();for(String key:old.stringPropertyNames())if(key.equals("class")||key.equals("style")||key.startsWith("xp")||key.startsWith("stat.")||key.startsWith("practice."))check(Objects.equals(old.getProperty(key),now.getProperty(key)),"reset preserves "+key);
  check(data.mana()<=data.maxMana()&&data.stamina()<=data.maxStamina(),"reduced resource caps applied immediately");
  var expected=PlayerProgress.fromProperties(now);check(Math.abs(p.getMaxHealth()-(20+expected.bonuses().health()))<.001,"native max-health attribute reflects reset passives");
 }
}

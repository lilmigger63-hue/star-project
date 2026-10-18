package dk.starswordmaster.progression;
import dk.starswordmaster.PlayerStore;
import dk.starswordmaster.ui.StatusData;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import static dk.starswordmaster.progression.PlayerProgress.PlayerClass.*;
import static dk.starswordmaster.progression.PlayerProgress.Stat.*;

public class UltimateRankTest {
 private static final String[] IDS={"astral_sword_sovereign","celestial_archon","eclipse_sovereign"};
 private static final PlayerProgress.PlayerClass[] CLASSES={SWORDSMAN,MAGE,SPELLBLADE};
 private static final String[][] ROUTES={{"quick_cut","aura_awaken","sword_mastery"},{"spark","mana_control","arcane_mastery"},{"heavy_cut","element_binding","spellblade_mastery"}};
 private PlayerProgress ready(int c,int level,int skill){
  var p=new Properties();p.setProperty("xp",BigInteger.TEN.multiply(BigInteger.valueOf(level-1)).multiply(BigInteger.valueOf(level+5)).toString());
  p.setProperty("class",CLASSES[c].name());for(var s:Skill.values())p.setProperty("practice."+s,Integer.toString(25*(skill-1)*(skill-1)));
  p.setProperty("learned",String.join(",",ROUTES[c]));return PlayerProgress.fromProperties(p);
 }
 @Test public void requiresExactClassLevelSkillParentAndOnePerkPoint(){
  for(int c=0;c<3;c++){
   var a=Ability.ALL.get(IDS[c]);assertEquals(CLASSES[c],a.exclusiveClass());assertEquals(c==1?Skill.FIRE:Skill.SWORD,a.skill());assertEquals(20,a.requirement());assertEquals(25,a.characterRequirement());assertEquals(ROUTES[c][2],a.parent());assertFalse(a.active());
   assertFalse(ready(c,24,20).unlock(IDS[c]));assertFalse(ready(c,25,19).unlock(IDS[c]));
   var p=ready(c,25,20);var props=p.toProperties();props.setProperty("learned",ROUTES[c][0]+","+ROUTES[c][1]);assertFalse(PlayerProgress.fromProperties(props).unlock(IDS[c]));
   for(int other=0;other<3;other++)if(other!=c)assertFalse(ready(other,25,20).unlock(IDS[c]));
   var deficient=ready(c,25,20).toProperties();deficient.setProperty("practice."+a.skill(),"8100");assertFalse(PlayerProgress.fromProperties(deficient).unlock(IDS[c]));
   assertEquals(3,p.rank());var points=p.points();var stats=p.statPoints();assertTrue(p.unlock(IDS[c]));assertEquals(4,p.rank());
   assertEquals(points.subtract(BigInteger.ONE),p.points());assertEquals(stats,p.statPoints());assertFalse(p.unlock(IDS[c]));assertFalse(p.assign(0,IDS[c]));
   var last=ready(c,25,100);for(var candidate:Ability.ALL.values())if(!candidate.id().equals(IDS[c])&&last.points().compareTo(BigInteger.ONE)>0)last.unlock(candidate.id());
   assertEquals(BigInteger.ONE,last.points());assertTrue(last.unlock(IDS[c]));assertEquals(BigInteger.ZERO,last.points());
  }
  var exhausted=ready(0,25,100);for(var a:Ability.ALL.values())if(!a.id().equals(IDS[0])&&exhausted.points().signum()>0)exhausted.unlock(a.id());
  assertEquals(BigInteger.ZERO,exhausted.points());assertFalse(exhausted.unlock(IDS[0]));
 }
 @Test public void exactDerivedStatsPreserveAllocatedStatsAndUnspentPoints(){
  long[][] expected={{3,2,0,0},{0,0,1,4},{2,1,0,2}};
  for(int c=0;c<3;c++){
   var p=ready(c,25,20);for(var s:PlayerProgress.Stat.values())assertTrue(p.spendStat(s));var available=p.statPoints();assertTrue(p.unlock(IDS[c]));
   for(var s:PlayerProgress.Stat.values()){assertEquals(1,p.stat(s));assertEquals(1+expected[c][s.ordinal()]+(c==0&&s==AGILITY?2:0),p.effectiveStat(s));}
   assertEquals(available,p.statPoints());assertEquals(0.025*p.effectiveStat(STRENGTH),p.strengthDamageBonus(),1e-9);
   assertEquals(5*p.effectiveStat(ENDURANCE),p.enduranceStaminaBonus());assertEquals(5*p.effectiveStat(SPIRIT),p.spiritManaBonus());
   assertEquals(Math.min(.4,.01*p.effectiveStat(AGILITY)),p.agilityCooldownReduction(),1e-9);
   var d=StatusData.from(p,true).orElseThrow();assertEquals(IDS[c],d.rankNodeId());assertEquals(Long.valueOf(p.effectiveStat(SPIRIT)),d.effectiveStats().get(SPIRIT));
  }
 }
 @Test public void rankFourKeepsMasteryAndOnlyBoostsItsOffensiveCategory(){
  Set<String> swords=Set.of("astral_crescent","falling_star","phantom_blades","celestial_rush","sovereign_judgment","heavenly_sword_domain","aqua_blade","quake_blade","shadow_edge","quick_cut","heavy_cut","heavy_sweep","riposte","aura_wave","rapid_slash","crushing_edge","piercing_thrust","seismic_cut","flame_blade","frost_blade","glacier_sweep");
  Set<String> magic=Set.of("static_spark","chain_lightning","heavens_wrath","water_bolt","tidal_wave","ocean_prison","stone_spike","worldbreaker","shadow_bolt","curse_mark","eclipse_nova","spark","frost_touch","fire_burst","ice_lance","frost_nova","flame_lance","ember_ring","ice_prison");
  Set<String> enchanted=Set.of("aqua_blade","quake_blade","shadow_edge","flame_blade","frost_blade","glacier_sweep");
  for(int c=0;c<3;c++){
   var p=ready(c,25,20);assertEquals(1.15,p.damageMultiplier(),1e-9);assertTrue(p.unlock(IDS[c]));
   assertEquals(1+p.strengthDamageBonus()+.15,p.damageMultiplier(),1e-9);
   var boosted=c==0?swords:c==1?magic:enchanted;
   for(var a:Ability.ALL.values()){
    assertEquals(a.id(),boosted.contains(a.id())?.05:0,p.rankDamageBonus(a),1e-9);
    assertEquals(a.id(),p.damageMultiplier()*(boosted.contains(a.id())?1.05:1)*(boosted.contains(a.id())?(c==2?1.06:1.08):1),p.damageMultiplier(a),1e-9);
   }
   assertEquals(1,p.healingMultiplier(),0);
  }
  var p=ready(0,25,20);for(String id:new String[]{"heavy_cut","heavy_sweep","impact_training",IDS[0]})assertTrue(p.unlock(id));p.setStyle(PlayerProgress.SwordStyle.HEAVY);
  assertEquals(p.damageMultiplier()*1.15*1.05*1.08,p.damageMultiplier(Ability.ALL.get("heavy_cut")),1e-9);
 }
 @Test public void saveReloadSnapshotsAndReconnectNeverStackBonuses() throws Exception {
  Path dir=Files.createTempDirectory("ultimate-rank");
  try{
   for(int c=0;c<3;c++){
    var p=ready(c,25,20);assertTrue(p.unlock(IDS[c]));var before=p.toProperties();var bonus=p.bonuses();
    for(int i=0;i<50;i++){p=PlayerProgress.fromProperties(p.toProperties());StatusData.from(p,true);assertFalse(p.unlock(IDS[c]));assertEquals(before,p.toProperties());assertEquals(bonus,p.bonuses());assertEquals(4,p.rank());}
    var id=UUID.randomUUID();try(var out=Files.newBufferedWriter(dir.resolve(id+".properties"))){before.store(out,null);}
    var store=new PlayerStore(dir);assertEquals(bonus,store.get(id).bonuses());store.forget(id);assertEquals(bonus,store.get(id).bonuses());assertEquals(before,store.get(id).toProperties());
   }
  }finally{try(var files=Files.list(dir)){for(var file:files.toList())Files.delete(file);}Files.delete(dir);}
 }
 @Test public void oldRanksAndSaveSchemaRemainUnchangedAndForgedRanksAreRejected(){
  for(int c=0;c<3;c++){
   var p=ready(c,25,20);var old=p.toProperties();assertEquals(3,p.rank());assertEquals(old,PlayerProgress.fromProperties(old).toProperties());assertEquals("1",old.getProperty("schema"));
   for(int n=0;n<3;n++){var props=oldCopy(old);props.setProperty("learned",String.join(",",Arrays.copyOf(ROUTES[c],n)));var q=PlayerProgress.fromProperties(props);assertEquals(n<2?1:2,q.rank());assertEquals(1,q.damageMultiplier(),1e-9);}
   var forged=ready(c,24,20).toProperties();forged.setProperty("learned",forged.getProperty("learned")+","+IDS[c]);assertEquals(3,PlayerProgress.fromProperties(forged).rank());
  }
 }
 private Properties oldCopy(Properties old){var p=new Properties();p.putAll(old);return p;}
 @Test public void effectiveStatArithmeticDoesNotOverflowAtIntegerLimit(){
  var props=ready(0,25,20).toProperties();props.setProperty("xp","1000000000000000000000000000000");props.setProperty("stat.STRENGTH",Integer.toString(Integer.MAX_VALUE));
  var p=PlayerProgress.fromProperties(props);assertTrue(p.unlock(IDS[0]));assertEquals((long)Integer.MAX_VALUE+3,p.effectiveStat(STRENGTH));assertTrue(Double.isFinite(p.damageMultiplier()));assertEquals(.5,p.bonuses().miningSpeed(),0);
 }
}

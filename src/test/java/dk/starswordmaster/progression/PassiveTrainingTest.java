package dk.starswordmaster.progression;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class PassiveTrainingTest {
 @Test public void fifteenOptionalNodesGiveOnlyDerivedStatsAndCostOnePerkEach(){
  var props=new Properties();props.setProperty("xp","200000");for(var s:Skill.values())props.setProperty("practice."+s,"10000");var p=PlayerProgress.fromProperties(props);
  for(String id:new String[]{"quick_cut","quick_dash","flow_training","heavy_cut","heavy_sweep","impact_training","guard","guard_training","spark","fire_affinity","mend","restorative_affinity"})assertTrue(id,p.unlock(id));
  var available=p.statPoints();int count=0;for(var a:Ability.ALL.values())if(a.statBonus()!=null){
   assertFalse(a.active());assertEquals(0,a.mana(),0);assertEquals(0,a.stamina(),0);assertEquals(0,a.cooldownTicks());
   long before=p.effectiveStat(a.statBonus());var points=p.points();assertTrue(a.id(),p.unlock(a.id()));assertFalse(p.unlock(a.id()));assertFalse(p.assign(0,a.id()));
   assertEquals(before+1,p.effectiveStat(a.statBonus()));assertEquals(points.subtract(java.math.BigInteger.ONE),p.points());count++;
  }
  assertEquals(15,count);assertEquals(85,Ability.ALL.size());assertEquals(54,Ability.ALL.values().stream().filter(Ability::active).count());
  assertEquals(available,p.statPoints());assertEquals(3,p.effectiveStat(PlayerProgress.Stat.STRENGTH));assertEquals(3,p.effectiveStat(PlayerProgress.Stat.AGILITY));assertEquals(3,p.effectiveStat(PlayerProgress.Stat.ENDURANCE));assertEquals(6,p.effectiveStat(PlayerProgress.Stat.SPIRIT));
  for(var s:PlayerProgress.Stat.values())assertEquals(0,p.stat(s));var restored=PlayerProgress.fromProperties(p.toProperties());assertEquals(p.learned(),restored.learned());assertEquals(p.bonuses(),restored.bonuses());var before=restored.toProperties();for(int i=0;i<20;i++)restored=PlayerProgress.fromProperties(restored.toProperties());assertEquals(before,restored.toProperties());
 }
 @Test public void trainingCanBePurchasedWithoutParentsOrPractice(){
  var props=new Properties();props.setProperty("xp","200000");props.setProperty("practice.SWORD","1600");props.setProperty("learned","quick_cut,quick_dash,flow_training");var p=PlayerProgress.fromProperties(props);
  assertTrue(p.unlock("agility_training_2"));assertTrue(p.unlock("agility_training_1"));assertFalse(p.unlock("agility_training_2"));assertTrue(p.unlock("strength_training_1"));
 }
}

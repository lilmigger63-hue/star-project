package dk.starswordmaster.progression;
import org.junit.Test;
import java.math.BigInteger;
import java.util.*;
import static org.junit.Assert.*;
public class UnrestrictedLearningTest {
 @Test public void everyNodeCostsOnePointWithoutClassLevelTrainingOrParents(){
  for(String id:Ability.ALL.keySet()){
   var p=new PlayerProgress();p.awardXp(BigInteger.valueOf(70));assertEquals(2,p.level().intValueExact());var stats=p.statPoints();
   assertTrue(id,p.canUnlock(id));assertTrue(id,p.unlock(id));assertEquals(Set.of(id),p.learned());assertEquals(BigInteger.ZERO,p.points());assertEquals(stats,p.statPoints());assertFalse(p.unlock(id));assertFalse(p.unlock("spark"));
   var loaded=PlayerProgress.fromProperties(p.toProperties());assertEquals(id,p.learned(),loaded.learned());assertEquals(p.points(),loaded.points());assertTrue(loaded.resetLearnedSkills());assertEquals(BigInteger.ONE,loaded.points());assertFalse(loaded.resetLearnedSkills());assertEquals(BigInteger.ONE,loaded.points());
  }
 }
 @Test public void anyChosenClassCanLearnAnyNodeAndUnknownIdsNeverSpend(){
  for(var cls:PlayerProgress.PlayerClass.values())for(String id:Ability.ALL.keySet()){
   var p=new PlayerProgress();p.awardXp(BigInteger.valueOf(400));if(cls!=PlayerProgress.PlayerClass.UNINITIATED)assertTrue(p.chooseClass(cls));var before=p.points();assertTrue(id,p.unlock(id));assertEquals(before.subtract(BigInteger.ONE),p.points());assertFalse(p.unlock(id));assertFalse(p.unlock("invalid"));assertFalse(p.unlock(null));
  }
 }
 @Test public void freshLevelOneStillNeedsEarnedPerks(){var p=new PlayerProgress();for(String id:Ability.ALL.keySet())assertFalse(p.canUnlock(id));assertFalse(p.unlock("missing"));assertEquals(BigInteger.ZERO,p.points());}
 @Test public void ultimatePassiveRankCanBeLearnedDirectlyWithoutMastery(){
  String[] ids={"astral_sword_sovereign","celestial_archon","eclipse_sovereign"};var classes=new PlayerProgress.PlayerClass[]{PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE};
  for(int i=0;i<3;i++){var p=new PlayerProgress();p.awardXp(BigInteger.valueOf(400));assertTrue(p.chooseClass(classes[i]));assertTrue(p.unlock(ids[i]));assertEquals(4,p.rank());assertEquals(4,PlayerProgress.fromProperties(p.toProperties()).rank());}
 }
}

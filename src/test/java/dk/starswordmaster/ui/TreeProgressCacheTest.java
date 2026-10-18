package dk.starswordmaster.ui;
import dk.starswordmaster.progression.*;
import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.*;
public class TreeProgressCacheTest {
 @Test public void equivalentAndResourceOnlySnapshotsRetainNodeState(){
  var p=new PlayerProgress();var cache=new TreeProgressCache();assertTrue(cache.update(p));var state=cache.node("spark");
  assertFalse(cache.update(p));p.tick(false);assertFalse(cache.update(p));assertSame(state,cache.node("spark"));
  assertFalse(cache.update(PlayerProgress.fromProperties(p.toProperties())));assertSame(state,cache.node("spark"));
 }
 @Test public void levelLearningStyleStatsAndTrainingInvalidateOnlyWhenRelevant(){
  var p=new PlayerProgress();var cache=new TreeProgressCache();cache.update(p);assertFalse(cache.node("spark").available());
  p.awardXp(BigInteger.valueOf(70));assertTrue(cache.update(p));assertTrue(cache.node("spark").available());
  assertTrue(p.unlock("spark"));assertTrue(cache.update(p));assertTrue(cache.node("spark").known());assertFalse(cache.node("spark").available());
  p.setStyle(PlayerProgress.SwordStyle.HEAVY);assertTrue(cache.update(p));assertTrue(p.spendStat(PlayerProgress.Stat.AGILITY));assertTrue(cache.update(p));
  p.awardPractice(Skill.FIRE,20);assertFalse(cache.update(p));p.awardPractice(Skill.FIRE,20);assertTrue(cache.update(p));
  assertTrue(p.resetLearnedSkills());assertTrue(cache.update(p));assertFalse(cache.node("spark").known());
 }
}

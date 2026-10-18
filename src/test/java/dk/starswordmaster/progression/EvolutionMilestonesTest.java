package dk.starswordmaster.progression;
import java.math.BigInteger;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;
import static dk.starswordmaster.progression.PlayerProgress.PlayerClass.*;
public class EvolutionMilestonesTest {
 private final UUID id=new UUID(1,2);
 private ClassEvolution sword(int level){return ClassEvolution.from(SWORDSMAN,BigInteger.valueOf(level));}
 @Test public void joinReloadAndReconnectNeverReplayMilestones(){var m=new EvolutionMilestones();m.seed(id,sword(30));m.observe(id,sword(30));assertNull(m.take(id,sword(30)));m.forget(id);m.seed(id,sword(30));assertNull(m.take(id,sword(30)));}
 @Test public void multilevelTransitionsCoalesceToFinalStageExactlyOnce(){var m=new EvolutionMilestones();m.seed(id,sword(9));m.observe(id,sword(10));m.observe(id,sword(20));m.observe(id,sword(30));assertEquals(sword(30),m.take(id,sword(30)));assertNull(m.take(id,sword(30)));m.observe(id,sword(31));assertNull(m.take(id,sword(31)));}
 @Test public void lateChoiceNotifiesButBaseClassDoesNot(){var m=new EvolutionMilestones();var empty=ClassEvolution.from(UNINITIATED,BigInteger.valueOf(30));m.seed(id,empty);m.observe(id,sword(5));assertNull(m.take(id,sword(5)));m.seed(id,empty);m.observe(id,sword(30));assertEquals(sword(30),m.take(id,sword(30)));}
 @Test public void playersAreIndependentAndStalePendingIsDiscarded(){var m=new EvolutionMilestones();var other=new UUID(3,4);m.seed(id,sword(9));m.seed(other,sword(30));m.observe(id,sword(20));assertNull(m.take(other,sword(30)));assertNull(m.take(id,sword(9)));m.observe(id,sword(30));m.clear();assertNull(m.take(id,sword(30)));}
}

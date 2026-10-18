package dk.starswordmaster.presentation;
import org.junit.Test;
import static org.junit.Assert.*;
public class WorldVfxBudgetTest {
 @Test public void malformedCountsCannotOverflowBudget(){var b=new WorldVfxBudget();assertTrue(b.takeTerrain(1));assertFalse(b.takeTerrain(Integer.MAX_VALUE));assertFalse(b.takeCinematic(Integer.MAX_VALUE));assertEquals(1,b.used());}
 @Test public void repeatedCastersCannotExceedEitherPreviousLimit(){
  var b=new WorldVfxBudget();for(int i=0;i<64;i++){b.takeTerrain(96);b.takeCinematic(96);}
  assertTrue(b.used()<=4096);assertFalse(b.takeTerrain(2048));assertFalse(b.takeCinematic(2048));assertFalse(b.takeCinematic(-1));
  b.reset();assertEquals(0,b.used());assertTrue(b.takeTerrain(2048));assertTrue(b.takeCinematic(2048));assertEquals(4096,b.used());
 }
}

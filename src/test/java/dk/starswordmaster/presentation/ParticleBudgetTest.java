package dk.starswordmaster.presentation;
import org.junit.Test;
import static org.junit.Assert.*;
public class ParticleBudgetTest {
 @Test public void rejectsFarAndInvalidPositionsWithoutSpendingBudget(){var b=new ParticleBudget();assertFalse(b.take(Double.NaN));assertFalse(b.take(Double.POSITIVE_INFINITY));assertFalse(b.take(-1));assertFalse(b.take(4096.1));assertEquals(256,b.remaining());assertTrue(b.take(4096));assertEquals(255,b.remaining());}
 @Test public void capsManyCastersAndResetsOnlyForNewTick(){var b=new ParticleBudget();int accepted=0;for(int i=0;i<64*20;i++)if(b.take(0))accepted++;assertEquals(256,accepted);assertEquals(0,b.remaining());assertFalse(b.take(1));b.reset();assertEquals(256,b.remaining());assertTrue(b.take(1));}
}

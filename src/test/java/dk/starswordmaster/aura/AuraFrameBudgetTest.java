package dk.starswordmaster.aura;
import org.junit.Test;
import static org.junit.Assert.*;
public class AuraFrameBudgetTest {
 @Test public void meshBudgetAndCasterLimitHoldAcrossBothHands(){
  var b=new AuraFrameBudget();int count=0;
  for(int i=0;i<100;i++)for(int hand=0;hand<2;hand++)count+=b.take(i,hand,32,0);
  assertTrue(count<=1024);assertTrue(b.casters()<=64);assertEquals(1024-count,b.remaining());
 }
 @Test public void duplicateHandAndDistantInvalidDrawsSpendNothing(){
  var b=new AuraFrameBudget();assertEquals(0,b.take(1,0,32,4096.01));assertEquals(0,b.take(1,0,32,Double.NaN));
  assertEquals(32,b.take(1,0,32,4096));assertEquals(0,b.take(1,0,32,0));assertEquals(32,b.take(1,1,32,0));
  b.reset();assertEquals(1024,b.remaining());assertEquals(32,b.take(1,0,32,0));
 }
 @Test public void allowsExactlySixtyFourCastersAndRejectsInvalidRequests(){
  var b=new AuraFrameBudget();assertEquals(0,b.take(1,0,0,0));assertEquals(0,b.take(1,0,-1,0));
  for(int i=0;i<64;i++)assertEquals(4,b.take(i,0,4,0));
  assertEquals(0,b.take(100,0,4,0));assertEquals(4,b.take(0,1,4,0));
 }
 @Test public void insufficientSpaceRejectsWholeMeshBeforeConstruction(){
  var b=new AuraFrameBudget();
  for(int i=0;i<16;i++)assertEquals(63,b.take(i,0,63,0));
  assertEquals(16,b.remaining());assertEquals(0,b.take(20,0,32,0));assertEquals(16,b.remaining());
 }
}

package dk.starswordmaster.presentation;
import org.junit.Test;
import static org.junit.Assert.*;
public class AbilityVfxTimelineTest {
 @Test public void fiveStagesFollowOriginalClipDurationAndEndSafely(){
  for(var s:AbilityPresentation.ALL.values()){
   int d=s.ticks();assertEquals(AbilityVfxTimeline.Phase.ANTICIPATION,AbilityVfxTimeline.sample(0,d,0).phase());
   assertEquals(AbilityVfxTimeline.Phase.ACTIVATION,AbilityVfxTimeline.sample(d*.22f,d,0).phase());
   assertEquals(AbilityVfxTimeline.Phase.PRIMARY,AbilityVfxTimeline.sample(d*.45f,d,0).phase());
   assertEquals(AbilityVfxTimeline.Phase.IMPACT,AbilityVfxTimeline.sample(d*.7f,d,0).phase());
   assertEquals(AbilityVfxTimeline.Phase.DISSIPATION,AbilityVfxTimeline.sample(d,d,0).phase());
   assertEquals(0,AbilityVfxTimeline.sample(d+8,d,0).alpha(),0);
   assertEquals(0,AbilityVfxTimeline.sample(Float.NaN,d,0).alpha(),0);
   for(float age=0;age<d+9;age+=.25f){var f=AbilityVfxTimeline.sample(age,d,0);assertTrue(Float.isFinite(f.alpha()));assertTrue(f.alpha()>=0&&f.alpha()<=1);assertTrue(f.travel()>=0&&f.travel()<=1);}
  }
 }
 @Test public void delayedPartsNeverOutliveTheSameScene(){
  assertEquals(0,AbilityVfxTimeline.sample(2,30,4).alpha(),0);
  assertTrue(AbilityVfxTimeline.sample(10,30,4).alpha()>0);
  assertEquals(0,AbilityVfxTimeline.sample(38,30,4).alpha(),0);
 }
}

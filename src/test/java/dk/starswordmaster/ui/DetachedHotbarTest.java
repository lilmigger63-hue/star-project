package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class DetachedHotbarTest {
 @Test public void abilityBarStaysAwayFromCenteredHotbarIncludingLeftOffhand(){
  for(int w:new int[]{320,427,562,628,629,630,640,853,1920}){var r=ResourceHudLayout.forScreen(w,480);var s=SkillHudLayout.forResources(r);double bottom=s.y()+SkillHudLayout.SCALED_HEIGHT,right=s.x()+SkillHudLayout.SCALED_WIDTH;assertTrue(right+8<=w/2-91-29||bottom<=480-23-4);}
 }
 @Test public void wideResourcesAndVanillaIndicatorsKeepTheirPositions(){var wide=ResourceHudLayout.forScreen(640,360);assertEquals(294,wide.y());assertEquals(6,wide.x());assertEquals(29,wide.vanillaLift());var narrow=ResourceHudLayout.forScreen(320,240);assertEquals(89,narrow.y());assertEquals(6,narrow.x());assertEquals(29,narrow.vanillaLift());}
}

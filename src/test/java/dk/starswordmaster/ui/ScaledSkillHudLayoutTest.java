package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class ScaledSkillHudLayoutTest {
 @Test public void skillsFollowStaminaAtEverySupportedGuiSize(){
  for(int[] size:new int[][]{{1920,1080},{1280,720},{960,720},{853,481},{640,480}})for(int scale=1;scale<=4;scale++){
   int w=Math.max(320,(size[0]+scale-1)/scale),h=Math.max(240,(size[1]+scale-1)/scale);var r=ResourceHudLayout.forScreen(w,h);var s=SkillHudLayout.forScreen(w,h);
   assertEquals(r.barX(),s.x());assertEquals(r.barY(2)+ResourceHudLayout.BAR_HEIGHT+2,s.y());assertTrue(s.y()+16.5<=h-6);
  }
 }
 @Test public void narrowGroupClearsNativeIndicatorsIncludingExtraRows(){
  for(int w:new int[]{320,427,562,628}){var r=ResourceHudLayout.forScreen(w,240);var s=SkillHudLayout.forScreen(w,240);assertTrue(r.stacked());assertTrue(s.y()+16.5<=240-59-r.vanillaLift()-4);}
 }
 @Test public void wideGroupAlsoClearsLeftOffhandSlot(){
  for(int w:new int[]{630,640,853,1920}){var s=SkillHudLayout.forScreen(w,480);var r=ResourceHudLayout.forScreen(w,480);assertFalse(r.stacked());assertEquals(480-66,r.y());assertTrue(s.x()+136.5+8<=w/2-91-29);assertEquals(29,r.vanillaLift());}
 }
 @Test public void scalesOriginalGeometryWithoutRoundingHalfUnits(){assertEquals(0.75,SkillHudLayout.SCALE,0);assertEquals(136.5,SkillHudLayout.SCALED_WIDTH,0);assertEquals(16.5,SkillHudLayout.SCALED_HEIGHT,0);assertEquals(12,SkillHudLayout.ICON_SIZE*SkillHudLayout.SCALE,0);assertEquals(15,SkillHudLayout.SLOT_PITCH*SkillHudLayout.SCALE,0);}
}

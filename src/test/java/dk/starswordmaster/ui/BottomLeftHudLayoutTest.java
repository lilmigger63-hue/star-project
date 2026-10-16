package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class BottomLeftHudLayoutTest {
 @Test public void widePanelStaysAtLeftScreenEdgeWhenWindowGrows(){
  for(int w:new int[]{630,640,853,1280,1920}){var r=ResourceHudLayout.forScreen(w,480);assertFalse(r.stacked());assertEquals(6,r.x());assertEquals(480-26,r.y()+ResourceHudLayout.HEIGHT);}
 }
 @Test public void narrowPanelStacksAtLeftAboveCompactIndicators(){
  for(int w:new int[]{320,321,427,561,629}){var r=ResourceHudLayout.forScreen(w,240);assertTrue(r.stacked());assertEquals(6,r.x());assertTrue(r.y()+40<=240-49-r.vanillaLift()-4);}
 }
 @Test public void hungerAndExperienceMeetHotbarWithoutMovingOtherIndicators(){
  for(int w:new int[]{320,640,853}){var r=ResourceHudLayout.forScreen(w,240);int xpTop=240-29-SkillHudLayout.EXPERIENCE_LIFT,foodTop=240-39-SkillHudLayout.EXPERIENCE_LIFT;assertEquals(212,xpTop);assertEquals(202,foodTop);assertEquals(29,r.vanillaLift());assertEquals(1,xpTop-(foodTop+9));}
 }
 @Test public void stackedPanelAndTooltipsClearExtraArmorAndAirRows(){
  for(int extra:new int[]{0,10,18,24}){var r=ResourceHudLayout.forScreen(320,240,extra);int nativeTop=240-59-r.vanillaLift()-extra;assertTrue(r.y()>=6);assertTrue(r.y()+40<=nativeTop-4);int tooltipBottom=240-59-r.tooltipLift(240,extra)+9;assertTrue(tooltipBottom<=r.y()-4);}
 }
 @Test public void chatViewportFitsAboveAllHudAtDifferentChatScales(){
  for(int w:new int[]{320,427,562,640,853})for(int h:new int[]{240,360,481})for(int extra:new int[]{0,10,18})for(double scale:new double[]{0.5,0.75,1}){
   var r=ResourceHudLayout.forScreen(w,h,extra);int lift=r.chatLift(h,extra),bottom=h-28-lift;assertTrue(bottom<=r.y()-4);assertTrue(bottom<=h-59-r.vanillaLift()-extra-4);int height=r.chatHeight(h,180,scale,extra);assertTrue(height<=180);assertTrue(h-40-lift-height*scale>=5);
  }
 }
 @Test public void creativeHeldItemNameClearsStackedPanel(){var r=ResourceHudLayout.forScreen(320,240);int creativeBottom=240-45-r.tooltipLift(240,0,false)+9;assertTrue(creativeBottom<=r.y()-4);}
}

package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class ResourceHudLayoutTest {
 @Test public void wideGuiPlacesPanelLeftOfUnchangedSkillBar(){var skills=SkillHudLayout.forScreen(640,360);var l=ResourceHudLayout.forScreen(640,360);assertFalse(l.stacked());assertEquals(skills.x()-8,l.x()+ResourceHudLayout.WIDTH);assertEquals(334,l.y()+ResourceHudLayout.HEIGHT);assertEquals(44,l.vanillaLift());}
 @Test public void narrowGuiStacksPanelAboveSkillBar(){var skills=SkillHudLayout.forScreen(320,240);var l=ResourceHudLayout.forScreen(320,240);assertTrue(l.stacked());assertEquals(320/2,l.x()+ResourceHudLayout.WIDTH/2);assertEquals(188,l.y()+ResourceHudLayout.HEIGHT);assertEquals(76,l.vanillaLift());}
 @Test public void layoutsFitCurrentGuiAndClearHotbarAcrossResizeAndScale(){
  for(int[] size:new int[][]{{1920,1080},{1280,800},{960,720},{853,481},{640,480}})for(int scale=1;scale<=4;scale++){
   // Minecraft limits effective GUI scales to at least 320 x 240.
   int w=Math.max(320,(size[0]+scale-1)/scale),h=Math.max(240,(size[1]+scale-1)/scale);var l=ResourceHudLayout.forScreen(w,h);var skills=SkillHudLayout.forScreen(w,h);
   assertTrue(l.x()>=6);assertTrue(l.x()+ResourceHudLayout.WIDTH<=w-6);assertTrue(l.y()>0);assertTrue(l.y()+ResourceHudLayout.HEIGHT<h-22);
   assertTrue(l.stacked()?l.y()+ResourceHudLayout.HEIGHT<=skills.y()-4:l.x()+ResourceHudLayout.WIDTH<=skills.x()-8);
   // The lowest locator marker reaches height - 18. It must clear a stacked panel.
   if(l.stacked())assertTrue(h-18-l.vanillaLift()<=l.y()-2);
   assertTrue(h-24-l.vanillaLift()<skills.y());
  }
 }
 @Test public void threeBarsStayBesideRoundMarkerAndDoNotOverlap(){var l=ResourceHudLayout.forScreen(640,360);assertEquals(40,ResourceHudLayout.PORTRAIT_SIZE);assertEquals(l.x()+44,l.barX());assertEquals(132,ResourceHudLayout.BAR_WIDTH);for(int i=0;i<3;i++){assertTrue(l.barY(i)>=l.y());assertTrue(l.barY(i)+ResourceHudLayout.BAR_HEIGHT<=l.y()+ResourceHudLayout.HEIGHT);if(i>0)assertTrue(l.barY(i)>=l.barY(i-1)+ResourceHudLayout.BAR_HEIGHT+2);}assertEquals(l.x()+ResourceHudLayout.WIDTH,l.barX()+ResourceHudLayout.BAR_WIDTH);}
 @Test public void changingWidthReflowsWithoutChangingSkillBarGeometry(){var wide=ResourceHudLayout.forScreen(640,360);var small=ResourceHudLayout.forScreen(320,240);assertFalse(wide.stacked());assertTrue(small.stacked());assertEquals(182,SkillHudLayout.WIDTH);assertEquals(22,SkillHudLayout.HEIGHT);assertEquals(5,SkillHudLayout.SLOTS);}
 @Test public void heldItemNameClearsArmorWithExtraHealthAndAbsorption(){
  assertEquals(0,ResourceHudLayout.tooltipExtraLift(20,0));assertEquals(10,ResourceHudLayout.tooltipExtraLift(21,0));assertEquals(10,ResourceHudLayout.tooltipExtraLift(30,4));assertEquals(18,ResourceHudLayout.tooltipExtraLift(40,4));
  for(int w:new int[]{320,640}){var l=ResourceHudLayout.forScreen(w,240);int armorTop=240-59-l.vanillaLift();int labelBottom=240-59-l.vanillaLift()-ResourceHudLayout.tooltipExtraLift(30,4)+9;assertTrue(labelBottom<armorTop);}
 }
 @Test public void itemNameRespectsVanillasRetainedHealthAfterBuffExpires(){
  assertEquals(18,ResourceHudLayout.tooltipExtraLift(21,21,41,0));assertEquals(18,ResourceHudLayout.tooltipExtraLift(30,30,50,0));assertEquals(10,ResourceHudLayout.tooltipExtraLift(20,24,20,0));assertEquals(0,ResourceHudLayout.tooltipExtraLift(20,20,20,0));
 }
}

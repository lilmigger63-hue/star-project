package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class SkillHudLayoutTest {
 @Test public void followsResourcesAcrossResolutionsAndGuiScales(){
  for(int[] size:new int[][]{{1920,1080},{1280,800},{853,481},{640,480}})for(int scale=1;scale<=4;scale++){
   int w=Math.max(320,(size[0]+scale-1)/scale),h=Math.max(240,(size[1]+scale-1)/scale);var r=ResourceHudLayout.forScreen(w,h);var l=SkillHudLayout.forScreen(w,h);
   assertEquals(r.barX(),l.x());assertEquals(r.y()+42,l.y());assertEquals(136.5,SkillHudLayout.SCALED_WIDTH,0);assertEquals(16.5,SkillHudLayout.SCALED_HEIGHT,0);
  }
 }
 @Test public void fiveDesignSlotsKeepTheirOrderAndSymmetricEmptySides(){
  var l=new SkillHudLayout(0,0);assertEquals(5,SkillHudLayout.SLOTS);assertEquals(20,SkillHudLayout.SLOT_PITCH);assertEquals(16,SkillHudLayout.ICON_SIZE);
  assertEquals(41,l.slotX(0));assertEquals(121,l.slotX(4));assertEquals(l.slotX(0),SkillHudLayout.WIDTH-l.slotX(4)-SkillHudLayout.SLOT_PITCH);
  for(int i=0;i<5;i++){assertEquals(l.slotX(i)+2,l.iconX(i));assertEquals(3,l.iconY());if(i>0)assertEquals(15,(l.slotX(i)-l.slotX(i-1))*SkillHudLayout.SCALE,0);}
 }
 @Test public void resizeKeepsLeftAnchorAndReflowsWithResources(){
  var a=SkillHudLayout.forScreen(640,360);var b=SkillHudLayout.forScreen(427,240);assertEquals(a.x(),b.x());assertEquals(-205,b.y()-a.y());
 }
 @Test public void stackedSkillsClearNativeAirAndArmor(){
  var r=ResourceHudLayout.forScreen(320,240,18);var l=SkillHudLayout.forResources(r);assertEquals(r.barY(2)+12+2,l.y());assertTrue(l.y()+SkillHudLayout.SCALED_HEIGHT<=240-59-r.vanillaLift()-18-4);
 }
 @Test public void resourceAwareLayoutIncludesExtraNativeRows(){
  var ordinary=SkillHudLayout.forResources(ResourceHudLayout.forScreen(320,240));var extra=SkillHudLayout.forResources(ResourceHudLayout.forScreen(320,240,18));assertEquals(ordinary.y()-18,extra.y());assertEquals(ordinary.x(),extra.x());
 }
}

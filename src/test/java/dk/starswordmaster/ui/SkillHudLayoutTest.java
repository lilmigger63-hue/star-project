package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class SkillHudLayoutTest {
 @Test public void matchesVanillaBoundsAcrossResolutionsAndGuiScales(){
  for(int[] size:new int[][]{{1920,1080},{1280,800},{853,481},{640,480}})for(int scale=1;scale<=4;scale++){
   int w=(size[0]+scale-1)/scale,h=(size[1]+scale-1)/scale;
   var l=SkillHudLayout.forScreen(w,h);
   assertEquals(w/2-91,l.x());assertEquals(182,SkillHudLayout.WIDTH);assertEquals(22,SkillHudLayout.HEIGHT);
   assertEquals(h-22-SkillHudLayout.GAP,l.y()+SkillHudLayout.HEIGHT);
   assertTrue(l.y()+SkillHudLayout.HEIGHT<h-23); // Even the vanilla selection's top border stays clear.
  }
 }
 @Test public void fiveVanillaSizedSlotsAreSymmetricWithEmptySides(){
  var l=SkillHudLayout.forScreen(640,360);
  assertEquals(5,SkillHudLayout.SLOTS);assertEquals(20,SkillHudLayout.SLOT_PITCH);assertEquals(16,SkillHudLayout.ICON_SIZE);
  assertEquals(l.x()+41,l.slotX(0));assertEquals(l.x()+121,l.slotX(4));
  assertEquals(l.slotX(0)-l.x(),l.x()+SkillHudLayout.WIDTH-l.slotX(4)-SkillHudLayout.SLOT_PITCH);
  for(int i=0;i<5;i++){assertEquals(l.slotX(i)+2,l.iconX(i));assertEquals(l.y()+3,l.iconY());if(i>0)assertEquals(20,l.slotX(i)-l.slotX(i-1));}
 }
 @Test public void resizeUsesCurrentScaledWindow(){
  var a=SkillHudLayout.forScreen(640,360);var b=SkillHudLayout.forScreen(427,240);
  assertEquals(427/2-640/2,b.x()-a.x());assertEquals(-120,b.y()-a.y());
 }
 @Test public void resourcesAndLiftedVanillaIndicatorsDoNotOverlapEitherBar(){
  var l=SkillHudLayout.forScreen(320,240);
  assertTrue(l.manaY()+4<=l.staminaY());assertTrue(l.staminaY()+4<l.y());
  // Vanilla XP occupies y=height-29 .. height-24; lifting keeps it above resources.
  assertTrue(240-24-SkillHudLayout.VANILLA_LIFT<l.manaY());
  assertTrue(l.manaY()<l.y());assertTrue(l.y()+SkillHudLayout.HEIGHT<240-22);
 }
 @Test public void locatorDownArrowClearsManaStrip(){var l=SkillHudLayout.forScreen(320,240);assertTrue(240-18-SkillHudLayout.VANILLA_LIFT<=l.manaY()-2);}
}

package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class HotbarExperienceLayoutTest {
 @Test public void xpBottomMatchesActualOuterHotbarEdgeAcrossScales(){
  for(int[] size:new int[][]{{1920,1080},{1280,720},{960,720},{853,481},{640,480}})for(int scale=1;scale<=4;scale++){
   int h=Math.max(240,(size[1]+scale-1)/scale);int xp=h-29-SkillHudLayout.infoBarLift(true);assertEquals(h-23,xp+5);int food=h-39-SkillHudLayout.EXPERIENCE_LIFT;assertTrue(food+9+1<=xp);int level=h-35-SkillHudLayout.infoBarLift(true);assertEquals(6,xp-level);assertTrue(level+10<=h-23);
  }
 }
 @Test public void locatorAndOtherNativeIndicatorsKeepTheirPreviousLift(){assertEquals(29,SkillHudLayout.infoBarLift(false));assertEquals(29,SkillHudLayout.VANILLA_LIFT);for(int w:new int[]{320,630,640}){var r=ResourceHudLayout.forScreen(w,240);assertEquals(w<630?89:174,r.y());assertEquals(6,r.x());var skills=SkillHudLayout.forResources(r);assertEquals(r.barY(2)+14,skills.y());}}
}

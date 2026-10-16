package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class TouchingHotbarTest {
 @Test public void exactZeroGapAtEveryGuiSize(){for(int[] screen:new int[][]{{1920,1080},{1280,720},{853,481},{640,480}})for(int scale=1;scale<=4;scale++){int w=Math.max(320,(screen[0]+scale-1)/scale),h=Math.max(240,(screen[1]+scale-1)/scale);var bar=SkillHudLayout.forScreen(w,h);assertEquals(w/2-91,bar.x());assertEquals(h-23,bar.y()+22);assertEquals(0,SkillHudLayout.GAP);}}
 @Test public void resourcePanelDocksLeftWhileVanillaGroupClearsSkills(){var wide=ResourceHudLayout.forScreen(640,360);assertEquals(294,wide.y());assertEquals(6,wide.x());assertEquals(29,wide.vanillaLift());var narrow=ResourceHudLayout.forScreen(320,240);assertEquals(108,narrow.y());assertEquals(6,narrow.x());assertEquals(29,narrow.vanillaLift());}
}

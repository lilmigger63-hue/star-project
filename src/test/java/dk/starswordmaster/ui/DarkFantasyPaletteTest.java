package dk.starswordmaster.ui;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class DarkFantasyPaletteTest {
 private static double channel(int color,int shift){double v=(color>>shift&255)/255.0;return v<=0.04045?v/12.92:Math.pow((v+0.055)/1.055,2.4);}
 private static double luminance(int color){return 0.2126*channel(color,16)+0.7152*channel(color,8)+0.0722*channel(color,0);}
 private static double contrast(int a,int b){double x=luminance(a),y=luminance(b);return (Math.max(x,y)+0.05)/(Math.min(x,y)+0.05);}
 @Test public void normalAndMutedTextRemainReadableOnEveryPanel(){
  for(int ink:new int[]{DarkFantasyPalette.TEXT,DarkFantasyPalette.MUTED,DarkFantasyPalette.GOLD})for(int panel:new int[]{DarkFantasyPalette.CHARCOAL,DarkFantasyPalette.SLATE,0xFF303338})assertTrue("Text contrast "+contrast(ink,panel),contrast(ink,panel)>=4.5);
 }
 @Test public void everySkillStateHasADistinctVisibleFrame(){
  Set<Integer> colors=new HashSet<>();
  for(int ink:new int[]{DarkFantasyPalette.LEARNED,DarkFantasyPalette.AVAILABLE,DarkFantasyPalette.CLASS_LOCKED,DarkFantasyPalette.LOCKED}){
   assertTrue(colors.add(ink));assertEquals(255,ink>>>24);assertTrue("Skill frame contrast "+contrast(ink,DarkFantasyPalette.SLATE),contrast(ink,DarkFantasyPalette.SLATE)>=3);
  }
 }
}

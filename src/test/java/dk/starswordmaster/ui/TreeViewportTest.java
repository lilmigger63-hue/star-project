package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class TreeViewportTest {
 @Test public void zoomKeepsThePointUnderTheCursor(){var v=new TreeViewport();double x=v.worldX(90,160),y=v.worldY(80,120);assertEquals(-200,x,1e-9);v.zoomAt(90,80,160,120,3);assertEquals(x,v.worldX(90,160),1e-9);assertEquals(y,v.worldY(80,120),1e-9);}
 @Test public void draggingMovesNodesByMouseDelta(){var v=new TreeViewport();double before=v.screenX(50,160);v.pan(31,-20);assertEquals(before+31,v.screenX(50,160),1e-9);}
 @Test public void zoomRemainsUsableAtExtremeScroll(){var v=new TreeViewport();v.zoomAt(0,0,100,100,10000);assertEquals(1.8,v.zoom(),0);v.zoomAt(0,0,100,100,-10000);assertEquals(0.12,v.zoom(),0);}
 @Test public void projectionAndHitCoordinatesAgree(){var v=new TreeViewport();v.pan(37,10);v.zoomAt(23,45,200,100,4);double x=v.screenX(-200,200),y=v.screenY(350,100);assertEquals(-200,v.worldX(x,200),1e-9);assertEquals(350,v.worldY(y,100),1e-9);}
 @Test public void expandedBranchesFitInitialViewport(){var v=new TreeViewport();v.fit(500,700);assertTrue(v.screenX(-510,250)>=12);assertTrue(v.screenX(410,250)<=488);assertTrue(v.screenY(-410,350)>=12);assertTrue(v.screenY(450,350)<=688);}
 @Test public void focusCentersAWorldPointAtTheCurrentZoom(){
  var v=new TreeViewport();v.pan(50,80);double zoom=v.zoom();v.focus(325,-610);
  assertEquals(zoom,v.zoom(),0);assertEquals(240,v.screenX(325,240),1e-9);assertEquals(180,v.screenY(-610,180),1e-9);
 }
 @Test public void focusFitsEachSectorAndKeepsPointerZoomAnchored(){
  for(var sector:SkillTreeLayout.Sector.values()){
   var v=new TreeViewport();v.focus(sector,320,180);
   for(var node:SkillTreeLayout.NODES)if(SkillTreeLayout.sector(node)==sector){
    double x=v.screenX(node.x(),160),y=v.screenY(node.y(),90);
    assertTrue(node.id(),x>=12&&x<=308&&y>=12&&y<=168);
   }
   double x=v.worldX(70,160),y=v.worldY(60,90);v.zoomAt(70,60,160,90,2);
   assertEquals(x,v.worldX(70,160),1e-9);assertEquals(y,v.worldY(60,90),1e-9);
  }
 }
 @Test public void tinyFitCanZoomSmoothlyWithoutJumpingToTheOldMinimum(){
  var v=new TreeViewport();v.fit(120,80);double fitted=v.zoom();assertTrue(fitted<0.12);
  v.zoomAt(20,25,60,40,1);assertEquals(fitted*1.15,v.zoom(),1e-12);
  v.zoomAt(20,25,60,40,-10000);assertEquals(fitted,v.zoom(),1e-12);
 }
}
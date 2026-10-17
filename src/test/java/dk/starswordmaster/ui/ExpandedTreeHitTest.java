package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class ExpandedTreeHitTest {
 @Test public void everyNodeSelectsItselfAtEverySupportedZoom(){
  var view=new TreeViewport();view.fit(482,260);view.pan(13.25,-21.75);
  for(double delta:new double[]{0,8,9,50,-100}){
   view.zoomAt(241,180,241,180,delta);
   for(var node:SkillTreeLayout.NODES){
    int x=(int)view.screenX(node.x(),241),y=(int)view.screenY(node.y(),180);
    assertEquals(node.id()+" at "+view.zoom(),node,SkillTreeLayout.hitNode(view,x,y,241,180));
   }
  }
 }
 @Test public void emptySpaceDoesNotSelectAnUnrelatedNode(){
  var view=new TreeViewport();view.fit(482,260);
  assertNull(SkillTreeLayout.hitNode(view,-5000,-5000,241,180));
 }
}

package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class TreeRenderProjectionTest {
 @Test public void clipsCrossingLineWithBothEndpointsOutside(){
  var line=TreeRenderProjection.clip(-20,30,120,30,0,10,100,70);
  assertNotNull(line);assertEquals(0,line.x1(),1e-9);assertEquals(100,line.x2(),1e-9);assertEquals(30,line.y1(),1e-9);
 }
 @Test public void clipsDiagonalAndRejectsOffscreenOrNonfiniteSegments(){
  var line=TreeRenderProjection.clip(-10,-10,110,110,0,0,100,50);
  assertNotNull(line);assertEquals(0,line.x1(),1e-9);assertEquals(50,line.x2(),1e-9);assertEquals(50,line.y2(),1e-9);
  assertNull(TreeRenderProjection.clip(-10,-5,110,-5,0,0,100,50));
  assertNull(TreeRenderProjection.clip(Double.NaN,0,1,1,0,0,100,50));
  assertNotNull(TreeRenderProjection.clip(20,20,20,20,0,0,100,50));
 }
 @Test public void projectionIsReusedUntilCameraOrScreenChanges(){
  var projection=new TreeRenderProjection();var view=new TreeViewport();var screen=TreeScreenLayout.of(960,540);view.fit(screen.treeWidth(),screen.bodyBottom()-screen.bodyTop());
  assertTrue(projection.update(view,screen));var nodes=projection.nodes();var lines=projection.lines();
  assertFalse(projection.update(view,screen));assertSame(nodes,projection.nodes());assertSame(lines,projection.lines());
  view.pan(13,9);assertTrue(projection.update(view,screen));assertFalse(projection.update(view,screen));
  assertTrue(projection.update(view,TreeScreenLayout.of(1280,720)));
 }
 @Test public void fitIncludesEveryInteractiveNodeAndEveryRealConnectionAtOverviewDetail(){
  var projection=new TreeRenderProjection();var view=new TreeViewport();var screen=TreeScreenLayout.of(640,360);view.fit(screen.treeWidth(),screen.bodyBottom()-screen.bodyTop());projection.update(view,screen);
  assertEquals(SkillTreeLayout.NODES.size(),projection.nodes().size());assertFalse(projection.fullDetail());
  for(var edge:SkillTreeLayout.EDGES)assertTrue(edge.child().id(),projection.lines().stream().anyMatch(l->edge.child().id().equals(l.childId())));
  for(var node:projection.nodes()){
   assertEquals((int)view.screenX(node.node().x(),screen.centerX())-projection.nodeSize()/2,node.x());
   assertEquals(node.node().id(),SkillTreeLayout.hitNode(view,node.x()+projection.nodeSize()/2,node.y()+projection.nodeSize()/2,screen.centerX(),screen.centerY()).id());
  }
 }
 @Test public void panningFarOutsideCullsNodesLinesAndHub(){
  var projection=new TreeRenderProjection();var view=new TreeViewport();var screen=TreeScreenLayout.of(960,540);view.pan(100000,100000);projection.update(view,screen);
  assertTrue(projection.nodes().isEmpty());assertTrue(projection.lines().isEmpty());assertFalse(projection.hubVisible());
 }
}

package dk.starswordmaster.ui;

import dk.starswordmaster.progression.Ability;
import dk.starswordmaster.progression.Skill;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class RadialSkillTreeLayoutTest {
 @Test public void everySkillOccupiesItsThemedRay(){
  Map<Skill,Double> angles=Map.of(Skill.SWORD,225.0,Skill.FIRE,270.0,Skill.FROST,315.0,Skill.LIGHTNING,0.0,Skill.WATER,45.0,Skill.EARTH,90.0,Skill.DARK,135.0,Skill.SUPPORT,180.0);
  for(var node:SkillTreeLayout.NODES){
   double angle=Math.toDegrees(Math.atan2(node.y(),node.x()));
   double deviation=Math.abs(Math.IEEEremainder(angle-angles.get(Ability.ALL.get(node.id()).skill()),360));
   assertTrue(node.id()+" deviates "+deviation+" degrees",deviation<=20);
  }
 }
 @Test public void realPrerequisitesAlwaysLieCloserToTheHub(){
  Map<String,SkillTreeLayout.Node> nodes=new HashMap<>();
  for(var node:SkillTreeLayout.NODES)nodes.put(node.id(),node);
  for(var ability:Ability.ALL.values())if(!ability.parent().isEmpty()){
   var parent=nodes.get(ability.parent());var child=nodes.get(ability.id());
   assertTrue(ability.id(),Math.hypot(child.x(),child.y())>Math.hypot(parent.x(),parent.y())+40);
  }
 }
 @Test public void everyNodeFitsSmallAndWideUsableAreas(){
  for(int[] size:new int[][]{{180,110},{320,160},{900,220}}){
   var view=new TreeViewport();view.fit(size[0],size[1]);
   for(var node:SkillTreeLayout.NODES){
    double x=view.screenX(node.x(),size[0]/2.0),y=view.screenY(node.y(),size[1]/2.0);
    assertTrue(node.id()+" in "+Arrays.toString(size),x>=12&&x<=size[0]-12&&y>=12&&y<=size[1]-12);
   }
  }
 }
 @Test public void cachedEdgesExactlyMatchRealPrerequisitesIncludingCrossSectorLinks(){
  Set<String> connected=new HashSet<>();
  for(var edge:SkillTreeLayout.EDGES){
   assertEquals(Ability.ALL.get(edge.child().id()).parent(),edge.parent().id());
   assertTrue(connected.add(edge.child().id()));
   assertEquals(SkillTreeLayout.sector(edge.parent())!=SkillTreeLayout.sector(edge.child()),edge.crossSector());
   assertEquals(new SkillTreeLayout.Point(edge.parent().x(),edge.parent().y()),edge.points().getFirst());
   assertEquals(new SkillTreeLayout.Point(edge.child().x(),edge.child().y()),edge.points().getLast());
  }
  assertEquals(Ability.ALL.values().stream().filter(a->!a.parent().isEmpty()).count(),connected.size());
  for(String id:List.of("static_spark","water_bolt","stone_spike","shadow_bolt"))
   assertTrue(SkillTreeLayout.EDGES.stream().anyMatch(e->e.child().id().equals(id)&&e.crossSector()));
 }
 @Test public void allEightDecorativeSpinesHaveTwigsAndStaySeparateFromAbilities(){
  assertEquals(8,SkillTreeLayout.Sector.values().length);
  for(var sector:SkillTreeLayout.Sector.values()){
   assertEquals(1,SkillTreeLayout.BRANCHES.stream().filter(b->b.sector()==sector&&b.spine()).count());
   assertTrue(SkillTreeLayout.BRANCHES.stream().filter(b->b.sector()==sector&&!b.spine()).count()>=4);
   assertTrue(SkillTreeLayout.NODES.stream().anyMatch(n->SkillTreeLayout.sector(n)==sector));
  }
  Set<String> ids=new HashSet<>();for(var node:SkillTreeLayout.NODES)assertTrue(ids.add(node.id()));
  assertEquals(Ability.ALL.keySet(),ids);
 }
 @Test public void globalBoundsIncludeHubNodePaddingAndEveryDecorativePoint(){
  var b=SkillTreeLayout.BOUNDS;
  assertTrue(b.left()<=-90&&b.top()<=-90&&b.right()>=90&&b.bottom()>=90);
  for(var n:SkillTreeLayout.NODES){
   assertTrue(n.x()-32>=b.left()&&n.x()+32<=b.right());
   assertTrue(n.y()-32>=b.top()&&n.y()+32<=b.bottom());
  }
  for(var branch:SkillTreeLayout.BRANCHES)for(var p:branch.points())
   assertTrue(p.x()>=b.left()&&p.x()<=b.right()&&p.y()>=b.top()&&p.y()<=b.bottom());
  for(var edge:SkillTreeLayout.EDGES)for(var p:edge.points())
   assertTrue(Double.isFinite(p.x())&&Double.isFinite(p.y())&&p.x()>=b.left()&&p.x()<=b.right()&&p.y()>=b.top()&&p.y()<=b.bottom());
 }
 @Test public void cachedDepthTraversesActualParents(){
  for(var a:Ability.ALL.values())assertEquals(a.id(),a.parent().isEmpty()?0:SkillTreeLayout.depth(a.parent())+1,SkillTreeLayout.depth(a.id()));
 }
}
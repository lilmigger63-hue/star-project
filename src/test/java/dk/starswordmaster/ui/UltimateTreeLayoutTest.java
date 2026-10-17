package dk.starswordmaster.ui;
import dk.starswordmaster.progression.Ability;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class UltimateTreeLayoutTest {
 @Test public void rendersAll85RegisteredNodesExactlyOnceWithRoomAfterMastery(){
  var nodes=SkillTreeLayout.NODES;Set<String> ids=new HashSet<>();for(var n:nodes)assertTrue(ids.add(n.id()));assertEquals(85,nodes.size());assertEquals(Ability.ALL.keySet(),ids);
  for(var n:nodes)if(Ability.ALL.get(n.id()).characterRequirement()==25){var parent=nodes.stream().filter(p->p.id().equals(Ability.ALL.get(n.id()).parent())).findFirst().orElseThrow();assertTrue(Math.hypot(n.x()-parent.x(),n.y()-parent.y())>=200);}
  for(int i=0;i<nodes.size();i++)for(int j=0;j<i;j++)assertTrue(nodes.get(i).id()+" / "+nodes.get(j).id(),Math.hypot(nodes.get(i).x()-nodes.get(j).x(),nodes.get(i).y()-nodes.get(j).y())>=60);
 }
 @Test public void expandedTreeFitsInitialUsableViewport(){
  for(int[] size:new int[][]{{500,700},{800,400},{700,800}}){var v=new TreeViewport();v.fit(size[0],size[1]);for(var n:SkillTreeLayout.NODES){double x=v.screenX(n.x(),size[0]/2.0),y=v.screenY(n.y(),size[1]/2.0);assertTrue(n.id(),x>=12&&x<=size[0]-12&&y>=12&&y<=size[1]-12);}}
 }
}

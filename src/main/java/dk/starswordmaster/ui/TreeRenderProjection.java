package dk.starswordmaster.ui;

import java.util.ArrayList;
import java.util.List;
import dk.starswordmaster.ui.SkillTreeLayout.*;

/** Camera-dependent geometry, rebuilt only when the projection or viewport changes. */
public final class TreeRenderProjection {
 public record Segment(double x1,double y1,double x2,double y2) {}
 public record Line(Segment segment,String childId,int color) {}
 public record ProjectedNode(Node node,int x,int y) {}
 public record Label(Sector sector,int x,int y) {}
 private record WorldLine(Point from,Point to,String childId,int color,boolean twig) {}
 private static final List<WorldLine> WORLD_LINES=worldLines();
 private List<Line> lines=List.of();
 private List<ProjectedNode> nodes=List.of();
 private List<Label> labels=List.of();
 private TreeScreenLayout screen;
 private double zoom=Double.NaN,originX,originY;
 private int nodeSize,hubX,hubY,hubRadius;
 private boolean fullDetail,hubVisible;
 private static List<WorldLine> worldLines(){
  var result=new ArrayList<WorldLine>();
  for(var branch:SkillTreeLayout.BRANCHES)add(result,branch.points(),null,branch.spine()?0xFF49413A:0xFF363532,!branch.spine());
  for(var edge:SkillTreeLayout.EDGES)add(result,edge.points(),edge.child().id(),edge.crossSector()?0xFF685579:0xFF55524B,false);
  return List.copyOf(result);
 }
 private static void add(List<WorldLine> result,List<Point> path,String childId,int color,boolean twig){
  for(int i=1;i<path.size();i++)result.add(new WorldLine(path.get(i-1),path.get(i),childId,color,twig));
 }
 public boolean update(TreeViewport view,TreeScreenLayout layout){
  double nextX=view.screenX(0,layout.centerX()),nextY=view.screenY(0,layout.centerY());
  if(layout.equals(screen)&&zoom==view.zoom()&&originX==nextX&&originY==nextY)return false;
  screen=layout;zoom=view.zoom();originX=nextX;originY=nextY;fullDetail=zoom>=0.19;nodeSize=SkillTreeLayout.nodeSize(zoom);
  var projectedLines=new ArrayList<Line>();
  for(var line:WORLD_LINES){
   if(line.twig()&&!fullDetail)continue;
   var segment=clip((int)(originX+line.from().x()*zoom),(int)(originY+line.from().y()*zoom),
    (int)(originX+line.to().x()*zoom),(int)(originY+line.to().y()*zoom),-0.5,layout.bodyTop()-0.5,layout.treeWidth()+0.5,layout.bodyBottom()+0.5);
   if(segment!=null)projectedLines.add(new Line(segment,line.childId(),line.color()));
  }
  lines=List.copyOf(projectedLines);
  var projectedNodes=new ArrayList<ProjectedNode>();int margin=Math.max(3,SkillTreeLayout.frameWidth(zoom));
  for(var node:SkillTreeLayout.NODES){
   int x=(int)(originX+node.x()*zoom)-nodeSize/2,y=(int)(originY+node.y()*zoom)-nodeSize/2;
   if(intersects(x-margin,y-margin,x+nodeSize+margin,y+nodeSize+margin))projectedNodes.add(new ProjectedNode(node,x,y));
  }
  nodes=List.copyOf(projectedNodes);
  hubX=(int)originX;hubY=(int)originY;hubRadius=Math.max(8,(int)(42*zoom));
  hubVisible=intersects(hubX-hubRadius,hubY-hubRadius,hubX+hubRadius+1,hubY+hubRadius+1);
  var projectedLabels=new ArrayList<Label>();
  if(zoom>=0.28)for(var sector:Sector.values()){
   double angle=Math.toRadians(sector.angle());
   projectedLabels.add(new Label(sector,(int)(originX+Math.cos(angle)*130*SkillTreeLayout.SPACING*zoom),(int)(originY+Math.sin(angle)*130*SkillTreeLayout.SPACING*zoom)-4));
  }
  labels=List.copyOf(projectedLabels);return true;
 }
 public boolean intersects(double left,double top,double right,double bottom){return right>0&&left<screen.treeWidth()&&bottom>screen.bodyTop()&&top<screen.bodyBottom();}
 public List<Line> lines(){return lines;}
 public List<ProjectedNode> nodes(){return nodes;}
 public List<Label> labels(){return labels;}
 public boolean fullDetail(){return fullDetail;}
 public int nodeSize(){return nodeSize;}
 public int hubX(){return hubX;}
 public int hubY(){return hubY;}
 public int hubRadius(){return hubRadius;}
 public boolean hubVisible(){return hubVisible;}
 /** Liang–Barsky clipping retains crossing lines even when both endpoints are offscreen. */
 public static Segment clip(double x1,double y1,double x2,double y2,double left,double top,double right,double bottom){
  if(!Double.isFinite(x1)||!Double.isFinite(y1)||!Double.isFinite(x2)||!Double.isFinite(y2))return null;
  double dx=x2-x1,dy=y2-y1,start=0,end=1;
  // Slab intersection avoids temporary arrays and handles horizontal, vertical and point segments.
  if(dx==0){if(x1<left||x1>right)return null;}else{
   double a=(left-x1)/dx,b=(right-x1)/dx;start=Math.max(start,Math.min(a,b));end=Math.min(end,Math.max(a,b));
  }
  if(dy==0){if(y1<top||y1>bottom)return null;}else{
   double a=(top-y1)/dy,b=(bottom-y1)/dy;start=Math.max(start,Math.min(a,b));end=Math.min(end,Math.max(a,b));
  }
  return start>end?null:new Segment(x1+start*dx,y1+start*dy,x1+end*dx,y1+end*dy);
 }
}

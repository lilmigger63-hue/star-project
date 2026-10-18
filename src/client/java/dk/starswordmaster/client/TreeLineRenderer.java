package dk.starswordmaster.client;

import dk.starswordmaster.ui.TreeRenderProjection;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2f;
import java.util.ArrayList;
import java.util.List;

/** Native GUI quads share Minecraft's GUI pipeline, texture setup, layer ordering and scissor. */
final class TreeLineRenderer {
 private record Quad(Matrix3x2f pose,String childId,int color) {}
 private List<Quad> quads=List.of();
 void project(List<TreeRenderProjection.Line> lines){
  var projected=new ArrayList<Quad>(lines.size());
  for(var line:lines){
   var s=line.segment();double dx=s.x2()-s.x1(),dy=s.y2()-s.y1(),length=Math.hypot(dx,dy);
   float nx=length==0?0:(float)(-dy/length),ny=length==0?1:(float)(dx/length);
   // A unit rectangle becomes a one-pixel-wide segment. Cache its transform, including end caps.
   float ux=length==0?1:(float)(dx/length),uy=length==0?0:(float)(dy/length);
   var pose=new Matrix3x2f((float)dx+ux,(float)dy+uy,nx,ny,(float)s.x1()-nx/2,(float)s.y1()-ny/2);
   projected.add(new Quad(pose,line.childId(),line.color()));
  }
  quads=List.copyOf(projected);
 }
 void draw(DrawContext context,java.util.Map<String,Integer> edgeColors){
  var matrices=context.getMatrices();
  for(var quad:quads){
   matrices.pushMatrix();
   try{matrices.mul(quad.pose());context.fill(0,0,1,1,quad.childId()==null?quad.color():edgeColors.get(quad.childId()));}
   finally{matrices.popMatrix();}
  }
 }
}

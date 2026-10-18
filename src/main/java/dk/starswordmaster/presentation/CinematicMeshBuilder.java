package dk.starswordmaster.presentation;

import java.util.*;
import static dk.starswordmaster.presentation.CinematicGeometry.*;

/** Closed, original low-poly solids. Construction happens only while the catalogue initializes. */
final class CinematicMeshBuilder {
 private CinematicMeshBuilder() {}
 static Point p(double x,double y,double z){return new Point((float)x,(float)y,(float)z);}
 static List<Face> join(List<Face>... groups){var out=new ArrayList<Face>();for(var g:groups)out.addAll(g);return List.copyOf(out);}
 static List<Face> move(List<Face> faces,double x,double y,double z,double turn,double tilt,double scale){
  var out=new ArrayList<Face>();double c=Math.cos(turn),s=Math.sin(turn),ct=Math.cos(tilt),st=Math.sin(tilt);
  for(var f:faces){var vertices=new ArrayList<Point>();for(var v:f.vertices()){
   double px=v.x()*scale,py=v.y()*scale,pz=v.z()*scale,rx=px*c-py*s,ry=px*s+py*c;
   vertices.add(p(rx+x,ry*ct-pz*st+y,ry*st+pz*ct+z));
  }out.add(new Face(vertices,f.tone(),f.alpha()));}return List.copyOf(out);
 }
 private static void face(List<Face> out,int tone,float alpha,Point... points){
  out.add(new Face(points.length==3?List.of(points[0],points[1],points[2],points[2]):List.of(points),tone,alpha));
 }
 /** Four-sided capped sweep; real thickness at every bend, including projectile tails. */
 static List<Face> tube(Point[] centers,double[] radii,int tone,float alpha,boolean loop){
  var out=new ArrayList<Face>();int n=centers.length;Point[][] rings=new Point[n][4];
  for(int i=0;i<n;i++){
   Point a=centers[i==0?(loop?n-1:0):i-1],b=centers[i==n-1?(loop?0:n-1):i+1];
   double dx=b.x()-a.x(),dy=b.y()-a.y(),dz=b.z()-a.z(),length=Math.sqrt(dx*dx+dy*dy+dz*dz);
   dx/=length;dy/=length;dz/=length;
   double ux,uy,uz;if(Math.abs(dy)<.92){ux=dz;uy=0;uz=-dx;}else{ux=-dy;uy=dx;uz=0;}
   double ul=Math.sqrt(ux*ux+uy*uy+uz*uz);ux/=ul;uy/=ul;uz/=ul;
   double vx=dy*uz-dz*uy,vy=dz*ux-dx*uz,vz=dx*uy-dy*ux;
   for(int j=0;j<4;j++){double c=j==0?1:j==2?-1:0,s=j==1?1:j==3?-1:0,r=radii[i];
    rings[i][j]=p(centers[i].x()+r*(ux*c+vx*s),centers[i].y()+r*(uy*c+vy*s),centers[i].z()+r*(uz*c+vz*s));}
  }
  for(int i=0;i<(loop?n:n-1);i++)for(int j=0;j<4;j++){
   int k=(j+1)%4,next=(i+1)%n;face(out,(tone+j)%3,alpha,rings[i][j],rings[i][k],rings[next][k],rings[next][j]);
  }
  if(!loop){face(out,2,alpha,rings[0][3],rings[0][2],rings[0][1],rings[0][0]);face(out,1,alpha,rings[n-1]);}
  return List.copyOf(out);
 }
 static List<Face> beam(Point a,Point b,double width,int tone,float alpha){return tube(new Point[]{a,b},new double[]{width,width*.7},tone,alpha,false);}
 static List<Face> flame(double height,double width,double lean,float alpha){
  return tube(new Point[]{p(0,0,0),p(-lean*.35,height*.28,0),p(lean*.3,height*.68,width*.3),p(lean,height,width*.12)},new double[]{width*.65,width,width*.5,.012},0,alpha,false);
 }
 static List<Face> crystal(double height,double width,double lean,float alpha){
  Point bottom=p(0,-height*.12,0),top=p(lean,height,0);Point[] belt={p(width,height*.12,0),p(0,height*.12,width*.65),p(-width,height*.12,0),p(0,height*.12,-width*.65)};
  var out=new ArrayList<Face>();for(int i=0;i<4;i++){int j=(i+1)%4;face(out,i%3,alpha,bottom,belt[j],belt[i]);face(out,i%3,alpha,top,belt[i],belt[j]);}return List.copyOf(out);
 }
 static List<Face> orb(double radius,double height,float alpha){
  return crystal(height,radius,0,alpha);
 }
 static List<Face> extrude(Point[] polygon,double depth,int tone,float alpha){
  var out=new ArrayList<Face>();int n=polygon.length;Point[] front=new Point[n],back=new Point[n];
  for(int i=0;i<n;i++){front[i]=p(polygon[i].x(),polygon[i].y(),depth);back[i]=p(polygon[i].x(),polygon[i].y(),-depth);}
  // All current outlines are quadrilateral convex silhouettes, with flat sealed end caps.
  face(out,tone,alpha,front);face(out,2,alpha,back[3],back[2],back[1],back[0]);
  for(int i=0;i<n;i++){int j=(i+1)%n;face(out,i%2==0?1:tone,alpha,back[i],back[j],front[j],front[i]);}return List.copyOf(out);
 }
 static List<Face> box(double x,double y,double width,double height,double depth,int tone,float alpha){
  return extrude(new Point[]{p(x-width,y,0),p(x+width,y,0),p(x+width,y+height,0),p(x-width,y+height,0)},depth,tone,alpha);
 }
 /** A solid tapered blade, crossguard and grip: recognizably a sword from every angle. */
 static List<Face> sword(double height,double width,float alpha){
  var blade=extrude(new Point[]{p(0,height*.18,0),p(width,height*.32,0),p(0,height,0),p(-width,height*.32,0)},width*.23,0,alpha);
  return join(blade,box(0,height*.17,width*1.65,height*.055,width*.32,1,alpha),box(0,0,width*.25,height*.17,width*.27,2,alpha));
 }
 static List<Face> arc(double radius,double span,double thickness,double depth,double tilt){
  int n=7;Point[] path=new Point[n];double[] radii=new double[n];
  for(int i=0;i<n;i++){double a=-span/2+span*i/(n-1);path[i]=p(Math.sin(a)*radius,Math.cos(a)*tilt,Math.cos(a)*radius);radii[i]=i==0||i==n-1?thickness*.13:thickness*(.6+.4*Math.sin(i*Math.PI/(n-1)));}
  // Depth is achieved by an anisotropic transform on a closed swept blade edge.
  var tube=tube(path,radii,0,.88f,false);var out=new ArrayList<Face>();
  for(var f:tube){var v=new ArrayList<Point>();for(var p:f.vertices())v.add(p(p.x(),p.y()*depth,p.z()));out.add(new Face(v,f.tone(),f.alpha()));}return List.copyOf(out);
 }
 static List<Face> ring(double radius,double y,double width,int sections,float alpha){
  Point[] path=new Point[sections];double[] radii=new double[sections];
  for(int i=0;i<sections;i++){double a=i*Math.PI*2/sections;path[i]=p(Math.cos(a)*radius,y,Math.sin(a)*radius);radii[i]=width;}
  return tube(path,radii,0,alpha,true);
 }
 /** Extruded hooked water cross sections retain a rolling lip and a visible back wall. */
 static List<Face> wave(double width,double height,double curl,int sections){
  Point[][] grid=new Point[sections+1][6];var out=new ArrayList<Face>();
  for(int i=0;i<=sections;i++){
   double x=-width/2+width*i/sections,h=height*(.75+.25*Math.cos(x*Math.PI/width));
   double[][] yz={{-.28,-.18},{h*.62,-.12},{h,curl*.35},{h*.83,curl},{h*.65,curl*.75},{-.28,.22}};
   for(int j=0;j<6;j++)grid[i][j]=p(x,yz[j][0],yz[j][1]+.10*Math.cos(x*2));
  }
  for(int i=0;i<sections;i++)for(int j=0;j<6;j++){int k=(j+1)%6;face(out,j==2?3:j==1?1:0,j==2?.84f:.64f,grid[i][j],grid[i+1][j],grid[i+1][k],grid[i][k]);}
  // Four triangles seal each concave hooked profile without covering the curl opening.
  int[][] triangles={{0,1,5},{1,4,5},{1,2,4},{2,3,4}};
  for(var t:triangles){face(out,0,.64f,grid[0][t[0]],grid[0][t[1]],grid[0][t[2]]);face(out,1,.64f,grid[sections][t[2]],grid[sections][t[1]],grid[sections][t[0]]);}
  return List.copyOf(out);
 }
 static List<Face> tendril(double radius,double height,double turns,double phase,int segments,float alpha){
  Point[] path=new Point[segments+1];double[] radii=new double[segments+1];
  for(int i=0;i<=segments;i++){double t=(double)i/segments,a=t*Math.PI*2*turns+phase;
   path[i]=p(Math.cos(a)*radius*(1-t*.4),height*t,Math.sin(a)*radius*(1-t*.4));radii[i]=.105*(1-t)+.018;}
  return tube(path,radii,2,alpha,false);
 }
 static List<Face> bolt(Point start,Point end,double width,double side){
  Point[] path=new Point[5];double[] radii=new double[5];
  for(int i=0;i<5;i++){double t=i/4.;path[i]=p(start.x()+(end.x()-start.x())*t+(i==1?side:i==3?-side:0),start.y()+(end.y()-start.y())*t,start.z()+(end.z()-start.z())*t);radii[i]=width*(i%2==0?1:.7);}
  return tube(path,radii,1,.98f,false);
 }
}

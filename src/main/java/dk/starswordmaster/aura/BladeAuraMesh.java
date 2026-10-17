package dk.starswordmaster.aura;

import java.util.ArrayList;
import java.util.List;

/** Bounded geometry in the generated sword model's 0..1 coordinates, before its display transform. */
public final class BladeAuraMesh {
 public static final int MAX_QUADS=64;
 private BladeAuraMesh(){}
 public record Point(float x,float y,float z){}
 public record Quad(List<Point> vertices,int rgb,int alpha){}
 public record Profile(float width,int details,int rank,boolean swinging){}

 public static Profile profile(int rank,float power,int level,boolean swordsman,int quality,double distanceSquared,boolean swinging){
  int r=Math.clamp(rank,1,4),q=Math.clamp(quality,0,2);
  float p=Float.isFinite(power)?Math.clamp(power,0,1):0;
  float training=(float)(Math.log1p(Math.max(0,level))/(12+Math.log1p(Math.max(0,level))));
  float width=(.016f+r*.007f+(r-1)*(r-1)*.009f+p*.023f+training*.014f)*(swordsman?1.22f:1);
  int details=(r*2+q*3+(swinging?6:0));
  if(distanceSquared>1024)details=Math.max(1,details/4);
  else if(distanceSquared>256)details=Math.max(2,details/2);
  return new Profile(width,details,r,swinging);
 }

 public static int quadCount(Profile p,boolean elemental){
  int count=7+(p.rank()>=2?2:0)+(p.rank()>=3?p.rank():0)+p.details();
  if(elemental)count+=(p.details()+3)/4;
  if(p.swinging())count+=p.rank()+1;
  return Math.min(MAX_QUADS,count);
 }

 public static List<Quad> build(Profile p,float age,int rgb,int highlight,int shade,int accent){
  var out=new ArrayList<Quad>(MAX_QUADS);
  float time=Float.isFinite(age)?age:0;
  float pulse=1+.055f*(float)Math.sin(time*.13);
  float width=p.width()*pulse;
  // A soft outer silhouette, personal-color blade sheath and narrow bright edge on both faces.
  for(int side:new int[]{-1,1}){
   float z=side*.041f;
   ribbon(out,.06f,1.035f,0,width*1.8f,z,p.rank()>=3?rgb:shade,28+p.rank()*18);
   ribbon(out,.08f,1.02f,0,width,z+side*.001f,rgb,75+p.rank()*30);
   ribbon(out,.1f,1.005f,0,width*.36f,z+side*.002f,highlight,70+p.rank()*38);
  }
  // A perpendicular blade sheet keeps the glow visible when the flat sprite is edge-on.
  out.add(new Quad(List.of(point(.08f,0,-width),point(1.02f,0,-width*.3f),point(1.02f,0,width*.3f),point(.08f,0,width)),rgb,70+p.rank()*12));
  if(p.rank()>=2){
   for(int side:new int[]{-1,1})ribbon(out,.17f,.96f,side*width*1.35f,width*.18f,.052f,rgb,110+p.rank()*20);
  }
  if(p.rank()>=3){
   // Orbiting facets become a crown at rank four, with no independent ticking or history.
   for(int i=0;i<p.rank();i++){
    float s=.26f+i*.19f;
    float offset=(float)Math.cos(time*.065+i*2.1)*width*2.5f;
    diamond(out,s,offset,width*(p.rank()==4?.65f:.48f),.065f,highlight,p.rank()==4?230:175);
   }
  }
  for(int i=0;i<p.details();i++){
   float travel=(time*.009f+i*.618034f)%1;
   if(travel<0)travel++;
   int side=(i&1)==0?1:-1;
   float offset=side*width*(1.35f+.7f*(float)Math.sin(time*.04f+i));
   diamond(out,.13f+travel*.86f,offset,.009f+(p.rank()*.0025f),.06f+(i%3)*.018f,rgb,100+(i%3)*24);
   if(accent>=0&&i%4==0)diamond(out,.17f+travel*.75f,offset*1.2f,.011f,.09f,accent,165);
  }
  if(p.swinging()){
   // Short, item-local echoes are regenerated only while swinging. Nothing can linger after unequip.
   for(int i=1;i<=p.rank()+1;i++){
    float offset=-width*(1.4f+i*.95f);
    ribbon(out,.2f+i*.025f,1-i*.035f,offset,width*.25f,.075f+i*.008f,rgb,Math.max(18,72-i*10));
   }
  }
  return List.copyOf(out.subList(0,Math.min(MAX_QUADS,out.size())));
 }
 private static Point point(float s,float offset,float z){
  return new Point(.24f+s*.70f-offset*.7071068f,.24f+s*.70f+offset*.7071068f,.5f+z);
 }
 private static void ribbon(List<Quad> out,float from,float to,float offset,float halfWidth,float z,int rgb,int alpha){
  out.add(new Quad(List.of(point(from,offset-halfWidth,z),point(to,offset-halfWidth*.65f,z),point(to,offset+halfWidth*.65f,z),point(from,offset+halfWidth,z)),rgb,alpha));
 }
 private static void diamond(List<Quad> out,float s,float offset,float size,float z,int rgb,int alpha){
  out.add(new Quad(List.of(point(s-size,offset,z),point(s,offset-size,z),point(s+size,offset,z),point(s,offset+size,z)),rgb,alpha));
 }
}

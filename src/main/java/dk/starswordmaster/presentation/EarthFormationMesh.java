package dk.starswordmaster.presentation;

import java.util.*;

/** Reusable opaque stone geometry and finite cosmetic choreography. No world/gameplay dependencies. */
public final class EarthFormationMesh {
 public static final int MAX_FORMATIONS=16,MAX_ROCKS_PER_FORMATION=9,MAX_FRAME_QUADS=2048;
 public enum Kind {SPIKE,WALL,QUAKE,WORLDBREAKER}
 public record Point(float x,float y,float z){}
 public record Face(List<Point> points,float nx,float ny,float nz,int shade){}
 public record Rock(double x,double z,float width,float depth,float height,int delay,int variant,float turn){}
 private static final List<Face>[][][] CACHE=cache();
 private EarthFormationMesh(){}
 public static Kind kind(String ability){return switch(ability){case "stone_spike"->Kind.SPIKE;case "earthen_wall"->Kind.WALL;case "quake_blade"->Kind.QUAKE;case "worldbreaker"->Kind.WORLDBREAKER;default->null;};}
 public static List<Rock> layout(Kind kind,float power,float yaw,long seed){
  float p=Float.isFinite(power)?Math.clamp(power,0,1):0;
  double angle=Float.isFinite(yaw)?Math.toRadians(yaw):0,c=Math.cos(angle),s=Math.sin(angle);
  var random=new Random(seed);var rocks=new ArrayList<Rock>();
  int count=switch(kind){case SPIKE->3;case WALL->5;case QUAKE->8;case WORLDBREAKER->9;};
  for(int i=0;i<count;i++){
   double x,z;float width,depth,height;int delay;
   switch(kind){
    case SPIKE->{x=i==0?0:(i==1?-.72:.68);z=i==0?0:.3;width=i==0?.75f:.36f;depth=width;height=(i==0?2.6f:1.5f)+p;delay=i==0?2:4;}
    case WALL->{x=(i-2)*1.05;z=2.8-.15*Math.abs(i-2);width=.79f;depth=.65f;height=2.6f+.65f*p+(i%2)*.25f;delay=2+Math.abs(i-2);}
    case QUAKE->{x=(i%2==0?-.12:.12);z=2.1+i*1.05;width=.57f;depth=.8f;height=1.6f+p+i*.10f;delay=2+i*2;}
    case WORLDBREAKER->{double a=i*Math.PI*2/count,r=3.7+(i%2)*1.2;x=Math.cos(a)*r;z=Math.sin(a)*r;width=.95f+.28f*p;depth=width;height=4.1f+2.1f*p+random.nextFloat();delay=2+(i%3)*2;}
    default->throw new AssertionError(kind);
   }
   rocks.add(new Rock(x*c-z*s,x*s+z*c,width,depth,height,delay,random.nextInt(4),random.nextFloat()*360));
  }
  return List.copyOf(rocks);
 }
 public static int lifetime(Kind kind){return switch(kind){case SPIKE->34;case WALL->100;case QUAKE->40;case WORLDBREAKER->56;};}
 /** Rise from below the sampled ground, hold, then submerge; the wall finishes at the 100 tick buff boundary. */
 public static float reveal(Kind kind,Rock rock,double age){
  if(!Double.isFinite(age)||age<rock.delay()||age>=lifetime(kind))return 0;
  double local=age-rock.delay(),rise=smooth(Math.min(1,local/(kind==Kind.WORLDBREAKER?7:4)));
  double end=kind==Kind.QUAKE?Math.min(lifetime(kind),rock.delay()+17):lifetime(kind);
  double fall=1-smooth(Math.clamp((age-(end-8))/8,0,1));
  return (float)(rise*fall);
 }
 private static double smooth(double v){return v*v*(3-2*v);}
 public static List<Face> faces(int variant,int quality,boolean wall){return CACHE[wall?1:0][Math.floorMod(variant,4)][Math.clamp(quality,0,2)];}
 @SuppressWarnings("unchecked") private static List<Face>[][][] cache(){
  List<Face>[][][] cache=(List<Face>[][][])new List<?>[2][4][3];
  for(int wall=0;wall<2;wall++)for(int variant=0;variant<4;variant++)for(int quality=0;quality<3;quality++)cache[wall][variant][quality]=build(variant,quality==0?4:quality==1?5:6,wall==1);
  return cache;
 }
 private static List<Face> build(int variant,int sides,boolean wall){
  var random=new Random(751L+variant);var points=new Point[3][sides];var faces=new ArrayList<Face>();
  for(int ring=0;ring<3;ring++)for(int i=0;i<sides;i++){
   double angle=i*Math.PI*2/sides;
   float radius=(ring==0?1:ring==1?.84f:wall?.62f:.09f)*( .88f+random.nextFloat()*.12f);
   float y=ring==0?0:ring==1?.48f+(random.nextFloat()-.5f)*.14f:.88f+random.nextFloat()*.12f;
   points[ring][i]=new Point((float)Math.cos(angle)*radius+(ring==2?.12f:0),y,(float)Math.sin(angle)*radius+(ring==2?-.08f:0));
  }
  for(int i=0;i<sides;i++){
   int j=(i+1)%sides;
   for(int ring=0;ring<2;ring++)add(faces,points[ring][i],points[ring+1][i],points[ring+1][j],points[ring][j],i);
   add(faces,points[2][i],new Point(.12f,1.02f,-.08f),points[2][j],points[2][j],i+2);
   add(faces,points[0][j],new Point(0,0,0),points[0][i],points[0][i],0);
  }
  return List.copyOf(faces);
 }
 private static void add(List<Face> faces,Point a,Point b,Point c,Point d,int shade){
  float ux=b.x-a.x,uy=b.y-a.y,uz=b.z-a.z,vx=c.x-a.x,vy=c.y-a.y,vz=c.z-a.z;
  float nx=uy*vz-uz*vy,ny=uz*vx-ux*vz,nz=ux*vy-uy*vx,length=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);
  faces.add(new Face(List.of(a,b,c,d),nx/length,ny/length,nz/length,shade%4));
 }
 public static final class FrameBudget {
  private int quads;
  public void reset(){quads=0;}
  public boolean take(int count){if(count<1||count>MAX_FRAME_QUADS-quads)return false;quads+=count;return true;}
 }
}

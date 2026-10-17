package dk.starswordmaster.client;

import dk.starswordmaster.presentation.EarthFormationMesh;
import java.lang.reflect.*;
import java.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.math.*;

/** Smoke-only observations of the real terrain renderer's emitted native entity vertices. */
public final class EarthSmokeCapture {
 public record Frame(int vertices,int normals,boolean finite,boolean opaque,boolean noncoplanar,double transformError,double groundError,double drift,double minY,double maxY){}
 private static final ArrayDeque<Frame> FRAMES=new ArrayDeque<>();
 private static final IdentityHashMap<Object,Vec3d> ANCHORS=new IdentityHashMap<>();
 private static final ArrayList<Vec3d> EXPECTED=new ArrayList<>();
 private static Vec3d camera=Vec3d.ZERO;
 private static Counter counter;
 private static double groundError,drift;
 public static int maximumFrameVertices;
 private EarthSmokeCapture(){}
 public static void reset(){FRAMES.clear();ANCHORS.clear();EXPECTED.clear();counter=null;maximumFrameVertices=0;}
 public static List<Frame> recent(){return List.copyOf(FRAMES);}
 private static Object call(Object object,String name){try{var m=object.getClass().getDeclaredMethod(name);m.setAccessible(true);return m.invoke(object);}catch(ReflectiveOperationException e){throw new AssertionError("Earth smoke accessor "+name,e);}}
 private static Object field(Class<?> type,Object owner,String name){try{var f=type.getDeclaredField(name);f.setAccessible(true);return f.get(owner);}catch(ReflectiveOperationException e){throw new AssertionError("Earth smoke field "+name,e);}}
 public static void begin(net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext context){
  EXPECTED.clear();counter=null;groundError=drift=0;
  if(!Boolean.getBoolean("starsmoke.finishing"))return;
  camera=context.worldState().cameraRenderState.pos;
  var client=MinecraftClient.getInstance();
  for(var draw:(List<?>)field(EarthFormationRenderer.class,null,"frame")){
   Object anchored=call(draw,"anchored");var rock=(EarthFormationMesh.Rock)call(anchored,"rock");
   var base=(Vec3d)call(anchored,"base");var old=ANCHORS.putIfAbsent(anchored,base);
   if(old!=null)drift=Math.max(drift,old.distanceTo(base));
   double ground=nativeGround(client,base,rock);
   groundError=Math.max(groundError,Math.abs(base.y+.12-ground));
   float reveal=(float)call(draw,"reveal");int quality=(int)call(draw,"quality");
   var kind=(EarthFormationMesh.Kind)call(draw,"kind");double angle=Math.toRadians(rock.turn()),cos=Math.cos(angle),sin=Math.sin(angle);
   for(var face:EarthFormationMesh.faces(rock.variant(),quality,kind==EarthFormationMesh.Kind.WALL))for(var p:face.points()){
    double x=p.x()*rock.width(),z=p.z()*rock.depth();
    EXPECTED.add(new Vec3d(base.x+x*cos+z*sin,base.y-(1-reveal)*rock.height()+p.y()*rock.height(),base.z-x*sin+z*cos));
   }
  }
 }
 /** Independent native collision read, rather than relying on the production terrain helper. */
 private static double nativeGround(MinecraftClient client,Vec3d base,EarthFormationMesh.Rock rock){
  if(client.world==null)return Double.NaN;double lowest=Double.POSITIVE_INFINITY;
  for(int i=0;i<5;i++){
   double x=base.x+(i==1?-1:i==2?1:0)*rock.width()*.7,z=base.z+(i==3?-1:i==4?1:0)*rock.depth()*.7;
   int bx=MathHelper.floor(x),bz=MathHelper.floor(z);double surface=Double.NaN;
   if(!client.world.isChunkLoaded(bx>>4,bz>>4))return Double.NaN;
   for(int y=MathHelper.floor(base.y+2);y>=MathHelper.floor(base.y)-4;y--){
    var pos=new BlockPos(bx,y,bz);var state=client.world.getBlockState(pos);
    for(var box:state.getCollisionShape(client.world,pos).getBoundingBoxes())if(x-bx>=box.minX&&x-bx<=box.maxX&&z-bz>=box.minZ&&z-bz<=box.maxZ&&y+box.maxY<=base.y+1.5)surface=Double.isNaN(surface)?y+box.maxY:Math.max(surface,y+box.maxY);
    if(Double.isFinite(surface))break;
   }
   if(!Double.isFinite(surface))return Double.NaN;lowest=Math.min(lowest,surface);
  }
  return lowest;
 }
 public static VertexConsumer wrap(VertexConsumer delegate){
  if(!Boolean.getBoolean("starsmoke.finishing"))return delegate;
  return counter=new Counter(delegate);
 }
 public static void end(){
  if(counter==null)return;
  maximumFrameVertices=Math.max(maximumFrameVertices,counter.vertices);
  boolean finite=counter.finite&&Double.isFinite(groundError)&&counter.vertices==EXPECTED.size();
  if(FRAMES.size()==512)FRAMES.removeFirst();
  FRAMES.addLast(new Frame(counter.vertices,counter.normals,finite,counter.opaque,counter.noncoplanar,counter.transformError,groundError,drift,counter.minY,counter.maxY));counter=null;
 }
 private static final class Counter implements VertexConsumer {
  final VertexConsumer delegate;int vertices,normals;boolean finite=true,opaque=true,noncoplanar;
  double transformError,minY=Double.POSITIVE_INFINITY,maxY=Double.NEGATIVE_INFINITY;
  Vec3d origin,basis,plane;
  Counter(VertexConsumer delegate){this.delegate=delegate;}
  public VertexConsumer vertex(float x,float y,float z){
   var p=new Vec3d(x+camera.x,y+camera.y,z+camera.z);finite&=Double.isFinite(p.x+p.y+p.z);
   if(vertices<EXPECTED.size())transformError=Math.max(transformError,p.distanceTo(EXPECTED.get(vertices)));else finite=false;
   minY=Math.min(minY,p.y);maxY=Math.max(maxY,p.y);
   if(origin==null)origin=p;else{
    var offset=p.subtract(origin);
    if(basis==null&&offset.lengthSquared()>.00001)basis=offset;
    else if(basis!=null&&plane==null&&basis.crossProduct(offset).lengthSquared()>.00001)plane=basis.crossProduct(offset).normalize();
    else if(plane!=null&&Math.abs(plane.dotProduct(offset))>.001)noncoplanar=true;
   }
   vertices++;delegate.vertex(x,y,z);return this;
  }
  public VertexConsumer color(int r,int g,int b,int a){opaque&=a==255;delegate.color(r,g,b,a);return this;}
  public VertexConsumer texture(float u,float v){finite&=Float.isFinite(u)&&Float.isFinite(v);delegate.texture(u,v);return this;}
  public VertexConsumer overlay(int u,int v){delegate.overlay(u,v);return this;}
  public VertexConsumer light(int u,int v){delegate.light(u,v);return this;}
  public VertexConsumer normal(float x,float y,float z){normals++;finite&=Float.isFinite(x)&&Float.isFinite(y)&&Float.isFinite(z)&&x*x+y*y+z*z>.001f;delegate.normal(x,y,z);return this;}
 }
}

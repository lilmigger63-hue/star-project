package dk.starswordmaster.client;

import dk.starswordmaster.network.AbilityPresentationPayload;
import dk.starswordmaster.presentation.*;
import java.lang.reflect.*;
import java.util.*;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;

/** Smoke-only interception of the vertices actually submitted by the cinematic renderer. */
public final class OverhaulSmokeCapture {
 public static final class Observation {
  public int vertices,normals,frames;public boolean finite=true,bounded=true,normal=true,alpha=true,noncoplanar,personal;
  public final Set<Integer> qualities=new TreeSet<>(),colors=new HashSet<>(),expectedColors=new HashSet<>();
  public final Set<String> stages=new HashSet<>();
  private Vec3d origin,basis,plane;
  private void point(Vec3d p){
   if(origin==null)origin=p;
   else {var v=p.subtract(origin);if(basis==null&&v.lengthSquared()>1e-6)basis=v;
    else if(basis!=null&&plane==null&&basis.crossProduct(v).lengthSquared()>1e-6)plane=basis.crossProduct(v).normalize();
    else if(plane!=null&&Math.abs(plane.dotProduct(v))>1e-3)noncoplanar=true;}
  }
 }
 private static final Map<String,Observation> LOCAL=new LinkedHashMap<>(),REMOTE=new LinkedHashMap<>();
 private static final Map<String,Observation> RECENT=new LinkedHashMap<>();
 private static Vec3d camera=Vec3d.ZERO;private static Observation current,recent;private static AbilityPresentationPayload event;
 private static int frameVertices;public static int maximumFrameVertices;
 private OverhaulSmokeCapture(){}
 public static boolean enabled(){return Boolean.getBoolean("starsmoke.overhaul");}
 public static void reset(){RECENT.clear();}
 public static Observation recent(String id){return RECENT.get(id);}
 public static Map<String,Observation> all(boolean remote){return Map.copyOf(remote?REMOTE:LOCAL);}
 private static Object field(Object object,String name){try{var f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);}catch(ReflectiveOperationException e){throw new AssertionError("Overhaul smoke field "+name,e);}}
 private static Object value(Object object,String name){try{var m=object.getClass().getDeclaredMethod(name);m.setAccessible(true);return m.invoke(object);}catch(NoSuchMethodException e){return field(object,name);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 public static void begin(WorldRenderContext context){if(!enabled())return;camera=context.worldState().cameraRenderState.pos;frameVertices=0;current=recent=null;}
 /** Invoked immediately before real geometry submission, with the renderer's actual selected draw. */
 public static void select(Object draw){
  if(!enabled())return;var scene=value(draw,"scene");event=(AbilityPresentationPayload)field(scene,"event");
  boolean local=event.caster().equals(MinecraftClient.getInstance().player.getUuid());
  current=(local?LOCAL:REMOTE).computeIfAbsent(event.ability(),k->new Observation());
  recent=local?RECENT.computeIfAbsent(event.ability(),k->new Observation()):null;
  int quality=((Number)value(draw,"quality")).intValue();float age=((Number)value(draw,"age")).floatValue();
  String stage=AbilityVfxTimeline.sample(age,AbilityPresentation.ALL.get(event.ability()).ticks(),0).phase().name();
  for(var o:observations()){for(int rgb:(int[])field(scene,"colors"))o.expectedColors.add(rgb);o.frames++;o.qualities.add(quality);o.stages.add(stage);o.personal|=event.visual().auraUnlocked()&&dk.starswordmaster.progression.Ability.ALL.get(event.ability()).stamina()>0;}
 }
 private static List<Observation> observations(){return current==null?List.of():recent==null?List.of(current):List.of(current,recent);}
 public static VertexConsumer wrap(VertexConsumer delegate){return enabled()?new Counter(delegate):delegate;}
 public static void end(){if(!enabled())return;maximumFrameVertices=Math.max(maximumFrameVertices,frameVertices);current=recent=null;}
 private static final class Counter implements VertexConsumer {
  private final VertexConsumer delegate;Counter(VertexConsumer delegate){this.delegate=delegate;}
  public VertexConsumer vertex(float x,float y,float z){
   var p=new Vec3d(x+camera.x,y+camera.y,z+camera.z);boolean finite=Double.isFinite(p.x+p.y+p.z);
   boolean bounded=event==null||Math.min(p.squaredDistanceTo(event.origin()),p.squaredDistanceTo(event.impact()))<=64*64;
   for(var o:observations()){o.vertices++;o.finite&=finite;o.bounded&=bounded;o.point(p);}frameVertices++;delegate.vertex(x,y,z);return this;
  }
  public VertexConsumer color(int r,int g,int b,int a){for(var o:observations()){o.alpha&=a>=0&&a<=220;o.colors.add((r<<16)|(g<<8)|b);}delegate.color(r,g,b,a);return this;}
  public VertexConsumer texture(float u,float v){for(var o:observations())o.finite&=Float.isFinite(u)&&Float.isFinite(v);delegate.texture(u,v);return this;}
  public VertexConsumer overlay(int u,int v){delegate.overlay(u,v);return this;}
  public VertexConsumer light(int u,int v){delegate.light(u,v);return this;}
  public VertexConsumer normal(float x,float y,float z){float len=x*x+y*y+z*z;for(var o:observations()){o.normals++;o.finite&=Float.isFinite(len);o.normal&=len>.90f&&len<1.10f;}delegate.normal(x,y,z);return this;}
 }
}

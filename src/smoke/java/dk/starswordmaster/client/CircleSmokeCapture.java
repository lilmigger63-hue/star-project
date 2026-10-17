package dk.starswordmaster.client;

import dk.starswordmaster.client.circles.CircleSnapshot;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

/** Smoke-only independent comparison with the actual deferred, PAL-posed native arm cuboid matrix. */
public final class CircleSmokeCapture {
 public record Draw(int entityId,int hand,boolean firstPerson,boolean local,String element,int layers,int vertices,boolean finite,float[] palmMatrix){}
 public record Alignment(int entityId,int hand,boolean firstPerson,float matrixError,float centerError,float[] nativePalm,float[] circlePalm){}
 private record Pending(CircleSnapshot snapshot,ModelPart arm,float[] matrix){}
 private static final ArrayList<Pending> PENDING=new ArrayList<>();
 private static final ArrayDeque<Draw> DRAWS=new ArrayDeque<>();
 private static final ArrayDeque<Alignment> ALIGNMENTS=new ArrayDeque<>();
 public static long submitted,rendered,vertices,firstPerson,thirdPerson,remote,nativeComparisons,unmatchedNativeArms,frames;
 public static float maximumError;
 private CircleSmokeCapture(){}
 public static void reset(){PENDING.clear();DRAWS.clear();ALIGNMENTS.clear();submitted=rendered=vertices=firstPerson=thirdPerson=remote=nativeComparisons=unmatchedNativeArms=frames=0;maximumError=0;}
 public static List<Draw> recent(){return List.copyOf(DRAWS);}
 public static List<Alignment> alignments(){return List.copyOf(ALIGNMENTS);}
 public static int pending(){return PENDING.size();}
 public static void endFrame(){unmatchedNativeArms+=PENDING.size();PENDING.clear();frames++;}
 public static void extracted(CircleSnapshot snapshot,ModelPart arm,MatrixStack matrices){
  if(PENDING.size()==256)PENDING.removeFirst();
  PENDING.add(new Pending(snapshot,arm,matrices.peek().getPositionMatrix().get(new float[16])));
  submitted++;
 }
 public static OrderedRenderCommandQueue.Custom wrap(CircleSnapshot snapshot,OrderedRenderCommandQueue.Custom original){
  return (entry,delegate)->{
   var counter=new CountingVertices(delegate);original.render(entry,counter);
   rendered++;vertices+=counter.vertices;
   var player=MinecraftClient.getInstance().player;
   boolean local=player!=null&&player.getUuid().equals(snapshot.player());
   if(snapshot.firstPerson())firstPerson++;else thirdPerson++;
   if(!local)remote++;
   if(DRAWS.size()==512)DRAWS.removeFirst();
   DRAWS.addLast(new Draw(snapshot.entityId(),snapshot.hand(),snapshot.firstPerson(),local,snapshot.element().name(),snapshot.profile().layers(),counter.vertices,counter.finite,entry.getPositionMatrix().get(new float[16])));
  };
 }
 /** Receives the real native matrix immediately before that arm's cuboid vertices are emitted. */
 public static void nativeCuboids(ModelPart arm,MatrixStack.Entry entry){
  if(PENDING.isEmpty())return;
  int best=-1;float error=Float.POSITIVE_INFINITY;float[] bestMatrix=null;
  for(int n=0;n<PENDING.size();n++){
   var pending=PENDING.get(n);if(pending.arm()!=arm)continue;
   var snapshot=pending.snapshot();
   float x=(snapshot.hand()==0?1:-1)*(snapshot.slim()?.03125f:.0625f);
   float[] nativePalm=new Matrix4f(entry.getPositionMatrix()).translate(x,.80f,0).rotateX((float)Math.PI/2).get(new float[16]);
   float difference=0;for(int i=0;i<16;i++)difference=Math.max(difference,Math.abs(nativePalm[i]-pending.matrix()[i]));
   if(difference<error){best=n;error=difference;bestMatrix=nativePalm;}
  }
  // The same vanilla model is reused by many players, armor and outline passes. Only consume its matching root transform.
  if(best<0||error>.001f)return;
  var pending=PENDING.remove(best);var snapshot=pending.snapshot();
  float center=0;for(int i=12;i<15;i++)center=Math.max(center,Math.abs(bestMatrix[i]-pending.matrix()[i]));
  nativeComparisons++;maximumError=Math.max(maximumError,error);
  if(ALIGNMENTS.size()==512)ALIGNMENTS.removeFirst();
  ALIGNMENTS.addLast(new Alignment(snapshot.entityId(),snapshot.hand(),snapshot.firstPerson(),error,center,bestMatrix,pending.matrix()));
 }
 private static final class CountingVertices implements VertexConsumer {
  private final VertexConsumer delegate;int vertices;boolean finite=true;
  CountingVertices(VertexConsumer delegate){this.delegate=delegate;}
  public VertexConsumer vertex(float x,float y,float z){vertices++;finite&=Float.isFinite(x)&&Float.isFinite(y)&&Float.isFinite(z);delegate.vertex(x,y,z);return this;}
  public VertexConsumer color(int r,int g,int b,int a){delegate.color(r,g,b,a);return this;}
  public VertexConsumer texture(float u,float v){delegate.texture(u,v);return this;}
  public VertexConsumer overlay(int u,int v){delegate.overlay(u,v);return this;}
  public VertexConsumer light(int u,int v){delegate.light(u,v);return this;}
  public VertexConsumer normal(float x,float y,float z){delegate.normal(x,y,z);return this;}
 }
}

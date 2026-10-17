package dk.starswordmaster.client;

import dk.starswordmaster.client.aura.SwordAuraSnapshot;
import dk.starswordmaster.client.aura.AuraRenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import java.util.ArrayDeque;
import java.util.List;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

/** Smoke-only observations of native deferred aura commands and their actual emitted vertices. */
public final class AuraSmokeCapture {
 public record Draw(int entityId,int hand,ItemDisplayContext context,boolean local,boolean swinging,boolean fullBrightShader,int rank,int color,int vertexCount,boolean finite,float[] transform){}
 public record ModelAlignment(int entityId,ItemDisplayContext context,float matrixError,float minZ,float maxZ,int opaqueBladeSamples,int bladeSamples){}
 private record Pending(SwordAuraSnapshot snapshot,float[] matrix){}
 private static final ArrayDeque<Draw> RECENT=new ArrayDeque<>();
 private static final ArrayDeque<ModelAlignment> MODELS=new ArrayDeque<>();
 private static final ArrayDeque<Pending> PENDING=new ArrayDeque<>();
 public static long submitted,rendered,vertices,firstPerson,thirdPerson,remote;
 public static long gpuAuraDraws,gpuAuraAfterItems,gpuAuraBeforeItems;
 private static boolean nativeItemDrawn;
 public static void beginFrame(){nativeItemDrawn=false;}
 /** Observe actual RenderLayer.draw calls, after opaque buffers have reached the GPU. */
 public static void gpuLayer(RenderLayer layer){
  if(!Boolean.getBoolean("starsmoke.auras"))return;
  if(layer==TexturedRenderLayers.getItemEntityTranslucentCull())nativeItemDrawn=true;
  if(AuraRenderLayer.isAura(layer)){
   gpuAuraDraws++;
   if(nativeItemDrawn)gpuAuraAfterItems++;else gpuAuraBeforeItems++;
  }
 }
 private AuraSmokeCapture(){}
 public static void reset(){MODELS.clear();PENDING.clear();RECENT.clear();submitted=rendered=vertices=firstPerson=thirdPerson=remote=0;gpuAuraDraws=gpuAuraAfterItems=gpuAuraBeforeItems=0;nativeItemDrawn=false;}
 public static List<Draw> recent(){return List.copyOf(RECENT);}
 public static List<ModelAlignment> models(){return List.copyOf(MODELS);}
 public static OrderedRenderCommandQueue.Custom wrap(SwordAuraSnapshot snapshot,MatrixStack matrices,RenderLayer layer,OrderedRenderCommandQueue.Custom original){
  if(PENDING.size()==128)PENDING.removeFirst();
  PENDING.addLast(new Pending(snapshot,matrices.peek().getPositionMatrix().get(new float[16])));
  submitted++;
  var pipeline=layer.getRenderPipeline();var flags=pipeline.getShaderDefines().flags();
  boolean fullBright=flags.contains("EMISSIVE")&&flags.contains("NO_CARDINAL_LIGHTING")&&flags.contains("NO_OVERLAY")&&!flags.contains("PER_FACE_LIGHTING")&&!pipeline.isCull()&&!pipeline.isWriteDepth();
  return (entry,delegate)->{
   var count=new CountingVertices(delegate);
   original.render(entry,count);
   rendered++;vertices+=count.count;
   boolean local=MinecraftClient.getInstance().player!=null&&MinecraftClient.getInstance().player.getUuid().equals(snapshot.player());
   if(snapshot.context().isFirstPerson())firstPerson++;else thirdPerson++;
   if(!local)remote++;
   if(RECENT.size()==512)RECENT.removeFirst();
   RECENT.addLast(new Draw(snapshot.entityId(),snapshot.hand(),snapshot.context(),local,snapshot.swinging(),fullBright,snapshot.visual().rank(),snapshot.visual().auraColor(),count.count,count.finite,entry.getPositionMatrix().get(new float[16])));
  };
 }
 /** Compare with the matrix and textured quads vanilla actually submits for this same held model. */
 public static void nativeModel(MatrixStack matrices,ItemDisplayContext context,List<BakedQuad> quads){
  if(!Boolean.getBoolean("starsmoke.auras")||PENDING.isEmpty())return;
  var pending=PENDING.peekLast();
  if(pending.snapshot().context()!=context)return;
  PENDING.removeLast();
  float[] actual=matrices.peek().getPositionMatrix().get(new float[16]);float error=0,minZ=100,maxZ=-100;
  for(int i=0;i<16;i++)error=Math.max(error,Math.abs(actual[i]-pending.matrix()[i]));
  int opaque=0,samples=0;
  for(var quad:quads){
   int[] data=quad.vertexData();
   for(int i=0;i<4;i++){float z=Float.intBitsToFloat(data[i*8+2]);minZ=Math.min(minZ,z);maxZ=Math.max(maxZ,z);}
   if(quad.face()!=Direction.NORTH&&quad.face()!=Direction.SOUTH)continue;
   // Sample the aura's diagonal in the actual baked face UVs, including NORTH's mirrored UV mapping.
   for(float along:new float[]{.3f,.5f,.7f,.9f}){
    float x=.24f+.7f*along,y=x;
    float u=interpolate(data,4,x,y),v=interpolate(data,5,x,y);
    var sprite=quad.sprite();var contents=sprite.getContents();
    int pixelX=Math.clamp((int)((u-sprite.getMinU())/(sprite.getMaxU()-sprite.getMinU())*contents.getWidth()),0,contents.getWidth()-1);
    int pixelY=Math.clamp((int)((v-sprite.getMinV())/(sprite.getMaxV()-sprite.getMinV())*contents.getHeight()),0,contents.getHeight()-1);
    if(!contents.isPixelTransparent(0,pixelX,pixelY))opaque++;
    samples++;
   }
  }
  if(MODELS.size()==512)MODELS.removeFirst();
  MODELS.addLast(new ModelAlignment(pending.snapshot().entityId(),context,error,minZ,maxZ,opaque,samples));
 }
 private static float interpolate(int[] data,int offset,float x,float y){
  float x0=Float.intBitsToFloat(data[0]),y0=Float.intBitsToFloat(data[1]),base=Float.intBitsToFloat(data[offset]);
  float dx=0,dy=0;
  for(int i=1;i<4;i++){
   float xx=Float.intBitsToFloat(data[i*8]),yy=Float.intBitsToFloat(data[i*8+1]),value=Float.intBitsToFloat(data[i*8+offset]);
   if(Math.abs(xx-x0)>1e-6f&&Math.abs(yy-y0)<1e-6f)dx=(value-base)/(xx-x0);
   if(Math.abs(yy-y0)>1e-6f&&Math.abs(xx-x0)<1e-6f)dy=(value-base)/(yy-y0);
  }
  return base+dx*(x-x0)+dy*(y-y0);
 }
 private static final class CountingVertices implements VertexConsumer {
  private final VertexConsumer delegate;int count;boolean finite=true;
  CountingVertices(VertexConsumer delegate){this.delegate=delegate;}
  @Override public VertexConsumer vertex(float x,float y,float z){count++;finite&=Float.isFinite(x)&&Float.isFinite(y)&&Float.isFinite(z);delegate.vertex(x,y,z);return this;}
  @Override public VertexConsumer color(int r,int g,int b,int a){delegate.color(r,g,b,a);return this;}
  @Override public VertexConsumer texture(float u,float v){delegate.texture(u,v);return this;}
  @Override public VertexConsumer overlay(int u,int v){delegate.overlay(u,v);return this;}
  @Override public VertexConsumer light(int u,int v){delegate.light(u,v);return this;}
  @Override public VertexConsumer normal(float x,float y,float z){delegate.normal(x,y,z);return this;}
 }
}

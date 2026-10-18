package dk.starswordmaster.client.vfx;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.systems.RenderSystem;
import dk.starswordmaster.StarSwordmaster;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
/** Native unlit material with depth writes: clouds cannot paint over foreground spell solids. */
public final class ConstructRenderLayer {
 private static final Identifier TEXTURE=Identifier.ofVanilla("textures/block/white_concrete.png");
 private static final RenderPipeline PIPELINE=RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
  .withLocation(Identifier.of("starswordmaster","pipeline/elemental_construct"))
  .withShaderDefine("NO_CARDINAL_LIGHTING").withShaderDefine("NO_OVERLAY")
  .withBlend(BlendFunction.TRANSLUCENT).withCull(false).withDepthWrite(true).build();
 private static final RenderLayer LAYER=RenderLayer.of("starswordmaster_elemental_construct",1536,false,true,PIPELINE,
  RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(TEXTURE,false)).build(false));
 private static RenderLayer active;
 private ConstructRenderLayer(){}
 public static RenderLayer get(){
  if(active!=null)return active;
  try{if(RenderSystem.getDevice().precompilePipeline(PIPELINE).isValid())return active=LAYER;}
  catch(RuntimeException unavailable){StarSwordmaster.LOG.warn("Construct shader unavailable; using native emissive fallback",unavailable);}
  return active=RenderLayer.getEntityTranslucentEmissive(TEXTURE);
 }
}

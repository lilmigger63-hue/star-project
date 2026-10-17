package dk.starswordmaster.client.aura;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import dk.starswordmaster.StarSwordmaster;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;

/** Native entity shader with true unlit color, depth testing and two-sided translucent geometry. */
public final class AuraRenderLayer {
 private static final Identifier TEXTURE=Identifier.ofVanilla("textures/block/white_concrete.png");
 // Vanilla's entity-translucent-emissive pipeline still applies PER_FACE_LIGHTING. Aura color must not.
 private static final RenderPipeline PIPELINE=RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
  .withLocation(Identifier.of("starswordmaster","pipeline/sword_aura"))
  .withShaderDefine("NO_CARDINAL_LIGHTING").withShaderDefine("NO_OVERLAY")
  .withBlend(BlendFunction.TRANSLUCENT).withCull(false).withDepthWrite(false).build();
 private static final RenderLayer LAYER=RenderLayer.of("starswordmaster_sword_aura",1536,false,true,PIPELINE,
  RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(TEXTURE,false)).build(false));
 private static RenderLayer active;
 private AuraRenderLayer(){}
 public static boolean isAura(RenderLayer layer){return active!=null&&layer==active;}
 public static RenderLayer get(){
  if(active!=null)return active;
  try{
   if(RenderSystem.getDevice().precompilePipeline(PIPELINE).isValid())return active=LAYER;
  }catch(RuntimeException unavailable){StarSwordmaster.LOG.warn("Full-bright sword aura shader unavailable; using native emissive fallback",unavailable);}
  return active=RenderLayer.getEntityTranslucentEmissive(TEXTURE);
 }
}

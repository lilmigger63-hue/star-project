package dk.starswordmaster.client.circles;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;

/** A single shared atlas/buffer; depth-tested, emissive, two-sided and without depth writes. */
public final class CircleRenderLayer {
 private static final RenderPipeline PIPELINE=RenderPipeline.builder(RenderPipelines.ENTITY_EMISSIVE_SNIPPET)
  .withLocation(Identifier.of("starswordmaster","pipeline/magic_circle"))
  .withShaderDefine("NO_CARDINAL_LIGHTING").withShaderDefine("NO_OVERLAY")
  .withBlend(BlendFunction.TRANSLUCENT).withCull(false).withDepthWrite(false).build();
 private static final RenderLayer LAYER=RenderLayer.of("starswordmaster_magic_circle",6144,false,true,PIPELINE,
  RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(Identifier.of("starswordmaster","textures/effect/magic_circles.png"),false)).build(false));
 private CircleRenderLayer(){}
 public static RenderLayer get(){return LAYER;}
 public static boolean isCircle(RenderLayer layer){return layer==LAYER;}
}

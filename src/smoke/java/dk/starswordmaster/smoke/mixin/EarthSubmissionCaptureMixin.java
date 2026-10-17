package dk.starswordmaster.smoke.mixin;

import dk.starswordmaster.client.EarthFormationRenderer;
import dk.starswordmaster.client.EarthSmokeCapture;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EarthFormationRenderer.class)
abstract class EarthSubmissionCaptureMixin {
 @Inject(method="draw",at=@At("HEAD"))
 private static void starsmoke$begin(WorldRenderContext context,CallbackInfo ci){EarthSmokeCapture.begin(context);}
 @Redirect(method="draw",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
 private static VertexConsumer starsmoke$actualVertices(VertexConsumerProvider provider,RenderLayer layer){return EarthSmokeCapture.wrap(provider.getBuffer(layer));}
 @Inject(method="draw",at=@At("RETURN"))
 private static void starsmoke$end(WorldRenderContext context,CallbackInfo ci){EarthSmokeCapture.end();}
}

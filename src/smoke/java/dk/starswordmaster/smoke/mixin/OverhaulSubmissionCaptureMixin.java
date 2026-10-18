package dk.starswordmaster.smoke.mixin;

import dk.starswordmaster.client.CinematicVfxRenderer;
import dk.starswordmaster.client.OverhaulSmokeCapture;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CinematicVfxRenderer.class)
abstract class OverhaulSubmissionCaptureMixin {
 @Inject(method="draw",at=@At("HEAD"))
 private static void starsmoke$begin(WorldRenderContext context,CallbackInfo ci){OverhaulSmokeCapture.begin(context);}
 @Redirect(method="draw",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
 private static VertexConsumer starsmoke$vertices(VertexConsumerProvider provider,RenderLayer layer){return OverhaulSmokeCapture.wrap(provider.getBuffer(layer));}
 @Inject(method="emit",at=@At("HEAD"))
 private static void starsmoke$draw(@Coerce Object draw,net.minecraft.client.util.math.MatrixStack matrices,VertexConsumer vertices,CallbackInfo ci){OverhaulSmokeCapture.select(draw);}
 @Inject(method="draw",at=@At("RETURN"))
 private static void starsmoke$end(WorldRenderContext context,CallbackInfo ci){OverhaulSmokeCapture.end();}
}

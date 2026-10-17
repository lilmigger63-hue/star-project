package dk.starswordmaster.smoke.mixin;

import dk.starswordmaster.client.AuraSmokeCapture;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLayer.MultiPhase.class)
abstract class AuraGpuLayerCaptureMixin {
 @Inject(method="draw",at=@At("HEAD"))
 private void starsmoke$actualLayerOrder(BuiltBuffer buffer,CallbackInfo ci){
  AuraSmokeCapture.gpuLayer((RenderLayer)(Object)this);
 }
}

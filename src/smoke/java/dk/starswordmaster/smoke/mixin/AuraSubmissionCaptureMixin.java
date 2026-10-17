package dk.starswordmaster.smoke.mixin;

import dk.starswordmaster.client.AuraSmokeCapture;
import dk.starswordmaster.client.SwordAuraRenderer;
import dk.starswordmaster.client.aura.SwordAuraSnapshot;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=SwordAuraRenderer.class,remap=false)
abstract class AuraSubmissionCaptureMixin {
 @Inject(method="beginFrame",at=@At("HEAD"))
 private static void starsmoke$beginGpuFrame(CallbackInfo ci){AuraSmokeCapture.beginFrame();}
 @Redirect(method="submit",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitCustom(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$Custom;)V",remap=true))
 private static void starsmoke$capture(OrderedRenderCommandQueue target,MatrixStack submittedMatrices,RenderLayer layer,OrderedRenderCommandQueue.Custom custom,SwordAuraSnapshot snapshot,MatrixStack matrices,OrderedRenderCommandQueue queue){
  target.submitCustom(submittedMatrices,layer,Boolean.getBoolean("starsmoke.auras")?AuraSmokeCapture.wrap(snapshot,submittedMatrices,layer,custom):custom);
 }
}

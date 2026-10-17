package dk.starswordmaster.smoke.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dk.starswordmaster.client.CircleSmokeCapture;
import dk.starswordmaster.client.MagicCircleRenderer;
import dk.starswordmaster.client.circles.CircleSnapshot;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagicCircleRenderer.class)
abstract class CircleSubmissionCaptureMixin {
 @Inject(method="submitAtPalm",at=@At("HEAD"))
 private static void starsmoke$nativePalmExtraction(CircleSnapshot snapshot,ModelPart arm,MatrixStack matrices,OrderedRenderCommandQueue queue,CallbackInfo ci){CircleSmokeCapture.extracted(snapshot,arm,matrices);}
 @WrapOperation(method="submitAtPalm",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitCustom(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$Custom;)V"))
 private static void starsmoke$nativePalmDraw(OrderedRenderCommandQueue queue,MatrixStack matrices,RenderLayer layer,OrderedRenderCommandQueue.Custom custom,Operation<Void> original,CircleSnapshot snapshot,ModelPart arm,MatrixStack source,OrderedRenderCommandQueue sourceQueue){
  original.call(queue,matrices,layer,CircleSmokeCapture.wrap(snapshot,custom));
 }
}

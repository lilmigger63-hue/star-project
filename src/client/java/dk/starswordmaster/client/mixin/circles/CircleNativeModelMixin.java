package dk.starswordmaster.client.mixin.circles;

import dk.starswordmaster.client.MagicCircleRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
abstract class CircleNativeModelMixin {
 @Shadow protected EntityModel<?> model;
 // PAL suppresses ordinary FeatureRenderers in first person. Extract directly after native pose setup.
 @Inject(method="render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Ljava/lang/Object;)V",shift=At.Shift.AFTER))
 private void starswordmaster$nativeWrists(LivingEntityRenderState state,MatrixStack matrices,OrderedRenderCommandQueue queue,CameraRenderState camera,CallbackInfo ci){
  if(state instanceof PlayerEntityRenderState player&&model instanceof PlayerEntityModel nativeModel)MagicCircleRenderer.nativeModel(player,nativeModel,matrices,queue);
 }
}

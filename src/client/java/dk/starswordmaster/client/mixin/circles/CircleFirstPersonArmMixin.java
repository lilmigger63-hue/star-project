package dk.starswordmaster.client.mixin.circles;

import dk.starswordmaster.client.MagicCircleRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
abstract class CircleFirstPersonArmMixin {
 @Inject(method="renderArm",at=@At("TAIL"))
 private void starswordmaster$nativeFirstPersonWrist(MatrixStack matrices,OrderedRenderCommandQueue queue,int light,Identifier skin,ModelPart arm,boolean sleeve,CallbackInfo ci){
  MagicCircleRenderer.firstPersonArm(((PlayerEntityRenderer<?>)(Object)this).getModel(),arm,matrices,queue);
 }
}

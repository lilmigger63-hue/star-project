package dk.starswordmaster.smoke.mixin;

import dk.starswordmaster.client.CircleSmokeCapture;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
abstract class CircleNativeArmCaptureMixin {
 @Inject(method="renderCuboids",at=@At("HEAD"))
 private void starsmoke$actualAnimatedArm(MatrixStack.Entry entry,VertexConsumer vertices,int light,int overlay,int color,CallbackInfo ci){CircleSmokeCapture.nativeCuboids((ModelPart)(Object)this,entry);}
}

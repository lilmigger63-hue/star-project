package dk.starswordmaster.smoke.mixin;
import dk.starswordmaster.client.CircleSmokeCapture;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(GameRenderer.class)
abstract class CircleSmokeFrameMixin {
 @Inject(method="render",at=@At("TAIL"))
 private void starsmoke$finishNativeCircleFrame(CallbackInfo ci){CircleSmokeCapture.endFrame();}
}

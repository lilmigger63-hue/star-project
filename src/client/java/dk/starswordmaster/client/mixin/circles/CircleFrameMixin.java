package dk.starswordmaster.client.mixin.circles;
import dk.starswordmaster.client.MagicCircleRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(GameRenderer.class)
abstract class CircleFrameMixin {
 @Inject(method="render",at=@At("HEAD"))
 private void starswordmaster$circleFrame(CallbackInfo ci){MagicCircleRenderer.beginFrame();}
}

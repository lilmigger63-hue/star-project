package dk.starswordmaster.smoke.mixin;
import dk.starswordmaster.client.*;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=SkillTreeScreen.class,remap=false)
abstract class TreePerfRenderMixin {
 @Inject(method="render",at=@At("HEAD")) private void begin(DrawContext c,int x,int y,float delta,CallbackInfo ci){TreePerfCapture.begin();}
 @Inject(method="render",at=@At("RETURN")) private void end(DrawContext c,int x,int y,float delta,CallbackInfo ci){TreePerfCapture.end();}
}

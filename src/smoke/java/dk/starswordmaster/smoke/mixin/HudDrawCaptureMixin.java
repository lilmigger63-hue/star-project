package dk.starswordmaster.smoke.mixin;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dk.starswordmaster.client.HudSmokeCapture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(DrawContext.class)
abstract class HudDrawCaptureMixin {
 @Inject(method="drawTextWithBackground",at=@At("HEAD"))
 private void text(net.minecraft.client.font.TextRenderer renderer,net.minecraft.text.Text text,int x,int y,int width,int color,CallbackInfo ci){if(Boolean.getBoolean("starsmoke.hud"))HudSmokeCapture.text((DrawContext)(Object)this,x,y,width);}
 @Inject(method="drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V",at=@At("HEAD"))
 private void capture(RenderPipeline pipeline,Identifier texture,int x,int y,int width,int height,CallbackInfo ci){HudSmokeCapture.capture((DrawContext)(Object)this,texture,x,y,width,height);}
}

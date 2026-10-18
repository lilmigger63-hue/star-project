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
 @Inject(method="fill(IIIII)V",at=@At("HEAD"))
 private void fill(int x1,int y1,int x2,int y2,int color,CallbackInfo ci){dk.starswordmaster.client.TreePerfCapture.fill();HudSmokeCapture.fill((DrawContext)(Object)this,x1,y1,x2,y2,color);}
 @Inject(method="drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIIII)V",at=@At("HEAD"))
 private void icon(RenderPipeline pipeline,Identifier texture,int x,int y,float u,float v,int width,int height,int regionWidth,int regionHeight,int textureWidth,int textureHeight,CallbackInfo ci){HudSmokeCapture.capture((DrawContext)(Object)this,texture,x,y,width,height);}
 @Inject(method="drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V",at=@At("HEAD"))
 private void experience(net.minecraft.client.font.TextRenderer renderer,net.minecraft.text.Text text,int x,int y,int color,boolean shadow,CallbackInfo ci){if(Boolean.getBoolean("starsmoke.hud")&&color==0xFF80FF20)HudSmokeCapture.experience((DrawContext)(Object)this,x,y,renderer.getWidth(text));}
}

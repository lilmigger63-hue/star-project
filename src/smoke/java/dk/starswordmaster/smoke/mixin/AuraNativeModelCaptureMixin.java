package dk.starswordmaster.smoke.mixin;

import dk.starswordmaster.client.AuraSmokeCapture;
import java.util.List;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OrderedRenderCommandQueueImpl.class)
abstract class AuraNativeModelCaptureMixin {
 @Inject(method="submitItem",at=@At("HEAD"))
 private void starsmoke$nativeBlade(MatrixStack matrices,ItemDisplayContext context,int light,int overlay,int outline,int[] tints,List<BakedQuad> quads,RenderLayer layer,ItemRenderState.Glint glint,CallbackInfo ci){
  AuraSmokeCapture.nativeModel(matrices,context,quads);
 }
}

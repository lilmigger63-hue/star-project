package dk.starswordmaster.client.mixin.aura;

import dk.starswordmaster.client.SwordAuraRenderer;
import dk.starswordmaster.client.aura.AuraItemState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderState.LayerRenderState.class)
abstract class AuraLayerRenderStateMixin {
 @Shadow @Final private ItemRenderState field_55345;
 @Inject(method="render",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/model/json/Transformation;apply(ZLnet/minecraft/client/util/math/MatrixStack$Entry;)V",shift=At.Shift.AFTER))
 private void starswordmaster$renderAura(MatrixStack matrices,OrderedRenderCommandQueue queue,int light,int overlay,int outline,CallbackInfo ci){
  SwordAuraRenderer.submit(((AuraItemState)field_55345).starswordmaster$takeAura(),matrices,queue);
 }
}

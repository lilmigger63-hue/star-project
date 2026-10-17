package dk.starswordmaster.client.mixin.aura;

import dk.starswordmaster.client.SwordAuraRenderer;
import dk.starswordmaster.client.aura.AuraItemState;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelManager.class)
abstract class AuraItemModelManagerMixin {
 @Inject(method="clearAndUpdate",at=@At("TAIL"))
 private void starswordmaster$capture(ItemRenderState state,ItemStack stack,ItemDisplayContext context,World world,HeldItemContext owner,int seed,CallbackInfo ci){
  ((AuraItemState)state).starswordmaster$setAura(SwordAuraRenderer.capture(stack,context,owner));
 }
}

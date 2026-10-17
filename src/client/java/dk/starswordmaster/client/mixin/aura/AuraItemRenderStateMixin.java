package dk.starswordmaster.client.mixin.aura;

import dk.starswordmaster.client.aura.AuraItemState;
import dk.starswordmaster.client.aura.SwordAuraSnapshot;
import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderState.class)
abstract class AuraItemRenderStateMixin implements AuraItemState {
 @Unique private SwordAuraSnapshot starswordmaster$aura;
 @Unique private boolean starswordmaster$auraConsumed;
 @Override public void starswordmaster$setAura(SwordAuraSnapshot snapshot){starswordmaster$aura=snapshot;}
 @Override public SwordAuraSnapshot starswordmaster$takeAura(){
  if(starswordmaster$auraConsumed)return null;
  starswordmaster$auraConsumed=true;return starswordmaster$aura;
 }
 @Inject(method="clear",at=@At("HEAD"))
 private void starswordmaster$clearAura(CallbackInfo ci){starswordmaster$aura=null;starswordmaster$auraConsumed=false;}
 @Inject(method="render",at=@At("HEAD"))
 private void starswordmaster$beginAura(CallbackInfo ci){starswordmaster$auraConsumed=false;}
}

package dk.starswordmaster.client.mixin.aura;

import dk.starswordmaster.client.SwordAuraRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class AuraFrameMixin {
 @Inject(method="render",at=@At("HEAD"))
 private void starswordmaster$beginFrame(CallbackInfo ci){SwordAuraRenderer.beginFrame();}
}

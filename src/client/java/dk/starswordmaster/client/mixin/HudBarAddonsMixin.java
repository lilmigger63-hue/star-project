package dk.starswordmaster.client.mixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dk.starswordmaster.client.SkillHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
/** Fabric exposes the info bar, but 1.21.10 renders its locator markers separately. */
@Mixin(InGameHud.class)
abstract class HudBarAddonsMixin {
 @WrapOperation(method="renderMainHud",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/hud/bar/Bar;renderAddons(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"))
 private void starswordmaster$liftBarAddons(Bar bar,DrawContext context,RenderTickCounter tick,Operation<Void> original){
  SkillHud.renderVanillaIndicator(context,()->original.call(bar,context,tick));
 }
}

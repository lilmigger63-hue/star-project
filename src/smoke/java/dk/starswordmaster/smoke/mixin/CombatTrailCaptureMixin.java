package dk.starswordmaster.smoke.mixin;
import dk.starswordmaster.client.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(value=NativeSwordTrails.class,remap=false)
abstract class CombatTrailCaptureMixin {
 @Redirect(method="submit",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitCustom(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue$Custom;)V",remap=true))
 private static void capture(OrderedRenderCommandQueue queue,MatrixStack matrices,RenderLayer layer,OrderedRenderCommandQueue.Custom custom,NativeSwordTrails.Batch batch,OrderedRenderCommandQueue original,int limit){queue.submitCustom(matrices,layer,Boolean.getBoolean("starsmoke.bdo")?CombatSmokeCapture.wrap(batch,layer,custom):custom);}
}

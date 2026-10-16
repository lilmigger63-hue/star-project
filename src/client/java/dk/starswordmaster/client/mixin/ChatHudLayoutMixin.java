package dk.starswordmaster.client.mixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dk.starswordmaster.client.SkillHud;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
/** Move chat's baseline and matching mouse coordinates together; the native input field stays unchanged. */
@Mixin(ChatHud.class)
abstract class ChatHudLayoutMixin {
 @Shadow public abstract double getChatScale();
 @ModifyExpressionValue(method="render",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/DrawContext;getScaledWindowHeight()I"))
 private int starswordmaster$renderHeight(int height){return height-SkillHud.chatLift();}
 @ModifyExpressionValue(method={"toChatLineY","mouseClicked"},at=@At(value="INVOKE",target="Lnet/minecraft/client/util/Window;getScaledHeight()I"))
 private int starswordmaster$mouseHeight(int height){return height-SkillHud.chatLift();}
 @ModifyReturnValue(method="getHeight()I",at=@At("RETURN"))
 private int starswordmaster$visibleHeight(int height){return SkillHud.chatHeight(height,getChatScale());}
}

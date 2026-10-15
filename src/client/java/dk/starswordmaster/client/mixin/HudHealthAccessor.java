package dk.starswordmaster.client.mixin;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
/** Read vanilla's short-lived displayed health so item labels clear its actual armor rows. */
@Mixin(InGameHud.class)
public interface HudHealthAccessor {
 @Accessor("renderHealthValue") int starswordmaster$getRenderHealthValue();
}

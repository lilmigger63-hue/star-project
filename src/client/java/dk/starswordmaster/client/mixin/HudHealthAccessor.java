package dk.starswordmaster.client.mixin;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
/** Read native displayed health and active info bar without altering either. */
@Mixin(InGameHud.class)
public interface HudHealthAccessor {
 @Accessor("renderHealthValue") int starswordmaster$getRenderHealthValue();
 @Accessor("currentBar") org.apache.commons.lang3.tuple.Pair<?,?> starswordmaster$getCurrentBar();
}

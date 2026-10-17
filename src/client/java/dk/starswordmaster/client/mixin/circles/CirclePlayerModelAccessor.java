package dk.starswordmaster.client.mixin.circles;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(PlayerEntityModel.class)
public interface CirclePlayerModelAccessor {
 @Accessor("thinArms") boolean starswordmaster$thinArms();
}

package dk.starswordmaster.client.mixin.circles;
import dk.starswordmaster.client.circles.CircleRenderLayer;
import java.util.SequencedMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(VertexConsumerProvider.Immediate.class)
abstract class CircleBufferedLayerMixin {
 @Shadow @Final protected SequencedMap<RenderLayer,BufferAllocator> layerBuffers;
 @Inject(method="getBuffer",at=@At("HEAD"))
 private void starswordmaster$ownedCircleBuffer(RenderLayer layer,CallbackInfoReturnable<?> cir){
  if((Object)this==MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers()&&CircleRenderLayer.isCircle(layer)&&!layerBuffers.containsKey(layer))layerBuffers.put(layer,new BufferAllocator(layer.getExpectedBufferSize()));
 }
}

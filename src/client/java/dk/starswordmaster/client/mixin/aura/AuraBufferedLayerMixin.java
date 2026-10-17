package dk.starswordmaster.client.mixin.aura;

import dk.starswordmaster.client.aura.AuraRenderLayer;
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

/** Append translucent aura after vanilla's fixed opaque item buffers instead of its early fallback buffer. */
@Mixin(VertexConsumerProvider.Immediate.class)
abstract class AuraBufferedLayerMixin {
 @Shadow @Final protected SequencedMap<RenderLayer,BufferAllocator> layerBuffers;
 @Inject(method="getBuffer",at=@At("HEAD"))
 private void starswordmaster$lateAuraBuffer(RenderLayer layer,CallbackInfoReturnable<?> cir){
  if((Object)this!=MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers())return;
  if(AuraRenderLayer.isAura(layer)&&!layerBuffers.containsKey(layer)){
   layerBuffers.put(layer,new BufferAllocator(layer.getExpectedBufferSize()));
  }
 }
}

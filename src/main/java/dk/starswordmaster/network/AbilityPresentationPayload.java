package dk.starswordmaster.network;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import java.util.UUID;
/** Empty ability means cancel. Ephemeral, server-to-client only. */
public record AbilityPresentationPayload(UUID caster,int entityId,long sequence,long sentAt,String ability,Vec3d origin,Vec3d impact) implements CustomPayload {
 public static final Id<AbilityPresentationPayload> ID=new Id<>(Identifier.of("starswordmaster","ability_presentation"));
 public static final PacketCodec<RegistryByteBuf,AbilityPresentationPayload> CODEC=new PacketCodec<>(){
  public AbilityPresentationPayload decode(RegistryByteBuf b){return new AbilityPresentationPayload(b.readUuid(),b.readVarInt(),b.readVarLong(),b.readVarLong(),b.readString(64),new Vec3d(b.readDouble(),b.readDouble(),b.readDouble()),new Vec3d(b.readDouble(),b.readDouble(),b.readDouble()));}
  public void encode(RegistryByteBuf b,AbilityPresentationPayload p){b.writeUuid(p.caster());b.writeVarInt(p.entityId());b.writeVarLong(p.sequence());b.writeVarLong(p.sentAt());b.writeString(p.ability(),64);b.writeDouble(p.origin().x);b.writeDouble(p.origin().y);b.writeDouble(p.origin().z);b.writeDouble(p.impact().x);b.writeDouble(p.impact().y);b.writeDouble(p.impact().z);}
 };
 public Id<? extends CustomPayload> getId(){return ID;}
}

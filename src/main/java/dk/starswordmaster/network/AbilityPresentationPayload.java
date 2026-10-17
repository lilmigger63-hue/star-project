package dk.starswordmaster.network;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import java.util.UUID;
import java.util.List;
import dk.starswordmaster.presentation.VisualState;
/** Empty ability means cancel. Ephemeral, server-to-client only. */
public record AbilityPresentationPayload(UUID caster,int entityId,long sequence,long sentAt,String ability,Vec3d origin,Vec3d impact,VisualState visual,List<Vec3d> impacts) implements CustomPayload {
 public AbilityPresentationPayload(UUID caster,int entityId,long sequence,long sentAt,String ability,Vec3d origin,Vec3d impact){this(caster,entityId,sequence,sentAt,ability,origin,impact,VisualState.DEFAULT,List.of());}
 public AbilityPresentationPayload {impacts=List.copyOf(impacts);if(impacts.size()>8)throw new IllegalArgumentException("Too many visual impacts");}
 public static final Id<AbilityPresentationPayload> ID=new Id<>(Identifier.of("starswordmaster","ability_presentation_v2"));
 public static final PacketCodec<RegistryByteBuf,AbilityPresentationPayload> CODEC=new PacketCodec<>(){
  public AbilityPresentationPayload decode(RegistryByteBuf b){var caster=b.readUuid();int entity=b.readVarInt();long seq=b.readVarLong(),at=b.readVarLong();String ability=b.readString(64);var origin=readPosition(b);var impact=readPosition(b);var visual=VisualStatePayload.readState(b);int count=b.readUnsignedByte();if(count>8)throw new IllegalArgumentException("Too many visual impacts");var positions=new java.util.ArrayList<Vec3d>(count);for(int i=0;i<count;i++)positions.add(readPosition(b));return new AbilityPresentationPayload(caster,entity,seq,at,ability,origin,impact,visual,positions);}
  public void encode(RegistryByteBuf b,AbilityPresentationPayload p){b.writeUuid(p.caster());b.writeVarInt(p.entityId());b.writeVarLong(p.sequence());b.writeVarLong(p.sentAt());b.writeString(p.ability(),64);writePosition(b,p.origin());writePosition(b,p.impact());VisualStatePayload.writeState(b,p.visual());b.writeByte(p.impacts().size());for(var point:p.impacts())writePosition(b,point);}
 };
 private static Vec3d readPosition(RegistryByteBuf b){return new Vec3d(b.readDouble(),b.readDouble(),b.readDouble());}
 private static void writePosition(RegistryByteBuf b,Vec3d p){b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);}
 public Id<? extends CustomPayload> getId(){return ID;}
}

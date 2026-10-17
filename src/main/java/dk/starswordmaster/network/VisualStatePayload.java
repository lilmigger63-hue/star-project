package dk.starswordmaster.network;
import dk.starswordmaster.presentation.*;
import dk.starswordmaster.progression.PlayerProgress;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.UUID;
/** Bounded S2C appearance state, sent on changes and when a player starts tracking. */
public record VisualStatePayload(UUID player,int entityId,VisualState state,boolean removed) implements CustomPayload {
 public static final Id<VisualStatePayload> ID=new Id<>(Identifier.of("starswordmaster","visual_state_v1"));
 public static void writeState(RegistryByteBuf b,VisualState s){b.writeBoolean(s.auraUnlocked());b.writeByte(s.rank());b.writeByte(s.swordLevel());b.writeFloat(s.power());b.writeByte(s.playerClass().ordinal());b.writeInt(s.auraColor());b.writeByte(s.element().ordinal());}
 public static VisualState readState(RegistryByteBuf b){
  boolean aura=b.readBoolean();int rank=b.readUnsignedByte(),sword=b.readUnsignedByte();float power=b.readFloat();int cls=b.readUnsignedByte(),color=b.readInt(),element=b.readUnsignedByte();
  var classes=PlayerProgress.PlayerClass.values();var elements=AbilityPresentation.Element.values();
  return new VisualState(aura,rank,sword,power,cls<classes.length?classes[cls]:PlayerProgress.PlayerClass.UNINITIATED,color,element<elements.length?elements[element]:AbilityPresentation.Element.STAR);
 }
 public static final PacketCodec<RegistryByteBuf,VisualStatePayload> CODEC=new PacketCodec<>(){
  public VisualStatePayload decode(RegistryByteBuf b){var id=b.readUuid();int entity=b.readVarInt();boolean removed=b.readBoolean();return new VisualStatePayload(id,entity,removed?VisualState.DEFAULT:readState(b),removed);}
  public void encode(RegistryByteBuf b,VisualStatePayload p){b.writeUuid(p.player());b.writeVarInt(p.entityId());b.writeBoolean(p.removed());if(!p.removed())writeState(b,p.state());}
 };
 public Id<? extends CustomPayload> getId(){return ID;}
}

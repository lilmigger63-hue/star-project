package dk.starswordmaster.network;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
public record ActionPayload(String action,String target,int slot) implements CustomPayload {
 public static final Id<ActionPayload> ID=new Id<>(Identifier.of("starswordmaster","action"));
 public static final PacketCodec<RegistryByteBuf,ActionPayload> CODEC=PacketCodec.tuple(PacketCodecs.STRING,ActionPayload::action,PacketCodecs.STRING,ActionPayload::target,PacketCodecs.VAR_INT,ActionPayload::slot,ActionPayload::new);
 public Id<? extends CustomPayload> getId(){return ID;}
}

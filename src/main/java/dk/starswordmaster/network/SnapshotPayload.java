package dk.starswordmaster.network;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
public record SnapshotPayload(String data) implements CustomPayload {
 public static final Id<SnapshotPayload> ID=new Id<>(Identifier.of("starswordmaster","snapshot"));
 public static final PacketCodec<RegistryByteBuf,SnapshotPayload> CODEC=PacketCodec.tuple(PacketCodecs.STRING,SnapshotPayload::data,SnapshotPayload::new);
 public Id<? extends CustomPayload> getId(){return ID;}
}

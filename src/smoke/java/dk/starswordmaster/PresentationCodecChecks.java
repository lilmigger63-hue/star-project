package dk.starswordmaster;
import dk.starswordmaster.network.*;
import dk.starswordmaster.presentation.*;
import dk.starswordmaster.progression.PlayerProgress;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import java.util.*;
/** Native registry-backed wire checks; excluded from production jar. */
public final class PresentationCodecChecks {
 private PresentationCodecChecks(){}
 private static void check(boolean value,String name){if(!value)throw new AssertionError("PRESENTATION_CODEC FAIL: "+name);System.out.println("PRESENTATION_CODEC PASS: "+name);}
 public static void run(ServerPlayerEntity player){run(player.getEntityWorld(),player.getUuid(),player.getId(),ServerVisualState.state(player),player.getEntityPos());}
 public static void run(net.minecraft.server.world.ServerWorld world){run(world,new UUID(123,456),7,VisualState.DEFAULT,new Vec3d(.5,80,.5));}
 private static void run(net.minecraft.server.world.ServerWorld world,UUID caster,int entityId,VisualState state,Vec3d origin){
  var buffer=new RegistryByteBuf(Unpooled.buffer(),world.getRegistryManager());
  try{
   var points=new ArrayList<Vec3d>();for(int i=0;i<8;i++)points.add(origin.add(i*.5,1,2));
   var event=new AbilityPresentationPayload(caster,entityId,123,world.getTime(),"chain_lightning",origin,points.getFirst(),state,points);
   AbilityPresentationPayload.CODEC.encode(buffer,event);check(buffer.readableBytes()<=384,"largest eight-impact event is bounded to384bytes");check(event.equals(AbilityPresentationPayload.CODEC.decode(buffer)),"authoritative color/power/impact paths roundtrip exactly");buffer.clear();
   var appearance=new VisualStatePayload(caster,entityId,state,false);VisualStatePayload.CODEC.encode(buffer,appearance);check(buffer.readableBytes()<=40,"appearance updates stay compact");check(appearance.equals(VisualStatePayload.CODEC.decode(buffer)),"tracked-player appearance roundtrip");buffer.clear();
   var empty=new AbilityPresentationPayload(caster,entityId,124,world.getTime(),"spark",origin,origin,state,List.of(),entityId);check(empty.targetId()==entityId,"confirmed target identity retained");AbilityPresentationPayload.CODEC.encode(buffer,empty);check(empty.equals(AbilityPresentationPayload.CODEC.decode(buffer)),"confirmed target identity roundtrips on version3 wire");buffer.clear();AbilityPresentationPayload.CODEC.encode(buffer,empty);buffer.setByte(buffer.writerIndex()-1,9);boolean rejected=false;try{AbilityPresentationPayload.CODEC.decode(buffer);}catch(IllegalArgumentException expected){rejected=true;}check(rejected,"oversized network impact lists rejected before allocation");buffer.clear();
   buffer.writeBoolean(true);buffer.writeByte(255);buffer.writeByte(255);buffer.writeFloat(Float.NaN);buffer.writeByte(255);buffer.writeInt(-1);buffer.writeByte(255);var sanitized=VisualStatePayload.readState(buffer);check(sanitized.rank()==4&&sanitized.swordLevel()==100&&sanitized.power()==0&&sanitized.playerClass()==PlayerProgress.PlayerClass.UNINITIATED&&sanitized.element()==AbilityPresentation.Element.STAR,"invalid compact visual values are finite/safelyclamped");
  }finally{buffer.release();}
 }
}

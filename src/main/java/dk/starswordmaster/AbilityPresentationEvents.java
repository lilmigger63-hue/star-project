package dk.starswordmaster;
import dk.starswordmaster.network.AbilityPresentationPayload;
import dk.starswordmaster.presentation.AbilityPresentation;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import java.util.*;
/** Server-confirmed presentation events. Never applies a gameplay effect. */
public final class AbilityPresentationEvents {
 private record Active(long until,ItemStack weapon){}
 private static final Map<UUID,Long> sequences=new HashMap<>();
 private static final Map<UUID,Active> active=new HashMap<>();
 private AbilityPresentationEvents(){}
 public static void start(ServerPlayerEntity player,String ability,Vec3d impact){
  var p=AbilityPresentation.ALL.get(ability);if(p==null)return;
  active.put(player.getUuid(),new Active(player.getEntityWorld().getTime()+p.ticks(),player.getMainHandStack().copy()));
  send(player,ability,impact);
 }
 public static void cancel(ServerPlayerEntity player){if(active.remove(player.getUuid())!=null)send(player,"",player.getEntityPos());}
 public static void tick(ServerPlayerEntity player){
  var p=active.get(player.getUuid());if(p==null)return;
  if(!player.isAlive()||!ItemStack.areItemsAndComponentsEqual(p.weapon(),player.getMainHandStack()))cancel(player);
  else if(player.getEntityWorld().getTime()>=p.until())active.remove(player.getUuid());
 }
 private static void send(ServerPlayerEntity player,String ability,Vec3d impact){
  var event=new AbilityPresentationPayload(player.getUuid(),player.getId(),sequences.merge(player.getUuid(),1L,Long::sum),player.getEntityWorld().getTime(),ability,player.getEntityPos(),impact);
  var recipients=new HashSet<>(PlayerLookup.tracking(player));recipients.add(player);
  for(var recipient:recipients)if(ServerPlayNetworking.canSend(recipient,AbilityPresentationPayload.ID))ServerPlayNetworking.send(recipient,event);
 }
 public static void forget(UUID player){active.remove(player);/* Keep sequence across reconnect in this server session. */}
 public static void clear(){active.clear();sequences.clear();}
}

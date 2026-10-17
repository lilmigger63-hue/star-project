package dk.starswordmaster.client;
import dk.starswordmaster.presentation.VisualState;
import java.util.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import dk.starswordmaster.network.VisualStatePayload;
/** Authoritative identity/state cache, bounded independently of loaded chunks. */
public final class ClientVisualState {
 private static final Map<UUID,VisualState> states=new LinkedHashMap<>();
 private static ClientWorld world;
 private ClientVisualState(){}
 public static VisualState get(UUID id){return states.getOrDefault(id,VisualState.DEFAULT);}
 public static void put(UUID id,VisualState state){observeWorld(MinecraftClient.getInstance().world);if(states.size()>=256&&!states.containsKey(id))states.remove(states.keySet().iterator().next());states.put(id,state);}
 public static void remove(UUID id){states.remove(id);}
 public static void clear(){states.clear();world=null;}
 private static void observeWorld(ClientWorld next){if(world!=next){clear();world=next;}}
 public static void register(){
  ClientPlayNetworking.registerGlobalReceiver(VisualStatePayload.ID,(event,context)->context.client().execute(()->{if(context.client().world==null)return;if(event.removed())remove(event.player());else put(event.player(),event.state());}));
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->clear());
  ClientTickEvents.START_CLIENT_TICK.register(client->observeWorld(client.world));
 }
}

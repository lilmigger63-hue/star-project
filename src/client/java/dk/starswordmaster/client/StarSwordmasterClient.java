package dk.starswordmaster.client;
import dk.starswordmaster.network.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import java.io.*;
public final class StarSwordmasterClient implements ClientModInitializer {
 public static PlayerProgress data=new PlayerProgress();
 public static boolean ready;
 public static final KeyBinding[] keys=new KeyBinding[5];
 private static KeyBinding menu,status;
 public static void send(String action,String target,int slot){if(ready&&ClientPlayNetworking.canSend(ActionPayload.ID))ClientPlayNetworking.send(new ActionPayload(action,target,slot));}
 @Override public void onInitializeClient(){
  var category=KeyBinding.Category.create(Identifier.of("starswordmaster","skills"));
  int[] codes={GLFW.GLFW_KEY_Z,GLFW.GLFW_KEY_X,GLFW.GLFW_KEY_C,GLFW.GLFW_KEY_V,GLFW.GLFW_KEY_B};
  for(int i=0;i<5;i++)keys[i]=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.starswordmaster.slot"+(i+1),InputUtil.Type.KEYSYM,codes[i],category));
  menu=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.starswordmaster.tree",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_K,category));
  status=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.starswordmaster.status",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_H,category));
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{ready=false;data=new PlayerProgress();});
  ClientPlayNetworking.registerGlobalReceiver(SnapshotPayload.ID,(payload,context)->context.client().execute(()->{
   try{Properties properties=new Properties();properties.load(new StringReader(payload.data()));data=PlayerProgress.fromProperties(properties);ready=true;}
   catch(IOException|IllegalArgumentException e){ready=false;}
  }));
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   while(status.wasPressed())if(client.player!=null&&client.currentScreen==null)client.setScreen(new StatusScreen());
   while(menu.wasPressed())if(ready&&client.player!=null&&client.currentScreen==null)client.setScreen(new SkillTreeScreen());
   for(int i=0;i<5;i++)while(keys[i].wasPressed())if(ready&&client.player!=null&&client.currentScreen==null)send("cast","",i);
  });
  SkillHud.register();
 }
}

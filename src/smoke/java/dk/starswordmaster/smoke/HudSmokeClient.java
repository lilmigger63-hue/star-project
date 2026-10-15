package dk.starswordmaster.smoke;
import dk.starswordmaster.client.*;
import dk.starswordmaster.ui.SkillHudLayout;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.util.Identifier;
import java.util.*;
import org.lwjgl.glfw.GLFW;
/** Local visual regression scenario; never included in the ordinary mod JAR. */
public class HudSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait;private boolean started,checkedRender;private Properties before;
 private void check(boolean value,String name){if(!value)throw new AssertionError("HUD_SMOKE FAIL: "+name);System.out.println("HUD_SMOKE PASS: "+name);}
 private void size(MinecraftClient client,int w,int h,int scale){client.getWindow().setWindowedSize(w,h);GLFW.glfwSetWindowPos(client.getWindow().getHandle(),0,0);client.options.getGuiScale().setValue(scale);client.onResolutionChanged();}
 private void screenshot(MinecraftClient client,String name){ScreenshotRecorder.saveScreenshot(client.runDirectory,"hud-"+name+".png",client.getFramebuffer(),1,message->{});System.out.println("HUD_SMOKE SCREENSHOT: "+name);}
 private void bounds(MinecraftClient client){int w=client.getWindow().getScaledWidth(),h=client.getWindow().getScaledHeight();var l=SkillHudLayout.forScreen(w,h);check(l.x()==w/2-91&&l.y()+22+SkillHudLayout.GAP==h-22,"aligned with current vanilla hotbar at "+w+"x"+h);}
 private void open(MinecraftClient client,String id){var key=Arrays.stream(client.options.allKeys).filter(k->k.getId().equals("key.starswordmaster."+id)).findFirst().orElseThrow();KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(key));}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.hud"))return;
  ClientLifecycleEvents.CLIENT_STARTED.register(client->{
  client.options.pauseOnLostFocus=false;
  var skills=Identifier.of("starswordmaster","skills");
  HudElementRegistry.attachElementBefore(skills,Identifier.of("starsmoke","before_skills"),(context,tick)->{before=StarSwordmasterClient.ready?StarSwordmasterClient.data.toProperties():null;});
  HudElementRegistry.attachElementAfter(skills,Identifier.of("starsmoke","after_skills"),(context,tick)->{
   if(before!=null){if(!before.equals(StarSwordmasterClient.data.toProperties()))throw new AssertionError("HUD_SMOKE FAIL: rendering changed progression");if(!checkedRender){checkedRender=true;System.out.println("HUD_SMOKE PASS: rendering does not mutate player data");}}
  });
  });
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   if(!started&&++ticks>100&&client.currentScreen!=null){started=true;client.createIntegratedServerLoader().start("HudSmoke",()->client.setScreen(new TitleScreen()));}
   if(client.player==null||!StarSwordmasterClient.ready||wait++<20)return;wait=0;
   switch(stage++){
    case 0->{
     client.getTutorialManager().setStep(TutorialStep.NONE);client.setScreen(null);size(client,1280,720,2);
     var uuid=client.player.getUuid();client.getServer().execute(()->{var player=client.getServer().getPlayerManager().getPlayer(uuid);player.clearStatusEffects();player.setHealth(player.getMaxHealth()-4);player.getAbilities().invulnerable=true;});
     int[] codes={GLFW.GLFW_KEY_Z,GLFW.GLFW_KEY_X,GLFW.GLFW_KEY_C,GLFW.GLFW_KEY_V,GLFW.GLFW_KEY_B};for(int i=0;i<5;i++)check(StarSwordmasterClient.keys[i].getDefaultKey().getCode()==codes[i],"slot "+(i+1)+" retains default key");
    }
    case 1->{bounds(client);check(StarSwordmasterClient.data.canCast(3),"fixture permits Mending Light cast");StarSwordmasterClient.send("cast","",3);}
    case 2->{check(StarSwordmasterClient.data.cooldown("mend")>0,"existing cooldown active for screenshot");screenshot(client,"scale2");}
    case 8->size(client,960,720,3);
    case 10->{bounds(client);check(client.getWindow().getScaleFactor()==3,"GUI scale 3 applied");screenshot(client,"scale3");}
    case 16->size(client,853,481,1);
    case 18->{bounds(client);screenshot(client,"odd-scale1");}
    case 24->size(client,640,480,2);
    case 26->{bounds(client);screenshot(client,"small-scale2");}
    case 28->{client.options.hudHidden=true;}
    case 29->screenshot(client,"hidden");
    case 30->{client.options.hudHidden=false;open(client,"status");}
    case 31->{check(client.currentScreen instanceof StatusScreen,"H still opens status");client.currentScreen.close();open(client,"tree");}
    case 32->{check(client.currentScreen instanceof SkillTreeScreen,"K still opens skill tree");client.currentScreen.close();}
    case 33->{check(checkedRender,"HUD was rendered");System.out.println("HUD_SMOKE COMPLETE");}
    default->{}
   }
  });
 }
}

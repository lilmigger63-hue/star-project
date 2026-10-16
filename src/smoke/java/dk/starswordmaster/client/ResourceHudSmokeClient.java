package dk.starswordmaster.client;
import dk.starswordmaster.ui.*;
import dk.starswordmaster.client.mixin.HudHealthAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.*;
/** Actual SP/two-client HUD exercise; shares package to check visibility without adding production test APIs. */
public class ResourceHudSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait,renders,fallbackRenders,hiddenRenders,reconnectWait,initialLevel;private boolean expectBoostExpiry,checkedBoostExpiry,lastVisible,started,finished,reconnecting,single,forceFallback,sawDeath;private String role;private double health,mana,stamina,maxMana;private ClientPlayerEntity beforeDeath;private Properties beforeRender;
 private void check(boolean value,String message){if(!value)throw new AssertionError("RESOURCE_"+role+" FAIL: "+message);System.out.println("RESOURCE_"+role+" PASS: "+message);}
 private ResourceHudData data(MinecraftClient c){return ResourceHudData.from(StarSwordmasterClient.data,StarSwordmasterClient.ready,c.player.getHealth(),c.player.getMaxHealth(),c.player.getAbsorptionAmount()).orElseThrow();}
 private void connect(MinecraftClient c){if(single){c.createIntegratedServerLoader().start("ResourceSmoke",()->c.setScreen(new TitleScreen()));return;}ConnectScreen.connect(new TitleScreen(),c,ServerAddress.parse("127.0.0.1:25569"),new ServerInfo("Local resource HUD test","127.0.0.1:25569",ServerInfo.ServerType.OTHER),false,null);}
 private void size(MinecraftClient c,int w,int h,int scale){c.getWindow().setWindowedSize(w,h);GLFW.glfwSetWindowPos(c.getWindow().getHandle(),0,0);c.options.getGuiScale().setValue(scale);c.onResolutionChanged();}
 private void screenshot(MinecraftClient c,String name){ScreenshotRecorder.saveScreenshot(c.runDirectory,"resources-"+role+"-"+name+".png",c.getFramebuffer(),1,message->{});}
 private void command(MinecraftClient c,String name){c.getNetworkHandler().sendChatCommand("starhudtest "+name);}
 private void open(MinecraftClient c,String name){var key=Arrays.stream(c.options.allKeys).filter(k->k.getId().equals("key.starswordmaster."+name)).findFirst().orElseThrow();KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(key));}
 private void bounds(MinecraftClient c){int w=c.getWindow().getScaledWidth(),h=c.getWindow().getScaledHeight();var l=SkillHud.resourceLayout(w,h);var s=SkillHudLayout.forResources(l);check(l.x()>=6&&l.x()+176<=w-6,"panel fits current GUI "+w+"x"+h);check(s.x()==l.barX()&&s.y()==l.barY(2)+14,"skills left align below stamina");check(!l.stacked()||s.y()+SkillHudLayout.SCALED_HEIGHT<=h-59-l.vanillaLift()-4,"skills clear native indicators");check(ResourceHudLayout.tooltipExtraLift(c.player.getMaxHealth(),c.player.getAbsorptionAmount())>=10,"item name clears armor at expanded max health");}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.resources"))return;role=System.getProperty("starsmoke.resources.role","A");single=Boolean.getBoolean("starsmoke.resources.singleplayer");
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);var skills=Identifier.of("starswordmaster","skills");
   HudElementRegistry.attachElementBefore(skills,Identifier.of("starsmoke","resource_before"),(context,tick)->{if(forceFallback){StarSwordmasterClient.ready=false;fallbackRenders++;}beforeRender=StarSwordmasterClient.data==null?null:StarSwordmasterClient.data.toProperties();renders++;});
   HudElementRegistry.attachElementAfter(skills,Identifier.of("starsmoke","resource_after"),(context,tick)->{lastVisible=SkillHud.visible(c);if(beforeRender!=null&&!beforeRender.equals(StarSwordmasterClient.data.toProperties()))throw new AssertionError("RESOURCE_"+role+" FAIL: HUD mutated progression");});
  });
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   ticks++;if(!finished&&ticks>6000)throw new AssertionError("RESOURCE_"+role+" FAIL: timeout at stage "+stage);
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;connect(c);}if(reconnecting&&c.player==null&&++reconnectWait>40){reconnecting=false;connect(c);}
   if(expectBoostExpiry&&!checkedBoostExpiry&&c.player!=null){
    int retained=((HudHealthAccessor)c.inGameHud).starswordmaster$getRenderHealthValue();
    if(retained>c.player.getMaxHealth()){
     check(ResourceHudLayout.tooltipExtraLift(c.player.getMaxHealth(),c.player.getHealth(),retained,c.player.getAbsorptionAmount())>=18,"item name clears retained armor rows after Health Boost expires");checkedBoostExpiry=true;screenshot(c,"boost-expired");
    }
   }
   if(finished||c.player==null||(!StarSwordmasterClient.ready&&!forceFallback)||wait++<20)return;wait=0;
   if((stage==0||stage==24)&&!single&&c.getNetworkHandler().getPlayerList().size()<2)return;
   if(stage==2&&c.player.getHealth()>=health)return; // Wait for real damage after vanilla load protection.
   switch(stage++){
    case 0->{c.setScreen(null);size(c,1280,720,2);var d=data(c);initialLevel=d.level().intValueExact();check(initialLevel==(role.equals("A")?10:100),"authoritative per-player level");check(d.health().maximum()==(role.equals("A")?21:30),"actual Endurance max health");check(c.player.getSkin()!=null,"vanilla skin/default skin ready");check(SkillHud.visible(c),"new HUD visible during gameplay");int[] codes={GLFW.GLFW_KEY_Z,GLFW.GLFW_KEY_X,GLFW.GLFW_KEY_C,GLFW.GLFW_KEY_V,GLFW.GLFW_KEY_B};for(int i=0;i<5;i++)check(StarSwordmasterClient.keys[i].getDefaultKey().getCode()==codes[i],"skill binding "+i+" preserved");}
    case 1->{bounds(c);screenshot(c,"side");health=data(c).health().current();command(c,"damage");}
    case 2->{var d=data(c);check(d.health().current()<health,"damage updates actual health meter");health=d.health().current();mana=d.mana().current();screenshot(c,"damaged");StarSwordmasterClient.send("cast","",2);}
    case 3->{var d=data(c);check(d.health().current()>health,"Mending Light updates health meter");check(d.mana().current()<mana&&StarSwordmasterClient.data.cooldown("mend")>0,"healing consumes synchronized mana and starts cooldown");screenshot(c,"healed");mana=d.mana().current();stamina=d.stamina().current();StarSwordmasterClient.send("cast","",1);}
    case 4->{var d=data(c);check(d.mana().current()<mana&&d.stamina().current()<stamina,"sword guard consumes both resources");check(c.player.hasStatusEffect(StatusEffects.RESISTANCE),"existing guard effect still works");mana=d.mana().current();stamina=d.stamina().current();screenshot(c,"spent");}
    case 5->{var d=data(c);check(d.mana().current()>mana&&d.stamina().current()>stamina,"live regeneration updates both meters");maxMana=d.mana().maximum();command(c,"level");}
    case 6->{var d=data(c);check(d.level().intValueExact()==Math.min(100,initialLevel+1),"level badge updates from actual XP source");check(d.mana().maximum()==maxMana+(role.equals("A")?2:0),"resource maximum updates with level");screenshot(c,"level");command(c,"absorb");}
    case 7->{var d=data(c);check(d.absorption()>0&&d.healthText().contains(" +"),"absorption retained and shown separately");screenshot(c,"absorption");command(c,"poison");}
    case 8->{check(c.player.hasStatusEffect(StatusEffects.POISON),"poison state available for health icon");screenshot(c,"poison");command(c,"wither");}
    case 9->{check(c.player.hasStatusEffect(StatusEffects.WITHER)&&!c.player.hasStatusEffect(StatusEffects.POISON),"wither health icon state");screenshot(c,"wither");command(c,"clear");size(c,960,720,3);command(c,"air");command(c,"tooltip");c.inGameHud.setOverlayMessage(Text.literal("HUD message"),false);}
    case 10->{bounds(c);check(ResourceHudLayout.forScreen(c.getWindow().getScaledWidth(),c.getWindow().getScaledHeight()).stacked(),"small GUI uses stacked layout");check(c.player.getArmor()>0&&c.player.getHungerManager().getFoodLevel()==6,"vanilla armor and hunger retained");screenshot(c,"scale3-air");size(c,853,481,1);}
    case 11->{bounds(c);screenshot(c,"odd-scale1");size(c,640,480,2);}
    case 12->{bounds(c);screenshot(c,"small-scale2");c.options.hudHidden=true;hiddenRenders=renders;}
    case 13->{check(!SkillHud.visible(c)&&renders==hiddenRenders,"F1 hides entire custom HUD with vanilla HUD");screenshot(c,"hidden");c.options.hudHidden=false;open(c,"status");}
    case 14->{check(c.currentScreen instanceof StatusScreen,"H status screen preserved");c.currentScreen.close();open(c,"tree");}
    case 15->{check(c.currentScreen instanceof SkillTreeScreen,"K skill tree preserved");c.currentScreen.close();forceFallback=true;}
    case 16->{check(fallbackRenders>0&&!lastVisible,"missing snapshot hides replacement and allows vanilla hearts");screenshot(c,"fallback");forceFallback=false;}
    case 17->{check(SkillHud.visible(c),"late snapshots restore resource HUD");check(renders>0,"HUD rendered without mutating saved fields");}
    case 18->{beforeDeath=c.player;command(c,"die");}
    case 19->{sawDeath=!c.player.isAlive();if(sawDeath)c.player.requestRespawn();c.setScreen(null);}
    case 20->{check(c.player.isAlive()&&(sawDeath||c.player!=beforeDeath),"actual death and respawn completed");var d=data(c);check(d.health().current()==d.health().maximum(),"HUD uses full expanded health after respawn");check(d.level().intValueExact()==Math.min(100,initialLevel+1),"level persists after respawn");screenshot(c,"respawn");}
    case 21->command(c,"boost");
    case 22->{int retained=((HudHealthAccessor)c.inGameHud).starswordmaster$getRenderHealthValue();if(retained<c.player.getMaxHealth()){stage--;return;}check(c.player.getMaxHealth()>40,"real Health Boost expands vanilla health rows");screenshot(c,"boosted");expectBoostExpiry=true;command(c,"unboost");}
    case 23->{check(checkedBoostExpiry,"retained-health transition observed");c.disconnect(Text.literal("Resource HUD lifecycle test"));reconnecting=true;reconnectWait=0;}
    case 24->{var d=data(c);check(d.level().intValueExact()==Math.min(100,initialLevel+1)&&d.health().maximum()==(role.equals("A")?21:30),"own level and max health persist on reload");check(d.health().current()==d.health().maximum(),"current health preserved on reload: "+d.health().text());check(SkillHud.visible(c),"HUD restored after reconnect/world reload");screenshot(c,"reload");System.out.println("RESOURCE_"+role+" COMPLETE "+(single?"singleplayer":"multiplayer"));finished=true;}
    default->{}
   }
  });
 }
}

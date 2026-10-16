package dk.starswordmaster.smoke;
import dk.starswordmaster.client.*;
import dk.starswordmaster.progression.PlayerProgress;
import dk.starswordmaster.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.*;
import net.minecraft.client.network.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import java.util.*;
import org.lwjgl.glfw.GLFW;
/** Two-client end-to-end test using real H/K clicks, server snapshots and legacy saves. */
public class PointsSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait,reconnectWait,vanillaXp;
 private boolean started,reconnecting,finished,sawDeath;
 private ClientPlayerEntity beforeDeath;
 private String role;
 private void check(boolean value,String message){if(!value)throw new AssertionError("POINTS_"+role+" FAIL: "+message);System.out.println("POINTS_"+role+" PASS: "+message);}
 private void connect(MinecraftClient client){ConnectScreen.connect(new TitleScreen(),client,ServerAddress.parse("127.0.0.1:25567"),new ServerInfo("Local point test","127.0.0.1:25567",ServerInfo.ServerType.OTHER),false,null);}
 private void open(MinecraftClient client,String name){var key=Arrays.stream(client.options.allKeys).filter(k->k.getId().equals("key.starswordmaster."+name)).findFirst().orElseThrow();KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(key));}
 private List<ButtonWidget> plus(MinecraftClient client){return client.currentScreen.children().stream().filter(e->e instanceof ButtonWidget).map(e->(ButtonWidget)e).filter(b->b.getMessage().getString().equals("+")).toList();}
 private void click(MinecraftClient client,ButtonWidget button){check(button.visible&&button.active,"button is available: "+button.getMessage().getString());var click=new Click(button.getX()+button.getWidth()/2.0,button.getY()+button.getHeight()/2.0,new MouseInput(0,0));check(client.currentScreen.mouseClicked(click,false),"real mouse click handled");client.currentScreen.mouseReleased(click);}
 private ButtonWidget unlock(MinecraftClient client){return client.currentScreen.children().stream().filter(e->e instanceof ButtonWidget).map(e->(ButtonWidget)e).filter(b->b.getMessage().equals(Text.translatable("screen.starswordmaster.learn_perk"))).findFirst().orElseThrow();}
 private void choose(MinecraftClient client,double x,double y){var screen=client.currentScreen;var view=new TreeViewport();int panel=screen.width-158;view.fit(panel,screen.height-100);screen.mouseClicked(new Click((int)view.screenX(x,panel/2.0),(int)view.screenY(y,screen.height/2.0),new MouseInput(0,0)),false);}
 private void screenshot(MinecraftClient client,String name){ScreenshotRecorder.saveScreenshot(client.runDirectory,"points-"+role+"-"+name+".png",client.getFramebuffer(),1,message->{});}
 private void balanceA(PlayerProgress p){check(p.level().intValueExact()==3&&p.statPoints().intValueExact()==4&&p.points().intValueExact()==1,"A retains separate balances after lifecycle change");check(p.stat(PlayerProgress.Stat.STRENGTH)==2&&p.stat(PlayerProgress.Stat.AGILITY)==1&&p.stat(PlayerProgress.Stat.ENDURANCE)==1&&p.stat(PlayerProgress.Stat.SPIRIT)==2,"A retains all allocated stats");check(p.learned().equals(Set.of("spark")),"A retains learned perk");}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.points"))return;role=System.getProperty("starsmoke.points.role","A");
  ClientLifecycleEvents.CLIENT_STARTED.register(client->{client.options.pauseOnLostFocus=false;client.getTutorialManager().setStep(TutorialStep.NONE);});
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   ticks++;if(!started&&ticks>100&&client.currentScreen!=null){started=true;connect(client);}
   if(reconnecting&&client.player==null&&++reconnectWait>40){reconnecting=false;connect(client);}
   if(finished||client.player==null||!StarSwordmasterClient.ready||wait++<20)return;wait=0;
   if(stage==0&&client.getNetworkHandler().getPlayerList().size()<2){if(ticks>2400)throw new AssertionError("Both test clients must connect");return;}
   var p=StarSwordmasterClient.data;
   if(stage==0){client.setScreen(null);client.getWindow().setWindowedSize(1280,800);client.options.getGuiScale().setValue(1);client.onResolutionChanged();vanillaXp=client.player.experienceLevel;check(client.getNetworkHandler().getPlayerList().size()>=2,"two clients share the server");}
   if(role.equals("B")){runB(client,p);return;}
   switch(stage++){
    case 0->{check(p.level().intValueExact()==2&&p.statPoints().intValueExact()==4&&p.stat(PlayerProgress.Stat.STRENGTH)==1&&p.points().intValueExact()==1,"legacy A receives only budget difference");open(client,"status");}
    case 1->{check(client.currentScreen instanceof StatusScreen&&plus(client).size()==4,"H has four stat controls");click(client,plus(client).get(0));}
    case 2->{check(p.statPoints().intValueExact()==3&&p.stat(PlayerProgress.Stat.STRENGTH)==2&&p.points().intValueExact()==1,"Strength uses stat point only");click(client,plus(client).get(1));}
    case 3->{check(p.statPoints().intValueExact()==2&&p.stat(PlayerProgress.Stat.AGILITY)==1,"Agility updates from server");click(client,plus(client).get(2));}
    case 4->{check(p.statPoints().intValueExact()==1&&p.stat(PlayerProgress.Stat.ENDURANCE)==1,"Endurance updates from server");click(client,plus(client).get(3));}
    case 5->{check(p.statPoints().intValueExact()==0&&p.stat(PlayerProgress.Stat.SPIRIT)==1&&p.points().intValueExact()==1,"all five earned stat points allocated freely");check(plus(client).stream().noneMatch(b->b.active),"no stat purchase with perk point remaining");screenshot(client,"wide");client.currentScreen.close();open(client,"tree");}
    case 6->{check(client.currentScreen instanceof SkillTreeScreen&&plus(client).isEmpty(),"K has no stat allocation controls");StarSwordmasterClient.send("stat","STRENGTH",0);click(client,unlock(client));}
    case 7->{check(p.statPoints().intValueExact()==0&&p.stat(PlayerProgress.Stat.STRENGTH)==2&&p.points().intValueExact()==0&&p.learned().contains("spark"),"server rejects stat overdraft while K spends one perk");client.currentScreen.close();client.getNetworkHandler().sendChatCommand("starpointstest level");}
    case 8->{check(p.level().intValueExact()==3&&p.statPoints().intValueExact()==5&&p.points().intValueExact()==1,"next level grants exactly five stat points and existing perk reward");check(client.player.experienceLevel==vanillaXp,"mod level gain leaves vanilla XP unchanged");open(client,"status");}
    case 9->click(client,plus(client).get(3));
    case 10->{balanceA(p);check(p.maxMana()==114,"Spirit still uses existing mana formula");screenshot(client,"spare-points");client.currentScreen.close();beforeDeath=client.player;client.getNetworkHandler().sendChatCommand("starpointstest die");}
    case 11->{sawDeath=!client.player.isAlive();if(sawDeath)client.player.requestRespawn();client.setScreen(null);}
    case 12->{check(client.player.isAlive()&&(sawDeath||client.player!=beforeDeath),"actual death and respawn completed");balanceA(p);}
    case 13->{client.disconnect(new TitleScreen(),false);reconnecting=true;reconnectWait=0;}
    case 14->{balanceA(p);check(client.getNetworkHandler().getPlayerList().size()>=2,"second player remains connected during reconnect");client.getWindow().setWindowedSize(640,480);client.options.getGuiScale().setValue(2);client.onResolutionChanged();open(client,"status");}
    case 15->{check(client.currentScreen instanceof StatusScreen,"H works after reload");client.currentScreen.mouseScrolled(150,100,0,-5);}
    case 16->{var button=plus(client).get(0);check(button.visible&&button.active,"stat button visible before event-order test");client.currentScreen.setFocused(button);button.setFocused(true);client.currentScreen.mouseScrolled(150,100,0,-1000);client.currentScreen.keyPressed(new KeyInput(GLFW.GLFW_KEY_ENTER,0,0));}
    case 17->{balanceA(p);check(p.statPoints().intValueExact()==4,"scroll plus Enter cannot activate an offscreen stat control");client.currentScreen.keyPressed(new KeyInput(GLFW.GLFW_KEY_HOME,0,0));client.currentScreen.mouseScrolled(150,100,0,-5);}
    case 18->{screenshot(client,"narrow");StarSwordmasterClient.ready=false;StarSwordmasterClient.data=null;}
    case 19->{check(StarSwordmasterClient.ready&&client.currentScreen instanceof StatusScreen,"missing data and late snapshots are safe with stat controls");balanceA(StarSwordmasterClient.data);System.out.println("POINTS_A COMPLETE");finished=true;}
    default->{}
   }
  });
 }
 private void runB(MinecraftClient client,PlayerProgress p){
  switch(stage++){
   case 0->{check(p.level().intValueExact()==1&&p.statPoints().intValueExact()==0&&p.points().intValueExact()==0,"legacy B starts before level-up threshold");client.getNetworkHandler().sendChatCommand("starpointstest level");}
   case 1->{check(p.level().intValueExact()==3&&p.totalXp().intValueExact()==169&&p.statPoints().intValueExact()==10&&p.points().intValueExact()==2,"single real XP award grants ten stats for two levels");check(client.player.experienceLevel==vanillaXp,"multi-level gain leaves vanilla XP unchanged");open(client,"status");}
   case 2->click(client,plus(client).get(1));
   case 3->{check(p.statPoints().intValueExact()==9&&p.stat(PlayerProgress.Stat.AGILITY)==1&&p.points().intValueExact()==2,"B can retain nine unused stat points");client.currentScreen.close();open(client,"tree");}
   case 4->click(client,unlock(client));
   case 5->{check(p.statPoints().intValueExact()==9&&p.points().intValueExact()==1,"B skill unlock spends only perk point");choose(client,160,-100);click(client,unlock(client));}
   case 6->{check(p.statPoints().intValueExact()==9&&p.points().intValueExact()==0&&p.learned().equals(Set.of("spark","light")),"both perk points consumed without using stats");choose(client,0,-140);check(!unlock(client).active,"K cannot unlock with only stat points available");StarSwordmasterClient.send("unlock","frost_touch",0);}
   case 7->{check(p.statPoints().intValueExact()==9&&p.points().intValueExact()==0&&!p.learned().contains("frost_touch"),"server refuses to substitute stat points for perks");client.currentScreen.close();}
   default->{check(p.statPoints().intValueExact()==9&&p.points().intValueExact()==0&&p.stat(PlayerProgress.Stat.AGILITY)==1,"B remains isolated from A purchases and lifecycle");if(stage>=36){System.out.println("POINTS_B COMPLETE");finished=true;}}
  }
 }
}

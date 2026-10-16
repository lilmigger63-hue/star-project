package dk.starswordmaster.smoke;
import dk.starswordmaster.StatAttributes;
import dk.starswordmaster.client.*;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.StatusData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import java.util.*;
/** Two actual clients: low/high stats, H allocation, death, reconnect and resized UI. */
public class EffectsSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait,reconnectWait;private boolean started,reconnecting,finished,sawDeath;private ClientPlayerEntity beforeDeath;private String role;private boolean single;
 private void check(boolean value,String message){if(!value)throw new AssertionError("STAT_EFFECTS_"+role+" FAIL: "+message);System.out.println("STAT_EFFECTS_"+role+" PASS: "+message);}
 private void equal(double expected,double actual,String message){check(Math.abs(expected-actual)<0.0001,message+" ("+actual+")");}
 private void connect(MinecraftClient c){if(single){c.createIntegratedServerLoader().start("EffectsSmoke",()->c.setScreen(new TitleScreen()));return;}ConnectScreen.connect(new TitleScreen(),c,ServerAddress.parse("127.0.0.1:25568"),new ServerInfo("Local stat effects test","127.0.0.1:25568",ServerInfo.ServerType.OTHER),false,null);}
 private void open(MinecraftClient c,String name){var key=Arrays.stream(c.options.allKeys).filter(k->k.getId().equals("key.starswordmaster."+name)).findFirst().orElseThrow();KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(key));}
 private List<ButtonWidget> plus(MinecraftClient c){return c.currentScreen.children().stream().filter(e->e instanceof ButtonWidget).map(e->(ButtonWidget)e).filter(b->b.getMessage().getString().equals("+")).toList();}
 private void click(MinecraftClient c,ButtonWidget b){check(b.visible&&b.active,"H stat button available");var click=new Click(b.getX()+b.getWidth()/2.0,b.getY()+b.getHeight()/2.0,new MouseInput(0,0));check(c.currentScreen.mouseClicked(click,false),"H mouse click handled");c.currentScreen.mouseReleased(click);}
 private void command(MinecraftClient c,String command){c.getNetworkHandler().sendChatCommand("stareffectstest "+command);}
 private void screenshot(MinecraftClient c,String name){ScreenshotRecorder.saveScreenshot(c.runDirectory,"effects-"+role+"-"+name+".png",c.getFramebuffer(),1,message->{});}
 private void attributes(MinecraftClient c,PlayerProgress p){var b=p.bonuses();equal(1+b.miningSpeed(),c.player.getAttributeValue(EntityAttributes.BLOCK_BREAK_SPEED),"mining attribute synced");equal(0.1*(1+b.movementSpeed()),c.player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED),"movement attribute synced");equal(20+b.health(),c.player.getMaxHealth(),"max health synced");for(var a:List.of(EntityAttributes.BLOCK_BREAK_SPEED,EntityAttributes.MOVEMENT_SPEED,EntityAttributes.MAX_HEALTH))check(c.player.getAttributeInstance(a).getModifiers().stream().filter(m->Set.of(StatAttributes.MINING_ID,StatAttributes.SPEED_ID,StatAttributes.HEALTH_ID).contains(m.id())).count()==1,"one synced modifier "+a);check(p.statPoints().intValueExact()==495-p.stat(PlayerProgress.Stat.STRENGTH)-p.stat(PlayerProgress.Stat.AGILITY)-p.stat(PlayerProgress.Stat.ENDURANCE)-p.stat(PlayerProgress.Stat.SPIRIT)&&p.points().intValueExact()==85,"point types and allocations unchanged");}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.effects"))return;role=System.getProperty("starsmoke.effects.role","A");single=Boolean.getBoolean("starsmoke.effects.singleplayer");
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   ticks++;if(!finished&&ticks>4000)throw new AssertionError("STAT_EFFECTS_"+role+" FAIL: scenario timeout at stage "+stage);if(!started&&ticks>100&&c.currentScreen!=null){started=true;connect(c);}if(reconnecting&&c.player==null&&++reconnectWait>40){reconnecting=false;connect(c);}
   if(finished||c.player==null||!StarSwordmasterClient.ready||wait++<30)return;wait=0;
   if(stage==0&&!single&&c.getNetworkHandler().getPlayerList().size()<2){if(ticks>2400)throw new AssertionError("Both effects clients must connect");return;}
   var p=StarSwordmasterClient.data;
   switch(stage++){
    case 0->{c.setScreen(null);c.getWindow().setWindowedSize(1280,800);c.options.getGuiScale().setValue(1);c.onResolutionChanged();attributes(c,p);check(p.stat(PlayerProgress.Stat.STRENGTH)==(role.equals("A")?10:100),"low/high fixture loaded");equal(10,c.player.getHealth(),"initial health not healed by stat application");command(c,"check");command(c,"regen");open(c,"status");}
    case 1->{check(c.currentScreen instanceof StatusScreen,"H status screen separate");check(StatusData.from(p,true).orElseThrow().bonuses().equals(p.bonuses()),"H receives exact current stat bonuses");screenshot(c,"wide");}
    case 2->command(c,"combat");
    case 3->{if(role.equals("A"))click(c,plus(c).get(2));}
    case 4->{check(p.stat(PlayerProgress.Stat.ENDURANCE)==(role.equals("A")?11:100),"H allocation updated only selected player");attributes(c,p);equal(10,c.player.getHealth(),"buying Endurance adds capacity without healing");check(p.points().intValueExact()==85,"perk points unaffected by H");screenshot(c,"allocated");c.currentScreen.close();open(c,"tree");}
    case 5->{check(c.currentScreen instanceof SkillTreeScreen&&plus(c).isEmpty(),"K remains skill tree without stat controls");c.currentScreen.close();beforeDeath=c.player;command(c,"die");}
    case 6->{sawDeath=!c.player.isAlive();if(sawDeath)c.player.requestRespawn();c.setScreen(null);}
    case 7->{check(c.player.isAlive()&&(sawDeath||c.player!=beforeDeath),"actual death and respawn");attributes(c,p);equal(c.player.getMaxHealth(),c.player.getHealth(),"fresh respawn fills expanded health");command(c,"health");}
    case 8->command(c,"alive");
    case 9->{attributes(c,p);equal(c.player.getMaxHealth()-0.5,c.player.getHealth(),"alive respawn preserves partial health above 20");}
    case 10->{c.disconnect(Text.literal("Stat effects lifecycle test"));reconnecting=true;reconnectWait=0;}
    case 11->{attributes(c,p);check(p.stat(PlayerProgress.Stat.ENDURANCE)==(role.equals("A")?11:100),"allocated stats retained on reload");equal(c.player.getMaxHealth()-0.5,c.player.getHealth(),"partial health above 20 preserved on reconnect");command(c,"partial");c.getWindow().setWindowedSize(640,480);c.options.getGuiScale().setValue(2);c.onResolutionChanged();open(c,"status");}
    case 12->{check(c.currentScreen instanceof StatusScreen,"H at small resolution and GUI scale 2");screenshot(c,"narrow-character");c.currentScreen.mouseScrolled(150,100,0,-5);}
    case 13->{screenshot(c,"narrow-stats");attributes(c,p);check(single||c.getNetworkHandler().getPlayerList().size()>=2,"players still independently connected");System.out.println("STAT_EFFECTS_"+role+" COMPLETE "+(single?"singleplayer":"multiplayer"));finished=true;}
    default->{}
   }
  });
 }
}

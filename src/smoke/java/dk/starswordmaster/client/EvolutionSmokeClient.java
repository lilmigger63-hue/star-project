package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.StatusData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import java.util.*;
import org.lwjgl.glfw.GLFW;
/** Exercises real C2S actions, snapshots, client UI, death and reconnect. */
public final class EvolutionSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait,checks;private boolean started,finished,reconnecting,sawDeath;private PlayerProgress stable;private Properties saved;
 private void check(boolean ok,String message){if(!ok)throw new AssertionError("EVOLUTION_CLIENT FAIL stage="+stage+": "+message);checks++;System.out.println("EVOLUTION_CLIENT PASS: "+message);}
 private void spendStat(MinecraftClient c){try{var m=StatusScreen.class.getDeclaredMethod("spendStat",PlayerProgress.Stat.class);m.setAccessible(true);m.invoke(c.currentScreen,PlayerProgress.Stat.STRENGTH);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 private boolean statButtonActive(MinecraftClient c){try{var f=StatusScreen.class.getDeclaredField("statButtons");f.setAccessible(true);return ((net.minecraft.client.gui.widget.ButtonWidget)((List<?>)f.get(c.currentScreen)).getFirst()).active;}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 private void connect(MinecraftClient c){ConnectScreen.connect(new TitleScreen(),c,ServerAddress.parse("127.0.0.1:25578"),new ServerInfo("Evolution smoke","127.0.0.1:25578",ServerInfo.ServerType.OTHER),false,null);}
 private void command(MinecraftClient c,String s){c.getNetworkHandler().sendChatCommand("starevolution "+s);}
 private void shot(MinecraftClient c,String name){ScreenshotRecorder.saveScreenshot(c.runDirectory,"evolution-"+name+".png",c.getFramebuffer(),1,m->{});}
 @Override public void onInitializeClient(){if(!Boolean.getBoolean("starsmoke.evolution"))return;
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);c.options.getMaxFps().setValue(30);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{if(finished)return;if(++ticks>6500)throw new AssertionError("EVOLUTION timeout stage="+stage);
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;connect(c);}
   if(reconnecting&&c.player==null&&++wait>60){reconnecting=false;wait=0;connect(c);return;}
   if(c.currentScreen instanceof DeathScreen){sawDeath=true;c.player.requestRespawn();c.setScreen(null);return;}
   if(c.player==null||!c.player.isLoaded()||!StarSwordmasterClient.ready)return;GLFW.glfwSetCursorPos(c.getWindow().getHandle(),c.getWindow().getWidth()-2,c.getWindow().getHeight()-2);if(++wait<30)return;wait=0;var p=StarSwordmasterClient.data;
   switch(stage++){
    case 0->{check(p.level().intValueExact()==4,"level-four fixture synchronized");StarSwordmasterClient.send("class","SWORDSMAN",0);}
    case 1->{check(p.playerClass()==PlayerProgress.PlayerClass.UNINITIATED,"server rejects class selection below5");command(c,"five");}
    case 2->{check(p.level().intValueExact()==5,"level-five server fixture");c.setScreen(new SkillTreeScreen());check(c.currentScreen.children().stream().filter(b->b instanceof net.minecraft.client.gui.widget.ButtonWidget).map(b->(net.minecraft.client.gui.widget.ButtonWidget)b).anyMatch(b->b.getMessage().getString().equals("Swordsman")&&b.active),"K class-selection button enabled at5");shot(c,"class-five");StarSwordmasterClient.send("class","SWORDSMAN",0);}
    case 3->{check(p.playerClass()==PlayerProgress.PlayerClass.SWORDSMAN&&p.evolution().stage()==0,"server accepts permanent base class at5");StarSwordmasterClient.send("class","MAGE",0);}
    case 4->{check(p.playerClass()==PlayerProgress.PlayerClass.SWORDSMAN,"server rejects second permanent class");command(c,"nine");}
    case 5->{check(p.level().intValueExact()==9&&p.evolution().stage()==0,"level9 has no automatic evolution");command(c,"gain");}
    case 6->{check(p.level().intValueExact()>=30&&p.evolution().stage()==3&&p.rank()==1,"multi-level XP award evolves immediately, independently of rank");check(p.points().equals(p.level().subtract(java.math.BigInteger.ONE)),"automatic evolution costs zero perkpoints");c.setScreen(new SkillTreeScreen());shot(c,"sword-hub");StarSwordmasterClient.send("unlock","quick_cut",0);}
    case 7->{check(p.learned().contains("quick_cut"),"normal skill unlock works after evolution");StarSwordmasterClient.send("reset_skills","",0);}
    case 8->{check(p.learned().isEmpty()&&p.evolution().stage()==3&&p.rank()==1,"free reset preserves class evolution");command(c,"late");}
    case 9->{check(p.level().intValueExact()==30&&p.playerClass()==PlayerProgress.PlayerClass.UNINITIATED&&p.evolution().stage()==0,"classless30 has no evolution");StarSwordmasterClient.send("class","MAGE",0);}
    case 10->{check(p.playerClass()==PlayerProgress.PlayerClass.MAGE&&p.evolution().stage()==3,"late actual class action immediately grants final stage");var d=StatusData.from(p,true).orElseThrow();check(d.evolution().equals(p.evolution()),"H reads exact derived server evolution");c.setScreen(new StatusScreen());}
    case 11->{shot(c,"mage-status");command(c,"persist");}
    case 12->{if(p.mana()<p.maxMana()||p.stamina()<p.maxStamina()){stage--;return;}check(p.evolution().stage()==3,"disk reload snapshot preserves evolution");stable=p;}
    case 13->{check(stable==p,"unchanged five-tick snapshots retain client PlayerProgress identity");saved=p.toProperties();c.setScreen(null);command(c,"die");}
    case 14->{if(!sawDeath||!c.player.isAlive()){stage--;return;}check(p.playerClass()==PlayerProgress.PlayerClass.MAGE&&p.evolution().stage()==3,"real death and respawn preserve automatic evolution");c.disconnect(Text.literal("Evolution reconnect validation"));reconnecting=true;wait=0;}
    case 15->{check(p.playerClass()==PlayerProgress.PlayerClass.MAGE&&p.evolution().stage()==3,"real reconnect reconstructs evolved character");check(p.points().equals(p.level().subtract(java.math.BigInteger.ONE)),"reconnect cannot duplicate level rewards");check(p.evolution().manaBonus()==30,"derived capacity is added once after reconnect");c.setScreen(new SkillTreeScreen());}
    case 16->{shot(c,"mage-hub");c.setScreen(new StatusScreen());command(c,"spectator");}
    case 17->{check(c.player.isSpectator(),"native spectator fixture rejects stat spending");stable=p;spendStat(c);check(!statButtonActive(c),"H debounces pending stat request");}
    case 18->{check(stable==p&&p.stat(PlayerProgress.Stat.STRENGTH)==0,"server-rejected stat request leaves snapshot and stats unchanged");check(statButtonActive(c),"H pending request expires without changed snapshot");command(c,"survival");}
    case 19->{check(!c.player.isSpectator(),"actual survival restored");spendStat(c);}
    case 20->{check(p.stat(PlayerProgress.Stat.STRENGTH)==1&&p.statPoints().intValueExact()==144,"valid H purchase still spends exactly one statpoint");check(statButtonActive(c),"authoritative changed snapshot releases H purchase immediately");shot(c,"status-after-purchase");System.out.println("EVOLUTION_CLIENT COMPLETE checks="+checks);finished=true;}
   }
  });
 }
}

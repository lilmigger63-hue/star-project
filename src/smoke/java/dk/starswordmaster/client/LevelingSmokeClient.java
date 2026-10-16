package dk.starswordmaster.client;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.math.BigInteger;
import java.util.*;
public class LevelingSmokeClient implements ClientModInitializer {
 private int ticks,wait,stage,checks,initialVanillaLevel;private float initialVanillaProgress;private boolean started,finished,reconnecting,single,sawDeath;private String role;
 private BigInteger oldLevel,oldStats,oldPerks,xp;private Properties saved;
 private void check(boolean ok,String msg){if(!ok)throw new AssertionError("LEVEL_"+role+" FAIL: "+msg+" stage="+stage);checks++;System.out.println("LEVEL_"+role+" PASS: "+msg);}
 private void command(MinecraftClient c,String name){c.getNetworkHandler().sendChatCommand("starleveltest "+name);}
 private void connect(MinecraftClient c){if(single){c.createIntegratedServerLoader().start("LevelSmoke",()->c.setScreen(new TitleScreen()));return;}ConnectScreen.connect(new TitleScreen(),c,ServerAddress.parse("127.0.0.1:25571"),new ServerInfo("Leveling test","127.0.0.1:25571",ServerInfo.ServerType.OTHER),false,null);}
 private void size(MinecraftClient c,int w,int h,int scale){c.getWindow().setWindowedSize(w,h);GLFW.glfwSetWindowPos(c.getWindow().getHandle(),0,0);c.options.getGuiScale().setValue(scale);c.onResolutionChanged();}
 private void bounds(MinecraftClient c,String name){int w=c.getWindow().getScaledWidth(),h=c.getWindow().getScaledHeight();var s=SkillHudLayout.forScreen(w,h);var r=ResourceHudLayout.forScreen(w,h);
  check(s.x()==w/2-91&&s.y()+22==h-23,"zero GUI gap to native selection/offhand outer edge at "+w+"x"+h);check(SkillHudLayout.GAP==0&&SkillHudLayout.WIDTH==182&&SkillHudLayout.HEIGHT==22,"bar size/slots remain native sized");check(r.y()==h-(r.stacked()?92:66)&&r.vanillaLift()==(r.stacked()?76:44),"resource HUD and XP/vanilla lift remain unchanged");
  ScreenshotRecorder.saveScreenshot(c.runDirectory,"level-"+role+"-"+name+".png",c.getFramebuffer(),1,m->{});
 }
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.leveling"))return;single=Boolean.getBoolean("starsmoke.leveling.singleplayer");role=System.getProperty("starsmoke.leveling.role","A");
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   ticks++;if(!finished&&ticks>6000)throw new AssertionError("LEVEL_"+role+" timeout at "+stage);
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;connect(c);}if(reconnecting&&c.player==null&&++wait>40){reconnecting=false;wait=0;connect(c);}
   if(finished||c.player==null||!StarSwordmasterClient.ready)return;
   if(c.currentScreen instanceof DeathScreen){sawDeath=true;c.player.requestRespawn();c.setScreen(null);return;}
   if(wait++<20)return;wait=0;var p=StarSwordmasterClient.data;
   if(!single&&c.getNetworkHandler().getPlayerList().size()<2)return;
   if(role.equals("B")){observer(c,p);return;}
   switch(stage++){
    case 0->{c.setScreen(null);initialVanillaLevel=c.player.experienceLevel;initialVanillaProgress=c.player.experienceProgress;check(p.level().intValueExact()==7&&p.totalXp().intValueExact()==909,"level-7 starting snapshot");command(c,"sword");}
    case 1->{check(p.totalXp().intValueExact()==1009&&p.level().intValueExact()==8,"real practice grants 100 XP at level 7 across level-8 boundary");check(p.statPoints().intValueExact()==35&&p.points().intValueExact()==5,"one real level grants exactly five statpoints and one perkpoint");command(c,"fire");}
    case 2->{check(p.totalXp().intValueExact()==1159&&p.level().intValueExact()==9,"real practice grants 150 XP when already level 8");check(p.statPoints().intValueExact()==40&&p.points().intValueExact()==6,"boosted XP keeps normal per-level rewards");c.setScreen(new StatusScreen());StarSwordmasterClient.send("stat","STRENGTH",0);}
    case 3->{check(p.stat(PlayerProgress.Stat.STRENGTH)==1&&p.statPoints().intValueExact()==39&&p.points().intValueExact()==6,"H stat spending keeps perks separate");c.currentScreen.close();c.setScreen(new SkillTreeScreen());StarSwordmasterClient.send("unlock","light",0);}
    case 4->{check(p.points().intValueExact()==5&&p.statPoints().intValueExact()==39&&p.learned().contains("light"),"K uses only perkpoints");c.currentScreen.close();oldLevel=p.level();oldStats=p.statPoints();oldPerks=p.points();command(c,"bulk");}
    case 5->{var gained=p.level().subtract(oldLevel);check(gained.intValueExact()>1&&p.totalXp().intValueExact()==16159,"one boosted reward preserves all XP across several levels");check(p.statPoints().subtract(oldStats).equals(gained.multiply(BigInteger.valueOf(5)))&&p.points().subtract(oldPerks).equals(gained),"bulk reward grants each level's points exactly once");check(c.player.experienceLevel==initialVanillaLevel&&c.player.experienceProgress==initialVanillaProgress,"mod XP leaves vanilla XP unchanged");command(c,"legacy");}
    case 6->{check(p.level().intValueExact()==100&&p.totalXp().intValueExact()==200000&&p.statPoints().signum()==0&&p.points().intValueExact()==97,"legacy capped save preserves level, XP and spent/unspent points");check(p.maxMana()==2793&&p.maxStamina()==199&&p.stat(PlayerProgress.Stat.SPIRIT)==495,"legacy resources, stats and class perks unchanged");command(c,"next");}
    case 7->{check(p.level().intValueExact()==101&&p.totalXp().intValueExact()==202100&&p.xpInLevel().intValueExact()==50,"legacy character progresses beyond level 100 without losing XP");check(p.statPoints().intValueExact()==5&&p.points().intValueExact()==98,"level 101 adds only five stats and one perk");command(c,"huge");}
    case 8->{check(p.level().compareTo(BigInteger.valueOf(Long.MAX_VALUE))>0,"server/client synchronize level beyond 64-bit range");xp=BigInteger.TEN.pow(100).multiply(BigInteger.valueOf(3)).divide(BigInteger.TWO).add(BigInteger.valueOf(202100));check(p.totalXp().equals(xp),"100-digit boosted XP arrives exactly");var d=StatusData.from(p,true).orElseThrow();check(d.totalXp().equals(xp)&&Double.isFinite(d.xpProgress())&&d.xpProgress()>=0&&d.xpProgress()<1,"H model shows exact huge values and finite progress");check(Double.isFinite(p.maxMana())&&Double.isFinite(p.maxStamina()),"existing resource formulas stay finite");c.setScreen(new StatusScreen());}
    case 9->{check(c.currentScreen instanceof StatusScreen,"H renders high level and XP without crash");c.currentScreen.close();size(c,1280,720,2);}
    case 10->{bounds(c,"1280-scale2");size(c,960,720,3);}
    case 11->{bounds(c,"960-scale3");size(c,853,481,1);}
    case 12->{bounds(c,"853-scale1");size(c,640,480,2);}
    case 13->{bounds(c,"640-scale2");int[] keys={GLFW.GLFW_KEY_Z,GLFW.GLFW_KEY_X,GLFW.GLFW_KEY_C,GLFW.GLFW_KEY_V,GLFW.GLFW_KEY_B};for(int i=0;i<5;i++)check(StarSwordmasterClient.keys[i].getDefaultKey().getCode()==keys[i],"slot key "+i+" preserved");saved=p.toProperties();command(c,"die");}
    case 14->{check(sawDeath&&c.player.isAlive(),"actual death and respawn completed");check(p.totalXp().equals(xp)&&p.level().equals(oldLevel=CharacterExperience.read(saved).level()),"death preserves huge XP/level");check(p.statPoints().equals(CharacterExperience.read(saved).level().subtract(BigInteger.ONE).multiply(BigInteger.valueOf(5)).subtract(BigInteger.valueOf(495))),"death does not duplicate points");c.disconnect(Text.literal("Leveling persistence test"));reconnecting=true;wait=0;}
    case 15->{check(p.level().equals(oldLevel)&&p.totalXp().equals(xp)&&p.stat(PlayerProgress.Stat.SPIRIT)==495&&p.learned().contains("mana_control"),"huge progression, stats, class and abilities survive world reload/reconnect");check(p.points().equals(oldLevel.subtract(BigInteger.valueOf(3))),"perkpoint entitlement unchanged after reconnect");check(SkillHud.visible(c),"HUD restored after reload");System.out.println("LEVEL_A COMPLETE "+(single?"singleplayer":"multiplayer")+" checks="+checks);finished=true;}
    default->{}
   }
  });
 }
 private void observer(MinecraftClient c,PlayerProgress p){
  if(stage==0){c.setScreen(null);check(p.level().intValueExact()==100&&p.totalXp().intValueExact()==200000&&p.statPoints().signum()==0&&p.points().intValueExact()==97,"B legacy migration is isolated from A");StarSwordmasterClient.send("unlock","light",0);stage=1;return;}
  if(stage==1){check(p.points().intValueExact()==96&&p.statPoints().signum()==0,"B spends only own perk balance");command(c,"sword");stage=2;return;}
  if(stage==2){check(p.totalXp().intValueExact()==200150&&p.level().intValueExact()==100&&p.statPoints().signum()==0,"B normal practice uses multiplier without premature level rewards");stage=3;}
  if(stage++>=24){check(p.totalXp().intValueExact()==200150&&p.points().intValueExact()==96&&p.stat(PlayerProgress.Stat.SPIRIT)==495,"A bulk XP, death and reconnect cannot change B data");System.out.println("LEVEL_B COMPLETE multiplayer checks="+checks);finished=true;}
 }
}

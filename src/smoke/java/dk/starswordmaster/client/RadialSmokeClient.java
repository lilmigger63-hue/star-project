package dk.starswordmaster.client;

import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.*;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.math.BigInteger;
import java.util.*;

/** Actual window/input/network smoke; never mutates client progression to simulate a server result. */
public final class RadialSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait,checks,observerTicks;private boolean started,finished,single,reconnecting,sawDeath;private String role;
 private java.util.concurrent.CompletableFuture<Void> reload;
 private Properties initial,resetSnapshot;private Set<String> learned;private BigInteger xp,level,points;private int learnedCount;
 private void check(boolean ok,String message){if(!ok)throw new AssertionError("RADIAL_"+role+" FAIL stage="+stage+": "+message);checks++;System.out.println("RADIAL_"+role+" PASS: "+message);}
 private void command(MinecraftClient c,String s){c.getNetworkHandler().sendChatCommand("starradial "+s);}
 private void connect(MinecraftClient c){if(single)c.createIntegratedServerLoader().start("RadialSmoke",()->c.setScreen(new TitleScreen()));else ConnectScreen.connect(new TitleScreen(),c,ServerAddress.parse("127.0.0.1:25575"),new ServerInfo("Radial smoke","127.0.0.1:25575",ServerInfo.ServerType.OTHER),false,null);}
 private void size(MinecraftClient c,int w,int h,int scale){c.setScreen(null);c.getWindow().setWindowedSize(w,h);GLFW.glfwSetWindowPos(c.getWindow().getHandle(),0,0);c.options.getGuiScale().setValue(scale);c.onResolutionChanged();c.setScreen(new SkillTreeScreen());}
 private Object field(Object object,String name){try{var f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 private ButtonWidget named(Screen s,String label){return s.children().stream().filter(e->e instanceof ButtonWidget).map(e->(ButtonWidget)e).filter(b->b.getMessage().getString().equals(label)).findFirst().orElseThrow(()->new AssertionError("Missing button "+label+" in "+s.children()));}
 private void click(MinecraftClient c,ButtonWidget b){check(b.visible&&b.active,"native button available: "+b.getMessage().getString());var click=new Click(b.getX()+b.getWidth()/2.0,b.getY()+b.getHeight()/2.0,new MouseInput(0,0));var screen=c.currentScreen;check(screen.mouseClicked(click,false),"native button mouse click handled");screen.mouseReleased(click);}
 private void snapshot(MinecraftClient c,String name){
  check(c.currentScreen instanceof SkillTreeScreen,"actual skill tree screen rendered");var s=c.currentScreen;var layout=(TreeScreenLayout)field(s,"layout");var view=(TreeViewport)field(s,"view");
  check(layout.width()==s.width&&layout.height()==s.height,"layout tracks real GUI "+s.width+"x"+s.height);
  var buttons=s.children().stream().filter(e->e instanceof ButtonWidget).map(e->(ButtonWidget)e).filter(b->b.visible).toList();
  for(var b:buttons)check(b.getX()>=0&&b.getY()>=0&&b.getX()+b.getWidth()<=s.width&&b.getY()+b.getHeight()<=s.height,"visible control fits: "+b.getMessage().getString());
  for(int i=0;i<buttons.size();i++)for(int j=i+1;j<buttons.size();j++){var a=buttons.get(i);var b=buttons.get(j);check(!(a.getX()<b.getX()+b.getWidth()&&a.getX()+a.getWidth()>b.getX()&&a.getY()<b.getY()+b.getHeight()&&a.getY()+a.getHeight()>b.getY()),"visible controls do not overlap");}
  if(name.contains("fit"))for(var node:SkillTreeLayout.NODES){double x=view.screenX(node.x(),layout.centerX()),y=view.screenY(node.y(),layout.centerY());int half=SkillTreeLayout.nodeSize(view.zoom())/2+3;check(x-half>=0&&x+half<=layout.treeWidth()&&y-half>=layout.bodyTop()&&y+half<=layout.bodyBottom(),"fit includes node "+node.id());}
  ScreenshotRecorder.saveScreenshot(c.runDirectory,"radial-"+role+"-"+name+".png",c.getFramebuffer(),1,m->{});
 }
 private void select(MinecraftClient c,String id){
  var s=(SkillTreeScreen)c.currentScreen;var layout=(TreeScreenLayout)field(s,"layout");var view=(TreeViewport)field(s,"view");
  var node=SkillTreeLayout.NODES.stream().filter(n->n.id().equals(id)).findFirst().orElseThrow();s.mouseScrolled(layout.centerX(),layout.centerY(),0,22);view.focus(node.x(),node.y());
  double centerX=layout.centerX(),centerY=layout.centerY();var drag=new Click(centerX,centerY,new MouseInput(0,0));check(s.mouseDragged(drag,13,7),"native tree accepts pan");check(s.mouseScrolled(centerX,centerY,0,2),"native tree accepts pointer-anchored zoom");
  double x=view.screenX(node.x(),centerX),y=view.screenY(node.y(),centerY);check(s.mouseClicked(new Click(x,y,new MouseInput(0,0)),false),"native node click handled after pan/zoom");check(field(s,"selected").equals(id),"native transformed hit test selects real ability "+id);
 }
 private void preserved(PlayerProgress p){var now=p.toProperties();for(var key:initial.stringPropertyNames())if(key.equals("class")||key.equals("style")||key.startsWith("xp")||key.startsWith("practice.")||key.startsWith("stat."))check(Objects.equals(initial.getProperty(key),now.getProperty(key)),"network reset preserves "+key);}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.radial"))return;single=Boolean.getBoolean("starsmoke.radial.singleplayer");role=System.getProperty("starsmoke.radial.role","A");
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);c.options.getMaxFps().setValue(30);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   if(finished)return;if(++ticks>7500)throw new AssertionError("RADIAL_"+role+" timeout at stage "+stage);
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;connect(c);}
   if(reconnecting&&c.player==null&&++wait>60){reconnecting=false;wait=0;connect(c);return;}
   if(c.currentScreen instanceof DeathScreen){sawDeath=true;c.player.requestRespawn();c.setScreen(null);return;}
   if(c.player==null||!c.player.isLoaded()||!StarSwordmasterClient.ready)return;
   GLFW.glfwSetCursorPos(c.getWindow().getHandle(),c.getWindow().getWidth()-2,c.getWindow().getHeight()-2);
   if(!single&&stage<16&&c.getNetworkHandler().getPlayerList().size()<2)return;
   if(++wait<24)return;wait=0;var p=StarSwordmasterClient.data;
   if(role.equals("B")){observer(c,p);return;}
   switch(stage++){
    case 0->{c.setScreen(null);command(c,"native");}
    case 1->{check(p.rank()==4&&p.level().intValueExact()==100,"rank-four server snapshot after actual native XP checks");initial=p.toProperties();learned=Set.copyOf(p.learned());learnedCount=learned.size();xp=p.totalXp();level=p.level();StarSwordmasterClient.send("reset_skills","RadialB",0);}
    case 2->{check(p.learned().equals(learned)&&p.totalXp().equals(xp),"forged target cannot reset sender or other player");StarSwordmasterClient.send("reset_skills","",-1);}
    case 3->{check(p.learned().equals(learned),"malformed reset slot rejected");size(c,320,240,1);}
    case 4->{check(c.currentScreen.width==320&&c.currentScreen.height==240,"real window resized to 320x240 GUI");snapshot(c,"320x240-fit");click(c,(ButtonWidget)field(c.currentScreen,"focusButton"));}
    case 5->{snapshot(c,"320x240-focus");select(c,"quick_cut");}
    case 6->{snapshot(c,"320x240-details");click(c,(ButtonWidget)field(c.currentScreen,"reset"));check(c.currentScreen instanceof ConfirmScreen,"reset opens real confirmation screen");}
    case 7->{click(c,named(c.currentScreen,"Cancel"));}
    case 8->{check(p.learned().equals(learned)&&p.points().equals(level.subtract(BigInteger.ONE).subtract(BigInteger.valueOf(learnedCount))),"cancel leaves learned skills and exact balance unchanged");click(c,(ButtonWidget)field(c.currentScreen,"reset"));}
    case 9->{click(c,named(c.currentScreen,"Confirm Reset"));}
    case 10->{check(p.learned().isEmpty()&&p.points().equals(level.subtract(BigInteger.ONE)),"confirmed actual ActionPayload refunds exact perk entitlement");check(p.rank()==1,"authoritative reset rank synchronizes immediately");for(int i=0;i<5;i++)check(p.slot(i).isEmpty()&&!p.canCast(i),"reset clears slot and prevents cast "+i);preserved(p);resetSnapshot=p.toProperties();points=p.points();command(c,"verify");StarSwordmasterClient.send("reset_skills","",0);}
    case 11->{check(p.points().equals(points)&&p.totalXp().equals(xp)&&p.learned().isEmpty(),"second actual reset grants no XP or extra points");StarSwordmasterClient.send("assign","quick_cut",0);StarSwordmasterClient.send("cast","",0);}
    case 12->{check(p.slot(0).isEmpty()&&p.cooldown("quick_cut")==0,"server rejects assignment and cast of reset ability");if(!(c.currentScreen instanceof SkillTreeScreen))c.setScreen(new SkillTreeScreen());click(c,(ButtonWidget)field(c.currentScreen,"unlock"));}
    case 13->{check(p.learned().contains("quick_cut")&&p.points().equals(points.subtract(BigInteger.ONE)),"actual learn button buys selected real ability after reset");@SuppressWarnings("unchecked") var slots=(List<ButtonWidget>)field(c.currentScreen,"assignButtons");click(c,slots.getFirst());}
    case 14->{check(p.slot(0).equals("quick_cut")&&p.canCast(0),"actual assign button restores valid cast eligibility");StarSwordmasterClient.send("reset_skills","",0);}
    case 15->{check(p.learned().isEmpty()&&p.slot(0).isEmpty()&&p.points().equals(points),"relearn and reset conserve perk balance");command(c,"persist");c.setScreen(null);command(c,"die");}
    case 16->{if(!sawDeath||!c.player.isAlive()){stage--;return;}check(p.learned().isEmpty()&&p.points().equals(points)&&p.totalXp().equals(xp),"actual death/respawn preserves reset and balance");c.disconnect(Text.literal("Radial persistence reload"));reconnecting=true;wait=0;}
    case 17->{check(p.learned().isEmpty()&&p.points().equals(points)&&p.totalXp().equals(xp)&&p.playerClass()==PlayerProgress.PlayerClass.SWORDSMAN,"disconnect/world reload preserves reset character");preserved(p);size(c,640,360,1);}
    case 18->{check(c.currentScreen.width==640&&c.currentScreen.height==360,"real window 640x360 GUI");snapshot(c,"640x360-fit");click(c,(ButtonWidget)field(c.currentScreen,"focusButton"));}
    case 19->{snapshot(c,"640x360-focus");size(c,854,480,1);}
    case 20->{check(c.currentScreen.width==854&&c.currentScreen.height==480,"real window 854x480 GUI");snapshot(c,"854x480-fit");click(c,(ButtonWidget)field(c.currentScreen,"focusButton"));}
    case 21->{snapshot(c,"854x480-focus");size(c,1280,720,2);}
    case 22->{check(c.currentScreen.width==640&&c.currentScreen.height==360,"actual GUI scale two at 1280x720");snapshot(c,"1280x720-scale2-fit");reload=c.reloadResources();}
    case 23->{if(!reload.isDone()){stage--;return;}check(!reload.isCompletedExceptionally(),"native resource reload completed");snapshot(c,"resource-reload-fit");select(c,"quick_cut");check(field(c.currentScreen,"selected").equals("quick_cut"),"node selection works after actual resource reload");size(c,1920,1080,3);}
    case 24->{check(c.currentScreen.width==640&&c.currentScreen.height==360,"actual GUI scale three at1920x1080");snapshot(c,"1920x1080-scale3-fit");System.out.println("RADIAL_A COMPLETE "+(single?"singleplayer":"multiplayer")+" checks="+checks);finished=true;}
    default->{}
   }
  });
 }
 private void observer(MinecraftClient c,PlayerProgress p){
  if(initial==null){c.setScreen(null);check(p.rank()==4,"B receives own rank-four snapshot");initial=p.toProperties();learned=Set.copyOf(p.learned());xp=p.totalXp();points=p.points();}
  check(p.learned().equals(learned)&&p.totalXp().equals(xp)&&p.points().equals(points)&&p.rank()==4,"A kills/reset/reload cannot alter B snapshot");
  if(++observerTicks>=55){System.out.println("RADIAL_B COMPLETE multiplayer checks="+checks);finished=true;}
 }
}

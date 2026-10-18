package dk.starswordmaster.client;
import dk.starswordmaster.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import org.lwjgl.glfw.GLFW;
public final class TreePerfClient implements ClientModInitializer {
 private int ticks,stage;private boolean started,opened,finished;
 private Object field(Object o,String name){try{var f=o.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(o);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 @Override public void onInitializeClient(){if(!TreePerfCapture.ENABLED)return;
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.options.getMaxFps().setValue(120);c.getTutorialManager().setStep(TutorialStep.NONE);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{if(finished)return;if(++ticks>5000)throw new AssertionError("TREE_PERF timeout");
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;c.createIntegratedServerLoader().start("TreePerfSmoke",()->c.setScreen(new TitleScreen()));}
   if(c.player==null||!c.player.isLoaded()||!StarSwordmasterClient.ready||StarSwordmasterClient.data.rank()!=4)return;
   GLFW.glfwSetCursorPos(c.getWindow().getHandle(),c.getWindow().getWidth()-2,c.getWindow().getHeight()-2);
   if(!opened){opened=true;c.setScreen(null);c.getWindow().setWindowedSize(1920,1080);c.options.getGuiScale().setValue(2);c.onResolutionChanged();c.setScreen(new SkillTreeScreen());TreePerfCapture.start("fit");return;}
   if(!TreePerfCapture.done())return;
   ScreenshotRecorder.saveScreenshot(c.runDirectory,"tree-perf-"+System.getProperty("starsmoke.treeperf.label","baseline")+"-"+TreePerfCapture.phase+".png",c.getFramebuffer(),1,m->{});
   var screen=c.currentScreen;var view=(TreeViewport)field(screen,"view");var layout=(TreeScreenLayout)field(screen,"layout");
   switch(stage++) {case 0->{view.focus(SkillTreeLayout.Sector.SWORD,layout.treeWidth(),layout.bodyBottom()-layout.bodyTop());TreePerfCapture.start("focus");}case 1->{view.pan(-240,80);TreePerfCapture.start("pan");}default->{System.out.println("TREE_PERF COMPLETE label="+System.getProperty("starsmoke.treeperf.label","baseline"));finished=true;}}
  });
 }
}

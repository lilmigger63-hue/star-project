package dk.starswordmaster.smoke;
import dk.starswordmaster.client.*;
import dk.starswordmaster.ui.StatusData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.gui.Click;
import dk.starswordmaster.ui.StatusLayout;
import java.util.*;
import org.lwjgl.glfw.GLFW;
public class StatusSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait;
 private boolean started;
 private KeyBinding status,tree;
 private InputUtil.Key original;
 private double mana;
 private Set<String> learned;
 private void check(boolean value,String name){if(!value)throw new AssertionError("STATUS_SMOKE FAIL: "+name);System.out.println("STATUS_SMOKE PASS: "+name);}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.status"))return;
  ClientTickEvents.END_CLIENT_TICK.register(client->{
   if(!started&&++ticks>100&&client.currentScreen!=null){started=true;client.createIntegratedServerLoader().start("StatusSmoke",()->client.setScreen(new TitleScreen()));}
   if(client.player==null||client.world==null||wait++<20)return;wait=0;
   switch(stage++){
    case 0->{
     status=Arrays.stream(client.options.allKeys).filter(k->k.getId().equals("key.starswordmaster.status")).findFirst().orElseThrow();
     tree=Arrays.stream(client.options.allKeys).filter(k->k.getId().equals("key.starswordmaster.tree")).findFirst().orElseThrow();
     check(status.getDefaultKey().getCode()==GLFW.GLFW_KEY_H,"H default registered");
     check(Arrays.stream(client.options.allKeys).filter(k->k.getDefaultKey().getCode()==GLFW.GLFW_KEY_H).count()==1,"H has no vanilla or Better Combat conflict");
     check(tree.getDefaultKey().getCode()==GLFW.GLFW_KEY_K,"K tree binding preserved");
     original=KeyBindingHelper.getBoundKeyOf(status);StarSwordmasterClient.ready=false;StarSwordmasterClient.data=null;client.setScreen(new StatusScreen());
    }
    case 1->{check(client.currentScreen instanceof StatusScreen,"missing data screen did not crash");check(StarSwordmasterClient.ready&&StarSwordmasterClient.data!=null,"late snapshot recovered automatically");var data=StatusData.from(StarSwordmasterClient.data,true).orElseThrow();check(data.level()==10&&data.xpInLevel()==5&&data.xpForNextLevel()==250,"screen receives actual XP");check(data.active().size()==4&&data.passive().size()==1,"learned abilities correctly grouped");mana=data.mana();learned=Set.copyOf(data.active());client.getWindow().setWindowedSize(1280,800);}
    case 2->{check(StatusData.from(StarSwordmasterClient.data,true).orElseThrow().mana()>mana,"resources continue updating while open");check(new HashSet<>(StatusData.from(StarSwordmasterClient.data,true).orElseThrow().active()).equals(learned),"viewing does not change learned skills");client.currentScreen.mouseScrolled(client.currentScreen.width/2.0,client.currentScreen.height/2.0,0,-12);}
    case 3->{client.currentScreen.keyPressed(new KeyInput(GLFW.GLFW_KEY_HOME,0,0));System.out.println("STATUS_SMOKE: wide status screenshot ready");}
    case 7->{client.currentScreen.close();KeyBinding.onKeyPressed(original);}
    case 8->{check(client.currentScreen instanceof StatusScreen,"H opens independent status screen");client.currentScreen.keyPressed(new KeyInput(GLFW.GLFW_KEY_ESCAPE,0,0));check(client.currentScreen==null,"Escape closes status screen");KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(tree));}
    case 9->{check(client.currentScreen instanceof SkillTreeScreen,"K still opens original skill tree");client.currentScreen.close();status.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_J));KeyBinding.updateKeysByCode();KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(status));}
    case 10->{check(client.currentScreen instanceof StatusScreen,"status key can be rebound to J");status.setBoundKey(original);KeyBinding.updateKeysByCode();client.getWindow().setWindowedSize(640,480);client.options.getGuiScale().setValue(2);client.onResolutionChanged();}
    case 11->{
     var screen=client.currentScreen;var layout=StatusLayout.forScreen(screen.width,screen.height);
     check(!layout.twoColumns(),"small GUI uses one column");screen.keyPressed(new KeyInput(GLFW.GLFW_KEY_END,0,0));
     double x=layout.x()+layout.width()-6;var click=new Click(x,layout.bodyBottom()-2,new MouseInput(0,0));
     check(screen.mouseClicked(click,false),"scrollbar accepts click");
     check(screen.mouseDragged(new Click(x,layout.bodyTop()+2,new MouseInput(0,0)),0,-100),"scrollbar accepts drag");
     check(screen.mouseReleased(click),"scrollbar releases drag");
     screen.keyPressed(new KeyInput(GLFW.GLFW_KEY_HOME,0,0));System.out.println("STATUS_SMOKE: compact status screenshot ready");
    }
    case 30->{client.getWindow().setWindowedSize(1280,800);client.options.getGuiScale().setValue(1);client.onResolutionChanged();}
    case 32->{client.currentScreen.keyPressed(new KeyInput(GLFW.GLFW_KEY_HOME,0,0));System.out.println("STATUS_SMOKE COMPLETE");}
    default->{}
   }
  });
 }
}

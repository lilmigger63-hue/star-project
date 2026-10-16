package dk.starswordmaster.client;
import dk.starswordmaster.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.lwjgl.glfw.GLFW;
/** Actual native geometry, read-only rendering, chat hit targets and visibility on a local test world. */
public class HudSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait,checks;private boolean started,finished,checkedRender,forceFallback,lastVisible;private Properties before;private double mana,stamina,health;
 private void check(boolean value,String name){if(!value)throw new AssertionError("HUD_SMOKE FAIL: "+name+" stage="+stage);checks++;System.out.println("HUD_SMOKE PASS: "+name);}
 private void size(MinecraftClient c,int w,int h,int scale){c.getWindow().setWindowedSize(w,h);GLFW.glfwSetWindowPos(c.getWindow().getHandle(),0,0);c.options.getGuiScale().setValue(scale);c.onResolutionChanged();}
 private void screenshot(MinecraftClient c,String name){ScreenshotRecorder.saveScreenshot(c.runDirectory,"hud-038-"+name+".png",c.getFramebuffer(),1,message->{});}
 private void player(MinecraftClient c,Consumer<ServerPlayerEntity> action){var id=c.player.getUuid();c.getServer().execute(()->action.accept(c.getServer().getPlayerManager().getPlayer(id)));}
 private HudSmokeCapture.Rect texture(String id){var matches=HudSmokeCapture.textures.get(id);if(matches==null||matches.isEmpty())throw new AssertionError("HUD_SMOKE missing native texture "+id+" captured="+HudSmokeCapture.textures.keySet());return matches.getFirst();}
 private void bounds(MinecraftClient c){
  int w=c.getWindow().getScaledWidth(),h=c.getWindow().getScaledHeight();var s=SkillHudLayout.forScreen(w,h);var r=SkillHud.resourceLayout(w,h);var hotbar=texture("hud/hotbar");var selected=texture("hud/hotbar_selection");var xp=texture("hud/experience_bar_background");
  check(s.x()==w/2-91&&hotbar.x()==s.x()&&selected.y()==s.y()+22,"actual selected-slot outer edge touches centered skills at "+w+"x"+h);
  check(r.x()==6&&r.y()>=6&&r.x()+176<=w-6,"resources dock at left screen edge");
  check(r.stacked()?r.y()+40<=xp.y()-30:r.x()+176<=s.x()-8,"resources clear compact native group and hotbars");
  var food=HudSmokeCapture.textures.entrySet().stream().filter(e->e.getKey().startsWith("hud/food_")).flatMap(e->e.getValue().stream()).toList();
  check(!food.isEmpty()&&food.stream().allMatch(f->f.y()+f.height()<=xp.y()),"actual hunger textures are above native XP");
  check(xp.y()+xp.height()<=s.y()-2&&s.y()-(xp.y()+xp.height())<=8,"native XP group is compact above skills");
 }
 private void open(MinecraftClient c,String id){var key=Arrays.stream(c.options.allKeys).filter(k->k.getId().equals("key.starswordmaster."+id)).findFirst().orElseThrow();KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(key));}
 private void chat(MinecraftClient c,double scale){
  c.options.getChatScale().setValue(scale);c.options.getChatHeightFocused().setValue(1.0);c.setScreen(new ChatScreen("",false));
  var chat=c.inGameHud.getChatHud();chat.clear(false);chat.addMessage(Text.literal("HUD layout link").setStyle(Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand("/hud-test"))));
 }
 private void checkChat(MinecraftClient c,double scale){
  int h=c.getWindow().getScaledHeight(),lift=SkillHud.chatLift();var hud=c.inGameHud.getChatHud();
  var style=hud.getTextStyleAt(10,h-40-lift-4*scale);
  check(style!=null&&style.getClickEvent() instanceof ClickEvent.SuggestCommand,"chat hit target follows rendered text at chat scale "+scale);
  check(hud.getTextStyleAt(10,h-44)==null,"old chat position has no stale click target");
  check(h-40-lift-hud.getVisibleLineCount()*(int)(9*(c.options.getChatLineSpacing().getValue()+1))*scale>=0,"chat viewport stays inside screen");screenshot(c,"chat-"+scale);
 }
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.hud"))return;
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);
   HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR,Identifier.of("starsmoke","hud_capture"),(context,tick)->{HudSmokeCapture.textures.clear();if(forceFallback)StarSwordmasterClient.ready=false;});
   var skills=Identifier.of("starswordmaster","skills");
   HudElementRegistry.attachElementBefore(skills,Identifier.of("starsmoke","before_skills"),(context,tick)->before=StarSwordmasterClient.ready?StarSwordmasterClient.data.toProperties():null);
   HudElementRegistry.attachElementAfter(skills,Identifier.of("starsmoke","after_skills"),(context,tick)->{lastVisible=SkillHud.visible(c);if(before!=null){if(!before.equals(StarSwordmasterClient.data.toProperties()))throw new AssertionError("HUD_SMOKE FAIL: rendering changed player data");checkedRender=true;}});
  });
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   if(!finished&&++ticks>5000)throw new AssertionError("HUD_SMOKE timeout at stage "+stage);
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;c.createIntegratedServerLoader().start("HudSmoke",()->c.setScreen(new TitleScreen()));}
   if(finished||c.player==null||(!StarSwordmasterClient.ready&&!forceFallback)||(stage!=3&&wait++<20))return;if(stage==3&&StarSwordmasterClient.data.cooldown("guard")==0)return;wait=0;
   switch(stage++){
    case 0->{c.setScreen(null);size(c,1280,720,2);player(c,p->{p.clearStatusEffects();p.setHealth(p.getMaxHealth()-4);p.getAbilities().invulnerable=true;p.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));p.equipStack(EquipmentSlot.CHEST,new ItemStack(Items.IRON_CHESTPLATE));p.setExperienceLevel(6);p.setExperiencePoints(10);p.getHungerManager().setFoodLevel(6);});int[] keys={GLFW.GLFW_KEY_Z,GLFW.GLFW_KEY_X,GLFW.GLFW_KEY_C,GLFW.GLFW_KEY_V,GLFW.GLFW_KEY_B};for(int i=0;i<5;i++)check(StarSwordmasterClient.keys[i].getDefaultKey().getCode()==keys[i],"slot key "+i+" preserved");}
    case 1->{bounds(c);check(StarSwordmasterClient.data.canCast(3),"existing Mending Light can cast");mana=StarSwordmasterClient.data.mana();health=c.player.getHealth();StarSwordmasterClient.send("cast","",3);}
    case 2->{check(StarSwordmasterClient.data.cooldown("mend")>0&&c.player.getHealth()>health&&StarSwordmasterClient.data.mana()<mana,"healing, mana and cooldown update from server");screenshot(c,"wide-scale2");mana=StarSwordmasterClient.data.mana();stamina=StarSwordmasterClient.data.stamina();StarSwordmasterClient.send("cast","",2);}
    case 3->{check(StarSwordmasterClient.data.mana()<mana&&StarSwordmasterClient.data.stamina()<stamina,"sword guard updates both actual resources");mana=StarSwordmasterClient.data.mana();stamina=StarSwordmasterClient.data.stamina();}
    case 4->{check(StarSwordmasterClient.data.mana()>mana&&StarSwordmasterClient.data.stamina()>stamina,"regeneration updates both meters");size(c,960,720,3);}
    case 5->{bounds(c);check(c.getWindow().getScaleFactor()==3,"GUI scale 3 applied");screenshot(c,"narrow-scale3");player(c,p->{p.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST,1200,4));p.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,1200,1));p.setAir(0);});}
    case 6->{bounds(c);var r=SkillHud.resourceLayout(c.getWindow().getScaledWidth(),c.getWindow().getScaledHeight());var armor=texture("hud/armor_full");check(r.y()+40<=armor.y()-4,"stacked panel clears actual expanded armor rows");var air=HudSmokeCapture.textures.entrySet().stream().filter(e->e.getKey().startsWith("hud/air")).flatMap(e->e.getValue().stream()).toList();check(!air.isEmpty()&&air.stream().allMatch(a->r.y()+40<=a.y()-4),"stacked panel clears native air bubbles");screenshot(c,"extra-health-air");chat(c,1);}
    case 7->{checkChat(c,1);c.options.getChatLineSpacing().setValue(1.0);chat(c,0.5);}
    case 8->{checkChat(c,0.5);c.setScreen(null);c.options.getChatScale().setValue(1.0);c.options.getChatLineSpacing().setValue(0.0);player(c,p->p.clearStatusEffects());size(c,853,481,1);}
    case 9->{bounds(c);screenshot(c,"odd-scale1");size(c,640,480,2);}
    case 10->{bounds(c);screenshot(c,"small-scale2");size(c,1120,800,2);}
    case 11->{bounds(c);check(SkillHud.resourceLayout(c.getWindow().getScaledWidth(),c.getWindow().getScaledHeight()).stacked(),"just below width threshold stacks");size(c,1124,800,2);}
    case 12->{bounds(c);check(!SkillHud.resourceLayout(c.getWindow().getScaledWidth(),c.getWindow().getScaledHeight()).stacked(),"width threshold returns to left-bottom dock");screenshot(c,"threshold-wide");c.options.hudHidden=true;}
    case 13->{check(!SkillHud.visible(c)&&SkillHud.chatLift()==0,"F1 hides custom HUD and restores native chat baseline");screenshot(c,"hidden");c.options.hudHidden=false;forceFallback=true;}
    case 14->{check(!lastVisible&&!SkillHud.visible(c)&&SkillHud.chatLift()==0,"missing snapshot restores vanilla HUD and chat");check(HudSmokeCapture.textures.keySet().stream().anyMatch(k->k.startsWith("hud/heart/")),"native hearts return while snapshot is missing");screenshot(c,"fallback");forceFallback=false;}
    case 15->{check(SkillHud.visible(c),"late snapshot restores resource HUD");player(c,p->p.changeGameMode(GameMode.SPECTATOR));}
    case 16->{check(c.player.isSpectator()&&!SkillHud.visible(c)&&SkillHud.chatLift()==0,"spectator preserves native HUD rules");screenshot(c,"spectator");player(c,p->p.changeGameMode(GameMode.SURVIVAL));}
    case 17->{check(SkillHud.visible(c),"survival restores custom HUD");size(c,640,480,2);HudSmokeCapture.itemName=null;player(c,p->{p.changeGameMode(GameMode.CREATIVE);p.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.DIAMOND_SWORD));});}
    case 18->{var r=SkillHud.resourceLayout(c.getWindow().getScaledWidth(),c.getWindow().getScaledHeight());var label=HudSmokeCapture.itemName;check(c.player.isCreative()&&label!=null&&label.y()+label.height()<=r.y()-2,"actual creative item label clears stacked resources");screenshot(c,"creative-item");player(c,p->p.changeGameMode(GameMode.SURVIVAL));}
    case 19->open(c,"status");
    case 20->{check(c.currentScreen instanceof StatusScreen,"H still opens status");c.currentScreen.close();open(c,"tree");}
    case 21->{check(c.currentScreen instanceof SkillTreeScreen,"K still opens skill tree");c.currentScreen.close();check(checkedRender,"HUD renders without mutating player data");System.out.println("HUD_SMOKE COMPLETE checks="+checks);finished=true;}
    default->{}
   }
  });
 }
}

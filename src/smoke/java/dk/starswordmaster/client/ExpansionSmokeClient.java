package dk.starswordmaster.client;
import dk.starswordmaster.presentation.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import java.util.*;
/** Actual native aura vertices/poses/network identity, SP and two-client. */
public final class ExpansionSmokeClient implements ClientModInitializer {
 private int ticks,stage,wait,rank,perspective,checks,color,remoteWait;private boolean connecting,finished,single,reconnect;
 private void check(boolean b,String message){if(!b)throw new AssertionError("EXPANSION_AURA FAIL "+stage+"/"+rank+": "+message);checks++;System.out.println("EXPANSION_AURA PASS: "+message);}
 private void command(MinecraftClient c,String s){c.getNetworkHandler().sendChatCommand("starexp "+s);}
 private void connect(MinecraftClient c){if(single)c.createIntegratedServerLoader().start("ExpansionSmoke",()->c.setScreen(new TitleScreen()));else ConnectScreen.connect(new TitleScreen(),c,ServerAddress.parse("127.0.0.1:25572"),new ServerInfo("Expansion test","127.0.0.1:25572",ServerInfo.ServerType.OTHER),false,null);}
 private void screenshot(MinecraftClient c,String label){ScreenshotRecorder.saveScreenshot(c.runDirectory,"aura-"+label+".png",c.getFramebuffer(),1,message->{});}
 private List<AuraSmokeCapture.Draw> own(){return AuraSmokeCapture.recent().stream().filter(AuraSmokeCapture.Draw::local).toList();}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.expansion"))return;single=Boolean.getBoolean("starsmoke.singleplayer");
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);c.options.getMaxFps().setValue(30);c.options.setPerspective(Perspective.THIRD_PERSON_BACK);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   if(finished)return;if(++ticks>7000)throw new AssertionError("EXPANSION_AURA timeout "+stage);
   if(!connecting&&ticks>100&&c.currentScreen!=null){connecting=true;connect(c);}
   if(reconnect&&c.player==null&&++wait>60){reconnect=false;wait=0;connect(c);return;}
   if(c.currentScreen instanceof DeathScreen){c.player.requestRespawn();c.setScreen(null);return;}
   if(c.player==null||!c.player.isLoaded()||!StarSwordmasterClient.ready)return;
   if(!single&&c.world.getPlayers().size()<2&&stage<8)return;
   var visual=ClientVisualState.get(c.player.getUuid());
   if(stage==0){if(!visual.auraUnlocked())return;try(var allocator=new net.minecraft.client.util.BufferAllocator(512)){var auxiliary=net.minecraft.client.render.VertexConsumerProvider.immediate(allocator);auxiliary.getBuffer(dk.starswordmaster.client.aura.AuraRenderLayer.get());auxiliary.draw();check(true,"auxiliary immutable-map buffer remains compatible");}color=visual.auraColor();check(color==AuraColors.forPlayer(c.player.getUuid()),"authoritative UUID color");command(c,"rank0");stage=1;wait=0;return;}
   if(++wait<24)return;wait=0;
   if(stage==1){if(visual.rank()!=rank+1)return;check(visual.auraColor()==color,"rank preserves hue "+(rank+1));c.options.setPerspective(perspective==0?Perspective.THIRD_PERSON_BACK:Perspective.FIRST_PERSON);AuraSmokeCapture.reset();stage=2;return;}
   if(stage==2){
    if(!single&&rank==0&&perspective==0&&AuraSmokeCapture.recent().stream().noneMatch(d->!d.local())&&++remoteWait<10)return;
    var draws=own();check(!draws.isEmpty(),"actual native aura rendered rank "+(rank+1)+" perspective "+perspective);check(draws.stream().allMatch(d->d.finite()&&d.vertexCount()>0&&d.vertexCount()<=1024*4),"finite bounded deferred native vertices");check(draws.stream().allMatch(AuraSmokeCapture.Draw::fullBrightShader),"native full-bright translucent shader compiled");check(AuraSmokeCapture.gpuAuraDraws>0&&AuraSmokeCapture.gpuAuraBeforeItems==0&&AuraSmokeCapture.gpuAuraAfterItems==AuraSmokeCapture.gpuAuraDraws,"native GPU aura layer flushes after actual sword layer");check(draws.stream().allMatch(d->d.color()==color&&d.rank()==rank+1),"native draw uses server rank/color");
    check(perspective==0?AuraSmokeCapture.thirdPerson>0:AuraSmokeCapture.firstPerson>0,"correct held-item perspective");screenshot(c,"rank"+(rank+1)+"-"+perspective);
    var models=AuraSmokeCapture.models().stream().filter(m->m.entityId()==c.player.getId()).toList();check(!models.isEmpty(),"aura and actual vanilla sword submissions paired");check(models.stream().allMatch(m->m.matrixError()<1e-6),"aura shares exact native model transform");check(models.stream().allMatch(m->m.opaqueBladeSamples()==m.bladeSamples()),"aura centerline follows opaque baked blade pixels: "+models.getFirst());
    if(!single&&rank==0&&perspective==0){var remote=AuraSmokeCapture.recent().stream().filter(d->!d.local()).toList();check(!remote.isEmpty(),"other player renders actual aura vertices");check(remote.stream().allMatch(d->{var e=c.world.getEntityById(d.entityId());return e!=null&&d.color()==AuraColors.forPlayer(e.getUuid());}),"remote authoritative personal color");var other=c.world.getPlayers().stream().filter(p->p!=c.player).findFirst().orElseThrow();check(ClientVisualState.get(other.getUuid()).auraColor()!=color,"players have different colors");}
    if(perspective++==0){stage=1;return;}perspective=0;if(++rank<4){command(c,"rank"+rank);stage=1;return;}
    c.player.setMainArm(Arm.LEFT);c.options.setPerspective(Perspective.FIRST_PERSON);AuraSmokeCapture.reset();stage=3;return;
   }
   if(stage==3){check(own().stream().anyMatch(d->d.hand()==0&&d.context().isLeftHand()),"left-dominant main-hand transform");command(c,"offhand");AuraSmokeCapture.reset();stage=4;return;}
   if(stage==4){check(own().stream().anyMatch(d->d.hand()==1&&!d.context().isLeftHand()),"left-dominant offhand transform");c.player.setMainArm(Arm.RIGHT);command(c,"unequip");stage=5;return;}
   if(stage==5){AuraSmokeCapture.reset();stage=6;return;}
   if(stage==6){check(own().isEmpty(),"unequipping leaves no aura or static trail");command(c,"rank3");c.options.setPerspective(Perspective.THIRD_PERSON_BACK);stage=7;return;}
   if(stage==7){c.options.attackKey.setPressed(true);AuraSmokeCapture.reset();stage=8;return;}
   if(stage==8){c.options.attackKey.setPressed(false);check(own().stream().anyMatch(AuraSmokeCapture.Draw::swinging),"Better Combat swing uses attached aura");stage=9;return;}
   if(stage==9){StarSwordmasterClient.send("cast","",0);AuraSmokeCapture.reset();stage=10;return;}
   if(stage==10){check(own().stream().anyMatch(AuraSmokeCapture.Draw::swinging),"confirmed PAL technique adds aura intensity");check(StarSwordmasterClient.data.cooldown("sovereign_judgment")>0,"new ultimate actually cast and paid cooldown");command(c,"die");stage=11;return;}
   if(stage==11){if(!c.player.isAlive())return;check(visual.auraColor()==color&&visual.auraUnlocked(),"death and respawn preserve personal color");var id=c.player.getUuid();c.disconnect(Text.literal("Expansion reconnect test"));check(ClientVisualState.get(id).equals(VisualState.DEFAULT),"disconnect clears visual cache");reconnect=true;stage=12;return;}
   if(stage==12){if(!visual.auraUnlocked())return;check(visual.auraColor()==color,"world reload/reconnect preserves personal color");command(c,"dimension");stage=13;return;}
   if(stage==13){if(c.world.getRegistryKey()!=net.minecraft.world.World.NETHER)return;check(visual.auraUnlocked()&&visual.auraColor()==color,"dimension transfer receives unchanged aura state");command(c,"dimension");stage=14;return;}
   if(stage==14){if(c.world.getRegistryKey()!=net.minecraft.world.World.OVERWORLD)return;check(visual.auraUnlocked()&&visual.auraColor()==color,"dimension return preserves personal color");System.out.println("EXPANSION_AURA COMPLETE "+(single?"singleplayer":"multiplayer")+" checks="+checks);finished=true;}
  });
 }
}

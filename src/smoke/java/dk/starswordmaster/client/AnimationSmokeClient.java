package dk.starswordmaster.client;
import dk.starswordmaster.presentation.*;
import dk.starswordmaster.network.AbilityPresentationPayload;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.animation.*;
import net.bettercombat.client.animation.AttackAnimationStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.lang.reflect.*;
import java.util.*;
/** Deterministic real PAL/Better Combat SP and two-client checks. Not in release. */
public class AnimationSmokeClient implements ClientModInitializer {
 private record Case(String id,String mode){}
 private final List<Case> cases=new ArrayList<>();private final Set<String> observed=new HashSet<>();private final Set<AbilityPresentation.Element> screenshots=new HashSet<>();
 private final Map<UUID,AbilityPresentationPayload> remoteActive=new HashMap<>();private boolean remoteCancelled;
 private int ticks,index,state,wait,checks;private boolean particlesSeen;private long beforeSequence;private boolean started,finished,single,attackSeen,boneSeen,ownB,remoteB,reconnecting;private String role;private Object removedClip;private float targetHealth;
 private void check(boolean value,String msg){if(!value)throw new AssertionError("ANIMATION_"+role+" FAIL: "+msg+" case="+index+" state="+state);checks++;System.out.println("ANIMATION_"+role+" PASS: "+msg);}
 private static Object field(Object object,String name){try{Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);}catch(Exception e){throw new AssertionError(e);}}
 @SuppressWarnings("unchecked") private static Map<UUID,Object> active(){try{Field f=AbilityPresentationClient.class.getDeclaredField("active");f.setAccessible(true);return (Map<UUID,Object>)f.get(null);}catch(Exception e){throw new AssertionError(e);}}
 @SuppressWarnings("unchecked") private long sequence(UUID id){try{Field f=AbilityPresentationClient.class.getDeclaredField("ledger");f.setAccessible(true);return ((Map<UUID,Long>)field(f.get(null),"seen")).getOrDefault(id,0L);}catch(Exception e){throw new AssertionError(e);}}
 private void command(MinecraftClient c,String text){c.getNetworkHandler().sendChatCommand("staranimtest "+text);}
 private void connect(MinecraftClient c){if(single){c.createIntegratedServerLoader().start("AnimationSmoke",()->c.setScreen(new TitleScreen()));return;}ConnectScreen.connect(new TitleScreen(),c,ServerAddress.parse("127.0.0.1:25570"),new ServerInfo("Local animation test","127.0.0.1:25570",ServerInfo.ServerType.OTHER),false,null);}
 private PlayerAnimationController controller(MinecraftClient c){return (PlayerAnimationController)PlayerAnimationAccess.getPlayerAnimationLayer(c.player,PalAbilityAnimations.LAYER);}
 private float targetHealth(MinecraftClient c){return c.world.getEntitiesByClass(ZombieEntity.class,c.player.getBoundingBox().expand(5),e->e.isAlive()).stream().findFirst().map(ZombieEntity::getHealth).orElse(-1f);}
 @SuppressWarnings("unchecked") private void clip(boolean remove,AbilityPresentation p){try{Field f=PlayerAnimResources.class.getDeclaredField("ANIMATIONS");f.setAccessible(true);var map=(Map<Identifier,Object>)f.get(null);var id=Identifier.of("starswordmaster",p.animationId());if(remove)removedClip=map.remove(id);else if(removedClip!=null){map.put(id,removedClip);removedClip=null;}}catch(Exception e){throw new AssertionError(e);}}
 private void duplicate(MinecraftClient c,Object running){try{var e=(AbilityPresentationPayload)field(running,"event");var method=AbilityPresentationClient.class.getDeclaredMethod("receive",MinecraftClient.class,AbilityPresentationPayload.class);method.setAccessible(true);method.invoke(null,c,e);method.invoke(null,c,e);check(active().get(c.player.getUuid())==running,"duplicate confirmed packets do not replay cast");}catch(Exception e){throw new AssertionError(e);}}
 private boolean customParticles(MinecraftClient c){
  if(((java.util.Queue<?>)field(c.particleManager,"newParticles")).stream().anyMatch(x->x.getClass().getName().contains("FantasyParticles$InkParticle")))return true;
  for(var renderer:((Map<?,?>)field(c.particleManager,"particles")).values())for(Class<?> type=renderer.getClass();type!=null;type=type.getSuperclass())for(var f:type.getDeclaredFields())try{f.setAccessible(true);if(f.get(renderer) instanceof Iterable<?> values)for(var value:values)if(value.getClass().getName().contains("FantasyParticles$InkParticle"))return true;}catch(IllegalAccessException e){throw new AssertionError(e);}
  return false;
 }
 private void verifyBudgetAndSprites(MinecraftClient c){
  for(var ink:ParticlePattern.Ink.values())check(FantasyParticles.emit(c,ink,0xFFFFFF,c.player.getEntityPos().add(0,3,0),net.minecraft.util.math.Vec3d.ZERO,2,0.08f,0),"native atlas loaded pixel sprite "+ink);
  try{
   var b=AbilityPresentationClient.class.getDeclaredField("budget");b.setAccessible(true);var budget=(ParticleBudget)b.get(null);budget.reset();
   var emit=AbilityPresentationClient.class.getDeclaredMethod("emit",MinecraftClient.class,AbilityPresentation.class,ParticlePattern.Point.class,net.minecraft.util.math.Vec3d.class,net.minecraft.util.math.Vec3d.class);emit.setAccessible(true);
   var queue=(java.util.Queue<?>)field(c.particleManager,"newParticles");var point=new ParticlePattern.Point(0,0,0,ParticlePattern.Space.BODY,ParticlePattern.Ink.STREAK,0.08f,2,0,0);var at=c.player.getEntityPos().add(0,4,0);var style=AbilityPresentation.ALL.get("quick_cut");
   c.options.getParticles().setValue(net.minecraft.particle.ParticlesMode.MINIMAL);int before=queue.size();emit.invoke(null,c,style,point,at,net.minecraft.util.math.Vec3d.ZERO);check(queue.size()==before&&budget.remaining()==256,"minimal particle setting respected without spending budget");c.options.getParticles().setValue(net.minecraft.particle.ParticlesMode.ALL);
   before=queue.size();for(int i=0;i<1000;i++)emit.invoke(null,c,style,point,at,net.minecraft.util.math.Vec3d.ZERO);
   check(queue.size()-before==256&&budget.remaining()==0,"real client limits 1000 emission requests to 256 actual particles");
  }catch(Exception e){throw new AssertionError(e);}
 }
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.animations"))return;single=Boolean.getBoolean("starsmoke.animations.singleplayer");role=System.getProperty("starsmoke.animations.role","A");
  cases.add(new Case("quick_cut","normalAttack"));AbilityPresentation.ALL.keySet().forEach(id->cases.add(new Case(id,"ok")));
  cases.add(new Case("heavy_cut","mana"));cases.add(new Case("heavy_cut","stamina"));cases.add(new Case("heavy_cut","cooldown"));cases.add(new Case("heavy_cut","weapon"));cases.add(new Case("spark","target"));cases.add(new Case("mend","fullhealth"));
  cases.add(new Case("ember_ring","damage"));cases.add(new Case("guard","switch"));cases.add(new Case("guard","attack"));cases.add(new Case("mend","vanillaAttack"));cases.add(new Case("mend","fallback"));cases.add(new Case("quick_cut","normalAttack"));cases.add(new Case("guardian_stance","death"));
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);c.options.setPerspective(Perspective.THIRD_PERSON_BACK);c.options.getParticles().setValue(net.minecraft.particle.ParticlesMode.ALL);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   ticks++;if(!finished&&ticks>11000)throw new AssertionError("ANIMATION_"+role+" timeout at "+index+"/"+state);
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;connect(c);}
   if(reconnecting&&c.player==null&&++wait>40){reconnecting=false;connect(c);wait=0;}
   if(finished||c.player==null||!StarSwordmasterClient.ready)return;
   if(c.currentScreen instanceof DeathScreen){c.player.requestRespawn();c.setScreen(null);return;}
   for(var prior:remoteActive.values())if(sequence(prior.caster())>prior.sequence()&&!active().containsKey(prior.caster())&&!remoteCancelled){remoteCancelled=true;check(true,"observer received server cancellation before natural expiry");}
   for(var r:active().values()){
    var e=(AbilityPresentationPayload)field(r,"event");if(!e.caster().equals(c.player.getUuid())&&(boolean)field(r,"started")){
     remoteActive.put(e.caster(),e);
     if(observed.add(e.ability())){var other=c.world.getPlayerByUuid(e.caster());var layer=PlayerAnimationAccess.getPlayerAnimationLayer(other,PalAbilityAnimations.LAYER);check(layer instanceof PlayerAnimationController pc&&(pc.getTriggeredAnimation()!=null||pc.isActive()),"observer PAL pose for "+e.ability());check(customParticles(c),"observer custom pixel VFX for "+e.ability());}
     if(e.ability().equals("guard")&&c.world.getPlayerByUuid(e.caster())!=null&&c.world.getPlayerByUuid(e.caster()).getName().getString().endsWith("B"))remoteB=true;
    }
   }
   if(role.equals("B")){observer(c);return;}
   if(!single&&c.getNetworkHandler().getPlayerList().size()<2)return;
   if(index==cases.size()){
    if(!single&&!remoteB)return;
    check(single||remoteB,"other player's confirmed guard visible");command(c,"audit");
    c.disconnect(Text.literal("Animation lifecycle check"));check(active().isEmpty(),"disconnect clears all running poses");reconnecting=true;state=90;index++;wait=0;return;
   }
   if(state==90){if(++wait<40)return;check(active().isEmpty()&&!controller(c).isActive(),"reconnect/respawn leave no stuck animation");System.out.println("ANIMATION_A COMPLETE "+(single?"singleplayer":"multiplayer")+" checks="+checks);finished=true;return;}
   var test=cases.get(index);var p=AbilityPresentation.ALL.get(test.id());
   if(state==0){
    c.setScreen(null);c.options.attackKey.setPressed(false);c.player.getInventory().setSelectedSlot(0);
    if(index==0){
     verifyBudgetAndSprites(c);
     for(var presentation:AbilityPresentation.ALL.values())check(PlayerAnimResources.hasAnimation(Identifier.of("starswordmaster",presentation.animationId())),"PAL loaded dedicated clip "+presentation.abilityId());
     for(var value:AbilityPresentation.Clip.values())check(PlayerAnimResources.hasAnimation(Identifier.of("starswordmaster",value.id())),"PAL loaded original clip "+value);
     try{var receiver=AbilityPresentationClient.class.getDeclaredMethod("receive",MinecraftClient.class,AbilityPresentationPayload.class);receiver.setAccessible(true);UUID stale=UUID.randomUUID();receiver.invoke(null,c,new AbilityPresentationPayload(stale,-1,1,c.world.getTime()-30,"quick_cut",c.player.getEntityPos(),c.player.getEntityPos()));check(!active().containsKey(stale),"already completed delayed cast never replays");}catch(Exception e){throw new AssertionError(e);}
     var manager=PlayerAnimationAccess.getPlayerAnimManager(c.player);var own=controller(c);var bc=PlayerAnimationAccess.getPlayerAnimationLayer(c.player,AttackAnimationStack.ID);int a=-99999,b=-99999;
     for(var layer:manager.getLayers()){if(layer.right()==own)a=layer.left();if(layer.right()==bc)b=layer.left();}
     check(a==b-1,"PAL ability layer below Better Combat attacks: "+a+" / "+b);
    }
    command(c,"prepare "+test.id()+" "+(test.mode().equals("vanillaAttack")?"unarmed":Set.of("normalAttack","damage","switch","attack","fallback","death").contains(test.mode())?"ok":test.mode()));state=1;wait=0;return;
   }
   if(state==1){
    if(++wait<10||!StarSwordmasterClient.data.slot(0).equals(test.id()))return;
    beforeSequence=sequence(c.player.getUuid());attackSeen=false;boneSeen=false;particlesSeen=false;
    if(test.mode().equals("normalAttack")){targetHealth=targetHealth(c);c.options.attackKey.setPressed(true);state=10;wait=0;return;}
    if(test.id().equals("quick_cut")&&test.mode().equals("ok"))c.player.setMainArm(net.minecraft.util.Arm.LEFT);
    else c.player.setMainArm(net.minecraft.util.Arm.RIGHT);
    if(test.mode().equals("fallback"))clip(true,p);
    StarSwordmasterClient.send("cast","",0);state=2;wait=0;return;
   }
   if(state==10){
    attackSeen|=PalAbilityAnimations.attackActive(c.player);if(++wait<25)return;c.options.attackKey.setPressed(false);
    check(attackSeen,"ordinary Better Combat attack animation before/after ability suite");check(targetHealth(c)<targetHealth,"ordinary Better Combat attack still damages hostile");state=4;wait=0;return;
   }
   if(state==2){
    wait++;var running=active().get(c.player.getUuid());
    if(Set.of("mana","stamina","cooldown","weapon","target","fullhealth").contains(test.mode())){
     if(wait<14)return;check(sequence(c.player.getUuid())==beforeSequence&&running==null,"rejected "+test.mode()+" cast has no event, pose or particles");state=4;wait=0;return;
    }
    if(running==null||!(boolean)field(running,"started")){if(wait>20)check(false,"confirmed event missing/not started for "+test.id()+" paused="+c.isPaused()+" entity="+c.player.getId());return;}
    var e=(AbilityPresentationPayload)field(running,"event");if(!e.ability().equals(test.id()))return;
    check(sequence(c.player.getUuid())==beforeSequence+1,"one successful event for "+test.id());
    check((boolean)field(running,"started"),"client started confirmed "+test.id());
    particlesSeen=customParticles(c);
    if(test.mode().equals("fallback")){check(!controller(c).isActive(),"missing animation uses particle-only fallback");clip(false,p);}else check(controller(c).getTriggeredAnimation()!=null||controller(c).isActive(),"PAL playback for "+test.id());
    for(var modifier:controller(c).getModifiers())if(modifier instanceof com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier mirror)check(mirror.enabled==(c.player.getMainArm()==net.minecraft.util.Arm.LEFT),"animation follows current dominant hand");
    duplicate(c,running);
    state=3;wait=0;return;
   }
   if(state==3){
    particlesSeen|=customParticles(c);
    wait++;var running=active().get(c.player.getUuid());var bone=controller(c).getBone("right_arm");boneSeen|=bone!=null&&Math.abs(bone.rotX)>0.1;
    if(Set.of("damage","switch","attack","vanillaAttack","death").contains(test.mode())){
     if(wait==3){if(test.mode().equals("damage"))command(c,"damage");if(test.mode().equals("switch"))command(c,"weapon");if(test.mode().equals("attack")||test.mode().equals("vanillaAttack")){c.options.attackKey.setPressed(true);if(test.mode().equals("vanillaAttack"))net.minecraft.client.option.KeyBinding.onKeyPressed(net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.getBoundKeyOf(c.options.attackKey));}if(test.mode().equals("death"))command(c,"die");}
     attackSeen|=PalAbilityAnimations.attackActive(c.player)||c.player.handSwinging;
     if(running!=null){if(wait>12)check(false,"interruption did not release pose "+test.mode());return;}
     if(test.mode().equals("vanillaAttack"))check(attackSeen,"actual vanilla punch started");
     check(wait<p.ticks(),"early cancellation on "+test.mode());if(test.mode().equals("damage"))check(sequence(c.player.getUuid())==beforeSequence+2,"damage cancellation synchronized from server");c.options.attackKey.setPressed(false);state=4;wait=0;return;
    }
    if(wait<4)return;
    check(particlesSeen,"Minecraft particle manager received custom pixel "+test.id()+" VFX");
    if(!test.mode().equals("fallback"))check(boneSeen,"original clip changes right-arm pose for "+test.id());
    if(screenshots.add(p.element()))ScreenshotRecorder.saveScreenshot(c.runDirectory,"animation-"+p.element()+".png",c.getFramebuffer(),1,message->{});
    state=4;wait=0;return;
   }
   if(state==4){if(++wait<28||!c.player.isAlive())return;check(!active().containsKey(c.player.getUuid()),"presentation naturally released for "+test.id()+"/"+test.mode());index++;state=0;wait=0;}
  });
 }
 private void observer(MinecraftClient c){
  if(!observed.containsAll(AbilityPresentation.ALL.keySet()))return;
  if(state==0){check(observed.size()==32,"observer saw all 32 active ability events");command(c,"prepare guard ok");state=1;wait=0;return;}
  if(state==1){if(++wait<10)return;StarSwordmasterClient.send("cast","",0);state=2;wait=0;return;}
  if(state==2){var r=active().get(c.player.getUuid());if(r==null||!(boolean)field(r,"started")){if(++wait>30)check(false,"observer own cast missing/not initialized");return;}check(controller(c).getTriggeredAnimation()!=null||controller(c).isActive(),"observer also animates own confirmed cast");ownB=true;state=3;wait=0;}
  if(state==3&&++wait>30&&remoteCancelled){check(active().isEmpty(),"observer and own poses all expire");command(c,"audit");System.out.println("ANIMATION_B COMPLETE multiplayer checks="+checks);finished=true;}
 }
}

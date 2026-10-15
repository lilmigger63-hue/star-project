package dk.starswordmaster.client;
import dk.starswordmaster.network.AbilityPresentationPayload;
import dk.starswordmaster.presentation.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.*;
import net.minecraft.util.math.Vec3d;
import java.util.*;
/** Plays confirmed events once. Particles and PAL state never mutate combat data. */
public final class AbilityPresentationClient {
 private static final PresentationLedger ledger=new PresentationLedger();
 private static final Map<UUID,Running> active=new LinkedHashMap<>();
 private static ClientWorld world;
 private static int budget;
 private static final class Running {
  final AbilityPresentationPayload event;final AbilityPresentation style;final long received;
  PlayerEntity player;ItemStack weapon;int age,lastHurt;boolean started,wasSwinging;
  Running(AbilityPresentationPayload e,AbilityPresentation p,long received){event=e;style=p;this.received=received;}
 }
 private AbilityPresentationClient(){}
 public static void register(){
  PalAbilityAnimations.register();
  ClientPlayNetworking.registerGlobalReceiver(AbilityPresentationPayload.ID,(event,context)->context.client().execute(()->receive(context.client(),event)));
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->clear());
  BetterCombatClientEvents.ATTACK_START.register((player,hand)->cancel(player.getUuid()));
  ClientTickEvents.END_CLIENT_TICK.register(AbilityPresentationClient::tick);
 }
 private static void receive(MinecraftClient client,AbilityPresentationPayload e){
  if(client.world==null)return;
  if(world!=client.world){clear();world=client.world;}
  if(!ledger.accept(e.caster(),e.sequence(),e.sentAt(),world.getTime()))return;
  cancel(e.caster());if(e.ability().isEmpty())return;
  var style=AbilityPresentation.ALL.get(e.ability());
  if(style==null||style.elapsed(e.sentAt(),world.getTime())>=style.ticks()||!finite(e.origin())||!finite(e.impact()))return;
  if(active.size()>=64)cancel(active.keySet().iterator().next());
  active.put(e.caster(),new Running(e,style,world.getTime()));
 }
 private static boolean finite(Vec3d v){return Double.isFinite(v.x)&&Double.isFinite(v.y)&&Double.isFinite(v.z);}
 private static void tick(MinecraftClient client){
  if(world!=client.world){clear();world=client.world;}
  if(world==null||client.isPaused())return;
  budget=256;
  for(var iterator=active.values().iterator();iterator.hasNext();){
   var r=iterator.next();
   r.age=Math.max(r.age,r.style.elapsed(r.event.sentAt(),world.getTime()));
   if(r.age>=r.style.ticks()){stop(r);iterator.remove();continue;}
   var entity=world.getEntityById(r.event.entityId());
   if(!(entity instanceof PlayerEntity player)||!player.getUuid().equals(r.event.caster())){
    if(world.getTime()-r.received>5){stop(r);iterator.remove();}continue;
   }
   boolean firstFrame=!r.started;
   if(firstFrame){
    r.player=player;r.weapon=player.getMainHandStack().copy();r.lastHurt=player.hurtTime;r.wasSwinging=player.handSwinging;r.started=true;
    PalAbilityAnimations.play(player,r.style,r.age);
    // Existing damage/healing is instantaneous, so the impact is shown immediately.
    if(r.age<=2&&r.event.impact().squaredDistanceTo(r.event.origin().add(0,1,0))>1)
     for(int i=0;i<8;i++){double a=i*Math.PI/4;particle(client,r.style.element(),r.event.impact().add(Math.cos(a)*0.2,0,Math.sin(a)*0.2),new Vec3d(Math.cos(a)*0.025,0.025,Math.sin(a)*0.025));}
   }
   boolean interrupted=!player.isAlive()||player!=r.player||player.hurtTime>r.lastHurt||player.isSpectator()||!ItemStack.areItemsAndComponentsEqual(r.weapon,player.getMainHandStack())||(player.handSwinging&&!r.wasSwinging)||PalAbilityAnimations.attackActive(player);
   if(interrupted||r.age>=r.style.ticks()){stop(r);iterator.remove();continue;}
   r.lastHurt=player.hurtTime;r.wasSwinging=player.handSwinging;
   var base=(r.style.pattern()==AbilityPresentation.Pattern.BURST||r.style.pattern()==AbilityPresentation.Pattern.CAGE)?r.event.impact().subtract(0,1,0):player.getEntityPos();
   double yaw=Math.toRadians(-player.getYaw());
   if(firstFrame||r.age%2==0)for(var point:ParticlePattern.points(r.style,firstFrame&&r.style.pattern()==AbilityPresentation.Pattern.RAY?0:r.age)){
    Vec3d at;
    if(r.style.pattern()==AbilityPresentation.Pattern.RAY)at=r.event.origin().add(0,1.25,0).lerp(r.event.impact(),point.z());
    else at=base.add(point.x()*Math.cos(yaw)+point.z()*Math.sin(yaw),point.y(),-point.x()*Math.sin(yaw)+point.z()*Math.cos(yaw));
    particle(client,r.style.element(),at,new Vec3d(0,0.008,0));
   }
   r.age++;
  }
 }
 private static void particle(MinecraftClient client,AbilityPresentation.Element element,Vec3d at,Vec3d velocity){
  if(budget--<=0||client.player==null||client.player.squaredDistanceTo(at)>64*64)return;
  ParticleEffect effect=switch(element){case FIRE->ParticleTypes.SMALL_FLAME;case FROST->ParticleTypes.SNOWFLAKE;case STAR->ParticleTypes.END_ROD;case HEALING->ParticleTypes.HAPPY_VILLAGER;case STEEL->ParticleTypes.CRIT;};
  world.addParticleClient(effect,at.x,at.y,at.z,velocity.x,velocity.y,velocity.z);
 }
 private static void stop(Running running){if(running.player!=null)PalAbilityAnimations.stop(running.player);}
 private static void cancel(UUID caster){var running=active.remove(caster);if(running!=null)stop(running);}
 private static void clear(){active.values().forEach(AbilityPresentationClient::stop);active.clear();ledger.clear();world=null;}
}

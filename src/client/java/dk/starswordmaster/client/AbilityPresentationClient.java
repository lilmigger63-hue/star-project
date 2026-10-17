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
 private static final ParticleBudget budget=new ParticleBudget();
 private static final class Running {
  final AbilityPresentationPayload event;final AbilityPresentation style;final long received;
  PlayerEntity player;ItemStack weapon;int age,lastHurt;boolean started,wasSwinging;
  Running(AbilityPresentationPayload e,AbilityPresentation p,long received){event=e;style=p;this.received=received;}
 }
 private AbilityPresentationClient(){}
 public static void register(){
  PalAbilityAnimations.register();FantasyParticles.register();
  ClientPlayNetworking.registerGlobalReceiver(AbilityPresentationPayload.ID,(event,context)->context.client().execute(()->receive(context.client(),event)));
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->clear());
  BetterCombatClientEvents.ATTACK_START.register((player,hand)->cancel(player.getUuid()));
  ClientTickEvents.END_CLIENT_TICK.register(AbilityPresentationClient::tick);
 }
 private static void receive(MinecraftClient client,AbilityPresentationPayload e){
  if(client.world==null)return;
  if(world!=client.world){clear();world=client.world;}
  if(!ledger.accept(e.caster(),e.sequence(),e.sentAt(),world.getTime()))return;
  ClientVisualState.put(e.caster(),e.visual());
  cancel(e.caster());if(e.ability().isEmpty())return;
  var style=AbilityPresentation.ALL.get(e.ability());
  if(style==null||style.elapsed(e.sentAt(),world.getTime())>=style.ticks()||!finite(e.origin())||!finite(e.impact())||e.impacts().stream().anyMatch(p->!finite(p)))return;
  if(active.size()>=ParticleBudget.MAX_CASTERS)cancel(active.keySet().iterator().next());
  active.put(e.caster(),new Running(e,style,world.getTime()));
 }
 private static boolean finite(Vec3d v){return Double.isFinite(v.x)&&Double.isFinite(v.y)&&Double.isFinite(v.z);}
 private static void tick(MinecraftClient client){
  if(world!=client.world){clear();world=client.world;}
  if(world==null||client.isPaused())return;
  budget.reset();
  for(var iterator=active.values().iterator();iterator.hasNext();){
   var r=iterator.next();
   r.age=Math.max(r.age,r.style.elapsed(r.event.sentAt(),world.getTime()));
   if(r.age>=r.style.ticks()){stop(r);iterator.remove();continue;}
   var entity=world.getEntityById(r.event.entityId());
   if(!(entity instanceof PlayerEntity player)||!player.getUuid().equals(r.event.caster())){
    if(world.getTime()-r.received>5){stop(r);iterator.remove();}continue;
   }
   if(!player.isAlive()||player.isSpectator()){stop(r);iterator.remove();continue;}
   boolean firstFrame=!r.started;
   if(firstFrame){r.player=player;r.weapon=player.getMainHandStack().copy();r.lastHurt=player.hurtTime;r.wasSwinging=player.handSwinging;r.started=true;}
   boolean interrupted=player!=r.player||player.hurtTime>r.lastHurt||!ItemStack.areItemsAndComponentsEqual(r.weapon,player.getMainHandStack())||(player.handSwinging&&!r.wasSwinging)||PalAbilityAnimations.attackActive(player);
   if(interrupted){stop(r);iterator.remove();continue;}
   if(firstFrame){
    PalAbilityAnimations.play(player,r.style,r.age,r.event.visual().power());
    // Gameplay is still instantaneous: target accents appear on confirmation, never after cosmetic wind-up.
    if(r.age<=2&&r.event.impact().squaredDistanceTo(r.event.origin().add(0,1,0))>1){
     var accents=ParticlePattern.impact(r.style);for(int i=0;i<accents.size();i++){
      var point=VisualPowerScaling.scale(accents.get(i),r.event.visual().power());
      var target=r.event.impacts().size()>1?r.event.impacts().get(i%r.event.impacts().size()):r.event.impact();
      emit(client,r.style,point,target.add(point.x(),point.y(),point.z()),Vec3d.ZERO,r.event.visual());
     }
    }
   }
   r.lastHurt=player.hurtTime;r.wasSwinging=player.handSwinging;
   if(firstFrame||r.age%2==0)for(var raw:ParticlePattern.points(r.style,r.age)){
    var point=VisualPowerScaling.scale(raw,r.event.visual().power());
    Vec3d at=position(r,point,player);
    var drift=switch(point.ink()){case EMBER,SHARD,SHADOW->new Vec3d(0,.015,0);case DROPLET->new Vec3d(0,-.025,0);default->Vec3d.ZERO;};
    emit(client,r.style,point,at,drift,r.event.visual());
   }
   r.age++;
  }
 }
 private static Vec3d position(Running r,ParticlePattern.Point p,PlayerEntity player){
  double mirror=player.getMainArm()==net.minecraft.util.Arm.LEFT?-1:1;
  if(p.space()==ParticlePattern.Space.RAY||p.space()==ParticlePattern.Space.HAND){
   Vec3d start=r.event.origin().add(0,1.25,0),end=r.event.impact();
   double fraction=Math.clamp(p.z(),0,1);
   if(p.space()==ParticlePattern.Space.RAY&&r.event.ability().equals("chain_lightning")&&!r.event.impacts().isEmpty()){
    double segment=fraction*r.event.impacts().size();int index=Math.min(r.event.impacts().size()-1,(int)segment);
    start=index==0?start:r.event.impacts().get(index-1);end=r.event.impacts().get(index);fraction=Math.min(1,segment-index);
   }
   Vec3d forward=p.space()==ParticlePattern.Space.HAND?player.getRotationVec(1):end.subtract(start).normalize();
   Vec3d right=new Vec3d(forward.z,0,-forward.x);if(right.lengthSquared()<1e-8)right=new Vec3d(1,0,0);else right=right.normalize();
   Vec3d up=forward.crossProduct(right).normalize();
   var base=p.space()==ParticlePattern.Space.HAND?player.getEntityPos().add(0,1.25,0).add(forward.multiply(p.z())):start.lerp(end,fraction);
   return base.add(right.multiply(p.x()*mirror)).add(up.multiply(p.y()));
  }
  Vec3d base=switch(p.space()){case IMPACT->r.event.impact().subtract(0,1,0);case ORIGIN->r.event.origin();default->player.getEntityPos();};
  double yaw=Math.toRadians(-player.getYaw()),x=p.x()*mirror;
  return base.add(x*Math.cos(yaw)+p.z()*Math.sin(yaw),p.y(),-x*Math.sin(yaw)+p.z()*Math.cos(yaw));
 }
 private static void emit(MinecraftClient client,AbilityPresentation style,ParticlePattern.Point point,Vec3d at,Vec3d velocity){
  emit(client,style,point,at,velocity,VisualState.DEFAULT);
 }
 private static void emit(MinecraftClient client,AbilityPresentation style,ParticlePattern.Point point,Vec3d at,Vec3d velocity,VisualState visual){
  if(client.player==null)return;var mode=client.options.getParticles().getValue();
  double distance=client.player.squaredDistanceTo(at);
  if(mode==ParticlesMode.MINIMAL||(mode==ParticlesMode.DECREASED&&world.random.nextInt(3)==0)||(VfxQuality.level()==0&&world.random.nextBoolean())||(distance>1024&&world.random.nextBoolean())||!budget.take(distance))return;
  int color=ParticlePattern.color(style.element(),point.tone());var ability=dk.starswordmaster.progression.Ability.ALL.get(style.abilityId());
  if(visual.auraUnlocked()&&ability!=null&&ability.stamina()>0)color=point.tone()==0?AuraColors.shade(visual.auraColor(),.8f):AuraColors.highlight(visual.auraColor(),visual.rank()==4?.8f:.45f);
  if(FantasyParticles.emit(client,point.ink(),color,at,velocity,point.life(),point.size(),point.angle()))return;
  ParticleEffect fallback=switch(style.element()){case FIRE->ParticleTypes.SMALL_FLAME;case FROST->ParticleTypes.SNOWFLAKE;case STAR->ParticleTypes.END_ROD;case HEALING->ParticleTypes.HAPPY_VILLAGER;case STEEL->ParticleTypes.CRIT;case LIGHTNING->ParticleTypes.ELECTRIC_SPARK;case WATER->ParticleTypes.DRIPPING_WATER;case EARTH->ParticleTypes.CRIT;case DARK->ParticleTypes.PORTAL;};
  world.addParticleClient(fallback,at.x,at.y,at.z,velocity.x,velocity.y,velocity.z);
 }
 private static void stop(Running running){if(running.player!=null)PalAbilityAnimations.stop(running.player);}
 private static void cancel(UUID caster){var running=active.remove(caster);if(running!=null)stop(running);}
 private static void clear(){active.values().forEach(AbilityPresentationClient::stop);active.clear();ledger.clear();world=null;}
}

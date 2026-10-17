package dk.starswordmaster;

import dk.starswordmaster.progression.Ability;
import dk.starswordmaster.progression.PlayerProgress;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Server-only mechanics; the caller owns validation of progression, spending and presentation. */
public final class ExpandedAbilityEffects {
 private static final int MAX_TARGETS=8;
 private static final int MAX_CHAIN_TARGETS=5;
 private static final Set<String> IDS=Set.of(
  "static_spark","chain_lightning","thunder_step","heavens_wrath",
  "water_bolt","tidal_wave","aqua_blade","ocean_prison",
  "stone_spike","earthen_wall","quake_blade","worldbreaker",
  "shadow_bolt","curse_mark","shadow_edge","eclipse_nova",
  "astral_crescent","falling_star","phantom_blades","celestial_rush",
  "sovereign_judgment","heavenly_sword_domain");
 private ExpandedAbilityEffects() {}

 public record Result(boolean affected,Vec3d impact,List<Vec3d> impacts) {
  public Result {
   impacts=List.copyOf(impacts);
   if(impacts.size()>MAX_TARGETS)throw new IllegalArgumentException("Too many ability impacts");
  }
 }

 /** Returns null only for a legacy ability, which the original implementation must handle. */
 public static Result apply(ServerPlayerEntity player,PlayerProgress progress,Ability ability) {
  if(!IDS.contains(ability.id()))return null;
  if(ability.stamina()>0&&!player.getMainHandStack().isIn(ItemTags.SWORDS))return rejected(player);
  float scale=(float)progress.damageMultiplier(ability);
  return switch(ability.id()) {
   case "static_spark" -> single(player,16,3*scale,e->{});
   case "chain_lightning" -> chain(player,7*scale);
   case "thunder_step" -> thunderStep(player);
   case "heavens_wrath" -> storm(player,10*scale);
   case "water_bolt" -> single(player,16,4*scale,e->{});
   case "tidal_wave" -> tidalWave(player,5*scale);
   case "aqua_blade" -> single(player,4,7*scale,e->{});
   case "ocean_prison" -> single(player,16,6*scale,e->slow(e,100,3));
   case "stone_spike" -> single(player,16,5*scale,e->{});
   case "earthen_wall" -> earthenWall(player);
   case "quake_blade" -> area(player,player.getEntityPos(),4,8*scale,e->slow(e,60,1));
   case "worldbreaker" -> area(player,player.getEntityPos(),6,14*scale,e->slow(e,60,1));
   case "shadow_bolt" -> single(player,16,5*scale,e->{});
   case "curse_mark" -> single(player,16,3*scale,e->weaken(e,100));
   case "shadow_edge" -> single(player,4,8*scale,e->{});
   case "eclipse_nova" -> area(player,player.getEntityPos(),6,13*scale,e->weaken(e,60));
   case "astral_crescent" -> cleave(player,8*scale,false);
   case "falling_star" -> area(player,player.getEntityPos(),4,11*scale,e->slow(e,40,1));
   // The surrounding blade images are presentation only: exactly one authoritative hit.
   case "phantom_blades" -> single(player,16,10*scale,e->{});
   case "celestial_rush" -> cleave(player,8*scale,true);
   case "sovereign_judgment" -> single(player,5,16*scale,e->{});
   case "heavenly_sword_domain" -> area(player,player.getEntityPos(),6,15*scale,e->weaken(e,60));
   default -> throw new IllegalStateException("Unimplemented expanded ability "+ability.id());
  };
 }

 private static Result rejected(ServerPlayerEntity player) {
  return new Result(false,player.getEntityPos().add(0,1,0),List.of());
 }
 private static Result result(ServerPlayerEntity player,List<Vec3d> impacts) {
  return impacts.isEmpty()?rejected(player):new Result(true,impacts.getFirst(),impacts);
 }
 private static boolean hostile(ServerPlayerEntity player,HostileEntity target) {
  return target.isAlive()&&!target.isRemoved()&&!player.isTeammate(target)&&player.canSee(target);
 }
 private static Comparator<HostileEntity> nearest(Vec3d origin) {
  return Comparator.comparingDouble((HostileEntity e)->e.getEntityPos().squaredDistanceTo(origin))
   .thenComparingInt(HostileEntity::getId);
 }
 private static List<HostileEntity> nearby(ServerPlayerEntity player,Vec3d center,double radius,
                                           Predicate<HostileEntity> condition,int limit) {
  // Every search has a finite local box, a finite range and a bounded damage budget.
  return player.getEntityWorld().getEntitiesByClass(HostileEntity.class,new Box(center,center).expand(radius),
   e->hostile(player,e)&&e.getEntityPos().squaredDistanceTo(center)<=radius*radius&&condition.test(e))
   .stream().sorted(nearest(center)).limit(limit).toList();
 }
 private static HostileEntity target(ServerPlayerEntity player,double range) {
  Vec3d look=player.getRotationVec(1);
  return nearby(player,player.getEntityPos(),range,
   e->e.getEyePos().subtract(player.getEyePos()).normalize().dotProduct(look)>.65,1)
   .stream().findFirst().orElse(null);
 }
 private static boolean hit(ServerPlayerEntity player,HostileEntity target,float damage) {
  return target.damage(player.getEntityWorld(),player.getDamageSources().indirectMagic(player,player),damage);
 }
 private static Vec3d impact(HostileEntity target) {return target.getEntityPos().add(0,1,0);}
 private static void slow(HostileEntity target,int ticks,int amplifier) {
  target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,ticks,amplifier));
 }
 private static void weaken(HostileEntity target,int ticks) {
  target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,ticks,0));
 }
 private static Result single(ServerPlayerEntity player,double range,float damage,Consumer<HostileEntity> afterHit) {
  var target=target(player,range);
  if(target==null||!hit(player,target,damage))return rejected(player);
  afterHit.accept(target);return result(player,List.of(impact(target)));
 }
 private static Result damageTargets(ServerPlayerEntity player,List<HostileEntity> targets,float damage,
                                     Consumer<HostileEntity> afterHit) {
  var impacts=new ArrayList<Vec3d>(MAX_TARGETS);
  for(var target:targets)if(hit(player,target,damage)) {
   afterHit.accept(target);impacts.add(impact(target));
   if(impacts.size()==MAX_TARGETS)break;
  }
  return result(player,impacts);
 }
 private static Result area(ServerPlayerEntity player,Vec3d center,double radius,float damage,
                            Consumer<HostileEntity> afterHit) {
  return damageTargets(player,nearby(player,center,radius,e->true,MAX_TARGETS),damage,afterHit);
 }
 private static Result storm(ServerPlayerEntity player,float damage) {
  var target=target(player,16);if(target==null)return rejected(player);
  Vec3d center=target.getEntityPos();
  var result=area(player,center,5,damage,e->slow(e,80,1));
  return result.affected()?new Result(true,center.add(0,1,0),result.impacts()):result;
 }
 private static Result chain(ServerPlayerEntity player,float damage) {
  var current=target(player,16);if(current==null)return rejected(player);
  var visited=new HashSet<Integer>();var impacts=new ArrayList<Vec3d>(MAX_CHAIN_TARGETS);
  for(int jump=0;jump<MAX_CHAIN_TARGETS&&current!=null;jump++) {
   visited.add(current.getId());
   if(!hit(player,current,damage))break;
   impacts.add(impact(current));damage*=.8f;
   HostileEntity previous=current;
   current=nearby(player,previous.getEntityPos(),5,
    e->!visited.contains(e.getId())&&previous.canSee(e),1).stream().findFirst().orElse(null);
  }
  return result(player,impacts);
 }
 private static Vec3d forward(ServerPlayerEntity player) {
  Vec3d look=player.getRotationVec(1);Vec3d horizontal=new Vec3d(look.x,0,look.z);
  return horizontal.lengthSquared()<1e-6?Vec3d.ZERO:horizontal.normalize();
 }
 private static boolean canDash(ServerPlayerEntity player,Vec3d direction) {
  return direction.lengthSquared()>0&&!player.hasVehicle()
   &&player.getEntityWorld().isSpaceEmpty(player,player.getBoundingBox().stretch(direction.multiply(1.2)));
 }
 private static void dash(ServerPlayerEntity player,Vec3d direction) {
  player.addVelocity(direction.multiply(1.05).add(0,.08,0));player.velocityModified=true;
 }
 private static Result thunderStep(ServerPlayerEntity player) {
  Vec3d direction=forward(player);if(!canDash(player,direction))return rejected(player);
  dash(player,direction);
  player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,40,0));
  player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,40,0));
  return new Result(true,player.getEntityPos().add(0,1,0),List.of());
 }
 private static Result earthenWall(ServerPlayerEntity player) {
  var resistance=player.getStatusEffect(StatusEffects.RESISTANCE);
  var absorption=player.getStatusEffect(StatusEffects.ABSORPTION);
  boolean improveResistance=resistance==null||resistance.getAmplifier()==0&&resistance.getDuration()<100;
  boolean improveAbsorption=absorption==null||absorption.getAmplifier()==0&&absorption.getDuration()<80;
  if(!improveResistance&&!improveAbsorption)return rejected(player);
  boolean affected=false;
  if(improveResistance)affected|=player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,100,0));
  if(improveAbsorption)affected|=player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,80,0));
  return new Result(affected,player.getEntityPos().add(0,1,0),List.of());
 }
 private static Result cleave(ServerPlayerEntity player,float damage,boolean rush) {
  Vec3d direction=forward(player);
  if(direction.lengthSquared()==0||rush&&!canDash(player,direction))return rejected(player);
  var targets=nearby(player,player.getEntityPos(),5,e->{
   Vec3d offset=e.getEntityPos().subtract(player.getEntityPos());
   return new Vec3d(offset.x,0,offset.z).normalize().dotProduct(direction)>.35;
  },MAX_TARGETS);
  var result=damageTargets(player,targets,damage,e->{});
  if(rush&&result.affected())dash(player,direction);
  return result;
 }
 private static Result tidalWave(ServerPlayerEntity player,float damage) {
  Vec3d direction=forward(player);if(direction.lengthSquared()==0)return rejected(player);
  var targets=nearby(player,player.getEntityPos(),8,e->{
   Vec3d offset=e.getEntityPos().subtract(player.getEntityPos());double along=offset.dotProduct(direction);
   return along>0&&along<=8&&Math.abs(offset.y)<=3
    &&new Vec3d(offset.x,0,offset.z).subtract(direction.multiply(along)).lengthSquared()<=9;
  },MAX_TARGETS);
  return damageTargets(player,targets,damage,e->{
   e.addVelocity(direction.multiply(.35).add(0,.1,0));e.velocityModified=true;
  });
 }
}

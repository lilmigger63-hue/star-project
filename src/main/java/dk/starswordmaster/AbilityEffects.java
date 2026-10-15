package dk.starswordmaster;
import dk.starswordmaster.progression.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.effect.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.*;
import net.minecraft.util.math.Vec3d;
import java.util.*;
public final class AbilityEffects {
 private AbilityEffects(){}
 private static HostileEntity target(ServerPlayerEntity p,double range){
  Vec3d look=p.getRotationVec(1);
  return p.getEntityWorld().getEntitiesByClass(HostileEntity.class,p.getBoundingBox().expand(range),e->e.isAlive()&&p.canSee(e)&&e.squaredDistanceTo(p)<=range*range&&e.getEyePos().subtract(p.getEyePos()).normalize().dotProduct(look)>0.65)
   .stream().min(Comparator.comparingDouble(e->e.squaredDistanceTo(p))).orElse(null);
 }
 public static boolean cast(ServerPlayerEntity player,int slot){
  var progress=StarSwordmaster.progress(player);
  if(!progress.canCast(slot))return false;
  var a=Ability.ALL.get(progress.slot(slot));
  if(a.stamina()>0&&!player.getMainHandStack().isIn(ItemTags.SWORDS))return false;
  HostileEntity target=null;
  String id=a.id();
  if(Set.of("spark","frost_touch","quick_cut","heavy_cut","riposte","aura_wave","fire_burst","ice_lance","flame_blade","frost_blade","rapid_slash","flame_lance","piercing_thrust","ice_prison").contains(id)){
   target=target(player,id.equals("piercing_thrust")?5:id.equals("frost_touch")?8:a.stamina()>0&&!id.equals("aura_wave")?4:16);if(target==null)return false;
  }
  if((id.equals("mend")||id.equals("sanctuary")||id.equals("renew")||id.equals("restoration"))&&player.getHealth()>=player.getMaxHealth())return false;
  if(id.equals("light")&&player.hasStatusEffect(StatusEffects.NIGHT_VISION))return false;
  if(id.equals("guard")&&player.hasStatusEffect(StatusEffects.RESISTANCE))return false;
  if(id.equals("renew")&&player.hasStatusEffect(StatusEffects.REGENERATION))return false;
  if(id.equals("mirror_guard")&&player.hasStatusEffect(StatusEffects.ABSORPTION))return false;
  if(id.equals("frost_ward")&&player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))return false;
  if(id.equals("guardian_stance")&&player.hasStatusEffect(StatusEffects.RESISTANCE))return false;
  boolean affected=false;
  float scale=(float)progress.damageMultiplier(a);
  switch(id){
   case "spark"->{affected=hit(player,target,3*scale);if(affected)target.setOnFireFor(3);}
   case "frost_touch"->{affected=hit(player,target,2*scale);if(affected)slow(target,80);}
   case "light"->{player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION,900,0));affected=true;}
   case "mend"->{player.heal((float)(3*progress.healingMultiplier()));affected=true;}
   case "quick_cut"->{affected=hit(player,target,4*scale*(progress.style()==PlayerProgress.SwordStyle.QUICK?1.2f:1));}
   case "quick_dash"->{player.addVelocity(player.getRotationVec(1).multiply(0.75).add(0,0.12,0));player.velocityModified=true;affected=true;}
   case "heavy_cut"->{affected=hit(player,target,7*scale*(progress.style()==PlayerProgress.SwordStyle.HEAVY?1.2f:1));}
   case "heavy_sweep"->{affected=area(player,player.getEntityPos(),4,5*scale,false,false);}
   case "guard"->{player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,80,progress.style()==PlayerProgress.SwordStyle.DEFENSIVE?1:0));affected=true;}
   case "riposte"->{affected=hit(player,target,5*scale);if(affected)player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,40,0));}
   case "aura_wave"->{affected=hit(player,target,9*scale);}
   case "blade_focus"->{player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH,160,0));affected=true;}
   case "fire_burst"->{affected=area(player,target.getEntityPos(),3,6*scale,true,false);}
   case "ice_lance"->{affected=hit(player,target,8*scale);if(affected)slow(target,120);}
   case "sanctuary"->{player.heal((float)(6*progress.healingMultiplier()));player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,300,0));affected=true;}
   case "frost_nova"->{affected=area(player,player.getEntityPos(),6,4*scale,false,true);}
   case "flame_blade"->{affected=hit(player,target,6*scale);if(affected)target.setOnFireFor(5);}
   case "frost_blade"->{affected=hit(player,target,6*scale);if(affected)slow(target,140);}
   case "rapid_slash"->{affected=hit(player,target,5*scale);if(affected)target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,60,0));}
   case "crushing_edge"->{affected=area(player,player.getEntityPos(),4,9*scale,false,false);}
   case "mirror_guard"->{player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,140,0));player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,80,0));affected=true;}
   case "flame_lance"->{affected=hit(player,target,12*scale);if(affected)target.setOnFireFor(6);}
   case "frost_ward"->{player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,200,0));player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,100,0));affected=true;}
   case "arcane_step"->{player.addVelocity(player.getRotationVec(1).multiply(1.05).add(0,0.12,0));player.velocityModified=true;player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,40,0));affected=true;}
   case "glacier_sweep"->{affected=area(player,player.getEntityPos(),5,5*scale,false,true);}
   case "renew"->{player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,160,0));affected=true;}
   case "piercing_thrust"->{affected=hit(player,target,6*scale);}
   case "seismic_cut"->{affected=area(player,player.getEntityPos(),5,8*scale,false,true);}
   case "guardian_stance"->{player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,100,progress.style()==PlayerProgress.SwordStyle.DEFENSIVE?1:0));player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,100,0));affected=true;}
   case "ember_ring"->{affected=area(player,player.getEntityPos(),5,7*scale,true,false);}
   case "ice_prison"->{affected=hit(player,target,5*scale);if(affected)target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,100,3));}
   case "restoration"->{player.heal((float)(4*progress.healingMultiplier()));player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,160,0));affected=true;}
   default->{return false;}
  }
  if(!affected)return false;
  progress.cast(slot);
  if(!id.equals("quick_dash")&&!id.equals("arcane_step")&&!id.equals("light"))StarSwordmaster.practice(player,a.skill(),10);
  if(target!=null||Set.of("heavy_sweep","frost_nova","fire_burst","crushing_edge","glacier_sweep","seismic_cut","ember_ring").contains(id))StarSwordmaster.combat(player);
  var world=player.getEntityWorld();Vec3d at=target==null?player.getEntityPos().add(0,1,0):target.getEntityPos().add(0,1,0);
  AbilityPresentationEvents.start(player,id,at);
  world.playSound(null,player.getX(),player.getY(),player.getZ(),a.stamina()>0?SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP:SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,SoundCategory.PLAYERS,0.7f,1.1f);
  return true;
 }
 private static void slow(HostileEntity e,int duration){e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,duration,1));}
 private static boolean hit(ServerPlayerEntity p,HostileEntity e,float amount){return e.damage(p.getEntityWorld(),p.getDamageSources().indirectMagic(p,p),amount);}
 private static boolean area(ServerPlayerEntity p,Vec3d center,double radius,float damage,boolean burn,boolean frost){
  boolean affected=false;
  for(var e:p.getEntityWorld().getEntitiesByClass(HostileEntity.class,new net.minecraft.util.math.Box(center,center).expand(radius),e->e.isAlive()&&e.getEntityPos().squaredDistanceTo(center)<=radius*radius&&p.canSee(e))){
   if(hit(p,e,damage)){affected=true;if(burn)e.setOnFireFor(4);if(frost)slow(e,100);}
  }
  return affected;
 }
}

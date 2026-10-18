package dk.starswordmaster;

import dk.starswordmaster.progression.KillExperience;
import dk.starswordmaster.progression.ProgressionConfig;
import dk.starswordmaster.progression.OwnershipPolicy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import java.math.BigInteger;

/** Server death callback policy. No client-supplied victim, player, health or reward enters this path. */
public final class KillRewards {
 private final KillExperience.DeathClaims deaths=new KillExperience.DeathClaims();
 public void clear(){deaths.clear();}
 public static ServerPlayerEntity creditedPlayer(DamageSource source){
  // Vanilla arrows and our indirectMagic(caster,caster) already expose the actual attacker here.
  if(source.getAttacker() instanceof ServerPlayerEntity player)return player;
  // Custom projectiles may supply their projectile as attacker instead of resolving its owner.
  if(source.getAttacker() instanceof ProjectileEntity projectile&&projectile.getOwner() instanceof ServerPlayerEntity player)return player;
  if(source.getAttacker()==null&&source.getSource() instanceof ProjectileEntity projectile&&projectile.getOwner() instanceof ServerPlayerEntity player)return player;
  return null;
 }
 public BigInteger claim(LivingEntity victim,ServerPlayerEntity killer,ProgressionConfig config){
  if(!config.killXpEnabled()||killer==null||!killer.isAlive()||killer.isSpectator())return BigInteger.ZERO;
  if(victim instanceof PlayerEntity||!(victim instanceof MobEntity)||victim.isRemoved()||victim.isAlive()||victim.isInvulnerable()||victim.isTeammate(killer))return BigInteger.ZERO;
  // Hostile ownership (e.g. evoker -> vex) remains eligible. Player-owned chains and pets do not.
  // Tameable references remain protective even when an offline owner cannot be resolved.
  if(OwnershipPolicy.<Entity>hasProtectedOwner(victim,
   entity->entity instanceof Tameable tameable?tameable.getOwner():entity instanceof Ownable ownable?ownable.getOwner():null,
   entity->entity instanceof PlayerEntity||entity.isTeammate(killer)||entity instanceof Tameable tameable&&tameable.getOwnerReference()!=null))return BigInteger.ZERO;
  if(!config.includePassiveMobs()&&!(victim instanceof Monster)&&victim.getType().getSpawnGroup()!=SpawnGroup.MONSTER)return BigInteger.ZERO;
  if(config.excludedEntityIds().contains(Registries.ENTITY_TYPE.getId(victim.getType()).toString()))return BigInteger.ZERO;
  BigInteger base=KillExperience.baseReward(victim.getAttributeValue(EntityAttributes.MAX_HEALTH),config.healthMultiplier());
  return base.signum()>0&&deaths.claim(victim)?base:BigInteger.ZERO;
 }
}

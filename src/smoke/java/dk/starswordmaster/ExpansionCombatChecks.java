package dk.starswordmaster;

import dk.starswordmaster.progression.Ability;
import dk.starswordmaster.progression.PlayerProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/** Native server checks. No test fixture is packaged in the production jar. */
public final class ExpansionCombatChecks {
 private ExpansionCombatChecks() {}
 private static void check(boolean value,String message) {
  if(!value)throw new AssertionError("EXPANSION_COMBAT FAIL: "+message);
  System.out.println("EXPANSION_COMBAT PASS: "+message);
 }
 private static void equal(double expected,double actual,String message) {
  check(Math.abs(expected-actual)<0.0002,message+" expected="+expected+" actual="+actual);
 }
 private static Ability ability(String id) {
  var ability=Ability.ALL.get(id);check(ability!=null,"registered "+id);return ability;
 }
 private static ZombieEntity zombie(ServerPlayerEntity player,List<Entity> fixtures,double x,double z) {
  var e=new ZombieEntity(EntityType.ZOMBIE,player.getEntityWorld());
  e.refreshPositionAndAngles(player.getX()+x,player.getY(),player.getZ()+z,0,0);
  e.setAiDisabled(true);e.setNoGravity(true);
  e.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(1000);
  e.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(0);e.setHealth(1000);
  check(player.getEntityWorld().spawnEntity(e),"spawn native hostile fixture");fixtures.add(e);return e;
 }
 private static void clear(List<Entity> fixtures) {fixtures.forEach(Entity::discard);fixtures.clear();}

 public static void run(ServerPlayerEntity player) {
  var position=player.getEntityPos();float yaw=player.getYaw(),pitch=player.getPitch();
  var velocity=player.getVelocity();var held=player.getMainHandStack().copy();
  boolean gravity=player.hasNoGravity();var effects=List.copyOf(player.getStatusEffects());
  var scoreboard=player.getEntityWorld().getScoreboard();var oldTeam=player.getScoreboardTeam();
  var team=scoreboard.addTeam("exp_"+java.util.UUID.randomUUID().toString().substring(0,8));
  var fixtures=new ArrayList<Entity>();
  try {
   // Keep horizontal coordinates in the already ticking player chunk: a synchronous getChunk at a
   // remote coordinate does not make its entity sections queryable until the chunk-status update.
   player.teleport(player.getEntityWorld(),position.x,160,position.z,Set.of(),0,0,false);
   player.setNoGravity(true);player.setVelocity(Vec3d.ZERO);player.clearStatusEffects();
   player.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));
   var progress=new PlayerProgress();
   var damage=new LinkedHashMap<String,Float>();
   damage.put("static_spark",3f);damage.put("chain_lightning",7f);
   damage.put("heavens_wrath",10f);damage.put("water_bolt",4f);damage.put("tidal_wave",5f);
   damage.put("aqua_blade",7f);damage.put("ocean_prison",6f);damage.put("stone_spike",5f);
   damage.put("quake_blade",8f);damage.put("worldbreaker",14f);damage.put("shadow_bolt",5f);
   damage.put("curse_mark",3f);damage.put("shadow_edge",8f);damage.put("eclipse_nova",13f);
   damage.put("astral_crescent",8f);damage.put("falling_star",11f);damage.put("phantom_blades",10f);
   damage.put("celestial_rush",8f);damage.put("sovereign_judgment",16f);damage.put("heavenly_sword_domain",15f);
   for(var entry:damage.entrySet()) {
    var target=zombie(player,fixtures,0,3);var a=ability(entry.getKey());
    check(player.getEntityWorld().getEntitiesByClass(ZombieEntity.class,player.getBoundingBox().expand(20),e->e==target).size()==1,"fixture is present in native entity index");
    double mana=progress.mana(),stamina=progress.stamina();
    var result=ExpandedAbilityEffects.apply(player,progress,a);
    check(result!=null&&result.affected(),"native success "+a.id());
    equal(entry.getValue()*progress.damageMultiplier(a),1000-target.getHealth(),"one actual damage calculation "+a.id());
    check(result.impacts().size()==1,"one actual impact "+a.id());
    equal(mana,progress.mana(),"helper leaves mana to common cast wrapper "+a.id());
    equal(stamina,progress.stamina(),"helper leaves stamina to common cast wrapper "+a.id());
    if(Set.of("heavens_wrath","ocean_prison","quake_blade","worldbreaker","falling_star").contains(a.id()))
     check(target.hasStatusEffect(StatusEffects.SLOWNESS),"actual slow "+a.id());
    if(Set.of("curse_mark","eclipse_nova","heavenly_sword_domain").contains(a.id()))
     check(target.hasStatusEffect(StatusEffects.WEAKNESS),"actual weakness "+a.id());
    if(a.id().equals("tidal_wave"))check(target.getVelocity().horizontalLengthSquared()>0,"actual tidal knockback");
    if(a.id().equals("celestial_rush"))check(player.getVelocity().horizontalLengthSquared()>0,"actual celestial dash");
    clear(fixtures);player.setVelocity(Vec3d.ZERO);
    check(!ExpandedAbilityEffects.apply(player,progress,a).affected(),"empty attack rejected "+a.id());
   }
   check(player.getVelocity().lengthSquared()==0,"empty celestial rush cannot dash");
   var step=ExpandedAbilityEffects.apply(player,progress,ability("thunder_step"));
   check(step.affected()&&player.getVelocity().horizontalLengthSquared()>0&&player.hasStatusEffect(StatusEffects.SPEED)&&player.hasStatusEffect(StatusEffects.RESISTANCE),"thunder step moves and applies protection");
   player.clearStatusEffects();
   check(ExpandedAbilityEffects.apply(player,progress,ability("earthen_wall")).affected(),"earthen wall grants protection");
   check(player.hasStatusEffect(StatusEffects.RESISTANCE)&&player.hasStatusEffect(StatusEffects.ABSORPTION),"actual earthen status effects");
   check(!ExpandedAbilityEffects.apply(player,progress,ability("earthen_wall")).affected(),"unchanged protection rejected");
   player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,200,1));
   player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,200,1));
   check(!ExpandedAbilityEffects.apply(player,progress,ability("earthen_wall")).affected(),"stronger existing protection rejected");
   check(player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier()==1&&player.getStatusEffect(StatusEffects.ABSORPTION).getAmplifier()==1,"protection never downgrades stronger effects");
   player.clearStatusEffects();player.setVelocity(Vec3d.ZERO);

   var pet=new WolfEntity(EntityType.WOLF,player.getEntityWorld());
   pet.refreshPositionAndAngles(player.getX(),player.getY(),player.getZ()+2,0,0);
   pet.setAiDisabled(true);pet.setNoGravity(true);pet.setOwner(player);pet.setTamed(true,true);
   player.getEntityWorld().spawnEntity(pet);fixtures.add(pet);float petHealth=pet.getHealth();
   for(String id:damage.keySet())check(!ExpandedAbilityEffects.apply(player,progress,ability(id)).affected(),"pet-only attack rejected "+id);
   equal(petHealth,pet.getHealth(),"all attacks preserve tamed pet health");clear(fixtures);
   var ally=zombie(player,fixtures,0,3);
   scoreboard.addScoreHolderToTeam(player.getNameForScoreboard(),team);
   scoreboard.addScoreHolderToTeam(ally.getNameForScoreboard(),team);
   for(String id:damage.keySet())check(!ExpandedAbilityEffects.apply(player,progress,ability(id)).affected(),"allied hostile excluded "+id);
   equal(1000,ally.getHealth(),"all attacks preserve allied hostile health");clear(fixtures);

   var chain=new ArrayList<ZombieEntity>();
   for(int i=0;i<7;i++)chain.add(zombie(player,fixtures,0,3+i*3));
   var chained=ExpandedAbilityEffects.apply(player,progress,ability("chain_lightning"));
   check(chained.affected()&&chained.impacts().size()==5,"chain capped at initial target plus four jumps");
   for(int i=0;i<7;i++) {
    equal(i<5?7*Math.pow(.8,i):0,1000-chain.get(i).getHealth(),"diminishing chain target "+i);
    if(i<5)check(chained.impacts().get(i).equals(chain.get(i).getEntityPos().add(0,1,0)),"actual ordered chain impact "+i);
   }
   clear(fixtures);
   var crowded=new ArrayList<ZombieEntity>();
   for(int i=0;i<12;i++)crowded.add(zombie(player,fixtures,(i%3-1)*.8,1.5+(i/3)*.6));
   var area=ExpandedAbilityEffects.apply(player,progress,ability("worldbreaker"));
   check(area.affected()&&area.impacts().size()==8&&crowded.stream().filter(e->e.getHealth()<1000).count()==8,"area capped at eight actual hits");
   clear(fixtures);
   var remote=zombie(player,fixtures,0,20);
   check(!ExpandedAbilityEffects.apply(player,progress,ability("static_spark")).affected()&&remote.getHealth()==1000,"ranged target outside sixteen blocks rejected");
   clear(fixtures);
   var behind=zombie(player,fixtures,0,-3);
   for(String id:List.of("static_spark","tidal_wave","astral_crescent","celestial_rush"))
    check(!ExpandedAbilityEffects.apply(player,progress,ability(id)).affected(),"forward attack excludes targets behind caster "+id);
   check(behind.getHealth()==1000,"forward attacks preserve rear hostile health");clear(fixtures);
   var swordTarget=zombie(player,fixtures,0,3);player.equipStack(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
   for(String id:damage.keySet())if(ability(id).stamina()>0)check(!ExpandedAbilityEffects.apply(player,progress,ability(id)).affected(),"sword tag required "+id);
   check(swordTarget.getHealth()==1000,"bare-handed sword attempts do no damage");
   check(ExpandedAbilityEffects.apply(player,progress,ability("spark"))==null,"legacy ability delegates to original implementation");
   System.out.println("EXPANSION_COMBAT COMPLETE: 22 actual effects, resource boundary, pets, targets, single hits and bounded damage");
  } finally {
   clear(fixtures);player.clearStatusEffects();effects.forEach(player::addStatusEffect);
   scoreboard.removeTeam(team);
   if(oldTeam!=null)scoreboard.addScoreHolderToTeam(player.getNameForScoreboard(),oldTeam);
   player.equipStack(EquipmentSlot.MAINHAND,held);player.setNoGravity(gravity);
   player.teleport(player.getEntityWorld(),position.x,position.y,position.z,Set.of(),yaw,pitch,false);player.setVelocity(velocity);
  }
 }
}

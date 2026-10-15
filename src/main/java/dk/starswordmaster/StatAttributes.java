package dk.starswordmaster;
import dk.starswordmaster.progression.PlayerProgress;
import net.minecraft.entity.attribute.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
/** Server-owned modifiers; vanilla synchronizes these tracked attributes to clients. */
public final class StatAttributes {
 public static final Identifier MINING_ID=Identifier.of("starswordmaster","stat_mining"),SPEED_ID=Identifier.of("starswordmaster","stat_speed"),HEALTH_ID=Identifier.of("starswordmaster","stat_health");
 private StatAttributes(){}
 public static void apply(ServerPlayerEntity player,PlayerProgress progress){
  var b=progress.bonuses();
  update(player.getAttributeInstance(EntityAttributes.BLOCK_BREAK_SPEED),MINING_ID,b.miningSpeed(),EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
  update(player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED),SPEED_ID,b.movementSpeed(),EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
  update(player.getAttributeInstance(EntityAttributes.MAX_HEALTH),HEALTH_ID,b.health(),EntityAttributeModifier.Operation.ADD_VALUE);
  if(player.getHealth()>player.getMaxHealth())player.setHealth(player.getMaxHealth());
 }
 static void update(EntityAttributeInstance attribute,Identifier id,double amount,EntityAttributeModifier.Operation operation){
  if(attribute==null)return;
  if(amount==0){if(attribute.hasModifier(id))attribute.removeModifier(id);return;}
  var modifier=new EntityAttributeModifier(id,amount,operation);
  // Health must load before vanilla restores saved current health (also on an alive respawn).
  boolean persistent=id.equals(HEALTH_ID);
  if(modifier.equals(attribute.getModifier(id))&&attribute.getPersistentModifiers().contains(modifier)==persistent)return;
  attribute.removeModifier(id);
  if(persistent)attribute.addPersistentModifier(modifier);else attribute.addTemporaryModifier(modifier);
 }
}

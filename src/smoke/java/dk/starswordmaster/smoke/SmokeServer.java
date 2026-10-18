package dk.starswordmaster.smoke;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.*;
import net.minecraft.entity.effect.*;
import java.util.*;
public class SmokeServer implements ModInitializer {
 private final Set<UUID> prepared=new HashSet<>();
 public void onInitialize(){if(Boolean.getBoolean("starsmoke.radial"))return;ServerTickEvents.END_SERVER_TICK.register(server->{for(var p:server.getPlayerManager().getPlayerList())if(p.getGameProfile().name().equals("StarSmoke")&&prepared.add(p.getUuid())){
  var world=p.getEntityWorld();world.getChunk(0,0);p.teleport(world,0.5,80,0.5,Set.of(),0,0,false);p.setNoGravity(true);p.setHealth(10);p.getHungerManager().setFoodLevel(6);
  p.getInventory().setStack(0,new ItemStack(Items.IRON_SWORD));p.getInventory().setSelectedSlot(0);
  for(int z=1;z<=4;z++){
   var mob=new ZombieEntity(EntityType.ZOMBIE,world);mob.refreshPositionAndAngles(0.5,80,0.5+z,0,0);mob.setAiDisabled(true);mob.setNoGravity(true);mob.setHealth(20);mob.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,10000));world.spawnEntity(mob);
  }
  System.out.println("STAR_SMOKE_SERVER: prepared player, sword, injury and four hostile targets");
 }});}
}

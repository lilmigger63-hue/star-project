package dk.starswordmaster.smoke;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.entity.EquipmentSlot;
import java.util.*;
import java.math.BigInteger;
import static net.minecraft.server.command.CommandManager.literal;
/** Isolated fixtures; never included in the normal mod JAR. */
public class LevelingSmokeServer implements ModInitializer {
 private final Set<UUID> prepared=new HashSet<>();
 @Override public void onInitialize(){
  if(!Boolean.getBoolean("starsmoke.leveling"))return;
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{handler.player.getAbilities().invulnerable=true;handler.player.setNoGravity(true);});
  ServerTickEvents.END_SERVER_TICK.register(server->{for(var p:server.getPlayerManager().getPlayerList())if(p.getGameProfile().name().startsWith("LevelSmoke")&&prepared.add(p.getUuid())){
   reset(p,p.getGameProfile().name().endsWith("B"));p.teleport(p.getEntityWorld(),p.getGameProfile().name().endsWith("B")?12.5:0.5,80,0.5,Set.of(),0,0,false);p.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));p.getHungerManager().setFoodLevel(6);
  }});
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(literal("starleveltest")
   .then(literal("sword").executes(c->{var p=c.getSource().getPlayerOrThrow();StarSwordmaster.practice(p,Skill.SWORD,5);StarSwordmaster.progress(p).awardXp(java.math.BigInteger.valueOf(100));StarSwordmaster.sync(p);return 1;}))
   .then(literal("fire").executes(c->{var p=c.getSource().getPlayerOrThrow();StarSwordmaster.practice(p,Skill.FIRE,5);StarSwordmaster.progress(p).awardXp(java.math.BigInteger.valueOf(100));StarSwordmaster.sync(p);return 1;}))
   .then(literal("bulk").executes(c->{var p=c.getSource().getPlayerOrThrow();StarSwordmaster.progress(p).awardXp(BigInteger.valueOf(10000));StarSwordmaster.sync(p);return 1;}))
   .then(literal("legacy").executes(c->{reset(c.getSource().getPlayerOrThrow(),true);return 1;}))
   .then(literal("next").executes(c->{var p=c.getSource().getPlayerOrThrow();StarSwordmaster.progress(p).awardXp(BigInteger.valueOf(1400));StarSwordmaster.sync(p);return 1;}))
   .then(literal("huge").executes(c->{var p=c.getSource().getPlayerOrThrow();StarSwordmaster.progress(p).awardXp(BigInteger.TEN.pow(100));StarSwordmaster.sync(p);return 1;}))
   .then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}))));
 }
 @SuppressWarnings("unchecked") private void reset(ServerPlayerEntity player,boolean legacy){
  var props=new Properties();props.setProperty("schema","1");props.setProperty("xp",legacy?"200000":"909");if(!legacy)props.setProperty("xpCurveVersion","2");
  props.setProperty("practice.SWORD","20");props.setProperty("practice.FIRE",legacy?"10000":"20");props.setProperty("class",legacy?"MAGE":"UNINITIATED");
  props.setProperty("learned",legacy?"spark,mana_control":"spark,quick_cut");props.setProperty("stat.SPIRIT",legacy?"495":"0");props.setProperty("mana","60");props.setProperty("stamina","70");props.setProperty("slot.0","spark");if(!legacy)props.setProperty("slot.1","quick_cut");
  try{var sf=StarSwordmaster.class.getDeclaredField("store");sf.setAccessible(true);var pf=PlayerStore.class.getDeclaredField("players");pf.setAccessible(true);((Map<UUID,PlayerProgress>)pf.get(sf.get(null))).put(player.getUuid(),PlayerProgress.fromProperties(props));}catch(ReflectiveOperationException e){throw new AssertionError(e);}
  StatAttributes.apply(player,StarSwordmaster.progress(player));StarSwordmaster.sync(player);
 }
}

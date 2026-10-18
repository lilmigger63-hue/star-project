package dk.starswordmaster.smoke;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.*;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;
/** Fixtures/commands exclusively for isolated HUD tests, never packaged with the mod. */
public class ResourceHudSmokeServer implements ModInitializer {
 private final Set<UUID> prepared=new HashSet<>(),pendingDamage=new HashSet<>();
 private void protect(ServerPlayerEntity p){p.getAbilities().invulnerable=true;p.getHungerManager().setFoodLevel(6);p.setNoGravity(true);}
 @Override public void onInitialize(){
  if(!Boolean.getBoolean("starsmoke.resources"))return;
  ServerLifecycleEvents.SERVER_STARTING.register(server->{
   pendingDamage.clear();
   try{
    var dir=server.getSavePath(WorldSavePath.ROOT).resolve("data/starswordmaster");Files.createDirectories(dir);
    for(String role:new String[]{"A","B"}){
     UUID id=UUID.nameUUIDFromBytes(("OfflinePlayer:ResourceSmoke"+role).getBytes(StandardCharsets.UTF_8));if(server.isSingleplayer()&&role.equals("A"))id=server.getHostProfile().id();var path=dir.resolve(id+".properties");
     if(Files.exists(path)&&!Boolean.getBoolean("starsmoke.resources.reset"))continue;
     var p=new Properties();p.setProperty("schema","1");p.setProperty("xp",role.equals("A")?"1599":"200000");p.setProperty("class","MAGE");p.setProperty("mana","80");p.setProperty("stamina","80");
     p.setProperty("stat.ENDURANCE",role.equals("A")?"10":"100");p.setProperty("stat.SPIRIT",role.equals("A")?"10":"100");p.setProperty("practice.FIRE","10000");p.setProperty("practice.SUPPORT","10000");p.setProperty("practice.FROST","20");
     p.setProperty("learned","spark,mana_control,mend,light,guard"+(role.equals("B")?",arcane_mastery":""));p.setProperty("slot.0","light");p.setProperty("slot.1","guard");p.setProperty("slot.2","mend");p.setProperty("slot.3","spark");
     try(var writer=Files.newBufferedWriter(path)){p.store(writer,"Local resource HUD fixture, schema unchanged");}
    }
   }catch(java.io.IOException e){throw new java.io.UncheckedIOException(e);}
  });
  ServerTickEvents.END_SERVER_TICK.register(server->{for(var p:server.getPlayerManager().getPlayerList()){
   if(!p.getGameProfile().name().startsWith("ResourceSmoke"))continue;
   if(prepared.add(p.getUuid())){StatAttributes.apply(p,StarSwordmaster.progress(p));protect(p);p.clearStatusEffects();p.setHealth(p.getMaxHealth()-2);p.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));p.equipStack(EquipmentSlot.CHEST,new ItemStack(Items.IRON_CHESTPLATE));}
   // Vanilla intentionally rejects damage until the player has finished loading.
   if(pendingDamage.contains(p.getUuid())&&p.isLoaded()&&!p.isInTeleportationState()){
    pendingDamage.remove(p.getUuid());p.getAbilities().invulnerable=false;float before=p.getHealth();boolean hit=p.damage(p.getEntityWorld(),p.getDamageSources().magic(),3);protect(p);
    if(!hit||p.getHealth()>=before)throw new AssertionError("RESOURCE_SERVER FAIL: actual damage");System.out.println("RESOURCE_SERVER PASS: damage changed actual health");
   }
  }});
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->protect(handler.player));
  ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer,newPlayer,alive)->protect(newPlayer));
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(literal("starhudtest")
   .then(literal("damage").executes(c->{pendingDamage.add(c.getSource().getPlayerOrThrow().getUuid());return 1;}))
   .then(literal("level").executes(c->{var p=c.getSource().getPlayerOrThrow();StarSwordmaster.practice(p,Skill.FROST,5);StarSwordmaster.progress(p).awardXp(java.math.BigInteger.valueOf(100));StarSwordmaster.sync(p);return 1;}))
   .then(literal("absorb").executes(c->{c.getSource().getPlayerOrThrow().addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,600,0));return 1;}))
   .then(literal("poison").executes(c->{c.getSource().getPlayerOrThrow().addStatusEffect(new StatusEffectInstance(StatusEffects.POISON,200,0));return 1;}))
   .then(literal("wither").executes(c->{var p=c.getSource().getPlayerOrThrow();p.removeStatusEffect(StatusEffects.POISON);p.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER,200,0));return 1;}))
   .then(literal("clear").executes(c->{c.getSource().getPlayerOrThrow().clearStatusEffects();return 1;}))
   .then(literal("boost").executes(c->{var p=c.getSource().getPlayerOrThrow();p.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST,400,4));p.setHealth(p.getMaxHealth());p.equipStack(EquipmentSlot.CHEST,new ItemStack(Items.IRON_CHESTPLATE));p.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.DIAMOND_SWORD));return 1;}))
   .then(literal("unboost").executes(c->{var p=c.getSource().getPlayerOrThrow();p.removeStatusEffect(StatusEffects.HEALTH_BOOST);p.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));return 1;}))
   .then(literal("tooltip").executes(c->{c.getSource().getPlayerOrThrow().equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.DIAMOND_SWORD));return 1;}))
   .then(literal("air").executes(c->{c.getSource().getPlayerOrThrow().setAir(30);return 1;}))
   .then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}))));
 }
}

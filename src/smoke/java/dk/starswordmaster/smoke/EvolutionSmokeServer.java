package dk.starswordmaster.smoke;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import java.math.BigInteger;
import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;
/** Server-action and persisted-data fixtures in an isolated disposable world. */
public final class EvolutionSmokeServer implements ModInitializer {
 private final Set<UUID> prepared=new HashSet<>();private int checks;
 private void check(boolean ok,String message){if(!ok)throw new AssertionError("EVOLUTION_SERVER FAIL: "+message);checks++;System.out.println("EVOLUTION_SERVER PASS: "+message);}
 private PlayerStore store(){try{var f=StarSwordmaster.class.getDeclaredField("store");f.setAccessible(true);return (PlayerStore)f.get(null);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 @SuppressWarnings("unchecked") private void install(ServerPlayerEntity p,PlayerProgress progress){try{var f=PlayerStore.class.getDeclaredField("players");f.setAccessible(true);((Map<UUID,PlayerProgress>)f.get(store())).put(p.getUuid(),progress);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
 private void fixture(ServerPlayerEntity p,int level,String cls){Properties props=new Properties();props.setProperty("xp",BigInteger.TEN.multiply(BigInteger.valueOf(level-1)).multiply(BigInteger.valueOf(level+5)).toString());props.setProperty("xpCurveVersion","2");props.setProperty("class",cls);for(var skill:Skill.values())props.setProperty("practice."+skill,"250000");install(p,PlayerProgress.fromProperties(props));StatAttributes.apply(p,StarSwordmaster.progress(p));StarSwordmaster.sync(p);}
 @Override public void onInitialize(){if(!Boolean.getBoolean("starsmoke.evolution"))return;
  ServerTickEvents.END_SERVER_TICK.register(s->{for(var p:s.getPlayerManager().getPlayerList())if(p.isLoaded()&&prepared.add(p.getUuid())){p.getAbilities().invulnerable=true;p.setNoGravity(true);p.teleport(p.getEntityWorld(),.5,150,.5,Set.of(),0,0,false);fixture(p,4,"UNINITIATED");}});
  CommandRegistrationCallback.EVENT.register((d,a,e)->d.register(literal("starevolution")
   .then(literal("five").executes(c->{fixture(c.getSource().getPlayerOrThrow(),5,"UNINITIATED");return 1;}))
   .then(literal("nine").executes(c->{fixture(c.getSource().getPlayerOrThrow(),9,"SWORDSMAN");return 1;}))
   .then(literal("gain").executes(c->{var p=c.getSource().getPlayerOrThrow();var data=StarSwordmaster.progress(p);data.awardXp(BigInteger.valueOf(10000));check(data.evolution().stage()==3,"one server XP award crosses all evolution thresholds");check(data.points().equals(data.level().subtract(BigInteger.ONE)),"evolutions consume no perk points");check(data.statPoints().equals(data.points().multiply(BigInteger.valueOf(5))),"five statpoints per new level");StarSwordmaster.sync(p);return 1;}))
   .then(literal("late").executes(c->{fixture(c.getSource().getPlayerOrThrow(),30,"UNINITIATED");return 1;}))
   .then(literal("persist").executes(c->{var p=c.getSource().getPlayerOrThrow();var data=StarSwordmaster.progress(p);Properties before=data.toProperties();store().save(p.getUuid());store().forget(p.getUuid());var loaded=StarSwordmaster.progress(p);check(before.equals(loaded.toProperties()),"actual disk save/forget/reload preserves character exactly");check(loaded.evolution().stage()==3&&loaded.playerClass()==PlayerProgress.PlayerClass.MAGE,"late chosen Mage evolution survives disk reload");double health=p.getMaxHealth();for(int i=0;i<30;i++)StatAttributes.apply(p,loaded);check(p.getMaxHealth()==health,"native attribute application is idempotent");StarSwordmaster.sync(p);System.out.println("EVOLUTION_SERVER COMPLETE checks="+checks);return 1;}))
   .then(literal("spectator").executes(c->{c.getSource().getPlayerOrThrow().changeGameMode(net.minecraft.world.GameMode.SPECTATOR);return 1;}))
   .then(literal("survival").executes(c->{c.getSource().getPlayerOrThrow().changeGameMode(net.minecraft.world.GameMode.SURVIVAL);return 1;}))
   .then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}))));
 }
}

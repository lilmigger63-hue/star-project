package dk.starswordmaster.smoke;
import dk.starswordmaster.StarSwordmaster;
import dk.starswordmaster.progression.Skill;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.util.WorldSavePath;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;
/** Fixture/commands only for the isolated, localhost point-system test server. */
public class PointsSmokeServer implements ModInitializer {
 @Override public void onInitialize(){
  if(!Boolean.getBoolean("starsmoke.points"))return;
  ServerLifecycleEvents.SERVER_STARTING.register(server->{
   if(!Boolean.getBoolean("starsmoke.points.reset"))return;
   try{
    Path dir=server.getSavePath(WorldSavePath.ROOT).resolve("data/starswordmaster");Files.createDirectories(dir);
    for(String suffix:new String[]{"A","B"}){
     String name="PointsSmoke"+suffix;UUID id=UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes(StandardCharsets.UTF_8));
     var p=new Properties();p.setProperty("schema","1");p.setProperty("xp",suffix.equals("A")?"70":"69");p.setProperty("practice.SWORD","20");if(suffix.equals("A"))p.setProperty("stat.STRENGTH","1");
     try(var writer=Files.newBufferedWriter(dir.resolve(id+".properties"))){p.store(writer,"Legacy point-system test fixture");}
    }
   }catch(java.io.IOException e){throw new java.io.UncheckedIOException(e);}
  });
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{
   if(handler.player.getGameProfile().name().startsWith("PointsSmoke")){handler.player.getAbilities().invulnerable=true;System.out.println("POINTS_SERVER: joined "+handler.player.getGameProfile().name());}
  });
  ServerLivingEntityEvents.AFTER_DEATH.register((entity,source)->{if(entity instanceof net.minecraft.server.network.ServerPlayerEntity player&&player.getGameProfile().name().startsWith("PointsSmoke"))System.out.println("POINTS_SERVER PASS: actual death "+player.getGameProfile().name());});
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(literal("starpointstest")
   .then(literal("level").executes(c->{var p=c.getSource().getPlayerOrThrow();if(!p.getGameProfile().name().startsWith("PointsSmoke"))return 0;StarSwordmaster.practice(p,Skill.SWORD,5);StarSwordmaster.sync(p);return 1;}))
   .then(literal("die").executes(c->{var p=c.getSource().getPlayerOrThrow();if(!p.getGameProfile().name().startsWith("PointsSmoke"))return 0;p.getAbilities().invulnerable=false;p.kill(p.getEntityWorld());return 1;}))));
 }
}

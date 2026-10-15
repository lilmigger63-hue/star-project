package dk.starswordmaster;
import dk.starswordmaster.network.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import java.io.*;
import java.util.*;
import org.slf4j.*;
import static net.minecraft.server.command.CommandManager.literal;
public final class StarSwordmaster implements ModInitializer {
 public static final Logger LOG=LoggerFactory.getLogger("StarSwordmaster");
 private static PlayerStore store;
 private static long ticks;
 private static final Map<UUID,Long> combatUntil=new HashMap<>(),practiceAt=new HashMap<>();
 private static final Map<UUID,Integer> requests=new HashMap<>();
 public static PlayerProgress progress(ServerPlayerEntity p){return store.get(p.getUuid());}
 public static void combat(ServerPlayerEntity p){combatUntil.put(p.getUuid(),ticks+160);}
 public static void practice(ServerPlayerEntity p,Skill skill,int amount){
  if(ticks-practiceAt.getOrDefault(p.getUuid(),-100L)<10)return;
  var data=progress(p);int old=data.level();data.awardPractice(skill,amount);practiceAt.put(p.getUuid(),ticks);
  int gained=data.level()-old;
  if(gained>0)p.sendMessage(Text.translatable("message.starswordmaster.level_up",data.level(),gained*PlayerProgress.STAT_POINTS_PER_LEVEL,gained),true);
 }
 @Override public void onInitialize(){
  PayloadTypeRegistry.playC2S().register(ActionPayload.ID,ActionPayload.CODEC);
  PayloadTypeRegistry.playS2C().register(SnapshotPayload.ID,SnapshotPayload.CODEC);
  PayloadTypeRegistry.playS2C().register(AbilityPresentationPayload.ID,AbilityPresentationPayload.CODEC);
  ServerLifecycleEvents.SERVER_STARTED.register(server->{store=new PlayerStore(server.getSavePath(WorldSavePath.ROOT).resolve("data/starswordmaster"));ticks=0;AbilityPresentationEvents.clear();combatUntil.clear();practiceAt.clear();requests.clear();LOG.info("Star Swordmaster ready: 5 skill slots, permanent classes at level 10");});
  ServerLifecycleEvents.SERVER_STOPPING.register(server->{if(store!=null)store.saveAll();});
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{StatAttributes.apply(handler.player,progress(handler.player));sync(handler.player);});
  ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer,newPlayer,alive)->{AbilityPresentationEvents.cancel(oldPlayer);StatAttributes.apply(newPlayer,progress(newPlayer));if(!alive)newPlayer.setHealth(newPlayer.getMaxHealth());});
  ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->{AbilityPresentationEvents.forget(handler.player.getUuid());if(store!=null)store.forget(handler.player.getUuid());combatUntil.remove(handler.player.getUuid());practiceAt.remove(handler.player.getUuid());requests.remove(handler.player.getUuid());});
  ServerPlayNetworking.registerGlobalReceiver(ActionPayload.ID,(payload,context)->context.server().execute(()->handle(context.player(),payload)));
  ServerTickEvents.END_SERVER_TICK.register(server->{
   ticks++;if(ticks%20==0)requests.clear();
   for(var p:server.getPlayerManager().getPlayerList()){
    AbilityPresentationEvents.tick(p);var data=progress(p);StatAttributes.apply(p,data);
    if(p.isAlive())data.tick(ticks<combatUntil.getOrDefault(p.getUuid(),0L));
    if(ticks%5==0)sync(p);
   }
   if(ticks%200==0&&store!=null)store.saveAll();
  });
  ServerLivingEntityEvents.AFTER_DAMAGE.register((entity,source,base,actual,blocked)->{
   if(entity instanceof ServerPlayerEntity p&&actual>0){combat(p);AbilityPresentationEvents.cancel(p);}
   if(entity instanceof HostileEntity&&source.getAttacker() instanceof ServerPlayerEntity p&&actual>0){combat(p);if(source.isOf(DamageTypes.PLAYER_ATTACK)&&p.getMainHandStack().isIn(ItemTags.SWORDS))practice(p,Skill.SWORD,10);}
  });
  ServerLivingEntityEvents.AFTER_DEATH.register((entity,source)->{if(entity instanceof ServerPlayerEntity p)AbilityPresentationEvents.cancel(p);if(entity instanceof HostileEntity&&source.getAttacker() instanceof ServerPlayerEntity p){combat(p);if(source.isOf(DamageTypes.PLAYER_ATTACK)&&p.getMainHandStack().isIn(ItemTags.SWORDS))practice(p,Skill.SWORD,20);}});
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(literal("starswordmaster").executes(c->{var p=c.getSource().getPlayerOrThrow();var d=progress(p);p.sendMessage(Text.literal("Star Swordmaster | Level "+d.level()+" | "+d.playerClass()+" | Rank "+d.rank()+" | K: færdighedstræ"),false);return 1;})));
 }
 private static void handle(ServerPlayerEntity player,ActionPayload request){
  if(store==null||!player.isAlive()||player.isSpectator()||request.action().length()>20||request.target().length()>64)return;
  int count=requests.merge(player.getUuid(),1,Integer::sum);if(count>30)return;
  PlayerProgress p=progress(player);boolean ok=false;
  try{
   ok=switch(request.action()){
    case "unlock"->p.unlock(request.target());
    case "assign"->p.assign(request.slot(),request.target());
    case "class"->p.chooseClass(PlayerProgress.PlayerClass.valueOf(request.target()));
    case "stat"->p.spendStat(PlayerProgress.Stat.valueOf(request.target()));
    case "style"->{p.setStyle(PlayerProgress.SwordStyle.valueOf(request.target()));yield true;}
    case "cast"->AbilityEffects.cast(player,request.slot());
    default->false;
   };
  }catch(IllegalArgumentException ignored){}
  if(!ok)player.sendMessage(Text.literal("Kan ikke bruges: kontrollér krav, ressourcer og cooldown."),true);
  if(ok&&!request.action().equals("cast")){if(request.action().equals("stat"))StatAttributes.apply(player,p);store.save(player.getUuid());}
  sync(player);
 }
 public static void sync(ServerPlayerEntity player){
  if(store==null||!ServerPlayNetworking.canSend(player,SnapshotPayload.ID))return;
  try{var writer=new StringWriter();progress(player).toProperties().store(writer,null);ServerPlayNetworking.send(player,new SnapshotPayload(writer.toString()));}
  catch(IOException e){throw new UncheckedIOException(e);}
 }
}

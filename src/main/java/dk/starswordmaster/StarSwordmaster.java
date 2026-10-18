package dk.starswordmaster;
import dk.starswordmaster.network.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import java.util.*;
import java.math.BigInteger;
import org.slf4j.*;
import static net.minecraft.server.command.CommandManager.literal;
public final class StarSwordmaster implements ModInitializer {
 public static final Logger LOG=LoggerFactory.getLogger("StarSwordmaster");
 private static PlayerStore store;
 private static long ticks;
 private static final Map<UUID,Long> combatUntil=new HashMap<>(),practiceAt=new HashMap<>();
 private static final Map<UUID,Integer> requests=new HashMap<>();
 private static final Map<UUID,SnapshotWire.Sender> snapshots=new HashMap<>();
 private static final EvolutionMilestones evolutionMilestones=new EvolutionMilestones();
 private static final KillRewards killRewards=new KillRewards();
 private static ProgressionConfig progressionConfig=ProgressionConfig.defaults();
 private static final Map<UUID,BigInteger> pendingKillXp=new HashMap<>(),pendingLevels=new HashMap<>();
 public static PlayerProgress progress(ServerPlayerEntity p){return store.get(p.getUuid());}
 public static void combat(ServerPlayerEntity p){combatUntil.put(p.getUuid(),ticks+160);}
 public static void practice(ServerPlayerEntity p,Skill skill,int amount){
  if(ticks-practiceAt.getOrDefault(p.getUuid(),-100L)<10)return;
  var data=progress(p);var old=data.level();data.awardPractice(skill,amount,progressionConfig.practiceCharacterXpMultiplier());practiceAt.put(p.getUuid(),ticks);
  var gained=data.level().subtract(old);
  if(gained.signum()>0)pendingLevels.merge(p.getUuid(),gained,BigInteger::add);
 }
 @Override public void onInitialize(){
  dk.starswordmaster.presentation.PresentationParticleTypes.register();
  progressionConfig=ProgressionConfig.load(FabricLoader.getInstance().getConfigDir().resolve("starswordmaster-progression.json"));
  PayloadTypeRegistry.playC2S().register(ActionPayload.ID,ActionPayload.CODEC);
  PayloadTypeRegistry.playS2C().register(SnapshotPayload.ID,SnapshotPayload.CODEC);
  PayloadTypeRegistry.playS2C().register(AbilityPresentationPayload.ID,AbilityPresentationPayload.CODEC);
  PayloadTypeRegistry.playS2C().register(VisualStatePayload.ID,VisualStatePayload.CODEC);
  EntityTrackingEvents.START_TRACKING.register((entity,viewer)->{if(entity instanceof ServerPlayerEntity subject)ServerVisualState.send(viewer,subject);});
  EntityTrackingEvents.STOP_TRACKING.register((entity,viewer)->{if(entity instanceof ServerPlayerEntity subject)ServerVisualState.remove(viewer,subject);});
  ServerLifecycleEvents.SERVER_STARTED.register(server->{store=new PlayerStore(server.getSavePath(WorldSavePath.ROOT).resolve("data/starswordmaster"));ticks=0;snapshots.clear();evolutionMilestones.clear();AbilityPresentationEvents.clear();ServerVisualState.clear();combatUntil.clear();practiceAt.clear();requests.clear();killRewards.clear();pendingKillXp.clear();pendingLevels.clear();LOG.info("Star Swordmaster ready: 5 skill slots, permanent classes at level 5");});
  ServerLifecycleEvents.SERVER_STOPPING.register(server->{if(store!=null)store.saveAll();snapshots.clear();evolutionMilestones.clear();});
  ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{snapshots.remove(handler.player.getUuid());evolutionMilestones.seed(handler.player.getUuid(),progress(handler.player).evolution());StatAttributes.apply(handler.player,progress(handler.player));sync(handler.player);ServerVisualState.send(handler.player,handler.player);});
  ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer,newPlayer,alive)->{AbilityPresentationEvents.cancel(oldPlayer);StatAttributes.apply(newPlayer,progress(newPlayer));ServerVisualState.send(newPlayer,newPlayer);if(!alive)newPlayer.setHealth(newPlayer.getMaxHealth());});
  ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player,origin,destination)->{AbilityPresentationEvents.cancel(player);ServerVisualState.send(player,player);});
  ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->{snapshots.remove(handler.player.getUuid());evolutionMilestones.forget(handler.player.getUuid());AbilityPresentationEvents.forget(handler.player.getUuid());ServerVisualState.forget(handler.player.getUuid());if(store!=null)store.forget(handler.player.getUuid());combatUntil.remove(handler.player.getUuid());practiceAt.remove(handler.player.getUuid());requests.remove(handler.player.getUuid());pendingKillXp.remove(handler.player.getUuid());pendingLevels.remove(handler.player.getUuid());});
  ServerPlayNetworking.registerGlobalReceiver(ActionPayload.ID,(payload,context)->context.server().execute(()->handle(context.player(),payload)));
  ServerTickEvents.END_SERVER_TICK.register(server->{
   ticks++;if(ticks%20==0)requests.clear();
   for(var p:server.getPlayerManager().getPlayerList()){
    AbilityPresentationEvents.tick(p);var data=progress(p);evolutionMilestones.observe(p.getUuid(),data.evolution());StatAttributes.apply(p,data);
    if(p.isAlive())data.tick(ticks<combatUntil.getOrDefault(p.getUuid(),0L));
    if(ticks%5==0)sync(p);if(ticks%20==0){ServerVisualState.sync(p);flushXpFeedback(p);flushEvolutionFeedback(p);}
   }
   if(ticks%200==0&&store!=null)store.saveAll();
  });
  ServerLivingEntityEvents.AFTER_DAMAGE.register((entity,source,base,actual,blocked)->{
   if(entity instanceof ServerPlayerEntity p&&actual>0){combat(p);AbilityPresentationEvents.cancel(p);}
   if(entity instanceof HostileEntity&&source.getAttacker() instanceof ServerPlayerEntity p&&actual>0){combat(p);if(source.isOf(DamageTypes.PLAYER_ATTACK)&&p.getMainHandStack().isIn(ItemTags.SWORDS))practice(p,Skill.SWORD,10);}
  });
  ServerLivingEntityEvents.AFTER_DEATH.register((entity,source)->{
   if(entity instanceof ServerPlayerEntity p)AbilityPresentationEvents.cancel(p);
   ServerPlayerEntity killer=KillRewards.creditedPlayer(source);
   if(store!=null&&killer!=null){
    BigInteger base=killRewards.claim(entity,killer,progressionConfig);
    if(base.signum()>0){
     combat(killer);var data=progress(killer);var oldXp=data.totalXp();var oldLevel=data.level();
     data.awardXp(base);pendingKillXp.merge(killer.getUuid(),data.totalXp().subtract(oldXp),BigInteger::add);
     var gained=data.level().subtract(oldLevel);if(gained.signum()>0)pendingLevels.merge(killer.getUuid(),gained,BigInteger::add);
     StatAttributes.apply(killer,data);sync(killer);
    }
    if(entity instanceof HostileEntity&&source.isOf(DamageTypes.PLAYER_ATTACK)&&killer.getMainHandStack().isIn(ItemTags.SWORDS))practice(killer,Skill.SWORD,20);
   }
  });
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(literal("starswordmaster").executes(c->{var p=c.getSource().getPlayerOrThrow();var d=progress(p);p.sendMessage(Text.translatable("message.starswordmaster.command_status",d.level().toString(),Text.translatable("status.starswordmaster.class."+d.playerClass().name().toLowerCase(Locale.ROOT)),d.rank()),false);return 1;})));
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
    case "reset_skills"->{if(!request.target().isEmpty()||request.slot()!=0)yield false;p.resetLearnedSkills();AbilityPresentationEvents.cancel(player);yield true;}
    case "cast"->AbilityEffects.cast(player,request.slot());
    default->false;
   };
  }catch(IllegalArgumentException ignored){}
  if(!ok)player.sendMessage(Text.translatable("message.starswordmaster.action_failed"),true);
  if(ok&&!request.action().equals("cast")){if(request.action().equals("class")||request.action().equals("stat")||request.action().equals("unlock")||request.action().equals("reset_skills"))StatAttributes.apply(player,p);store.save(player.getUuid());}
  sync(player);ServerVisualState.sync(player);
 }
 private static void flushEvolutionFeedback(ServerPlayerEntity player){
  var evolution=evolutionMilestones.take(player.getUuid(),progress(player).evolution());if(evolution==null)return;
  var color=switch(evolution.playerClass()){case SWORDSMAN->net.minecraft.util.Formatting.GOLD;case MAGE->net.minecraft.util.Formatting.AQUA;default->net.minecraft.util.Formatting.LIGHT_PURPLE;};
  player.sendMessage(Text.translatable("message.starswordmaster.class_evolution",Text.translatable(evolution.titleKey())).formatted(color),false);
  // One bounded ring per real milestone. Uses existing vanilla particles and tracking packets.
  var dust=new net.minecraft.particle.DustParticleEffect(color.getColorValue(),1f);
  for(int i=0;i<12;i++){double angle=i*Math.PI/6;player.getEntityWorld().spawnParticles(dust,player.getX()+Math.cos(angle)*.7,player.getY()+.9,player.getZ()+Math.sin(angle)*.7,1,.025,.06,.025,0);}
 }
 private static void flushXpFeedback(ServerPlayerEntity player){
  BigInteger xp=pendingKillXp.remove(player.getUuid()),gained=pendingLevels.remove(player.getUuid());
  if(gained!=null&&gained.signum()>0)player.sendMessage(Text.translatable("message.starswordmaster.level_up",progress(player).level().toString(),gained.multiply(BigInteger.valueOf(PlayerProgress.STAT_POINTS_PER_LEVEL)).toString(),gained.toString()),true);
  else if(progressionConfig.showKillXp()&&xp!=null&&xp.signum()>0)player.sendMessage(Text.translatable("message.starswordmaster.kill_xp",dk.starswordmaster.ui.ProgressNumbers.compact(xp)),true);
 }
 public static void sync(ServerPlayerEntity player){
  if(store==null||!ServerPlayNetworking.canSend(player,SnapshotPayload.ID))return;
  var properties=progress(player).toProperties();
  var cache=snapshots.computeIfAbsent(player.getUuid(),id->new SnapshotWire.Sender());
  String wire=cache.changed(properties);if(wire==null)return;
  ServerPlayNetworking.send(player,new SnapshotPayload(wire));cache.remember(properties);
 }
}

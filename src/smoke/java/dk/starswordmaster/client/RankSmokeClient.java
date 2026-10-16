package dk.starswordmaster.client;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import dk.starswordmaster.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.client.util.ScreenshotRecorder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
/** Real integrated-server attributes, damage, C2S unlocks and translated H/K rendering. Test sources only. */
public final class RankSmokeClient implements ClientModInitializer {
 private static final String[] IDS={"astral_sword_sovereign","celestial_archon","eclipse_sovereign"};
 private static final String[] CLASSES={"SWORDSMAN","MAGE","SPELLBLADE"};
 private static final String[] ROUTES={"quick_cut,aura_awaken,sword_mastery","spark,mana_control,arcane_mastery","heavy_cut,element_binding,spellblade_mastery"};
 private final AtomicInteger checks=new AtomicInteger();
 private volatile boolean tested;
 private int ticks,stage,wait,index;private boolean started,finished;
 private void check(boolean value,String message){if(!value)throw new AssertionError("RANK_SMOKE FAIL: "+message);checks.incrementAndGet();System.out.println("RANK_SMOKE PASS: "+message);}
 private void equal(double expected,double actual,String message){check(Math.abs(expected-actual)<0.0001,message+" expected="+expected+" actual="+actual);}
 private PlayerProgress fixture(int c){
  var props=new Properties();props.setProperty("xp","7200");props.setProperty("class",CLASSES[c]);props.setProperty("learned",ROUTES[c]);props.setProperty("mana","999");props.setProperty("stamina","999");
  for(var s:Skill.values())props.setProperty("practice."+s,"9025");for(var s:PlayerProgress.Stat.values())props.setProperty("stat."+s,"2");return PlayerProgress.fromProperties(props);
 }
 @SuppressWarnings("unchecked") private PlayerStore install(ServerPlayerEntity player,PlayerProgress data){
  try{var f=StarSwordmaster.class.getDeclaredField("store");f.setAccessible(true);var store=(PlayerStore)f.get(null);var map=PlayerStore.class.getDeclaredField("players");map.setAccessible(true);((Map<UUID,PlayerProgress>)map.get(store)).put(player.getUuid(),data);return store;}catch(ReflectiveOperationException e){throw new AssertionError(e);}
 }
 private void serverChecks(ServerPlayerEntity player){
  player.clearStatusEffects();player.setSprinting(false);player.getAbilities().invulnerable=true;player.setNoGravity(true);
  player.teleport(player.getEntityWorld(),0.5,100,0.5,Set.of(),0,0,false);player.setVelocity(0,0,0);player.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));
  for(int c=0;c<3;c++){
   var p=fixture(c);var store=install(player,p);check(p.unlock(IDS[c]),"server accepts "+IDS[c]);var saved=p.toProperties();
   for(int i=0;i<200;i++)StatAttributes.apply(player,p);
   var b=p.bonuses();equal(1+b.miningSpeed(),player.getAttributeValue(EntityAttributes.BLOCK_BREAK_SPEED),CLASSES[c]+" actual mining");equal(.1*(1+b.movementSpeed()),player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED),CLASSES[c]+" actual movement");equal(20+b.health(),player.getMaxHealth(),CLASSES[c]+" actual health");
   for(var attr:List.of(EntityAttributes.BLOCK_BREAK_SPEED,EntityAttributes.MOVEMENT_SPEED,EntityAttributes.MAX_HEALTH))check(player.getAttributeInstance(attr).getModifiers().stream().filter(m->Set.of(StatAttributes.MINING_ID,StatAttributes.SPEED_ID,StatAttributes.HEALTH_ID).contains(m.id())).count()==1,"one owned modifier after 200 updates "+attr);
   check(saved.equals(p.toProperties()),"attribute application leaves save unchanged");store.forget(player.getUuid());p=store.get(player.getUuid());check(saved.equals(p.toProperties()),"real PlayerStore reconnect preserves "+IDS[c]);StatAttributes.apply(player,p);equal(20+b.health(),player.getMaxHealth(),"reload does not stack health");
   String id=c==0?"quick_cut":c==1?"spark":"flame_blade";if(!p.learned().contains(id))check(p.unlock(id),"learn damage fixture "+id);check(p.assign(0,id),"assign offensive ability "+id);
   var target=new ZombieEntity(EntityType.ZOMBIE,player.getEntityWorld());target.refreshPositionAndAngles(.5,100,3.3,0,0);target.setAiDisabled(true);target.setNoGravity(true);target.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(1000);target.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(0);target.setHealth(1000);player.getEntityWorld().spawnEntity(target);
   float scale=(float)p.damageMultiplier(Ability.ALL.get(id));float expected=c==0?4*scale*1.2f:c==1?3*scale:6*scale;
   check(AbilityEffects.cast(player,0),"real combat casts "+id);equal(expected,1000-target.getHealth(),"real combat uses rank-four multiplier "+id);target.discard();AbilityPresentationEvents.cancel(player);
  }
  prepareClient(player,0);tested=true;
 }
 private void prepareClient(ServerPlayerEntity player,int c){install(player,fixture(c));StatAttributes.apply(player,StarSwordmaster.progress(player));StarSwordmaster.sync(player);}
 private void screenshot(MinecraftClient c,String name){ScreenshotRecorder.saveScreenshot(c.runDirectory,"rank-"+name+".png",c.getFramebuffer(),1,message->{});}
 @Override public void onInitializeClient(){
  if(!Boolean.getBoolean("starsmoke.ranks"))return;
  ClientLifecycleEvents.CLIENT_STARTED.register(c->{c.options.pauseOnLostFocus=false;c.getTutorialManager().setStep(TutorialStep.NONE);});
  ClientTickEvents.END_CLIENT_TICK.register(c->{
   if(finished)return;if(++ticks>4000)throw new AssertionError("RANK_SMOKE timeout "+stage);
   if(!started&&ticks>100&&c.currentScreen!=null){started=true;c.createIntegratedServerLoader().start("RankSmoke",()->c.setScreen(new TitleScreen()));}
   if(c.player==null||!c.player.isLoaded()||!StarSwordmasterClient.ready)return;
   if(stage==0){stage=1;c.getServer().execute(()->serverChecks(c.getServer().getPlayerManager().getPlayer(c.player.getUuid())));return;}
   if(!tested||++wait<20)return;wait=0;
   var p=StarSwordmasterClient.data;
   if(stage==1){if(!p.playerClass().name().equals(CLASSES[index])||p.rank()!=3)return;check(p.canUnlock(IDS[index]),"client eligible before purchase "+IDS[index]);StarSwordmasterClient.send("unlock",IDS[index],0);stage=2;return;}
   if(stage==2){if(p.rank()!=4)return;var d=StatusData.from(p,true).orElseThrow();check(d.rankNodeId().equals(IDS[index]),"snapshot carries correct ultimate title");check(p.points().intValueExact()==20,"C2S purchase consumes exactly one perk point");check(p.statPoints().intValueExact()==112,"C2S purchase does not consume stat points");for(var s:PlayerProgress.Stat.values())check(d.effectiveStats().get(s)==p.effectiveStat(s),"H effective stat "+s);c.setScreen(new StatusScreen());stage=3;return;}
   if(stage==3){screenshot(c,CLASSES[index]+"-status");c.setScreen(new SkillTreeScreen());check(SkillTreeLayout.NODES.size()==62,"K renders 62 nodes");stage=4;return;}
   if(stage==4){screenshot(c,CLASSES[index]+"-tree");c.setScreen(null);if(++index<3){stage=1;c.getServer().execute(()->prepareClient(c.getServer().getPlayerManager().getPlayer(c.player.getUuid()),index));return;}System.out.println("RANK_SMOKE COMPLETE checks="+checks.get());finished=true;}
  });
 }
}

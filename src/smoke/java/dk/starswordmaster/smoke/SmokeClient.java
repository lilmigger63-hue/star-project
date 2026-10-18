package dk.starswordmaster.smoke;
import dk.starswordmaster.client.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
public class SmokeClient implements ClientModInitializer {
 int ticks,stage,wait;boolean connecting;double beforeMana,beforeStamina;int beforeFire,beforeSword;
 void check(boolean condition,String message){if(!condition)throw new AssertionError("STAR_SMOKE FAIL: "+message);System.out.println("STAR_SMOKE PASS: "+message);}
 public void onInitializeClient(){if(Boolean.getBoolean("starsmoke.radial")||Boolean.getBoolean("starsmoke.expansion")||Boolean.getBoolean("starsmoke.ranks")||Boolean.getBoolean("starsmoke.leveling")||Boolean.getBoolean("starsmoke.animations")||Boolean.getBoolean("starsmoke.resources")||Boolean.getBoolean("starsmoke.effects")||Boolean.getBoolean("starsmoke.status")||Boolean.getBoolean("starsmoke.hud")||Boolean.getBoolean("starsmoke.points"))return;ClientTickEvents.END_CLIENT_TICK.register(client->{
  ticks++;
  if(!connecting&&ticks>100&&client.currentScreen!=null){connecting=true;if(Boolean.getBoolean("starsmoke.singleplayer")){client.createIntegratedServerLoader().start(System.getProperty("starsmoke.world","StarSmokeSP"),()->client.setScreen(new TitleScreen()));return;}ConnectScreen.connect(new TitleScreen(),client,ServerAddress.parse("127.0.0.1:"+Integer.getInteger("starsmoke.port",25565)),new ServerInfo("Local smoke test","127.0.0.1:"+Integer.getInteger("starsmoke.port",25565),ServerInfo.ServerType.OTHER),false,null);}
  if(!StarSwordmasterClient.ready||client.player==null)return;
  if(wait++<15)return;wait=0;var p=StarSwordmasterClient.data;
  if(Boolean.getBoolean("starsmoke.magic")){magicStep(p,client);return;}
  switch(stage++){
   case 0->{check(p.level().intValueExact()>=10,"authoritative snapshot received");StarSwordmasterClient.send("class","SWORDSMAN",0);}
   case 1->{check(p.playerClass()==PlayerProgress.PlayerClass.SWORDSMAN,"level-10 class selected");StarSwordmasterClient.send("class","MAGE",0);}
   case 2->{check(p.playerClass()==PlayerProgress.PlayerClass.SWORDSMAN,"class cannot be changed");StarSwordmasterClient.send("unlock","spark",0);}
   case 3->{check(p.learned().contains("spark"),"simple magic learned");StarSwordmasterClient.send("unlock","quick_cut",0);}
   case 4->{check(p.learned().contains("quick_cut"),"sword technique learned");StarSwordmasterClient.send("unlock","aura_awaken",0);}
   case 5->{check(p.rank()==2,"rank follows unlocked perk");StarSwordmasterClient.send("assign","spark",0);}
   case 6->{check(p.slot(0).equals("spark"),"magic assigned to slot");StarSwordmasterClient.send("assign","quick_cut",1);}
   case 7->{check(p.slot(1).equals("quick_cut"),"sword assigned to slot");beforeFire=Integer.parseInt(p.toProperties().getProperty("practice.FIRE"));beforeSword=Integer.parseInt(p.toProperties().getProperty("practice.SWORD"));beforeMana=p.mana();StarSwordmasterClient.send("cast","",0);}
   case 8->{check(p.mana()<beforeMana,"magic cast consumes mana");check(Integer.parseInt(p.toProperties().getProperty("practice.FIRE"))>beforeFire,"magic trains FIRE");check(Integer.parseInt(p.toProperties().getProperty("practice.SWORD"))==beforeSword,"magic does not steal sword practice");beforeStamina=p.stamina();StarSwordmasterClient.send("cast","",1);}
   case 9->{check(p.stamina()<beforeStamina,"sword cast consumes stamina");StarSwordmasterClient.send("unlock","mend",0);}
   case 10->{check(p.learned().contains("mend"),"healing learned");StarSwordmasterClient.send("assign","mend",2);}
   case 11->{StarSwordmasterClient.send("cast","",2);}
   case 12->{check(client.player.getHealth()>10,"healing changes actual health");StarSwordmasterClient.send("unlock","fire_burst",0);}
   case 13->{check(!p.learned().contains("fire_burst"),"wrong-class ability rejected");client.setScreen(new SkillTreeScreen());System.out.println("STAR_SMOKE: skill tree open for screenshot");}
   case 14->{StarSwordmasterClient.send("unlock","quick_dash",0);}
   case 15->{check(p.learned().contains("quick_dash"),"advanced sword prerequisite");StarSwordmasterClient.send("unlock","rapid_slash",0);}
   case 16->{check(p.learned().contains("rapid_slash"),"expanded sword technique learned");StarSwordmasterClient.send("assign","rapid_slash",3);}
   case 17->{beforeStamina=p.stamina();StarSwordmasterClient.send("cast","",3);}
   case 18->{check(p.stamina()<beforeStamina,"expanded sword technique executes");StarSwordmasterClient.send("unlock","renew",0);}
   case 19->{check(p.learned().contains("renew"),"expanded support magic learned");StarSwordmasterClient.send("assign","renew",4);}
   case 20->{StarSwordmasterClient.send("cast","",4);}
   case 21->{check(client.player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.REGENERATION),"expanded support magic executes");client.setScreen(new SkillTreeScreen());System.out.println("STAR_SMOKE: expansion checks complete; tree ready");}
   case 22->{StarSwordmasterClient.send("unlock","flow_training",0);}
   case 23->{check(p.learned().contains("flow_training"),"quick specialization learned");StarSwordmasterClient.send("style","QUICK",0);}
   case 24->{check(Math.abs(p.manaCost(Ability.ALL.get("quick_cut"))-9)<0.001,"style costs synchronized");beforeMana=p.mana();StarSwordmasterClient.send("cast","",1);}
   case 25->{check(p.mana()<beforeMana,"specialized sword cast charges mana");StarSwordmasterClient.send("unlock","piercing_thrust",0);}
   case 26->{check(p.learned().contains("piercing_thrust"),"new thrust learned");StarSwordmasterClient.send("assign","piercing_thrust",3);}
   case 27->{beforeSword=Integer.parseInt(p.toProperties().getProperty("practice.SWORD"));StarSwordmasterClient.send("cast","",3);}
   case 28->{check(Integer.parseInt(p.toProperties().getProperty("practice.SWORD"))>beforeSword,"new thrust hits and trains sword");StarSwordmasterClient.send("unlock","guard",0);}
   case 29->{StarSwordmasterClient.send("unlock","riposte",0);}
   case 30->{StarSwordmasterClient.send("unlock","guardian_stance",0);}
   case 31->{check(p.learned().contains("guardian_stance"),"guardian stance learned");StarSwordmasterClient.send("assign","guardian_stance",4);}
   case 32->{StarSwordmasterClient.send("cast","",4);}
   case 33->{check(client.player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.RESISTANCE),"guardian stance protects player");client.setScreen(new SkillTreeScreen());System.out.println("STAR_SMOKE: sword v0.3 checks complete");}
   case 34->{StarSwordmasterClient.send("unlock","heavy_cut",0);}
   case 35->{StarSwordmasterClient.send("unlock","heavy_sweep",0);}
   case 36->{StarSwordmasterClient.send("unlock","impact_training",0);}
   case 37->{StarSwordmasterClient.send("style","HEAVY",0);}
   case 38->{check(p.style()==PlayerProgress.SwordStyle.HEAVY,"heavy style selected");check(p.damageMultiplier(Ability.ALL.get("heavy_cut"))>p.damageMultiplier(),"heavy specialization increases damage");StarSwordmasterClient.send("unlock","seismic_cut",0);}
   case 39->{check(p.learned().contains("seismic_cut"),"seismic technique learned");StarSwordmasterClient.send("assign","seismic_cut",0);}
   case 40->{if(!p.canCast(0)){stage--;return;}beforeSword=Integer.parseInt(p.toProperties().getProperty("practice.SWORD"));beforeStamina=p.stamina();StarSwordmasterClient.send("cast","",0);}
   case 41->{check(Integer.parseInt(p.toProperties().getProperty("practice.SWORD"))>beforeSword,"seismic technique affects enemies");check(p.stamina()<beforeStamina,"seismic technique consumes stamina");client.setScreen(new SkillTreeScreen());System.out.println("STAR_SMOKE: sword v0.3 full checks complete");}
   default->{}
  }
 });}
 private void magicStep(PlayerProgress p,net.minecraft.client.MinecraftClient client){
  switch(stage++){
   case 0->{StarSwordmasterClient.send("class","MAGE",0);}
   case 1->{check(p.playerClass()==PlayerProgress.PlayerClass.MAGE,"mage class selected");StarSwordmasterClient.send("unlock","spark",0);}
   case 2->{StarSwordmasterClient.send("unlock","mana_control",0);}
   case 3->{StarSwordmasterClient.send("unlock","fire_burst",0);}
   case 4->{StarSwordmasterClient.send("unlock","flame_lance",0);}
   case 5->{StarSwordmasterClient.send("unlock","ember_ring",0);}
   case 6->{check(p.learned().contains("ember_ring"),"new fire spell learned");StarSwordmasterClient.send("unlock","fire_affinity",0);}
   case 7->{check(p.manaCost(Ability.ALL.get("ember_ring"))<32,"fire affinity reduces cost");StarSwordmasterClient.send("assign","ember_ring",0);}
   case 8->{beforeMana=p.mana();beforeFire=Integer.parseInt(p.toProperties().getProperty("practice.FIRE"));StarSwordmasterClient.send("cast","",0);}
   case 9->{check(p.mana()<beforeMana,"fire ring consumes mana");check(Integer.parseInt(p.toProperties().getProperty("practice.FIRE"))>beforeFire,"fire ring affects enemies");StarSwordmasterClient.send("unlock","frost_touch",0);}
   case 10->{StarSwordmasterClient.send("unlock","frost_affinity",0);}
   case 11->{StarSwordmasterClient.send("unlock","ice_lance",0);}
   case 12->{StarSwordmasterClient.send("unlock","ice_prison",0);}
   case 13->{check(p.learned().contains("ice_prison"),"new frost spell learned");StarSwordmasterClient.send("assign","ice_prison",1);}
   case 14->{beforeMana=p.mana();StarSwordmasterClient.send("cast","",1);}
   case 15->{check(p.mana()<beforeMana,"ice prison hits enemy");check(p.cooldown("ice_prison")>0&&p.cooldown("ice_prison")<=128,"frost affinity shortens live cooldown");StarSwordmasterClient.send("unlock","mend",0);}
   case 16->{StarSwordmasterClient.send("unlock","restorative_affinity",0);}
   case 17->{StarSwordmasterClient.send("unlock","sanctuary",0);}
   case 18->{StarSwordmasterClient.send("unlock","restoration",0);}
   case 19->{check(p.learned().contains("restoration")&&p.healingMultiplier()==1.25,"healing specialization learned");StarSwordmasterClient.send("assign","restoration",2);}
   case 20->{StarSwordmasterClient.send("cast","",2);}
   case 21->{check(client.player.getHealth()>=15,"specialized restoration heals actual health");check(client.player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.REGENERATION),"restoration adds regeneration");client.setScreen(new SkillTreeScreen());System.out.println("STAR_SMOKE: magic v0.3 checks complete");}
   default->{}
  }
 }
}

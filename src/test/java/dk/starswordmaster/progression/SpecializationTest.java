package dk.starswordmaster.progression;
import org.junit.Test;
import static org.junit.Assert.*;
public class SpecializationTest {
 private PlayerProgress trained(){var p=new PlayerProgress();p.awardXp(java.math.BigInteger.valueOf(10000));for(Skill s:Skill.values())for(int i=0;i<500;i++)p.awardPractice(s,20);return p;}
 private void learn(PlayerProgress p,String... ids){for(String id:ids)assertTrue("Learn "+id,p.unlock(id));}
 @Test public void quickTrainingOnlyAppliesInQuickStyle(){var p=trained();learn(p,"quick_cut","quick_dash");var a=Ability.ALL.get("quick_cut");double original=p.manaCost(a);learn(p,"flow_training");assertEquals(original*0.9,p.manaCost(a),1e-8);assertEquals(a.stamina()*0.9,p.staminaCost(a),1e-8);assertEquals(36,p.cooldownTicks(a));p.setStyle(PlayerProgress.SwordStyle.HEAVY);assertEquals(original,p.manaCost(a),1e-8);assertEquals(40,p.cooldownTicks(a));}
 @Test public void heavyTrainingBoostsSwordButNotMagic(){var p=trained();learn(p,"heavy_cut","heavy_sweep","impact_training");p.setStyle(PlayerProgress.SwordStyle.HEAVY);assertEquals(p.damageMultiplier()*1.15,p.damageMultiplier(Ability.ALL.get("heavy_cut")),1e-8);assertEquals(p.damageMultiplier(),p.damageMultiplier(Ability.ALL.get("spark")),1e-8);p.setStyle(PlayerProgress.SwordStyle.QUICK);assertEquals(p.damageMultiplier(),p.damageMultiplier(Ability.ALL.get("heavy_cut")),1e-8);}
 @Test public void defensiveTrainingReducesStaminaInDefensiveStyle(){var p=trained();learn(p,"guard","guard_training");var a=Ability.ALL.get("guard");p.setStyle(PlayerProgress.SwordStyle.DEFENSIVE);assertEquals(3,p.staminaCost(a),0);p.setStyle(PlayerProgress.SwordStyle.HEAVY);assertEquals(4,p.staminaCost(a),0);}
 @Test public void affinitiesChangeActualCastCostAndCooldown(){var p=trained();learn(p,"spark","fire_affinity","frost_touch","frost_affinity");p.assign(0,"spark");double before=p.mana();assertTrue(p.cast(0));assertEquals(10.8,before-p.mana(),1e-8);p.assign(1,"frost_touch");assertTrue(p.cast(1));assertEquals(48,p.cooldown("frost_touch"));}
 @Test public void restorationTrainingIsPassiveAndPersisted(){var p=trained();learn(p,"mend","restorative_affinity");assertEquals(1.25,p.healingMultiplier(),0);assertFalse(p.assign(0,"restorative_affinity"));assertEquals(1.25,PlayerProgress.fromProperties(p.toProperties()).healingMultiplier(),0);}
 @Test public void newTechniquesCanBeLearnedBeforeTheirVisualBranches(){var p=trained();learn(p,"piercing_thrust","guardian_stance","seismic_cut","ice_prison");learn(p,"quick_cut","quick_dash","guard","riposte","heavy_cut","heavy_sweep");assertTrue(p.chooseClass(PlayerProgress.PlayerClass.SWORDSMAN));assertEquals(p.learned(),PlayerProgress.fromProperties(p.toProperties()).learned());}
 @Test public void mageGetsAdvancedFireFrostAndRestoration(){var p=trained();p.chooseClass(PlayerProgress.PlayerClass.MAGE);learn(p,"spark","mana_control","fire_burst","flame_lance","ember_ring");learn(p,"ice_lance","ice_prison");learn(p,"sanctuary","restoration");var q=PlayerProgress.fromProperties(p.toProperties());assertEquals(p.learned(),q.learned());assertEquals(p.playerClass(),q.playerClass());}
 @Test public void actualV02SaveKeepsClassSlotsAndOldSkills() throws Exception {
  var properties=new java.util.Properties();try(var in=getClass().getResourceAsStream("/v0.2-player.properties")){assertNotNull(in);properties.load(in);}
  var p=PlayerProgress.fromProperties(properties);assertEquals(properties.getProperty("class"),p.playerClass().name());assertEquals(properties.getProperty("style"),p.style().name());
  for(int i=0;i<5;i++)assertEquals(properties.getProperty("slot."+i,""),p.slot(i));
  assertEquals(new java.util.HashSet<>(java.util.Arrays.asList(properties.getProperty("learned").split(","))),p.learned());
  assertFalse(p.learned().contains("flow_training"));assertFalse(p.learned().contains("ember_ring"));assertFalse(p.chooseClass(PlayerProgress.PlayerClass.MAGE));
 }
 @Test public void cooldownVisualDurationSurvivesStyleSwitchAndReload(){var p=trained();learn(p,"quick_cut","quick_dash","flow_training");p.assign(0,"quick_cut");p.cast(0);assertEquals(36,p.cooldownDuration("quick_cut"));p.setStyle(PlayerProgress.SwordStyle.HEAVY);assertEquals(36,p.cooldownDuration("quick_cut"));p.tick(false);var q=PlayerProgress.fromProperties(p.toProperties());assertEquals(36,q.cooldownDuration("quick_cut"));assertEquals(35,q.cooldown("quick_cut"));}
}

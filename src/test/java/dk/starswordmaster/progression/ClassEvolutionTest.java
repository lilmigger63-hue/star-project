package dk.starswordmaster.progression;

import org.junit.Test;
import java.math.BigInteger;
import java.util.Properties;
import static org.junit.Assert.*;
import static dk.starswordmaster.progression.PlayerProgress.PlayerClass.*;

public class ClassEvolutionTest {
 private static PlayerProgress at(int level) {
  return at(BigInteger.valueOf(level));
 }
 private static PlayerProgress at(BigInteger level) {
  var props=new Properties();
  props.setProperty("xpCurveVersion","2");
  props.setProperty("xp",BigInteger.TEN.multiply(level.subtract(BigInteger.ONE)).multiply(level.add(BigInteger.valueOf(5))).toString());
  return PlayerProgress.fromProperties(props);
 }
 @Test public void selectionIsManualPermanentAndValidAfterReloadFromLevelFive() {
  for(var cls:new PlayerProgress.PlayerClass[]{SWORDSMAN,MAGE,SPELLBLADE}) {
   var below=at(4);assertFalse(below.chooseClass(cls));assertEquals(UNINITIATED,below.playerClass());
   var p=at(5);assertEquals(UNINITIATED,p.playerClass());
   var points=p.points();var stats=p.statPoints();
   assertFalse(p.chooseClass(null));assertFalse(p.chooseClass(UNINITIATED));assertTrue(p.chooseClass(cls));
   assertFalse(p.chooseClass(cls));assertFalse(p.chooseClass(cls==MAGE?SWORDSMAN:MAGE));
   assertEquals(points,p.points());assertEquals(stats,p.statPoints());
   assertEquals(cls,PlayerProgress.fromProperties(p.toProperties()).playerClass());
   var forged=below.toProperties();forged.setProperty("class",cls.name());
   assertEquals(UNINITIATED,PlayerProgress.fromProperties(forged).playerClass());
  }
 }
 private static PlayerProgress chosen(int level,PlayerProgress.PlayerClass cls) {
  var p=at(level);assertTrue(p.chooseClass(cls));return p;
 }
 @Test public void stagesUseExactThresholdsWithoutSelectingAClassOrSpendingPoints() {
  int[] levels={4,5,9,10,19,20,29,30};int[] stages={0,0,0,1,1,2,2,3};
  for(int i=0;i<levels.length;i++) {
   assertEquals(0,at(levels[i]).evolution().stage());
   if(levels[i]<5)continue;
   for(var cls:new PlayerProgress.PlayerClass[]{SWORDSMAN,MAGE,SPELLBLADE}) {
    var p=chosen(levels[i],cls);var e=p.evolution();
    assertEquals(stages[i],e.stage());assertEquals(cls,e.playerClass());assertEquals(1,p.rank());
    assertEquals(BigInteger.valueOf(levels[i]-1),p.points());assertEquals(p.points().multiply(BigInteger.valueOf(5)),p.statPoints());
    assertTrue(p.learned().isEmpty());
    assertEquals(stages[i]==0?"status.starswordmaster.class."+cls.name().toLowerCase(java.util.Locale.ROOT):"status.starswordmaster.evolution."+cls.name().toLowerCase(java.util.Locale.ROOT)+"."+stages[i],e.titleKey());
   }
  }
 }
 @Test public void exactTotalsApplyToResourcesCostsRegenerationAndOnlyTheCorrectDamageKinds() {
  for(int stage=1;stage<=3;stage++)for(var cls:new PlayerProgress.PlayerClass[]{SWORDSMAN,MAGE,SPELLBLADE}) {
   int level=stage*10;var p=chosen(level,cls);var e=p.evolution();
   double damage=cls==SPELLBLADE?new double[]{.03,.06,.10}[stage-1]:.04*stage;
   int mana=cls==MAGE?10*stage:cls==SPELLBLADE?5*stage:0,stamina=cls==MAGE?0:5*stage;
   double manaDiscount=cls==MAGE?.05*(stage-1):cls==SPELLBLADE?new double[]{.03,.06,.08}[stage-1]:0;
   double staminaDiscount=cls==SWORDSMAN?.05*(stage-1):cls==SPELLBLADE?new double[]{.03,.06,.08}[stage-1]:0;
   assertEquals(damage,e.damageBonus(),1e-12);assertEquals(mana,e.manaBonus());assertEquals(stamina,e.staminaBonus());
   assertEquals(manaDiscount,e.manaDiscount(),1e-12);assertEquals(staminaDiscount,e.staminaDiscount(),1e-12);
   assertEquals(cls==MAGE?.05*stage:0,e.manaRegenerationBonus(),1e-12);
   assertEquals(100+2*(level-1)+mana,p.maxMana(),1e-12);assertEquals(100+level-1+stamina,p.maxStamina(),1e-12);
   assertEquals(cls==SWORDSMAN?stage:0,p.effectiveStat(PlayerProgress.Stat.AGILITY));assertEquals(0,p.stat(PlayerProgress.Stat.AGILITY));
   assertEquals(.15*(1+(cls==MAGE?.05*stage:0)),p.manaRegenPerTick(false),1e-12);
   assertEquals(.04*(1+(cls==MAGE?.05*stage:0)),p.manaRegenPerTick(true),1e-12);
   for(var a:Ability.ALL.values()) {
    boolean boosted=switch(cls) {case SWORDSMAN->a.damageKind()==Ability.DamageKind.SWORD||a.damageKind()==Ability.DamageKind.ENCHANTED_SWORD;case MAGE->a.damageKind()==Ability.DamageKind.MAGIC;case SPELLBLADE->a.damageKind()==Ability.DamageKind.ENCHANTED_SWORD;default->false;};
    assertEquals(a.id(),boosted?damage:0,e.damageBonus(a),1e-12);
    assertEquals(a.id(),1+(boosted?damage:0),p.damageMultiplier(a),1e-12);
    assertEquals(a.id(),a.mana()*(1-manaDiscount),p.manaCost(a),1e-12);
    assertEquals(a.id(),a.stamina()*(1-staminaDiscount),p.staminaCost(a),1e-12);
   }
   assertEquals(1,p.healingMultiplier(),0);
  }
 }
 @Test public void automaticMultilevelAwardAndLateChoiceUseTheFinalStageImmediately() {
  for(var cls:new PlayerProgress.PlayerClass[]{SWORDSMAN,MAGE,SPELLBLADE}) {
   var p=chosen(9,cls);p.awardXp(BigInteger.valueOf(10000));
   assertTrue(p.level().intValueExact()>=30);assertEquals(3,p.evolution().stage());
   var late=chosen(30,cls);assertEquals(3,late.evolution().stage());
   var huge=at(BigInteger.TEN.pow(100));assertEquals(0,huge.evolution().stage());assertTrue(huge.chooseClass(cls));
   assertEquals(3,huge.evolution().stage());assertTrue(Double.isFinite(huge.maxMana()));assertTrue(Double.isFinite(huge.maxStamina()));
  }
 }
 @Test public void repeatedSaveSnapshotsAndResetNeverReplayBonusesOrChangePointBudgets() {
  for(var cls:new PlayerProgress.PlayerClass[]{SWORDSMAN,MAGE,SPELLBLADE}) {
   var p=chosen(30,cls);var evolution=p.evolution();assertTrue(p.unlock("quick_cut"));assertTrue(p.assign(0,"quick_cut"));
   assertTrue(p.cast(0));var stats=p.statPoints();assertTrue(p.resetLearnedSkills());
   assertEquals(evolution,p.evolution());assertEquals(1,p.rank());assertEquals(stats,p.statPoints());assertEquals(BigInteger.valueOf(29),p.points());
   var expected=p.toProperties();double mana=p.maxMana(),stamina=p.maxStamina(),damage=p.damageMultiplier(Ability.ALL.get("quick_cut"));
   for(int i=0;i<50;i++) {
    assertFalse(p.resetLearnedSkills());p=PlayerProgress.fromProperties(p.toProperties());
    assertEquals(expected,p.toProperties());assertEquals(evolution,p.evolution());assertEquals(mana,p.maxMana(),0);assertEquals(stamina,p.maxStamina(),0);
    assertEquals(damage,p.damageMultiplier(Ability.ALL.get("quick_cut")),0);assertEquals(stats,p.statPoints());assertEquals(BigInteger.valueOf(29),p.points());
   }
   assertEquals("1",expected.getProperty("schema"));assertFalse(expected.stringPropertyNames().stream().anyMatch(k->k.toLowerCase(java.util.Locale.ROOT).contains("evolution")));
  }
 }
 @Test public void rankFourAndStylesStillMultiplyIndependentlyAndResetKeepsEvolution() {
  String[][] paths={{"quick_cut","aura_awaken","sword_mastery","astral_sword_sovereign"},{"spark","mana_control","arcane_mastery","celestial_archon"},{"heavy_cut","element_binding","spellblade_mastery","eclipse_sovereign"}};
  var classes=new PlayerProgress.PlayerClass[]{SWORDSMAN,MAGE,SPELLBLADE};
  for(int c=0;c<classes.length;c++) {
   var props=chosen(30,classes[c]).toProperties();for(var s:Skill.values())props.setProperty("practice."+s,"1000000");
   var p=PlayerProgress.fromProperties(props);var e=p.evolution();
   for(String id:paths[c])assertTrue(id,p.unlock(id));assertEquals(4,p.rank());assertEquals(e,p.evolution());
   for(var a:Ability.ALL.values())assertEquals(a.id(),p.damageMultiplier()*(1+p.rankDamageBonus(a))*(1+e.damageBonus(a)),p.damageMultiplier(a),1e-12);
   for(String id:new String[]{"heavy_cut","heavy_sweep","impact_training","quick_cut","quick_dash","flow_training","guard","guard_training","spark","fire_affinity"})if(!p.learned().contains(id))assertTrue(id,p.unlock(id));
   var sword=Ability.ALL.get("heavy_cut");var magic=Ability.ALL.get("spark");p.setStyle(PlayerProgress.SwordStyle.HEAVY);
   assertEquals(p.damageMultiplier()*1.15*(1+p.rankDamageBonus(sword))*(1+e.damageBonus(sword)),p.damageMultiplier(sword),1e-12);
   assertEquals(magic.mana()*.9*(1-e.manaDiscount()),p.manaCost(magic),1e-12);
   p.setStyle(PlayerProgress.SwordStyle.QUICK);assertEquals(sword.mana()*.9*(1-e.manaDiscount()),p.manaCost(sword),1e-12);
   assertEquals(sword.stamina()*.9*(1-e.staminaDiscount()),p.staminaCost(sword),1e-12);
   p.setStyle(PlayerProgress.SwordStyle.DEFENSIVE);assertEquals(sword.stamina()*.75*(1-e.staminaDiscount()),p.staminaCost(sword),1e-12);
   assertTrue(p.resetLearnedSkills());assertEquals(1,p.rank());assertEquals(e,p.evolution());assertEquals(29,p.points().intValueExact());
  }
 }
 @Test public void effectiveAgilityDoesNotOverflowAllocatedStatsAndRetainsExistingCaps() {
  var props=at(BigInteger.TEN.pow(100)).toProperties();props.setProperty("class","SWORDSMAN");props.setProperty("stat.AGILITY",Integer.toString(Integer.MAX_VALUE));
  var p=PlayerProgress.fromProperties(props);assertEquals((long)Integer.MAX_VALUE+3,p.effectiveStat(PlayerProgress.Stat.AGILITY));assertEquals(Integer.MAX_VALUE,p.stat(PlayerProgress.Stat.AGILITY));
  assertEquals(.4,p.agilityCooldownReduction(),0);assertEquals(.2,p.bonuses().movementSpeed(),0);
 }
}

package dk.starswordmaster.progression;

import org.junit.Test;
import java.math.BigInteger;
import java.util.Properties;
import java.util.UUID;
import java.nio.file.Files;
import dk.starswordmaster.PlayerStore;
import static org.junit.Assert.*;

public class SkillResetTest {
 private PlayerProgress mage() {
  var saved=new Properties();saved.setProperty("xp","200000");saved.setProperty("xpHalf","1");
  saved.setProperty("xpCurveVersion","2");saved.setProperty("xpLegacyOffset","96050");
  saved.setProperty("class","MAGE");saved.setProperty("style","DEFENSIVE");
  for(var s:Skill.values())saved.setProperty("practice."+s,"1000000");
  saved.setProperty("learned","spark,mana_control,arcane_mastery,celestial_archon,spirit_training_1");
  saved.setProperty("stat.SPIRIT","7");saved.setProperty("stat.ENDURANCE","3");
  saved.setProperty("slot.0","spark");saved.setProperty("mana","100000");saved.setProperty("stamina","100000");
  return PlayerProgress.fromProperties(saved);
 }
 @Test public void resetRefundsOnlyPerksAndPreservesCharacterPracticeAndAllocatedStats() {
  var p=mage();assertEquals(4,p.rank());assertTrue(p.cast(0));
  var before=p.toProperties();var level=p.level();var stats=p.statPoints();
  assertTrue(p.resetLearnedSkills());assertTrue(p.learned().isEmpty());
  assertEquals(level.subtract(BigInteger.ONE),p.points());assertEquals(stats,p.statPoints());
  assertEquals(1,p.rank());assertEquals(PlayerProgress.PlayerClass.MAGE,p.playerClass());
  for(String key:before.stringPropertyNames())
   if(key.startsWith("xp")||key.startsWith("practice.")||key.startsWith("stat.")||key.equals("class")||key.equals("style"))
    assertEquals(key,before.getProperty(key),p.toProperties().getProperty(key));
  for(int slot=0;slot<5;slot++){assertEquals("",p.slot(slot));assertFalse(p.canCast(slot));}
  assertEquals(Integer.parseInt(before.getProperty("cooldown.spark")),p.cooldown("spark"));
  assertTrue(p.mana()<=Double.parseDouble(before.getProperty("mana")));
  assertTrue(p.stamina()<=Double.parseDouble(before.getProperty("stamina")));
  assertTrue(p.mana()<=p.maxMana());assertTrue(p.stamina()<=p.maxStamina());
  assertEquals(7,p.effectiveStat(PlayerProgress.Stat.SPIRIT));
 }
 @Test public void repeatsAndReloadsNeverCreateAnExtraPointOrReplayXp() {
  var p=mage();p.resetLearnedSkills();var expected=p.toProperties();
  for(int i=0;i<6;i++){
   assertFalse(p.resetLearnedSkills());assertEquals(expected,p.toProperties());
   p=PlayerProgress.fromProperties(p.toProperties());
   assertEquals(expected.getProperty("xp"),p.totalXp().toString());
   assertEquals(p.level().subtract(BigInteger.ONE),p.points());assertTrue(p.learned().isEmpty());
  }
 }
 @Test public void emptyLevelOneResetDoesNothing() {
  var p=new PlayerProgress();var before=p.toProperties();assertFalse(p.resetLearnedSkills());
  assertEquals(before,p.toProperties());assertEquals(BigInteger.ZERO,p.points());
 }
 @Test public void resettingAndReloadingCannotBypassAnActiveCooldown() {
  var p=new PlayerProgress();p.awardXp(BigInteger.valueOf(1000));
  assertTrue(p.unlock("quick_cut"));assertTrue(p.assign(0,"quick_cut"));assertTrue(p.cast(0));
  int remaining=p.cooldown("quick_cut"),duration=p.cooldownDuration("quick_cut");
  assertTrue(p.resetLearnedSkills());assertFalse(p.resetLearnedSkills());
  p=PlayerProgress.fromProperties(p.toProperties());
  assertTrue(p.unlock("quick_cut"));assertTrue(p.assign(0,"quick_cut"));
  assertEquals(remaining,p.cooldown("quick_cut"));assertEquals(duration,p.cooldownDuration("quick_cut"));
  assertFalse(p.canCast(0));assertFalse(p.cast(0));
  for(int i=0;i<remaining;i++)p.tick(false);
  assertTrue(p.canCast(0));assertTrue(p.cast(0));
 }
 @Test public void currentResourcesDoNotHealWhenBonusesDisappear() {
  var props=mage().toProperties();props.setProperty("mana","20");props.setProperty("stamina","30");
  var p=PlayerProgress.fromProperties(props);p.resetLearnedSkills();assertEquals(20,p.mana(),0);assertEquals(30,p.stamina(),0);
 }
 @Test public void safeStoreSaveAndReloadPreservesExactResetEntitlement() throws Exception {
  var dir=Files.createTempDirectory("skill-reset-store");var id=UUID.randomUUID();var path=dir.resolve(id+".properties");
  try(var writer=Files.newBufferedWriter(path)){mage().toProperties().store(writer,null);}
  var store=new PlayerStore(dir);var p=store.get(id);assertTrue(p.resetLearnedSkills());
  var expected=p.toProperties();store.forget(id);
  for(int i=0;i<5;i++){
   store=new PlayerStore(dir);p=store.get(id);assertFalse(p.resetLearnedSkills());
   assertEquals(expected,p.toProperties());assertEquals(BigInteger.valueOf(99),p.points());
   assertEquals("1",p.toProperties().getProperty("xpHalf"));store.forget(id);
   assertFalse(Files.exists(dir.resolve(id+".tmp")));
  }
  Files.delete(path);Files.delete(dir);
 }
}

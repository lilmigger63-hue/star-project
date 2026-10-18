package dk.starswordmaster.progression;

import org.junit.Test;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Properties;
import static org.junit.Assert.*;

public class KillExperienceTest {
 @Test public void maxHealthRewardsAreExactAndRoundedUp() {
  for(long hp:new long[]{10,20,40,100,300,1000})assertEquals(BigInteger.valueOf(hp*3),KillExperience.baseReward(hp,3));
  assertEquals(BigInteger.valueOf(61),KillExperience.baseReward(20.1,3));
  assertEquals(BigInteger.ONE,KillExperience.baseReward(0.01,3));
  assertEquals(BigInteger.valueOf(80),KillExperience.baseReward(20,4));
 }
 @Test public void hugeBossesCannotOverflowAndMalformedHealthDoesNotPay() {
  assertEquals(new BigDecimal("3E100").toBigIntegerExact(),KillExperience.baseReward(1E100,3));
  assertEquals(BigDecimal.valueOf(Double.MAX_VALUE).multiply(BigDecimal.valueOf(3)).setScale(0,RoundingMode.CEILING).toBigIntegerExact(),KillExperience.baseReward(Double.MAX_VALUE,3));
  for(double hp:new double[]{Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY,0,-10})assertEquals(BigInteger.ZERO,KillExperience.baseReward(hp,3));
  for(double multiplier:new double[]{Double.NaN,Double.POSITIVE_INFINITY,0,-1})assertEquals(BigInteger.ZERO,KillExperience.baseReward(20,multiplier));
 }
 @Test public void aKillCrossingEightGetsExistingMultiplierOnlyOnNextKill() {
  var props=new Properties();props.setProperty("xp","909");props.setProperty("xpCurveVersion","2");
  var p=PlayerProgress.fromProperties(props);p.awardXp(KillExperience.baseReward(40,3));
  assertEquals(BigInteger.valueOf(1029),p.totalXp());p.awardXp(KillExperience.baseReward(40,3));
  assertEquals(BigInteger.valueOf(1209),p.totalXp());
 }
 @Test public void duplicateDeathCallbackCanOnlyClaimOneReward() {
  var ledger=new KillExperience.DeathClaims();var victim=new Object();
  assertTrue(ledger.claim(victim));assertFalse(ledger.claim(victim));assertTrue(ledger.claim(new Object()));
  ledger.clear();assertTrue(ledger.claim(victim));
 }
 @Test public void practiceStillTrainsWithoutCharacterXpByDefault() {
  var p=new PlayerProgress();for(int i=0;i<100;i++)p.awardPractice(Skill.SWORD,20);
  assertEquals(9,p.skillLevel(Skill.SWORD));assertEquals(BigInteger.ZERO,p.totalXp());assertEquals(BigInteger.ONE,p.level());
 }
 @Test public void optionalPracticeXpUsesExistingLevelMultiplier() {
  var props=new Properties();props.setProperty("xp","910");props.setProperty("xpCurveVersion","2");props.setProperty("practice.SWORD","20");
  var p=PlayerProgress.fromProperties(props);p.awardPractice(Skill.SWORD,5,0.5);
  assertEquals(BigInteger.valueOf(985),p.totalXp());assertEquals(2,p.skillLevel(Skill.SWORD));
 }
}

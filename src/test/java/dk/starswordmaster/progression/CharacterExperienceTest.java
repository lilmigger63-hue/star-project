package dk.starswordmaster.progression;
import org.junit.Test;
import java.math.BigInteger;
import java.util.Properties;
import static org.junit.Assert.*;
public class CharacterExperienceTest {
 private static BigInteger n(long value){return BigInteger.valueOf(value);}
 private CharacterExperience at(String xp){var p=new Properties();p.setProperty("xp",xp);p.setProperty("xpCurveVersion","2");return CharacterExperience.read(p);}
 @Test public void originalThresholdsThroughLevelSeven(){long xp=0;for(int level=1;level<=7;level++){var p=at(Long.toString(xp));assertEquals(n(level),p.level());assertEquals(n(50+20*level),p.xpForNextLevel());assertEquals(BigInteger.ZERO,p.xpInLevel());p.award(n(10));assertEquals(n(xp+10),p.totalXp());xp+=50+20*level;}}
 @Test public void rewardUsesLevelBeforeAwardEvenWhenCrossingEight(){var p=at("909");p.award(n(100));assertEquals(n(1009),p.totalXp());assertEquals(n(8),p.level());p.award(n(100));assertEquals(n(1159),p.totalXp());assertEquals(n(9),p.level());}
 @Test public void levelEightGrantsFiftyPercentMoreWithoutChangingThreshold(){var p=at("910");p.award(n(100));assertEquals(n(1060),p.totalXp());assertEquals(n(210),p.xpForNextLevel());assertEquals(n(150),p.xpInLevel());}
 @Test public void fractionalRewardsSurviveReloadAndLoseNothing(){var p=at("910");p.award(BigInteger.ONE);var props=new Properties();p.write(props);p=CharacterExperience.read(props);p.award(BigInteger.ONE);assertEquals(n(913),p.totalXp());}
 @Test public void hugeRewardsHaveNoLevelOrIntegerCeiling(){var p=new CharacterExperience();var amount=BigInteger.TEN.pow(100);p.award(amount);assertEquals(amount,p.totalXp());assertTrue(p.level().compareTo(n(Long.MAX_VALUE))>0);assertTrue(p.xpInLevel().signum()>=0);assertTrue(p.xpInLevel().compareTo(p.xpForNextLevel())<0);p.award(amount);assertEquals(amount.multiply(n(5)).divide(n(2)),p.totalXp());}
 @Test public void exactThresholdsAndManyLevelsUseTheOriginalCurve(){for(long level:new long[]{1,2,7,8,99,100,101,1000000,3000000000L}){BigInteger l=n(level),xp=l.subtract(BigInteger.ONE).multiply(l.add(n(5))).multiply(n(10));var p=at(xp.toString());assertEquals(l,p.level());assertEquals(BigInteger.ZERO,p.xpInLevel());if(level>1)assertEquals(l.subtract(BigInteger.ONE),at(xp.subtract(BigInteger.ONE).toString()).level());}}
 @Test public void oldCappedSavesKeepLevelTotalAndNoRetroactiveRewards(){var props=new Properties();props.setProperty("xp","200000");var p=CharacterExperience.read(props);assertEquals(n(100),p.level());assertEquals(n(200000),p.totalXp());assertEquals(BigInteger.ZERO,p.xpInLevel());for(int i=0;i<5;i++){p.write(props);p=CharacterExperience.read(props);assertEquals(n(100),p.level());assertEquals(n(200000),p.totalXp());}p.award(n(1400));assertEquals(n(101),p.level());assertEquals(n(50),p.xpInLevel());assertEquals(n(202100),p.totalXp());}
 @Test public void invalidAwardsAndMissingDataAreSafe(){var p=CharacterExperience.read(new Properties());p.award(n(-1));p.award(BigInteger.ZERO);assertEquals(BigInteger.ONE,p.level());assertEquals(BigInteger.ZERO,p.totalXp());var bad=new Properties();bad.setProperty("xp","bad");assertEquals(BigInteger.ONE,CharacterExperience.read(bad).level());}
}

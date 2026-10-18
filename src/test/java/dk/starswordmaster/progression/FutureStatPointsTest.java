package dk.starswordmaster.progression;
import org.junit.Test;
import java.math.BigInteger;
import java.util.*;
import static org.junit.Assert.*;
public class FutureStatPointsTest {
 private Properties legacy(int level){var p=new Properties();p.setProperty("xpCurveVersion","2");p.setProperty("xp",BigInteger.TEN.multiply(BigInteger.valueOf(level-1)).multiply(BigInteger.valueOf(level+5)).toString());return p;}
 @Test public void freshFutureLevelsGiveThreeStatsAndOnePerk(){var p=new PlayerProgress();for(int level=2;level<=30;level++){var before=p.level();var stats=p.statPoints();var perks=p.points();p.awardXp(BigInteger.valueOf(250));var gained=p.level().subtract(before);assertEquals(stats.add(gained.multiply(BigInteger.valueOf(3))),p.statPoints());assertEquals(perks.add(gained),p.points());}assertEquals(p.toProperties(),PlayerProgress.fromProperties(p.toProperties()).toProperties());}
 @Test public void legacyAllocatedAndUnusedPointsArePreservedBeforeThreePointFutureGains(){var props=legacy(10);props.setProperty("stat.STRENGTH","8");var p=PlayerProgress.fromProperties(props);assertEquals(8,p.stat(PlayerProgress.Stat.STRENGTH));assertEquals(BigInteger.valueOf(37),p.statPoints());var before=p.level();p.awardXp(BigInteger.valueOf(10000));assertEquals(BigInteger.valueOf(37).add(p.level().subtract(before).multiply(BigInteger.valueOf(3))),p.statPoints());for(int i=0;i<20;i++){var saved=p.toProperties();p=PlayerProgress.fromProperties(saved);assertEquals(saved,p.toProperties());}}
 @Test public void legacyFullyAllocatedStatsAreNeverConfiscated(){var props=legacy(100);props.setProperty("stat.SPIRIT","495");var p=PlayerProgress.fromProperties(props);assertEquals(495,p.stat(PlayerProgress.Stat.SPIRIT));assertEquals(BigInteger.ZERO,p.statPoints());p.awardXp(BigInteger.valueOf(1400));assertEquals(101,p.level().intValueExact());assertEquals(BigInteger.valueOf(3),p.statPoints());assertEquals(495,PlayerProgress.fromProperties(p.toProperties()).stat(PlayerProgress.Stat.SPIRIT));}
 @Test public void hugeAwardsAndReconnectNeverReplayCredits(){var p=new PlayerProgress();p.awardXp(BigInteger.TEN.pow(100));assertEquals(p.level().subtract(BigInteger.ONE).multiply(BigInteger.valueOf(3)),p.statPoints());var saved=p.toProperties();for(int i=0;i<10;i++)p=PlayerProgress.fromProperties(p.toProperties());assertEquals(saved,p.toProperties());}
}

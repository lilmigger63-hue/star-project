package dk.starswordmaster.progression;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class KillExperience {
 private KillExperience(){}
 /** Decimal multiplication avoids overflow even when finite health exceeds long range. */
 public static BigInteger baseReward(double maxHealth,double multiplier){
  if(!Double.isFinite(maxHealth)||maxHealth<=0||!Double.isFinite(multiplier)||multiplier<=0)return BigInteger.ZERO;
  return BigDecimal.valueOf(maxHealth).multiply(BigDecimal.valueOf(multiplier)).setScale(0,RoundingMode.CEILING).toBigIntegerExact().max(BigInteger.ONE);
 }
 /** Weak entity keys deduplicate callbacks without retaining unloaded/dead entities. Server thread only. */
 public static final class DeathClaims {
  private final Set<Object> claimed=Collections.newSetFromMap(new WeakHashMap<>());
  public boolean claim(Object victim){return victim!=null&&claimed.add(victim);}
  public void clear(){claimed.clear();}
 }
}

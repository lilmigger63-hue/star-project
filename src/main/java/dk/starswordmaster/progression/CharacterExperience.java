package dk.starswordmaster.progression;
import java.math.BigInteger;
import java.util.Properties;
/** Exact character XP and curve arithmetic; no gameplay level ceiling or floating-point inversion. */
public final class CharacterExperience {
 private static final BigInteger ONE=BigInteger.ONE,TWO=BigInteger.TWO,TEN=BigInteger.TEN;
 private BigInteger total=BigInteger.ZERO,offset=BigInteger.ZERO,level=ONE;
 private int half;
 public BigInteger level(){return level;}
 public BigInteger totalXp(){return total;}
 public BigInteger xpInLevel(){return total.subtract(offset).subtract(atLevel(level));}
 public BigInteger xpForNextLevel(){return level.multiply(BigInteger.valueOf(20)).add(BigInteger.valueOf(50));}
 private static BigInteger atLevel(BigInteger level){return TEN.multiply(level.subtract(ONE)).multiply(level.add(BigInteger.valueOf(5)));}
 private void updateLevel(){level=total.subtract(offset).multiply(TEN).add(BigInteger.valueOf(900)).sqrt().subtract(BigInteger.valueOf(20)).divide(TEN);}
 /** The multiplier is determined once, from the level before this complete reward. */
 public void award(BigInteger base){
  if(base==null||base.signum()<=0)return;
  BigInteger reward=base;
  if(level.compareTo(BigInteger.valueOf(8))>=0){var parts=base.multiply(BigInteger.valueOf(3)).add(BigInteger.valueOf(half)).divideAndRemainder(TWO);reward=parts[0];half=parts[1].intValueExact();}
  total=total.add(reward);updateLevel();
 }
 public void write(Properties p){p.setProperty("xp",total.toString());p.setProperty("xpCurveVersion","2");p.setProperty("xpLegacyOffset",offset.toString());p.setProperty("xpHalf",Integer.toString(half));}
 public static BigInteger nonnegative(Properties p,String key){try{return new BigInteger(p.getProperty(key,"0")).max(BigInteger.ZERO);}catch(NumberFormatException e){return BigInteger.ZERO;}}
 public static CharacterExperience read(Properties p){
  var result=new CharacterExperience();result.total=nonnegative(p,"xp");
  if(p.getProperty("xpCurveVersion","").equals("2")){
   result.offset=nonnegative(p,"xpLegacyOffset").min(result.total);result.half=nonnegative(p,"xpHalf").equals(ONE)?1:0;
  }else{
   // Old saves displayed level 100 and zero progress even when their total contained excess XP.
   // Retain that total and old rewards, then resume the original curve from level 100.
   result.offset=result.total.subtract(atLevel(BigInteger.valueOf(100))).max(BigInteger.ZERO);
  }
  result.updateLevel();return result;
 }
}

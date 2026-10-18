package dk.starswordmaster.progression;
import java.math.BigInteger;
import java.util.Properties;
/** A one-time historical credit preserves legacy five-point levels; future levels always earn three. */
public record StatPointBudget(BigInteger legacyCredit) {
 public static final int PER_LEVEL=3;
 public static final StatPointBudget FRESH=new StatPointBudget(BigInteger.ZERO);
 public BigInteger entitlement(BigInteger level){return level.subtract(BigInteger.ONE).multiply(BigInteger.valueOf(PER_LEVEL)).add(legacyCredit);}
 public void write(Properties p){p.setProperty("statPointVersion","2");p.setProperty("statPointLegacyCredit",legacyCredit.toString());}
 public static StatPointBudget read(Properties p,BigInteger level){
  var credit="2".equals(p.getProperty("statPointVersion"))?CharacterExperience.nonnegative(p,"statPointLegacyCredit"):level.subtract(BigInteger.ONE).multiply(BigInteger.TWO);
  return new StatPointBudget(credit);
 }
}

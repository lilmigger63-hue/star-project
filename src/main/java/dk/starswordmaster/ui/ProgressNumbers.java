package dk.starswordmaster.ui;
import java.math.BigInteger;
/** Compact display only; exact original values remain available to tooltips and calculations. */
public final class ProgressNumbers {
 private ProgressNumbers(){}
 public static String compact(BigInteger value){
  String digits=value.toString();if(digits.length()<=6)return digits;
  return digits.charAt(0)+"."+digits.substring(1,4)+"e"+(digits.length()-1);
 }
}

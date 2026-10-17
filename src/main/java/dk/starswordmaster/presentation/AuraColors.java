package dk.starswordmaster.presentation;
import java.util.UUID;
/** UUID-derived HSV identity. No RNG, save migration or render/tick-dependent seed. */
public final class AuraColors {
 private AuraColors(){}
 private static long mix(long x){x=(x^(x>>>30))*0xbf58476d1ce4e5b9L;x=(x^(x>>>27))*0x94d049bb133111ebL;return x^(x>>>31);}
 public static int forPlayer(UUID id){
  long seed=mix(id.getMostSignificantBits()^Long.rotateLeft(id.getLeastSignificantBits(),23));
  double h=(seed>>>11)*0x1.0p-53,s=.72+((mix(seed)>>>11)*0x1.0p-53)*.22,v=.9+((mix(seed+1)>>>11)*0x1.0p-53)*.1;
  double part=h*6,f=part-Math.floor(part),p=v*(1-s),q=v*(1-s*f),t=v*(1-s*(1-f));
  double[] rgb=switch((int)part%6){case 0->new double[]{v,t,p};case 1->new double[]{q,v,p};case 2->new double[]{p,v,t};case 3->new double[]{p,q,v};case 4->new double[]{t,p,v};default->new double[]{v,p,q};};
  return ((int)Math.round(rgb[0]*255)<<16)|((int)Math.round(rgb[1]*255)<<8)|(int)Math.round(rgb[2]*255);
 }
 public static int shade(int rgb,float factor){float f=Float.isFinite(factor)?Math.clamp(factor,0,2):1;return channel(rgb>>16,f)<<16|channel(rgb>>8,f)<<8|channel(rgb,f);}
 private static int channel(int n,float f){return Math.clamp(Math.round((n&255)*f),0,255);}
 public static int highlight(int rgb,float amount){float a=Float.isFinite(amount)?Math.clamp(amount,0,1):0;int out=0;for(int shift:new int[]{16,8,0}){int c=(rgb>>shift)&255;out|=Math.round(c+(255-c)*a)<<shift;}return out;}
}

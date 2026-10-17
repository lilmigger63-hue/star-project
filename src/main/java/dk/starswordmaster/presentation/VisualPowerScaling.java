package dk.starswordmaster.presentation;
import java.math.BigInteger;
/** Shared cosmetic power: no gameplay, resource or reach calculations depend on this. */
public final class VisualPowerScaling {
 public static final double MAX_RADIUS=12;
 public static final float MAX_SPRITE=3;
 private VisualPowerScaling(){}
 public static float power(BigInteger level){
  if(level==null||level.compareTo(BigInteger.ONE)<=0)return 0;
  if(level.bitLength()>40)return 1;
  double n=level.doubleValue();return (float)((n-1)/(n+39));
 }
 public static float safe(float power){return Float.isFinite(power)?Math.clamp(power,0,1):0;}
 public static float radius(float power){return .65f+1.6f*safe(power);}
 public static float sprite(float power){return .7f+1.8f*safe(power);}
 public static float trail(float power){return .75f+1.75f*safe(power);}
 public static float intensity(float power){return .5f+.5f*safe(power);}
 public static float animationWeight(float power){return .75f+.25f*safe(power);}
 public static ParticlePattern.Point scale(ParticlePattern.Point p,float power){
  double r=radius(power),x=p.x()*r,y=p.y(),z=p.z();
  if(p.space()==ParticlePattern.Space.RAY){y*=r;z=Math.clamp(z,0,1);}
  else if(p.space()==ParticlePattern.Space.HAND){y*=r;z*=Math.min(1.3,r);}
  else{y=1+(y-1)*(.8+.5*safe(power));z*=p.ink()==ParticlePattern.Ink.STREAK?trail(power):r;}
  double length=Math.sqrt(x*x+y*y+z*z);if(length>MAX_RADIUS){double f=MAX_RADIUS/length;x*=f;y*=f;z*=f;}
  return new ParticlePattern.Point(x,y,z,p.space(),p.ink(),Math.min(MAX_SPRITE,p.size()*sprite(power)),p.life(),p.angle(),p.tone());
 }
}

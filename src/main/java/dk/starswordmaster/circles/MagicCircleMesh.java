package dk.starswordmaster.circles;

/** Pure bounded layered sigil choreography in native palm coordinates. */
public final class MagicCircleMesh {
 public static final int MAX_LAYERS=3;
 private MagicCircleMesh(){}
 public static float layerAngle(int layer,float age,dk.starswordmaster.presentation.AbilityPresentation.Element element){
  float speed=switch(element){case LIGHTNING->.044f;case WATER->.021f;case EARTH->.016f;case DARK->.029f;default->.025f;};
  float t=Float.isFinite(age)?age:0;
  return t*speed*(layer==1?-1.65f:layer==2?.68f:1)*(1+.035f*(float)Math.sin(t*.12f+layer));
 }
 public static float layerBrightness(int layer,float age,dk.starswordmaster.presentation.AbilityPresentation.Element element){
  float rate=element==dk.starswordmaster.presentation.AbilityPresentation.Element.LIGHTNING?1.5f:.24f;
  return .93f+.07f*(float)Math.sin((Float.isFinite(age)?age:0)*rate+layer*1.7f);
 }
 public record Profile(float radius,float alpha,int layers){}
 public static Profile profile(float age,int duration,float power,int rank,int swordLevel,int quality,double distanceSquared){
  float p=Float.isFinite(power)?Math.clamp(power,0,1):0;
  float refinement=.72f*p+.18f*Math.clamp(rank,0,4)/4f+.10f*(Math.clamp(swordLevel,1,100)-1)/99f;
  float progress=Float.isFinite(age)?Math.clamp(age/Math.max(1,duration),0,1):1;
  float charge=Math.clamp(progress/.22f,0,1);
  float release=Math.clamp((1-progress)/.38f,0,1);
  charge=charge*charge*(3-2*charge);release=release*release*(3-2*release);
  float radius=(.24f+.23f*refinement)*(.7f+.3f*charge)*(1+.14f*(1-release));
  int layers=quality<=0||distanceSquared>1600?1:quality==1||distanceSquared>400?2:1+(refinement>.13f?1:0)+(refinement>.54f?1:0);
  return new Profile(radius,.9f*charge*release,layers);
 }
}

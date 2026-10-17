package dk.starswordmaster.circles;

/** Pure bounded layered sigil choreography in native palm coordinates. */
public final class MagicCircleMesh {
 public static final int MAX_LAYERS=3;
 private MagicCircleMesh(){}
 public record Profile(float radius,float alpha,int layers){}
 public static Profile profile(float age,int duration,float power,int rank,int swordLevel,int quality,double distanceSquared){
  float p=Float.isFinite(power)?Math.clamp(power,0,1):0;
  float refinement=.72f*p+.18f*Math.clamp(rank,0,4)/4f+.10f*(Math.clamp(swordLevel,1,100)-1)/99f;
  float progress=Float.isFinite(age)?Math.clamp(age/Math.max(1,duration),0,1):1;
  float charge=Math.clamp(progress/.22f,0,1);
  float release=Math.clamp((1-progress)/.38f,0,1);
  float radius=(.24f+.23f*refinement)*(.7f+.3f*charge)*(1+.14f*(1-release));
  int layers=quality<=0||distanceSquared>1600?1:quality==1||distanceSquared>400?2:1+(refinement>.13f?1:0)+(refinement>.54f?1:0);
  return new Profile(radius,.9f*charge*release,layers);
 }
}

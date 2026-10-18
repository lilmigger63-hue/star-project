package dk.starswordmaster.presentation;
/** Cosmetic timing only. Server damage and healing still occur on the original confirmed activation. */
public final class AbilityVfxTimeline {
 public enum Phase { ANTICIPATION,ACTIVATION,PRIMARY,IMPACT,DISSIPATION,DONE }
 public record Frame(Phase phase,float charge,float release,float travel,float impact,float collapse,float alpha){}
 private static final Frame DONE=new Frame(Phase.DONE,0,0,1,1,1,0);
 private AbilityVfxTimeline(){}
 public static Frame sample(float age,int duration,float delay){
  if(!Float.isFinite(age)||duration<=0||age<0||age>=duration+8)return DONE;
  float offset=Math.clamp(Float.isFinite(delay)?delay:0,0,1)*duration*.28f;
  float local=age-offset,p=age/duration;
  Phase phase=p<.16f?Phase.ANTICIPATION:p<.30f?Phase.ACTIVATION:p<.64f?Phase.PRIMARY:p<.80f?Phase.IMPACT:Phase.DISSIPATION;
  float charge=smooth(local/(duration*.22f)),release=smooth((local-duration*.16f)/(duration*.18f));
  float travel=smooth((local-duration*.18f)/(duration*.58f)),impact=smooth((local-duration*.58f)/(duration*.18f));
  // A delayed ornament must still end with its parent, never extend the scene lifetime.
  float collapse=smooth((age-duration*.8f)/(duration*.2f+8));
  return new Frame(phase,charge,release,travel,impact,collapse,charge*(1-collapse));
 }
 private static float smooth(float value){float x=Math.clamp(value,0,1);return x*x*(3-2*x);}
}

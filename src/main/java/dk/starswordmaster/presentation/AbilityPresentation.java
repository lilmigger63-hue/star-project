package dk.starswordmaster.presentation;
import java.util.*;
/** Cosmetic catalogue only: no costs, damage, cooldowns or progression. */
public record AbilityPresentation(Clip clip,Element element,Pattern pattern,double radius,int samples) {
 public enum Clip { SLASH(12),SWEEP(18),RAPID(12),HEAVY(18),THRUST(12),GUARD(16),DASH(10),CAST(14),CHANNEL(20),AURA(18),HEAL(20);
  private final int ticks;Clip(int ticks){this.ticks=ticks;}public int ticks(){return ticks;}
  public String id(){return "ability_"+name().toLowerCase(Locale.ROOT);}
 }
 public enum Element { STEEL,FIRE,FROST,STAR,HEALING }
 public enum Pattern { SLASH,CHOP,CAGE,THRUST,RAY,BURST,RING,WARD,AURA,HEAL,DASH }
 public int ticks(){return clip.ticks();}
 public int elapsed(long sentAt,long now){return (int)Math.max(0,Math.min(ticks(),now-sentAt));}
 public static final Map<String,AbilityPresentation> ALL;
 static {
  var map=new LinkedHashMap<String,AbilityPresentation>();
  put(map,Clip.CAST,Element.FIRE,Pattern.RAY,1,8,"spark");
  put(map,Clip.CAST,Element.FROST,Pattern.RAY,1,8,"frost_touch");
  put(map,Clip.AURA,Element.STAR,Pattern.AURA,0.8,5,"light");
  put(map,Clip.HEAL,Element.HEALING,Pattern.HEAL,0.65,5,"mend");
  put(map,Clip.HEAL,Element.HEALING,Pattern.HEAL,0.8,6,"renew");
  put(map,Clip.SLASH,Element.STEEL,Pattern.SLASH,1.6,7,"quick_cut");
  put(map,Clip.RAPID,Element.STEEL,Pattern.SLASH,1.8,9,"rapid_slash");
  put(map,Clip.DASH,Element.STEEL,Pattern.DASH,0.7,4,"quick_dash");
  put(map,Clip.HEAVY,Element.STEEL,Pattern.CHOP,2.2,9,"heavy_cut");
  put(map,Clip.SWEEP,Element.STEEL,Pattern.SLASH,3,10,"heavy_sweep");
  put(map,Clip.HEAVY,Element.STEEL,Pattern.CHOP,2.8,11,"crushing_edge");
  put(map,Clip.GUARD,Element.STEEL,Pattern.WARD,0.9,6,"guard");
  put(map,Clip.GUARD,Element.STAR,Pattern.WARD,1,7,"mirror_guard");
  put(map,Clip.GUARD,Element.STAR,Pattern.WARD,1.2,9,"guardian_stance");
  put(map,Clip.THRUST,Element.STEEL,Pattern.THRUST,1.6,5,"riposte");
  put(map,Clip.THRUST,Element.STEEL,Pattern.THRUST,2,6,"piercing_thrust");
  put(map,Clip.SLASH,Element.STAR,Pattern.RAY,1,10,"aura_wave");
  put(map,Clip.AURA,Element.STAR,Pattern.AURA,1,6,"blade_focus");
  put(map,Clip.CAST,Element.FIRE,Pattern.BURST,2,10,"fire_burst");
  put(map,Clip.THRUST,Element.FROST,Pattern.RAY,1,10,"ice_lance");
  put(map,Clip.HEAL,Element.HEALING,Pattern.HEAL,1.2,8,"sanctuary");
  put(map,Clip.HEAL,Element.HEALING,Pattern.HEAL,0.95,7,"restoration");
  put(map,Clip.CHANNEL,Element.FROST,Pattern.RING,6,12,"frost_nova");
  put(map,Clip.SLASH,Element.FIRE,Pattern.SLASH,1.9,8,"flame_blade");
  put(map,Clip.SLASH,Element.FROST,Pattern.SLASH,1.9,8,"frost_blade");
  put(map,Clip.THRUST,Element.FIRE,Pattern.RAY,1,12,"flame_lance");
  put(map,Clip.GUARD,Element.FROST,Pattern.WARD,1,6,"frost_ward");
  put(map,Clip.DASH,Element.STAR,Pattern.DASH,0.8,5,"arcane_step");
  put(map,Clip.SWEEP,Element.FROST,Pattern.RING,5,12,"glacier_sweep");
  put(map,Clip.HEAVY,Element.STEEL,Pattern.RING,5,12,"seismic_cut");
  put(map,Clip.CHANNEL,Element.FIRE,Pattern.RING,5,12,"ember_ring");
  put(map,Clip.CHANNEL,Element.FROST,Pattern.CAGE,1.2,8,"ice_prison");
  ALL=Collections.unmodifiableMap(map);
 }
 private static void put(Map<String,AbilityPresentation> map,Clip clip,Element element,Pattern pattern,double radius,int samples,String...ids){
  for(String id:ids)if(map.put(id,new AbilityPresentation(clip,element,pattern,radius,samples))!=null)throw new IllegalStateException("Duplicate presentation: "+id);
 }
}

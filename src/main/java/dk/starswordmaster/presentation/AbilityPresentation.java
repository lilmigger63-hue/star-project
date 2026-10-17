package dk.starswordmaster.presentation;
import java.util.*;
/** Cosmetic catalogue only: no costs, damage, cooldowns or progression. */
public record AbilityPresentation(Clip clip,Element element,Pattern pattern,double radius,int samples,String abilityId,int durationTicks) {
 public AbilityPresentation(Clip clip,Element element,Pattern pattern,double radius,int samples){this(clip,element,pattern,radius,samples,"",0);}
 public AbilityPresentation(Clip clip,Element element,Pattern pattern,double radius,int samples,String abilityId){this(clip,element,pattern,radius,samples,abilityId,0);}
 public String animationId(){return abilityId.isEmpty()?clip.id():"skill_"+abilityId;}
 public enum Clip { SLASH(12),SWEEP(18),RAPID(12),HEAVY(18),THRUST(12),GUARD(16),DASH(10),CAST(14),CHANNEL(20),AURA(18),HEAL(20),DUAL_CAST(20),GROUND_SLAM(22),OVERHEAD_CAST(30),SWORD_RELEASE(16),LIGHTNING_CHANNEL(20),WATER_WEAVE(22),EARTH_RAISE(20),SHADOW_SUMMON(26);
  private final int ticks;Clip(int ticks){this.ticks=ticks;}public int ticks(){return ticks;}
  public String id(){return (ordinal()<11?"ability_":"style_")+name().toLowerCase(Locale.ROOT);}
 }
 public enum Element { STEEL,FIRE,FROST,STAR,HEALING,LIGHTNING,WATER,EARTH,DARK }
 public enum Pattern { SLASH,CHOP,CAGE,THRUST,RAY,BURST,RING,WARD,AURA,HEAL,DASH }
 public int ticks(){return durationTicks>0?durationTicks:clip.ticks();}
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
  map.put("static_spark",new AbilityPresentation(Clip.CAST,Element.LIGHTNING,Pattern.RAY,1,7,"static_spark",12));
  map.put("chain_lightning",new AbilityPresentation(Clip.LIGHTNING_CHANNEL,Element.LIGHTNING,Pattern.RAY,1,12,"chain_lightning",20));
  map.put("thunder_step",new AbilityPresentation(Clip.DASH,Element.LIGHTNING,Pattern.DASH,1,6,"thunder_step",12));
  map.put("heavens_wrath",new AbilityPresentation(Clip.OVERHEAD_CAST,Element.LIGHTNING,Pattern.BURST,5,12,"heavens_wrath",30));
  map.put("water_bolt",new AbilityPresentation(Clip.DUAL_CAST,Element.WATER,Pattern.RAY,1,7,"water_bolt",14));
  map.put("tidal_wave",new AbilityPresentation(Clip.WATER_WEAVE,Element.WATER,Pattern.SLASH,4,12,"tidal_wave",22));
  map.put("aqua_blade",new AbilityPresentation(Clip.SWORD_RELEASE,Element.WATER,Pattern.SLASH,2,9,"aqua_blade",16));
  map.put("ocean_prison",new AbilityPresentation(Clip.WATER_WEAVE,Element.WATER,Pattern.CAGE,2,12,"ocean_prison",26));
  map.put("stone_spike",new AbilityPresentation(Clip.EARTH_RAISE,Element.EARTH,Pattern.BURST,1,8,"stone_spike",14));
  map.put("earthen_wall",new AbilityPresentation(Clip.EARTH_RAISE,Element.EARTH,Pattern.WARD,1.5,8,"earthen_wall",20));
  map.put("quake_blade",new AbilityPresentation(Clip.GROUND_SLAM,Element.EARTH,Pattern.RING,4,12,"quake_blade",22));
  map.put("worldbreaker",new AbilityPresentation(Clip.GROUND_SLAM,Element.EARTH,Pattern.BURST,6,12,"worldbreaker",30));
  map.put("shadow_bolt",new AbilityPresentation(Clip.SHADOW_SUMMON,Element.DARK,Pattern.RAY,1,8,"shadow_bolt",14));
  map.put("curse_mark",new AbilityPresentation(Clip.SHADOW_SUMMON,Element.DARK,Pattern.WARD,1,8,"curse_mark",18));
  map.put("shadow_edge",new AbilityPresentation(Clip.SWORD_RELEASE,Element.DARK,Pattern.SLASH,2,9,"shadow_edge",16));
  map.put("eclipse_nova",new AbilityPresentation(Clip.SHADOW_SUMMON,Element.DARK,Pattern.RING,6,12,"eclipse_nova",30));
  map.put("astral_crescent",new AbilityPresentation(Clip.SWORD_RELEASE,Element.STAR,Pattern.SLASH,3,12,"astral_crescent",16));
  map.put("falling_star",new AbilityPresentation(Clip.GROUND_SLAM,Element.STAR,Pattern.RING,4,12,"falling_star",22));
  map.put("phantom_blades",new AbilityPresentation(Clip.RAPID,Element.STAR,Pattern.SLASH,2,12,"phantom_blades",20));
  map.put("celestial_rush",new AbilityPresentation(Clip.DASH,Element.STAR,Pattern.DASH,1,8,"celestial_rush",14));
  map.put("sovereign_judgment",new AbilityPresentation(Clip.OVERHEAD_CAST,Element.STAR,Pattern.CHOP,5,12,"sovereign_judgment",28));
  map.put("heavenly_sword_domain",new AbilityPresentation(Clip.OVERHEAD_CAST,Element.STAR,Pattern.RING,6,12,"heavenly_sword_domain",32));
  ALL=Collections.unmodifiableMap(map);
 }
 private static void put(Map<String,AbilityPresentation> map,Clip clip,Element element,Pattern pattern,double radius,int samples,String...ids){
  for(String id:ids)if(map.put(id,new AbilityPresentation(clip,element,pattern,radius,samples,id))!=null)throw new IllegalStateException("Duplicate presentation: "+id);
 }
}

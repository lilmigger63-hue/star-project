package dk.starswordmaster.presentation;
import dk.starswordmaster.progression.Ability;
import dk.starswordmaster.presentation.ParticlePattern.Ink;
/** Replaces existing particle submissions, never adds particles or gameplay projectiles. */
public final class CombatSpriteStyle {
 private CombatSpriteStyle(){}
 public static Ink ink(AbilityPresentation style,Ink source){
  var ability=Ability.ALL.get(style.abilityId());boolean sword=ability!=null&&ability.stamina()>0;
  if(source==Ink.FLASH)return sword?Ink.IMPACT_SPARK:Ink.MAGIC_FLARE;
  if(sword&&(source==Ink.ARC||source==Ink.BLADE))return switch(style.abilityId()){
   case "heavy_cut","crushing_edge","seismic_cut","worldbreaker","sovereign_judgment"->Ink.SLASH_HEAVY;
   case "rapid_slash","celestial_rush","phantom_blades"->Ink.SLASH_PIXEL;
   case "aura_wave","astral_crescent","falling_star","heavenly_sword_domain"->Ink.SLASH_FINISH;
   case "flame_blade","frost_blade","aqua_blade","shadow_edge","quake_blade","thunder_step"->Ink.SLASH_CURVE;
   default->Ink.SLASH_LIGHT;
  };
  return source;
 }
}

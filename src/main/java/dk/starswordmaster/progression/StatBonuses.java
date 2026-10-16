package dk.starswordmaster.progression;
/** Stat-only contributions: fractions for speed/damage/cooldown/regen, flat amounts for resources/health. */
public record StatBonuses(double abilityDamage,double miningSpeed,double cooldownReduction,double movementSpeed,long stamina,double health,long mana,double manaRegeneration) {
 public static StatBonuses from(PlayerProgress p){
  return new StatBonuses(p.strengthDamageBonus(),Math.min(0.5,0.01*p.effectiveStat(PlayerProgress.Stat.STRENGTH)),p.agilityCooldownReduction(),Math.min(0.2,0.002*p.effectiveStat(PlayerProgress.Stat.AGILITY)),p.enduranceStaminaBonus(),Math.min(10,0.1*p.effectiveStat(PlayerProgress.Stat.ENDURANCE)),p.spiritManaBonus(),Math.min(1,0.01*p.effectiveStat(PlayerProgress.Stat.SPIRIT)));
 }
}

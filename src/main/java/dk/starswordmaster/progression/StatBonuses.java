package dk.starswordmaster.progression;
/** Stat-only contributions: fractions for speed/damage/cooldown/regen, flat amounts for resources/health. */
public record StatBonuses(double abilityDamage,double miningSpeed,double cooldownReduction,double movementSpeed,int stamina,double health,int mana,double manaRegeneration) {
 public static StatBonuses from(PlayerProgress p){
  return new StatBonuses(p.strengthDamageBonus(),Math.min(0.5,0.01*p.stat(PlayerProgress.Stat.STRENGTH)),p.agilityCooldownReduction(),Math.min(0.2,0.002*p.stat(PlayerProgress.Stat.AGILITY)),p.enduranceStaminaBonus(),Math.min(10,0.1*p.stat(PlayerProgress.Stat.ENDURANCE)),p.spiritManaBonus(),Math.min(1,0.01*p.stat(PlayerProgress.Stat.SPIRIT)));
 }
}

# Automatic class evolution — 0.9.0

Choose one permanent class manually in K at **character level 5**. Before level 5 the character is Uninitiated. The server and save loader use the same threshold. Class selection never changes through a skill reset.

Evolution is derived from the selected class and exact character level. It is automatic at 10, 20 and 30, needs no learned skill and costs no perk points. Late class selection immediately uses the current level's stage. The existing skill-based ranks 1–4, Mastery and ultimate nodes remain separate, at one perk point each, without learning prerequisites.

All values below are **total evolution bonuses**, not additional awards per transition.

| Class | Level 10 | Level 20 | Level 30 |
| --- | --- | --- | --- |
| Swordsman title | Blade Adept | Swordmaster | Astral Champion |
| Sword/enchanted-sword ability damage | +4% | +8% | +12% |
| Effective Agility | +1 | +2 | +3 |
| Maximum stamina | +5 | +10 | +15 |
| Stamina cost reduction | 0% | 5% | 10% |
| Mage title | Arcanist | Archmage | Celestial Magus |
| Offensive magic damage | +4% | +8% | +12% |
| Maximum mana | +10 | +20 | +30 |
| Mana regeneration | +5% | +10% | +15% |
| Mana cost reduction | 0% | 5% | 10% |
| Spellblade title | Runic Knight | Arcane Battlemaster | Eclipse Knight |
| Enchanted-sword ability damage | +3% | +6% | +10% |
| Maximum mana and stamina | +5 each | +10 each | +15 each |
| Mana and stamina cost reduction | 3% | 6% | 8% |

Damage and cost multipliers combine with existing stat, style, rank and passive calculations. Defensive/healing abilities receive no offensive damage bonus. Agility is an effective stat, never allocated points; existing movement/cooldown caps apply. Capacities are flat additions to existing formulas. The derived preset stops at stage 3 even with arbitrarily large BigInteger levels.

H shows evolution separately from rank, its current total effects and effective stats. K's hub shows the evolution title when readable, with a full tooltip. Actual milestone transitions coalesce to one class-colored message and a single 12-particle vanilla dust ring; logging in with an evolved character is silent. The existing cosmetic power input gains 0.02 per stage, capped at 1, to refine sword/magic presentation without changing gameplay, particle budgets, rank or the player's stable UUID aura hue.

No evolution reward or bonus is written into a save. Existing schema-1 Properties fields, character XP curve, level-8 reward multiplier and one perk point per level are unchanged. From 0.10.0 future levels grant three stat points; the separate one-time legacy stat credit preserves old budgets. Repeated loading, respawn, reconnect and reset therefore cannot accumulate evolution bonuses. Old valid class choices, learned IDs, allocated stats and skill proficiency are retained.

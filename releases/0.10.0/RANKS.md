# Class ranks and passive training

The current tree has **85 nodes: 54 active abilities and 31 passive nodes**. Rank 4, introduced in 0.4.1, costs one perk point in K and applies permanently to the chosen class. It is passive, cannot occupy a skill slot and does not cast a separate animation.

| Class | Rank 4 / ID | Visual parent | Learning cost | Effective attribute bonuses | Extra damage |
| --- | --- | --- | --- | --- | --- |
| Swordsman | Astral Sword Sovereign / `astral_sword_sovereign` | `sword_mastery` | 1 perk point | +3 Strength, +2 Agility | +5% sword ability damage |
| Mage | Celestial Archon / `celestial_archon` | `arcane_mastery` | 1 perk point | +4 Spirit, +1 Endurance | +5% offensive magic damage |
| Spellblade | Eclipse Sovereign / `eclipse_sovereign` | `spellblade_mastery` | 1 perk point | +2 Strength, +2 Spirit, +1 Agility | +5% enchanted sword ability damage |

Rank nodes connect directly to their Mastery parent with more than 200 layout units of separation. Existing pan/zoom and hit testing are reused, and the initial viewport fits every node. The fourth rank also increases sword aura detail while preserving the stable personal color.

## Bonuses and persistence

`stat()` remains the allocated base value. `effectiveStat()` adds learned passive and rank bonuses without modifying allocated values or granting spendable points. Effective values use `long`, so a base at `Integer.MAX_VALUE` does not overflow. H shows effective values, passive contributions, rank titles and damage categories with explanatory tooltips.

Existing attribute formulas/caps also apply to effective values. The existing rank-3-or-higher +0.15 damage multiplier remains at Rank 4. The category bonus multiplies afterward by **1.05**, alongside existing style/training bonuses:

`(1 + 0.025 × effective Strength + Mastery bonus) × existing style bonus × category bonus`

The original sword category includes Quick Cut, Heavy Cut, Storm Sweep, Riposte, Aura Wave, Rapid Slash, Crushing Edge, Piercing Thrust and Seismic Cut. Enchanted sword attacks count as swords when that category is checked. Original offensive magic includes Spark, Frost Touch, Fire Burst, Ice Lance, Frost Nova, Flame Lance, Ember Ring and Ice Shackles. Original enchanted sword attacks include Flame Blade, Frost Blade and Glacier Sweep. Expansion abilities use the same category rules in production code.

Healing, dashes, defensive effects, ordinary Better Combat attacks and vanilla's subsequent burn damage receive no extra category bonus. The existing Strength contribution continues to affect ability damage. The same server-authoritative effects code performs damage calculations; rank bonuses do not change targeting, reach, timing, costs or cooldowns.

As of 0.10.0 server `unlock()` validates a registered ID, not already learned, and one available perk point. Client `canUnlock()` mirrors those rules. All class nodes can be learned; rank derives from the highest owned rank node of the selected permanent class, so buying its ultimate directly grants rank 4 without Mastery. Buying another class's rank node does not replace that identity. The existing +0.15 damage multiplier depends on rank 3+, so a directly purchased own-class ultimate also receives it; individual learned-only resource passives still require their nodes. Save/snapshot accounting preserves old learned sets; fixed attribute modifier IDs prevent stacking on reload, repeated ticks and reconnect.

## Fifteen optional training nodes

Five branches have three tiers each. Each tier costs one perk point without predecessor or skill-level gates and adds **+1 effective attribute**. Every class can learn them. They are optional and are not prerequisites for Rank 4. The table records visual connections and training themes, not learning gates.

| Branch / ID prefix | Visual connection | Skill | Attribute per tier |
| --- | --- | --- | --- |
| Blade Training / `strength_training` | `impact_training` | Sword | +1 Strength |
| Flow Drills / `agility_training` | `flow_training` | Sword | +1 Agility |
| Guardian Drills / `endurance_training` | `guard_training` | Sword | +1 Endurance |
| Ember Meditation / `spirit_training` | `fire_affinity` | Fire | +1 Spirit |
| Life Meditation / `restorative_training` | `restorative_affinity` | Support | +1 Spirit |

Every node has original sharp 32 × 32 pixel art. Rank icons depict a golden astral sword, a luminous cosmic rune and a dark eclipse around an enchanted blade. The finishing pass preserves these icons. `en_us.json` is the presentation source of truth; `da_dk.json` intentionally contains identical English mod strings. Localization tests cover every ability name/description and dynamic UI family. Historical generators may contain old localized metadata; do not overwrite reviewed English locale files when regenerating artwork.

Tests are excluded from the release JAR. See [VALIDATION.md](VALIDATION.md) for actual results.

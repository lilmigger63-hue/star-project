# Expanded magic and personal sword auras

The tree has **85 nodes: 54 active and 31 passive**. The 0.5.0 expansion introduced 16 elemental spells, six sword techniques and `aura_sense`. Existing ability IDs, classes, four ranks, effective attributes, points, XP rules, slots and HUD positions remain compatible. No new dependencies or mobs; saves remain schema 1. Lightning, Water, Earth and Dark use existing `practice.<SKILL>` fields, starting at zero in old worlds. Auras add no save field.

## Expanded active abilities

Each ability costs one perk point to learn. Mana/stamina and cooldown below are base values before existing stat, class, rank and style modifiers. K shows prerequisites and effective costs. Server calculations determine reach and damage; cosmetic animation does not change them.

| ID | Class | Character / skill level | Mana / stamina | Cooldown, ticks | Effect before existing bonuses |
| --- | --- | --- | --- | --- | --- |
| static_spark | All | 1 / Lightning 1 | 14 / 0 | 50 | 3 damage; target within 16 blocks |
| chain_lightning | All | 10 / Lightning 5 | 30 / 0 | 140 | 7 damage, up to 5 targets; ×0.8 per jump, maximum 5 blocks between targets |
| thunder_step | Spellblade | 10 / Lightning 8 | 22 / 6 | 160 | Short movement impulse; Speed/Resistance for 40 ticks |
| heavens_wrath | Mage | 25 / Lightning 20 | 65 / 0 | 500 | 10 damage and slowing around the aimed target, radius 5 |
| water_bolt | All | 1 / Water 1 | 14 / 0 | 50 | 4 damage; target within 16 blocks |
| tidal_wave | All | 10 / Water 5 | 30 / 0 | 160 | 5 damage and knockback in a forward fan, reach 8 |
| aqua_blade | Spellblade | 10 / Water 8 | 26 / 6 | 140 | 7 damage; target within 4 blocks |
| ocean_prison | Mage | 20 / Water 15 | 40 / 0 | 240 | 6 damage and Slowness IV for 100 ticks |
| stone_spike | All | 1 / Earth 1 | 16 / 0 | 60 | 5 damage; target within 16 blocks |
| earthen_wall | All | 10 / Earth 5 | 24 / 0 | 220 | Resistance for 100 ticks and absorption for 80; places no blocks |
| quake_blade | Swordsman / Spellblade | 15 / Earth 10 | 34 / 10 | 180 | 8 damage and slowing, radius 4 |
| worldbreaker | Mage | 25 / Earth 20 | 70 / 0 | 600 | 14 damage and slowing, radius 6 |
| shadow_bolt | All | 1 / Dark 1 | 16 / 0 | 60 | 5 damage; target within 16 blocks |
| curse_mark | All | 10 / Dark 5 | 24 / 0 | 140 | 3 damage and Weakness for 100 ticks |
| shadow_edge | Spellblade | 10 / Dark 8 | 28 / 6 | 160 | 8 damage; target within 4 blocks |
| eclipse_nova | Mage | 25 / Dark 20 | 70 / 0 | 600 | 13 damage and Weakness, radius 6 |
| astral_crescent | Swordsman | 12 / Sword 8 | 24 / 7 | 100 | 8 damage in a forward sword fan, reach 5 |
| falling_star | Swordsman | 18 / Sword 12 | 34 / 10 | 180 | 11 damage and slowing, radius 4 |
| phantom_blades | Swordsman | 20 / Sword 15 | 32 / 9 | 160 | One 10-damage hit; blade afterimages are cosmetic |
| celestial_rush | Swordsman | 18 / Sword 12 | 28 / 8 | 160 | 8 damage in a sword fan and a short forward impulse on a valid target |
| sovereign_judgment | Swordsman | 30 / Sword 25 | 50 / 14 | 320 | 16 damage within 5 blocks; requires Rank 4 |
| heavenly_sword_domain | Swordsman | 40 / Sword 30 | 80 / 18 | 700 | 15 damage and Weakness, radius 6; requires Sovereign's Judgment |

Areas affect at most eight hostile mobs. Players, pets, teammates and targets without line of sight are excluded. Chain Lightning hits each target once. Spell trails and stone formations are cosmetic; they create no extra damaging projectiles, permanent terrain edits, fire or explosion damage. Earthen Wall retains its resistance/absorption gameplay rather than physical collisions. Rejected use spends no resources and starts no presentation.

## Personal sword aura

`aura_sense` requires Quick Cut, character level 10 and Sword 3. It is passive, not a sixth slot. Existing characters with Aura Awakening or Element Binding gain an aura automatically; new enchanted sword techniques can also unlock it.

The server derives a bright saturated color from the player's UUID across the full HSV wheel: saturation 72–94%, brightness 90–100%. There is no per-frame randomization or new save field. Color remains stable through death, reconnect and world restart. Rank increases width, light, facets and trails; Rank 4 adds white highlights and a star crown. The Swordsman envelope is 22% wider; Spellblade can gain a brief secondary elemental accent while retaining the personal base color.

The aura uses Minecraft's actual held-item transform for both hands, both perspectives and observers. It follows Better Combat and PAL. Only held swords receive it; inventory icons, dropped items and switched-out weapons do not. Depth testing remains enabled.

## Visual scaling and performance

For character level L, `p=(L−1)/(L+39)` is clamped to 0–1 and handles arbitrarily large `BigInteger` levels safely. Visual radius scales from 0.65 to 2.25; sprites from 0.7 to 2.5; trail length from 0.75 to 2.5. Arm contribution scales from 75 to 100% with unchanged duration, aim and vanilla legs. Hard limits remain **12 blocks of local visual radius, 3 blocks of particle sprite width, 256 new particles/tick, 64 active casters and 64 blocks of draw distance**. Sword auras use at most 64 quads per hand and 1,024 per frame globally for at most 64 players; quality and distance further reduce detail.

H offers **Low / Normal / Ultra**, saved locally in `config/starswordmaster-vfx.txt`. Minecraft's particle settings are respected. Quality modes never increase hard limits. The finishing pass adds separate bounded reusable spell geometry and terrain-sampled Earth formations; current geometry caps and verified behavior are documented in [VALIDATION.md](VALIDATION.md).

The server sends compact appearance state for tracking, changes, join, respawn and dimension travel. Valid casts contain power/color and at most eight real hit positions; clients draw cosmetic patterns locally. Install the same current version on server and clients because the presentation payload is versioned.

See [ABILITY_PRESENTATION.md](ABILITY_PRESENTATION.md) for all 54 ability presentations and [VALIDATION.md](VALIDATION.md) for checks actually completed.

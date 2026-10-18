# Unlimited leveling and legacy migration

Character level has no fixed maximum. XP, levels and available stat/perk points use `BigInteger`. An integer square root computes level without overflowing or looping through every level.

The XP requirement from level L to L+1 remains **50 + 20 × L**. The cumulative curve at the start of L is **10 × (L−1) × (L+5)**.

## Rewards and points

- A character at levels 1–7 **before** a reward receives the normal mod XP.
- A character already level 8+ receives **normal reward × 1.5**. The next-level requirement does not increase.
- A reward that crosses level 8 from level 7 uses the original multiplier for the whole reward; the following reward uses ×1.5.
- Half-XP persists between rewards/reloads without repeated rounding losses. An ordinary 100-XP reward becomes exactly 150.
- Every gained level grants **3 stat points in H and 1 perk point in K**. Available points derive from level minus prior purchases, preventing duplicate rewards on multi-level gains, snapshots, death or reload.
- Character XP comes primarily from credited hostile-mob deaths: `max(1, ceil(maximum health × 3))`. The maximum-health attribute is read at death, never remaining health. Player melee, arrows and existing caster-owned magic receive credit; PvP, owned pets/summons, environmental deaths and duplicate callbacks do not grant XP. Monster-interface or MONSTER-spawn-group modded mobs are included. Vanilla XP is unchanged.
- Practice counters remain intact; practice no longer gates learning, with displayed skill levels 1–100. Practice alone gives zero character XP by default. An optional server multiplier restores milestone character XP when desired.

## Existing saves

The same atomic schema-1 properties remain in use. Optional fields are `xpCurveVersion=2`, `xpLegacyOffset` and `xpHalf`; `xp` retains the existing total. Stat/perk points are not stored as replayable rewards.

Version 0.10.0 adds optional `statPointVersion=2` and `statPointLegacyCredit`. On the first load of an old save, the fixed credit is `2 × (loaded level − 1)`, captured before validating allocated stats. Available points become `3 × (level − 1) + credit − allocated`. This preserves every previously allocated and unspent five-point entitlement, including fully allocated level-100 characters, while future levels grant only three. Fresh characters start with zero credit. Snapshots/save/reload retain the same credit, so migration cannot repeat or grant bonus points. Learned advanced-only sets reload directly without requiring parents. All 85 nodes now need only one available perk point; class selection and ability-use rules are unchanged.

Legacy characters below level 100 retain exactly their level and XP progress. A legacy level-100 character could have 200,000 XP although level 100 starts at 103,950; the previous mod still displayed level 100 and zero progress. Migration retains **level 100, all 200,000 XP, allocated attributes and point budgets**. Historical surplus becomes a fixed offset rather than retroactive levels/points. New rewards continue the curve from level 100.

Example: legacy level 100 / 200,000 XP plus an ordinary reward of 1,400 yields 2,100 actual XP: level 101, 202,100 total XP and 50/2,070 XP toward the next level. Only 3 new stat points and 1 new perk point are granted. Reloading does not repeat migration.

Mana, stamina, health, damage, class/rank/style bonuses and allocated stats remain intact. New levels use the existing resource formulas. Bonus arithmetic uses `long`; purchasing a stat cannot overflow its allocated integer or spend a point without increasing it. Very large resource numbers use compact notation within the same HUD fields. H exposes full XP/level/point values through existing overflow tooltips.

Use the same version on client and server and back up the world. Older mod versions do not understand the new large XP values and must not write upgraded data back.

## Historical 0.3.7 HUD

The following describes the historical 0.3.7 geometry, retained here to explain the transition. [HUD_LAYOUT.md](HUD_LAYOUT.md) describes the current bar below stamina; the XP rules and migration above remain current.

The old skill bar was **182 × 22 GUI units**, with the same five slots and Z/X/C/V/B controls. It and the hotbar were centered on `GUI width / 2`. Vanilla's background started at `h−22`, but its selected-slot/offhand outer frame started at `h−23`; the old skill bar ended at that actual outer edge with zero gap, three units lower than 0.3.6. The resource portrait used `h−66` in side layout and `h−92` in stacked layout. Native XP/food/armor/air/locator shifts were then 44/76 units. Later HUD releases superseded these positions without changing leveling.

See [VALIDATION.md](VALIDATION.md) for executed build and gameplay checks.

## Server configuration

Optional `config/starswordmaster-progression.json` is read on startup. Missing files and invalid fields use defaults; no new dependency is required.

```json
{
  "killXpEnabled": true,
  "healthMultiplier": 3.0,
  "includePassiveMobs": false,
  "practiceCharacterXpMultiplier": 0.0,
  "showKillXp": true,
  "excludedEntityIds": []
}
```

Finite positive health and multiplier are multiplied using decimal arithmetic and rounded upward into BigInteger; there is no int/long truncation or per-kill cap. Optional passive inclusion still excludes players, owned or allied creatures. Exact awarded XP is coalesced in the actionbar once per second; a level-up message takes priority. Kill awards use the existing save lifecycle.

Resetting the perk tree on K clears learned nodes and assignments, reduces derived rank/bonuses and clamps resources without filling them. The existing derived perk balance refunds exactly all purchases; repeated resets and reloads cannot grant extra points. Class, sword style, character XP, practice and manually allocated stats are preserved. The server validates and immediately saves/synchronizes the requesting player only.

Active cooldown debt is retained through free reset, relearning and save/reload. Removed abilities cannot be cast; relearning does not bypass their remaining cooldown. Repeating a reset still grants no points.

# 0.7.0 — Radial skill tree and kill XP

- Eight deterministic constellation arms, true prerequisite routes, fit/focus navigation and separate narrow-screen details.
- Free server-authorized perk-tree reset; class, practice, XP and allocated stats remain. Cooldown debt survives reset/relearning/reload to prevent bypasses.
- Hostile kills grant ceil(maximum health × 3) character XP by default. Player melee, projectiles and existing magic credit the actual killer; duplicate deaths and protected creatures grant none. Enemy Evoker-owned Vex remain eligible.
- Practice proficiency remains; character XP from practice defaults to zero and is configurable. Existing unlimited levels, level-8 ×1.5 reward rule, five stat points and one perk point per level are unchanged.
- Optional server configuration and coalesced XP feedback. Existing saves, ability IDs and combat presentation remain compatible.

# Changelog

## 0.6.0 — 17 October 2026

- Add rotating, layered magic circles attached to the actual native/PAL arm pose, including mirrored and first-person hands and multiplayer observers.
- Add temporary, opaque, lit 3D Stone Spike, Earthen Wall, Quake Blade and Worldbreaker formations with loaded-terrain collision sampling, safe footprints and finite collapse.
- Add cached cinematic mesh silhouettes across all 54 active abilities, with staged reveal/dissolve, distance/frustum/quality LOD and explicit frame budgets. Retain sparse particles and existing animations.
- Apply a charcoal, iron, bronze and antique-gold interface to H, K and HUD frames. Make all mod text English under both English and Danish Minecraft settings.
- Preserve individual UUID aura hues; reduce first-person aura opacity for readability.
- Stop redundant save/snapshot prerequisite passes after no new skill can be restored.
- Preserve 85 tree nodes, 54 active abilities, gameplay values, saves, XP/point/rank rules, key bindings and HUD layout.

See [validation](VALIDATION.md) for actual tests, measured snapshot restore timings and limitations. Historical release directories are retained.

## Earlier releases

See archived documentation under [releases](releases/) for 0.5.0 and earlier versions.

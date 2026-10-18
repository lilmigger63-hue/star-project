# Imported asset credits — 0.10.0

All sources below were inspected and downloaded on 2026-10-18. Their relevant source pages explicitly identify CC0; the two Quaternius free Standard archives, Kenney and SpritePilot also contain CC0 notices, preserved under `assets/starswordmaster/licenses/`. No proprietary BDO/anime files, commercial packs, player models, source rigs or unrelated textures ship in the mod.

License for all imported inputs: **CC0 1.0 Universal**, https://creativecommons.org/publicdomain/zero/1.0/ . Original Star Swordmaster code, poses and artwork retain their existing repository license. “BDO-inspired” describes action style; this project has no affiliation with its publisher.

## Animation inputs actually converted

The Blender extractor `tools/retarget_external_motion.py` reads evaluated source arm/torso orientations, maps the Z-up basis, samples 33 normalized frames and exports bounded curves. `tools/blend_combat_motion.py` blends selected curves into all 54 existing skill performances with neutral endpoints and unchanged duration. Head, legs, root motion, mesh, texture, damage timing and player skin are retained by the native game. This is a partial upper-body retarget, not a claim of an exact full-body BDO conversion.

| Source | Author | Imported motion input |
| --- | --- | --- |
| [Humanoid Soulslike Animations](https://opengameart.org/content/humanoid-soulslike-animations) | Gres | straighswordanimationpack.zip → SS_Main.fbx, SS_TH.fbx; evaluated upper-arm/spine rotation curves |
| [Universal Animation Library](https://quaternius.com/packs/universalanimationlibrary.html) | Quaternius | free Standard archive → UAL1_Standard.glb (Sword Attack, Spell Simple, Roll, Push; rotations only) |
| [Universal Animation Library 2](https://quaternius.com/packs/universalanimationlibrary2.html) | Quaternius | free Standard archive → UAL2_Standard.glb (sword combos, dash/block, shield dash and overhand throw) |
| [Lowpoly Animated Sword](https://opengameart.org/content/lowpoly-animated-sword-w12-animations-blend-rigged) | zisongbr | Arms.blend → Attack_01/02/03/04, Block_Idle and Skill_Heal evaluated rotations |
| [Low Poly Hero with Sword and Shield](https://opengameart.org/content/animated-low-poly-hero-with-sword-and-shield) | Robin Lamb | hero_1.zip → character.blend → defend pose rotations |

The Soulslike compressed Blender file uses a newer format than installed Blender 4.3.2. The same author's supplied FBX archive was used successfully instead. Quaternius Standard files were obtained through the public free-download flow; paid Pro/Source archives were not downloaded. Exact extracted action/bone names, source-frame ranges and selected-file SHA-256 values are in `tools/assets/retargeted_motion.json`. Raw downloads remain in the local asset cache, outside the checkout.

## VFX inputs actually compiled

Only selected alpha images/frames are compiled; source archive names, source hashes, input filenames and runtime identifiers are recorded in `tools/assets/combat_sprite_manifest.json`. Each output is a 64×64 particle-atlas frame with a transparent gutter, neutral runtime tint and nearest-neighbor scaling. Original transparency and ordered frames are retained. Existing 3D meshes/circles remain; these sprites replace selected existing accent particles without increasing their count.

| Source | Author | Imported files | Runtime resource |
| --- | --- | --- | --- |
| https://opengameart.org/content/weapon-slash-effect | Cethiel | `Classic/1/Classic_01.png`, `Classic/1/Classic_02.png`, `Classic/1/Classic_03.png`, `Classic/1/Classic_04.png`, `Classic/1/Classic_05.png`, `Classic/1/Classic_06.png` | `combat/slash_light_*.png` (6 frames) |
| https://opengameart.org/content/slash-effect | MetaShinryu | `slash.png` | `combat/slash_curve_*.png` (1 frames) |
| https://opengameart.org/content/slash-effect-collection | MetaShinryu | `Slash Effect Collection/Big/Circular.png` | `combat/slash_finish_*.png` (1 frames) |
| https://opengameart.org/content/high-resolution-slash-effects | MetaShinryu | `High res slashes/Leap/Leap big.png` | `combat/slash_heavy_*.png` (1 frames) |
| https://opengameart.org/content/pixel-art-sword-slash-effect | tbbk | `pixel_slash.png` | `combat/slash_pixel_*.png` (9 frames) |
| https://spritepilot.com/free-game-assets/combat | SpritePilot | `spritepilot-combat-pack/32px/parry-flash-32px_strip7.png` | `combat/impact_spark_*.png` (7 frames) |
| https://kenney.nl/assets/particle-pack | Kenney | `PNG (Transparent)/flare_01.png` | `combat/magic_flare_*.png` (1 frames) |

Reproduction: download the exact publicly licensed selected archives, run the Blender extractor with `--disable-autoexec`, then `python tools/generate_ability_animations.py` and `python tools/import_combat_sprites.py ASSET_CACHE`. Raw external scripts/macros are never enabled. No network/download step is required for normal Gradle builds or tests.

## References not imported

- https://www.gameassetdeals.com/asset/230268/slash-sprite-sheet-mega-pack — commercial reference; no license purchased and no content imported.
- https://booth.pm/ja/items/384853 — optional reference; redistribution permission for exact files was not established, so no content imported.

All five requested animation sources and seven requested free VFX sources contribute actual converted inputs. Native playback/rendering checks and their limits are recorded separately in `VALIDATION.md`; successful extraction alone does not establish in-game playback.

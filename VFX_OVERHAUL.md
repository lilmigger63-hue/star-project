# Volumetric combat presentation — 0.8.0

All 54 active abilities retain their IDs, costs, damage, ranges, authoritative activation and original clip durations. The 31 passive nodes have no cast animations. This release builds on the existing renderer and native PAL/Better Combat hooks; it does not add a competing gameplay or animation system.

## Original shapes and choreography

Fire uses capped turbulent flame masses and condensed spears. Frost uses solid faceted crystals and interlocking shields. Water has hooked, sealed rolling crests and spiral cages. Lightning uses branching, capped prisms and a storm crown. Dark magic has curled tendrils and a hollow eclipse with spectral blades. Healing uses gold/emerald crystal petals and ceremonial loops. Sword and astral effects include thick crescents, recognizable blades, parry forms, descending judgment and a five-blade celestial formation. Earth retains its existing terrain-sampled opaque formations and gains coordinated chip/ripple meshes. Concept images were viewed as direction only; no image pixels or proprietary assets ship.

Every ability selects a distinct immutable mesh assembled from reusable closed solids. Parts carry whole-part Low/Normal/Ultra thresholds, motion and delays. All 54 original PAL clips were refined with a short load, decisive release near 22% of their existing duration, weighted follow-through and neutral recovery. Rotations remain upper-body only, aim and locomotion remain native, and Better Combat retains priority 2000 over cast layer 1999.

Five cosmetic phases are anticipation (0–16%), activation (16–30%), primary (30–64%), impact (64–80%) and dissipation (80% through the existing 8-tick mesh tail). Existing immediate hit accents remain on server confirmation: these phases never postpone damage or healing. Whole-part transforms animate the cached meshes. First-person caster geometry has reduced opacity; Eclipse Nova is hollow and Frost Nova leaves a small central core to preserve the view.

## Attachment, materials and limits

Native hand circles still attach to actual PAL arm matrices in first/third person and either hand. Existing three pixel-art layers gain element-specific counter-rotation and restrained rune pulses. Sword auras retain their native baked-item attachment, UUID-derived base hue and rank scaling. Sword constructs derive base, bright and dark facets from that same hue; magical elements retain their own palettes.

The original native unlit shader approach is reused for a depth-writing construct material so later clouds cannot paint across foreground solids. The native emissive fallback remains safe. Mesh normals and four-tone facet palettes are cached; no mesh is regenerated per frame. Native nearest filtering is retained, with no shader framework or dependency added.

Limits remain 96 faces per complete ability mesh, 2,048 cinematic faces/frame, 2,048 terrain faces/frame, and 4,096 shared world-effect faces/frame. Each renderer retains its own quota. Low removes whole ornament groups while preserving closed primary forms; distance/frustum culling and the 64-caster / 64-block limits remain. Particle caps remain 256 new/tick with existing pattern/impact caps. Circles retain 384 quads and auras retain 1,024 quads/frame. Finite lifetimes, cancellation, world/entity identity checks and buffer disposal prevent persistent effects.

The presentation-only channel is now `ability_presentation_v3`: one optional server-selected target entity ID allows Curse Mark, cages and target-anchored constructs to follow the actual victim. Captured entity identity prevents following a reused numeric ID; missing/dead targets fall back to the original confirmed hit. Existing versioned appearance and snapshot payloads remain unchanged. All clients/server must use the same release. No target selection, damage or save schema changed.

## Registered geometry audit

Each row also uses its dedicated `skill_<id>.json` PAL clip and existing distinct particle choreography. Face counts are complete Low / Normal / Ultra meshes, before frame quotas and culling. Form labels are renderer routing categories; the actual solids are individually assembled per ID, not identical recolors.

| Ability | Element | Routing / anchor | Faces Low / Normal / Ultra | Part motion |
| --- | --- | --- | --- | --- |
| `spark` | FIRE | LANCE / RAY | 14 / 28 / 36 | travel, pulse, fall |
| `frost_touch` | FROST | LANCE / RAY | 8 / 16 / 24 | travel, stagger, fall |
| `light` | STAR | HALO / CASTER | 16 / 40 / 56 | pulse, orbit |
| `mend` | HEALING | HALO / CASTER | 24 / 40 / 48 | rise, open |
| `renew` | HEALING | HALO / CASTER | 52 / 68 / 76 | rise, orbit, pulse |
| `quick_cut` | STEEL | CRESCENT / CASTER | 26 / 44 / 50 | travel, stagger |
| `rapid_slash` | STEEL | CRESCENT / CASTER | 26 / 52 / 70 | travel, stagger |
| `quick_dash` | STEEL | DASH / ORIGIN | 18 / 30 / 56 | travel, stagger |
| `heavy_cut` | STEEL | CRESCENT / CASTER | 44 / 52 / 60 | travel, fall |
| `heavy_sweep` | STEEL | CRESCENT / CASTER | 26 / 52 / 68 | travel, stagger, fall |
| `crushing_edge` | STEEL | CRESCENT / CASTER | 52 / 60 / 68 | travel, pulse, fall |
| `guard` | STEEL | SHIELD / CASTER | 18 / 24 / 32 | still, pulse |
| `mirror_guard` | STAR | SHIELD / CASTER | 6 / 18 / 42 | open, orbit |
| `guardian_stance` | STAR | SHIELD / CASTER | 42 / 66 / 74 | rise, orbit, pulse |
| `riposte` | STEEL | LANCE / CASTER | 18 / 44 / 52 | travel, open |
| `piercing_thrust` | STEEL | LANCE / CASTER | 18 / 30 / 38 | travel |
| `aura_wave` | STAR | LANCE / RAY | 44 / 70 / 78 | travel, fall |
| `blade_focus` | STAR | HALO / CASTER | 36 / 60 / 68 | orbit, pulse |
| `fire_burst` | FIRE | BLOOM / IMPACT | 56 / 80 / 94 | rise, open, fall |
| `ice_lance` | FROST | LANCE / RAY | 8 / 16 / 32 | travel, still, fall |
| `sanctuary` | HEALING | HALO / CASTER | 40 / 56 / 80 | open, rise, orbit |
| `restoration` | HEALING | HALO / CASTER | 40 / 56 / 96 | rise, open, orbit |
| `frost_nova` | FROST | RING / CASTER | 40 / 56 / 72 | rise, pulse |
| `flame_blade` | FIRE | CRESCENT / CASTER | 26 / 40 / 54 | travel, rise, fall |
| `frost_blade` | FROST | CRESCENT / CASTER | 42 / 58 / 66 | travel, stagger |
| `flame_lance` | FIRE | LANCE / RAY | 14 / 36 / 50 | travel, orbit |
| `frost_ward` | FROST | SHIELD / CASTER | 18 / 30 / 38 | rise, pulse |
| `arcane_step` | STAR | DASH / ORIGIN | 36 / 60 / 76 | travel, open, fall |
| `glacier_sweep` | FROST | RING / CASTER | 24 / 40 / 56 | stagger |
| `seismic_cut` | STEEL | RING / CASTER | 42 / 58 / 66 | travel, stagger |
| `ember_ring` | FIRE | RING / CASTER | 52 / 80 / 94 | open, rise |
| `ice_prison` | FROST | CAGE / IMPACT | 32 / 48 / 88 | rise, pulse |
| `static_spark` | LIGHTNING | BOLT / RAY | 18 / 24 / 30 | travel, pulse |
| `chain_lightning` | LIGHTNING | BOLT / RAY | 36 / 54 / 60 | pulse |
| `thunder_step` | LIGHTNING | DASH / ORIGIN | 36 / 48 / 56 | travel, pulse |
| `heavens_wrath` | LIGHTNING | BOLT / IMPACT | 74 / 86 / 94 | orbit, fall, pulse |
| `water_bolt` | WATER | HELIX / RAY | 30 / 38 / 46 | travel, fall |
| `tidal_wave` | WATER | WAVE / CASTER | 38 / 64 / 80 | travel, fall |
| `aqua_blade` | WATER | WAVE / CASTER | 32 / 46 / 62 | travel, orbit, fall |
| `ocean_prison` | WATER | CAGE / IMPACT | 72 / 96 / 96 | rise, orbit |
| `stone_spike` | EARTH | RING / IMPACT | 24 / 40 / 48 | open, fall |
| `earthen_wall` | EARTH | SHIELD / CASTER | 6 / 22 / 28 | rise, fall, pulse |
| `quake_blade` | EARTH | RING / CASTER | 24 / 38 / 46 | stagger, travel |
| `worldbreaker` | EARTH | RING / CASTER | 48 / 64 / 80 | open, fall |
| `shadow_bolt` | DARK | HELIX / RAY | 34 / 52 / 60 | travel, orbit, fall |
| `curse_mark` | DARK | SHIELD / IMPACT | 42 / 60 / 68 | orbit, rise, pulse |
| `shadow_edge` | DARK | CRESCENT / CASTER | 26 / 48 / 66 | travel, orbit, stagger |
| `eclipse_nova` | DARK | RING / CASTER | 58 / 76 / 94 | orbit, open |
| `astral_crescent` | STAR | CRESCENT / CASTER | 26 / 52 / 68 | travel, fall |
| `falling_star` | STAR | RING / CASTER | 16 / 40 / 56 | fall, open |
| `phantom_blades` | STAR | CRESCENT / IMPACT | 36 / 54 / 72 | stagger |
| `celestial_rush` | STAR | DASH / ORIGIN | 36 / 42 / 48 | travel |
| `sovereign_judgment` | STAR | CRESCENT / IMPACT | 34 / 58 / 74 | fall, open, rise |
| `heavenly_sword_domain` | STAR | RING / CASTER | 54 / 72 / 90 | rise |

## CPU stress measurement

`tools/VfxChoreographyBenchmark.java` exercises cached profile selection, quality groups, finite timelines and shared quota requests for 64 candidate casters over 10,000 simulated frames. After 5,000 warm-up frames, this Java 21 cloud run measured **42.291 / 35.296 / 35.850 ms** for three complete runs, checksums identical (14982248.085): **4.229 / 3.530 / 3.585 microseconds per simulated frame**. This excludes native drawing, GPU work, packets and particles. It is neither an FPS measurement nor a before/after performance claim. Real multiplayer is verified separately with two clients; no claim of 64 real network clients is made.

See VALIDATION.md for executed build/native checks and limitations. Smoke sources and benchmark helpers are excluded from the production JAR.

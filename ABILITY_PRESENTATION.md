# Ability animations and visual effects

All **54 active abilities** have an original individual PAL clip, `skill_<ability-id>.json`, and spatial effect choreography. The **31 passive nodes** do not cast animations. The 11 original clips, 54 ability clips and eight reusable style clips give **73 PAL resources**. Better Combat retains ordinary attack priority, and only server-confirmed casts create presentations.

The original 32 clips last 10–20 ticks; the 22 expansion clips last 12–32. All use PAL format 3, are nonlooping and explicitly return to neutral. Arms and limited torso rotation animate while legs, position and aim remain unchanged. Arm rotation contribution scales cosmetically from 75 to 100% with character level, preserving duration. Artwork and animation are original.

The finishing pass layers bounded reusable spell geometry over the existing choreography and uses actual arm rendering transforms for hand seals. Earth formations are temporary cosmetic stone geometry anchored to sampled loaded terrain, without block changes or physical wall collisions. Their observed behavior and remaining visual checks are recorded in [VALIDATION.md](VALIDATION.md).

## Original active abilities

Each row uses `skill_<ID>.json`. Blade afterimages are brief translucent trails rather than gameplay entities. The radius below describes the original unscaled particle pattern; it does not change damage reach.

| Ability / ID | Animation | Original particle choreography | Base pattern radius |
| --- | --- | --- | --- |
| Spark / `spark` | Shoulder gather, finger cast | Small palm seal, coiling ember trail, warm impact flash | 1 |
| Frost Touch / `frost_touch` | Inward cold gather, palm push | Angular frost thread and icy petals around target | 1 |
| Starlight / `light` | Open chest, lift a star-bearing hand | Five-petal star orbit with floating seals | 0.8 |
| Mending Light / `mend` | Gather palms, gently release | Rising healing spiral of seals and small light petals | 0.65 |
| Quick Cut / `quick_cut` | Tight diagonal cut, recoil | Narrow light arc, short blade afterimages and speed streaks | 1.6 |
| Swordstep / `quick_dash` | Tuck arms, drive shoulder forward | Two low speed trails at the confirmed origin | 0.7 |
| Heavy Cut / `heavy_cut` | High guard, heavy vertical cut | Large vertical crescent, falling streaks, strong impact flash | 2.2 |
| Storm Sweep / `heavy_sweep` | Wide side charge, horizontal sweep | Wide low crescent with staggered arcs and streaks | 3 |
| Star Guard / `guard` | Diagonal chest guard, brief parry | Compact diamond guard, palm seal and steel streaks | 0.9 |
| Riposte / `riposte` | Parry, withdraw, counterthrust | Narrow paired thrust trails and concentrated sparks | 1.6 |
| Aura Wave / `aura_wave` | Luminous rising slash | Traveling curved energy front along the confirmed hit path | 1 |
| Blade Focus / `blade_focus` | Center blade, control edge, draw toward chest | Rotating blade streaks and shoulder star seals | 1 |
| Fire Burst / `fire_burst` | Compress, explosively spread hands | Glowing flame bloom with rising petals at target | 2 |
| Ice Lance / `ice_lance` | Shape with support palm, shoulder thrust | Narrow shard lance with a straight core and ice flash | 1 |
| Sanctuary / `sanctuary` | Prayer, protective arm arch | Two low light orbits, larger seals and healing stars | 1.2 |
| Frost Nova / `frost_nova` | Gather inward, release radially | Two staggered outward rings of low frost shards | 6 |
| Flame Blade / `flame_blade` | Low windup, rising whip cut | Waving fire arc with curled embers and a warm edge | 1.9 |
| Frost Blade / `frost_blade` | High stance, braking diagonal cut | Toothed ice crescent alternating shards and trailing arcs | 1.9 |
| Rapid Slash / `rapid_slash` | Crossed cuts, change direction | Three brief alternating diagonal trail phases | 1.8 |
| Crushing Edge / `crushing_edge` | Gather high grip, crushing finish | Heavy crossed arcs and concentrated impact light | 2.8 |
| Mirror Guard / `mirror_guard` | Cross hands, mirror tilt | Rotating mirror shards with paired blade reflections | 1 |
| Flame Lance / `flame_lance` | Draw shoulder, thrust, rolling release | Coiling ember helix around a narrow lance core | 1 |
| Frost Ward / `frost_ward` | Trace a diamond, lock blade | Curved crown of ice petals and compact frontal seal | 1 |
| Arcane Step / `arcane_step` | Cross chest, magical sidestep | Brief origin seal and spiraling star-speed streaks | 0.8 |
| Glacier Sweep / `glacier_sweep` | High side guard, broad sweep | Outward fan of low ice shards and frost arcs | 5 |
| Renewal / `renew` | Heart spiral, alternating hand lift | Counterrotating healing spirals and green arcs | 0.8 |
| Piercing Thrust / `piercing_thrust` | Hip draw, long directed thrust | Converging lights and forward thrust streaks | 2 |
| Seismic Cut / `seismic_cut` | High lift, low slam, shock recoil | Three low outward fracture lines with angular sparks | 5 |
| Guardian Stance / `guardian_stance` | Wide protective brace | Three protective seal/pillar sectors around the body | 1.2 |
| Ember Ring / `ember_ring` | Cross gather, circular spread, lift | Rotating flame petals and seals along an expanding ring | 5 |
| Ice Shackles / `ice_prison` | Offset pillar lift, crossed closure | Four rising icy corners and sealing marks | 1.2 |
| Restoration / `restoration` | Low gather, heart cross, release | Rising healing bloom with reversed rotation and seals | 0.95 |

## Expansion abilities

Each row uses its own `skill_<ID>.json`, with a distinct rhythm and hand pose built on reusable style resources. Earth rows include the finishing pass's solid formations; other rows describe the established choreography beneath the new layered geometry.

| ID | Animation | Visual effect | Clip ticks |
| --- | --- | --- | --- |
| `static_spark` | Pinch a charge beside the shoulder, snap the wrist, then discharge a second tiny recoil. | Zigzag lightning with a palm charge and short electric impact | 12 |
| `chain_lightning` | Lift the conductor hand and relay three alternating arcs between the open palms. | Branching zigzags through real server-confirmed hit positions | 20 |
| `thunder_step` | Cross a charged guard, tuck both elbows through the step and flare an electric exit. | Low paired lightning trails from the origin | 12 |
| `heavens_wrath` | Stack stormclouds overhead, split their crown and conduct descending thunder with both hands. | Descending bolts, storm runes and rotating storm crown | 30 |
| `water_bolt` | Cup a droplet above the support palm, corkscrew it forward and collect the spent stream. | Coiling water-droplet helix along the aiming line | 14 |
| `tidal_wave` | Scoop a low tide, roll the crest between the hands and push the whole wave outward. | Forward water fan with a high wave crest | 22 |
| `aqua_blade` | Weave water beneath the hilt, carve a flowing rising edge and roll its crescent away. | Flowing blade crescent and droplets | 16 |
| `ocean_prison` | Raise offset spirals into a high water cage, then close its gate with crossed palms. | Rising water spirals and runes around the target | 26 |
| `stone_spike` | Find the earth with a low palm and lever a narrow stone point up from beneath the target. | Terrain-sampled solid spike rises beneath the confirmed target; dust and chips | 14 |
| `earthen_wall` | Set two foundations, lift a heavy slab and brace the barrier with square open palms. | Interlocking solid stone wall segments rise from sampled terrain; ward runes | 20 |
| `quake_blade` | Take a weighted crown guard, drive the sword into the low line and absorb two ground pulses. | Directional wave of solid jagged stones follows the confirmed facing | 22 |
| `worldbreaker` | Draw the mountain overhead, suspend its weight, then shatter the seal with a monumental double slam. | Bounded arrangement of massive solid rising pillars and ground shockwaves | 30 |
| `shadow_bolt` | Hook a shadow from the opposite palm, twist it into a dart and cast it from a half-open guard. | Coiling shadow trail with dark petals | 14 |
| `curse_mark` | Trace a hooked sigil, seal its center with two fingers and pull the curse thread taut. | Counterrotating curse rune at the target | 18 |
| `shadow_edge` | Draw shadow along the blade, cut a low hooked arc and fold the trailing darkness inward. | Dark blade afterimages along a diagonal slash | 16 |
| `eclipse_nova` | Cup a black sun, eclipse it behind crossed arms, then tear the horizon into an open nova. | Six-point eclipse front with runes and shadow shards | 30 |
| `astral_crescent` | Draw a low celestial edge, sweep a clean rising moon and stop with the point held high. | Wide rising astral crescent with blade afterimages | 16 |
| `falling_star` | Set the star above the crown, draw it downward through the blade and catch its falling weight. | Five-point star front at a heavy slam | 22 |
| `phantom_blades` | Mark three floating sword lanes with alternating cuts and gather their phantom edges into guard. | Four alternating phantom blade phases; no extra damage | 20 |
| `celestial_rush` | Tuck a star-point guard, drive the shoulders through a straight rush and flare the trailing blade. | Paired speed trails, blade shards and star flashes | 14 |
| `sovereign_judgment` | Raise the sovereign blade like a scepter, hold its verdict aloft and cast a single commanding stroke. | Large descending blade and rune trail | 28 |
| `heavenly_sword_domain` | Draw an oath across the heart, crown the domain overhead and unfold a cathedral of celestial swords. | Rotating blade crown with alternating rune pillars | 32 |

Reusable styles: `style_dual_cast`, `style_ground_slam`, `style_overhead_cast`, `style_sword_release`, `style_lightning_channel`, `style_water_weave`, `style_earth_raise`, `style_shadow_summon`.

## Pixel art, geometry and limits

Twelve original sparse particle silhouettes remain: **streak, arc, sigil, shard, flash, ember, bolt, droplet, rock, shadow, rune and blade**. Elements use distinctive shapes, paths, phases and timing, alongside fire red/ember, frost cyan, lightning electric violet/blue, water turquoise, earth ochre, dark violet/crimson, astral moonlight/gold and healing gold/emerald accents. Sword techniques retain each player's personal UUID-derived aura color, with analogous shades and Rank 4 highlights. Rank never replaces the base hue.

Sprites retain sharp pixel edges and native nearest-neighbor filtering. Particle points use native atlas quads and create no secondary particles, collision, terrain edits or gameplay entities. Ordinary trails last 3–8 ticks; the particle class enforces at most 12 ticks and 3 blocks of width after scaling. Larger attacks gain broader silhouettes than basic casts without fullscreen overlays or screen shake. Reusable meshes and layered rune textures supply additional shape under separate hard geometry budgets, quality and distance limits.

Particle safeguards remain **at most 12 pattern points per update**, normally every other tick; **up to eight immediate impact points**; **256 new particles/tick**; **64 active casters**; **64 blocks of draw distance**; and **12 blocks of scaled local visual radius** (base patterns stay within 6.1). The budget cannot become negative. Minecraft ALL/DECREASED/MINIMAL and the mod's Low/Normal/Ultra settings reduce detail within these limits.

## Activation, combat and networking

The server sends one compact presentation event after a valid cast and resource spending. UUID/entity ID, sequence, world time, ability ID, origin and impact remain authoritative. Versioned v2 payloads include compact server-computed rank/power/color and at most eight actual hit positions; event size is below 384 bytes and appearance updates below 40 bytes. Clients generate cosmetic geometry locally. There are no particle streams, new gameplay projectiles or C2S animation permissions. Duplicate, expired and unknown events are rejected. A late valid event plays only its remaining original lifetime.

**Damage and healing keep their original timing.** Immediate impact flashes use existing server hit points. Visual anticipation, trails and recovery do not delay gameplay. Range, target selection, damage, costs, cooldowns, stats, XP and saves remain unchanged.

The PAL layer remains immediately below Better Combat's attack layer (1999/2000). Ordinary combos retain priority. Casts mirror for the selected hand; native first-/third-person arm hooks attach rune circles to rendered hands, while projectile/world paths retain the confirmed aim. The renderer does not write to aim, movement or Better Combat state.

Attack, damage, death, weapon switch, a new cast, entity/world replacement and disconnect clear active clips. Previously emitted particles expire quickly. Missing PAL clips retain the remaining cosmetic fallback; missing/unloaded sprites fall back to existing native elemental particles. Additional geometry follows the same presentation lifetime authority.

## Installation and validation

Install the same current version on server and clients. Runtime requirements remain Minecraft 1.21.10, Java 21, Fabric API, Better Combat 3.0.0+1.21.10, PAL 1.1.3 and Cloth Config. No new dependencies are introduced by the finishing pass.

Original asset generators live in `tools/`; historical generator locale metadata may predate English-only presentation, so preserve reviewed `en_us.json` and its identical English `da_dk.json` when regenerating art. Smoke sources in `src/smoke` are excluded from the mod JAR. See [EXPANSION.md](EXPANSION.md) for aura/scaling formulas and [VALIDATION.md](VALIDATION.md) for the build, singleplayer and multiplayer checks actually executed.

## Finishing-pass mesh layers (0.6.0)

Every row above additionally selects one cached mesh profile: curved crescents, crossed lances, jagged branching bolts, helices, waves, cages, radial rings, shields, blooms, halos or dash ribbons. Each ability retains its distinct original animation and particle choreography. Meshes are created once at resource initialization, not rebuilt per frame. The mesh frame cap is2,048 quads, with 64 casters and 64 blocks of draw distance, frustum culling and quality/distance LOD. Chain Lightning bounds include all confirmed targets; Curse Mark's seal attaches to its confirmed target rather than the caster.

Magic circles are original transparent pixel-art sigils extracted from actual native/PAL arm poses. Their palm plane mirrors with arm choice, rotates in two/three layers and dissolves with the existing animation. Native-frame deduplication caps them at 64 casters,128 hands and384 quads. The eight element palettes are distinct. First-person sword aura opacity is reduced to 65% of its third-person opacity to preserve sight; its size, RGB identity, rank progression and actual weapon attachment remain unchanged.

Earth uses separate opaque, lit, low-poly models: Stone Spike's clustered tip, Earthen Wall's interlocking segments, Quake Blade's staggered directional wave and Worldbreaker's pillar array. Five loaded collision samples per footprint determine safe ground/ceiling fitting; liquid, steep or unloaded footprints are rejected. The wall ends at 100 ticks; other formations collapse within 34–56 ticks. Limits are 16 formations,9 rocks each and2,048 quads/frame. Small dust/chip sprites remain secondary to actual stone geometry. No blocks, entities or collision are created in gameplay.

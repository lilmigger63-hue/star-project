# Star Swordmaster

A Fabric mod for **Minecraft 1.21.10 / Java 21** with permanent classes, sword mastery, magic and a branching skill tree. The current tree contains **85 nodes: 54 active abilities and 31 passive nodes**. All player-facing mod text is English, including when Minecraft uses Danish; vanilla and other mods keep their selected language.

The finishing pass uses charcoal, slate, aged iron, bronze and antique gold across the skill tree, character status and HUD. Existing original skill icons and the constellation artwork are retained beneath subdued panels. Learned, available, class-locked and unavailable nodes have distinct frames and small state marks; selected nodes have an outer gold frame. See [VALIDATION.md](VALIDATION.md) for the checks actually completed for the current build.

## Downloads and installation

Download **0.6.0**: [mod JAR](https://raw.githubusercontent.com/lilmigger63-hue/star-project/main/releases/0.6.0/star-swordmaster-0.6.0.jar) · [SHA-256](releases/0.6.0/SHA256SUMS.txt) · [validation](VALIDATION.md) · [changelog](CHANGELOG.md). Use 0.6.0 on the server and all clients. Historical release directories remain unchanged.

1. Create a Minecraft **1.21.10** installation with **Fabric Loader 0.19.5** and **Java 21**.
2. Install [Fabric API 0.138.4+1.21.10](https://modrinth.com/mod/fabric-api), [Better Combat 3.0.0+1.21.10](https://modrinth.com/mod/better-combat), [Cloth Config 20.0.149](https://modrinth.com/mod/cloth-config) and [Player Animation Library 1.1.3](https://modrinth.com/mod/player-animation-library). The PAL file is named `PlayerAnimationLibFabric-1.1.3+mc.1.21.9.jar`; its publisher also lists Minecraft 1.21.10 compatibility.
3. Put the Star Swordmaster release JAR in `mods/`. Keep only one version; remove the old Star Swordmaster JAR when upgrading.
4. In multiplayer, install the same mod version and required dependencies on the server and every client. Presentation payloads are versioned.

Back up the whole world before updating. A separate profile is useful for first-time testing. Compatibility with unrelated modpacks requires separate testing.

## Playing

| Control | Action |
| --- | --- |
| **K** | Skill tree: select a node, inspect requirements, learn with a perk point, then assign an active ability to slot 1–5 |
| **H** | Character status: level/XP, class/rank/style, effective attributes, points, resources and learned skills |
| **Z / X / C / V / B** | Activate skill slots 1–5 |
| Drag / scroll in K | Pan / zoom the tree |
| Escape / Done | Close a screen |

All mod keys can be rebound in Minecraft's Controls settings. Hover nodes for complete descriptions, prerequisites, class/level requirements and effective resource costs/cooldowns. Selected combat styles appear in brackets. The H screen's **Low / Normal / Ultra** VFX selector is local to the client.

Start as **Uninitiated**. Hit existing hostile mobs with a sword to practice Sword; swinging at air grants no practice. Skills improve through use, and skill milestones grant character XP. Every character level after level 1 grants **5 stat points** for attributes in H and **1 perk point** for nodes in K. These are separate budgets and are separate from vanilla XP. Viewing either screen spends nothing. The server validates every purchase and updates the display through snapshots.

At **character level 10**, choose **Swordsman**, **Mage** or **Spellblade** in K. The confirmation warns that the choice is **permanent in that world**. All classes retain access to basic magic and basic sword techniques. Adept, Master and Rank 4 nodes extend the selected class; [RANKS.md](RANKS.md) lists requirements and bonuses.

Sword techniques require a sword in the main hand. Aim at a hostile target for targeted attacks. Magic spends mana; sword techniques also spend stamina. Rejected casts spend no resources. Mana and stamina regenerate automatically, more slowly in combat; combat lasts eight seconds after damage. Better Combat continues to control ordinary attacks.

Choose Quick, Heavy or Defensive style at the bottom of K. They improve Quick Cut, Heavy Cut and Star Guard respectively; passive style training extends bonuses to sword abilities. Changing style affects future activations. An existing cooldown retains its original duration and display.

## Abilities and attributes

Basic magic includes Spark, Frost Touch, Starlight, Mending Light and Renewal. Sword basics include Quick Cut, Swordstep, Heavy Cut, Storm Sweep, Star Guard and Riposte. Advanced branches add fire/frost, healing, enchanted swords, auras and astral techniques. Lightning, Water, Earth and Dark each have four active spells. See [EXPANSION.md](EXPANSION.md) for the 22 expansion abilities and [ABILITY_PRESENTATION.md](ABILITY_PRESENTATION.md) for all 54 presentations.

| Attribute | Existing effect per effective point | Additional capped effect |
| --- | --- | --- |
| Strength | +2.5% ability damage | +1% mining speed, maximum +50% |
| Agility | −1% cooldown, maximum −40% | +0.2% movement speed, maximum +20% |
| Endurance | +5 maximum stamina | +0.1 maximum health, maximum +10 health / 5 hearts |
| Spirit | +5 maximum mana | +1% mana regeneration, maximum +100% |

H shows effective values, passive contributions and tooltips explaining formulas/caps. Movement/mining bonuses combine with vanilla attributes, sprint and effects such as Speed/Haste. Mining still requires the correct tool and preserves blocks' normal drops. Health Boost and other health bonuses remain valid. Allocating Endurance increases capacity without healing; normal death/respawn fills the new maximum, while living respawn/reload retains current health.

At 20 ticks per second, mana regeneration starts at **3/second out of combat** and **0.8/second in combat**; Spirit can at most double these to 6 and 1.6. Regeneration stops at maximum mana.

Passive specializations are available to every class that meets their prerequisites: Flow Training gives Quick-style sword abilities 10% lower costs and cooldowns; Impact Training gives Heavy style +15% sword ability damage; Guard Training gives Defensive style −25% stamina cost; Fire Affinity gives −10% fire mana costs; Frost Affinity gives −20% frost cooldowns; Restorative Affinity gives +25% direct healing, excluding regeneration over time.

There are no new mobs, bosses, dimensions or story quests. Area damage targets hostile mobs and excludes co-op players and pets. Earth formations are cosmetic and leave terrain intact.

## HUD

The **176 × 40 GUI-unit** resource portrait shows mana, health and stamina, a circular player face and character level. The five-slot skill bar sits **2 units below stamina**, aligned to the resource bars, and scales the original layout to **75%: 136.5 × 16.5 units**, including 12 × 12 icons, cooldowns and key labels. Vanilla's hotbar stays centered at the bottom. The XP bar meets its actual outer frame with zero gap and no overlap.

The resource group sits 6 units from the left and adapts below GUI width 630 to reserve room for offhand, extra health, absorption, mounted indicators and air. Resource numbers are synchronized actual/current maxima, with compact notation for very large values. Absorption has a separate `+value` and gold strip; the heart icon reflects poison, Wither and freezing. F1 and spectator hide the mod HUD. Vanilla hearts and normal chat positioning remain the fallback until data is available. Chat, item names and actionbar receive space above the group, including their interaction areas. [HUD_LAYOUT.md](HUD_LAYOUT.md) documents exact coordinates.

## Progress and saved data

Character levels have no fixed maximum. Rewards received while already level 8+ grant **×1.5 mod XP**; each subsequent level still gives 5 stat points and 1 perk point. [LEVELING.md](LEVELING.md) explains the curve, fractional XP and legacy migration.

Progress is saved per player in `data/starswordmaster/<UUID>.properties` inside the world. Death, reload and reconnect preserve it. Existing registry IDs, classes and schema-1 data remain compatible. Available stat points are `5 × (level − 1) − allocated points`; available perk points are `level − 1 − learned nodes`. A level-10 character with 8 allocated stat points therefore has 37 unspent points. Reloading does not repeat rewards. Passive bonuses never overwrite allocated stats or grant spendable points. Fixed attribute modifier IDs prevent bonus stacking on repeated ticks or reconnects.

## Building and testing

Use **Java 21**. Run `./gradlew clean build smokeClasses` on Linux/macOS or `gradlew.bat clean build smokeClasses` on Windows. Install the normal JAR from `build/libs/`, excluding `-sources.jar` and smoke artifacts. JUnit, progression tests, resource/localization checks and pure layout/geometry tests run with the build. Smoke sources and capture mixins are excluded from the release JAR.

Smoke tasks require real runtime dependency JARs in each run directory's `mods/`, a display for clients, prepared isolated worlds and accepted server EULA. Multiplayer smoke uses offline login bound to loopback. Test scenarios may replace test progression, change health/equipment/gamemode or create/remove mobs: use the dedicated test worlds only. For A/B scenarios start Server, A and B tasks in separate terminals.

| Scenario | Gradle tasks (prefix `runSmoke`) | Directories / world / port |
| --- | --- | --- |
| Status | `Status` | `run/saves/StatusSmoke`; level 10, XP 1355, four active/one passive |
| HUD | `Hud` | `run/saves/HudSmoke`; player `StatusSmoke`; six window/GUI combinations |
| Points | `PointsServer`, `PointsA`, `PointsB` | `run/points-{server,a,b}`; `PointsSmoke`; 25567 |
| Attribute effects | `EffectsServer`, `EffectsA`, `EffectsB`, `EffectsSingleplayer` | `run/effects-*`; `EffectsSmoke`; 25568 |
| Resource HUD | `ResourcesServer`, `ResourcesA`, `ResourcesB`, `ResourcesSingleplayer` | `run/resources-*`; `ResourceSmoke`; 25569 |
| Animations | `AnimationsServer`, `AnimationsA`, `AnimationsB`, `AnimationsSingleplayer` | `run/animations-*`; `AnimationSmoke`; 25570 |
| Leveling | `LevelingServer`, `LevelingA`, `LevelingB`, `LevelingSingleplayer` | `run/leveling-*`; `LevelSmoke`; 25571 |
| Expansion/auras | `ExpansionServer`, `ExpansionA`, `ExpansionB`, `ExpansionSingleplayer` | `run/expansion-*`; `ExpansionSmoke`; 25572 |

Singleplayer worlds are under the corresponding `run/*-sp/saves/` directory. Animation/leveling worlds need cheats; animation servers use easy difficulty, `allow-flight=true` and OP for AnimationSmokeA/B. Leveling uses OP for LevelSmokeA/B. Points/effects use PointsSmokeA/B; resources use ResourceSmokeA/B; expansion uses ExpansionA/B. Server scenarios initialize their dedicated test profiles; singleplayer scenarios generally preserve profiles on reload, so repeatable fresh runs may need a fresh isolated fixture.

The HUD fixture uses XP 1355, MAGE, mana/stamina 100, Endurance 10, FIRE/FROST/SUPPORT/SWORD practice 10000, learned spark/frost_touch/guard/mend/mana_control, and slots spark/frost_touch/guard/mend/empty. Reset only its test cooldowns for repeat runs. Screenshots go to the run directory's `screenshots/`.

Automated checks and a successful build do not establish visual or multiplayer acceptance. See [VALIDATION.md](VALIDATION.md) for measured results and outstanding limits. Sound playback and compatibility with unrelated modpacks require separate verification.

### Finishing-pass native checks

`./gradlew runSmokeFinishingSingleplayer` runs all54 abilities, actual native hand/rock vertex checks, Better Combat, rejection/interruption and UI screenshots in isolated `run/finishing-sp/saves/AnimationSmoke`. Multiplayer uses `runSmokeFinishingServer`, then `runSmokeFinishingA` and `runSmokeFinishingB` after the server reports ready, on port 25574 and separate `run/finishing-*` directories. Test clients may use Danish Minecraft to verify English mod text. `runSmokeFinishingLifecycle` checks natural wall expiry, interruptions and first-person readability. These fixtures build only their own stone test platform and replace test character progression; never run them in a real play world. Runtime dependency JARs belong in each profile's `mods`. Smoke code is excluded from the release.

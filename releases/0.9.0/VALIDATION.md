# Validation — 0.9.0

## Build and automated checks

Minecraft/Fabric 1.21.10, Java 21; unchanged Better Combat 3.0.0 and Player Animation Library 1.1.3 dependencies. The fresh command

```
JAVA_HOME=/workspace/toolchain/jdk GRADLE_USER_HOME=/workspace/toolchain/gradle ./gradlew clean build smokeClasses --offline --console=plain
```

completed successfully with all 16 tasks executed. **254 JUnit tests, zero failures/errors/skips**, plus **37 progression checks**. `python3 -m unittest discover -s tools -p 'test_*.py'` passed both animation-generator tests. Release metadata is 0.9.0; the production JAR contains no smoke classes or smoke resources.

New checks cover canonical snapshot escaping/lifecycle/dedup; crossing-line clipping, culling, cached projections and progression; unchanged-snapshot UI timeout; every class threshold and evolution total/category; late selection, multilevel XP, huge BigInteger levels, save/reset/nonstacking, rank/style interactions, notification coalescing and stable aura hue. Geometry checks retain all 85 IDs/prerequisites, eight sectors, outer mastery hierarchy, fit/focus and accurate projected hit testing. All 85 node coordinates were independently compared with the pre-expansion coordinates and verified exactly 1.7× within floating-point tolerance. Minimum node separation is now 108.8 world units.

## Native Minecraft checks actually completed

All profiles use isolated disposable worlds with real Fabric clients/server and the required runtime mods under Xvfb/llvmpipe. They modify test characters and spawn test entities, so they must never be run against player worlds. COMPLETE markers establish completion; idle JVMs are subsequently stopped manually, producing a cleanup exit such as 143. That cleanup is not described as a successful Gradle build.

- `runSmokeRadialSingleplayer`: **1,565 client checks**, **99 server checks**, including **39 native kill/progression checks**. Actual tree fit/focus/pan/hit/details, learn/assignment, free reset/cancel/relearn, cooldown eligibility, disk reload, death and reconnect pass. Window sizes include 320×240, 640×360, 854×480, 1280×720 at GUI scale 2, and 1920×1080 at GUI scale 3. Real resource reload completes and post-reload selection still works.
- `runSmokeRadialServer/A/B`: two actual multiplayer clients, A **1,565 checks**, B **56 checks**, server **102 checks** including **42 native checks**. Per-player progression remains independent across kills, reset, respawn and reconnect. The same scale/resize/resource-reload/navigation checks pass on A. HP-based kill credit, hostile Evoker-owned Vex, protected player/pet exclusions, vanilla XP preservation and exact point entitlements remain valid.
- `runSmokeEvolutionServer/Client`: final extended run **27 client checks and six server checks**. Actual C2S class actions reject level 4, accept level 5, reject a second class and immediately derive stage 3 for late level-30 selection. One real server XP award crosses all milestones without consuming perk points. H reads actual evolution data; reset, save/forget/disk load, repeated attribute application, death and reconnect preserve bonuses once. Unchanged snapshots retain client object identity. A real spectator stat request is rejected without a changed snapshot; H recovers after its UI timeout, and a subsequent survival purchase consumes exactly one stat point.
- Evolution logs contain exactly two milestone messages (one crossed-level Swordsman transition and one late Mage choice), with no replay after death/reconnect. The pure milestone tests cover all intermediate stages and per-player isolation.

Fresh read-only review identified the unchanged-snapshot pending-stat issue; it was fixed, reviewed again and exercised natively. No remaining Important review findings were reported.

## Performance evidence and scope

The controlled native before/after benchmark used the same original geometry before expansion, 1920×1080 / GUI 2, 30 warm-up +120 measured renders for fit/focus/pan. Fit mean CPU submission fell **93.388 ms → 1.298 ms**, fills **7,548 → 932**, allocated bytes **1,667,911 → 253,813** per render. Focus and pan also improved. See [TREE_PERFORMANCE.md](TREE_PERFORMANCE.md) for all values, measurement scope and reproducibility caveats. These are actual render-thread CPU/GUI submission measurements, not hardware GPU FPS. Expanded geometry was validated separately by automated and native tests above.

All 54 active ability IDs, PAL clips, circles, terrain meshes and budget constants remain; this release changes derived class damage/resource multipliers and a bounded existing cosmetic-power input, not ability timing/target logic or VFX topology. The extensive 54-ability native presentation validation from 0.8.0 is archived in [releases/0.8.0/VALIDATION.md](releases/0.8.0/VALIDATION.md); it was not rerun as an all-54 native sequence for 0.9.0. Existing combat/domain/mesh/animation automated regressions pass.

Representative actual screenshots are in `docs/screenshots/0.9.0`. No audio-device, arbitrary-modpack, hardware-60-FPS or 64-real-client claim is made. Local offline Mojang-service/audio/cursor warnings did not prevent the recorded checks. Save schema, unlimited leveling, 1.5× rewards from level 8+, five stat points/one perk per level, personal UUID aura color, H/K bindings and HUD anchors remain unchanged.

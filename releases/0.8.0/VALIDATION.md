# Validation — 0.8.0

## Build and automated checks

Java 21, Fabric/Minecraft 1.21.10, Better Combat 3.0.0 and PAL 1.1.3. `JAVA_HOME=/workspace/toolchain/jdk GRADLE_USER_HOME=/workspace/toolchain/gradle ./gradlew clean build smokeClasses --offline --console=plain` completed successfully with all 16 tasks executed. **222 JUnit tests passed, no failures/errors/skips**, plus **37 progression checks**. `python3 -m unittest discover -s tools -p 'test_*.py'` passed both animation-generator tests covering all 54 clips. All 73 shipped PAL resources retain finite upper-body rotation-only controls and exact durations.

Geometry tests cover all 54 unique immutable meshes, closed topology and nonzero volume, finite bounds/normals, complete Low/Normal/Ultra parts, real target anchors and five phases. Regression tests reproduced and fixed a budget-overflow edge case, the solid eclipse center blocking the viewing ray, and hidden celestial sword hilts. Existing progression, reset, ownership, XP, HUD, aura and save tests remain green.

## Native Minecraft checks actually executed

All profiles used isolated run/overhaul-* worlds, native software rendering under Xvfb and the required runtime mods. Test fixtures create their own arena/entities and replace test progression; never use them in a player world. Runtime harnesses log COMPLETE and are then stopped; this cleanup exit is not presented as a passing Gradle build.

- `runSmokeOverhaulSingleplayer`: final expanded suite completed **2,600 checks**. All 54 native cinematic submissions have finite bounded vertices, unit normals, actual 3D volume and valid opacity. All five timeline stages were observed; all five priority ultimates exercised Low/Normal/Ultra. All eight magical elements exercised actual native first-/third-person palm matrices with both dominant arms (32 element/perspective/hand combinations). Actual Better Combat hits before/after, denied casts, fallback, interruptions, death, disconnect/reconnect and unchanged terrain were checked.
- `runSmokeOverhaulServer/A/B`: dedicated localhost port 25576 with two real clients. Caster A completed **1,931 checks**, observer B **117 checks**, including all 54 remote PAL/particle/native geometry presentations, five stages, native remote circles and cancellation. Real server target movement and removal verified exact victim attachment and safe fallback. Source also guards against numeric-ID reuse by checking the captured entity identity; forced ID replacement was not run as a separate native case.
- `runSmokeOverhaulShowcase`: **582 checks** after final celestial-hilt/readability polish, including all five ultimates at three quality levels, first-person celestial domain/eclipse/frost/water, real facet palette membership and ordinary Better Combat before/after. Actual screenshots were inspected; the eclipse now leaves the center clear, sword crossguards are above ground and foreground solids are not overpainted by clouds.
- `runSmokeOverhaulCodec`: **eight registry-backed wire checks**, completed and server exited normally. Eight-hit payload remains below 384 bytes; appearance below 40 bytes; all authoritative data and new victim ID roundtrip; oversized hit lists and malformed compact visual fields are rejected/clamped.

The full suite preceded the final hilt-position adjustment; the focused showcase and complete automated suite validate that final change. No shader-dependency, gameplay entity, collision or terrain modification was added. Every existing ability ID, server cost/damage/healing/cooldown/target rule and save field remains.

Representative real captures are in docs/screenshots/0.8.0; local run directories retain the complete captures. No concept images are shipped as textures. Existing 0.7.0 download/validation remains archived under releases/0.7.0.

## Performance and limits

CPU-only 64-candidate-caster cache/timeline stress measured 42.291 / 35.296 / 35.850 ms over 10,000 simulated frames, identical checksums; this is not an FPS/GPU/network benchmark or a before/after claim. See VFX_OVERHAUL.md for methodology and reproducible helper. All original particle/caster/distance limits remain. Cinematic and terrain geometry retain 2,048 faces each and share a 4,096-face total. Low retains closed cores; additional details use complete parts. Geometry/normals/palettes are cached; buffers are reused and disposed on client shutdown.

Audio-device acceptance, arbitrary modpacks and 64 real multiplayer clients were not tested. Native tests use llvmpipe/software rendering; no hardware-GPU FPS claim is made. Mojang authentication warnings in offline local fixtures are unrelated to mod checks.

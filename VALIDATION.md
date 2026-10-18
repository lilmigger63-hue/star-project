# Validation — 0.7.0

## Executed build and automated tests

Java 21, Minecraft 1.21.10, Fabric API 0.138.4, Loom 1.14.10, Better Combat 3.0.0 and PAL 1.1.3. `JAVA_HOME=/workspace/toolchain/jdk GRADLE_USER_HOME=/workspace/toolchain/gradle ./gradlew clean build smokeClasses --offline --console=plain` completed successfully, all 16 tasks executed. **213 JUnit tests passed, zero failures/errors/skips**, plus **37 standalone progression checks**. After the circular hub polish, incremental `build smokeClasses` was also run before the final singleplayer capture. Production JAR is checked for version, all 54 ability clips and exclusion of smoke-only classes.

New coverage: eight-sector 85-ID catalogue, real prerequisite edges/cycles/depth,64-unit spacing, decorative-versus-mechanical paths, complete bounds, fit/focus/pointerzoom/hit tests, responsive screen regions, BigInteger display, kill reward arithmetic/config/duplicate claims/ownership, reset entitlement/preservation/reload and cooldown debt. A reproduced cast/reset/relearn cooldown exploit was fixed and verified RED→GREEN, including save/reload. Hostile Evoker-owned Vex exclusion was fixed and checked with pure ownership tests and a real native kill.

## Native Minecraft scenarios

Dedicated isolated run/radial-* profiles only. No real player world was used. Required runtime mods were the versions above; test Minecraft used Danish while new mod labels remained English. Software-rendered Xvfb captures verify actual native menus/input, not mockups. Lack of audio device prevented sound acceptance; offline-loopback profiles log unavailable Mojang service warnings.

`runSmokeRadialSingleplayer`: actual lethal melee, arrow and indirect-magic events;20 HP mob = 60 base XP;1,000 maximum-HP mob killed from 1 remaining HP at level 8 = 4,500 actual XP; multiple levels and exact 5 stat / 1 perk entitlement; duplicate event no second reward; enemyEvoker-owned Vex = 42 base XP; passive cow / owned wolf / environment excluded; practice counter increments with no character XP; vanilla XP unchanged. Native backend **39checks**. Server persistence/derivedattribute/reset checks **99checks**. Client **1174checks**, including cancellation/confirmation through real buttons and actual ActionPayloads, malformedtarget/slot rejection, server-approved relearn/assignment, repeatedreset, death/respawn, diskforget/reload, integratedserver disconnect/reconnect and GUI windows320×240,640×360,854×480 plus1280×720 atGUIscale2.

`runSmokeRadialServer/A/B`: dedicated localhost port 25575 with two real clients; same kill/reset/persistence checks, PvP exclusion and ownership isolation. Backend **42checks**, persistence/attribute/reset **102checks**, client A **1174checks**. Client B completed 56 checks verifying its own rank 4 / XP/points/learned snapshot remains unchanged during A's kills/resets/reconnect. Completion counts are logged by each harness. Harness processes are stopped after COMPLETE; Gradle runtime exit caused by that cleanup is not counted as a passing build.

Native screenshots in docs/screenshots/0.7.0 show full fit, zoomed sword arm and narrow details. All eight spokes and real cross-arm links were visually inspected. Full-tree overview intentionally uses small icons; focus/zoom gives readable inspection. Narrow screens use separate full-width details and tree pages so controls remain accessible. A third-party boss mod was not installed: high-health native coverage uses an actual zombie with 1,000 MAX_HEALTH. Generic modded hostility uses the Monster interface or MONSTER spawn group; arbitrary modpack acceptance remains untested.

## Preserved behavior

All 85 ability IDs / 54 active / 31 passive nodes, prerequisites/class gates, permanent classes, four ranks, manual stats, existing XP curve / legacy offset / fractional carry, unlimited levels and single 1.5× reward multiplier from level 8. No combat damage/cost/cooldown duration, gameplay VFX, native Minecraft XP or HUD placement changed. Positive active cooldown debt survives respec/reconnect; discarded learned skills cannot cast. Existing 0.6.0 release and its validation remain in releases/0.6.0.

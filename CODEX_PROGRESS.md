# Current objective

Tree performance/class evolution overhaul is implemented and validated for release 0.9.0 on feat/tree-performance-class-evolution, based on published 0.8.0 commit1c6159a.

Completed in requested order: native baseline; cached native GUI line quads, clipping/culling/progression/text/button caches, overviewLOD and snapshot dedup;1.7× radial spacing; level5 manual permanent classes; independent derived10/20/30 evolution, fixed bounded bonuses, H/K UI and coalesced milestone feedback. All85nodes/54active abilities and existing skill ranks preserved. Save schema/XP/point rules unchanged.

Fresh clean build smokeClasses passed16tasks;254JUnit (no failures/errors/skips),37progression,2Python animation tests. NativeSP1565client/99server; two-clientMPA1565/B56/server102; evolution27client/6server. Resource reload, GUIs1–3, narrow windows, reset/respawn/reconnect validated. Review found pending-stat issue after snapshot dedup; fixed20tick UI timeout and actual spectator-rejection/survival-purchase native regression passed. No remaining Important review issues.

Measured oldgeometryfit native render CPU93.388ms→1.298ms,7548→932fills,1,667,911→253,813B/render. Not hardwareFPS. Fullmethods/results: TREE_PERFORMANCE.md; exactbalance: CLASS_EVOLUTION.md; tests/limits: VALIDATION.md. Versioned artifacts/checksum/docs in releases/0.9.0, actualcaptures docs/screenshots/0.9.0. Prior0.7/0.8downloads untouched.

Toolchain:JAVA_HOME=/workspace/toolchain/jdk,GRADLE_USER_HOME=/workspace/toolchain/gradle. Xvfb:99 must restart after environment reset. Native COMPLETE profiles are intentionally stopped after validation; cleanup143 is not a passing Gradle build. All test worlds are disposable run/* profiles. No player worlds touched.

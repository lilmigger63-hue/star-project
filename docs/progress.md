# Progress — playable-mod plan
User requested immediate implementation of a playable first release, then expansion. Execute inline in the existing isolated cloud workspace; do not modify the old modpack.
Build prerequisite: proxy returns CONNECT 403 for maven.fabricmc.net, meta.fabricmc.net and api.modrinth.com. Required domains were saved in the environment draft; this does not apply them to this runtime.
Task 1 started. Tasks 2–4 require the missing upstream access. No installable JAR exists yet.
Task 1: complete — 37 progression checks and 4 JUnit domain/storage tests pass.
Task 2: complete for base release — real Fabric 0.19.5 server and Better Combat 3.0.0 start; 15 client/server checks pass, including actual damage resources, healing, permanent class and rank.
Regression: fire spells held with a sword trained SWORD instead of FIRE; reproduced against saved live player data, corrected damage-source classification, real client now confirms FIRE increases and SWORD does not. Persistence checker passes.
Task 3: complete for base release — real rendered GUI inspected, texture region scaling corrected, tree fits small screens; 4 viewport tests pass.
Base 0.1.0 candidate retained in dist. Production installation test in progress.
Next: user-authorized automatic expansion adds eight active abilities and concrete passive rank bonuses, preserving the save schema and stable existing IDs.
Expansion complete: version 0.2, 32 nodes/26 active abilities, eight additional active skills and rank bonuses; stable old IDs and save schema retained.
Final review: one Important mana/stamina restoration ordering bug reproduced RED and fixed GREEN; full suite passes. GUI detail/state and Frost Touch range issues corrected. Deferred minor: configurable balance remains hardcoded, documented in README/VALIDATION.
Final runtime evidence: 20 real-client checks pass against installed release0.2 and 20 pass in integrated singleplayer. 12 JUnit tests and 37 standalone checks pass.
Resumed after transport failure: workspace intact; offline build succeeds. Packaging resumed without rebuilding gameplay or repeating completed runtime checks.
Version 0.3 expansion plan: six active skills (Piercing Thrust, Seismic Cut, Guardian Stance, Ember Ring, Ice Prison, Restoration) and six passive specializations (quick, heavy, defensive, fire, frost, healing). Existing IDs/class choices/save schema stay stable. Centralize computed costs/cooldowns/damage so HUD and server agree. Test style-switching, resource deduction, class/prerequisite gates and persistence before live singleplayer validation. User already authorized autonomous sword/magic expansion without design questions.
Version 0.3 complete: six new active skills, six passive specializations; 44 nodes/32 active. Shared cost/cooldown/damage calculation used by server and HUD. Active style highlighted in menu. Old0.2 save fixture preserves class, style, slots and learned IDs.
Independent review: no release blockers. Fixed two minor findings with RED→GREEN regression tests: actual cooldown visual duration now survives style switches/reloads, and expanded tree bounds fit initial viewport. Deferred minor: English ability descriptions remain Danish; README documents this.
Verification: 22 JUnit tests (no skipped/failures/errors), 37 standalone checks. Real integrated singleplayer tests cover all six new active skills: sword scenario32 checks, mage scenario11 checks. Test-only player hunger reduced to eliminate vanilla regeneration as a false positive for healing. No gameplay test helpers in released JAR.

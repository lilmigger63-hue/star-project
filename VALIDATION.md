# Validation — Star Swordmaster 0.6.0

Verified 17 October 2026. Historical reports are preserved in [the 0.5.0 archive](releases/0.5.0/VALIDATION.md) and earlier release directories.

## Automated checks

The finishing branch passes182 JUnit tests, with no failures or skipped tests, plus37 standalone progression checks. Final packaging uses `./gradlew clean build smokeClasses` on Java 21, Minecraft 1.21.10/Fabric, Better Combat 3.0.0+1.21.10 and PAL 1.1.3. No dependencies were added.

New coverage includes:

- Identical English `en_us`/`da_dk` mod translations, all 85 ability names/descriptions and dynamic UI families, missing/static translation references and Danish literal detection. Contrast checks cover readable charcoal/iron/bronze node states.
- Native-circle profile limits, continuous bounded power/rank refinement, finite timing and hand/frame deduplication.
- Deterministic noncoplanar Earth meshes, outward normals, wall 100-tick lifetime, delayed directional waves, budget limits, collision ground/ceiling fitting, liquid/cliff/unloaded/world-boundary rejection.
- Cached geometry covering all 54 active abilities, distinct silhouettes, finite reveal/dissolve envelopes, complete chain bounds and correct target anchoring for Curse Mark.
- Reverse-ordered save prerequisites and repeated snapshots preserve learned skills, ranks and both point budgets. The existing save/progression/XP/stat suite remains green.

## Actual Minecraft runs

All scenarios run real Minecraft/Fabric/PAL/Better Combat with a virtual display and software OpenGL, using isolated local worlds and offline test logins.

- Singleplayer finishing suite: **1,033 checks**. All 54 abilities animate, produce custom sprites and reuse the current authoritative costs/cooldowns. All eight magical elements produce actual native palm-circle vertices. Both arms and first-/third-person views match the native animated arm matrices within 0.001, with no unmatched extracted arm transforms. All four Earth formations submit actual opaque lit 3D vertices; checks compare every world vertex to its sampled-ground/rise transform, verify noncoplanarity and stable anchors, and confirm unchanged arena blocks.
- Dedicated server with two simultaneous clients: **1,079 checks** on the caster and **114 checks** on the observer. Both clients use Danish Minecraft; mod panels remain English. The observer sees all 54 PAL presentations plus actual remote native hand sigils, matched to the remote arm transforms. Rejection, duplicates, interruption, hand mirroring, reconnect and friendly-pet protection remain checked.
- Focused lifecycle suite: an additional **166 checks** verify natural wall expiry, weapon/damage interruption, cleared Earth/cinematic/hand state and ordinary Better Combat attacks before/after. This final suite also includes first-person Worldbreaker with reduced aura opacity.
- Native HUD regression: **101 checks** across six window/GUI combinations, including narrow windows, verify 136.5×16.5skill bar size/position, native hotbar/XP/hunger placement, icon scaling, extra health/air/offhand, click/scroll chat, F1, spectator, delayed data, H/K and rendering that does not mutate progress. The capture fixture was updated to recognize the intentional new iron frame color; geometry assertions were preserved.

Real screenshots were inspected for native hand circles in both dominant-arm modes, solid Earth pillars/spikes, the English H/K theme and the English status panel under Danish Minecraft. Vanilla Danish chat warnings remain Danish, as intended. The final Earth pass reduces large chip sprites and replaces duplicate pyramid-like embellishments with ground rings, keeping the solid formations legible. First-person sword aura opacity is reduced while preserving its hue and shape.

## Performance and safeguards

Reusable cinematic meshes are cached once per ability, and Earth topology is cached by shape/quality. Terrain sampling happens once per formation, only against loaded collision data. Rendering uses distance/frustum/quality culling and finite cleanup. Core particle limits stay 256 new particles/tick, 64 casters and 64 blocks. Additional mesh caps are2,048 cinematic quads/frame,2,048 Earth quads/frame and384 hand-circle quads/frame. Existing sword aura caps remain 1,024 quads/frame. Owned temporary render buffers are retained/reused and closed on client shutdown; native shared-buffer registrations are scoped to the game's owned entity provider.

A reproducible isolated Java 21 microbenchmark restored the same Swordsman snapshot 2,000 times in each of three trials, after 100 warmups. Before the no-progress early exit, times were 681.975 / 685.881 / 472.714 ms; after, 192.403 / 177.669 / 173.844 ms. Both produced checksum 354000. The change skips redundant prerequisite passes once no skill was learned; it changes no accepted save content. These are CPU microbenchmark results on the 4-core development environment, not FPS or GPU claims.

## Limits and release checks

Audio, long-duration gameplay balance, unrelated modpacks, other graphics drivers and human internet co-op were not tested. Pinterest/YouTube videos were not played or copied into assets; implementation used the supplied written brief and bundled screenshot, with original generated art.

Final packaging verifies the remapped JAR's version, ZIP integrity, required assets/mixin configs, 73 PAL clips, absence of smoke classes and SHA-256. The public download is verified after publication. No archived release files are replaced.

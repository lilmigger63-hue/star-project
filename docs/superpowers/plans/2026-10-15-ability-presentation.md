# Ability presentation implementation

1. Inspect PAL/Better Combat public APIs and runtime versions. Add compile access to these already-required, pinned runtime libraries through Modrinth Maven, with no bundled dependencies. Test the explicit catalogue against Ability.ALL before implementation.
2. Add presentation catalogue/geometry/lifecycle policy under main presentation package, independent of Minecraft for JUnit verification. Provide original JSON clips in assets/starswordmaster/player_animations. Assert non-looping, bounded clips and every active ability mapping; passives absent.
3. Add one server-authoritative presentation payload and dispatcher at the existing successful cast boundary. Send only to caster and trackers; emit damage/death cancellation without modifying combat. Remove the replaced generic particle cloud. Assert rejected casts emit nothing in runtime fixtures.
4. Register PAL controller factory and Better Combat attack listener on client. Add safe controller playback, short lifecycle, deduplication and bounded cosmetic particle patterns. Keep no gameplay state in the renderer.
5. Add isolated smoke fixtures for representative/all abilities, rejected casts, cancellation, normal Better Combat attacks, and two-client observation. Compile first, then run real SP and MP tests and existing unit/progression tests; inspect logs and screenshots.
6. Document complete ability-to-animation/VFX mapping and test evidence, review changes, fix substantive findings, build a versioned release JAR, publish to the authorized repository and verify the download.

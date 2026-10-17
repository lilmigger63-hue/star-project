# Dark fantasy finishing implementation plan

> For agentic workers: use executing-plans for root integration and dispatching-parallel-agents for independent renderer/UI domains. User has already selected direct execution and recommended defaults.

Goal: implement the ZIP brief on0.5.0 without replacing gameplay.
Architecture: reuse confirmed-event lifecycle; attach native hand sigils and bounded terrain/world meshes; translate/polish existing UI.
Tech stack: Java21, Fabric/Minecraft1.21.10, BC3.0.0, PAL1.1.3; no new dependencies.
Spec: ../specs/2026-10-17-dark-fantasy-finishing.md

Global constraints: preserve saves/IDs/gameplay/XP/HUD positions/keys/unique aura;256particles/tick,64casters,64blocks; original art; Englishmodstrings withoutglobal languagechanges.
Review focus: unloaded/sloped/liquid terrain; actual arm transforms vs item transforms; native deferred draw/buffer ownership; duplicate firstperson/world paths; interruption/dimensioncleanup and borrowed resources.

- [x] English/UI/docs: inventory all display literals and locale keys; add failing English/completeness checks; convert mod strings only and translate current guides; shared restrained UI palette, preserve geometry/hit tests; run focusedtests.
- [x] Hand circles: expose CastVisual(event,style,age); test bounded ring geometry/timing/mirroring; hook actual animated native arms first/third/remote; layered original sigils, depth/quality/budget and duplicate suppression; native matrix/screenshot checks.
- [x] Earth formations: test deterministic bounded volumetric geometry/lifetime; terrain sample loaded collision/liquid safely once; actual rock spike/wall/directionalwave/pillararray; finitebuff-alignedlifetime, no setBlockState; native ground/screenshot checks.
- [x] Other elements: reusable bounded world geometry with54 explicit profiles, correct palette and personal sword color; staged distinct arcs/bolts/ribbons/facets/runes, preserve immediate gameplay impacts; cache static metadata/geometry, test coverage/limits/highlevels.
- [x] Integration: lifecycle registration/cancel/clear/world transitions; no stale native buffers; synchronize only existing confirmed events; optimize proven repeated work; validate unchanged gameplay/saves/BC.
- [x] QA: ./gradlew clean build smokeClasses; SP/two-client renderer and54ability suites; actualH/KEnglish+GUIscales; screenshots, measured budgets, fresh review andfixes.
- [x] Release: Englishcurrentdocs/changelog/validation,0.6.0JARmetadata/no-smoke/checksum, publish authorizedGitHubmain andverifypublicdownload; keeparchivedreleases.

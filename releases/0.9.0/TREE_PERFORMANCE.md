# Skill-tree rendering measurements — 0.9.0

The benchmark measures actual `SkillTreeScreen.render` CPU submission time and thread allocation in a native Minecraft 1.21.10 client, not GPU frame time or FPS. Both runs used the same 85-node pre-expansion geometry, rank-four level-100 fixture, selected `spark`, 1920×1080 window, GUI scale 2, Java 21, Xvfb/llvmpipe, one Mesa worker and a 120 FPS cap. Each view discards 30 warm-up renders and records 120 renders. Screenshots are taken outside the measured section. The original classes remained loaded while subsequent source changes were prepared.

| View | Original mean / p95 CPU (ms) | Optimized mean / p95 CPU (ms) | Original → optimized fill submissions | Original → optimized bytes/render |
| --- | --- | --- | --- | --- |
| Fit all | 93.388 / 102.161 | 1.298 / 1.722 | 7,548 → 932 | 1,667,911 → 253,813 |
| Sword focus | 96.547 / 115.616 | 0.807 / 1.048 | 8,064 → 717 | 1,814,384 → 207,938 |
| Focus, pan −240/+80 | 55.437 / 70.262 | 0.535 / 0.715 | 6,245 → 572 | 1,409,394 → 167,303 |

Fit-view render-thread CPU fell about **98.6%** in this controlled sample. Fill counts are calls to the five-integer `DrawContext.fill` overload, including panels and controls; they are **not** GPU draw-call counts. Thread allocation includes the render method and native GUI state creation, not the entire game frame. Minecraft batches compatible GUI quads on its own pipeline. Software rendering, warm-up and the fixed scene limit generalization: no claim of hardware 60 FPS, arbitrary modpack performance or 64-client behavior is made.

## Implementation

- Immutable real prerequisite routes and decorative geometry are built once. Projection caches camera zoom/origin and GUI bounds; Liang–Barsky clipping keeps crossing segments with offscreen endpoints.
- Each visible segment is one transformed native GUI quad instead of hundreds of pixel fills. It retains Minecraft's scissor, pipeline batching and layer ordering; no custom GPU buffers or new mixins/dependencies are required by production code.
- Nodes, icons, selection frames, hub and labels are culled. Overview zoom below 0.19 keeps all real skills/dependencies interactive while replacing unreadable icon interiors with colored nodes and omitting twig decoration. Normal icons return on zoom-in.
- Progression, translated labels, details, XP formatting, node availability and button text are cached separately from changing resources/cooldowns. Selected prerequisite chains retain gold highlighting. Resource reload invokes screen `init` in Minecraft 1.21.10; it invalidates language/font-dependent caches even if the language object is unchanged.
- Canonical Properties snapshots omit the transient date comment. Server comparisons skip unchanged encoding/transmission; client comparisons skip duplicate parsing/reconstruction. Join/disconnect/server lifecycle reset caches. Existing immediate updates and five-tick polling remain.
- H stat requests have a 20-client-tick UI debounce expiry, so rejected requests cannot leave plus buttons locked when an unchanged snapshot is suppressed. No automatic retry or stat mutation occurs.

## Reproduction and evidence

Smoke-only `TreePerfCapture`, `TreePerfClient`, `TreePerfServer` and `TreePerfRenderMixin` measure actual renders. `runSmokeTreePerf` and `runSmokeTreePerfOptimized` differ only in their label; rebuilding the same current source twice does not recreate a historical before/after comparison. The reported baseline used original 0.8.0 screen/layout; the optimized run used the performance implementation before 1.7× spacing and class evolution. Current tasks exercise the current geometry. These local profiles need the disposable `TreePerfSmoke` fixture, existing runtime mods and display; they must never use a player world.

Recorded complete logs: `/workspace/tree-perf-baseline.log`, `/workspace/tree-perf-optimized.log`. Raw captures are copied to `docs/screenshots/0.9.0`. The completed expanded tree is also covered by automated geometry/projection tests and native fit/focus/hit/reset/resize/resource-reload tests. Native profiles log COMPLETE before idle JVM cleanup; cleanup exit 143 is not described as a successful Gradle build.

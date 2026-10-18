# Bounded 3D combat presentation expansion

Base:776b4bf / published0.7.0. Binding brief: uploaded star_swordmaster_vfx_overhaul/CODEX_PROMPT_FULL.md, CSV54IDs and11conceptimages (all viewed). User explicitly requests autonomous implementation after firstdownload; firstdownloadverified beforethiswork.

## Architecture and tasks
1. Cached original volumetric meshes for all54activeIDs, unique perability silhouette and choreography. Extend CinematicGeometry using cached Part/LOD metadata; preserve Mesh.faces compatibility and existing maximum96faces/mesh,2048cinematicfaces/frame. Core volumes remain closed onLow rather than skipping arbitraryfaces. Per-element shape families and five ceremonialultimates.
2. Pure AbilityVfxTimeline with five cosmeticphases derived from existing PALduration (no serveractivation/damagetimechange). Client renderer uses per-part transforms, actual confirmed impactpaths, target/caster/origin anchors, dynamicviewbounds andrank/power scaling. Globalworldgeometrybudget sharedwithEarth; existingcapsneverincrease. Native pixelmaterials/originalart only, unlitdepthtestedshader unchanged.
3. Refine54original PALclips with deliberate windup/release/recovery within originalticks, correctmirroring/aim/legs/BCpriority; improveexistingnativecircle brightness/rotation/release withoutchangingpalmattachments/layercaps. No newdependencies/protocol/gameplayentities.
4. Extend existing native finishing harness: all54coverage, actual3Dsubmittedvertices, allqualityLODs/budgets, handcircles first/third/both/remote, BCbeforeafter/rejections/interruptions/worldcleanup, terrainhash. Benchmark CPUcast-spam and allocation-boundedcache; report actualmeasurements only.
5. Fullcleanbuild+smokeClasses, freshreview/fixes, real screenshots per-element andfiveultimates, docs AbilityPresentation/VFX_OVERHAUL/VALIDATION/progress, immutable0.8.0artifactdownloadverified. Retain0.7.0andhistoricalreleases.

Interfaces: CinematicGeometry retains Point/Face/Mesh/form/anchor/ALL/MAX_FACES/FRAME_FACES; Mesh adds cached parts carrying faces/minQuality/motion/delay. Rootrenderer owns lifecycle/culling/transforms; meshagent owns generatorandtests; animationagent ownsclips/circlecodeandtests. Sharedbudgetcoordinates Earth andcinematiconly; auracircles retainoriginalseparatehardcaps.

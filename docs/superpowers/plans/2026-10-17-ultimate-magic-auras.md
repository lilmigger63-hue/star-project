# Ultimate Magic and Auras Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans for root integration and superpowers:dispatching-parallel-agents for independent renderer/assets work.

**Goal:** Deliver both supplied expansions with stable personal sword-aura colors.
**Architecture:** Extend authoritative progression/events; render bounded cosmetic state through existing PAL/native particles and held-sword transforms.
**Tech Stack:** Java21, Minecraft1.21.10, Fabric, Better Combat3.0.0, PAL1.1.3; no added dependencies.
**Spec:** ../specs/2026-10-17-ultimate-magic-auras.md

## Global Constraints
- Preserve old gameplay/save/XP/rank/point/HUD/key behavior.
- Preserve256particles/tick,64casters,64block distance; new geometry<=12blocks, sprites<=3blocks.
- UUID hue remains stable; server confirms state and all activations.

## Review Focus
- Extremely large levels and nonfinite decoded visual inputs: finite safe caps.
- Remote entity late tracking and reconnect: authoritative cached state, cleared worlds.
- Mirrored first/third-person held items with Better Combat: correct transforms and no duplicate aura.
- Invalid cast/empty area/pets: no spend, animation or unintended target damage.
- Existing saves and tiny GUI: old data retained, pan/zoom/access to all new nodes.

### 1 Domain, color and scaling
- [x] Write failing registration/HSV/scaling/save/requirement tests; run focused tests.
- [x] Implement VisualPowerScaling, AuraColors, VisualState, new Skill/Ability definitions and rank-damage categories; prove green.

### 2 Combat and synchronization
- [x] Add hostile-only new effects using existing targeting/cost validation and one event after success.
- [x] Add bounded authoritative visual payload/state tracking and impact path metadata.
- [x] Test rejection, costs, actual damage/control, save and event idempotence.

### 3 Independent renderer and assets
- [x] Renderer: held-sword hooks and finite bounded geometry/trails, first/third/remote, quality/culling/cleanup; test native rendering.
- [x] Assets:22unique clips +8reusable styles +23icons +element sprites; resource and pose tests.

### 4 Presentation and skill tree
- [x] Extend all54 active presentations with smooth scale, layers, new patterns and personal sword palettes.
- [x] Integrate85-node colored branches, translations, quality control and documentation.
- [x] Retain existing gameplay values and hard budgets; run regression build.

### 5 Runtime, review and release
- [x] Run integrated/dedicated two-client checks, screenshots, lifecycle/huge-level/color tests and Better Combat.
- [x] Review/fix, final ./gradlew build, package version0.5.0 with checksum and publish.

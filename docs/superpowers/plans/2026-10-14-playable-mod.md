# Star Swordmaster Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Deliver a buildable, playable Fabric mod with progression, permanent classes, five abilities and an interactive skill tree.
**Architecture:** A pure Java progression model is adapted to authoritative Fabric server state and synchronized client UI. Ability effects are executed by the server; clients never authorize progression or damage.
**Tech Stack:** Java 21, Minecraft 1.21.10, Fabric 0.19.5, Fabric API, Better Combat, Gradle Loom.
**Spec:** ../specs/2026-10-14-star-swordmaster-design.md

## Global Constraints
- No new monsters or bosses; preserve the existing modpack.
- Permanent class selection at level 10, with skill-dependent ranks.
- Exactly five configurable ability bindings; mana for all abilities and stamina for sword techniques.
- Learn by use, slower regeneration during combat, player-specific persisted data.

## Review Focus
- Spoofed unlock/cast requests must fail server-side.
- Save/reload and death must preserve progression without resetting permanent class.
- Duplicate ability assignments must not create extra cooldowns.
- Co-op effects must not damage other players.
- GUI scaling and rebinding must preserve access to all five slots.

### Task 1: Progression model
Files: progression/PlayerProgress.java, Skill.java, Ability.java, ProgressionTest.java.
Interfaces: awardPractice(Skill,int), chooseClass(PlayerClass), unlock(String), assign(int,String), cast(int), tick(boolean), toProperties(), fromProperties(Properties).
- [ ] Write and run failing tests for level-10 gating/permanent classes, prerequisites/ranks, resource/cooldown checks, five-slot uniqueness, combat regeneration and persistence validation.
- [ ] Implement the domain model and run the entire standalone Java suite.
- [ ] Record evidence and review model invariants.

### Task 2: Fabric integration and effects
Files: build.gradle, gradle.properties, settings.gradle, fabric.mod.json, StarSwordmaster.java, PlayerStore.java, ModNetworking.java, AbilityEffects.java.
Consumes: PlayerProgress and stable ability IDs. Produces server lifecycle, persistence and validated request handlers.
- [ ] Verify exact compatible upstream dependency versions and checksums; establish a working Loom build.
- [ ] Write integration tests for invalid requests, isolated player data and save/reload.
- [ ] Implement authoritative handlers, effects, practice XP and persistent storage.
- [ ] Run build/tests and actual dedicated-server startup with dependencies.

### Task 3: Client menu and HUD
Files: StarSwordmasterClient.java, SkillTreeScreen.java, SkillHud.java, client icon assets and language files.
Consumes: server snapshots and request protocol from Task 2.
- [ ] Test layout transformations, five-slot interactions, duplicate assignments and rebindings.
- [ ] Implement zoomable/draggable graph, detail panel, permanent-class confirmation, skill assignment, HUD and bindings.
- [ ] Build and run client checks; report graphical tests separately if no client display exists.

### Task 4: Delivery
Files: README.md, distribution JAR and source ZIP.
- [ ] Review security, persistence, co-op and compatibility against the spec.
- [ ] Run the full build/tests, inspect the resulting mod JAR and exercise installation/startup.
- [ ] Deliver only a verified build, with dependencies and explicit remaining local gameplay checks.

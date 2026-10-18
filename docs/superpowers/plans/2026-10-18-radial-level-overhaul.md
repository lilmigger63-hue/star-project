# Radial tree and kill XP implementation

Source specification: uploaded Star_Project_Level_And_Skill_Tree_Overhaul/CODEX_MASTER_PROMPT.md and specs/*.md. User requests implementation without routine approval and delivery before starting VFX.

## Tasks
1. Backend: test and implement server-authorized idempotent reset, max-health kill rewards with configurable eligibility/practice reward, exact existing XP multiplier, atomic save/snapshot/attribute refresh. Preserve allocated stats, proficiency, class, XP migration and every ability ID. Tests must catch duplicate reset/death, wrong killer, projectile attribution, invalid health, save/reload, level-eight behavior.
2. Geometry: test and implement cached eight-sector SkillTreeLayout with all 85 IDs once, increasing prerequisite radii, nonoverlapping nodes, decorative spines distinct from actual edges, full bounds; extend TreeViewport fit/focus and retain pointer-anchored zoom/hit tests.
3. UI: integrate geometry and translated labels in SkillTreeScreen, responsive tree/details/control regions, fit/focus/reset confirmation, exact hover values and compact XP/point summaries. Test via native isolated GUI harness at several resolutions/scales.
4. Release: run full build and smokeClasses, existing and new appropriate native progression/multiplayer checks, fresh code review, fix material findings, document actual validation and publish immutable 0.7.0 artifact with verified download.

Interfaces: backend reset_skills ActionPayload (no target/data); PlayerProgress.resetLearnedSkills() owns reset. Layout Node and cached Sector/Edge lists are shared by viewport/render/hit tests. New UI translations root-owned; backend owns its feedback messages. Shared changes coordinated before editing.

Next project: read BDO VFX references and create separate implementation checklist only after first download is available.

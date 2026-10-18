# Radial skill tree

The tree presents the existing 85 abilities once each around a graphical character hub. The hub is not an ability and costs no perk points. Existing IDs, prerequisites, proficiency requirements, character levels and class restrictions remain authoritative.

## Eight branches

Angles run clockwise in screen coordinates, with zero pointing right.

| Branch | Direction | Theme |
| --- | --- | --- |
| Swordsmanship | Upper left, 225° | Antique gold |
| Fire | Up, 270° | Ember orange |
| Frost | Upper right, 315° | Pale blue |
| Lightning | Right, 0° | Muted yellow |
| Water | Lower right, 45° | Deep cyan |
| Earth | Down, 90° | Moss green |
| Dark | Lower left, 135° | Ash purple |
| Support | Left, 180° | Pale teal |

`SkillTreeLayout` builds immutable geometry once from `Ability.ALL`. Actual parent depth, progression requirements and deterministic subtree lanes place nodes within their owning sector. Every child lies farther from the hub than its real parent, including links between sectors. Collision spacing is at least 108.8 world units (1.7× the former 64). Class-specific advanced skills and training nodes remain in their owning branches.

Muted brown spines and small forks are decoration: they do not create prerequisites, abilities or clickable nodes. The separate cached `EDGES` list contains only actual parent-child requirements. Unlearned links between sectors use ash purple; learned links use the child branch's theme; the selected skill's entire prerequisite chain uses gold. Node frames distinguish learned, available, locked and class-locked states, with gold around the selection. Existing pixel-art ability icons remain in use when readable; low-zoom overview uses small colored nodes while retaining every real skill and dependency.

## Navigation and actions

Scroll over the tree to zoom toward the pointer; drag to pan. **Fit** frames the whole layout, including its hub and decorative extents. The camera can fit below the old minimum zoom in a small tree area. The branch button cycles through focused views of all eight branches and then returns to the whole tree. Zooming from a small fitted view remains smooth and keeps the pointer's world position fixed.

Wide GUIs show a separate details panel. Below 500 GUI pixels wide, selecting a skill opens a full-width details page; the details/back control switches between that page and the tree. The fixed header and bottom controls stay outside tree hit testing. Details retain learning and assignment to five ability slots, with class choice and sword-style controls still available.

The header separates available perk points and stat points, and shows character level plus current/required XP. Compact values have exact-value hover text. Ability details show description, prerequisite, proficiency, character/class requirements, costs, cooldown and availability.

**RESET SKILLS** opens a confirmation screen. Confirmation requests a server-authorized reset; cancelling does not send a reset request. Learned abilities and assigned ability slots are cleared, spent perk points are refunded, and allocated stats, proficiency, class and XP are preserved. Repeating an already completed reset grants no additional points.

## Verification status

Automated geometry and viewport checks cover all 85 IDs, all eight sectors, real prerequisite routing, parent-depth ordering, node separation, padded bounds, decorative forks, hit testing, pointer-anchored zoom, small-area fitting and branch focus. Geometry and routes are cached; rendering does not traverse the ability graph or recompute the layout.

Native fit/focus/pan/selection/reset, narrow and wide windows, GUI scale changes and resource reload are exercised in the isolated radial smoke profiles. See `VALIDATION.md` for completed checks and `TREE_PERFORMANCE.md` for native before/after CPU measurements.

Active cooldown debt is retained through free reset, relearning and save/reload. Removed abilities cannot be cast; relearning does not bypass their remaining cooldown. Repeating a reset still grants no points.

`ClassEvolution` is independent of purchased ranks: manual class selection starts at level 5 and automatic titles/bonuses at levels 10, 20 and 30 appear in H and the K hub. See `CLASS_EVOLUTION.md`.

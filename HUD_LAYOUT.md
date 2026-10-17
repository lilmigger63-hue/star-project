# HUD layout

The resource panel retains its **176 × 40 GUI units**, portrait/level marker and blue mana / red health / green stamina identities. Iron/bronze frames and antique-gold labels match H/K. The skill bar sits below stamina; vanilla's hotbar stays centered at the bottom. Color changes do not change geometry or gameplay.

## Skill bar

- One **0.75 × 0.75 transform** scales the original 182 × 22 bar to exactly **136.5 × 16.5 GUI units**, without rounding the rendered model to 137 × 17.
- All five slots, frames, empty side areas, icons, cooldown overlays and labels scale together. Icons are 12 × 12, with 15-unit slot spacing. Existing textures use nearest-neighbor filtering.
- The bar's left edge is `resource.barX()`. Its top is **2 units below stamina's bottom edge**.
- Z/X/C/V/B, slot order, assignments, cooldowns and activation are preserved. Assignment buttons in K retain their original input regions/sizes. The gameplay transform does not scale H/K icons.

## Responsive anchors and native indicators

All coordinates use the current scaled GUI window. At width **630 or greater**, the resource panel is 6 units from the left, at `y=h−66`. The skill bar is at `x=50`, `y=h−24`, ending 7.5 units above the bottom. At least 8 units separate the group from vanilla's left offhand frame, including when a shield is equipped.

Below width 630, the left group moves above the armor/air indicators. The normal resource top is `h−151`; the skill bar follows stamina and ends at least 4 units above native indicators. The layout reserves 19 extra units compared with 0.3.8: 2 for the gap and 17 for the rounded reservation of a 16.5-unit bar. Rendering remains exactly 16.5 units high. Extra health rows, absorption, temporarily retained display health and mounted air indicators contribute to reservations.

**The vanilla hotbar does not move.** Its background starts at `h−22`, and the selected slot's outer frame at `h−23`. The 5-unit XP bar starts at **`h−28`** and ends exactly at **`h−23`**, giving **zero gap without overlap**. The level number follows at `h−34`; food is above XP at `h−38`, retaining vanilla's normal shake. This XP/level/food group moved down 30 units from 0.3.9.

Only XP, its level number and food use that placement. Armor, air, mount health, locator and mount-jump retain the previous 29-unit lift. Minecraft's choice between XP, locator and mount-jump bars is preserved, including associated level text. Resource and skill bars, chat and item-name anchors retain their established coordinates.

## Chat, visibility and data

Item names, actionbar and chat reserve space above the left group, including narrow windows. Chat rendering, click and hover regions move together; viewport height fits the remaining space and old messages can scroll. The typing field and message contents are preserved.

F1, spectator, missing/late snapshots and native-heart fallback retain existing behavior. Resource values remain authoritative snapshots or synchronized vanilla health/absorption. Layout/theme changes do not alter abilities, stats, points, saves, network state, unlimited leveling or ×1.5 rewards received at level 8+. See [VALIDATION.md](VALIDATION.md) for executed checks.

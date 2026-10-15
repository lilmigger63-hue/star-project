# Star Swordmaster 0.3.3

Fabric mod for Minecraft **1.21.10**, Java 21.

Download `star-swordmaster-0.3.3.jar` from this directory and put it in your Minecraft profile's `mods` folder. Remove any older Star Swordmaster JAR first.

Requires Fabric Loader 0.19.5, Fabric API 0.138.4+1.21.10, Better Combat 3.0.0+1.21.10, Cloth Config 20.0.149 and Player Animation Library 1.1.3 (the Fabric MC 1.21.9 artifact also supports 1.21.10). Dependencies are installed separately from their official pages linked in the [project README](../../README.md).

The skill bar now matches the vanilla hotbar at 182 × 22 GUI units, with five centered slots, 16 × 16 icons and a 4-unit gap. Resource strips remain above it, and vanilla status indicators move up to keep them clear.

**H** opens character status and allocates stat points with the four plus buttons; **K** opens the existing skill tree. Both keys can be changed in Minecraft Controls. Each character level gained grants 5 stat points; perk points retain their existing rules and buy skills only through K. This release preserves allocated stats, learned perks and the existing save format. Unspent stats are derived as 5 × (level − 1) minus allocated points: older characters receive the budget difference without duplicate awards on reload. For example, level 10 with 8 allocated points has 37 available stat points.

See [validation](VALIDATION.md) for test results and limitations. `SHA256SUMS.txt` records the JAR checksum.

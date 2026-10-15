# Active ability presentation

User intent: every registered active ability has a suitable animation and restrained, identifiable VFX, visible to caster and observers with Better Combat 3.0.0 and PAL 1.1.3 on Minecraft 1.21.10. Gameplay, targeting, costs, progression and storage remain unchanged. Proceed autonomously under the user's existing instruction.

Use the existing PAL animation stack, original short non-looping upper-body clips and an explicit presentation catalogue for all 32 active abilities. A dedicated layer yields to Better Combat attacks. Clips cover slash, heavy slash, thrust, parry, dash, cast, channel, aura and healing; fire/frost/star/healing/steel palettes and slash, ray, ring, ward, aura and healing patterns distinguish abilities. No passive casts.

A successful server activation emits one ephemeral S2C event (caster identity, sequence, ability, origin and impact position). Tracking players plus caster receive it once; clients deduplicate and expire stale events. The event replaces the old generic particle cloud and does not schedule gameplay. Immediate impact VFX reflect existing instant damage; subsequent trails/follow-through are cosmetic. No speculative animation on keypress. No persisted animation data.

On clients, PAL supplies the same animation controller for local and remote players. Damage, death, weapon change, another cast, ordinary attacks, world change and disconnect release the short-lived layer. Missing clips safely leave particles visible. Particle updates have bounded counts, lifetimes and concurrent casts, respect particle settings and distance. Server damage/death cancellation is synchronized to observers; held-item and ordinary attack state are also observed locally.

Verify catalogue completeness/exclusion of passives, original clip validity, geometry limits, event deduplication/lifetimes/cancellation, failure rejection and unchanged combat results. Run the existing build plus isolated real Minecraft singleplayer and two-client dedicated-server checks with required dependencies. Record actual results and limits, then publish the normal JAR and source.

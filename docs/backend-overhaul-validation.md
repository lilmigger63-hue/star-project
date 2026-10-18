# Kill XP and skill reset backend

The server uses Fabric API 0.138.4+1.21.10 `ServerLivingEntityEvents.AFTER_DEATH`, whose callback occurs after uncancellable death. The damage source's actual server player attacker receives credit. Player-owned projectile fallback is accepted when the source exposes that projectile; both existing magic implementations use `indirectMagic(player, player)` and already identify their caster.

Eligibility defaults to non-player mobs implementing `Monster` or registered in `SpawnGroup.MONSTER`, including compatible modded monsters and bosses. Player entities, uncredited deaths, living/removed/invulnerable victims, teammates, owned `Tameable` pets and player-controlled `Ownable` chains are excluded; hostile Evoker-owned Vex are eligible. A weak entity death-claim set prevents duplicate callbacks without retaining dead entities. No client request can award XP. The health input is the actual double-valued maximum-health attribute, never remaining health; decimal multiplication and ceiling produce `BigInteger` without a floating-point product overflow or arbitrary boss reward cap. `PlayerProgress.awardXp` remains the sole entry to the unchanged character multiplier and legacy migration arithmetic.

Optional server config is `config/starswordmaster-progression.json`, loaded at startup:

```json
{
  "killXpEnabled": true,
  "healthMultiplier": 3.0,
  "includePassiveMobs": false,
  "practiceCharacterXpMultiplier": 0.0,
  "showKillXp": true,
  "excludedEntityIds": []
}
```

Missing or malformed files use defaults. Malformed individual fields fall back independently. `healthMultiplier` must be positive and finite; practice multiplier may also be zero. Nonzero practice multiplier opts back into character XP from proficiency milestones, before the existing level multiplier. Practice counters and displayed proficiency continue regardless. Exclusions are exact registry IDs. No entity-tag exclusion mechanism is introduced.

Kill feedback aggregates actual gained character XP once per second, uses compact BigInteger text, and gives the existing level-up notification priority. It reuses actionbar text and the verified snapshot protocol. Vanilla XP is untouched.

`PlayerProgress.resetLearnedSkills()` returns whether learned skills or assigned slots changed. It clears learned skills and slots, retaining positive cooldown debt; derived perk entitlement refunds naturally. It preserves XP/level/legacy offset/fractional carry, permanent class, style, practice and allocated stats, and clamps mana/stamina downward without healing. Derived ranks and bonuses follow the remaining class and allocated stats. Positive cooldowns of valid active skills are restored even while unlearned, preventing a reconnect bypass.

The `reset_skills` action requires empty target and slot zero, runs on the existing server executor, and respects alive/spectator and request-limit checks. It always affects only the requesting player. Successful repeated empty resets remain harmless. The server cancels current ability presentation, applies derived attributes (including downward health clamp), saves atomically through `PlayerStore`, and syncs snapshots/visual state. Existing temporary vanilla spell effects expire normally; reset does not strip unrelated potion effects.

Validation executed with Java 21 and the shared Gradle cache:

```sh
JAVA_HOME=/workspace/toolchain/jdk GRADLE_USER_HOME=/workspace/toolchain/gradle ./gradlew test progressionTest smokeClasses
```

That run passed 207 JUnit tests, all 37 standalone progression checks and smoke-source compilation. Subsequent focused tests cover an additional real `PlayerStore` save/reload/reset cycle preserving exact fractional carry and legacy offset. Native singleplayer/multiplayer checks are tracked separately; pure tests do not establish native event/network behavior. Legacy smoke level commands now award their fixture XP explicitly instead of relying on the retired default practice reward.

Active cooldown debt is retained through free reset, relearning and save/reload. Removed abilities cannot be cast; relearning does not bypass their remaining cooldown. Repeating a reset still grants no points.

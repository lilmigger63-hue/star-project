# Animationer og effekter — version 0.3.6

Alle **32 aktive evner** i `Ability.ALL` er dækket. De 12 passive noder udløser ingen kast-animation. De 11 korte, originale klip ligger i `assets/starswordmaster/player_animations/` og indlæses gennem **Player Animation Library 1.1.3**. Klippene varer 10–20 ticks (0,5–1 sekund ved 20 TPS), vender tilbage til neutral og ændrer kun overkrop/arme; vanilla-bevægelse og sigte bevares. PAL-formatversion 3 er eksplicit angivet.

## Komplet oversigt

Klipnavn svarer til `ability_<navn>.json`. Stål bruger små kritiske gnister, ild små flammer, frost snefnug, stjerne lysende End Rod-partikler, og healing grønne helingspartikler. Radius nedenfor er udelukkende visuel og styrer aldrig skade eller målvalg.

| Evne / ID | Animation | Visuel effekt | Visuel radius |
| --- | --- | --- | --- |
| Gnister (`spark`) | `cast` | ild: stråle fra kaster til træfpunkt | 1 |
| Frostgreb (`frost_touch`) | `cast` | frost: stråle fra kaster til træfpunkt | 1 |
| Stjernelys (`light`) | `aura` | stjerne: stigende aura | 0.8 |
| Lægende lys (`mend`) | `heal` | healing: stigende helingsspiral | 0.65 |
| Lynsnit (`quick_cut`) | `slash` | stål: sværdspor | 1.6 |
| Sværdskridt (`quick_dash`) | `dash` | stål: kort bevægelsesspor | 0.7 |
| Kraftslag (`heavy_cut`) | `heavy` | stål: lodret hugspor | 2.2 |
| Stormfejning (`heavy_sweep`) | `sweep` | stål: sværdspor | 3 |
| Stjerneparade (`guard`) | `guard` | stål: beskyttende frontskjold | 0.9 |
| Modhug (`riposte`) | `thrust` | stål: koncentreret stikspor | 1.6 |
| Aurabølge (`aura_wave`) | `slash` | stjerne: stråle fra kaster til træfpunkt | 1 |
| Sværdets vilje (`blade_focus`) | `aura` | stjerne: stigende aura | 1 |
| Flammeudbrud (`fire_burst`) | `cast` | ild: udbrud omkring træfpunkt | 2 |
| Islanse (`ice_lance`) | `thrust` | frost: stråle fra kaster til træfpunkt | 1 |
| Helligdom (`sanctuary`) | `heal` | healing: stigende helingsspiral | 1.2 |
| Frostnova (`frost_nova`) | `channel` | frost: udadgående ring | 6 |
| Flammesværd (`flame_blade`) | `slash` | ild: sværdspor | 1.9 |
| Frostsværd (`frost_blade`) | `slash` | frost: sværdspor | 1.9 |
| Lynpres (`rapid_slash`) | `rapid` | stål: sværdspor | 1.8 |
| Knusende klinge (`crushing_edge`) | `heavy` | stål: lodret hugspor | 2.8 |
| Spejlparade (`mirror_guard`) | `guard` | stjerne: beskyttende frontskjold | 1 |
| Flammelanse (`flame_lance`) | `thrust` | ild: stråle fra kaster til træfpunkt | 1 |
| Frostværn (`frost_ward`) | `guard` | frost: beskyttende frontskjold | 1 |
| Magisk sværdskridt (`arcane_step`) | `dash` | stjerne: kort bevægelsesspor | 0.8 |
| Gletsjerfejning (`glacier_sweep`) | `sweep` | frost: udadgående ring | 5 |
| Fornyelse (`renew`) | `heal` | healing: stigende helingsspiral | 0.8 |
| Gennemborende stød (`piercing_thrust`) | `thrust` | stål: koncentreret stikspor | 2 |
| Jordrystende hug (`seismic_cut`) | `heavy` | stål: udadgående ring | 5 |
| Vogterstilling (`guardian_stance`) | `guard` | stjerne: beskyttende frontskjold | 1.2 |
| Glødering (`ember_ring`) | `channel` | ild: udadgående ring | 5 |
| Frostlænker (`ice_prison`) | `channel` | frost: lodrette froststænger omkring målet | 1.2 |
| Genoprettelse (`restoration`) | `heal` | healing: stigende helingsspiral | 0.95 |

## Aktivering, Better Combat og netværk

Serveren sender én kort præsentationshændelse efter en vellykket aktivering og eksisterende ressourceforbrug. Hændelsen indeholder kasterens UUID/entity-ID, løbenummer, verdenstid, evne-ID og positioner. Spilleren selv og spillere, der tracker kasteren, modtager den én gang. Der sendes ingen stream af partikler og ingen C2S-anmodning om animationer. Dubletter og gamle hændelser afvises; forsinkede gyldige hændelser starter på det relevante tidspunkt i klippet og udløber på deres oprindelige tidspunkt.

PAL-laget placeres umiddelbart under Better Combats angrebslag (verificeret prioritet 1999/2000), oven på dets hvilepositioner. Et almindeligt Better Combat-angreb afbryder evneposen; vanilla-slag uden registreret våben gør det samme. Et allerede igangværende Better Combat-angreb beholder sin animation. Modden skriver aldrig til Better Combats combo-, angrebs- eller skadestilstand. Håndspejling opdateres ved hvert kast.

Skade, død, våbenskift, nyt kast, almindelige angreb, entity-/verdensskift og disconnect rydder kortvarige animationer. Serveren synkroniserer skade-/døds-/våbenskiftsafbrydelser; klienterne følger også native angreb og entities. Et manglende klip giver partikler som fallback. Der er ingen animationsdata i gemmeformatet.

Skade og healing sker fortsat øjeblikkeligt som før. Træfpartikler vises straks ved det eksisterende træfpunkt; spor, ringe og aura er efterfølgende kosmetiske. Ingen nye projektil-entities eller forsinket/gentagen skade. Eksisterende lyde bevares, mens den tidligere generiske partikelklump erstattes.

## Grænser og installation

Partikelmønstre har højst 12 punkter pr. opdatering (normalt hver anden tick), højst 6 blokkes visuel radius og op til 8 små træfpartikler. Klienten begrænser samtidigt aktive kastere til 64, nye partikler til 256/tick og synlig afstand til 64 blokke; Minecrafts egne partikelindstillinger gælder også. Allerede udsendte vanilla-partikler forsvinder med deres normale korte levetid, når en animation afbrydes.

Brug 0.3.6 på server og klienter for de nye hændelser. Minecraft 1.21.10, Better Combat 3.0.0+1.21.10 og PAL 1.1.3 er uændrede runtime-krav. Build har nu compile-only-adgang til netop disse to eksisterende mods gennem versionsfastlåst Modrinth Maven; de bundtes ikke, og der tilføjes ingen nye runtime-biblioteker. Første build kræver netværk; efter cacheopbygning virker `./gradlew --offline build smokeClasses`.

Se [VALIDATION.md](VALIDATION.md) for faktiske build-/spiltestresultater. `src/smoke` er isoleret testkode og indgår ikke i den normale mod-JAR.

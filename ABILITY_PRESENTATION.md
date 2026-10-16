# Animationer og effekter — 0.4.0

Alle **32 aktive evner** har et særskilt originalt PAL-klip, `skill_<evne-id>.json`, og hver sin rumlige effektkoreografi. De 12 passive noder får ingen kast-animation. De 11 oprindelige klip bevares sammen med de 32 nye; PAL, Better Combat-laget og den eksisterende serverbekræftelse bruges fortsat.

Korte forberedelser, handling og efterbevægelse ligger inden for de hidtidige **10–20 ticks**. Klippene er PAL-format 3, ikke-loopende, med eksplicit neutral afslutning. Kun arme og begrænset torso-rotation animeres; ben, spillerens position og sigte ændres ikke. De originale animationer/designs kopierer ingen animefigur eller scene.

## Komplet oversigt

Hver række bruger klippet `skill_<ID>`. Klinge-efterbilleder er korte, gennemskinnelige buer efter sværdets bevægelse, ikke nye spiller-entities. Radius er fortsat udelukkende visuel.

| Evne / ID | Animation | Udbygget visuel effekt | Maks. mønsterradius |
| --- | --- | --- | --- |
| Gnister / `spark` | Skulder-samling, fingerkast | Små håndsegl, snoet glødespor, varmt træfglimt | 1 |
| Frostgreb / `frost_touch` | Indadgående kuldesamling, håndfladepres | Kantet frosttråd, iskronblade omkring målet | 1 |
| Stjernelys / `light` | Bryståbning, løftet stjernehånd | Fembladet stjernebane med svævende segl | 0,8 |
| Lægende lys / `mend` | Samlede hænder, blid frigivelse | Stigende helingsspiral af segl og små lysblade | 0,65 |
| Lynsnit / `quick_cut` | Stramt diagonalt snit, rekyl | Smal lysbue, korte klinge-efterbilleder og fartstreger | 1,6 |
| Sværdskridt / `quick_dash` | Armene ind, skulderfremdrift | To lave fartspor ved den bekræftede startposition | 0,7 |
| Kraftslag / `heavy_cut` | Høj garde, tungt lodret hug | Stor lodret halvmåne, faldende striber, kraftigere træfglimt | 2,2 |
| Stormfejning / `heavy_sweep` | Bred sideopladning, vandret fejning | Bred lav halvmåne med forskudte buer og striber | 3 |
| Stjerneparade / `guard` | Diagonal brystgarde, kort parade | Kompakt diamantgarde, håndsegl og stålstreger | 0,9 |
| Modhug / `riposte` | Parade, tilbagetræk, svarstik | Smalt dobbelt stikspor og koncentrerede gnister | 1,6 |
| Aurabølge / `aura_wave` | Lysende opadgående hug | Rejsende buet energifront langs det eksisterende træfpunkt | 1 |
| Sværdets vilje / `blade_focus` | Centrér klingen, kantkontrol, brysttræk | Roterende klingestreger og stjernesegl ved skuldrene | 1 |
| Flammeudbrud / `fire_burst` | Komprimér, eksplosiv håndspredning | Glødende flammeblomst med opadgående blade ved målet | 2 |
| Islanse / `ice_lance` | Form med støttehånd, skulderstik | Smal skår-lanse med lige kerne og isglimt | 1 |
| Helligdom / `sanctuary` | Bøn, beskyttende armhvælving | To lave lysbaner, større segl og helingsstjerner | 1,2 |
| Frostnova / `frost_nova` | Samling indad, radial frigivelse | To forskudte, udadgående ringe af lave frostskår | 6 |
| Flammesværd / `flame_blade` | Lav optakt, stigende piskesnit | Bølget ildbue med krøllede gløder og varm kant | 1,9 |
| Frostsværd / `frost_blade` | Høj stilling, bremset skråhug | Tandet ishalvmåne, skiftevis frostskår og efterbuer | 1,9 |
| Lynpres / `rapid_slash` | Krydsede snit, retningsskift | Tre korte, skiftevis diagonale sporfaser | 1,8 |
| Knusende klinge / `crushing_edge` | Samlet højt greb, knusende afslutning | Tunge krydsede buer og større koncentreret træflys | 2,8 |
| Spejlparade / `mirror_guard` | Krydsede hænder, spejltilt | Roterende spejlskår med dobbelte klinge-refleksbuer | 1 |
| Flammelanse / `flame_lance` | Skuldertræk, stik, rullende frigivelse | Snoet glødehelix omkring en smal lansekerne | 1 |
| Frostværn / `frost_ward` | Tegn en rude, lås klingen | Buet krone af isblade og et kompakt frontsegl | 1 |
| Magisk sværdskridt / `arcane_step` | Brystkryds, magisk sideskæring | Kort startsegl og spiralformede stjerne-fartstreger | 0,8 |
| Gletsjerfejning / `glacier_sweep` | Høj sidegarde, bred bue | Udadgående vifte af lave isskår og frostbuer | 5 |
| Fornyelse / `renew` | Hjertespiral, skiftende håndløft | Modroterende helingsspiraler og grønne buer | 0,8 |
| Gennemborende stød / `piercing_thrust` | Hoftetræk, langt rettet stik | Konvergerende lyspunkter og fremadrettede stikstreger | 2 |
| Jordrystende hug / `seismic_cut` | Højt løft, lavt slag, chokrekyl | Tre lave, udadgående brudlinjer med kantede gnister | 5 |
| Vogterstilling / `guardian_stance` | Bred vogter-afstivning | Tre beskyttende segl-/søjlesektorer omkring kroppen | 1,2 |
| Glødering / `ember_ring` | Krydssamling, cirkelspredning, løft | Roterende flammeblade og segl langs en voksende ring | 5 |
| Frostlænker / `ice_prison` | Forskudt søjleløft, krydset lukning | Fire hjørner af stigende isstænger og forseglingsmærker | 1,2 |
| Genoprettelse / `restoration` | Lav samling, hjertekryds, frigivelse | Stigende helingsblomst med omvendt rotation og segl | 0,95 |

## Pixelgrafik og begrænsninger

Seks originale, sparsomme partikelsilhuetter: **streak, arc, sigil, shard, flash, ember**. Stål bruger køligt hvidt/guld, ild orange/glødegult, frost cyan/ishvidt, stjerne violet/guld og healing mint/lysegrønt. Forskelle kommer fra form, bane, fase og rytme, ikke kun farveskift.

Spritekonturerne har skarpe pixelkanter og native nærmeste-nabo-filter. Korte buer, runer og skår er ét native atlas-quad pr. punkt. Punkter skaber ingen ekstra partikler, kolliderer ikke og ændrer ingen blokke eller entities. Normale spor varer 3–8 ticks; den fælles partikelklasse håndhæver højst 12 ticks og 1,5 blokkes quad-bredde. Tunge hug har større buer/træf end små kast og hurtige snit; effekterne er sparsomme, korte og uden skærmrystelser eller fuldskærmsoverlays.

De hidtidige grænser bevares: **højst 12 mønsterpunkter pr. opdatering**, normalt hver anden tick, **op til 8 umiddelbare træfpunkter**, **256 nye partikler/tick**, **64 aktive kastere**, **64 blokkes visningsafstand** og **6 blokkes mønsterradius**. Budgettet kan ikke blive negativt. Minecrafts indstillinger ALL/DECREASED/MINIMAL respekteres, også for de nye atlaspartikler.

## Aktivering, kamp og netværk

Den eksisterende server sender stadig præcis én kort præsentationshændelse efter gyldig aktivering og ressourceforbrug. UUID/entity-ID, sekvensnummer, verdenstid, evne-ID, start og træfposition er uændrede. Klienten genererer geometrien lokalt; ingen partikelstrøm, nye gameplay-projektiler eller C2S-animationsanmodninger. Dubletter, udløbne hændelser og ukendte evner afvises som før. En forsinket gyldig hændelse afspilles kun i den resterende oprindelige levetid.

**Skade og healing sker stadig på det oprindelige tidspunkt.** Et træfglimt vises straks ved serverens eksisterende træfpunkt. Visuel håndopladning, spor og efterbilleder følger bagefter og forsinker ikke gameplay. Rækkevidde, målvalg, skade, priser, cooldowns, stats, XP og gemte data ændres ikke.

PAL-laget er fortsat umiddelbart under Better Combats angrebslag, verificeret 1999/2000. Almindelige kombinationer bevares. Håndspejling opdateres ved kast; kropsmønstre spejles rumligt, og hånd-/projektilspor følger den relevante sigteretning. Der skrives ikke til spillerens sigte, bevægelse eller Better Combat-tilstand.

Angreb, skade, død, våbenskift, nyt kast, entity-/verdensskift og disconnect rydder aktive klip. Serverens eksisterende skade-/døds-/våbenskiftsafbrydelse bevares. Allerede udsendte kosmetiske quads dør hurtigt af sig selv. Manglende PAL-klip giver fortsat partikler; manglende eller endnu ikke indlæste sprites falder tilbage til de eksisterende native elementpartikler.

## Installation og tests

Brug **0.4.0 på både server og klienter**. De seks partikeltyper registreres normalt af Fabric; ingen nye afhængigheder. Minecraft 1.21.10, Better Combat 3.0.0+1.21.10, PAL 1.1.3, Fabric API og Cloth Config er uændrede krav.

Generatorerne i `tools/generate_ability_animations.py` og `tools/generate_presentation_particles.py` kan genskabe de originale assets. `src/smoke` indeholder isolerede tests og pakkes ikke i mod-JAR'en. Se [VALIDATION.md](VALIDATION.md) for de faktisk gennemførte build-, singleplayer- og multiplayertests.

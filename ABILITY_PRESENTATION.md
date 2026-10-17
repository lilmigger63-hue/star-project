# Animationer og effekter — 0.5.0

Alle **54 aktive evner** har et særskilt originalt PAL-klip, `skill_<evne-id>.json`, og en unik rumlig effektkoreografi. De **31 passive noder** får ingen kast-animation. De 11 oprindelige klip bevares; 54 evneklip og otte genbrugelige stilklip giver i alt **73 PAL-ressourcer**. Better Combat-laget og den eksisterende serverbekræftelse bruges fortsat.

De hidtidige 32 klip beholder deres 10–20 ticks; de 22 nye varer 12–32 ticks. Alle er PAL-format 3, ikke-loopende, med eksplicit neutral afslutning. Kun arme og begrænset torso-rotation animeres; ben, position og sigte ændres ikke. Armrotationernes bidrag skalerer kosmetisk 75–100 % med level, uden ændret timing. Originale animationer/designs kopierer ingen animefigur eller scene.

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

## 22 nye evner

Hver række bruger `skill_<ID>.json`. Den unikke rytme og håndstilling bygger på en af de otte nye stilressourcer; kastene er selvstændige klip.

| ID | Animation, faktisk klipbeskrivelse | Visuel effekt | Ticks |
| --- | --- | --- | --- |
| `static_spark` | Pinch a charge beside the shoulder, snap the wrist, then discharge a second tiny recoil. | Zigzag-lyn med håndladning og kort elektrisk træf | 12 |
| `chain_lightning` | Lift the conductor hand and relay three alternating arcs between the open palms. | Forgrenet zigzag langs de faktiske servertræfpunkter | 20 |
| `thunder_step` | Cross a charged guard, tuck both elbows through the step and flare an electric exit. | Lave dobbelte lynspor fra startpositionen | 12 |
| `heavens_wrath` | Stack stormclouds overhead, split their crown and conduct descending thunder with both hands. | Faldende lyn, stormruner og roterende stormkrone | 30 |
| `water_bolt` | Cup a droplet above the support palm, corkscrew it forward and collect the spent stream. | Snoet vanddråbe-helix langs sigtelinjen | 14 |
| `tidal_wave` | Scoop a low tide, roll the crest between the hands and push the whole wave outward. | Fremadrettet vandvifte med høj bølgetop | 22 |
| `aqua_blade` | Weave water beneath the hilt, carve a flowing rising edge and roll its crescent away. | Flydende klingehalvmåne og vanddråber | 16 |
| `ocean_prison` | Raise offset spirals into a high water cage, then close its gate with crossed palms. | Stigende vandspiraler og runer omkring målet | 26 |
| `stone_spike` | Find the earth with a low palm and lever a narrow stone point up from beneath the target. | Kantede stenskår løftet fra målets fodpunkt | 14 |
| `earthen_wall` | Set two foundations, lift a heavy slab and brace the barrier with square open palms. | Tre lag af svævende klippestykker og værnruner | 20 |
| `quake_blade` | Take a weighted crown guard, drive the sword into the low line and absorb two ground pulses. | Tre radiale brudlinjer med hævede stenskår | 22 |
| `worldbreaker` | Draw the mountain overhead, suspend its weight, then shatter the seal with a monumental double slam. | Dobbelte chokfronter og større springende klippestykker | 30 |
| `shadow_bolt` | Hook a shadow from the opposite palm, twist it into a dart and cast it from a half-open guard. | Snoet skyggespor med mørke blade | 14 |
| `curse_mark` | Trace a hooked sigil, seal its center with two fingers and pull the curse thread taut. | Modroterende forbandelsesrune ved målet | 18 |
| `shadow_edge` | Draw shadow along the blade, cut a low hooked arc and fold the trailing darkness inward. | Mørke klinge-efterbilleder langs et diagonalt hug | 16 |
| `eclipse_nova` | Cup a black sun, eclipse it behind crossed arms, then tear the horizon into an open nova. | Sekstakket formørkelsesfront med runer og skyggeskår | 30 |
| `astral_crescent` | Draw a low celestial edge, sweep a clean rising moon and stop with the point held high. | Bred stigende stjernehalvmåne med klinge-efterbilleder | 16 |
| `falling_star` | Set the star above the crown, draw it downward through the blade and catch its falling weight. | Femspidset stjernefront ved et tungt nedslag | 22 |
| `phantom_blades` | Mark three floating sword lanes with alternating cuts and gather their phantom edges into guard. | Fire skiftende faser af fantomklinger; ingen ekstra træf | 20 |
| `celestial_rush` | Tuck a star-point guard, drive the shoulders through a straight rush and flare the trailing blade. | Dobbelte fartspor, klingeskår og stjerneglimt | 14 |
| `sovereign_judgment` | Raise the sovereign blade like a scepter, hold its verdict aloft and cast a single commanding stroke. | Stort faldende klinge- og runespor | 28 |
| `heavenly_sword_domain` | Draw an oath across the heart, crown the domain overhead and unfold a cathedral of celestial swords. | Roterende klingekrans med vekslende runesøjler | 32 |

Genbrugelige klip: `style_dual_cast.json`, `style_ground_slam.json`, `style_overhead_cast.json`, `style_sword_release.json`, `style_lightning_channel.json`, `style_water_weave.json`, `style_earth_raise.json`, `style_shadow_summon.json`.

## Pixelgrafik og begrænsninger

Tolv originale, sparsomme partikelsilhuetter: **streak, arc, sigil, shard, flash, ember, bolt, droplet, rock, shadow, rune, blade**. Grundpaletterne er stål hvid/guld, ild orange/gul, frost cyan/ishvid, stjerne violet/guld, healing mint, lyn gul/hvid, vand blå/cyan, jord sand/grøn og mørke violet/sort. Sværdteknikker bruger spillerens personlige aurafarve, når auraen er låst op, inklusive lysere/mørkere nuancer og rang 4-højlys. Rang ændrer aldrig grundfarven. Forskelle kommer fra form, bane, fase og rytme, ikke kun farveskift.

Spritekonturerne har skarpe pixelkanter og native nærmeste-nabo-filter. Korte buer, runer og skår er ét native atlas-quad pr. punkt. Punkter skaber ingen ekstra partikler, kolliderer ikke og ændrer ingen blokke eller entities. Normale spor varer 3–8 ticks; den fælles partikelklasse håndhæver højst 12 ticks og 3 blokkes quad-bredde efter levelskalering. Tunge hug har større buer/træf end små kast og hurtige snit; effekterne er sparsomme, korte og uden skærmrystelser eller fuldskærmsoverlays.

De hidtidige grænser bevares: **højst 12 mønsterpunkter pr. opdatering**, normalt hver anden tick, **op til 8 umiddelbare træfpunkter**, **256 nye partikler/tick**, **64 aktive kastere**, **64 blokkes visningsafstand** og **12 blokkes skaleret visuel radius** (grundmønstrene holder sig inden for 6,1). Budgettet kan ikke blive negativt. Minecrafts indstillinger ALL/DECREASED/MINIMAL respekteres, også for de nye atlaspartikler.

## Aktivering, kamp og netværk

Den eksisterende server sender stadig præcis én kort præsentationshændelse efter gyldig aktivering og ressourceforbrug. UUID/entity-ID, sekvensnummer, verdenstid, evne-ID, start og træfposition bevares. Den versionerede v2-payload tilføjer kompakt serverberegnet rank/power/farve og højst otte faktiske træfpositioner. Største hændelse holder sig under 384 bytes; separate appearance-opdateringer under 40 bytes. Klienten genererer geometrien lokalt; ingen partikelstrøm, nye gameplay-projektiler eller C2S-animationsanmodninger. Dubletter, udløbne hændelser og ukendte evner afvises som før. En forsinket gyldig hændelse afspilles kun i den resterende oprindelige levetid.

**Skade og healing sker stadig på det oprindelige tidspunkt.** Et træfglimt vises straks ved serverens eksisterende træfpunkt. Visuel håndopladning, spor og efterbilleder følger bagefter og forsinker ikke gameplay. Rækkevidde, målvalg, skade, priser, cooldowns, stats, XP og gemte data ændres ikke.

PAL-laget er fortsat umiddelbart under Better Combats angrebslag, verificeret 1999/2000. Almindelige kombinationer bevares. Håndspejling opdateres ved kast; kropsmønstre spejles rumligt, og hånd-/projektilspor følger den relevante sigteretning. Der skrives ikke til spillerens sigte, bevægelse eller Better Combat-tilstand.

Angreb, skade, død, våbenskift, nyt kast, entity-/verdensskift og disconnect rydder aktive klip. Serverens eksisterende skade-/døds-/våbenskiftsafbrydelse bevares. Allerede udsendte kosmetiske quads dør hurtigt af sig selv. Manglende PAL-klip giver fortsat partikler; manglende eller endnu ikke indlæste sprites falder tilbage til de eksisterende native elementpartikler.

## Installation og tests

Brug **0.5.0 på både server og klienter**. De tolv partikeltyper registreres normalt af Fabric; ingen nye afhængigheder. Minecraft 1.21.10, Better Combat 3.0.0+1.21.10, PAL 1.1.3, Fabric API og Cloth Config er uændrede krav.

Generatorerne i `tools/generate_expansion_assets.py`, `tools/generate_ability_animations.py` og `tools/generate_presentation_particles.py` kan genskabe de originale assets. `src/smoke` indeholder isolerede tests og pakkes ikke i mod-JAR'en. Se [EXPANSION.md](EXPANSION.md) for aura-/skaleringstal og [VALIDATION.md](VALIDATION.md) for de faktisk gennemførte build-, singleplayer- og multiplayertests.

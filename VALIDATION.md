# Validering — Star Swordmaster 0.3.4

## Udvidede stateffekter i version 0.3.4

Verificeret 15. oktober 2026:
- `./gradlew --offline build smokeClasses` med Java 21 består: 62 JUnit-tests, ingen fejl eller springede tests, og 37 selvstændige progressionskontroller. Ni nye tests dækker nul/lave/høje stats, grænserne lige før/ved/over loftet, kampregeneration, maksimal mana, eksisterende rang-/klasse-/stilperks og uændrede gamle gemmefelter. Statusmodellen testes også mod de beregnede statbonusser.
- Nye bonusser: Styrke +1% mining/point, maks. +50%; Smidighed +0,2% movement speed/point, maks. +20%; Udholdenhed +0,1 maksimalt liv/point, maks. +10 liv; Ånd +1% manaregenerering/point, maks. +100%. De eksisterende evneskade-, cooldown-, stamina- og manabonusser er uændrede. De nye grænser gælder statbidraget og erstatter ikke andre mods' attributter.
- Serveren ejer tre attributmodifiers med faste ID'er. Minecraft synkroniserer dem til klienterne. Mining og movement er midlertidige; ekstra maksimalt liv er vedvarende, så vanilla kan indlæse det før aktuelt liv og kopiere det ved levende respawn. Ingen ændringer i moddens schema-1-data, fordelte stats, XP-kilder, lærte evner eller protokol. Plusknapperne i H og separate perkpoint i K bevares; hvert opnået karakterlevel giver fortsat præcis fem statpoint.
- En rigtig dedikeret Fabric-server med Better Combat og to samtidige Minecraft 1.21.10-klienter består 59 klientkontroller for A, 57 for B og 54 serverkontroller. A starter med 10 i hver stat, B med 100; begge har 14 eksisterende evner/perks og Mester-rang. Der testes faktisk mining med jernhakke og Haste, værktøjskrav for diamantmalm, ubrudelig bedrock, sprint + Speed, Health Boost, eksisterende ressource-/skade-/healingperks samt præcis 20 ticks manaregenerering både i og uden kamp.
- Fire yderligere attributkontroller køres efter rigtig Minecraft-bootstrap: 100 ens opdateringer giver ingen duplikater/ekstra ændringscallbacks, andre modifiers og attributbaser bevares, ændrede/nulstillede stats erstatter/fjerner kun eget ID, og manglende attributter håndteres sikkert. Klient-server-scenariet anvender desuden bonusserne 200 gange og kontrollerer én modifier pr. attribut samt uændrede progressionsdata.
- Rigtige H-klik opdaterer Udholdenhed og maksimalt liv uden heling eller ændrede perkpoint. K har fortsat ingen statkontroller. Død giver fuldt udvidet maksimalt liv; levende respawn bevarer delvist helbred over 20. Genforbindelse genindlæser både moddata og Minecraft-spillerdata uden duplikation: 20,6/21,1 og 29,5/30 liv bevares. Spillernes værdier forbliver separate.
- Det samme scenario består i rigtig singleplayer: 59 klientkontroller og 27 serverkontroller, inklusive normal gem-og-afslut, fuld nedlukning/genstart af den integrerede server og genindlæsning med 20,6/21,1 liv og den købte Udholdenhed bevaret. Attributkontrollerne består igen efter begge serverstarter.
- H er visuelt kontrolleret ved 1280×800/skala 1 og 640×480/skala 2, inklusive aktuelle gamle/nye bonusser, caps, plusknapper, scroll og tooltips. Dansk og engelsk har opdaterede statbeskrivelser; generatorens stattekster matcher ressourcefilerne.
- Release-JAR'en har korrekt 0.3.4-metadata, gyldig ZIP-integritet og ingen smoke-testklasser. Ingen nye eksterne afhængigheder. Testprocesser lukkes efter de eksplicitte COMPLETE-markører; Gradles afsluttende exit 130 ved denne manuelle lukning er ikke et fejlet gameplay-assert.

Begrænsning: langvarig balance og vilkårlige andre modpakker er ikke gennemspillet. Cloudmiljøets manglende lydenhed og onlineprofiladgang er uændrede; testserveren er kun bundet til localhost.

## Separate statpoint og perkpoint i version 0.3.3

Verificeret 15. oktober 2026:
- `./gradlew --offline build smokeClasses` med Java 21: 53 JUnit-tests og 37 selvstændige progressionskontroller består. Nye tests dækker alle 100 levels, to levels fra én XP-tildeling, adskilte pointbeholdninger, gamle fordelte stats, gentagne indlæsninger, persistens for forskellige UUID'er, levelmaksimum og op til 495 point i én stat.
- Statbudgettet udledes af level og gemte stats uden nye felter: `5 × (level − 1) − fordelte point`. Gamle stats og perkregler bevares; differencen til det nye budget er til rådighed uden gentagen tildeling. Gemmeformatet er fortsat schema 1.
- H viser fire plusknapper, særskilte pointtal og de eksisterende stateffekter. K har kun perk-køb. Serveren validerer, gemmer og synkroniserer via den eksisterende protokol. Minecraft-XP og moddens XP-kilder er uændrede.
- Begge klientscenarier består på en rigtig Fabric-server med Better Combat og to samtidige klienter: 46 kontroller for A og 43 for B. De dækker gamle profiler, H-køb i alle fire stats, K-oplåsning, afvisning af overforbrug og udskiftning af pointtype, flere levels, ubrugte point, død/respawn og genforbindelse. Klient B overvåger, at klient A ikke påvirker dens point. Testprofiler og helperkommandoer indgår ikke i mod-JAR'en.
- Kodegennemgangens scroll/Enter-kanttilfælde blev reproduceret med en fejlet klienttest før rettelsen. Input opdaterer nu knappernes synlighed før tastaturaktivering. Layouttests dækker knappernes placering og fravær af klikbare knapper uden for udsnittet. H er visuelt kontrolleret i bredt og smalt layout; manglende data/forsinkede snapshots er også dækket af klienttesten.

## Evnebjælke i version 0.3.2

Verificeret 15. oktober 2026:
- `./gradlew --offline build` med Java 21: 39 JUnit-tests og 37 selvstændige progressionskontroller består. Fem nye layouttests dækker vanilla-mål, fem centrerede slots, GUI-skala/resize og afstand til både XP og locator-pilens yderste kant.
- Klientscenariet `./gradlew runSmokeHud` er kørt med Minecraft 1.21.10, Better Combat og de eksisterende afhængigheder. Billeder er kontrolleret ved 1280×720/skala 2, 960×720/skala 3, 853×481/skala 1 og 640×480/skala 2 samt skjult HUD (F1).
- Alle 16 klientkontroller består: rækkefølgen Z/X/C/V/B, eksisterende cooldown, uændrede data efter rendering, placering efter resize samt bevaret H-status og K-færdighedstræ. Skærmbilleder viser en lokal testkarakter med fire tildelte evner og en tom femte plads.
- Vanilla-indikatorerne forskydes kun, mens evnebjælken vises. Locator-markører bruger samme forskydning som locator-bjælken via en lille klient-mixin, fordi Fabric 1.21.10 tegner dem separat. Ingen nye eksterne afhængigheder, ændrede evneberegninger eller ændringer i gemmeformatet. Locator-pilens afstand har en regressionstest; ingen ny samtidig co-op-session er gennemført.

## Statusskærm i version 0.3.1

Verificeret 15. oktober 2026; version 0.3.1 indeholder statusskærmen, mens downloadpakker for 0.3.0 bevares:
- `./gradlew --offline build` med Java 21: 34 JUnit-tests og de eksisterende 37 selvstændige progressionskontroller består. De nye tests dækker XP-grænser/levelmaksimum, ufuldstændige data, uforanderlig statusvisning, aktuelle snapshots samt layout og rulning ved forskellige GUI-størrelser.
- Den separate Fabric-klienttest består alle 17 kontroller i Minecraft 1.21.10 med Better Combat og de eksisterende afhængigheder: H, bevaret K, ombinding til J, Escape, manglende/forsinkede data, korrekt XP og evnegruppering, levende ressourceopdateringer samt rullebjælkens klik/træk ved smal GUI-skala. Layoutet er visuelt kontrolleret med én og to kolonner.
- Statusskærmen genbruger eksisterende spillerdata og synkronisering. Den sender ingen handlinger, og gemmeformat, progression og Better Combat-integration er uændrede. Testkilderne indgår ikke i mod-JAR'en. Ingen ny test med to samtidige co-op-spillere er udført for denne skærm.

## Udgivet version 0.3.0

Verificeret 14. oktober 2026:
- Java 21 og Fabric/Minecraft-versioner er uændrede fra 0.2; build og tests består.
- 22 JUnit-tests: otte grundtests, ni specialiserings-/kompatibilitetstests og fem viewporttests. Ingen fejl eller springede tests. Den separate progressionstest udfører yderligere 37 kontroller.
- Sværdscenariet udførte 32 kontroller i en rigtig Minecraft-singleplayer-klient, inklusive stilbonusser, Gennemborende stød, Vogterstilling og Jordrystende hug.
- Magikerscenariet udførte 11 kontroller i en separat singleplayer-verden: Glødering, Frostlænker, Genoprettelse, mana, cooldown-bonus og faktisk heling. Alle seks nye aktive evner er dermed brugt i spillet.
- Nye passive bonussers stilafhængighed, faktiske ressourceforbrug, cooldown og persistens er testet. En faktisk gemmefil fra version0.2 bevarer klasse, stil, slots og tidligere lærte evner.
- Cooldown-bjælken ændrer ikke varighed ved stilskift. Træets nye ydergrene er med i viewporttests. Begge rettelser har først fejlede regressionstests og derefter beståede tests.
- Kodegennemgang fandt ingen release-blokerende fejl. Træet er visuelt inspiceret i klienten; screenshots viser separate lokale testkarakterer, ikke startsituationen for en ny spiller.

Version0.3 er testet med Minecrafts integrerede singleplayer-server. Den almindelige release-JAR bygges og kontrolleres som arkiv; den særskilte installationstest på dedikeret server nedenfor vedrører version0.2. Tidligere klient-server-validering og afhængighedsversioner er bevaret, men der er ikke udført en ny samtidig multiplayer-session med mennesker i version0.3.

Begrænsninger: langvarig balance og lyd er ikke verificeret. Grafik er foreløbig, balanceværdier ligger i kildekoden, og engelske effektbeskrivelser er stadig på dansk. Ingen nye historiequests, NPC'er eller mobs. Smoke-testklasser og testverdener medfølger ikke i installationspakken.

## Tidligere validering af version0.2



Verificeret 14. oktober 2026:
- Java 21, Minecraft 1.21.10, Fabric Loader 0.19.5 og Fabric API 0.138.4+1.21.10. Build kontrolleret igen efter genstart af cloudmiljøet.
- 12 JUnit-tests: otte progression-/persistenstests og fire viewporttests. Ingen fejl eller springede tests. Den separate progressionstest udfører 37 adfærdskontroller.
- Den remappede release-JAR er installeret og startet med officiel Fabric-serverlaunch, Better Combat 3.0.0+1.21.10, Cloth Config 20.0.149 og Player Animation Library 1.1.3.
- En rigtig Minecraft-klient udførte 20 kontroller mod denne server: synkronisering, permanent klasse, grundmagi, sværdteknik, rang, evnepladser, mana, færdigheds-XP, stamina, heling, klassebegrænsning og udvidede evner.
- De samme 20 kontroller bestod i en lokal singleplayer-verden med Minecrafts integrerede server.
- Træet er visuelt kontrolleret i Minecraft; flytning, skalering og zoom har automatiske tests.
- Release-JAR og downloadpakker kontrolleres for ZIP-integritet. Smoke-testklasser indgår ikke i release-JAR'en.

## Rettede fejl
Ildbesværgelser med et sværd i hånden trænede tidligere sværd frem for ild; skadetyper filtreres nu korrekt. Den rigtige klient og gemte data bekræfter rettelsen.

Bonus-mana og bonus-stamina kunne blive beskåret ved genindlæsning. En regressionstest reproducerede fejlen før rettelsen og bekræfter nu, at de ekstra ressourcer bevares.

Træet og teksturregioner skaleres nu korrekt i små vinduer. Tilgængelige og klassebegrænsede evner har forskellige markeringer; detaljer findes også i tooltips.

## Begrænsninger
Alle 26 aktive evner er ikke gennemspillet individuelt. Grundevner og repræsentative udvidelser er testet i spillet; klassernes oplåsningsruter har modeltests. Balance kræver længere gennemspilning. To samtidige menneskelige co-op-spillere er ikke afprøvet; separate spillerdata og klient-server-funktioner er verificeret.

Cloudmiljøet har ingen lydenhed, så lydafspilning er ikke testet. Testserveren var kun tilgængelig lokalt. Minecrafts online profilrequests var utilgængelige under udviklingstestene.

Grafikken er foreløbig originalgrafik, og balanceværdierne er fastlagt i kildekoden. Der er ingen nye mobs, bosser, historiequests eller NPC'er fra universet.

## Genbrug af cloudmiljøet
Download-domæner, install_script og start_skill er gemt i konfigurationsudkastet. Gennemgå og gem miljøindstillingerne og publicér miljøet for at genbruge opsætningen i nye opgaver. Download og lokal installation af modden kræver ikke dette trin.

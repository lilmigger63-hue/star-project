# star-project
to mods

## Star Swordmaster 0.3.10 downloads

- [Download the mod (.jar)](https://raw.githubusercontent.com/lilmigger63-hue/star-project/main/releases/0.3.10/star-swordmaster-0.3.10.jar)
- [Checksums](releases/0.3.10/SHA256SUMS.txt)
- [Validation and limitations](VALIDATION.md)

Version **0.3.10** placerer vanilla-XP, leveltal og sult direkte over hotbaren: XP-bjælkens nederste kant møder hotbarens yderkant med **0 GUI-enheders afstand** og uden overlap. Ressourcepanelet og evnebjælken under stamina bevarer deres placeringer. [HUD-layout](HUD_LAYOUT.md).

Version **0.3.9** flytter evnebjælken **under stamina**, venstrejusteret med ressourcebjælkerne, og skalerer hele bjælken til **75 %: 136,5 × 16,5 GUI-enheder**. Evne- og ressourcegruppen tilpasser sig smalle vinduer med plads til offhand, chat og øvrige indikatorer. [Placeringer og responsivt layout](HUD_LAYOUT.md).

Version **0.3.7** giver ubegrænset karakterlevel, **×1,5 mod-XP fra belønninger modtaget på level 8+** samt daværende placering over hotbaren. Den placering er erstattet af layoutet i 0.3.9. Der gives stadig 5 statpoint og 1 perkpoint pr. level, og gamle levels/XP/point bevares sikkert. [XP-regel, migration og HUD-geometri](LEVELING.md).

Version 0.3.6 tilføjede animationer og visuelle effekter til **alle 32 aktive evner** med 11 originale Player Animation Library-klip. Sværdhug, stik, fejninger, parader, dash, ild, frost, stjerneaura og healing har passende udtryk; andre spillere ser de serverbekræftede aktiveringer. [Komplet oversigt over evner, animationer og effekter](ABILITY_PRESENTATION.md). Den runde markør og blå mana/rødt liv/grøn stamina fra 0.3.5 bevares sammen med H/K, Better Combat-angreb og separate stat-/perkpoint. Kræver Minecraft **1.21.10**, Better Combat **3.0.0+1.21.10** og PAL **1.1.3** samt de øvrige eksisterende afhængigheder nedenfor. Udskift den gamle Star Swordmaster-JAR på klient og server; behold kun én version.

Selvstændig Fabric-mod til Minecraft **1.21.10**. Din egen karakter udvikler sig gennem sværdkamp og magi, med permanente klasser og et forgrenet færdighedstræ. Inspireret af Star-Embracing Swordmaster, Solo Leveling og Skyrim; dette er en første spilbar version med egne mekanikker og grafik, ikke en færdig genskabelse af historierne.

## Installation
1. Lav en Minecraft 1.21.10-installation med **Fabric Loader 0.19.5** og Java 21.
2. Installer disse Fabric-mods fra deres officielle sider:
   - [Fabric API 0.138.4+1.21.10](https://modrinth.com/mod/fabric-api)
   - [Better Combat 3.0.0+1.21.10](https://modrinth.com/mod/better-combat)
   - [Cloth Config 20.0.149](https://modrinth.com/mod/cloth-config)
   - [Player Animation Library 1.1.3](https://modrinth.com/mod/player-animation-library). Den kompatible fil hedder `PlayerAnimationLibFabric-1.1.3+mc.1.21.9.jar`; udgiverens metadata angiver også Minecraft 1.21.10.
3. Læg **star-swordmaster-0.3.10.jar** i installationens `mods`-mappe. Behold kun én version af Star Swordmaster.
4. Start Minecraft. I co-op skal alle klienter og serveren have samme version af modden og dens afhængigheder.

Brug gerne en separat Minecraft-profil til første afprøvning. Andre modpakker er ikke kompatibilitetstestet; Better Combat og afhængighederne ovenfor er testet sammen med denne mod.

## Kom i gang
- **K** åbner træet. Alle moddens taster kan ændres under Minecrafts Kontrol-indstillinger.
- **H** åbner den separate statusskærm med level/XP, klasse, rang, kampstil, stats, point, mana/stamina og lærte evner. Plusknapperne ved Styrke, Smidighed, Udholdenhed og Ånd bruger ét statpoint pr. klik. Du kan fordele point frit eller gemme resten. Korte effektbeskrivelser står ved hver stat; afkortet tekst kan læses ved at holde musen over den. Serveren godkender og gemmer hvert køb; tallene opdateres fra dens snapshots. At kigge på skærmen bruger ingen point. Luk med Escape eller Færdig.
- **Z, X, C, V, B** aktiverer evneplads 1–5. Bjælken ligger 2 GUI-enheder under stamina og er venstrejusteret med ressourcebjælkerne.
- Evnebjælken måler **136,5 × 16,5 GUI-enheder**. Hele det oprindelige layout er skaleret til 75 %, inklusive fem slots, 12 × 12-ikoner, cooldown-visning, labels og tomme sideområder. Vanilla-hotbaren forbliver centreret nederst.
- Ressourcepanelet har en rund skinmarkør med level og tre bjælker i rækkefølgen mana, liv og stamina. Panelet måler 176 × 40 GUI-enheder og forankres 6 enheder fra venstre kant, 26 fra bunden. Ved GUI-bredde under 630 flyttes panelet og evnebjælken samlet op over de native indikatorer, stadig ved venstre kant. Ekstra liv, absorption og ridedyr/luft indgår i pladsreservationen. Alle positioner genberegnes ved skalering og ændring af vinduet.
- Tallene er faktiske værdier som `nuværende/maksimum`, afrundet til én decimal efter behov; værdier fra en milliard vises i kompakt notation, så teksten bliver i bjælken. Mana, stamina og level kommer fra moddens eksisterende serversnapshots; liv, maksimalt liv, absorption og statuseffekter kommer fra Minecrafts synkroniserede spillerobjekt. Absorption vises som et separat `+tal` og en gylden stribe. Livsikonet viser også gift, Wither og frysning, når relevant.
- Vanilla-hjerterne erstattes kun, mens det nye panel kan vises. Sult står over vanilla-XP; XP-bjælkens nederste kant møder hotbarens yderkant med præcis 0 afstand. Leveltallet følger med XP-bjælken. Rustning, luft, ridedyrshelbred, XP/locator og genstandsnavn bevares; rustning, luft, ridedyrshelbred og locator-/rideindikatorer beholder det tidligere løft på 29, mens XP/level/sult bruger den nye hotbar-placering. Genstandsnavne/actionbar og chat får plads over HUD'en; chatlinks og hoverområder følger med, og chatten kan scrolles i det tilpassede udsnit. F1 skjuler både ressourcepanel og evnebjælke; spectator viser dem ikke. Mens spillerdata mangler, bruges den normale vanilla-visning og chatplacering. Minecrafts faktiske health, skade, heling og statuseffekter ændres ikke.
- Start som Uindviet. Slå eksisterende fjendtlige mobs med et sværd for at træne sværdfærdigheden. Der gives ikke erfaring for at slå i luften.
- Færdigheder forbedres gennem brug. Når en færdighed stiger, får karakteren XP. Hvert karakterlevel efter level 1 giver **5 statpoint** til H og **1 perkpoint** til K. Flere levels på én gang giver belønningen for hvert level. Dette er separat fra Minecrafts almindelige XP.
- Statpoint køber kun stats i H. Perkpoint låser kun færdigheder/specialiseringer op i K; ingen af pointtyperne kan erstatte den anden. K har ingen statfordeling.
- Lær en grundevne i træet med et perkpoint. Klik derefter en af knapperne 1–5 for at tildele den til bjælken.
- Sværdteknikker kræver et sværd i hovedhånden. Ret sigtet mod en fjendtlig mob for målrettede angreb. Magi bruger mana; sværdteknikker bruger også stamina. Ugyldig brug koster ingen ressourcer.
- Mana og stamina regenererer automatisk, langsommere under kamp. Kampstatus varer otte sekunder efter skade.
- Ved **level 10** kan du vælge Sværdkæmper, Magiker eller Magisk sværdkæmper. Valget kræver bekræftelse og er **permanent i den enkelte verden**.
- Klasseperks åbner Adept- og Mester-rang. Alle klasser kan lære grundmagi og grundlæggende sværdteknikker.
- Vælg Hurtig, Tung eller Defensiv stil nederst i menuen. De forbedrer henholdsvis Lynsnit, Kraftslag og Stjerneparade. Nye passive stiltræninger udvider bonusserne til sværdteknikker generelt; den valgte stil er markeret med kantparenteser. Almindelig kamp bruger Better Combat.
- Træk i træets baggrund for at flytte udsnittet; scroll for at zoome. Hold musen over en evne for krav, pris og effekt. Lærte evner har guldmarkering, tilgængelige har turkis markering, og klassebegrænsede grene har rød markering.

## Indhold
44 noder: **32 aktive evner** og 12 passive specialiseringer og klasse-/rangperks.

Grundmagi: Gnister, Frostgreb, Stjernelys, Lægende lys og Fornyelse. Sværdteknikker: Lynsnit, Sværdskridt, Kraftslag, Stormfejning, Stjerneparade og Modhug.

Sværdkæmper: Auraopvågning, Aurabølge, Sværdets vilje, Sværdmester, Lynpres, Knusende klinge og Spejlparade.
Magiker: Manakontrol, Flammeudbrud, Islanse, Helligdom, Frostnova, Magimester, Flammelanse og Frostværn.
Magisk sværdkæmper: Elementbinding, Flammesværd, Frostsværd, Stjernesværdmester, Magisk sværdskridt og Gletsjerfejning.

### Stats i version 0.3.4

| Stat | Eksisterende effekt pr. point | Ny effekt pr. point | Maksimal ny bonus |
| --- | --- | --- | --- |
| Styrke | +2,5% evneskade | +1% normal mining-hastighed | +50% ved 50 point |
| Smidighed | −1% cooldown, maks. −40% | +0,2% movement speed | +20% ved 100 point |
| Udholdenhed | +5 maksimal stamina | +0,1 maksimalt liv | +10 liv / 5 hjerter ved 100 point |
| Ånd | +5 maksimal mana | +1% manaregenerering | +100% ved 100 point |

Grænserne gælder de nye statbidrag; de gamle effekter og rang-/klasse-/stilperks ændres ikke. H viser aktuelle statbidrag, mens tooltips forklarer effekten pr. point og grænserne. Movement speed og mining speed bruger Minecrafts bonus til attributbasen og kombineres med blandt andet sprint, Speed og Haste. Health Boost og andre livsbonusser bevares. Mining kræver fortsat det rigtige værktøj og ændrer hverken blokke eller loot.

Manaregenerering er fortsat langsommere i kamp: grundraten er 3 mana/sekund uden kamp og 0,8 i kamp ved 20 ticks/sekund; Ånd kan højst fordoble dem til 6 og 1,6. Regeneration stopper ved maksimal mana. Ekstra liv fra Udholdenhed øger kapaciteten uden at hele ved pointfordeling. Normal død/respawn giver fuldt nyt maksimalt liv; levende respawn og genindlæsning bevarer aktuelt liv.

Rangperks giver ekstra ressourcer; Mester-rang giver yderligere evneskade.

Ingen nye mobs, bosser eller dimensioner. Nye skadesområder rammer kun fjendtlige mobs og rammer ikke co-op-spillere eller deres husdyr.

## Nyt i version 0.3
Sværd: **Gennemborende stød** (rækkevidde 5 blokke), **Jordrystende hug** (områdeskade og sløvning) og **Vogterstilling** (absorption og modstand).
Magi: **Glødering** (ild omkring spilleren), **Frostlænker** (kraftig sløvning i fem sekunder) og **Genoprettelse** (direkte heling og regeneration). Disse tre avancerede besværgelser kræver Magiker-klassen.

Passive specialiseringer, som kan læres af alle klasser med de nødvendige grundfærdigheder:
- Flydende klinge: hurtig stil giver 10% lavere mana-/staminaforbrug og kortere cooldown på sværdteknikker.
- Kraftens vej: tung stil giver 15% ekstra evneskade med sværd.
- Vogterens vej: defensiv stil giver 25% lavere staminaforbrug.
- Ildtilknytning: ildbesværgelser bruger 10% mindre mana.
- Frosttilknytning: frostevner har 20% kortere cooldown.
- Lægende tilknytning: direkte heling er 25% stærkere; regeneration over tid får ikke denne bonus.

Menuen viser de aktuelle omkostninger og cooldowns med dine bonusser. Stilskift ændrer fremtidige aktiveringer, mens en igangværende cooldown beholder sin oprindelige varighed og visning.

## Gemte data
Progression gemmes pr. spiller i verdensmappen under `data/starswordmaster/<UUID>.properties`. Den bevares ved død og genindlæsning. Tag backup af hele verden, når du opdaterer mods. Version 0.3 bevarer eksisterende evne-ID'er, klasser og dataschema fra 0.1/0.2. Cooldown-visning får ekstra metadata uden at ændre det gamle format.

Version 0.3.3 bruger fortsat samme gemmeformat. Ubrugte statpoint beregnes som `5 × (level − 1) − allerede fordelte statpoint`; gamle karakterer beholder deres stats og får differencen til det nye budget til rådighed. Eksempel: level 10 med 8 fordelte point har 37 ubrugte statpoint. Gentagen indlæsning eller død tilføjer ikke budgettet igen. Alle 495 optjente statpoint ved level 100 kan samles i én stat og bevares ved lagring. Perkberegningen er fortsat `level − 1 − antal lærte noder`.

Version 0.3.4 ændrer ikke moddens schema-1-data. Serveren anvender én modifier pr. attribut med faste ID'er; mining og movement er midlertidige attributbonusser. Maksimalt liv gemmes som en almindelig vedvarende Minecraft-attribut, så vanilla indlæser den før aktuelt liv. Gentagne opdateringer, respawn og genforbindelse lægger ikke bonusserne sammen igen.

## Validering og begrænsninger
Bygningen, automatiske progression-/persistens-/viewporttests og rigtig Fabric-opstart med Better Combat er verificeret. En lokal Minecraft-klient bruges også til at teste klassevalg, rang, evnetildeling, evner og heling mod en rigtig server. Se `VALIDATION.md` for endelige resultater.
Grafikken er original, foreløbig pixelgrafik. Balanceværdier og XP-kurver er fastlagt i kildekoden i denne version. Lydafspilning kunne ikke verificeres i cloudmiljøet. Der er endnu ingen historiequests eller NPC'er fra universet.

Engelske evnenavne findes, men effektbeskrivelserne er foreløbig på dansk.

## Byg fra kildekoden
Brug Java 21. På Linux/macOS: `./gradlew build`. På Windows: `gradlew.bat build`.
JAR'en findes derefter i `build/libs/`. Installer den almindelige JAR, ikke `-sources.jar` eller lokale smoke-testfiler. De særskilte smoke-testkilder inkluderes ikke i den almindelige mod-JAR.

Statusskærmen tilføjer ingen afhængigheder og bevarer gemmeformatet. Dens model- og layouttests køres af `build`. Den separate klienttest kan køres med `./gradlew runSmokeStatus`, når en lokal testverden `StatusSmoke` og de normale runtime-afhængigheder er klargjort; scenariet forventer en testkarakter med level 10, 1355 XP, fire aktive evner og én passiv evne.

HUD-layoutets separate klienttest køres med `./gradlew runSmokeHud`. Den bruger en isoleret testverden `run/saves/HudSmoke`, de normale runtime-mods i `run/mods` og spillernavnet `StatusSmoke`. Klargør testprofilens schema-1-properties med XP 1355, klasse MAGE, Stamina/Mana 100, Udholdenhed 10, træning FIRE/FROST/SUPPORT/SWORD 10000, lærte spark/frost_touch/guard/mend/mana_control og slots spark/frost_touch/guard/mend/tom. Nulstil kun testprofilens cooldowns før gentagelser. Scenariet ændrer testspillerens helbred, udstyr, Minecraft-XP og gamemode og gemmer billeder i `run/screenshots/`; brug kun testverdenen. Det observerer native renderkoordinater, seks vindues-/GUI-kombinationer inklusive breddegrænsen 630, faktisk 75 %-skalering af bjælke/ikoner, nul afstand mellem native XP og valgt-slots yderkant, leveltekst sammen med XP, offhand-afstand, aktivering via V/C og vanilla-hotbarens talgenvej, cooldown/ressourceopdateringer, ekstra liv/luft, chatklik, F1, spectator, sene snapshots, creative-genstandsnavn og H/K. Testklasser og capture-mixins indgår ikke i mod-JAR'en.

Pointtesten bruger tre separate terminaler: `./gradlew runSmokePointsServer`, `./gradlew runSmokePointsA` og `./gradlew runSmokePointsB`. Klargør en lokal Minecraft-testverden `run/points-server/PointsSmoke`, serverens EULA/server.properties (offline, bundet til 127.0.0.1:25567) og de sædvanlige runtime-mods i `mods` under hver af `run/points-server`, `run/points-a` og `run/points-b`. Serveropgaven nulstiller kun testprofilerne PointsSmokeA/B til gamle schema-1-data ved opstart; brug derfor altid testmappen. De to klienter tester H/K-køb, flere levels, separate spillere, død/respawn, genforbindelse, scroll/tastatur og forsinkede data. Testkommandoerne findes kun i smoke-kilderne og indgår ikke i mod-JAR'en.

Stat-effekttesten bruger `./gradlew runSmokeEffectsServer`, `./gradlew runSmokeEffectsA` og `./gradlew runSmokeEffectsB` i tre terminaler. Klargør en isoleret testverden `run/effects-server/EffectsSmoke`, accepteret EULA, server.properties bundet til `127.0.0.1:25568` i offline-mode og runtime-mods under hver af `run/effects-server`, `run/effects-a` og `run/effects-b`. Serveropgaven nulstiller kun testprofilerne PointsSmokeA/B med lave/høje stats og eksisterende perks. `./gradlew runSmokeEffectsSingleplayer` bruger i stedet `run/effects-sp/saves/EffectsSmoke` og mods under `run/effects-sp/mods`; denne opgave bevarer profilen ved genindlæsning. Brug kun testverdener. Scenarierne tester rigtige attributter, værktøjs-/potioninteraktioner, serverens regeneration, H/K, separat spillerdata, død og levende respawn, delvist helbred over 20 ved genindlæsning samt GUI-skalering. Testkommandoerne og testkilderne medfølger ikke i release-JAR'en.

Ressource-HUD-testen bruger `./gradlew runSmokeResourcesServer`, `./gradlew runSmokeResourcesA` og `./gradlew runSmokeResourcesB` i tre terminaler. Klargør en isoleret testverden `run/resources-server/ResourceSmoke`, accepteret EULA, server.properties bundet til `127.0.0.1:25569` i offline-mode og runtime-mods under hver af `run/resources-server`, `run/resources-a` og `run/resources-b`. Serveropgaven nulstiller kun testprofilerne ResourceSmokeA/B (level 10 og 100). `./gradlew runSmokeResourcesSingleplayer` bruger i stedet `run/resources-sp/saves/ResourceSmoke` og mods under `run/resources-sp/mods`. Singleplayer-opgaven bevarer profilen ved genindlæsning, så gentagne friske testforløb kræver en ny testprofil/testverden. Brug kun testverdener. Testene dækker rigtige serverhandlinger, ressourceopdateringer, F1, manglende snapshots, GUI-skalering, H/K, respawn og genindlæsning. Testkommandoer og testkilder indgår ikke i release-JAR'en.

Animationstesten bruger `./gradlew runSmokeAnimationsServer`, `./gradlew runSmokeAnimationsA` og `./gradlew runSmokeAnimationsB` med hver sin `run/animations-*`-mappe. Serveren skal have en isoleret `AnimationSmoke`-testverden, accepteret EULA, `127.0.0.1:25570`, offline-mode, `allow-flight=true`, difficulty easy og OP til AnimationSmokeA/B. Alle tre mapper skal have de sædvanlige runtime-mods i `mods/`. `./gradlew runSmokeAnimationsSingleplayer` bruger `run/animations-sp/saves/AnimationSmoke` med cheats aktiveret. Scenarierne erstatter testspillernes progression og klargør/skaber/fjerner testmobs, så brug kun testverdener. De tester alle 32 aktive evner, PAL-klip og partikler, Better Combat før/efter, afvist brug, afbrydelser, manglende klip, multiplayer-observation og genindlæsning. Testkoden og testkommandoerne indgår ikke i den almindelige JAR.

Leveling-testen bruger `./gradlew runSmokeLevelingServer`, `./gradlew runSmokeLevelingA` og `./gradlew runSmokeLevelingB`. Klargør de normale runtime-mods i `run/leveling-{server,a,b}/mods`, en isoleret verden `LevelSmoke`, EULA, server på `127.0.0.1:25571` i offline-mode med `allow-flight=true` og OP til LevelSmokeA/B. `./gradlew runSmokeLevelingSingleplayer` bruger `run/leveling-sp/saves/LevelSmoke` med cheats. Testene erstatter kun testprofilernes data; brug aldrig almindelige verdener. De kontrollerer normale XP-kilder ved level 7/8, H/K, flere levels, gammel level-100-data, 100-cifret XP, fire skalaer, vanilla-XP samt død/respawn og genindlæsning. Smoke-kode indgår ikke i mod-JAR’en.

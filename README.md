# star-project
to mods

## Star Swordmaster 0.3.5 downloads

- [Download the mod (.jar)](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.5/star-swordmaster-0.3.5.jar)
- [Checksums](releases/0.3.5/SHA256SUMS.txt)
- [Validation and limitations](VALIDATION.md)

Version **0.3.5** giver en rund skin-/levelmarkør og tre ressourcebjælker: **blå mana, rødt liv og grøn stamina**, alle med aktuelle værdier og maksimum. HUD'en tilpasser sig GUI-skalaen og bevarer evnebjælken, H/K, statbonusserne fra 0.3.4 og de separate pointtyper. Kræver Minecraft **1.21.10** og samme Fabric-/Better Combat-afhængigheder som nedenfor. Udskift den gamle Star Swordmaster-JAR; behold kun én version.

### Previous version 0.3.4

- [Download 0.3.4 (.jar)](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.4/star-swordmaster-0.3.4.jar)

### Previous version 0.3.3

- [Download 0.3.3 (.jar)](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.3/star-swordmaster-0.3.3.jar)

### Previous version 0.3.2

- [Download 0.3.2 (.jar)](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.2/star-swordmaster-0.3.2.jar)

### Previous version 0.3.1

- [Download 0.3.1 (.jar)](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.1/star-swordmaster-0.3.1.jar)

### Previous version 0.3.0

- [Download the mod (.jar)](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.0/star-swordmaster-0.3.0.jar)
- [Download the installation ZIP](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.0/star-swordmaster-0.3.0-install.zip)
- [Download the source ZIP](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.0/star-swordmaster-0.3.0-source.zip)
- [Validation and limitations](VALIDATION.md)

Selvstændig Fabric-mod til Minecraft **1.21.10**. Din egen karakter udvikler sig gennem sværdkamp og magi, med permanente klasser og et forgrenet færdighedstræ. Inspireret af Star-Embracing Swordmaster, Solo Leveling og Skyrim; dette er en første spilbar version med egne mekanikker og grafik, ikke en færdig genskabelse af historierne.

## Installation
1. Lav en Minecraft 1.21.10-installation med **Fabric Loader 0.19.5** og Java 21.
2. Installer disse Fabric-mods fra deres officielle sider:
   - [Fabric API 0.138.4+1.21.10](https://modrinth.com/mod/fabric-api)
   - [Better Combat 3.0.0+1.21.10](https://modrinth.com/mod/better-combat)
   - [Cloth Config 20.0.149](https://modrinth.com/mod/cloth-config)
   - [Player Animation Library 1.1.3](https://modrinth.com/mod/player-animation-library). Den kompatible fil hedder `PlayerAnimationLibFabric-1.1.3+mc.1.21.9.jar`; udgiverens metadata angiver også Minecraft 1.21.10.
3. Læg **star-swordmaster-0.3.5.jar** i installationens `mods`-mappe. Behold kun én version af Star Swordmaster.
4. Start Minecraft. I co-op skal alle klienter og serveren have samme version af modden og dens afhængigheder.

Brug gerne en separat Minecraft-profil til første afprøvning. Andre modpakker er ikke kompatibilitetstestet; Better Combat og afhængighederne ovenfor er testet sammen med denne mod.

## Kom i gang
- **K** åbner træet. Alle moddens taster kan ændres under Minecrafts Kontrol-indstillinger.
- **H** åbner den separate statusskærm med level/XP, klasse, rang, kampstil, stats, point, mana/stamina og lærte evner. Plusknapperne ved Styrke, Smidighed, Udholdenhed og Ånd bruger ét statpoint pr. klik. Du kan fordele point frit eller gemme resten. Korte effektbeskrivelser står ved hver stat; afkortet tekst kan læses ved at holde musen over den. Serveren godkender og gemmer hvert køb; tallene opdateres fra dens snapshots. At kigge på skærmen bruger ingen point. Luk med Escape eller Færdig.
- **Z, X, C, V, B** aktiverer evneplads 1–5. Bjælken ligger over den normale hotbar.
- Evnebjælken er **182 × 22 GUI-enheder**, ligesom vanilla-hotbaren, med **4 GUI-enheders afstand** over den. De fem pladser bruger vanilla-afstand på 20 og ikoner på 16 × 16; sideområderne er tomme.
- Ressourcepanelet har en rund skinmarkør med level og tre bjælker i rækkefølgen mana, liv og stamina. Panelet måler 176 × 40 GUI-enheder og ligger 8 enheder til venstre for evnebjælken, når der er plads. Ved en GUI-bredde under 562 enheder centreres det 4 enheder over evnebjælken. Alle positioner genberegnes ved skalering og ændring af vinduet.
- Tallene er faktiske værdier som `nuværende/maksimum`, afrundet til én decimal efter behov. Mana, stamina og level kommer fra moddens eksisterende serversnapshots; liv, maksimalt liv, absorption og statuseffekter kommer fra Minecrafts synkroniserede spillerobjekt. Absorption vises som et separat `+tal` og en gylden stribe. Livsikonet viser også gift, Wither og frysning, når relevant.
- Vanilla-hjerterne erstattes kun, mens det nye panel kan vises. Rustning, sult, luft, ridedyrshelbred, XP/locator og genstandsnavn bevares og løftes 44 GUI-enheder i sidelayoutet eller 76 i det stablede layout. Genstandsnavne og korte actionbar-beskeder løftes desuden efter vanillas aktuelle helbredsrækker, inklusive den midlertidige helbredsvisning efter en buff udløber. F1 skjuler både ressourcepanel og evnebjælke; spectator viser dem ikke. Mens spillerdata mangler, bruges den normale vanilla-visning. Minecrafts faktiske health, skade, heling og statuseffekter ændres ikke.
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

Evnebjælkens separate klienttest køres med `./gradlew runSmokeHud`. Den bruger testverdenen `HudSmoke` og spillernavnet `StatusSmoke` med Gnister, Frostgreb, Stjernelys og Lægende lys tildelt plads 1–4; plads 5 er tom. Testen beskytter testspilleren og sætter helbredet fire point under maksimum for at afprøve den eksisterende Lægende lys-cooldown, og gemmer billeder i `run/screenshots/`. Brug kun en lokal testverden til dette scenarie. Testverdenen og testklasserne indgår ikke i mod-JAR'en.

Pointtesten bruger tre separate terminaler: `./gradlew runSmokePointsServer`, `./gradlew runSmokePointsA` og `./gradlew runSmokePointsB`. Klargør en lokal Minecraft-testverden `run/points-server/PointsSmoke`, serverens EULA/server.properties (offline, bundet til 127.0.0.1:25567) og de sædvanlige runtime-mods i `mods` under hver af `run/points-server`, `run/points-a` og `run/points-b`. Serveropgaven nulstiller kun testprofilerne PointsSmokeA/B til gamle schema-1-data ved opstart; brug derfor altid testmappen. De to klienter tester H/K-køb, flere levels, separate spillere, død/respawn, genforbindelse, scroll/tastatur og forsinkede data. Testkommandoerne findes kun i smoke-kilderne og indgår ikke i mod-JAR'en.

Stat-effekttesten bruger `./gradlew runSmokeEffectsServer`, `./gradlew runSmokeEffectsA` og `./gradlew runSmokeEffectsB` i tre terminaler. Klargør en isoleret testverden `run/effects-server/EffectsSmoke`, accepteret EULA, server.properties bundet til `127.0.0.1:25568` i offline-mode og runtime-mods under hver af `run/effects-server`, `run/effects-a` og `run/effects-b`. Serveropgaven nulstiller kun testprofilerne PointsSmokeA/B med lave/høje stats og eksisterende perks. `./gradlew runSmokeEffectsSingleplayer` bruger i stedet `run/effects-sp/saves/EffectsSmoke` og mods under `run/effects-sp/mods`; denne opgave bevarer profilen ved genindlæsning. Brug kun testverdener. Scenarierne tester rigtige attributter, værktøjs-/potioninteraktioner, serverens regeneration, H/K, separat spillerdata, død og levende respawn, delvist helbred over 20 ved genindlæsning samt GUI-skalering. Testkommandoerne og testkilderne medfølger ikke i release-JAR'en.

Ressource-HUD-testen bruger `./gradlew runSmokeResourcesServer`, `./gradlew runSmokeResourcesA` og `./gradlew runSmokeResourcesB` i tre terminaler. Klargør en isoleret testverden `run/resources-server/ResourceSmoke`, accepteret EULA, server.properties bundet til `127.0.0.1:25569` i offline-mode og runtime-mods under hver af `run/resources-server`, `run/resources-a` og `run/resources-b`. Serveropgaven nulstiller kun testprofilerne ResourceSmokeA/B (level 10 og 100). `./gradlew runSmokeResourcesSingleplayer` bruger i stedet `run/resources-sp/saves/ResourceSmoke` og mods under `run/resources-sp/mods`. Singleplayer-opgaven bevarer profilen ved genindlæsning, så gentagne friske testforløb kræver en ny testprofil/testverden. Brug kun testverdener. Testene dækker rigtige serverhandlinger, ressourceopdateringer, F1, manglende snapshots, GUI-skalering, H/K, respawn og genindlæsning. Testkommandoer og testkilder indgår ikke i release-JAR'en.

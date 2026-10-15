# star-project
to mods

## Star Swordmaster 0.3.2 downloads

- [Download the mod (.jar)](https://github.com/lilmigger63-hue/star-project/raw/refs/heads/main/releases/0.3.2/star-swordmaster-0.3.2.jar)
- [Checksums](releases/0.3.2/SHA256SUMS.txt)
- [Validation and limitations](VALIDATION.md)

Version **0.3.2** matches the skill bar to the vanilla hotbar's size and centers it immediately above it. The separate status screen remains on **H**; **K** still opens the skill tree. Requires Minecraft **1.21.10** and the same Fabric/Better Combat dependencies listed below. Replace the previous Star Swordmaster JAR; keep only one version installed.

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
3. Læg **star-swordmaster-0.3.2.jar** i installationens `mods`-mappe. Behold kun én version af Star Swordmaster.
4. Start Minecraft. I co-op skal alle klienter og serveren have samme version af modden og dens afhængigheder.

Brug gerne en separat Minecraft-profil til første afprøvning. Andre modpakker er ikke kompatibilitetstestet; Better Combat og afhængighederne ovenfor er testet sammen med denne mod.

## Kom i gang
- **K** åbner træet. Alle moddens taster kan ændres under Minecrafts Kontrol-indstillinger.
- **H** åbner den separate statusskærm. Den viser karakterens level/XP, klasse, rang, kampstil, stats, point, mana/stamina og lærte aktive/passive evner. Værdierne opdateres fra serverens eksisterende snapshots; visningen bruger ingen point og ændrer ikke progression. Luk med Escape eller Færdig.
- **Z, X, C, V, B** aktiverer evneplads 1–5. Bjælken ligger over den normale hotbar.
- Evnebjælken er **182 × 22 GUI-enheder**, ligesom vanilla-hotbaren, med **4 GUI-enheders afstand** over den. De fem pladser bruger vanilla-afstand på 20 og ikoner på 16 × 16; sideområderne er tomme. Placeringen genberegnes fra det aktuelle GUI-mål ved skalering og ændring af vinduet. Mana/stamina og level vises lige over evnebjælken. Hjerter, rustning, sult, luft, ridedyrshelbred, XP/locator og genstandsnavn flyttes 44 GUI-enheder op, mens evnebjælken vises, så indikatorerne forbliver synlige.
- Start som Uindviet. Slå eksisterende fjendtlige mobs med et sværd for at træne sværdfærdigheden. Der gives ikke erfaring for at slå i luften.
- Færdigheder forbedres gennem brug. Når en færdighed stiger, får karakteren XP; karakterlevels giver perkpoint og statpoint. Dette er separat fra Minecrafts almindelige XP.
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

Styrke øger aktive evners skade, Smidighed mindsker cooldowns, Udholdenhed øger stamina og Ånd øger mana. Rangperks giver ekstra ressourcer; Mester-rang giver yderligere evneskade.

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

## Validering og begrænsninger
Bygningen, automatiske progression-/persistens-/viewporttests og rigtig Fabric-opstart med Better Combat er verificeret. En lokal Minecraft-klient bruges også til at teste klassevalg, rang, evnetildeling, evner og heling mod en rigtig server. Se `VALIDATION.md` for endelige resultater.
Grafikken er original, foreløbig pixelgrafik. Balanceværdier og XP-kurver er fastlagt i kildekoden i denne version. Lydafspilning kunne ikke verificeres i cloudmiljøet. Der er endnu ingen historiequests eller NPC'er fra universet.

Engelske evnenavne findes, men effektbeskrivelserne er foreløbig på dansk.

## Byg fra kildekoden
Brug Java 21. På Linux/macOS: `./gradlew build`. På Windows: `gradlew.bat build`.
JAR'en findes derefter i `build/libs/`. Installer den almindelige JAR, ikke `-sources.jar` eller lokale smoke-testfiler. De særskilte smoke-testkilder inkluderes ikke i den almindelige mod-JAR.

Statusskærmen tilføjer ingen afhængigheder og bevarer gemmeformatet. Dens model- og layouttests køres af `build`. Den separate klienttest kan køres med `./gradlew runSmokeStatus`, når en lokal testverden `StatusSmoke` og de normale runtime-afhængigheder er klargjort; scenariet forventer en testkarakter med level 10, 1355 XP, fire aktive evner og én passiv evne.

Evnebjælkens separate klienttest køres med `./gradlew runSmokeHud`. Den bruger testverdenen `HudSmoke` og spillernavnet `StatusSmoke` med Gnister, Frostgreb, Stjernelys og Lægende lys tildelt plads 1–4; plads 5 er tom. Testen beskytter testspilleren og sætter helbredet fire point under maksimum for at afprøve den eksisterende Lægende lys-cooldown, og gemmer billeder i `run/screenshots/`. Brug kun en lokal testverden til dette scenarie. Testverdenen og testklasserne indgår ikke i mod-JAR'en.

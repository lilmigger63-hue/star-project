# Validering — Star Swordmaster 0.3.0

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

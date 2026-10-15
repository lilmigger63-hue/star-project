# Star Swordmaster — første spilbare version

## Formål og aftalt scope
En selvstændig Minecraft-mod til Minecraft 1.21.10 og Fabric 0.19.5 med Better Combat. Spilleren er sin egen karakter i et univers inspireret af Star-Embracing Swordmaster. Progressionen kombinerer permanente klasser og rang med færdigheder, der udvikles gennem brug, inspireret af Skyrim og Solo Leveling. Singleplayer prioriteres; progression fungerer også individuelt i co-op. Ingen nye mobs eller bosser. Egne grafiske assets; wikiers konkrete lore er endnu ikke undersøgt.

## Progression
Spilleren starter på level 1 som Uindviet. Færdigheder optjener erfaring ved relevante handlinger. Færdighedsstigninger giver karakter-XP, og hvert karakterlevel giver ét perkpoint og ét statpoint. Almindelig Minecraft-XP forbliver separat.
Ved level 10 bliver et permanent valg mellem Sværdkæmper, Magiker og Magisk sværdkæmper tilgængeligt. Spilleren kan udsætte valget. En bekræftelsesdialog forklarer, at valget ikke kan nulstilles gennem normal gameplay. Progression er knyttet til spilleren i den enkelte verden, gemmes og bevares ved død.
Alle kan lære simpel magi og grundlæggende sværdstile. Klassevalg åbner avancerede evner. Rangstigninger kræver oplåsning af bestemte evner, ikke blot karakterlevel.

## Forslag til første versions indhold
Disse navne og balancevalg er forslag til review, ikke hidtil aftalte lore-detaljer.
Tre sværdstile: Hurtig (mobilitet og hurtige teknikker), Tung (kraftige slag), Defensiv (parering og beskyttelse). Spilleren kan lære flere stile og vælge en aktiv stil i træets menu. Stilen ændrer bonusser og tilgængelige stilteknikker; almindelige angrebsanimationer følger Better Combat og våbnet.
Tre magigrene: Ild, Frost og Støtte. Fælles begynderevner: lille ildkugle, kort frostangreb, lys og svag selvheling. Avancerede magikere får større områdeevner og stærkere beskyttelse. Magiske sværdkæmpere får elementforstærkning og kombinationsteknikker. Sværdkæmpere får avancerede stilteknikker og aura.
Første version har mindst 18 aktive evner: fire fælles besværgelser, to grundteknikker per sværdstil, samt mindst to eksklusive aktive evner per hovedklasse og to avancerede magiske evner. Passive perks forbinder og udbygger grenene.
Tre ranger efter klassevalg: Lærling, Adept og Mester. Adept kræver henholdsvis Auraopvågning, Manakontrol eller Elementbinding; Mester kræver en senere klasseeksklusiv mestringsperk. Rangperks kræver tidligere perks og dokumenteret færdighedsniveau. Konkrete XP-kurver og omkostninger ligger i justerbar konfiguration.
Stats: styrke, smidighed, udholdenhed og ånd. Hver stat har en begrænset, dokumenteret effekt på skade, mobilitet, stamina eller mana; bonusser må ikke ændre Better Combats angrebsforløb på en måde, der bryder integrationen.

## Ressourcer og evner
Magi koster mana. Aktive sværdteknikker koster mana og lidt stamina. Almindelige angreb er gratis. Begge ressourcer regenererer automatisk, langsommere i kamp. Kampstatus varer som standard otte sekunder efter skade givet eller modtaget.
Hver aktiv evne definerer ressourcepris, cooldown, rækkevidde, måltype og effekt. Afvist brug forbruger ingen ressourcer. Gentagne ugyldige forsøg giver ikke XP. Færdigheds-XP fra kamp kræver en gyldig fjende og faktisk effekt; støtteevner kræver en reel effekt og begrænset XP-frekvens.

## HUD og færdighedstræ — godkendt retning
Vanilla-hotbar nederst; præcis fem aktive evnepladser lige over den. Mana og stamina vises ved evnebjælken uden at skjule vanilla-information. Hver plads viser ikon, brugerens tast, cooldown og utilstrækkelige ressourcer. Hver plads har sin egen omkonfigurerbare Minecraft-keybinding; foreslåede standardtaster er Z, X, C, V og B. Træet åbnes som standard med K, også omkonfigurerbart.
Træet er en stor graf med små ikoner og forbindelseslinjer på en original mørk baggrund, inspireret af brugerens screenshot. Musetræk flytter udsnittet; scrolling zoomer omkring markøren. Separate grene viser sværdstile, aura og magi. Låste noder er dæmpede, tilgængelige tydelige og lærte lysende. Alle grene er synlige, men permanent klasseudelukkede noder markeres tydeligt.
Ved klik åbnes detaljepanel med effekt, krav, perkpris og ressourceforbrug. Oplåsning og tildeling til plads 1–5 er separate handlinger. Passive evner kan ikke tildeles. En evne kan kun ligge i én plads ad gangen. Klassens rang og spillerens level fremgår af menuen.

## Teknik og datamodel
Java 21, Fabric API og Better Combat. Præcise kompatible dependency-versioner og buildværktøj verificeres før implementering; de opfindes ikke ud fra modpakkens manifest. Modden installeres hos klient og server i co-op.
Serveren ejer level, stats, klasse, rang, færdigheds-XP, perks, mana, stamina, aktiv stil, slots og cooldowns. Klienten viser synkroniserede data og sender kun ønsker om valg eller aktivering. Serveren validerer alle krav, mål, priser og cooldowns. Data gemmes med schema-version og sikre standardværdier ved manglende felter.
Separate komponenter til progression, perkdefinitioner, klasse/rang, ressourcer, evneeffekter, persistens/netværk og klient-GUI. Evne- og perkdefinitioner har stabile ID'er. Better Combat bruges til almindelige sværdangreb; integrationen verificeres uden at ændre den eksisterende Murim-modpakke.
Spillerdata synkroniseres ved login, genoplivning og ændringer. Flere spilleres data må aldrig deles. Ugyldige eller forældede klientanmodninger afvises og efterfølges af opdateret tilstand. Evner retter sig som standard mod fjendtlige mobs; co-op-spillere skades ikke af nye områdeevner.

## Validering og levering
Byg en selvstændig installérbar JAR med reproducerbar build og eget kildekodeprojekt. Test meningsfuldt: permanent klassevalg ved level 10, perkkrav, rangkrav, ressourceforbrug, regeneration, cooldowns, tildeling af fem pladser, persistens og afvisning af ugyldige anmodninger. Verificér faktisk Fabric-opstart og Better Combat-kompatibilitet i det omfang miljøet tillader.
GUI-layout, tastkonflikter, animationer og kampspil testes i Minecraft-klienten; hvis cloudmiljøet ikke kan køre grafisk klient, markeres disse checks som utestede og leveres som konkret lokal testvejledning. En bestået build må ikke beskrives som en bestået spiltest. Lever JAR, afhængighedsliste og installationsvejledning.

## Uden for første version
Nye mobs, bosser, historiequests, nye dimensioner, kopierede grafikassets og integration med den eksisterende Murim-mod. Fuld wiki-tro genskabelse er ikke lovet. Første version er et spilbart progressionssystem, som kan udvides senere.

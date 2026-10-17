# Ultimate magic og sværdauraer — 0.5.0

Træet har **85 noder: 54 aktive og 31 passive**. Versionen tilføjer 16 elementbesværgelser, seks sværdteknikker og `aura_sense`. Alle tidligere evne-ID'er, klasser, fire rangtrin, effektive stats, point, XP-regler, slots og HUD-positioner bevares. Ingen nye afhængigheder eller mobs; schema 1 bevares. De fire nye færdigheder bruger de eksisterende `practice.<SKILL>`-felter og starter på nul praksis i gamle verdener. Auraen tilføjer intet gemmefelt.

## Nye aktive evner

Alle koster ét perkpoint at lære. Mana/stamina og cooldown nedenfor er grundværdier; eksisterende stats, klasse, rang og stil anvendes som før. Detaljer og forældrekrav vises på K. Rækkevidde og skader er serverberegnede; animationer ændrer dem ikke.

| ID | Klasse | Karakter / færdighedslevel | Mana / stamina | Cooldown, ticks | Faktisk effekt før eksisterende bonusser |
| --- | --- | --- | --- | --- | --- |
| static_spark | Alle | 1 / Lyn 1 | 14 / 0 | 50 | 3 skade, mål inden for 16 |
| chain_lightning | Alle | 10 / Lyn 5 | 30 / 0 | 140 | 7 skade, højst 5 mål; ×0,8 pr. spring, højst 5 mellem mål |
| thunder_step | Magisk sværd | 10 / Lyn 8 | 22 / 6 | 160 | Kort bevægelsesimpuls, Speed/Resistance i 40 ticks |
| heavens_wrath | Magiker | 25 / Lyn 20 | 65 / 0 | 500 | 10 skade og sløvning omkring sigtet mål, radius 5 |
| water_bolt | Alle | 1 / Vand 1 | 14 / 0 | 50 | 4 skade, mål inden for 16 |
| tidal_wave | Alle | 10 / Vand 5 | 30 / 0 | 160 | 5 skade og knockback i en fremadrettet vifte, rækkevidde 8 |
| aqua_blade | Magisk sværd | 10 / Vand 8 | 26 / 6 | 140 | 7 skade, mål inden for 4 |
| ocean_prison | Magiker | 20 / Vand 15 | 40 / 0 | 240 | 6 skade og Slowness IV i 100 ticks |
| stone_spike | Alle | 1 / Jord 1 | 16 / 0 | 60 | 5 skade, mål inden for 16 |
| earthen_wall | Alle | 10 / Jord 5 | 24 / 0 | 220 | Resistance i 100 og absorption i 80 ticks; placerer ingen blokke |
| quake_blade | Sværdkæmper / Magisk sværd | 15 / Jord 10 | 34 / 10 | 180 | 8 skade og sløvning, radius 4 |
| worldbreaker | Magiker | 25 / Jord 20 | 70 / 0 | 600 | 14 skade og sløvning, radius 6 |
| shadow_bolt | Alle | 1 / Mørke 1 | 16 / 0 | 60 | 5 skade, mål inden for 16 |
| curse_mark | Alle | 10 / Mørke 5 | 24 / 0 | 140 | 3 skade og Weakness i 100 ticks |
| shadow_edge | Magisk sværd | 10 / Mørke 8 | 28 / 6 | 160 | 8 skade, mål inden for 4 |
| eclipse_nova | Magiker | 25 / Mørke 20 | 70 / 0 | 600 | 13 skade og Weakness, radius 6 |
| astral_crescent | Sværdkæmper | 12 / Sværd 8 | 24 / 7 | 100 | 8 skade i fremadrettet sværdvifte, rækkevidde 5 |
| falling_star | Sværdkæmper | 18 / Sværd 12 | 34 / 10 | 180 | 11 skade og sløvning, radius 4 |
| phantom_blades | Sværdkæmper | 20 / Sværd 15 | 32 / 9 | 160 | 10 skade i ét træf; bladefterbilleder er kosmetiske |
| celestial_rush | Sværdkæmper | 18 / Sværd 12 | 28 / 8 | 160 | 8 skade i sværdvifte og kort fremdrift ved gyldigt mål |
| sovereign_judgment | Sværdkæmper | 30 / Sværd 25 | 50 / 14 | 320 | 16 skade inden for 5; kræver rang 4 |
| heavenly_sword_domain | Sværdkæmper | 40 / Sværd 30 | 80 / 18 | 700 | 15 skade og Weakness, radius 6; kræver Sovereign Judgment |

Områder rammer højst otte fjendtlige mobs. Spillere, kæledyr, holdallierede og mål uden synslinje udelukkes. Kædelyn rammer hvert mål højst én gang. Magiske spor er kosmetiske; der skabes ingen ekstra gameplay-projektiler, blokødelæggelse, ild eller eksplosionsskade. Afvist brug bruger ingen ressourcer og starter ingen præsentation.

## Personlig sværdaura

`aura_sense` kræver Lynsnit, karakterlevel 10 og Sværd 3. Den er en passiv node, ikke en sjette evneplads. Gamle spillere med Auraopvågning eller Elementbinding får auraen automatisk; nye magiske sværdteknikker kan også åbne den.

Serveren udleder en lys, mættet farve fra spillerens UUID med hele HSV-farvecirklen: mætning 72–94 %, lysstyrke 90–100 %. Ingen per-frame tilfældighed og intet nyt gemmefelt. Farven består ved død, genforbindelse og verdensgenstart. Rang øger bredde, lys, facetter og klingespor; rang 4 tilføjer hvide højlys og en stjernekrone. Sværdkæmperens envelope er 22 % bredere; Magisk sværd kan få en kort sekundær elementaccent, mens grundfarven bevares.

Auraen deler Minecrafts faktiske held-item-transform i første-/tredjeperson, venstre-/højrehånd og hos andre spillere. Den følger derfor også Better Combat og PAL. Kun sværd i hænder vises; ingen aura på GUI-ikoner, droppede genstande eller efter våbenskift. Depth test bevares.

## Visuel skalering og ydelse

For karakterlevel L bruges p=(L−1)/(L+39), begrænset til 0–1 og med sikker behandling af vilkårligt store BigInteger-levels. Visuel radius skalerer fra 0,65 til 2,25; sprites fra 0,7 til 2,5; sporlængde fra 0,75 til 2,5. Armbevægelsens bidrag skalerer fra 75 til 100 % med samme animationsvarighed, sigte og vanilla-ben. Hårde grænser: 12 blokkes lokal visuel radius, 3 blokkes spritebredde, 256 nye partikler/tick, 64 aktive kastere og 64 blokkes visningsafstand. Klingeauraen bruger højst 64 quads pr. hånd og 1024 pr. frame i alt, højst 64 spillere; afstand og kvalitet reducerer detaljer yderligere.

H har et diskret VFX-valg: Low / Normal / Ultra. Det gemmes kun lokalt i `config/starswordmaster-vfx.txt`. Minecrafts partikelindstillinger respekteres også. Ingen kvalitetsindstilling hæver de hårde grænser.

Serveren sender kompakt appearance-state ved tracking, ændringer, join, respawn og dimensionsskift. Gyldige kast indeholder power/farve og højst otte faktiske træfpositioner; klienten tegner selv mønstrene. Brug **0.5.0 på både server og klienter**, da præsentationspayloaden er versioneret.

Se [ABILITY_PRESENTATION.md](ABILITY_PRESENTATION.md) for alle 54 animationer og [VALIDATION.md](VALIDATION.md) for udførte tests.

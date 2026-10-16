# Fjerde klasserang — 0.4.1

Færdighedstræet har **62 noder: 32 aktive evner og 30 passive noder**. Rang 4 låses op på K, koster ét perkpoint og gælder permanent for den valgte klasse. Den kan ikke tildeles en evneplads. Ingen nye kast eller animationer tilføjes.

| Klasse | Rang 4 / ID | Forudsætning | Karakter / færdighed | Effektive stats | Ekstra skade |
| --- | --- | --- | --- | --- | --- |
| Sværdkæmper | Astralsværdets Suveræn / `astral_sword_sovereign` | `sword_mastery` | Level 25 / Sword 20 | +3 Styrke, +2 Smidighed | +5% sværdevneskade |
| Magiker | Den Himmelske Arkon / `celestial_archon` | `arcane_mastery` | Level 25 / Fire 20 | +4 Ånd, +1 Udholdenhed | +5% offensiv magisk skade |
| Magisk sværdkæmper | Formørkelsens Suveræn / `eclipse_sovereign` | `spellblade_mastery` | Level 25 / Sword 20 | +2 Styrke, +2 Ånd, +1 Smidighed | +5% fortryllet sværdevneskade |

De engelske titler er **Astral Sword Sovereign**, **Celestial Archon** og **Eclipse Sovereign**. Rangnoderne forbindes direkte til deres Mastery-node med over 200 layoutenheders afstand. Træets oprindelige pan/zoom og hit-test genbruges; startudsnittet beregnes fra samtlige noders positioner. Én eksisterende træningsnode flyttes lidt for at give plads til sin nabo.

## Bonusberegning og gemmedata

`stat()` er fortsat den fordelte grundværdi. `effectiveStat()` beregner grundværdi plus lærte passive bonusser og rangbonus; ingen bonus skrives ind i fordelte stats eller giver statpoint. Effektive stats bruger `long`, så en grundværdi ved `Integer.MAX_VALUE` ikke løber over. H viser effektiv værdi og `(+passiv bonus)` med en forklaring af grundværdi/passiv i tooltip samt rangtitel og kategoriens +5% skade.

Alle hidtidige statregler og grænser bruges også på effektive stats. Mastery bevarer sin eksisterende +0,15 i skademultiplikatoren, også ved rang 4. Den nye kategori-bonus multipliceres bagefter med **1,05**, sammen med eksisterende stil-/træningsbonusser:

`(1 + 0,025 × effektiv Styrke + Mastery-bonus) × eksisterende stilbonus × kategoribonus`

- Sværdskade: Lynsnit, Kraftslag, Stormfejning, Modhug, Aurabølge, Lynpres, Knusende klinge, Gennemborende stød og Jordrystende hug. Fortryllede sværdangreb tæller også som sværd, hvis kategorien undersøges.
- Offensiv magi: Gnister, Frostgreb, Flammeudbrud, Islanse, Frostnova, Flammelanse, Glødering og Frostlænker.
- Fortryllede sværdangreb: Flammesværd, Frostsværd og Gletsjerfejning.

Heling, dash, defensive effekter, almindelige Better Combat-angreb og vanillas efterfølgende brandskade får ingen ekstra kategori-bonus. Eksisterende styrkebonus ændrer fortsat evneskade som før. Skadesberegningen bruges af den samme serverautoritative `AbilityEffects`-kode; rækkevidde, priser, cooldown, målvalg og timing ændres ikke.

Serverens `unlock()` validerer klasse, karakterlevel, færdighedslevel, Mastery og perkpoint. Klientens knapper bruger samme `canUnlock()`-regler. Det eksisterende save-/snapshotformat, pointregnskab og indlæsning bevares. Rang afledes af lærte noder; Minecraft-attributter bruger fortsat de samme modifier-ID'er, så genindlæsning, gentagne ticks og genforbindelse ikke kan stable bonusser.

## 15 valgfrie træningsnoder

Fem grene med tre trin hver, valgt for at udvide det eksisterende træ fra 44 til 62 noder. Hvert trin kræver det foregående, færdighedslevel **5 / 10 / 15**, koster ét perkpoint og giver **+1 effektiv stat**. De er åbne for alle klasser og er ikke ekstra krav til rang 4.

| Gren / ID-præfiks | Første forudsætning | Færdighed | Stat pr. trin |
| --- | --- | --- | --- |
| Klingetræning / `strength_training` | `impact_training` | Sword | +1 Styrke |
| Flydetræning / `agility_training` | `flow_training` | Sword | +1 Smidighed |
| Vogtertræning / `endurance_training` | `guard_training` | Sword | +1 Udholdenhed |
| Glødemeditation / `spirit_training` | `fire_affinity` | Fire | +1 Ånd |
| Livsmeditation / `restorative_training` | `restorative_affinity` | Support | +1 Ånd |

Hver node har dansk/engelsk tekst og et originalt 32×32-ikon med skarp pixelgrafik. `tools/generate_rank_assets.py` genskaber de 18 nye ikoner og oversættelser. De tre rangikoner viser henholdsvis et gyldent stjernesværd, en lysende lilla kosmisk rune og en mørklilla formørkelse omkring en fortryllet klinge. Testkilder pakkes ikke i mod-JAR'en. Faktiske kontroller findes i [VALIDATION.md](VALIDATION.md).

# Leveling og hotbar — 0.3.7

Karakterlevel har ingen fast maksimalværdi. XP, level og beholdninger af stat-/perkpoint bruger `BigInteger`; level beregnes med en heltalskvadratrod, så meget store XP-belønninger hverken løber over eller kræver en løkke gennem hvert level.

XP-kravet fra level L til L+1 er uændret: **50 + 20 × L**. Den samlede kurve ved begyndelsen af L er **10 × (L−1) × (L+5)**.

## XP-belønning og point

- Er karakteren level 1–7 **før** en belønning, gives den normale mod-XP.
- Er karakteren allerede level 8 eller højere, gives **normal belønning × 1,5**. Kravet til næste level multipliceres ikke.
- En belønning ved level 7, der krydser level 8, gives fuldt ud med den gamle faktor. Den næste belønning bruger ×1,5.
- Halve XP gemmes mellem belønninger og genindlæsninger; der afrundes ikke gentagne gange med tab. De eksisterende normale belønninger på 100 XP bliver præcis 150.
- Hvert nyt level giver stadig **5 statpoint til H og 1 perkpoint til K**. Tilgængelige point udledes af level minus allerede købte stats/evner; flere levels på én gang, snapshots, død og genindlæsning giver ikke dobbelt belønning.
- XP kommer stadig fra de eksisterende træningshændelser og milepæle. Træningstallene kan nu fortsætte efter færdighedslevel 100, så karakteren faktisk kan blive ved med at få XP. Færdighedstræets krav, evner og viste færdighedslevel 1–100 ændres ikke. Ingen ændringer i Minecrafts almindelige XP.

## Eksisterende gemmedata

De samme schema-1-properties og atomiske lagring bruges. Nye, valgfrie felter er `xpCurveVersion=2`, `xpLegacyOffset` og `xpHalf`; `xp` beholder hele den eksisterende total. Stat-/perkpoint gemmes fortsat ikke som en ekstra belønning, der kan genudbetales.

Gamle karakterer under level 100 indlæses på præcis samme level og samme XP-fremskridt. En gammel karakter på level 100 kunne have 200.000 total XP, selv om level 100 begynder ved 103.950. Den gamle mod viste alligevel level 100 og nul fremskridt. Migrationen beholder **både level 100, alle 200.000 XP, allerede fordelte stats og de gamle pointbeholdninger**. Det gamle overskud registreres som en fast historisk forskydning; det giver ikke retroaktive levels eller ekstra point. Nye belønninger fortsætter kurven fra level 100.

Eksempel: gammel level 100 / 200.000 XP + normal belønning på 1.400 → faktisk belønning 2.100 → level 101, 202.100 total XP og 50/2.070 XP til næste level. Der gives kun 5 nye statpoint og 1 nyt perkpoint. Gentagen indlæsning gentager ikke migrationen.

Eksisterende mana, stamina, liv, skader, klasse-/rang-/stilbonusser og fordelte stats bevares ved opdatering. Nye levels bruger de eksisterende ressourceformler; der er ingen rebalance. Bonusmultiplikationer er udvidet til `long`, og et statkøb kan ikke ombryde den eksisterende heltalstype eller opbruge et point uden at øge statten. Meget store ressourceværdier bruger kompakt notation i de samme HUD-felter; alle almindelige tal vises som før. H viser de fulde XP-/level-/pointtal med eksisterende tooltips, når teksten er bredere end feltet.

Installer samme nye version på klient og server. Behold en kopi af verdenen ved opdatering; ældre modversioner forstår ikke de nye store XP-tal og må ikke bruges til at skrive opdaterede data tilbage.

## HUD-geometri i 0.3.7

Placeringerne nedenfor beskriver 0.3.7. Se [HUD_LAYOUT.md](HUD_LAYOUT.md) for den nye venstre forankring og kompakte XP-/sultgruppe i 0.3.8; XP-reglen og migrationen ovenfor er uændrede.

Evnebjælken er stadig **182 × 22 GUI-enheder**, med de samme fem slots, ikoner, rækkefølge og **Z/X/C/V/B**. Begge bjælker er centreret på `GUI-bredde / 2`.

Minecraft 1.21.10 placerer hotbarens baggrund ved `GUI-højde−22`, men den valgte slots yderkant og offhand-rammen begynder ved **GUI-højde−23**. Evnebjælken slutter præcis dér: **0 GUI-enheders afstand til den faktiske ydre hotbar-kant**, uden at skjule markeringen eller flytte vanilla-hotbaren. Den forskydes derfor tre GUI-enheder ned fra 0.3.6.

Ressourceportrættets position bevares: top `h−66` ved sidelayout og `h−92` ved stablet layout. XP, sult, rustning, luft, locator og øvrige eksisterende native forskydninger forbliver 44/76 GUI-enheder som før. H og K bevares.

Se [VALIDATION.md](VALIDATION.md) for bygge- og spiltests.

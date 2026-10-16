# HUD-layout — 0.3.8

Ressourcepanelet bruger stadig samme 176 × 40 GUI-enheder, rund skin-/levelmarkør og faktiske serversynkroniserede værdier. Rækkefølgen er blå mana, rødt liv og grøn stamina. Ikoner, farver, absorption og cooldown-visning er uændrede.

## Ankre og kompakt vanilla-gruppe

Alle koordinater beregnes fra vinduets aktuelle **skalerede GUI-bredde og -højde**, ikke fysiske skærmpixel. Her er `h` GUI-højden:

- Ressourcepanelet forankres **6 GUI-enheder fra venstre kant**. I brede vinduer ligger det ved `y = h−66` (26 enheder fra bunden).
- Evnebjælken forbliver **182 × 22**, centreret ved `y = h−45`. Dens nederste kant er `h−23`, præcis vanilla-hotbarens faktiske yderkant inklusive markeringen af valgt slot. **Afstand 0, intet overlap.** Fem slots og Z/X/C/V/B ændres ikke.
- Den eksisterende native XP-/sult-/rustnings-/luft-/ridedyrsgruppe flyttes samlet **29 GUI-enheder op fra vanilla**. XP-bjælken ligger ved `h−58`, er 5 enheder høj og har 8 enheders afstand til evnebjælken. Sult ligger over XP-bjælken; den normale én-enhedsafstand tillader også vanillas sult-rystelse. XP-leveltekst, locator og ride-hopbjælke bruger fortsat Minecrafts egne renderere og valg af bjælketype. Locator-markørerne flyttes med via den eksisterende mixin.

Fra GUI-bredde **562** er der mindst 8 enheder mellem ressourcepanelet og den centrerede bjælkegruppe. Under grænsen flyttes ressourcerne op, stadig ved venstre kant, med 4 enheders afstand over rustning/luft. Panelets normale smalle position er `h−132`; den tilpasses ekstra native helbredsrækker, absorption, kortvarigt bevaret helbred og luftbobler over ridedyr med op til tre hjerterækker. Hotbaren og evnebjælken flyttes ikke ved denne ombrydning.

## Chat og genstandsnavne

Genstandsnavne og actionbar-tekst placeres over den reserverede HUD-plads. Vanillas ekstra forskydning af genstandsnavne i creative-mode kompenseres særskilt.

Chatbeskeder får en højere bundlinje, så både lukket og åben chat ligger over ressourcerne og de native tekster. Chatvisningens højde begrænses til den resterende skærmplads; ældre linjer kan stadig scrolles. Renderkoordinater, teksternes klik-/hoverområder og knappen for ventende beskeder bruger samme forskydning. Chatskalering og linjeafstand bevares. Det normale skrivefelt og beskederne ændres ikke.

## Funktioner og kompatibilitet

F1, spectator og manglende snapshots bevarer de tidligere synlighedsregler. Når erstatnings-HUD'en er utilgængelig, bruger chat og native indikatorer deres oprindelige placeringer; vanilla-hjerter kan vises igen. Forsinkede snapshots gendanner automatisk mod-HUD'en.

Ingen ændringer i leveling, ×1,5-XP-reglen fra level 8, stat-/perkpoint, ressourcer, evner, gemmedata eller netværk. H og K bevares. Ingen nye afhængigheder. Se [VALIDATION.md](VALIDATION.md) for bygge- og spiltests.

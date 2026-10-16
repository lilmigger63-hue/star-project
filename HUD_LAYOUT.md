# HUD-layout — 0.3.9

Ressourcepanelet bevarer sine 176 × 40 GUI-enheder, hoved/levelmarkør og blå mana/rødt liv/grøn stamina med faktiske synkroniserede værdier. Evnebjælken ligger nu under stamina; vanilla-hotbaren er fortsat centreret nederst.

## Evnebjælken

- Hele den eksisterende 182 × 22-bjælke tegnes med én **0,75 × 0,75-transformation**: præcis **136,5 × 16,5 GUI-enheder**. Der afrundes ikke til en 137 × 17-model.
- De fem slots, rammer, tomme sideområder, ikoner, cooldown-overlays og labels skaleres sammen. Ikoner er 12 × 12 og slotafstanden 15 GUI-enheder. Pixelgrafikken bruger de samme native nærmeste-nabo-teksturer som før; der tilføjes ingen udglatning eller nye assets.
- Bjælkens venstrekant er ressourcebjælkernes venstrekant (`resource.barX()`), og toppen er **2 GUI-enheder under stamina-bjælkens nederste kant**.
- Z/X/C/V/B, rækkefølge, indhold, cooldowns og aktivering er uændrede. HUD'en havde ingen musebaseret slotvælger; tildeling i K bevarer sine eksisterende klikområder og størrelse. Skaleringen er begrænset til gameplay-bjælken og påvirker ikke ikonerne i H/K.

## Responsive ankre og native indikatorer

Alle mål er GUI-enheder beregnet fra det aktuelle skalerede vindue. Ved GUI-bredde **630 eller mere** bevarer ressourcepanelet sin tidligere placering: 6 enheder fra venstre, top `h−66`. Evnebjælken ligger ved `x=50`, `y=h−24` og slutter 7,5 enheder fra bunden. Der reserveres mindst 8 enheder til vanilla-hotbarens **venstre offhand-ramme**, også når et skjold er udstyret.

Under 630 GUI-enheder flyttes hele venstre gruppe op over de native rustnings-/luftindikatorer. Ressourcepanelets normale top er `h−151`; evnebjælken følger med direkte under stamina og slutter mindst 4 enheder over indikatorerne. Gruppen reserverer 19 ekstra enheder i forhold til 0.3.8: 2 i mellemrum og 17 som den afrundede pladsreservation for den 16,5-høje bjælke. Selve renderingen er fortsat præcis 16,5. Ekstra native livsrækker, absorption, kortvarigt bevaret liv og luft over ridedyr indgår fortsat i pladsreservationen.

**Vanilla-hotbar, XP og sult flyttes ikke fra 0.3.8.** Hotbarens baggrund ligger ved `h−22`, valgt slots yderkant ved `h−23`. Den native indikatorgruppe har fortsat løft 29: XP-bjælke ved `h−58`, sult over XP, uændrede leveltekst-/locator-/ridehopfunktioner. Evnebjælken har ingen tilknytning til hotbarens overkant længere.

## Chat, synlighed og data

Den eksisterende reservation til genstandsnavne, actionbar og chat følger venstre gruppes top, så de også har plads i smalle vinduer. Chatrendering og klik-/hoverområder flyttes samlet, og chatudsigtens højde tilpasses den resterende plads; gamle linjer kan scrolles. Skrivefelt og chatbeskeder ændres ikke.

F1, spectator, manglende/sene snapshots og native hjerte-fallback er uændrede. Ingen ændringer i serverkode, ressourcedata, evner, stats, point, gemmedata, netværk, ubegrænset leveling eller ×1,5-XP fra belønninger modtaget på level 8+. Ingen nye afhængigheder. Se [VALIDATION.md](VALIDATION.md) for testresultater.

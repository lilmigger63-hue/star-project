package dk.starswordmaster.presentation;
/** Facet shading for cosmetic solids; personalized sword colors never change their base identity. */
public final class CinematicMaterial {
 private CinematicMaterial(){}
 public static int color(AbilityPresentation.Element element,int tone,boolean personal,int base,int rank){
  int core=personal?base:FantasyPalette.color(element,0),edge=personal?AuraColors.highlight(base,rank==4?.85f:.55f):FantasyPalette.color(element,1);
  return switch(tone){case 1->edge;case 2->AuraColors.shade(core,.48f);case 3->AuraColors.highlight(edge,.72f);default->core;};
 }
}

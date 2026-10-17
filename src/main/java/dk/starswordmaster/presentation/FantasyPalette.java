package dk.starswordmaster.presentation;
/** Restrained dark-fantasy cores and readable luminous accents. Player aura identity is separate. */
public final class FantasyPalette {
 private FantasyPalette(){}
 public static int color(AbilityPresentation.Element element,int tone){
  return switch(element){
   case STEEL->tone==0?0x88929B:0xE7D2A2;case FIRE->tone==0?0xBA3B29:0xF6B65E;
   case FROST->tone==0?0x579CAC:0xCCEAF1;case STAR->tone==0?0xA39468:0xF5E7B8;
   case HEALING->tone==0?0x4D9B7C:0xD4C78A;case LIGHTNING->tone==0?0x7877C9:0xC4CFFF;
   case WATER->tone==0?0x318F9E:0xA0E0DA;case EARTH->tone==0?0xA27E4B:0xDDC48A;
   case DARK->tone==0?0x633654:0xBC6C91;
  };
 }
}

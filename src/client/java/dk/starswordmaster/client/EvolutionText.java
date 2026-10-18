package dk.starswordmaster.client;
import dk.starswordmaster.progression.ClassEvolution;
import net.minecraft.text.Text;
import java.util.*;
/** Shared read-only descriptions of the actual derived evolution totals. */
final class EvolutionText {
 private EvolutionText(){}
 static Text title(ClassEvolution e){return Text.translatable(e.titleKey());}
 private static String percent(double n){return String.format(Locale.ROOT,"%.0f",100*n);}
 static List<Text> effects(ClassEvolution e){
  if(e.stage()==0)return List.of(Text.translatable("status.starswordmaster.evolution.pending"));
  String cls=e.playerClass().name().toLowerCase(Locale.ROOT),key="status.starswordmaster.evolution.effect."+cls;
  return switch(e.playerClass()){
   case SWORDSMAN->List.of(Text.translatable(key+".1",percent(e.damageBonus()),e.agilityBonus()),Text.translatable(key+".2",e.staminaBonus(),percent(e.staminaDiscount())));
   case MAGE->List.of(Text.translatable(key+".1",percent(e.damageBonus()),e.manaBonus()),Text.translatable(key+".2",percent(e.manaRegenerationBonus()),percent(e.manaDiscount())));
   case SPELLBLADE->List.of(Text.translatable(key+".1",percent(e.damageBonus()),e.manaBonus()),Text.translatable(key+".2",percent(e.manaDiscount())));
   default->List.of();
  };
 }
}

package dk.starswordmaster.presentation;
import dk.starswordmaster.progression.*;
import java.util.UUID;
/** Compact server-authored cosmetic inputs; no progression or player health is stored here. */
public record VisualState(boolean auraUnlocked,int rank,int swordLevel,float power,PlayerProgress.PlayerClass playerClass,int auraColor,AbilityPresentation.Element element) {
 public static final VisualState DEFAULT=new VisualState(false,0,1,0,PlayerProgress.PlayerClass.UNINITIATED,0xFFFFFF,AbilityPresentation.Element.STAR);
 public VisualState {rank=Math.clamp(rank,0,4);swordLevel=Math.clamp(swordLevel,1,100);power=VisualPowerScaling.safe(power);if(playerClass==null)playerClass=PlayerProgress.PlayerClass.UNINITIATED;auraColor&=0xFFFFFF;if(element==null)element=AbilityPresentation.Element.STAR;}
 public static VisualState from(UUID id,PlayerProgress p,AbilityPresentation.Element element){
  boolean aura=p.learned().contains("aura_sense")||p.learned().contains("aura_awaken")||p.learned().contains("element_binding")||p.learned().contains("thunder_step")||p.learned().contains("aqua_blade")||p.learned().contains("quake_blade")||p.learned().contains("shadow_edge");
  return new VisualState(aura,aura?Math.max(1,p.rank()):p.rank(),p.skillLevel(Skill.SWORD),VisualPowerScaling.power(p.level()),p.playerClass(),AuraColors.forPlayer(id),element);
 }
}

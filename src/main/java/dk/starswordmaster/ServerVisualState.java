package dk.starswordmaster;
import dk.starswordmaster.network.VisualStatePayload;
import dk.starswordmaster.presentation.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.*;
/** Server-authored persistent identity plus short-lived Spellblade elemental accents. */
public final class ServerVisualState {
 private record Accent(AbilityPresentation.Element element,long until){}
 private static final Map<UUID,Accent> accents=new HashMap<>();
 private static final Map<UUID,VisualState> last=new HashMap<>();
 private ServerVisualState(){}
 public static VisualState state(ServerPlayerEntity player){
  var p=StarSwordmaster.progress(player);var a=accents.get(player.getUuid());var element=AbilityPresentation.Element.STAR;
  if(a!=null){if(a.until()>player.getEntityWorld().getTime())element=a.element();else accents.remove(player.getUuid());}
  return VisualState.from(player.getUuid(),p,element);
 }
 public static void activated(ServerPlayerEntity player,Ability ability){
  if(StarSwordmaster.progress(player).playerClass()!=PlayerProgress.PlayerClass.SPELLBLADE||ability.stamina()<=0)return;
  var element=switch(ability.skill()){case LIGHTNING->AbilityPresentation.Element.LIGHTNING;case WATER->AbilityPresentation.Element.WATER;case EARTH->AbilityPresentation.Element.EARTH;case DARK->AbilityPresentation.Element.DARK;case FIRE->AbilityPresentation.Element.FIRE;case FROST->AbilityPresentation.Element.FROST;default->AbilityPresentation.Element.STAR;};
  if(ability.id().equals("flame_blade"))element=AbilityPresentation.Element.FIRE;if(ability.id().equals("frost_blade")||ability.id().equals("glacier_sweep"))element=AbilityPresentation.Element.FROST;
  accents.put(player.getUuid(),new Accent(element,player.getEntityWorld().getTime()+80));sync(player);
 }
 public static void send(ServerPlayerEntity viewer,ServerPlayerEntity subject){if(ServerPlayNetworking.canSend(viewer,VisualStatePayload.ID))ServerPlayNetworking.send(viewer,new VisualStatePayload(subject.getUuid(),subject.getId(),state(subject),false));}
 public static void sync(ServerPlayerEntity player){var next=state(player);if(next.equals(last.put(player.getUuid(),next)))return;var viewers=new HashSet<>(PlayerLookup.tracking(player));viewers.add(player);for(var viewer:viewers)send(viewer,player);}
 public static void remove(ServerPlayerEntity viewer,ServerPlayerEntity subject){if(ServerPlayNetworking.canSend(viewer,VisualStatePayload.ID))ServerPlayNetworking.send(viewer,new VisualStatePayload(subject.getUuid(),subject.getId(),VisualState.DEFAULT,true));}
 public static void forget(UUID id){accents.remove(id);last.remove(id);}
 public static void clear(){accents.clear();last.clear();}
}

package dk.starswordmaster.progression;
import java.util.*;
/** Connection-scoped feedback only. Evolution bonuses themselves are always derived. */
public final class EvolutionMilestones {
 private final Map<UUID,ClassEvolution> previous=new HashMap<>(),pending=new HashMap<>();
 public void seed(UUID id,ClassEvolution current){previous.put(id,current);pending.remove(id);}
 public void observe(UUID id,ClassEvolution current){
  var old=previous.put(id,current);if(old==null||old.equals(current))return;
  pending.remove(id);
  if(current.stage()>0&&(old.playerClass()!=current.playerClass()||current.stage()>old.stage()))pending.put(id,current);
 }
 public ClassEvolution take(UUID id,ClassEvolution current){var next=pending.remove(id);return current.equals(next)?next:null;}
 public void forget(UUID id){previous.remove(id);pending.remove(id);}
 public void clear(){previous.clear();pending.clear();}
}

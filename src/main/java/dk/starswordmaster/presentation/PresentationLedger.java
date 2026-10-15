package dk.starswordmaster.presentation;
import java.util.*;
/** Per-connection event order; world-time age prevents late packets replaying a cast. */
public final class PresentationLedger {
 private final Map<UUID,Long> seen=new HashMap<>();
 public boolean accept(UUID caster,long sequence,long sentAt,long now){
  if(sequence<=seen.getOrDefault(caster,0L)||sentAt>now+2||now-sentAt>40)return false;
  if(seen.size()>1024)seen.clear();
  seen.put(caster,sequence);return true;
 }
 public void clear(){seen.clear();}
}

package dk.starswordmaster.ui;
import dk.starswordmaster.progression.PlayerProgress;
import java.util.*;
/** Read-only HUD values: mod resources from its snapshot, health/absorption from vanilla synchronization. */
public record ResourceHudData(int level,Meter mana,Meter health,Meter stamina,double absorption) {
 public record Meter(double current,double maximum){
  public boolean available(){return Double.isFinite(current)&&current>=0&&Double.isFinite(maximum)&&maximum>0;}
  public double fraction(){return available()?Math.max(0,Math.min(1,current/maximum)):0;}
  public String text(){return available()?number(current)+"/"+number(maximum):"—";}
 }
 public static Optional<ResourceHudData> from(PlayerProgress progress,boolean ready,double health,double maxHealth,double absorption){
  if(!ready||progress==null)return Optional.empty();
  return Optional.of(new ResourceHudData(progress.level(),new Meter(progress.mana(),progress.maxMana()),new Meter(health,maxHealth),new Meter(progress.stamina(),progress.maxStamina()),absorption));
 }
 public String healthText(){return health.text()+(Double.isFinite(absorption)&&absorption>0?" +"+number(absorption):"");}
 private static String number(double value){return String.format(Locale.ROOT,"%.1f",value).replaceFirst("\\.0$","");}
}

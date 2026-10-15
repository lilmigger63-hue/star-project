package dk.starswordmaster.ui;
import dk.starswordmaster.progression.*;
import java.util.*;
/** Read-only presentation of the most recent authoritative player snapshot. */
public record StatusData(int level,int totalXp,int xpInLevel,int xpForNextLevel,
 PlayerProgress.PlayerClass playerClass,int rank,PlayerProgress.SwordStyle style,
 Map<PlayerProgress.Stat,Integer> stats,int statPoints,int perkPoints,
 double mana,double maxMana,double stamina,double maxStamina,List<String> active,List<String> passive,StatBonuses bonuses) {
 public static Optional<StatusData> from(PlayerProgress p,boolean ready){
  if(!ready||p==null)return Optional.empty();
  var stats=new EnumMap<PlayerProgress.Stat,Integer>(PlayerProgress.Stat.class);
  for(var stat:PlayerProgress.Stat.values())stats.put(stat,p.stat(stat));
  List<String> active=Ability.ALL.values().stream().filter(a->a.active()&&p.learned().contains(a.id())).map(Ability::id).toList();
  List<String> passive=Ability.ALL.values().stream().filter(a->!a.active()&&p.learned().contains(a.id())).map(Ability::id).toList();
  return Optional.of(new StatusData(p.level(),p.totalXp(),p.xpInLevel(),p.xpForNextLevel(),p.playerClass(),p.rank(),p.style(),Map.copyOf(stats),p.statPoints(),p.points(),p.mana(),p.maxMana(),p.stamina(),p.maxStamina(),active,passive,p.bonuses()));
 }
 public double xpProgress(){return xpForNextLevel==0?1:Math.max(0,Math.min(1,(double)xpInLevel/xpForNextLevel));}
}

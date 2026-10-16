package dk.starswordmaster.ui;
import dk.starswordmaster.progression.*;
import java.util.*;
import java.math.*;
/** Read-only presentation of the most recent authoritative player snapshot. */
public record StatusData(BigInteger level,BigInteger totalXp,BigInteger xpInLevel,BigInteger xpForNextLevel,
 PlayerProgress.PlayerClass playerClass,int rank,PlayerProgress.SwordStyle style,
 Map<PlayerProgress.Stat,Integer> stats,BigInteger statPoints,BigInteger perkPoints,
 double mana,double maxMana,double stamina,double maxStamina,List<String> active,List<String> passive,StatBonuses bonuses) {
 public static Optional<StatusData> from(PlayerProgress p,boolean ready){
  if(!ready||p==null)return Optional.empty();
  var stats=new EnumMap<PlayerProgress.Stat,Integer>(PlayerProgress.Stat.class);
  for(var stat:PlayerProgress.Stat.values())stats.put(stat,p.stat(stat));
  List<String> active=Ability.ALL.values().stream().filter(a->a.active()&&p.learned().contains(a.id())).map(Ability::id).toList();
  List<String> passive=Ability.ALL.values().stream().filter(a->!a.active()&&p.learned().contains(a.id())).map(Ability::id).toList();
  return Optional.of(new StatusData(p.level(),p.totalXp(),p.xpInLevel(),p.xpForNextLevel(),p.playerClass(),p.rank(),p.style(),Map.copyOf(stats),p.statPoints(),p.points(),p.mana(),p.maxMana(),p.stamina(),p.maxStamina(),active,passive,p.bonuses()));
 }
 public double xpProgress(){return new BigDecimal(xpInLevel).divide(new BigDecimal(xpForNextLevel),MathContext.DECIMAL64).doubleValue();}
}

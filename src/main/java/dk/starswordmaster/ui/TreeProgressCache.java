package dk.starswordmaster.ui;

import dk.starswordmaster.progression.*;
import java.math.BigInteger;
import java.util.*;

/** Relevant progression state excludes rapidly changing mana, stamina and cooldowns. */
public final class TreeProgressCache {
 public record NodeState(boolean known,boolean available) {}
 private static final PlayerProgress.Stat[] STATS=PlayerProgress.Stat.values();
 private final int[] stats=new int[STATS.length];
 private final Map<String,NodeState> nodes=new HashMap<>();
 private Set<String> learned=Set.of();
 private BigInteger level,statPoints;
 private PlayerProgress.PlayerClass playerClass;
 private PlayerProgress.SwordStyle style;
 public boolean update(PlayerProgress p){
  var nextLearned=p.learned();boolean changed=!p.statPoints().equals(statPoints)||!p.level().equals(level)||p.playerClass()!=playerClass||p.style()!=style||!nextLearned.equals(learned);
  for(int i=0;i<STATS.length;i++){int n=p.stat(STATS[i]);if(stats[i]!=n){stats[i]=n;changed=true;}}
  if(!changed)return false;
  level=p.level();statPoints=p.statPoints();playerClass=p.playerClass();style=p.style();learned=Set.copyOf(nextLearned);
  for(var node:SkillTreeLayout.NODES){nodes.put(node.id(),new NodeState(learned.contains(node.id()),p.canUnlock(node.id())));}
  return true;
 }
 public NodeState node(String id){return nodes.get(id);}
}

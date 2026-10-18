package dk.starswordmaster.ui;

import dk.starswordmaster.progression.*;
import java.math.BigInteger;
import java.util.*;

/** Relevant progression state excludes rapidly changing mana, stamina and cooldowns. */
public final class TreeProgressCache {
 public record NodeState(boolean known,boolean classLocked,boolean available) {}
 private static final Skill[] SKILLS=Skill.values();
 private static final PlayerProgress.Stat[] STATS=PlayerProgress.Stat.values();
 private final int[] skills=new int[SKILLS.length],stats=new int[STATS.length];
 private final Map<String,NodeState> nodes=new HashMap<>();
 private Set<String> learned=Set.of();
 private BigInteger level;
 private PlayerProgress.PlayerClass playerClass;
 private PlayerProgress.SwordStyle style;
 public boolean update(PlayerProgress p){
  var nextLearned=p.learned();boolean changed=!p.level().equals(level)||p.playerClass()!=playerClass||p.style()!=style||!nextLearned.equals(learned);
  for(int i=0;i<SKILLS.length;i++){int n=p.skillLevel(SKILLS[i]);if(skills[i]!=n){skills[i]=n;changed=true;}}
  for(int i=0;i<STATS.length;i++){int n=p.stat(STATS[i]);if(stats[i]!=n){stats[i]=n;changed=true;}}
  if(!changed)return false;
  level=p.level();playerClass=p.playerClass();style=p.style();learned=Set.copyOf(nextLearned);
  for(var node:SkillTreeLayout.NODES){var a=Ability.ALL.get(node.id());nodes.put(node.id(),new NodeState(learned.contains(node.id()),!a.classAllowed(playerClass),p.canUnlock(node.id())));}
  return true;
 }
 public NodeState node(String id){return nodes.get(id);}
}

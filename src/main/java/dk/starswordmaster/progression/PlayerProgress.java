package dk.starswordmaster.progression;
import java.util.*;
public final class PlayerProgress {
 public enum PlayerClass { UNINITIATED, SWORDSMAN, MAGE, SPELLBLADE }
 public enum Stat { STRENGTH, AGILITY, ENDURANCE, SPIRIT }
 public enum SwordStyle { QUICK, HEAVY, DEFENSIVE }
 private final EnumMap<Skill,Integer> practice=new EnumMap<>(Skill.class);
 private final Set<String> learned=new LinkedHashSet<>();
 private final Map<String,Integer> cooldowns=new HashMap<>();
 private final Map<String,Integer> cooldownDurations=new HashMap<>();
 private final String[] slots={"","","","",""};
 private int characterXp;
 private final EnumMap<Stat,Integer> stats=new EnumMap<>(Stat.class);
 public int stat(Stat s){return stats.getOrDefault(s,0);}
 public int statPoints(){return Math.max(0,level()-1-stats.values().stream().mapToInt(Integer::intValue).sum());}
 public boolean spendStat(Stat s){if(s==null||statPoints()==0)return false;stats.put(s,stat(s)+1);return true;}
 public double damageMultiplier(){return 1+0.025*stat(Stat.STRENGTH)+(rank()==3?0.15:0);}
 private PlayerClass playerClass=PlayerClass.UNINITIATED;
 private SwordStyle style=SwordStyle.QUICK;
 private double mana=100,stamina=100;
 public int level(){int xp=characterXp,level=1;while(level<100&&xp>=threshold(level)){xp-=threshold(level++);}return level;}
 public int totalXp(){return characterXp;}
 public int xpInLevel(){int current=level();if(current>=100)return 0;int xp=characterXp;for(int i=1;i<current;i++)xp-=threshold(i);return xp;}
 public int xpForNextLevel(){return level()>=100?0:threshold(level());}
 private static int threshold(int level){return 50+20*level;}
 public int skillLevel(Skill s){return Math.min(100,1+(int)Math.sqrt(practice.getOrDefault(s,0)/25.0));}
 public int points(){return Math.max(0,level()-1-learned.size());}
 public void awardPractice(Skill s,int amount){
  if(s==null||amount<=0)return;
  int before=skillLevel(s);
  practice.put(s,(int)Math.min(245025L,(long)practice.getOrDefault(s,0)+Math.min(amount,20)));
  characterXp=Math.min(200000,characterXp+100*(skillLevel(s)-before));
 }
 public boolean chooseClass(PlayerClass c){
  if(c==null||c==PlayerClass.UNINITIATED||playerClass!=PlayerClass.UNINITIATED||level()<10)return false;
  playerClass=c;return true;
 }
 private boolean eligible(Ability a){return a!=null&&(a.exclusiveClass()==null||a.exclusiveClass()==playerClass)&&skillLevel(a.skill())>=a.requirement()&&(a.parent().isEmpty()||learned.contains(a.parent()));}
 public boolean unlock(String id){
  Ability a=Ability.ALL.get(id);
  if(points()<1||learned.contains(id)||!eligible(a))return false;
  learned.add(id);return true;
 }
 public Set<String> learned(){return Collections.unmodifiableSet(learned);}
 public int rank(){
  if(playerClass==PlayerClass.UNINITIATED)return 0;
  String basic=switch(playerClass){case SWORDSMAN->"aura_awaken";case MAGE->"mana_control";case SPELLBLADE->"element_binding";default->"";};
  String master=switch(playerClass){case SWORDSMAN->"sword_mastery";case MAGE->"arcane_mastery";case SPELLBLADE->"spellblade_mastery";default->"";};
  return learned.contains(master)?3:learned.contains(basic)?2:1;
 }
 public boolean assign(int slot,String id){
  if(slot<0||slot>=5||id==null)return false;
  if(id.isEmpty()){slots[slot]="";return true;}
  Ability a=Ability.ALL.get(id);
  if(a==null||!a.active()||!learned.contains(id))return false;
  for(int i=0;i<5;i++)if(slots[i].equals(id))slots[i]="";
  slots[slot]=id;return true;
 }
 public String slot(int slot){return slot<0||slot>=5?"":slots[slot];}
 public int cooldown(String id){return cooldowns.getOrDefault(id,0);}
 public int cooldownDuration(String id){return cooldownDurations.getOrDefault(id,0);}
 public double mana(){return mana;}
 public double stamina(){return stamina;}
 public double maxMana(){return 100+2*(level()-1)+5*stat(Stat.SPIRIT)+(learned.contains("mana_control")?20:0)+(learned.contains("arcane_mastery")?20:0);}
 public double maxStamina(){return 100+level()-1+5*stat(Stat.ENDURANCE)+(learned.contains("aura_awaken")?15:0)+(learned.contains("element_binding")?10:0);}
 public double manaCost(Ability a){
  double cost=a.mana();if(a.skill()==Skill.FIRE&&learned.contains("fire_affinity"))cost*=0.9;
  if(a.stamina()>0&&style==SwordStyle.QUICK&&learned.contains("flow_training"))cost*=0.9;return cost;
 }
 public double staminaCost(Ability a){
  double cost=a.stamina();if(style==SwordStyle.DEFENSIVE&&learned.contains("guard_training"))cost*=0.75;
  else if(style==SwordStyle.QUICK&&learned.contains("flow_training"))cost*=0.9;return cost;
 }
 public int cooldownTicks(Ability a){
  double factor=1-Math.min(0.4,stat(Stat.AGILITY)*0.01);
  if(a.stamina()>0&&style==SwordStyle.QUICK&&learned.contains("flow_training"))factor*=0.9;
  if(a.skill()==Skill.FROST&&learned.contains("frost_affinity"))factor*=0.8;
  return a.active()?Math.max(10,(int)Math.round(a.cooldownTicks()*factor)):0;
 }
 public double damageMultiplier(Ability a){return damageMultiplier()*(a.stamina()>0&&style==SwordStyle.HEAVY&&learned.contains("impact_training")?1.15:1);}
 public double healingMultiplier(){return learned.contains("restorative_affinity")?1.25:1;}
 public boolean canCast(int slot){
  Ability a=Ability.ALL.get(slot(slot));
  return a!=null&&a.active()&&learned.contains(a.id())&&cooldown(a.id())==0&&mana>=manaCost(a)&&stamina>=staminaCost(a);
 }
 public boolean cast(int slot){
  if(!canCast(slot))return false;
  Ability a=Ability.ALL.get(slot(slot));mana-=manaCost(a);stamina-=staminaCost(a);int duration=cooldownTicks(a);cooldowns.put(a.id(),duration);cooldownDurations.put(a.id(),duration);return true;
 }
 public PlayerClass playerClass(){return playerClass;}
 public SwordStyle style(){return style;}
 public void setStyle(SwordStyle style){if(style!=null)this.style=style;}
 public void tick(boolean combat){
  cooldowns.replaceAll((id,t)->Math.max(0,t-1));
  mana=Math.min(maxMana(),mana+(combat?0.04:0.15));
  stamina=Math.min(maxStamina(),stamina+(combat?0.08:0.30));
 }
 public Properties toProperties(){
  Properties p=new Properties();p.setProperty("schema","1");p.setProperty("xp",Integer.toString(characterXp));p.setProperty("class",playerClass.name());p.setProperty("style",style.name());
  p.setProperty("mana",Double.toString(mana));p.setProperty("stamina",Double.toString(stamina));p.setProperty("learned",String.join(",",learned));
  for(Stat s:Stat.values())p.setProperty("stat."+s,Integer.toString(stat(s)));
  for(Skill s:Skill.values())p.setProperty("practice."+s,practice.getOrDefault(s,0).toString());
  for(int i=0;i<5;i++)p.setProperty("slot."+i,slots[i]);
  cooldowns.forEach((id,t)->p.setProperty("cooldown."+id,t.toString()));cooldownDurations.forEach((id,t)->p.setProperty("cooldownDuration."+id,t.toString()));return p;
 }
 private static int integer(Properties p,String key,int max){try{return Math.max(0,Math.min(max,Integer.parseInt(p.getProperty(key,"0"))));}catch(RuntimeException e){return 0;}}
 private static double number(Properties p,String key,double max){try{double v=Double.parseDouble(p.getProperty(key,Double.toString(max)));return Double.isFinite(v)?Math.max(0,Math.min(max,v)):max;}catch(RuntimeException e){return max;}}
 public static PlayerProgress fromProperties(Properties p){
  PlayerProgress q=new PlayerProgress();q.characterXp=integer(p,"xp",200000);
  for(Skill s:Skill.values())q.practice.put(s,integer(p,"practice."+s,245025));
  try{q.playerClass=PlayerClass.valueOf(p.getProperty("class","UNINITIATED"));}catch(IllegalArgumentException ignored){}
  if(q.level()<10)q.playerClass=PlayerClass.UNINITIATED;
  try{q.style=SwordStyle.valueOf(p.getProperty("style","QUICK"));}catch(IllegalArgumentException ignored){}
  for(Stat s:Stat.values()){int n=integer(p,"stat."+s,99);q.stats.put(s,Math.min(n,q.statPoints()));}
    Set<String> requested=new LinkedHashSet<>(Arrays.asList(p.getProperty("learned","").split(",")));
  for(int pass=0;pass<Ability.ALL.size();pass++)for(Ability a:Ability.ALL.values())if(requested.contains(a.id())&&!q.learned.contains(a.id()))q.unlock(a.id());
  q.mana=number(p,"mana",q.maxMana());q.stamina=number(p,"stamina",q.maxStamina());
  for(int i=0;i<5;i++)q.assign(i,p.getProperty("slot."+i,""));
  for(Ability a:Ability.ALL.values()){q.cooldowns.put(a.id(),integer(p,"cooldown."+a.id(),a.cooldownTicks()));int duration=p.containsKey("cooldownDuration."+a.id())?integer(p,"cooldownDuration."+a.id(),a.cooldownTicks()):q.cooldownTicks(a);q.cooldownDurations.put(a.id(),Math.max(q.cooldown(a.id()),duration));}
  return q;
 }
}

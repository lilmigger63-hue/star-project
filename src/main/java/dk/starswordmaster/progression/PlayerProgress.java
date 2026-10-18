package dk.starswordmaster.progression;
import java.util.*;
import java.math.BigInteger;
public final class PlayerProgress {
 public static final int STAT_POINTS_PER_LEVEL=5;
 public enum PlayerClass { UNINITIATED, SWORDSMAN, MAGE, SPELLBLADE }
 public enum Stat { STRENGTH, AGILITY, ENDURANCE, SPIRIT }
 public enum SwordStyle { QUICK, HEAVY, DEFENSIVE }
 private final EnumMap<Skill,BigInteger> practice=new EnumMap<>(Skill.class);
 private final Set<String> learned=new LinkedHashSet<>();
 private final Map<String,Integer> cooldowns=new HashMap<>();
 private final Map<String,Integer> cooldownDurations=new HashMap<>();
 private final String[] slots={"","","","",""};
 private CharacterExperience experience=new CharacterExperience();
 private final EnumMap<Stat,Integer> stats=new EnumMap<>(Stat.class);
 public int stat(Stat s){return stats.getOrDefault(s,0);}
 // Derived entitlement makes old saves and repeated snapshots idempotent: allocated stats are never re-awarded.
 public BigInteger statPoints(){return level().subtract(BigInteger.ONE).multiply(BigInteger.valueOf(STAT_POINTS_PER_LEVEL)).subtract(BigInteger.valueOf(stats.values().stream().mapToLong(Integer::longValue).sum())).max(BigInteger.ZERO);}
 public boolean spendStat(Stat s){if(s==null||statPoints().signum()==0||stat(s)==Integer.MAX_VALUE)return false;stats.put(s,stat(s)+1);return true;}
 public long effectiveStat(Stat s){
  long bonus=0;for(String id:learned){var a=Ability.ALL.get(id);if(a!=null&&a.statBonus()==s)bonus++;}
  if(rank()==4)bonus+=switch(playerClass){
   case SWORDSMAN->switch(s){case STRENGTH->3;case AGILITY->2;default->0;};
   case MAGE->switch(s){case SPIRIT->4;case ENDURANCE->1;default->0;};
   case SPELLBLADE->switch(s){case STRENGTH,SPIRIT->2;case AGILITY->1;default->0;};
   default->0;
  };
  return (long)stat(s)+bonus;
 }
 public double strengthDamageBonus(){return 0.025*effectiveStat(Stat.STRENGTH);}
 public double agilityCooldownReduction(){return Math.min(0.4,effectiveStat(Stat.AGILITY)*0.01);}
 public long enduranceStaminaBonus(){return 5L*effectiveStat(Stat.ENDURANCE);}
 public long spiritManaBonus(){return 5L*effectiveStat(Stat.SPIRIT);}
 public StatBonuses bonuses(){return StatBonuses.from(this);}
 public double damageMultiplier(){return 1+strengthDamageBonus()+(rank()>=3?0.15:0);}
 private PlayerClass playerClass=PlayerClass.UNINITIATED;
 private SwordStyle style=SwordStyle.QUICK;
 private double mana=100,stamina=100;
 public BigInteger level(){return experience.level();}
 public BigInteger totalXp(){return experience.totalXp();}
 public BigInteger xpInLevel(){return experience.xpInLevel();}
 public BigInteger xpForNextLevel(){return experience.xpForNextLevel();}
 public void awardXp(BigInteger amount){experience.award(amount);}
 private BigInteger trainingLevel(Skill s){return practice.getOrDefault(s,BigInteger.ZERO).divide(BigInteger.valueOf(25)).sqrt().add(BigInteger.ONE);}
 public int skillLevel(Skill s){return trainingLevel(s).min(BigInteger.valueOf(100)).intValueExact();}
 public BigInteger points(){return level().subtract(BigInteger.ONE).subtract(BigInteger.valueOf(learned.size())).max(BigInteger.ZERO);}
 public void awardPractice(Skill s,int amount){awardPractice(s,amount,0);}
 public void awardPractice(Skill s,int amount,double characterXpMultiplier){
  if(s==null||amount<=0)return;
  var before=trainingLevel(s);
  practice.put(s,practice.getOrDefault(s,BigInteger.ZERO).add(BigInteger.valueOf(Math.min(amount,20))));
  var reward=trainingLevel(s).subtract(before).multiply(BigInteger.valueOf(100));
  if(Double.isFinite(characterXpMultiplier)&&characterXpMultiplier>0)
   awardXp(new java.math.BigDecimal(reward).multiply(java.math.BigDecimal.valueOf(characterXpMultiplier)).toBigInteger());
 }
 public boolean chooseClass(PlayerClass c){
  if(c==null||c==PlayerClass.UNINITIATED||playerClass!=PlayerClass.UNINITIATED||level().compareTo(BigInteger.TEN)<0)return false;
  playerClass=c;return true;
 }
 private boolean eligible(Ability a){return a!=null&&a.classAllowed(playerClass)&&level().compareTo(BigInteger.valueOf(a.characterRequirement()))>=0&&skillLevel(a.skill())>=a.requirement()&&(a.parent().isEmpty()||learned.contains(a.parent()));}
 public boolean canUnlock(String id){return points().signum()>0&&!learned.contains(id)&&eligible(Ability.ALL.get(id));}
 public boolean unlock(String id){
  if(!canUnlock(id))return false;
  learned.add(id);return true;
 }
 public Set<String> learned(){return Collections.unmodifiableSet(learned);}
 /** Refunds derived perk entitlement only; resources may shrink but never heal. */
 public boolean resetLearnedSkills(){
  boolean changed=!learned.isEmpty()||Arrays.stream(slots).anyMatch(id->!id.isEmpty());
  if(!changed)return false;
  learned.clear();Arrays.fill(slots,"");
  // Positive cooldown debt survives free reset/relearning and reconnect; removed skills cannot cast.
  mana=Math.min(mana,maxMana());stamina=Math.min(stamina,maxStamina());
  return true;
 }
 public int rank(){
  if(playerClass==PlayerClass.UNINITIATED)return 0;
  String basic=switch(playerClass){case SWORDSMAN->"aura_awaken";case MAGE->"mana_control";case SPELLBLADE->"element_binding";default->"";};
  String master=switch(playerClass){case SWORDSMAN->"sword_mastery";case MAGE->"arcane_mastery";case SPELLBLADE->"spellblade_mastery";default->"";};
  return learned.contains(master)?(learned.contains(ultimateRankId())?4:3):learned.contains(basic)?2:1;
 }
 private String ultimateRankId(){return switch(playerClass){case SWORDSMAN->"astral_sword_sovereign";case MAGE->"celestial_archon";case SPELLBLADE->"eclipse_sovereign";default->"";};}
 public String rankNodeId(){return rank()==4?ultimateRankId():"";}
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
 // Keep the original resource formula; conversion remains finite at arbitrarily high character levels.
 private double resourceLevels(){return Math.min(Double.MAX_VALUE/4,level().subtract(BigInteger.ONE).doubleValue());}
 public double maxMana(){return 100+2*resourceLevels()+spiritManaBonus()+(learned.contains("mana_control")?20:0)+(learned.contains("arcane_mastery")?20:0);}
 public double maxStamina(){return 100+resourceLevels()+enduranceStaminaBonus()+(learned.contains("aura_awaken")?15:0)+(learned.contains("element_binding")?10:0);}
 public double manaCost(Ability a){
  double cost=a.mana();if(a.skill()==Skill.FIRE&&learned.contains("fire_affinity"))cost*=0.9;
  if(a.stamina()>0&&style==SwordStyle.QUICK&&learned.contains("flow_training"))cost*=0.9;return cost;
 }
 public double staminaCost(Ability a){
  double cost=a.stamina();if(style==SwordStyle.DEFENSIVE&&learned.contains("guard_training"))cost*=0.75;
  else if(style==SwordStyle.QUICK&&learned.contains("flow_training"))cost*=0.9;return cost;
 }
 public int cooldownTicks(Ability a){
  double factor=1-agilityCooldownReduction();
  if(a.stamina()>0&&style==SwordStyle.QUICK&&learned.contains("flow_training"))factor*=0.9;
  if(a.skill()==Skill.FROST&&learned.contains("frost_affinity"))factor*=0.8;
  return a.active()?Math.max(10,(int)Math.round(a.cooldownTicks()*factor)):0;
 }
 public double rankDamageBonus(Ability a){
  if(rank()!=4)return 0;
  return switch(playerClass){
   case SWORDSMAN->a.damageKind()==Ability.DamageKind.SWORD||a.damageKind()==Ability.DamageKind.ENCHANTED_SWORD?0.05:0;
   case MAGE->a.damageKind()==Ability.DamageKind.MAGIC?0.05:0;
   case SPELLBLADE->a.damageKind()==Ability.DamageKind.ENCHANTED_SWORD?0.05:0;
   default->0;
  };
 }
 public double damageMultiplier(Ability a){return damageMultiplier()*(a.stamina()>0&&style==SwordStyle.HEAVY&&learned.contains("impact_training")?1.15:1)*(1+rankDamageBonus(a));}
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
 public double manaRegenPerTick(boolean combat){return (combat?0.04:0.15)*(1+bonuses().manaRegeneration());}
 public void tick(boolean combat){
  cooldowns.replaceAll((id,t)->Math.max(0,t-1));
  mana=Math.min(maxMana(),mana+manaRegenPerTick(combat));
  stamina=Math.min(maxStamina(),stamina+(combat?0.08:0.30));
 }
 public Properties toProperties(){
  Properties p=new Properties();p.setProperty("schema","1");experience.write(p);p.setProperty("class",playerClass.name());p.setProperty("style",style.name());
  p.setProperty("mana",Double.toString(mana));p.setProperty("stamina",Double.toString(stamina));p.setProperty("learned",String.join(",",learned));
  for(Stat s:Stat.values())p.setProperty("stat."+s,Integer.toString(stat(s)));
  for(Skill s:Skill.values())p.setProperty("practice."+s,practice.getOrDefault(s,BigInteger.ZERO).toString());
  for(int i=0;i<5;i++)p.setProperty("slot."+i,slots[i]);
  cooldowns.forEach((id,t)->p.setProperty("cooldown."+id,t.toString()));cooldownDurations.forEach((id,t)->p.setProperty("cooldownDuration."+id,t.toString()));return p;
 }
 private static int integer(Properties p,String key,int max){try{return Math.max(0,Math.min(max,Integer.parseInt(p.getProperty(key,"0"))));}catch(RuntimeException e){return 0;}}
 private static double number(Properties p,String key,double max){try{double v=Double.parseDouble(p.getProperty(key,Double.toString(max)));return Double.isFinite(v)?Math.max(0,Math.min(max,v)):max;}catch(RuntimeException e){return max;}}
 public static PlayerProgress fromProperties(Properties p){
  PlayerProgress q=new PlayerProgress();q.experience=CharacterExperience.read(p);
  for(Skill s:Skill.values())q.practice.put(s,CharacterExperience.nonnegative(p,"practice."+s));
  try{q.playerClass=PlayerClass.valueOf(p.getProperty("class","UNINITIATED"));}catch(IllegalArgumentException ignored){}
  if(q.level().compareTo(BigInteger.TEN)<0)q.playerClass=PlayerClass.UNINITIATED;
  try{q.style=SwordStyle.valueOf(p.getProperty("style","QUICK"));}catch(IllegalArgumentException ignored){}
  for(Stat s:Stat.values()){int n=integer(p,"stat."+s,Integer.MAX_VALUE);q.stats.put(s,q.statPoints().min(BigInteger.valueOf(n)).intValueExact());}
    Set<String> requested=new LinkedHashSet<>(Arrays.asList(p.getProperty("learned","").split(",")));
  for(int pass=0;pass<Ability.ALL.size();pass++){
   int before=q.learned.size();
   for(Ability a:Ability.ALL.values())if(requested.contains(a.id())&&!q.learned.contains(a.id()))q.unlock(a.id());
   if(q.learned.size()==before)break;
  }
  q.mana=number(p,"mana",q.maxMana());q.stamina=number(p,"stamina",q.maxStamina());
  for(int i=0;i<5;i++)q.assign(i,p.getProperty("slot."+i,""));
  for(Ability a:Ability.ALL.values())if(a.active()){
   int remaining=integer(p,"cooldown."+a.id(),a.cooldownTicks());
   if(remaining>0){q.cooldowns.put(a.id(),remaining);int duration=p.containsKey("cooldownDuration."+a.id())?integer(p,"cooldownDuration."+a.id(),a.cooldownTicks()):q.cooldownTicks(a);q.cooldownDurations.put(a.id(),Math.max(remaining,duration));}
  }
  return q;
 }
}

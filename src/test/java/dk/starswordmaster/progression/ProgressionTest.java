package dk.starswordmaster.progression;
import java.util.Properties;
public class ProgressionTest {
 static int checks;
 static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
 static PlayerProgress veteran(){var p=new PlayerProgress();for(int i=0;i<1200;i++)p.awardPractice(Skill.SWORD,20);return p;}
 public static void main(String[] args){
  var p=new PlayerProgress();
  check(p.level()==1,"start weak");
  check(!p.chooseClass(PlayerProgress.PlayerClass.SWORDSMAN),"class gated below level 10");
  check(!p.chooseClass(PlayerProgress.PlayerClass.UNINITIATED),"cannot select starting class");
  p=veteran();check(p.level()>=10,"practice raises level");
  check(p.chooseClass(PlayerProgress.PlayerClass.SWORDSMAN),"choose at level 10");
  check(!p.chooseClass(PlayerProgress.PlayerClass.MAGE),"permanent class");
  check(!p.unlock("sword_mastery"),"prerequisite needed");
  check(!p.unlock("mana_control"),"class gate");
  check(p.unlock("spark"),"swordsman learns simple magic");
  check(!p.unlock("spark"),"duplicate unlock rejected");
  check(p.unlock("quick_cut"),"basic sword skill");
  check(p.unlock("aura_awaken"),"rank perk");
  check(p.rank()==2,"rank from perk");
  check(p.assign(0,"quick_cut"),"assign learned skill");
  check(p.assign(1,"quick_cut"),"move skill");
  check(p.slot(0).isEmpty(),"no duplicates");
  check(!p.assign(5,"spark"),"only five slots");
  check(!p.assign(-1,"spark"),"negative slot rejected");
  check(!p.assign(2,"aura_awaken"),"passive not assignable");
  double mana=p.mana(),stamina=p.stamina();
  check(p.cast(1),"first cast");
  check(p.mana()<mana&&p.stamina()<stamina,"sword pays both");
  mana=p.mana();check(!p.cast(1),"cooldown");check(p.mana()==mana,"failure free");
  var saved=p.toProperties();var q=PlayerProgress.fromProperties(saved);
  check(q.playerClass()==p.playerClass()&&q.level()==p.level()&&q.slot(1).equals("quick_cut"),"save reload");
  check(!q.cast(1),"cooldown survives reload");
  for(int i=0;i<200;i++)q.tick(false);check(q.cast(1),"cooldown expires");
  var combat=PlayerProgress.fromProperties(q.toProperties());var peaceful=PlayerProgress.fromProperties(q.toProperties());
  combat.tick(true);peaceful.tick(false);check(peaceful.mana()>combat.mana(),"slower combat regen");
  var negative=new PlayerProgress();negative.awardPractice(Skill.SWORD,-50);check(negative.level()==1,"negative xp ignored");
  var bad=new Properties();bad.setProperty("class","garbage");bad.setProperty("mana","NaN");
  check(Double.isFinite(PlayerProgress.fromProperties(bad).mana()),"invalid saved numbers sanitized");
  var stats=veteran();double oldMax=stats.maxMana();
  check(stats.spendStat(PlayerProgress.Stat.SPIRIT),"spend statpoint");
  check(stats.maxMana()>oldMax,"spirit increases mana");
  check(stats.statPoints()==stats.level()-2,"stat point accounting");
  check(PlayerProgress.fromProperties(stats.toProperties()).stat(PlayerProgress.Stat.SPIRIT)==1,"stats persist");
  check(!new PlayerProgress().spendStat(PlayerProgress.Stat.STRENGTH),"no free stats");
  var exhausted=veteran();exhausted.unlock("spark");exhausted.assign(0,"spark");
  for(int cycle=0;cycle<40;cycle++){exhausted.cast(0);for(int t=0;t<40;t++)exhausted.tick(true);}
  double beforeFail=exhausted.mana();check(!exhausted.cast(0),"insufficient mana rejects");check(beforeFail==exhausted.mana(),"resource failure unchanged");
  Properties spoof=new Properties();spoof.setProperty("class","MAGE");spoof.setProperty("learned","mana_control,spark");spoof.setProperty("slot.0","spark");
  var clean=PlayerProgress.fromProperties(spoof);check(clean.playerClass()==PlayerProgress.PlayerClass.UNINITIATED&&clean.learned().isEmpty(),"invalid save cannot grant free class or perks");
  System.out.println("PASS: "+checks+" progression checks");
 }
}

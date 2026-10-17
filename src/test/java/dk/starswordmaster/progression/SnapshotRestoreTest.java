package dk.starswordmaster.progression;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class SnapshotRestoreTest {
 @Test public void reverseOrderedPrerequisitesAndRepeatedSnapshotsPreserveBudgets(){
  for(var cls:List.of(PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE)){
   var props=new Properties();props.setProperty("class",cls.name());props.setProperty("xp","200000");for(var s:Skill.values())props.setProperty("practice."+s,"250000");
   var ids=new ArrayList<>(Ability.ALL.keySet());Collections.reverse(ids);props.setProperty("learned",String.join(",",ids));
   var p=PlayerProgress.fromProperties(props);var saved=p.toProperties();assertEquals(4,p.rank());
   for(int i=0;i<30;i++){var restored=PlayerProgress.fromProperties(saved);assertEquals(p.learned(),restored.learned());assertEquals(p.points(),restored.points());assertEquals(p.statPoints(),restored.statPoints());assertEquals(saved,restored.toProperties());}
  }
 }
}

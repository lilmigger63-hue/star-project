package dk.starswordmaster.progression;
import dk.starswordmaster.PlayerStore;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.*;
import java.util.*;
public class DomainContractTest {
 @Test public void progression(){ProgressionTest.main(new String[0]);}
 @Test public void playersPersistSeparately() throws Exception {
  Path dir=Files.createTempDirectory("star-save-test");var store=new PlayerStore(dir);UUID a=UUID.randomUUID(),b=UUID.randomUUID();
  for(int i=0;i<100;i++)store.get(a).awardPractice(Skill.SWORD,20);
  assertTrue(store.get(a).level().intValueExact()>store.get(b).level().intValueExact());store.saveAll();var restored=new PlayerStore(dir);
  assertEquals(store.get(a).level().intValueExact(),restored.get(a).level().intValueExact());assertEquals(1,restored.get(b).level().intValueExact());
  assertFalse(Files.exists(dir.resolve(a+".tmp")));
  for(Path p:Files.list(dir).toList())Files.delete(p);Files.delete(dir);
 }
 @Test public void corruptSaveIsNotSilentlyOverwritten() throws Exception {
  Path dir=Files.createTempDirectory("star-corrupt-test");UUID id=UUID.randomUUID();Path file=dir.resolve(id+".properties");String malformed="xp=\\uZZZZ";Files.writeString(file,malformed);
  try{new PlayerStore(dir).get(id);fail("corrupt file must fail");}catch(IllegalStateException expected){assertEquals(malformed,Files.readString(file));}
  Files.delete(file);Files.delete(dir);
 }
 @Test public void unknownAbilitiesAndInvalidSlotsCannotBeUsed(){
  var p=new PlayerProgress();assertFalse(p.unlock("nonexistent"));assertFalse(p.assign(0,"nonexistent"));assertFalse(p.cast(-1));assertFalse(p.cast(5));assertEquals(100,p.mana(),0.001);
 }
 @Test public void advancedStylesAndMagicRespectClasses(){
  for(var cls:new PlayerProgress.PlayerClass[]{PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE}){
   var p=new PlayerProgress();for(Skill skill:Skill.values())for(int i=0;i<500;i++)p.awardPractice(skill,20);assertTrue(p.chooseClass(cls));
   String[] route=switch(cls){
    case SWORDSMAN->new String[]{"quick_cut","quick_dash","rapid_slash"};
    case MAGE->new String[]{"spark","mana_control","fire_burst","flame_lance"};
    case SPELLBLADE->new String[]{"heavy_cut","element_binding","frost_blade","glacier_sweep"};
    default->throw new AssertionError();
   };
   for(String id:route)assertTrue("learn "+id,p.unlock(id));
   assertTrue(p.assign(4,route[route.length-1]));assertTrue(p.cast(4));
   if(cls!=PlayerProgress.PlayerClass.MAGE)assertFalse(p.unlock("flame_lance"));
  }
 }
 @Test public void rankPerksGrantResourceBonuses(){
  var p=new PlayerProgress();for(int i=0;i<500;i++)p.awardPractice(Skill.FIRE,20);assertTrue(p.chooseClass(PlayerProgress.PlayerClass.MAGE));p.unlock("spark");double capacity=p.maxMana();assertTrue(p.unlock("mana_control"));assertTrue(p.maxMana()>capacity);
 }
 @Test public void perkResourceBonusesSurviveSaveAndSnapshot(){
  var p=new PlayerProgress();for(int i=0;i<500;i++)p.awardPractice(Skill.FIRE,20);assertTrue(p.chooseClass(PlayerProgress.PlayerClass.MAGE));assertTrue(p.unlock("spark"));assertTrue(p.unlock("mana_control"));for(int i=0;i<5000;i++)p.tick(false);
  var q=PlayerProgress.fromProperties(p.toProperties());assertEquals(p.maxMana(),q.maxMana(),0);assertEquals(p.mana(),q.mana(),0);
  var s=new PlayerProgress();for(int i=0;i<500;i++)s.awardPractice(Skill.SWORD,20);s.chooseClass(PlayerProgress.PlayerClass.SWORDSMAN);s.unlock("quick_cut");s.unlock("aura_awaken");for(int i=0;i<5000;i++)s.tick(false);
  var r=PlayerProgress.fromProperties(s.toProperties());assertEquals(s.stamina(),r.stamina(),0);
 }
 @Test public void classSelectionOpensAtExactlyLevelTen(){
  var props=new Properties();props.setProperty("xp","1349");var nine=PlayerProgress.fromProperties(props);assertEquals(9,nine.level().intValueExact());assertFalse(nine.chooseClass(PlayerProgress.PlayerClass.MAGE));
  props.setProperty("xp","1350");var ten=PlayerProgress.fromProperties(props);assertEquals(10,ten.level().intValueExact());assertTrue(ten.chooseClass(PlayerProgress.PlayerClass.MAGE));
 }
}

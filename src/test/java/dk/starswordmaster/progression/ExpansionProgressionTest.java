package dk.starswordmaster.progression;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class ExpansionProgressionTest {
 public static final List<String> SPELLS=List.of("static_spark","chain_lightning","thunder_step","heavens_wrath","water_bolt","tidal_wave","aqua_blade","ocean_prison","stone_spike","earthen_wall","quake_blade","worldbreaker","shadow_bolt","curse_mark","shadow_edge","eclipse_nova");
 public static final List<String> SWORDS=List.of("astral_crescent","falling_star","phantom_blades","celestial_rush","sovereign_judgment","heavenly_sword_domain");
 private PlayerProgress trained(PlayerProgress.PlayerClass cls){var p=new Properties();p.setProperty("xp","200000");p.setProperty("class",cls.name());p.setProperty("mana","9999");p.setProperty("stamina","9999");for(var skill:Skill.values())p.setProperty("practice."+skill,"250000");return PlayerProgress.fromProperties(p);}
 @Test public void registersAllSpellsSwordTechniquesAndPassiveAura(){
  assertEquals(85,Ability.ALL.size());assertEquals(54,Ability.ALL.values().stream().filter(Ability::active).count());
  for(String id:SPELLS){var a=Ability.ALL.get(id);assertNotNull(id,a);assertTrue(a.active());assertTrue(a.mana()>0);assertTrue(a.cooldownTicks()>=40);assertNotEquals("",a.parent());}
  for(String id:SWORDS){var a=Ability.ALL.get(id);assertNotNull(id,a);assertTrue(a.active());assertEquals(PlayerProgress.PlayerClass.SWORDSMAN,a.exclusiveClass());assertTrue(a.stamina()>0);}
  assertFalse(Ability.ALL.get("aura_sense").active());assertFalse(new PlayerProgress().unlock("heavenly_sword_domain"));
 }
 @Test public void fourNewPracticeSkillsAndOldSavesAreCompatible(){
  for(String name:List.of("LIGHTNING","WATER","EARTH","DARK")){var s=Skill.valueOf(name);var p=new PlayerProgress();assertEquals(1,p.skillLevel(s));p.awardPractice(s,20);p.awardPractice(s,20);var q=PlayerProgress.fromProperties(p.toProperties());assertEquals(p.skillLevel(s),q.skillLevel(s));}
  var old=new Properties();old.setProperty("xp","7200");old.setProperty("class","SWORDSMAN");old.setProperty("practice.SWORD","10000");old.setProperty("learned","quick_cut,aura_awaken,sword_mastery");old.setProperty("stat.STRENGTH","9");
  var p=PlayerProgress.fromProperties(old);assertEquals(25,p.level().intValueExact());assertEquals(3,p.rank());assertEquals(9,p.stat(PlayerProgress.Stat.STRENGTH));assertEquals(Set.of("quick_cut","aura_awaken","sword_mastery"),p.learned());
  for(String name:List.of("LIGHTNING","WATER","EARTH","DARK"))assertEquals(1,p.skillLevel(Skill.valueOf(name)));
 }
 @Test public void newSkillsUseExactlyExistingPerkManaStaminaAndCooldownAccounting(){
  Set<String> exercised=new HashSet<>();for(var cls:List.of(PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE)){
   var p=trained(cls);for(var a:Ability.ALL.values())p.unlock(a.id());
   var all=new ArrayList<>(SPELLS);all.addAll(SWORDS);for(String id:all){var a=Ability.ALL.get(id);if(!a.classAllowed(cls))continue;
    assertTrue(id,p.learned().contains(id));assertTrue(p.assign(0,id));double mana=p.mana(),stamina=p.stamina();assertTrue(id,p.cast(0));assertEquals(p.manaCost(a),mana-p.mana(),1e-8);assertEquals(p.staminaCost(a),stamina-p.stamina(),1e-8);assertEquals(p.cooldownTicks(a),p.cooldown(id));assertFalse(p.cast(0));exercised.add(id);
    for(int i=0;i<2000;i++)p.tick(false);
   }
   var q=PlayerProgress.fromProperties(p.toProperties());assertEquals(p.learned(),q.learned());assertEquals(p.statPoints(),q.statPoints());assertEquals(p.points(),q.points());
  }
  assertTrue(exercised.containsAll(SPELLS));assertTrue(exercised.containsAll(SWORDS));
 }
 @Test public void allElementsAndUltimateBranchesAreOpenToEveryClass(){
  for(var cls:List.of(PlayerProgress.PlayerClass.SWORDSMAN,PlayerProgress.PlayerClass.MAGE,PlayerProgress.PlayerClass.SPELLBLADE)){
   var p=trained(cls);for(String id:List.of("static_spark","water_bolt","stone_spike","shadow_bolt")){var a=Ability.ALL.get(id);assertNotNull(a);assertTrue(p.unlock(a.parent()));assertTrue(p.unlock(id));}
   assertTrue(p.unlock("heavenly_sword_domain"));assertTrue(p.unlock("heavens_wrath"));assertTrue(p.unlock("ocean_prison"));
  }
 }
 @Test public void everyNewNodeAndElementHasBothTranslations() throws Exception {
  var ids=new ArrayList<>(SPELLS);ids.addAll(SWORDS);ids.add("aura_sense");
  for(String language:List.of("da_dk","en_us")){
   var json=com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/assets/starswordmaster/lang/"+language+".json"))).getAsJsonObject();
   for(String id:ids)for(String prefix:List.of("ability.","description.")){String key=prefix+"starswordmaster."+id;assertTrue(key,json.has(key));assertFalse(json.get(key).getAsString().isBlank());}
   for(var skill:Skill.values())assertTrue(json.has("skill.starswordmaster."+skill.name().toLowerCase(Locale.ROOT)));
   for(String quality:List.of("low","normal","ultra"))assertTrue(json.has("status.starswordmaster.quality."+quality));
  }
 }
}

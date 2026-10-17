package dk.starswordmaster.localization;

import com.google.gson.*;
import dk.starswordmaster.progression.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** English is the mod's presentation contract, including when vanilla uses Danish. */
public class EnglishPresentationTest {
 private static final Path ROOT=Path.of("src/main/resources/assets/starswordmaster/lang");
 private static final Pattern DANISH=Pattern.compile("[æøåÆØÅ]|\\b(fjende[nr]?|hurtig[t]?|giver|gendanner|rammer|omkring|tidligere|perkpoint|rang|livspoint|sekunder|kræver|klassevalg|magiker|sværdslag|kan ikke|lærte|tildel|færdighed|luk)\\b",Pattern.CASE_INSENSITIVE);
 private static JsonObject locale(String name)throws Exception{return JsonParser.parseString(Files.readString(ROOT.resolve(name+".json"))).getAsJsonObject();}
 private static void require(JsonObject json,String key){assertTrue("Missing English presentation: "+key,json.has(key));assertFalse("Empty: "+key,json.get(key).getAsString().isBlank());}
 @Test public void danishClientsReceiveExactlyTheEnglishModStrings()throws Exception{
  var en=locale("en_us");var da=locale("da_dk");assertEquals(en.keySet(),da.keySet());
  for(var key:en.keySet())assertEquals(key,en.get(key),da.get(key));
 }
 @Test public void everyAbilityAndDynamicUiFamilyHasAnEnglishName()throws Exception{
  var en=locale("en_us");
  for(var ability:Ability.ALL.values())for(String prefix:List.of("ability.","description."))require(en,prefix+"starswordmaster."+ability.id());
  for(var skill:Skill.values())require(en,"skill.starswordmaster."+skill.name().toLowerCase(Locale.ROOT));
  for(var cls:PlayerProgress.PlayerClass.values())require(en,"status.starswordmaster.class."+cls.name().toLowerCase(Locale.ROOT));
  for(var style:PlayerProgress.SwordStyle.values())require(en,"status.starswordmaster.style."+style.name().toLowerCase(Locale.ROOT));
  for(var stat:PlayerProgress.Stat.values())for(String prefix:List.of("stat.","effect.","rule.old.","rule.new."))require(en,"status.starswordmaster."+prefix+stat.name().toLowerCase(Locale.ROOT));
  for(String key:List.of("character","stats","resources","level","class","rank","style","total_xp","perk_points","stat_points","mana","stamina","active","passive","quality.low","quality.normal","quality.ultra","rank_damage.swordsman","rank_damage.mage","rank_damage.spellblade"))require(en,"status.starswordmaster."+key);
  for(String key:List.of("screen.starswordmaster.confirm_class","screen.starswordmaster.class_warning","screen.starswordmaster.previous_ability","screen.starswordmaster.required_class","screen.starswordmaster.close","screen.starswordmaster.cancel","screen.starswordmaster.confirm","screen.starswordmaster.tree_hint","screen.starswordmaster.learned","screen.starswordmaster.price","screen.starswordmaster.state.available","screen.starswordmaster.state.class_locked","screen.starswordmaster.state.locked","message.starswordmaster.command_status","message.starswordmaster.action_failed","hud.starswordmaster.level"))require(en,key);
 }
 @Test public void everyStaticTranslationReferenceExists()throws Exception{
  var en=locale("en_us");var reference=Pattern.compile("(Text\\.translatable|label|treeText)\\(\"([^\"]+)\"[,)]+",Pattern.MULTILINE);
  for(String directory:List.of("src/main/java","src/client/java"))try(var paths=Files.walk(Path.of(directory))){
   for(var path:paths.filter(p->p.toString().endsWith(".java")).toList()){
    var matcher=reference.matcher(Files.readString(path));
    while(matcher.find()){String key=matcher.group(2);if(matcher.group(1).equals("label"))key="status.starswordmaster."+key;else if(matcher.group(1).equals("treeText"))key="screen.starswordmaster."+key;if(key.contains("starswordmaster"))require(en,key);}
   }
  }
 }
 @Test public void englishLocalesAndProductionStringLiteralsContainNoDanish()throws Exception{
  for(var entry:locale("en_us").entrySet())assertFalse(entry.getKey()+": "+entry.getValue(),DANISH.matcher(entry.getValue().getAsString()).find());
  var literal=Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\"");
  for(String directory:List.of("src/main/java","src/client/java"))try(var paths=Files.walk(Path.of(directory))){
   for(var path:paths.filter(p->p.toString().endsWith(".java")).toList()){
    var strings=literal.matcher(Files.readString(path));while(strings.find())assertFalse(path+": "+strings.group(),DANISH.matcher(strings.group()).find());
   }
  }
 }
}

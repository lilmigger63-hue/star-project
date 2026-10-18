package dk.starswordmaster.progression;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/** Optional server config, read at startup. Invalid/missing individual fields retain safe defaults. */
public record ProgressionConfig(boolean killXpEnabled,double healthMultiplier,boolean includePassiveMobs,
 double practiceCharacterXpMultiplier,boolean showKillXp,Set<String> excludedEntityIds) {
 public ProgressionConfig {excludedEntityIds=Set.copyOf(excludedEntityIds);}
 public static ProgressionConfig defaults(){return new ProgressionConfig(true,3,false,0,true,Set.of());}
 public static ProgressionConfig load(Path path){
  if(!Files.isRegularFile(path))return defaults();
  try(var reader=Files.newBufferedReader(path)){
   JsonObject json=JsonParser.parseReader(reader).getAsJsonObject();
   Set<String> excluded=new HashSet<>();
   JsonElement ids=json.get("excludedEntityIds");
   if(ids!=null&&ids.isJsonArray())for(var id:ids.getAsJsonArray())
    if(id.isJsonPrimitive()&&id.getAsJsonPrimitive().isString())excluded.add(id.getAsString());
   return new ProgressionConfig(bool(json,"killXpEnabled",true),number(json,"healthMultiplier",3,false),
    bool(json,"includePassiveMobs",false),number(json,"practiceCharacterXpMultiplier",0,true),
    bool(json,"showKillXp",true),excluded);
  }catch(IOException|RuntimeException ignored){return defaults();}
 }
 private static boolean bool(JsonObject json,String key,boolean fallback){
  var v=json.get(key);return v!=null&&v.isJsonPrimitive()&&v.getAsJsonPrimitive().isBoolean()?v.getAsBoolean():fallback;
 }
 private static double number(JsonObject json,String key,double fallback,boolean allowZero){
  try{double n=json.get(key).getAsDouble();return Double.isFinite(n)&&(allowZero?n>=0:n>0)?n:fallback;}
  catch(RuntimeException ignored){return fallback;}
 }
}

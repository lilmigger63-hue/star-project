package dk.starswordmaster.presentation;

import com.google.gson.*;
import org.junit.Test;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.Assert.*;

public class AnimationResourceTest {
 private static final Path DIRECTORY=Path.of("src/main/resources/assets/starswordmaster/player_animations");
 private static final Set<String> BONES=Set.of("rightArm","leftArm","torso");
 private static final Set<String> AXES=Set.of("pitch","yaw","roll");

 @Test public void everyActiveAbilityHasItsOwnOriginalAnimation()throws Exception{
  assertEquals(32,AbilityPresentation.ALL.size());
  var poses=new HashSet<String>();
  var rhythms=new HashSet<String>();
  for(var entry:AbilityPresentation.ALL.entrySet()){
   String id="skill_"+entry.getKey();
   assertEquals("Catalogue must select its dedicated clip",id,entry.getValue().animationId());
   var json=read(id);
   assertEquals(id,json.get("name").getAsString());
   var frames=json.getAsJsonObject("emote").getAsJsonArray("moves");
   assertTrue(id+" needs anticipation, action, follow-through and recovery",frames.size()>=6);
   assertTrue(id+" repeats another animation's poses up to scale",poses.add(poseFingerprint(frames)));
   assertTrue(id+" repeats another animation's rhythm",rhythms.add(frames.asList().stream()
     .map(frame->frame.getAsJsonObject().get("tick").getAsString()).collect(Collectors.joining(","))));
  }
 }

 @Test public void allShippedClipsAreFiniteUpperBodyOnlyNonLoopingAndReturnToNeutral()throws Exception{
  var durations=new LinkedHashMap<String,Integer>();
  for(var clip:AbilityPresentation.Clip.values())durations.put(clip.id(),clip.ticks());
  AbilityPresentation.ALL.forEach((id,p)->durations.put("skill_"+id,p.ticks()));
  try(var paths=Files.list(DIRECTORY)){
   assertEquals("Only declared original clips should ship",durations.keySet(),paths.filter(p->p.toString().endsWith(".json"))
     .map(p->p.getFileName().toString().replace(".json","")).collect(Collectors.toSet()));
  }
  for(var entry:durations.entrySet())validateClip(entry.getKey(),entry.getValue());
 }

 private static JsonObject read(String id)throws Exception{
  var path=DIRECTORY.resolve(id+".json");
  assertTrue("Missing clip "+id,Files.isRegularFile(path));
  return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
 }

 private static void validateClip(String id,int duration)throws Exception{
  var json=read(id);
  assertEquals(id,json.get("name").getAsString());
  assertEquals("PAL torso semantics require format v3",3,json.get("version").getAsInt());
  var emote=json.getAsJsonObject("emote");
  assertFalse(id,emote.get("isLoop").getAsBoolean());
  assertTrue(id,emote.get("degrees").getAsBoolean());
  assertEquals(id,duration,emote.get("stopTick").getAsInt());
  assertEquals(id,0,emote.get("beginTick").getAsInt());
  assertEquals(id,0,emote.get("returnTick").getAsInt());
  int end=emote.get("endTick").getAsInt();
  assertTrue(id+" action must end before recovery",end>0&&end<duration);
  // Extra emote keys can disable locomotion or impose a global rotation.
  assertEquals(id,Set.of("isLoop","degrees","stopTick","beginTick","returnTick","endTick","moves"),emote.keySet());
  var neutral=new HashSet<String>();
  int previous=-1;
  for(var frame:emote.getAsJsonArray("moves")){
   var move=frame.getAsJsonObject();
   int tick=move.get("tick").getAsInt();
   assertTrue(id+" invalid or unordered tick "+tick,tick>=previous&&tick>=0&&tick<=duration);
   previous=tick;
   for(var field:move.entrySet()){
    if(field.getKey().equals("tick")||field.getKey().equals("easing"))continue;
    assertTrue(id+" forbidden bone or movement option: "+field.getKey(),BONES.contains(field.getKey()));
    var bone=field.getValue().getAsJsonObject();
    assertEquals(id+" must animate rotation only",AXES,bone.keySet());
    for(var value:bone.entrySet()){
     double angle=value.getValue().getAsDouble();
     assertTrue(id+" invalid angle",Double.isFinite(angle)&&Math.abs(angle)<=160);
     if(field.getKey().equals("torso")&&id.startsWith("skill_"))assertTrue(id+" torso must preserve aim",Math.abs(angle)<=28);
     if(tick==duration)assertEquals(id+" must finish neutral",0,angle,0);
    }
    if(tick==duration)neutral.add(field.getKey());
   }
  }
  assertEquals(id+" must explicitly release every animated bone",BONES,neutral);
 }

 private static String poseFingerprint(JsonArray frames){
  var values=new ArrayList<Double>();
  for(var frame:frames){
   var move=frame.getAsJsonObject();
   for(String bone:List.of("rightArm","leftArm","torso"))if(move.has(bone))
    for(String axis:List.of("pitch","yaw","roll"))values.add(move.getAsJsonObject(bone).get(axis).getAsDouble());
  }
  double scale=values.stream().mapToDouble(Math::abs).max().orElse(1);
  assertTrue("Animation needs a non-neutral action",scale>0);
  return values.stream().map(value->Long.toString(Math.round(value/scale*1_000_000))).collect(Collectors.joining(","));
 }
}

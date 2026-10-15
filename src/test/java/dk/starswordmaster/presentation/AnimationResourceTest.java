package dk.starswordmaster.presentation;
import com.google.gson.*;
import org.junit.Test;
import java.nio.file.*;
import static org.junit.Assert.*;
public class AnimationResourceTest {
 @Test public void everyClipIsOriginalFiniteNonLoopingAndReturnsToNeutral()throws Exception{
  for(var clip:AbilityPresentation.Clip.values()){
   var path=Path.of("src/main/resources/assets/starswordmaster/player_animations",clip.id()+".json");
   assertTrue("Missing clip "+clip,path.toFile().isFile());
   var json=JsonParser.parseString(Files.readString(path)).getAsJsonObject();assertEquals(clip.id(),json.get("name").getAsString());
   assertEquals("PAL torso semantics require format v3",3,json.get("version")==null?1:json.get("version").getAsInt());
   var e=json.getAsJsonObject("emote");assertFalse(e.get("isLoop").getAsBoolean());assertEquals(clip.ticks(),e.get("stopTick").getAsInt());
   for(var m:e.getAsJsonArray("moves")){
    var move=m.getAsJsonObject();int tick=move.get("tick").getAsInt();assertTrue(tick>=0&&tick<=clip.ticks());
    for(String bone:new String[]{"rightArm","leftArm","torso"})if(move.has(bone))for(var value:move.getAsJsonObject(bone).entrySet()){
     double n=value.getValue().getAsDouble();assertTrue(Double.isFinite(n)&&Math.abs(n)<=160);
     if(tick==clip.ticks())assertEquals(0,n,0);
    }
   }
  }
 }
}

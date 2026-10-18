package dk.starswordmaster.progression;

import org.junit.Test;
import java.nio.file.Files;
import java.util.Set;
import static org.junit.Assert.*;

public class ProgressionConfigTest {
 @Test public void defaultsAndMissingFileAreSafe() throws Exception {
  var missing=Files.createTempDirectory("progression-config").resolve("missing.json");
  var c=ProgressionConfig.load(missing);
  assertTrue(c.killXpEnabled());assertEquals(3,c.healthMultiplier(),0);assertFalse(c.includePassiveMobs());
  assertEquals(0,c.practiceCharacterXpMultiplier(),0);assertTrue(c.showKillXp());assertTrue(c.excludedEntityIds().isEmpty());
 }
 @Test public void optionalSettingsAndExclusionsCanBeLoaded() throws Exception {
  var path=Files.createTempFile("progression-config",".json");
  Files.writeString(path,"{\"killXpEnabled\":false,\"healthMultiplier\":4.5,\"includePassiveMobs\":true,\"practiceCharacterXpMultiplier\":0.25,\"showKillXp\":false,\"excludedEntityIds\":[\"minecraft:zombie\"]}");
  var c=ProgressionConfig.load(path);assertFalse(c.killXpEnabled());assertEquals(4.5,c.healthMultiplier(),0);
  assertTrue(c.includePassiveMobs());assertEquals(0.25,c.practiceCharacterXpMultiplier(),0);
  assertFalse(c.showKillXp());assertEquals(Set.of("minecraft:zombie"),c.excludedEntityIds());
 }
 @Test public void malformedFieldsFallBackIndependentlyWithoutCrashing() throws Exception {
  var path=Files.createTempFile("progression-config",".json");
  Files.writeString(path,"{\"healthMultiplier\":\"NaN\",\"includePassiveMobs\":true,\"practiceCharacterXpMultiplier\":-1,\"excludedEntityIds\":null}");
  var c=ProgressionConfig.load(path);assertEquals(3,c.healthMultiplier(),0);assertEquals(0,c.practiceCharacterXpMultiplier(),0);
  assertTrue(c.includePassiveMobs());assertTrue(c.excludedEntityIds().isEmpty());
  Files.writeString(path,"{broken");assertEquals(ProgressionConfig.defaults(),ProgressionConfig.load(path));
 }
}

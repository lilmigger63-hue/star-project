package dk.starswordmaster.progression;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.imageio.ImageIO;
import java.nio.file.*;
import java.util.*;
public class RankResourcesTest {
 private final Path root=Path.of("src/main/resources/assets/starswordmaster");
 @Test public void allNewPassivesHaveSharp32PixelArtAndBothTranslations() throws Exception {
  Set<Integer> artwork=new HashSet<>();int count=0;
  String da=Files.readString(root.resolve("lang/da_dk.json")),en=Files.readString(root.resolve("lang/en_us.json"));
  for(var a:Ability.ALL.values())if(!a.active()&&(a.characterRequirement()==25||a.statBonus()!=null)){
   var path=root.resolve("textures/skills/"+a.id()+".png");assertTrue(path.toString(),Files.exists(path));var im=ImageIO.read(path.toFile());assertEquals(32,im.getWidth());assertEquals(32,im.getHeight());
   int[] pixels=im.getRGB(0,0,32,32,null,0,32);assertTrue("unique "+a.id(),artwork.add(Arrays.hashCode(pixels)));assertTrue(Arrays.stream(pixels).distinct().count()>8);
   assertTrue(Files.readString(Path.of(path+".mcmeta")).contains("\"blur\": false"));
   for(String lang:new String[]{da,en}){assertTrue(lang.contains("\"ability.starswordmaster."+a.id()+"\""));assertTrue(lang.contains("\"description.starswordmaster."+a.id()+"\""));}count++;
  }
  assertEquals(18,count);
 }
}

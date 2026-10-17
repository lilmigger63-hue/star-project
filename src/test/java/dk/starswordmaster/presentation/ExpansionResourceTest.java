package dk.starswordmaster.presentation;

import com.google.gson.JsonParser;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/** Independent resource checks also runnable without the Minecraft runtime. */
public class ExpansionResourceTest {
 private static final Path ROOT=Path.of("src/main/resources/assets/starswordmaster");
 private static final String[] IDS={"static_spark","chain_lightning","thunder_step","heavens_wrath",
  "water_bolt","tidal_wave","aqua_blade","ocean_prison","stone_spike","earthen_wall","quake_blade","worldbreaker",
  "shadow_bolt","curse_mark","shadow_edge","eclipse_nova","astral_crescent","falling_star","phantom_blades",
  "celestial_rush","sovereign_judgment","heavenly_sword_domain"};
 private static final int[] TICKS={12,20,12,30,14,22,16,26,14,20,22,30,14,18,16,30,16,22,20,14,28,32};

 @Test public void allExpansionPerformancesKeepTheirAuthoredTiming()throws Exception {
  for(int i=0;i<IDS.length;i++) {
   var json=JsonParser.parseString(Files.readString(ROOT.resolve("player_animations/skill_"+IDS[i]+".json"))).getAsJsonObject();
   assertEquals("skill_"+IDS[i],json.get("name").getAsString());
   assertEquals(3,json.get("version").getAsInt());
   assertEquals(IDS[i],TICKS[i],json.getAsJsonObject("emote").get("stopTick").getAsInt());
  }
 }

 @Test public void eightReusableStylesShipWithTheirOwnChoreography()throws Exception {
  String[] styles={"dual_cast","ground_slam","overhead_cast","sword_release","lightning_channel","water_weave","earth_raise","shadow_summon"};
  int[] ticks={20,22,30,16,20,22,20,26};
  var poses=new HashSet<String>();
  for(int i=0;i<styles.length;i++) {
   var json=JsonParser.parseString(Files.readString(ROOT.resolve("player_animations/style_"+styles[i]+".json"))).getAsJsonObject();
   assertEquals(ticks[i],json.getAsJsonObject("emote").get("stopTick").getAsInt());
   var moves=json.getAsJsonObject("emote").getAsJsonArray("moves");
   assertTrue("Anticipation, release and recovery",moves.size()>=7);
   assertTrue("Distinct reusable style",poses.add(moves.toString()));
  }
 }

 @Test public void twentyThreeIconsAreOriginalSharpAndVisuallyDistinct()throws Exception {
  var ids=new ArrayList<>(List.of(IDS));ids.add("aura_sense");
  var images=new HashSet<String>();
  for(String id:ids) {
   var image=ImageIO.read(ROOT.resolve("textures/skills/"+id+".png").toFile());
   assertNotNull(id,image);
   assertEquals(id,32,image.getWidth());assertEquals(id,32,image.getHeight());
   var colors=new HashSet<Integer>();var pixels=new StringBuilder();
   for(int y=0;y<32;y++)for(int x=0;x<32;x++) {
    int rgba=image.getRGB(x,y);int alpha=rgba>>>24;
    assertTrue("No blurred pixels: "+id,alpha==0||alpha==255);
    if(alpha!=0)colors.add(rgba);
    pixels.append(rgba).append(',');
   }
   assertTrue("Readable shaded symbol: "+id,colors.size()>=5);
   assertTrue("Individual skill identity: "+id,images.add(pixels.toString()));
  }
 }
}

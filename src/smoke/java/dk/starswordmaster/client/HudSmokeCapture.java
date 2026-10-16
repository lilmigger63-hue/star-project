package dk.starswordmaster.client;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;
import java.util.*;
/** Observe actual native texture vertices in isolated HUD tests, not just the layout model. */
public final class HudSmokeCapture {
 public record Rect(float x,float y,int width,int height){}
 public static final Map<String,List<Rect>> textures=new HashMap<>();
 public static Rect itemName;
 public static void text(DrawContext c,int x,int y,int width){var point=c.getMatrices().transformPosition(x,y,new Vector2f());itemName=new Rect(point.x,point.y,width,9);}
 public static void capture(DrawContext c,Identifier texture,int x,int y,int width,int height){
  if(!Boolean.getBoolean("starsmoke.hud"))return;
  var point=c.getMatrices().transformPosition(x,y,new Vector2f());
  textures.computeIfAbsent(texture.getPath(),key->new ArrayList<>()).add(new Rect(point.x,point.y,width,height));
 }
}

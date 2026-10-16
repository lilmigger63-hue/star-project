package dk.starswordmaster.client;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;
import java.util.*;
/** Observe actual native texture vertices in isolated HUD tests, not just the layout model. */
public final class HudSmokeCapture {
 public record Rect(float x,float y,float width,float height){}
 public static final Map<String,List<Rect>> textures=new HashMap<>();
 public static Rect itemName,skillBar,experienceLevel;
 public static void text(DrawContext c,int x,int y,int width){var point=c.getMatrices().transformPosition(x,y,new Vector2f());itemName=new Rect(point.x,point.y,width,9);}
 public static void capture(DrawContext c,Identifier texture,int x,int y,int width,int height){
  if(!Boolean.getBoolean("starsmoke.hud"))return;
  var point=c.getMatrices().transformPosition(x,y,new Vector2f());
  var end=c.getMatrices().transformPosition(x+width,y+height,new Vector2f());
  textures.computeIfAbsent(texture.getPath(),key->new ArrayList<>()).add(new Rect(point.x,point.y,end.x-point.x,end.y-point.y));
 }
 public static void fill(DrawContext c,int x1,int y1,int x2,int y2,int color){
  if(!Boolean.getBoolean("starsmoke.hud")||x2-x1!=182||y2-y1!=22||color!=0xFF536273)return;
  var start=c.getMatrices().transformPosition(x1,y1,new Vector2f());var end=c.getMatrices().transformPosition(x2,y2,new Vector2f());skillBar=new Rect(start.x,start.y,end.x-start.x,end.y-start.y);
 }
 public static void experience(DrawContext c,int x,int y,int width){var point=c.getMatrices().transformPosition(x,y,new Vector2f());experienceLevel=new Rect(point.x,point.y,width,9);}
}

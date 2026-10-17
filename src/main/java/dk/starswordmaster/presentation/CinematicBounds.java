package dk.starswordmaster.presentation;
import java.util.List;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
/** Compute the full authoritative path once, including later chain targets. */
public final class CinematicBounds {
 private CinematicBounds(){}
 public static Box ray(Vec3d origin,Vec3d impact,List<Vec3d> impacts){
  var box=new Box(origin.add(0,1,0),impact).expand(2);
  for(var point:impacts)box=box.union(new Box(point,point).expand(2));
  return box;
 }
}

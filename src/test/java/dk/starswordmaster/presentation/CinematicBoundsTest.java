package dk.starswordmaster.presentation;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import net.minecraft.util.math.Vec3d;
public class CinematicBoundsTest {
 @Test public void includesEveryLaterChainTargetOutsideFirstSegment(){
  var origin=new Vec3d(0,80,0);var first=new Vec3d(0,81,8);var later=new Vec3d(12,86,6);
  var b=CinematicBounds.ray(origin,first,List.of(first,later));assertTrue(b.contains(later));assertTrue(b.contains(new Vec3d(13.9,87.9,7.9)));assertTrue(b.contains(origin.add(0,1,0)));
 }
 @Test public void singleTargetRetainsItsReadableWidth(){var b=CinematicBounds.ray(Vec3d.ZERO,new Vec3d(0,0,10),List.of());assertEquals(-2,b.minX,0);assertEquals(2,b.maxX,0);assertEquals(12,b.maxZ,0);}
}

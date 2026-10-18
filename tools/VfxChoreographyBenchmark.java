import dk.starswordmaster.presentation.*;
/** CPU-only cache/timeline stress; not a Minecraft FPS, networking or GPU benchmark. */
public final class VfxChoreographyBenchmark {
 private static final CinematicGeometry.Mesh[] MESHES=CinematicGeometry.ALL.values().toArray(CinematicGeometry.Mesh[]::new);
 private static final WorldVfxBudget BUDGET=new WorldVfxBudget();
 private static double run(int frames){
  double sum=0;
  for(int frame=0;frame<frames;frame++){
   BUDGET.reset();
   for(int caster=0;caster<64;caster++){
    var mesh=MESHES[(frame+caster)%MESHES.length];int quality=caster%3,count=0;
    for(var part:mesh.parts())if(part.minQuality()<=quality)count+=part.faces().size();
    if(!BUDGET.takeCinematic(count))continue;
    for(var part:mesh.parts())if(part.minQuality()<=quality){var t=AbilityVfxTimeline.sample(frame%(mesh.ticks()+8),mesh.ticks(),part.delay());sum+=t.alpha()*part.faces().size()+t.travel();}
   }
   if(BUDGET.used()>4096)throw new AssertionError("frame budget");
  }
  return sum;
 }
 public static void main(String[] args){
  run(5000);
  for(int i=0;i<3;i++){long at=System.nanoTime();double checksum=run(10000);double ms=(System.nanoTime()-at)/1e6;System.out.printf(java.util.Locale.ROOT,"64 candidate casters x 10000 frames: %.3f ms, %.3f us/frame, checksum %.3f%n",ms,ms/10,checksum);}
 }
}

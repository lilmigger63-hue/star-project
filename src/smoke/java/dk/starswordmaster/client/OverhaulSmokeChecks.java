package dk.starswordmaster.client;

import dk.starswordmaster.presentation.*;
import java.util.*;
import net.minecraft.client.MinecraftClient;

/** Assertions over real native cinematic submissions; smoke artifact only. */
public final class OverhaulSmokeChecks {
 public static final List<String> ULTIMATES=List.of("worldbreaker","heavens_wrath","eclipse_nova","heavenly_sword_domain","sovereign_judgment");
 private static final Set<String> VERIFIED=new HashSet<>();private static int checks;
 private OverhaulSmokeChecks(){}
 private static void check(boolean value,String message){if(!value)throw new AssertionError("OVERHAUL FAIL: "+message);checks++;System.out.println("OVERHAUL PASS: "+message);}
 public static void beforeCast(){OverhaulSmokeCapture.reset();}
 public static int verify(String id){
  int before=checks;var o=OverhaulSmokeCapture.recent(id);check(o!=null&&o.vertices>0,id+" real cinematic VertexConsumer submission");
  check(o.finite&&o.bounded,id+" finite world vertices within bounded confirmed-event arena");
  check(o.vertices==o.normals&&o.normal,id+" every actual cinematic vertex has finite unit normal");
  check(o.alpha,id+" actual translucency stays within 0..220");check(o.expectedColors.containsAll(o.colors),id+" actual facets retain their confirmed elemental/personal color palette");
  check(o.noncoplanar,id+" submitted geometry occupies a noncoplanar 3D volume");
  check(o.qualities.contains(VfxQuality.level()),id+" actual selected quality "+VfxQuality.level());
  check(OverhaulSmokeCapture.maximumFrameVertices<=CinematicGeometry.FRAME_FACES*4,"native cinematic frame vertex budget");
  VERIFIED.add(id);return checks-before;
 }
 public static int verifyTarget(MinecraftClient client,boolean missing){
  int before=checks;
  try{
   var sf=CinematicVfxRenderer.class.getDeclaredField("scenes");sf.setAccessible(true);
   Object scene=((Map<?,?>)sf.get(null)).get(client.player.getUuid());check(scene!=null,"confirmed target scene remains active");
   var ef=scene.getClass().getDeclaredField("event");ef.setAccessible(true);var e=(dk.starswordmaster.network.AbilityPresentationPayload)ef.get(scene);
   var af=CinematicVfxRenderer.class.getDeclaredMethod("anchor",scene.getClass(),float.class);af.setAccessible(true);
   var at=(net.minecraft.util.math.Vec3d)af.invoke(null,scene,1f);
   if(missing)check(at.equals(e.impact()),"removed visual target safely falls back to confirmed hit position");
   else{var tf=scene.getClass().getDeclaredField("target");tf.setAccessible(true);var target=(net.minecraft.entity.LivingEntity)tf.get(scene);
    check(target!=null&&target.isAlive()&&target.getId()==e.targetId(),"visual target uses exact server-confirmed entity identity");
    check(Math.abs(target.getX()-e.impact().x)>.8,"target actually moved through the server before verification");
    var of=scene.getClass().getDeclaredField("targetOffset");of.setAccessible(true);var offset=(net.minecraft.util.math.Vec3d)of.get(scene);
    check(at.squaredDistanceTo(target.getLerpedPos(1).add(offset))<.000001,"native presentation follows synchronized moving target");}
  }catch(ReflectiveOperationException error){throw new AssertionError(error);}
  return checks-before;
 }
 public static int completeShowcase(){
  int before=checks;var all=OverhaulSmokeCapture.all(false);
  for(var id:ULTIMATES){var o=all.get(id);check(VERIFIED.contains(id)&&o!=null&&o.qualities.containsAll(Set.of(0,1,2)),id+" final native Low/Normal/Ultra showcase coverage");check(o.finite&&o.normal&&o.noncoplanar,"final showcase geometry is finite and volumetric");}
  return checks-before;
 }
 public static int complete(boolean remote){
  int before=checks;var all=OverhaulSmokeCapture.all(remote);String who=remote?"observer":"caster";
  check(all.keySet().containsAll(AbilityPresentation.ALL.keySet()),who+" actual native geometry for all 54 confirmed abilities");
  check(all.values().stream().allMatch(o->o.vertices>0&&o.finite&&o.bounded&&o.normal&&o.alpha&&o.normals==o.vertices&&o.noncoplanar),who+" all 54 native mesh depth, transforms, normals and alpha validated");
  var stages=new HashSet<String>();all.values().forEach(o->stages.addAll(o.stages));
  check(stages.containsAll(Set.of("ANTICIPATION","ACTIVATION","PRIMARY","IMPACT","DISSIPATION")),who+" all five real rendered timeline stages observed: "+stages);
  if(!remote){
   check(VERIFIED.containsAll(AbilityPresentation.ALL.keySet()),"all 54 local confirmed casts verified individually");
   for(var id:ULTIMATES)check(all.get(id).qualities.containsAll(Set.of(0,1,2)),id+" real native Low/Normal/Ultra submissions");
  }
  return checks-before;
 }
 /** Set only the local smoke client's option, without writing production config files. */
 public static void quality(int quality){try{var f=VfxQuality.class.getDeclaredField("quality");f.setAccessible(true);f.setInt(null,quality);}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
}

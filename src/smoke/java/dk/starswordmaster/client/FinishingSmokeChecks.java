package dk.starswordmaster.client;

import dk.starswordmaster.presentation.*;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;

/** Native finishing assertions called by the existing real-world animation suite. Smoke jar only. */
public final class FinishingSmokeChecks {
 private static final EnumSet<AbilityPresentation.Element> ELEMENTS=EnumSet.noneOf(AbilityPresentation.Element.class);
 private static final EnumSet<EarthFormationMesh.Kind> EARTH=EnumSet.noneOf(EarthFormationMesh.Kind.class);
 private static final Set<String> HANDS=new HashSet<>();
 private static final Set<String> ELEMENT_HANDS=new HashSet<>();
 private static final Map<BlockPos,Integer> BLOCKS=new LinkedHashMap<>();
 private static int checks;
 private FinishingSmokeChecks(){}
 private static void check(boolean condition,String message){if(!condition)throw new AssertionError("FINISHING FAIL: "+message);checks++;System.out.println("FINISHING PASS: "+message);}
 public static void beforeCast(MinecraftClient client){
  CircleSmokeCapture.reset();EarthSmokeCapture.reset();BLOCKS.clear();
  if(client.world==null)return;
  // The smoke arena is finite and known: hash native loaded platform and air states, without creating chunks.
  for(int x=-18;x<=30;x+=2)for(int z=-18;z<=18;z+=2)if(client.world.isChunkLoaded(x>>4,z>>4))for(int y=78;y<=82;y++){
   var pos=new BlockPos(x,y,z);BLOCKS.put(pos,Block.getRawIdFromState(client.world.getBlockState(pos)));
  }
 }
 public static int verify(MinecraftClient client,String ability){
  int before=checks;var style=AbilityPresentation.ALL.get(ability);check(style!=null,"known confirmed ability "+ability);
  if(style.element()!=AbilityPresentation.Element.STEEL){
   var draws=CircleSmokeCapture.recent().stream().filter(CircleSmokeCapture.Draw::local).toList();
   check(!draws.isEmpty(),ability+" emitted actual native palm-circle vertices");
   check(draws.stream().allMatch(d->d.finite()&&d.vertices()>=4&&d.vertices()<=12),ability+" finite bounded circle vertices");
   boolean first=client.options.getPerspective().isFirstPerson();int dominant=client.player.getMainArm()==Arm.LEFT?0:1;
   check(draws.stream().allMatch(d->d.firstPerson()==first),ability+" native circle perspective matches camera");
   var hands=new HashSet<Integer>();draws.forEach(d->hands.add(d.hand()));
   Set<Integer> expected=dual(style.clip())?Set.of(0,1):Set.of(dominant);
   check(hands.equals(expected),ability+" actual native hands "+hands+" expected "+expected);
   var alignments=CircleSmokeCapture.alignments().stream().filter(a->a.entityId()==client.player.getId()).toList();
   check(!alignments.isEmpty()&&alignments.stream().allMatch(a->a.matrixError()<.001f&&a.centerError()<.001f),ability+" exact PAL/native arm matrix alignment");
   check(CircleSmokeCapture.unmatchedNativeArms==0&&CircleSmokeCapture.pending()==0,ability+" all extracted circles matched real native arm draws");
   ELEMENTS.add(style.element());String handView=(first?"first":"third")+(dominant==0?"Left":"Right");HANDS.add(handView);ELEMENT_HANDS.add(style.element().name()+handView);
  }
  var kind=EarthFormationMesh.kind(ability);
  if(kind!=null){
   var frames=EarthSmokeCapture.recent();
   check(!frames.isEmpty()&&frames.stream().anyMatch(f->f.vertices()>0),ability+" submitted actual opaque terrain mesh vertices");
   check(frames.stream().allMatch(f->f.finite()&&f.opaque()&&f.normals()==f.vertices()),ability+" finite lit opaque native stone faces");
   check(frames.stream().allMatch(EarthSmokeCapture.Frame::noncoplanar),ability+" native geometry occupies a noncoplanar 3D volume");
   check(frames.stream().allMatch(f->f.groundError()<.001&&f.transformError()<.001&&f.drift()<.001),ability+" native collision ground and animated vertices stay anchored");
   check(frames.stream().anyMatch(f->f.maxY()>80.1),ability+" actual spike/pillar tips rise visibly above arena ground");
   check(EarthSmokeCapture.maximumFrameVertices<=EarthFormationMesh.MAX_FRAME_QUADS*4,ability+" native terrain submission stays within global vertex cap");
   EARTH.add(kind);
  }
  check(!BLOCKS.isEmpty()&&BLOCKS.entrySet().stream().allMatch(e->client.world.isChunkLoaded(e.getKey().getX()>>4,e.getKey().getZ()>>4)&&e.getValue()==Block.getRawIdFromState(client.world.getBlockState(e.getKey()))),ability+" loaded native arena block states unchanged");
  return checks-before;
 }
 public static int expired(MinecraftClient client){
  int before=checks;
  try{
   for(var type:List.of(EarthFormationRenderer.class,CinematicVfxRenderer.class)){
    var field=type.getDeclaredField(type==EarthFormationRenderer.class?"ACTIVE":"scenes");field.setAccessible(true);
    check(!((Map<?,?>)field.get(null)).containsKey(client.player.getUuid()),type.getSimpleName()+" clears expired/interrupted caster state");
   }
  }catch(ReflectiveOperationException error){throw new AssertionError(error);}
  check(AbilityPresentationClient.visual(client.player.getUuid())==null&&CircleSmokeCapture.pending()==0,"interruption/expiry leaves no hand-circle state");return checks-before;
 }
 public static int complete(){
  int before=checks;var expected=EnumSet.allOf(AbilityPresentation.Element.class);expected.remove(AbilityPresentation.Element.STEEL);
  check(ELEMENTS.equals(expected),"all eight magical element circle renderers observed: "+ELEMENTS);
  check(EARTH.size()==EarthFormationMesh.Kind.values().length,"all four actual terrain formations observed: "+EARTH);
  check(HANDS.containsAll(Set.of("firstRight","firstLeft","thirdRight","thirdLeft")),"native first/third person and both dominant arms verified: "+HANDS);
  if(Boolean.getBoolean("starsmoke.overhaul"))for(var element:expected)for(String hand:List.of("firstRight","firstLeft","thirdRight","thirdLeft"))check(ELEMENT_HANDS.contains(element.name()+hand),"actual native circle coverage "+element+" / "+hand);
  return checks-before;
 }
 private static boolean dual(AbilityPresentation.Clip clip){return switch(clip){case DUAL_CAST,WATER_WEAVE,HEAL,CHANNEL,LIGHTNING_CHANNEL,EARTH_RAISE,SHADOW_SUMMON,OVERHEAD_CAST,AURA,GUARD,GROUND_SLAM->true;default->false;};}
}

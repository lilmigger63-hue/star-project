package dk.starswordmaster.client;
import dk.starswordmaster.client.aura.AuraRenderLayer;
import dk.starswordmaster.network.AbilityPresentationPayload;
import dk.starswordmaster.presentation.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.*;
/** Batched, static-mesh spell silhouettes complement the existing sparse particles and native hand sigils. */
public final class CinematicVfxRenderer {
 private static final Map<UUID,Scene> scenes=new LinkedHashMap<>();
 private static List<Draw> frame=List.of();
 private static ClientWorld world;private static BufferAllocator allocator;private static boolean registered;
 private static int frameFaces;
 private static final class Scene {
  final AbilityPresentationPayload event;final CinematicGeometry.Mesh mesh;final PlayerEntity player;final ItemStack weapon;final float yaw;final Box rayBounds;
  int hurt;boolean swing;
  Scene(AbilityPresentationPayload event,PlayerEntity player){this.event=event;this.player=player;mesh=CinematicGeometry.ALL.get(event.ability());weapon=player.getMainHandStack().copy();yaw=player.getYaw();rayBounds=CinematicBounds.ray(event.origin(),event.impact(),event.impacts());hurt=player.hurtTime;swing=player.handSwinging;}
 }
 private record Draw(Scene scene,float age,float alpha,float size,int stride){}
 private CinematicVfxRenderer(){}
 public static void register(){
  if(registered)return;registered=true;
  ClientTickEvents.END_CLIENT_TICK.register(CinematicVfxRenderer::tick);
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->clear());
  ClientLifecycleEvents.CLIENT_STOPPING.register(client->{clear();if(allocator!=null){allocator.close();allocator=null;}});
  WorldRenderEvents.END_EXTRACTION.register(CinematicVfxRenderer::extract);
  WorldRenderEvents.END_MAIN.register(CinematicVfxRenderer::draw);
 }
 public static void start(AbilityPresentationPayload event,PlayerEntity player){
  var client=MinecraftClient.getInstance();if(client.world==null||!CinematicGeometry.ALL.containsKey(event.ability()))return;
  if(world!=client.world){clear();world=client.world;}
  if(scenes.size()>=ParticleBudget.MAX_CASTERS)scenes.remove(scenes.keySet().iterator().next());
  scenes.put(event.caster(),new Scene(event,player));
 }
 public static void cancel(UUID id){scenes.remove(id);frame=List.of();}
 public static void clear(){scenes.clear();frame=List.of();world=null;frameFaces=0;}
 private static void tick(MinecraftClient client){
  if(world!=client.world){clear();return;}if(world==null||client.isPaused())return;
  scenes.values().removeIf(s->{
   var p=s.player;
   if(world.getEntityById(s.event.entityId())!=p||!p.isAlive()||p.isSpectator()||world.getTime()-s.event.sentAt()>=s.mesh.ticks()+8)return true;
   boolean interrupted=p.hurtTime>s.hurt||!ItemStack.areItemsAndComponentsEqual(s.weapon,p.getMainHandStack())||(p.handSwinging&&!s.swing)||PalAbilityAnimations.attackActive(p);
   s.hurt=p.hurtTime;s.swing=p.handSwinging;return interrupted;
  });
 }
 private static void extract(WorldExtractionContext context){
  frame=List.of();frameFaces=0;if(context.world()!=world||world==null||scenes.isEmpty())return;
  var client=MinecraftClient.getInstance();int quality=Math.min(VfxQuality.level(),2-client.options.getParticles().getValue().getId());
  var camera=context.camera().getPos();float partial=context.tickCounter().getTickProgress(false);var result=new ArrayList<Draw>();
  for(var scene:scenes.values()){
   var mesh=scene.mesh;var e=scene.event;float age=world.getTime()-e.sentAt()+partial,alpha=CinematicGeometry.envelope(age,mesh.ticks());
   if(alpha<.01f)continue;
   Vec3d anchor=switch(mesh.anchor()){case IMPACT->e.impact();case CASTER->scene.player.getLerpedPos(partial).add(0,1,0);default->e.origin().add(0,1,0);};
   double distance=camera.squaredDistanceTo(anchor);if(distance>4096)continue;
   var box=mesh.anchor()==CinematicGeometry.Anchor.RAY?scene.rayBounds:new Box(anchor,anchor).expand(10);
   if(!context.frustum().isVisible(box))continue;
   int stride=quality==0||distance>1024?3:quality==1||distance>256?2:1;
   int paths=mesh.anchor()==CinematicGeometry.Anchor.RAY&&mesh.form()==CinematicGeometry.Form.BOLT?Math.max(1,e.impacts().size()):1;
   int faces=(mesh.faces().size()+stride-1)/stride*paths;
   if(frameFaces+faces>CinematicGeometry.FRAME_FACES)continue;
   frameFaces+=faces;
   float power=e.visual().power(),rank=e.visual().rank();
   float size=mesh.anchor()==CinematicGeometry.Anchor.RAY?.8f+.65f*power:Math.min(8,mesh.radius()*(.65f+.65f*power+.055f*rank));
   result.add(new Draw(scene,age,alpha,size,stride));
  }
  frame=List.copyOf(result);
 }
 private static void draw(WorldRenderContext context){
  if(frame.isEmpty())return;
  if(allocator==null)allocator=new BufferAllocator(64*1024);
  var consumers=VertexConsumerProvider.immediate(allocator);var layer=AuraRenderLayer.get();var vertices=consumers.getBuffer(layer);
  var camera=context.worldState().cameraRenderState.pos;var matrices=context.matrices();
  for(var draw:frame){
   var mesh=draw.scene.mesh;var event=draw.scene.event;
   if(mesh.anchor()==CinematicGeometry.Anchor.RAY){
    var start=event.origin().add(0,1.25,0);var ends=mesh.form()==CinematicGeometry.Form.BOLT&&!event.impacts().isEmpty()?event.impacts():List.of(event.impact());
    for(var end:ends){
     Vec3d direction=end.subtract(start);double length=direction.length();if(length<.01)continue;
     matrices.push();matrices.translate(start.x-camera.x,start.y-camera.y,start.z-camera.z);
     matrices.multiply(new Quaternionf().rotationTo(new Vector3f(0,0,1),direction.toVector3f().normalize()));matrices.scale(draw.size,draw.size,(float)length);
     emit(draw,matrices,vertices);matrices.pop();start=end;
    }
   }else{
    Vec3d at=switch(mesh.anchor()){case IMPACT->event.impact();case ORIGIN->event.origin().add(0,1,0);default->draw.scene.player.getLerpedPos(MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false)).add(0,1,0);};
    if(event.ability().equals("curse_mark"))at=at.add(event.origin().add(0,1,0).subtract(at).normalize().multiply(.42));
    float progress=Math.clamp(draw.age/mesh.ticks(),0,1),reveal=.3f+.7f*Math.min(1,progress*2.5f);
    matrices.push();matrices.translate(at.x-camera.x,at.y-camera.y,at.z-camera.z);matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-draw.scene.yaw));
    if(mesh.form()==CinematicGeometry.Form.SHIELD&&mesh.anchor()==CinematicGeometry.Anchor.CASTER)matrices.translate(0,0,.75);
    if(mesh.form()==CinematicGeometry.Form.CRESCENT){matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float)Math.sin(mesh.spin()*23)*.65f));matrices.multiply(RotationAxis.POSITIVE_Y.rotation((progress-.5f)*1.5f));}
    if(mesh.form()==CinematicGeometry.Form.RING||mesh.form()==CinematicGeometry.Form.HALO)matrices.multiply(RotationAxis.POSITIVE_Y.rotation(draw.age*mesh.spin()));
    if(event.ability().equals("heavens_wrath"))matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
    if(mesh.form()==CinematicGeometry.Form.WAVE)matrices.translate(0,-.7,progress*4);
    if(mesh.form()==CinematicGeometry.Form.RING)matrices.translate(0,-.85,0);
    matrices.scale(draw.size*reveal,draw.size*reveal,draw.size*reveal);emit(draw,matrices,vertices);matrices.pop();
   }
  }
  consumers.draw();
 }
 private static void emit(Draw draw,MatrixStack matrices,VertexConsumer vertices){
  var event=draw.scene.event;var style=AbilityPresentation.ALL.get(event.ability());var ability=dk.starswordmaster.progression.Ability.ALL.get(event.ability());boolean personal=event.visual().auraUnlocked()&&ability.stamina()>0;
  var faces=draw.scene.mesh.faces();var matrix=matrices.peek();
  for(int i=0;i<faces.size();i+=draw.stride){
   var face=faces.get(i);int rgb=personal?(face.tone()==0?event.visual().auraColor():AuraColors.highlight(event.visual().auraColor(),event.visual().rank()==4?.85f:.55f)):FantasyPalette.color(style.element(),face.tone());
   int alpha=Math.clamp((int)(face.alpha()*draw.alpha*190),0,220),color=(alpha<<24)|rgb;
   for(int j=0;j<4;j++){var p=face.vertices().get(j);vertices.vertex(matrix,p.x(),p.y(),p.z()).color(color).texture(j==0||j==3?0:1,j<2?0:1).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(matrix,0,0,1);}
  }
 }
}

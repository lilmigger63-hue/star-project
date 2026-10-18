package dk.starswordmaster.client;
import dk.starswordmaster.client.vfx.ConstructRenderLayer;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.*;
/** Cached solid meshes, whole-part LOD and finite choreography from confirmed server events only. */
public final class CinematicVfxRenderer {
 private static final Map<UUID,Scene> scenes=new LinkedHashMap<>();
 private static List<Draw> frame=List.of();
 private static ClientWorld world;private static BufferAllocator allocator;private static boolean registered;
 private static int frameFaces;
 private static final class Scene {
  final AbilityPresentationPayload event;final CinematicGeometry.Mesh mesh;final PlayerEntity player;final ItemStack weapon;final float yaw;final Box rayBounds;
  final List<Vec3d> ends;final boolean personal;final int[] colors=new int[4];final LivingEntity target;final Vec3d targetOffset;int hurt;boolean swing;
  Scene(AbilityPresentationPayload event,PlayerEntity player){
   this.event=event;this.player=player;mesh=CinematicGeometry.ALL.get(event.ability());weapon=player.getMainHandStack().copy();yaw=player.getYaw();
   rayBounds=CinematicBounds.ray(event.origin(),event.impact(),event.impacts()).expand(4);
   ends=mesh.form()==CinematicGeometry.Form.BOLT&&!event.impacts().isEmpty()?event.impacts():List.of(event.impact());
   personal=event.visual().auraUnlocked()&&dk.starswordmaster.progression.Ability.ALL.get(event.ability()).stamina()>0;
   var found=player.getEntityWorld().getEntityById(event.targetId());
   target=found instanceof LivingEntity living?living:null;targetOffset=target==null?Vec3d.ZERO:event.impact().subtract(target.getEntityPos());
   for(int tone=0;tone<colors.length;tone++)colors[tone]=CinematicMaterial.color(AbilityPresentation.ALL.get(event.ability()).element(),tone,personal,event.visual().auraColor(),event.visual().rank());
   hurt=player.hurtTime;swing=player.handSwinging;
  }
 }
 private record Draw(Scene scene,float age,float alpha,float size,int quality,float partial){}
 private CinematicVfxRenderer(){}
 public static void register(){
  if(registered)return;registered=true;
  ClientTickEvents.END_CLIENT_TICK.register(CinematicVfxRenderer::tick);
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->clear());
  ClientLifecycleEvents.CLIENT_STOPPING.register(client->{clear();if(allocator!=null){allocator.close();allocator=null;}});
  WorldRenderEvents.END_EXTRACTION.register(CinematicVfxRenderer::extract);WorldRenderEvents.END_MAIN.register(CinematicVfxRenderer::draw);
 }
 public static void start(AbilityPresentationPayload event,PlayerEntity player){
  var c=MinecraftClient.getInstance();if(c.world==null||!CinematicGeometry.ALL.containsKey(event.ability())||c.world.getEntityById(event.entityId())!=player||!player.getUuid().equals(event.caster()))return;
  if(world!=c.world){clear();world=c.world;}
  if(scenes.size()>=ParticleBudget.MAX_CASTERS&&!scenes.containsKey(event.caster()))scenes.remove(scenes.keySet().iterator().next());
  scenes.put(event.caster(),new Scene(event,player));
 }
 public static void cancel(UUID id){scenes.remove(id);frame=List.of();}
 public static void clear(){scenes.clear();frame=List.of();world=null;frameFaces=0;}
 private static void tick(MinecraftClient c){
  if(world!=c.world){clear();return;}if(world==null||c.isPaused())return;
  scenes.values().removeIf(s->{
   var p=s.player;if(world.getEntityById(s.event.entityId())!=p||!p.isAlive()||p.isSpectator()||world.getTime()-s.event.sentAt()>=s.mesh.ticks()+8)return true;
   boolean interrupted=p.hurtTime>s.hurt||!ItemStack.areItemsAndComponentsEqual(s.weapon,p.getMainHandStack())||(p.handSwinging&&!s.swing)||PalAbilityAnimations.attackActive(p);
   s.hurt=p.hurtTime;s.swing=p.handSwinging;return interrupted;
  });
 }
 private static Vec3d anchor(Scene s,float partial){return switch(s.mesh.anchor()){case IMPACT->s.target!=null&&s.target.isAlive()&&world.getEntityById(s.event.targetId())==s.target?s.target.getLerpedPos(partial).add(s.targetOffset):s.event.impact();case CASTER->s.player.getLerpedPos(partial).add(0,1,0);default->s.event.origin().add(0,1,0);};}
 private static double distance(Box b,Vec3d p){double x=Math.max(b.minX-p.x,Math.max(0,p.x-b.maxX)),y=Math.max(b.minY-p.y,Math.max(0,p.y-b.maxY)),z=Math.max(b.minZ-p.z,Math.max(0,p.z-b.maxZ));return x*x+y*y+z*z;}
 private static void extract(WorldExtractionContext context){
  frame=List.of();frameFaces=0;if(context.world()!=world||world==null||scenes.isEmpty())return;
  var c=MinecraftClient.getInstance();int quality=Math.min(VfxQuality.level(),2-c.options.getParticles().getValue().getId());
  var camera=context.camera().getPos();float partial=context.tickCounter().getTickProgress(false);var draws=new ArrayList<Draw>();
  for(var s:scenes.values()){
   var m=s.mesh;var e=s.event;float age=world.getTime()-e.sentAt()+partial,alpha=AbilityVfxTimeline.sample(age,m.ticks(),0).alpha();if(alpha<.01f)continue;
   float power=e.visual().power(),rank=e.visual().rank();float size=m.anchor()==CinematicGeometry.Anchor.RAY?.8f+.65f*power:Math.min(8,m.radius()*(.65f+.65f*power+.055f*rank));
   var at=anchor(s,partial);Box bounds=m.anchor()==CinematicGeometry.Anchor.RAY?s.rayBounds:new Box(at,at).expand(size*3+2);
   double near=distance(bounds,camera);if(near>4096||!context.frustum().isVisible(bounds))continue;
   double centerDistance=camera.squaredDistanceTo(at);int lod=centerDistance>1024?0:centerDistance>256?Math.min(quality,1):quality;
   // Low keeps complete closed core parts; no arbitrary face skipping or holes in solids.
   int faces=0;for(var part:m.parts())if(part.minQuality()<=lod)faces+=part.faces().size();
   int paths=m.anchor()==CinematicGeometry.Anchor.RAY?s.ends.size():1;faces*=paths;
   if(!WorldVfxBudget.SHARED.takeCinematic(faces))continue;frameFaces+=faces;
   draws.add(new Draw(s,age,alpha,size,lod,partial));
  }
  frame=List.copyOf(draws);
 }
 private static void draw(WorldRenderContext context){
  if(frame.isEmpty())return;if(allocator==null)allocator=new BufferAllocator(64*1024);
  var consumers=VertexConsumerProvider.immediate(allocator);VertexConsumerProvider provider=consumers;var vertices=provider.getBuffer(ConstructRenderLayer.get());var camera=context.worldState().cameraRenderState.pos;var matrices=context.matrices();
  for(var d:frame){var s=d.scene;var m=s.mesh;var e=s.event;var timing=AbilityVfxTimeline.sample(d.age,m.ticks(),0);
   if(m.anchor()==CinematicGeometry.Anchor.RAY){
    Vec3d start=e.origin().add(0,1.25,0);
    for(var end:s.ends){var direction=end.subtract(start);float length=(float)direction.length();if(length<.01f)continue;
     boolean beam=m.form()==CinematicGeometry.Form.BOLT;Vec3d at=beam?start:start.lerp(end,timing.travel());
     matrices.push();matrices.translate(at.x-camera.x,at.y-camera.y,at.z-camera.z);
     matrices.multiply(new Quaternionf().rotationTo(new Vector3f(0,0,1),direction.toVector3f().normalize()));
     matrices.scale(d.size,d.size,beam?length:Math.min(2.4f,length)*(.4f+.6f*timing.release()));emit(d,matrices,vertices);matrices.pop();start=end;
    }
   }else{
    Vec3d at=anchor(s,d.partial);if(e.ability().equals("curse_mark"))at=at.add(e.origin().add(0,1,0).subtract(at).normalize().multiply(.42));
    matrices.push();matrices.translate(at.x-camera.x,at.y-camera.y,at.z-camera.z);matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-s.yaw));
    if(s.player.getMainArm()==Arm.LEFT)matrices.scale(-1,1,1);
    if(m.form()==CinematicGeometry.Form.SHIELD&&m.anchor()==CinematicGeometry.Anchor.CASTER)matrices.translate(0,0,.75);
    if(m.form()==CinematicGeometry.Form.CRESCENT){matrices.multiply(RotationAxis.POSITIVE_Z.rotation(e.ability().equals("heavy_cut")?.95f:.12f));matrices.multiply(RotationAxis.POSITIVE_Y.rotation((timing.travel()-.5f)*1.45f));}
    if(m.form()==CinematicGeometry.Form.WAVE)matrices.translate(0,-.7,timing.travel()*4);
    if(m.form()==CinematicGeometry.Form.RING)matrices.translate(0,-.85,0);
    matrices.scale(d.size,d.size,d.size);emit(d,matrices,vertices);matrices.pop();
   }
  }consumers.draw();
 }
 private static void emit(Draw draw,MatrixStack matrices,VertexConsumer vertices){
  var scene=draw.scene;var e=scene.event;var mesh=scene.mesh;var style=AbilityPresentation.ALL.get(e.ability());var normal=new Vector3f();
  boolean first=scene.player==MinecraftClient.getInstance().player&&MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
  for(var part:mesh.parts()){
   if(part.minQuality()>draw.quality)continue;var t=AbilityVfxTimeline.sample(draw.age,mesh.ticks(),part.delay());if(t.alpha()<.005f)continue;
   matrices.push();float open=.24f+.76f*t.release(),travel=t.travel(),collapse=t.collapse();
   switch(part.motion()){
    case STILL->matrices.scale(open,open,open);
    case ORBIT->{matrices.multiply(RotationAxis.POSITIVE_Y.rotation(draw.age*mesh.spin()));matrices.scale(open,open,open);}
    case RISE->matrices.translate(0,-.55*(1-t.release())-.4*collapse,0);
    case TRAVEL->{if(mesh.anchor()!=CinematicGeometry.Anchor.RAY)matrices.translate(0,0,travel*.35);matrices.scale(open,open,open);}
    case OPEN->{float spread=.3f+travel*.8f;matrices.scale(spread,Math.max(.2f,1-collapse),spread);}
    case STAGGER->{matrices.scale(open,open,open);matrices.translate(0,0,travel*.25);}
    case PULSE->{float pulse=open*(1+.045f*(float)Math.sin(draw.age*1.7));matrices.scale(pulse,pulse,pulse);}
    case FALL->matrices.translate(.16*collapse,.65*(1-travel)-.65*collapse,.12*collapse);
   }
   var entry=matrices.peek();
   for(var face:part.faces()){
    int rgb=scene.colors[Math.clamp(face.tone(),0,3)];
    float readability=first&&mesh.anchor()==CinematicGeometry.Anchor.CASTER?.65f:1;
    int alpha=Math.clamp((int)(face.alpha()*t.alpha()*210*readability),0,220);if(alpha<3)continue;int color=(alpha<<24)|rgb;var n=face.normal();
    entry.getNormalMatrix().transform(n.x(),n.y(),n.z(),normal).normalize();
    for(int i=0;i<4;i++){var p=face.vertices().get(i);vertices.vertex(entry,p.x(),p.y(),p.z()).color(color).texture(i==0||i==3?0:1,i<2?0:1).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(normal.x,normal.y,normal.z);}
   }matrices.pop();
  }
 }
}

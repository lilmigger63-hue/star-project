package dk.starswordmaster.client;

import dk.starswordmaster.network.AbilityPresentationPayload;
import dk.starswordmaster.presentation.EarthFormationMesh;
import dk.starswordmaster.presentation.EarthTerrainSampler;
import dk.starswordmaster.presentation.WorldVfxBudget;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.*;
import java.util.*;

/** Confirmed cosmetic terrain eruptions. Never creates entities, blocks, collision, or gameplay effects. */
public final class EarthFormationRenderer {
 private static final Identifier TEXTURE=Identifier.of("starswordmaster","textures/effect/formation_stone.png");
 private static final RenderLayer LAYER=RenderLayer.getEntitySolid(TEXTURE);
 private static final Map<UUID,Formation> ACTIVE=new LinkedHashMap<>();
 private static final EarthFormationMesh.FrameBudget BUDGET=new EarthFormationMesh.FrameBudget();
 private static List<Draw> frame=List.of();
 private static ClientWorld world;
 private static boolean registered;
 private record Anchored(EarthFormationMesh.Rock rock,Vec3d base,int light,Box bounds){}
 private static final class Formation {
  final UUID caster;final int entityId;final EarthFormationMesh.Kind kind;final long at;final List<Anchored> rocks;
  final PlayerEntity player;final ItemStack weapon;int lastHurt;boolean wasSwinging;
  Formation(AbilityPresentationPayload event,EarthFormationMesh.Kind kind,PlayerEntity player,List<Anchored> rocks){
   caster=event.caster();entityId=event.entityId();this.kind=kind;at=event.sentAt();this.rocks=List.copyOf(rocks);this.player=player;
   weapon=player.getMainHandStack().copy();lastHurt=player.hurtTime;wasSwinging=player.handSwinging;
  }
 }
 private record Draw(Anchored anchored,EarthFormationMesh.Kind kind,float reveal,int quality){}
 private EarthFormationRenderer(){}
 public static void register(){
  if(registered)return;registered=true;
  ClientTickEvents.END_CLIENT_TICK.register(EarthFormationRenderer::tick);
  WorldRenderEvents.END_EXTRACTION.register(EarthFormationRenderer::extract);
  // This point precedes native entity-buffer submission/flush and translucent terrain.
  WorldRenderEvents.BEFORE_ENTITIES.register(EarthFormationRenderer::draw);
 }
 public static void start(AbilityPresentationPayload event,float yaw){
  var client=MinecraftClient.getInstance();var current=client.world;
  if(current==null)return;
  if(world!=current){clear();world=current;}
  cancel(event.caster());
  if(!(current.getEntityById(event.entityId()) instanceof PlayerEntity player)||!player.getUuid().equals(event.caster())||!player.isAlive()||player.isSpectator())return;
  var kind=EarthFormationMesh.kind(event.ability());if(kind==null||!finite(event.origin())||!finite(event.impact()))return;
  long age=Math.max(0,current.getTime()-event.sentAt());
  if(age>=EarthFormationMesh.lifetime(kind))return;
  var center=kind==EarthFormationMesh.Kind.SPIKE?event.impact():event.origin();
  if(client.player==null||client.player.squaredDistanceTo(center)>4096)return;
  var column=new LoadedColumn(current);var anchors=new ArrayList<Anchored>();
  for(var rock:EarthFormationMesh.layout(kind,event.visual().power(),yaw,event.caster().getMostSignificantBits()^event.sequence())){
   double x=center.x+rock.x(),z=center.z+rock.z();
   // Keep all solid-looking forms out of the caster's physical feet, including close target confirmations.
   if(Math.hypot(x-event.origin().x,z-event.origin().z)<Math.max(rock.width(),rock.depth())+.85)continue;
   var anchored=anchor(column,rock,x,center.y,z);
   if(anchored!=null)anchors.add(anchored);
  }
  if(anchors.isEmpty())return;
  if(ACTIVE.size()>=EarthFormationMesh.MAX_FORMATIONS)ACTIVE.remove(ACTIVE.keySet().iterator().next());
  ACTIVE.put(event.caster(),new Formation(event,kind,player,anchors));
 }
 public static void cancel(UUID caster){ACTIVE.remove(caster);frame=List.of();}
 public static void clear(){ACTIVE.clear();frame=List.of();world=null;}
 private static void tick(MinecraftClient client){
  if(world!=client.world){clear();return;}
  if(world==null||client.isPaused())return;
  ACTIVE.values().removeIf(formation->{
   var entity=world.getEntityById(formation.entityId);
   if(world.getTime()-formation.at>=EarthFormationMesh.lifetime(formation.kind)||entity!=formation.player||!formation.player.isAlive()||formation.player.isSpectator())return true;
   var player=formation.player;
   boolean interrupted=player.hurtTime>formation.lastHurt||!ItemStack.areItemsAndComponentsEqual(formation.weapon,player.getMainHandStack())||(player.handSwinging&&!formation.wasSwinging)||PalAbilityAnimations.attackActive(player);
   formation.lastHurt=player.hurtTime;formation.wasSwinging=player.handSwinging;return interrupted;
  });
 }
 private static Anchored anchor(LoadedColumn column,EarthFormationMesh.Rock rock,double x,double referenceY,double z){
  var support=EarthTerrainSampler.support(column,x,referenceY,z,rock.width(),rock.depth(),rock.height());if(support.isEmpty())return null;
  double base=support.get().base(),height=support.get().height();
  var fitted=new EarthFormationMesh.Rock(rock.x(),rock.z(),rock.width(),rock.depth(),(float)height,rock.delay(),rock.variant(),rock.turn());
  Vec3d at=new Vec3d(x,base,z);int light=WorldRenderer.getLightmapCoordinates(column.world,BlockPos.ofFloored(x,base+.5,z));
  return new Anchored(fitted,at,light,new Box(x-rock.width()*1.5,base-1,z-rock.depth()*1.5,x+rock.width()*1.5,base+height+1,z+rock.depth()*1.5));
 }
 private static void extract(WorldExtractionContext context){
  WorldVfxBudget.SHARED.reset();
  frame=List.of();if(context.world()!=world||world==null||ACTIVE.isEmpty())return;
  var client=MinecraftClient.getInstance();int quality=Math.min(VfxQuality.level(),2-client.options.getParticles().getValue().getId());
  Vec3d camera=context.camera().getPos();float partial=context.tickCounter().getTickProgress(false);
  BUDGET.reset();var draws=new ArrayList<Draw>();
  for(var formation:ACTIVE.values()){
   double age=world.getTime()-formation.at+partial;
   for(var anchor:formation.rocks){
    float reveal=EarthFormationMesh.reveal(formation.kind,anchor.rock(),age);if(reveal<.002f)continue;
    double distance=camera.squaredDistanceTo(anchor.base());
    if(distance>4096||!context.frustum().isVisible(anchor.bounds())||world.getChunkManager().getChunk(MathHelper.floor(anchor.base().x)>>4,MathHelper.floor(anchor.base().z)>>4,ChunkStatus.FULL,false)==null)continue;
    int lod=distance>1024?0:distance>256?Math.min(quality,1):quality;
    var faces=EarthFormationMesh.faces(anchor.rock().variant(),lod,formation.kind==EarthFormationMesh.Kind.WALL);
    if(BUDGET.take(faces.size())&&WorldVfxBudget.SHARED.takeTerrain(faces.size()))draws.add(new Draw(anchor,formation.kind,reveal,lod));
   }
  }
  frame=List.copyOf(draws);
 }
 private static void draw(WorldRenderContext context){
  if(frame.isEmpty())return;
  var matrices=context.matrices();if(matrices==null)return;
  Vec3d camera=context.worldState().cameraRenderState.pos;
  var vertices=context.consumers().getBuffer(LAYER);
  for(var draw:frame){
   var a=draw.anchored();var rock=a.rock();
   matrices.push();
   // Translation, rather than vertical scaling, makes an actual tip rise through the ground.
   matrices.translate(a.base().x-camera.x,a.base().y-camera.y-(1-draw.reveal())*rock.height(),a.base().z-camera.z);
   matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rock.turn()));matrices.scale(rock.width(),rock.height(),rock.depth());
   var entry=matrices.peek();
   for(var face:EarthFormationMesh.faces(rock.variant(),draw.quality(),draw.kind()==EarthFormationMesh.Kind.WALL)){
    int color=switch(face.shade()){case 0->0xffb4a28a;case 1->0xff948777;case 2->0xffc0ad8d;default->0xff81786a;};
    for(int i=0;i<4;i++){
     var p=face.points().get(i);
     vertices.vertex(entry,p.x(),p.y(),p.z()).color(color).texture(i==0||i==3?0:1,i<2?1:0).overlay(OverlayTexture.DEFAULT_UV).light(a.light()).normal(entry,face.nx(),face.ny(),face.nz());
    }
   }
   matrices.pop();
  }
 }
 private static boolean finite(Vec3d p){return Double.isFinite(p.x)&&Double.isFinite(p.y)&&Double.isFinite(p.z);}
 /** All state/collision reads happen after explicit non-creating FULL chunk lookup. */
 private static final class LoadedColumn implements EarthTerrainSampler.Column {
  final ClientWorld world;
  private final Map<Long,WorldChunk> chunks=new HashMap<>();
  LoadedColumn(ClientWorld world){this.world=world;}
  private WorldChunk chunk(int x,int z){return chunks.computeIfAbsent(ChunkPos.toLong(x>>4,z>>4),key->world.getChunkManager().getChunk(x>>4,z>>4,ChunkStatus.FULL,false));}
  public boolean loaded(int x,int z){return world.getWorldBorder().contains(x+.5,z+.5)&&chunk(x,z)!=null;}
  public int bottom(){return world.getBottomY();}
  public int topExclusive(){return world.getTopYInclusive()+1;}
  private BlockState state(int x,int y,int z){return chunk(x,z).getBlockState(new BlockPos(x,y,z));}
  public boolean fluid(int x,int y,int z){return !state(x,y,z).getFluidState().isEmpty();}
  public double top(int x,int y,int z,double localX,double localZ){
   var pos=new BlockPos(x,y,z);double top=Double.NaN;
   for(var box:state(x,y,z).getCollisionShape(world,pos).getBoundingBoxes())if(localX>=box.minX&&localX<=box.maxX&&localZ>=box.minZ&&localZ<=box.maxZ)top=Double.isNaN(top)?box.maxY:Math.max(top,box.maxY);
   return top;
  }
  public double clearance(double x,double y,double z,double limit){
   int bx=MathHelper.floor(x),bz=MathHelper.floor(z);double available=Math.max(0,limit);
   for(int by=MathHelper.floor(y);by<=MathHelper.floor(y+limit)&&by<topExclusive();by++){
    var pos=new BlockPos(bx,by,bz);
    var state=state(bx,by,bz);
    if(!state.getFluidState().isEmpty())available=Math.min(available,Math.max(0,by-y));
    for(var box:state.getCollisionShape(world,pos).getBoundingBoxes())if(x-bx>=box.minX&&x-bx<=box.maxX&&z-bz>=box.minZ&&z-bz<=box.maxZ&&by+box.maxY>y)available=Math.min(available,Math.max(0,by+box.minY-y-.05));
   }
   return available;
  }
 }
}

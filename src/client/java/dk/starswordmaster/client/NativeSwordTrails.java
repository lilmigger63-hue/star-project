package dk.starswordmaster.client;
import dk.starswordmaster.aura.*;
import dk.starswordmaster.client.aura.*;
import dk.starswordmaster.presentation.AuraColors;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import java.util.*;
/** Actual native baked-model blade transforms, including PAL/Better Combat and mirrored hands. */
public final class NativeSwordTrails {
 private static final Map<Integer,State[]> PLAYERS=new HashMap<>();
 private static final MatrixStack IDENTITY=new MatrixStack();private static Object world;
 private static final class State {final PlayerEntity player;final SwordTrailHistory history=new SwordTrailHistory();final ItemStack weapon;final boolean first,cameraSpace;
  State(PlayerEntity p,ItemStack weapon,boolean first,boolean cameraSpace){player=p;this.weapon=weapon.copy();this.first=first;this.cameraSpace=cameraSpace;}}
 public record Batch(List<SwordTrailHistory.Face> faces,Vec3d offset,int rgb,boolean first,UUID caster){}
 private NativeSwordTrails(){}
 public static void register(){ClientPlayConnectionEvents.DISCONNECT.register((h,c)->clear());}
 public static void clear(){PLAYERS.clear();world=null;}
 public static void cancel(UUID id){PLAYERS.values().removeIf(states->{for(var s:states)if(s!=null&&s.player.getUuid().equals(id))return true;return false;});}
 public static void beginFrame(){
  var client=MinecraftClient.getInstance();if(world!=client.world){clear();world=client.world;}if(client.world==null)return;
  PLAYERS.entrySet().removeIf(e->{for(int hand=0;hand<2;hand++){var s=e.getValue()[hand];if(s!=null&&(!s.player.isAlive()||s.player.isSpectator()||client.world.getEntityById(e.getKey())!=s.player||!ItemStack.areItemsAndComponentsEqual(s.weapon,s.player.getStackInHand(hand==0?Hand.MAIN_HAND:Hand.OFF_HAND))||s.player.age-s.history.lastAge()>4))e.getValue()[hand]=null;}return e.getValue()[0]==null&&e.getValue()[1]==null;});
 }
 public static boolean recent(PlayerEntity player,int hand,float age){var states=PLAYERS.get(player.getId());return states!=null&&states[hand]!=null&&states[hand].player==player&&age-states[hand].history.lastAge()<4;}
 public static Batch capture(SwordAuraSnapshot snapshot,PlayerEntity player,MatrixStack matrices,int quality){
  if(!snapshot.swinging()&&!recent(player,snapshot.hand(),snapshot.age()))return null;
  if(player.hurtTime>0){PLAYERS.remove(player.getId());return null;}boolean cameraSpace=snapshot.context().isFirstPerson();
  var client=MinecraftClient.getInstance();
  // PAL's first-person body uses THIRD_PERSON item transforms in the world pass.
  // Keep those samples world-relative; only vanilla hand transforms are camera-space.
  boolean first=cameraSpace||(player==client.getCameraEntity()&&!client.gameRenderer.getCamera().isThirdPerson());
  var states=PLAYERS.get(player.getId());if(states==null){if(PLAYERS.size()>=64)return null;states=new State[2];PLAYERS.put(player.getId(),states);}
  var weapon=player.getStackInHand(snapshot.hand()==0?Hand.MAIN_HAND:Hand.OFF_HAND);var s=states[snapshot.hand()];
  if(s==null||s.player!=player||s.first!=first||s.cameraSpace!=cameraSpace||!ItemStack.areItemsAndComponentsEqual(s.weapon,weapon)){s=new State(player,weapon,first,cameraSpace);states[snapshot.hand()]=s;}
  var camera=client.gameRenderer.getCamera().getPos();
  if(snapshot.swinging()&&(snapshot.age()-s.history.lastAge()>=.25||snapshot.age()<s.history.lastAge())){
   var pose=matrices.peek().getPositionMatrix();var base=pose.transformPosition(new Vector3f(.34f,.34f,.5f));var tip=pose.transformPosition(new Vector3f(.95f,.95f,.5f));
   double x=cameraSpace?0:camera.x,y=cameraSpace?0:camera.y,z=cameraSpace?0:camera.z;
   s.history.add(snapshot.age(),base.x+x,base.y+y,base.z+z,tip.x+x,tip.y+y,tip.z+z);
  }
  var faces=s.history.mesh(snapshot.age(),quality);if(faces.isEmpty())return null;
  int rgb=snapshot.visual().auraUnlocked()?snapshot.visual().auraColor():switch(snapshot.visual().playerClass()){case SWORDSMAN->0xe4c18a;case MAGE->0xafdfff;case SPELLBLADE->0xc7b6ff;default->0xd6e2eb;};
  return new Batch(faces,cameraSpace?Vec3d.ZERO:camera,rgb,first,player.getUuid());
 }
 public static void submit(Batch batch,OrderedRenderCommandQueue queue,int limit){
  if(batch==null||limit<=0)return;
  queue.submitCustom(IDENTITY,AuraRenderLayer.trail(),(entry,vertices)->{
   int count=Math.min(limit,batch.faces().size());
   for(int n=0;n<count;n++){var face=batch.faces().get(n);int rgb=face.highlight()?AuraColors.highlight(batch.rgb(),.85f):batch.rgb();int alpha=Math.round(face.alpha()*(batch.first()?.55f:1)*255);int color=(alpha<<24)|rgb;
    for(int i=0;i<4;i++){var p=face.points().get(i);vertices.vertex(entry,(float)(p.x()-batch.offset().x),(float)(p.y()-batch.offset().y),(float)(p.z()-batch.offset().z)).color(color).texture(i==0||i==3?0:1,i<2?0:1).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(entry,0,0,1);}
   }
  });
 }
}

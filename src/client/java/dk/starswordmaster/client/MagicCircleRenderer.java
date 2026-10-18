package dk.starswordmaster.client;

import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import dk.starswordmaster.circles.CircleFrameBudget;
import dk.starswordmaster.circles.MagicCircleMesh;
import dk.starswordmaster.client.circles.CircleRenderLayer;
import dk.starswordmaster.client.circles.CircleSnapshot;
import dk.starswordmaster.client.mixin.circles.CirclePlayerModelAccessor;
import dk.starswordmaster.presentation.AbilityPresentation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

/** Sigils extracted from the actual native arm pose, after PAL and Better Combat have blended it. */
public final class MagicCircleRenderer {
 private static final CircleFrameBudget FRAME=new CircleFrameBudget();
 private static boolean registered;
 private MagicCircleRenderer(){}
 public static void register(){
  if(registered)return;registered=true;
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->FRAME.reset());
 }
 public static void beginFrame(){FRAME.reset();}

 /** LivingEntityRenderer has already applied native/PAL body transforms and setAngles. */
 public static void nativeModel(PlayerEntityRenderState state,PlayerEntityModel model,MatrixStack matrices,OrderedRenderCommandQueue queue){
  var client=MinecraftClient.getInstance();
  if(!registered||client.world==null||state.spectator||state.invisible)return;
  var entity=client.world.getEntityById(state.id);
  if(!(entity instanceof PlayerEntity player))return;
  boolean first=((IAvatarAnimationState)state).playerAnimLib$isFirstPersonPass();
  // The camera player's world-model copy is only valid in PAL's explicit first-person pass.
  if(player==client.getCameraEntity()&&!client.gameRenderer.getCamera().isThirdPerson()&&!first)return;
  boolean slim=((CirclePlayerModelAccessor)model).starswordmaster$thinArms();
  matrices.push();model.getRootPart().applyTransform(matrices);
  nativeArm(player,model.rightArm,Arm.RIGHT,slim,first,matrices,queue);
  nativeArm(player,model.leftArm,Arm.LEFT,slim,first,matrices,queue);
  matrices.pop();
 }

 /** Vanilla first-person fallback is called only when the native arm itself is submitted. */
 public static void firstPersonArm(PlayerEntityModel model,ModelPart arm,MatrixStack matrices,OrderedRenderCommandQueue queue){
  var client=MinecraftClient.getInstance();
  if(client.player==null||!client.options.getPerspective().isFirstPerson())return;
  nativeArm(client.player,arm,arm==model.leftArm?Arm.LEFT:Arm.RIGHT,((CirclePlayerModelAccessor)model).starswordmaster$thinArms(),true,matrices,queue);
 }
 private static void nativeArm(PlayerEntity player,ModelPart arm,Arm physical,boolean slim,boolean firstPerson,MatrixStack matrices,OrderedRenderCommandQueue queue){
  var client=MinecraftClient.getInstance();
  if(!registered||!arm.visible||arm.hidden||!player.isAlive()||player.isSpectator()||client.world==null||player.getEntityWorld()!=client.world)return;
  // Match the native F1 decision: a local first-person hand is hidden with the HUD.
  if(firstPerson&&client.options.hudHidden)return;
  var cast=AbilityPresentationClient.visual(player.getUuid());
  if(cast==null||cast.style().element()==AbilityPresentation.Element.STEEL)return;
  boolean dominant=physical==player.getMainArm();
  if(!dominant&&!dual(cast.style().clip()))return;
  double distance=client.gameRenderer.getCamera().getPos().squaredDistanceTo(player.getEntityPos());
  if(!Double.isFinite(distance)||distance>4096)return;
  var visual=cast.event().visual();
  float age=cast.age()+client.getRenderTickCounter().getTickProgress(false);
  int quality=Math.min(VfxQuality.level(),2-client.options.getParticles().getValue().getId());
  var profile=MagicCircleMesh.profile(age,cast.style().ticks(),visual.power(),visual.rank(),visual.swordLevel(),quality,distance);
  if(profile.alpha()<.005f||!FRAME.take(player.getId(),physical==Arm.LEFT?0:1))return;
  var snapshot=new CircleSnapshot(player.getUuid(),player.getId(),physical==Arm.LEFT?0:1,firstPerson,slim,cast.style().element(),profile,age);
  matrices.push();arm.applyTransform(matrices);
  palm(matrices,snapshot.hand(),slim);
  submitAtPalm(snapshot,arm,matrices,queue);
  matrices.pop();
 }
 /** Native arm extends along +Y. This plane is just beyond its fingertips, normal along that arm. */
 public static void palm(MatrixStack matrices,int hand,boolean slim){
  matrices.translate((hand==0?1:-1)*(slim?.03125f:.0625f),.80f,0);
  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
 }
 private static boolean dual(AbilityPresentation.Clip clip){
  return switch(clip){
   case DUAL_CAST,WATER_WEAVE,HEAL,CHANNEL,LIGHTNING_CHANNEL,EARTH_RAISE,SHADOW_SUMMON,OVERHEAD_CAST,AURA,GUARD,GROUND_SLAM->true;
   default->false;
  };
 }
 /** Matrix is already frozen at the real palm plane when the native queue captures this command. */
 public static void submitAtPalm(CircleSnapshot snapshot,ModelPart arm,MatrixStack matrices,OrderedRenderCommandQueue queue){
  queue.submitCustom(matrices,CircleRenderLayer.get(),(entry,vertices)->draw(snapshot,entry,vertices));
 }
 private static void draw(CircleSnapshot snapshot,MatrixStack.Entry entry,VertexConsumer vertices){
  int tile=switch(snapshot.element()){case FIRE->0;case FROST->1;case LIGHTNING->2;case WATER->3;case EARTH->4;case DARK->5;case HEALING->6;default->7;};
  float radius=snapshot.profile().radius();
  for(int layer=0;layer<snapshot.profile().layers();layer++){
   float angle=MagicCircleMesh.layerAngle(layer,snapshot.age(),snapshot.element());
   float c=(float)Math.cos(angle),s=(float)Math.sin(angle),size=radius*(layer==1?1.13f:layer==2?.82f:1);
   int alpha=Math.clamp((int)(255*snapshot.profile().alpha()*(layer==0?1:.84f)*MagicCircleMesh.layerBrightness(layer,snapshot.age(),snapshot.element())),0,255);
   for(int corner=0;corner<4;corner++){
    float x=(corner==0||corner==3?-size:size),y=(corner<2?-size:size);
    float u=(tile+(corner==0||corner==3?0:1))/8f,v=(layer+(corner<2?0:1))/3f;
    vertices.vertex(entry,c*x-s*y,s*x+c*y,layer*.008f).color(255,255,255,alpha).texture(u,v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(entry,0,0,1);
   }
  }
 }
}

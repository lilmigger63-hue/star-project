package dk.starswordmaster.client;

import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import dk.starswordmaster.aura.AuraFrameBudget;
import dk.starswordmaster.aura.BladeAuraMesh;
import dk.starswordmaster.client.aura.SwordAuraSnapshot;
import dk.starswordmaster.client.aura.AuraRenderLayer;
import dk.starswordmaster.presentation.AuraColors;
import dk.starswordmaster.progression.PlayerProgress;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.HeldItemContext;

/** Native held-model aura. All geometry shares the real sword transform, including PAL/Better Combat. */
public final class SwordAuraRenderer {
 private static final AuraFrameBudget BUDGET=new AuraFrameBudget();
 private static boolean registered;
 private SwordAuraRenderer(){}
 public static void register(){
  if(registered)return;registered=true;
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->BUDGET.reset());
 }
 public static void beginFrame(){BUDGET.reset();}

 public static SwordAuraSnapshot capture(ItemStack stack,ItemDisplayContext context,HeldItemContext owner){
  if(!registered||!(owner instanceof PlayerEntity player)||!player.isAlive()||player.isSpectator()||!stack.isIn(ItemTags.SWORDS)||!held(context))return null;
  var client=MinecraftClient.getInstance();
  if(client.world==null||player.getEntityWorld()!=client.world)return null;
  var visual=ClientVisualState.get(player.getUuid());
  if(!visual.auraUnlocked())return null;
  int hand=context.isLeftHand()==(player.getMainArm()==Arm.LEFT)?0:1;
  // Ignore equip-transition copies once that physical hand no longer holds a sword.
  if(!player.getStackInHand(hand==0?Hand.MAIN_HAND:Hand.OFF_HAND).isIn(ItemTags.SWORDS))return null;
  boolean swinging=player.handSwinging||PalAbilityAnimations.attackActive(player);
  if(!swinging){
   try{
    var ability=PlayerAnimationAccess.getPlayerAnimationLayer(player,PalAbilityAnimations.LAYER);
    swinging=ability!=null&&ability.isActive();
   }catch(RuntimeException ignored){}
  }
  float age=player.age+client.getRenderTickCounter().getTickProgress(false);
  return new SwordAuraSnapshot(player.getUuid(),player.getId(),hand,context,client.world,visual,age,swinging);
 }
 private static boolean held(ItemDisplayContext context){
  return context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.FIRST_PERSON_RIGHT_HAND||context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
 }

 /** Called only after LayerRenderState has applied the actual baked model's display transform. */
 public static void submit(SwordAuraSnapshot snapshot,MatrixStack matrices,OrderedRenderCommandQueue queue){
  if(snapshot==null)return;
  var client=MinecraftClient.getInstance();
  if(client.world==null||client.world!=snapshot.world()||client.player==null)return;
  var entity=client.world.getEntityById(snapshot.entityId());
  if(!(entity instanceof PlayerEntity player)||!player.isAlive()||player.isSpectator()||!player.getUuid().equals(snapshot.player())||!player.getStackInHand(snapshot.hand()==0?Hand.MAIN_HAND:Hand.OFF_HAND).isIn(ItemTags.SWORDS))return;
  double distanceSquared=client.gameRenderer.getCamera().getPos().squaredDistanceTo(player.getEntityPos());
  if(!Double.isFinite(distanceSquared)||distanceSquared>4096)return;
  var state=snapshot.visual();
  if(!state.auraUnlocked()||!ClientVisualState.get(snapshot.player()).auraUnlocked())return;
  int quality=VfxQuality.level();
  // Vanilla's minimal/decreased particle preferences also reduce native aura detail.
  quality=Math.min(quality,2-client.options.getParticles().getValue().getId());
  var profile=BladeAuraMesh.profile(state.rank(),state.power(),state.swordLevel(),state.playerClass()==PlayerProgress.PlayerClass.SWORDSMAN,quality,distanceSquared,snapshot.swinging());
  int base=state.auraColor();
  int accent=state.playerClass()==PlayerProgress.PlayerClass.SPELLBLADE?elementAccent(state.element().name()):-1;
  int count=BUDGET.take(snapshot.entityId(),snapshot.hand(),BladeAuraMesh.quadCount(profile,accent>=0),distanceSquared);
  if(count==0)return;
  var draw=BladeAuraMesh.build(profile,snapshot.age(),base,AuraColors.highlight(base,.9f),AuraColors.shade(base,.5f),accent);
  queue.submitCustom(matrices,AuraRenderLayer.get(),(entry,vertices)->{
   for(var quad:draw){
    int color=(quad.alpha()<<24)|(quad.rgb()&0xffffff);
    for(int i=0;i<4;i++){
     var point=quad.vertices().get(i);
     vertex(vertices,entry,point,color,i==0||i==3?0:1,i<2?0:1);
    }
   }
  });
 }
 private static void vertex(VertexConsumer vertices,MatrixStack.Entry entry,BladeAuraMesh.Point point,int color,float u,float v){
  vertices.vertex(entry,point.x(),point.y(),point.z()).color(color).texture(u,v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(entry,0,0,1);
 }
 private static int elementAccent(String element){
  return switch(element){case "FIRE"->0xffa034;case "FROST"->0xa5edff;case "LIGHTNING"->0xfff1a0;case "WATER"->0x44bdff;case "EARTH"->0xbaca6d;case "DARK"->0x695581;case "HEALING"->0xabffce;case "STAR"->0xe9f4ff;default->-1;};
 }
}

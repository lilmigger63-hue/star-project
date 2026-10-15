package dk.starswordmaster.client;
import com.zigythebird.playeranim.api.*;
import com.zigythebird.playeranim.animation.*;
import com.zigythebird.playeranimcore.enums.PlayState;
import com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier;
import com.zigythebird.playeranimcore.api.firstPerson.*;
import dk.starswordmaster.StarSwordmaster;
import dk.starswordmaster.presentation.AbilityPresentation;
import net.bettercombat.client.animation.AttackAnimationStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
/** A PAL layer in the same stack as Better Combat, below its attack layer. */
final class PalAbilityAnimations {
 static final Identifier LAYER=Identifier.of("starswordmaster","ability");
 private static boolean warned;
 private PalAbilityAnimations(){}
 static void register(){
  PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(LAYER,0,avatar->{
   var controller=new PlayerAnimationController(avatar,(c,data,setter)->PlayState.STOP);
   var mirror=new MirrorModifier();mirror.enabled=avatar.getMainArm()==Arm.LEFT;controller.addModifierLast(mirror);
   controller.setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
   controller.setFirstPersonConfiguration(new FirstPersonConfiguration(true,true,true,true,false));
   return controller;
  });
  PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((avatar,manager)->{
   var own=PlayerAnimationAccess.getPlayerAnimationLayer(avatar,LAYER);
   var attack=PlayerAnimationAccess.getPlayerAnimationLayer(avatar,AttackAnimationStack.ID);
   int priority=1000;
   for(var layer:manager.getLayers())if(layer.right()==attack)priority=layer.left()-1;
   if(own!=null){manager.removeLayer(own);manager.addAnimLayer(priority,own);}
  });
 }
 static boolean attackActive(PlayerEntity player){
  try{var layer=PlayerAnimationAccess.getPlayerAnimationLayer(player,AttackAnimationStack.ID);return layer!=null&&layer.isActive();}
  catch(RuntimeException e){return false;}
 }
 static boolean play(PlayerEntity player,AbilityPresentation p,int elapsed){
  try{
   var clip=Identifier.of("starswordmaster",p.clip().id());
   if(!PlayerAnimResources.hasAnimation(clip)||attackActive(player))return false;
   if(PlayerAnimationAccess.getPlayerAnimationLayer(player,LAYER) instanceof PlayerAnimationController c){
    for(var modifier:c.getModifiers())if(modifier instanceof MirrorModifier mirror)mirror.enabled=player.getMainArm()==Arm.LEFT;
    c.stopTriggeredAnimation();c.stop();return c.triggerAnimation(clip,elapsed);
   }
  }catch(RuntimeException e){if(!warned){warned=true;StarSwordmaster.LOG.warn("Ability animation unavailable; using particle fallback",e);}}
  return false;
 }
 static void stop(PlayerEntity player){
  try{if(PlayerAnimationAccess.getPlayerAnimationLayer(player,LAYER) instanceof PlayerAnimationController c){c.stopTriggeredAnimation();c.stop();}}
  catch(RuntimeException ignored){}
 }
}

package dk.starswordmaster.client;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import dk.starswordmaster.presentation.VisualPowerScaling;
/** Blend only our arm rotation contribution; never scale the player model, aim or animation time. */
final class PowerAnimationModifier extends AbstractModifier {
 private float weight=1;
 void power(float power){weight=VisualPowerScaling.animationWeight(power);}
 @Override public PlayerAnimBone get3DTransform(PlayerAnimBone bone){
  float x=bone.getRotX(),y=bone.getRotY(),z=bone.getRotZ();var result=super.get3DTransform(bone);
  if(bone.getName().equals("right_arm")||bone.getName().equals("left_arm"))result.updateRotation(x+(result.getRotX()-x)*weight,y+(result.getRotY()-y)*weight,z+(result.getRotZ()-z)*weight);
  return result;
 }
}

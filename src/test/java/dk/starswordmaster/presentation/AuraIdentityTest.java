package dk.starswordmaster.presentation;
import dk.starswordmaster.progression.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.math.BigInteger;
public class AuraIdentityTest {
 @Test public void uuidColorIsStableVibrantAndCoversTheWholeHueWheel(){
  Set<Integer> colors=new HashSet<>();Set<Integer> hues=new HashSet<>();var random=new Random(19372);
  for(int i=0;i<10000;i++){var id=new UUID(random.nextLong(),random.nextLong());int color=AuraColors.forPlayer(id);assertEquals(color,AuraColors.forPlayer(UUID.fromString(id.toString())));colors.add(color);
   float[] hsv=java.awt.Color.RGBtoHSB(color>>16&255,color>>8&255,color&255,null);hues.add((int)(hsv[0]*12)%12);assertTrue(hsv[1]>=.7f);assertTrue(hsv[2]>=.89f);
  }
  assertEquals(12,hues.size());assertTrue(colors.size()>9500);
 }
 @Test public void ranksReloadAndLevelUpsNeverReplaceAuraIdentity(){
  var id=UUID.fromString("11112222-3333-4444-5555-666677778888");var props=new Properties();props.setProperty("xp","7200");props.setProperty("class","SWORDSMAN");props.setProperty("practice.SWORD","10000");props.setProperty("learned","quick_cut,aura_sense");var p=PlayerProgress.fromProperties(props);
  int color=VisualState.from(id,p,AbilityPresentation.Element.STAR).auraColor();assertTrue(VisualState.from(id,p,AbilityPresentation.Element.STAR).auraUnlocked());assertEquals(1,p.rank());
  for(String node:List.of("aura_awaken","sword_mastery","astral_sword_sovereign")){assertTrue(p.unlock(node));p=PlayerProgress.fromProperties(p.toProperties());assertEquals(color,VisualState.from(id,p,AbilityPresentation.Element.STAR).auraColor());}
  p.awardXp(BigInteger.TEN.pow(60));assertEquals(color,VisualState.from(id,p,AbilityPresentation.Element.STAR).auraColor());assertEquals(4,p.rank());
 }
 @Test public void darkAndLightShadesAreFiniteAndDerivedWithoutRandomness(){
  int color=AuraColors.forPlayer(UUID.fromString("10000000-0000-0000-0000-000000000001"));assertEquals(0xFFFFFF,AuraColors.highlight(color,1));assertEquals(color,AuraColors.highlight(color,0));assertEquals(color,AuraColors.shade(color,1));assertEquals(0,AuraColors.shade(color,0));assertEquals(color,AuraColors.shade(color,Float.NaN));
 }
 @Test public void levelScalingIsSmoothBoundedAndSafeForHugeIntegers(){
  float before=-1;for(int level=1;level<=10000;level++){float n=VisualPowerScaling.power(BigInteger.valueOf(level));assertTrue(n>before);assertTrue(n>=0&&n<=1);before=n;}
  assertEquals(0,VisualPowerScaling.power(BigInteger.ONE),0);assertEquals(1,VisualPowerScaling.power(BigInteger.TEN.pow(10000)),0);assertEquals(0,VisualPowerScaling.power(null),0);
  for(float p:new float[]{Float.NaN,Float.POSITIVE_INFINITY,-1,0,.5f,1,10}){assertTrue(Float.isFinite(VisualPowerScaling.radius(p)));assertTrue(VisualPowerScaling.radius(p)<=2.25f);assertTrue(VisualPowerScaling.sprite(p)<=2.5f);}
 }
 @Test public void scalingAllActualGeometryPreservesRayFractionsAndHardBounds(){
  for(var style:AbilityPresentation.ALL.values())for(int tick=0;tick<style.ticks();tick++)for(var point:ParticlePattern.points(style,tick))for(float power:new float[]{0,.5f,1}){
   var p=VisualPowerScaling.scale(point,power);assertTrue(Double.isFinite(p.x()+p.y()+p.z()));assertTrue(p.x()*p.x()+p.y()*p.y()+p.z()*p.z()<=144.000001);assertTrue(p.size()<=3);assertEquals(point.life(),p.life());if(p.space()==ParticlePattern.Space.RAY)assertEquals(point.z(),p.z(),0);
  }
 }
 @Test public void untrustedVisualNumbersAreSafelyClamped(){var v=new VisualState(true,100,500,Float.NaN,null,-1,null);assertEquals(4,v.rank());assertEquals(100,v.swordLevel());assertEquals(0,v.power(),0);assertEquals(0xFFFFFF,v.auraColor());assertNotNull(v.element());assertNotNull(v.playerClass());}
 @Test public void armAnimationWeightGrowsWithoutPoseOvershoot(){assertEquals(.75f,VisualPowerScaling.animationWeight(0),0);assertEquals(.875f,VisualPowerScaling.animationWeight(.5f),0);assertEquals(1,VisualPowerScaling.animationWeight(1),0);assertEquals(.75f,VisualPowerScaling.animationWeight(Float.NaN),0);assertEquals(1,VisualPowerScaling.animationWeight(Float.MAX_VALUE),0);}
 @Test public void elementalSwordUnlockAlsoEnablesTheSpellbladeAura(){
  var p=new Properties();p.setProperty("xp","7200");p.setProperty("class","SPELLBLADE");p.setProperty("practice.LIGHTNING","10000");p.setProperty("learned","spark,static_spark,chain_lightning,thunder_step");var player=PlayerProgress.fromProperties(p);
  assertTrue(player.learned().contains("thunder_step"));assertTrue(VisualState.from(new UUID(1,2),player,AbilityPresentation.Element.LIGHTNING).auraUnlocked());
 }
}

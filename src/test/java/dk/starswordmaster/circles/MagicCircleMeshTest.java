package dk.starswordmaster.circles;

import org.junit.Test;
import static org.junit.Assert.*;

public class MagicCircleMeshTest {
 @Test public void chargeAndReleaseHaveFiniteEnvelope(){
  assertEquals(0,MagicCircleMesh.profile(0,20,0,0,1,2,0).alpha(),0);
  assertTrue(MagicCircleMesh.profile(6,20,0,0,1,2,0).alpha()>.5f);
  assertEquals(0,MagicCircleMesh.profile(20,20,0,0,1,2,0).alpha(),0);
 }
 @Test public void refinementIsContinuousAndBounded(){
  var low=MagicCircleMesh.profile(6,20,0,0,1,2,0);
  var higher=MagicCircleMesh.profile(6,20,.15f,1,12,2,0);
  var capped=MagicCircleMesh.profile(6,20,1,4,100,2,0);
  assertTrue(higher.radius()>low.radius());
  assertTrue(capped.radius()>higher.radius());
  assertTrue(capped.radius()<=.6f);
  assertEquals(3,capped.layers());
  assertEquals(1,MagicCircleMesh.profile(6,20,1,4,100,0,3000).layers());
 }
 @Test public void invalidPowerCannotPoisonVertices(){
  var profile=MagicCircleMesh.profile(6,20,Float.NaN,99,999,2,0);
  assertTrue(Float.isFinite(profile.radius()));
  assertTrue(Float.isFinite(profile.alpha()));
 }
 @Test public void layeredRunesCounterRotateWithBoundedElementPulse(){
  for(var e:dk.starswordmaster.presentation.AbilityPresentation.Element.values())for(int i=0;i<80;i++){
   float a=MagicCircleMesh.layerAngle(0,i,e),b=MagicCircleMesh.layerAngle(1,i,e);
   assertTrue(Float.isFinite(a)&&Float.isFinite(b));if(i>0)assertTrue(a*b<0);
   float light=MagicCircleMesh.layerBrightness(2,i,e);assertTrue(light>=.86f&&light<=1);
  }
 }
 @Test public void handsAndGlobalGeometryAreBounded(){
  var frame=new CircleFrameBudget();
  assertTrue(frame.take(1,0));
  assertFalse(frame.take(1,0));
  assertTrue(frame.take(1,1));
  for(int i=2;i<=64;i++){assertTrue(frame.take(i,0));assertTrue(frame.take(i,1));}
  assertFalse(frame.take(65,0));
  assertEquals(128,frame.circles());
  frame.reset();assertTrue(frame.take(1,0));
 }
}

package dk.starswordmaster.aura;

import org.junit.Test;
import static org.junit.Assert.*;

public class BladeAuraMeshTest {
 private BladeAuraMesh.Profile profile(int rank,float power,int level,boolean swordsman,int quality,double distance,boolean swing){
  return BladeAuraMesh.profile(rank,power,level,swordsman,quality,distance,swing);
 }
 @Test public void eachRankHasDistinctSilhouetteAndContinuousGrowth(){
  float width=0;int detail=0;
  for(int rank=1;rank<=4;rank++){
   var p=profile(rank,.3f,20,true,2,0,false);
   assertTrue(p.width()>width);assertTrue(p.details()>detail);width=p.width();detail=p.details();
  }
  assertTrue(profile(2,.41f,20,true,2,0,false).width()>profile(2,.40f,20,true,2,0,false).width());
  assertTrue(profile(2,.4f,21,true,2,0,false).width()>profile(2,.4f,20,true,2,0,false).width());
  assertTrue(profile(2,.4f,20,true,2,0,false).width()>profile(2,.4f,20,false,2,0,false).width());
 }
 @Test public void distanceQualityAndIdleReduceCostWithoutRemovingBlade(){
  var ultra=profile(4,1,999,true,2,0,true);
  assertTrue(ultra.details()>profile(4,1,999,true,0,0,true).details());
  assertTrue(ultra.details()>profile(4,1,999,true,2,2500,true).details());
  assertTrue(ultra.details()>profile(4,1,999,true,2,0,false).details());
  assertTrue(BladeAuraMesh.build(profile(1,0,0,true,0,4000,false),0,0x123456,0xffffff,0x050505,-1).size()>=4);
 }
 @Test public void geometryStaysFiniteBoundedAndSpansHiltToTip(){
  for(int rank=1;rank<=4;rank++)for(float power:new float[]{0,1,Float.NaN,Float.POSITIVE_INFINITY}){
   var mesh=BladeAuraMesh.build(profile(rank,power,Integer.MAX_VALUE,true,2,0,true),Float.NaN,0x123456,0xffffff,0x050505,0x00ffaa);
   assertTrue(mesh.size()<=BladeAuraMesh.MAX_QUADS);
   float min=100,max=-100;
   for(var quad:mesh)for(var point:quad.vertices()){
    assertTrue(Float.isFinite(point.x()));assertTrue(Float.isFinite(point.y()));assertTrue(Float.isFinite(point.z()));
    assertTrue(Math.abs(point.x())<2);assertTrue(Math.abs(point.y())<2);assertTrue(Math.abs(point.z())<2);
    min=Math.min(min,point.x()+point.y());max=Math.max(max,point.x()+point.y());
   }
   assertTrue(min<.7f);assertTrue(max>1.8f);
  }
 }
 @Test public void personalColorDominatesAndElementsAreSecondary(){
  var p=profile(4,.6f,80,true,2,0,true);
  var pure=BladeAuraMesh.build(p,12,0xc01040,0xffaaaa,0x500010,-1);
  assertTrue(pure.stream().allMatch(q->q.rgb()==0xc01040||q.rgb()==0xffaaaa||q.rgb()==0x500010));
  var elemental=BladeAuraMesh.build(p,12,0xc01040,0xffaaaa,0x500010,0x00ffff);
  long accents=elemental.stream().filter(q->q.rgb()==0x00ffff).count();
  assertTrue(accents>0);assertTrue(accents*2<elemental.size());
 }
 @Test public void frameBudgetBoundsGeneratedGeometryBeforeConstruction(){
  var budget=new AuraFrameBudget();int generated=0;
  for(int i=0;i<256;i++){
   var profile=profile(i%4+1,.7f,60,true,i%3,0,(i&1)==0);
   int reserved=budget.take(i,0,BladeAuraMesh.quadCount(profile,true),0);
   if(reserved==0)continue;
   int actual=BladeAuraMesh.build(profile,i,0xff0000,0xffffff,0x770000,0x00ffff).size();
   assertTrue(actual<=reserved);generated+=actual;
  }
  assertTrue(generated<=1024);
 }
 @Test public void sovereignIdleAuraHasBroadBrightEnvelopeWhileRankOneStaysSubtle(){
  var basic=profile(1,0,1,true,1,0,false);
  var sovereign=profile(4,.7f,100,true,1,0,false);
  assertTrue(sovereign.width()>=basic.width()*4);
  var mesh=BladeAuraMesh.build(sovereign,0,0xff6000,0xfff2e8,0x803000,-1);
  assertTrue(mesh.stream().anyMatch(q->q.rgb()==0xff6000&&q.alpha()>=185));
  assertTrue(mesh.stream().filter(q->q.rgb()==0xfff2e8&&q.alpha()>=200).count()>=6);
  assertTrue(BladeAuraMesh.build(basic,0,0xff6000,0xfff2e8,0x803000,-1).stream().allMatch(q->q.alpha()<=150));
 }
}

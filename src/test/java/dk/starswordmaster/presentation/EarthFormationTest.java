package dk.starswordmaster.presentation;

import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class EarthFormationTest {
 @Test public void allFourSpellsHaveDeterministicFiniteBoundedVolumes(){
  for(var kind:EarthFormationMesh.Kind.values())for(float power:new float[]{0,1,Float.NaN,Float.POSITIVE_INFINITY}){
   var rocks=EarthFormationMesh.layout(kind,power,37,723);
   assertFalse(rocks.isEmpty());assertTrue(rocks.size()<=EarthFormationMesh.MAX_ROCKS_PER_FORMATION);
   assertEquals(rocks,EarthFormationMesh.layout(kind,power,37,723));
   for(var rock:rocks){
    assertTrue(Double.isFinite(rock.x()+rock.z()+rock.width()+rock.depth()+rock.height()));
    assertTrue(rock.height()>1);assertTrue(Math.hypot(rock.x(),rock.z())<12);
    for(int lod=0;lod<3;lod++)for(var face:EarthFormationMesh.faces(rock.variant(),lod,kind==EarthFormationMesh.Kind.WALL)){
     assertEquals(4,face.points().size());
     assertTrue(Double.isFinite(face.nx()+face.ny()+face.nz()));
     assertEquals(1,Math.sqrt(face.nx()*face.nx()+face.ny()*face.ny()+face.nz()*face.nz()),.0001);
     for(var p:face.points())assertTrue(Double.isFinite(p.x()+p.y()+p.z()));
    }
   }
  }
 }
 @Test public void wallLastsForResistanceBuffAndEveryEffectExpires(){
  assertEquals(100,EarthFormationMesh.lifetime(EarthFormationMesh.Kind.WALL));
  for(var kind:EarthFormationMesh.Kind.values()){
   var rocks=EarthFormationMesh.layout(kind,1,0,3);
   for(var rock:rocks){
    assertEquals(0,EarthFormationMesh.reveal(kind,rock,0),0);
    assertEquals(0,EarthFormationMesh.reveal(kind,rock,EarthFormationMesh.lifetime(kind)),0);
    assertEquals(0,EarthFormationMesh.reveal(kind,rock,Double.NaN),0);
    assertTrue(EarthFormationMesh.reveal(kind,rock,rock.delay()+7)>.5);
   }
  }
 }
 @Test public void quakeRunsForwardAndRotatesWithFacing(){
  var north=EarthFormationMesh.layout(EarthFormationMesh.Kind.QUAKE,0,0,1);
  var west=EarthFormationMesh.layout(EarthFormationMesh.Kind.QUAKE,0,90,1);
  for(int i=0;i<north.size();i++){
   assertTrue(north.get(i).z()>1);assertEquals(-north.get(i).z(),west.get(i).x(),.00001);
   if(i>0){assertTrue(north.get(i).z()>north.get(i-1).z());assertTrue(north.get(i).delay()>north.get(i-1).delay());}
  }
 }
 @Test public void lowQualityPreservesCoreAndBudgetsAreHardLimits(){
  var budget=new EarthFormationMesh.FrameBudget();int drawn=0;
  while(budget.take(24))drawn+=24;
  assertTrue(drawn<=EarthFormationMesh.MAX_FRAME_QUADS);assertFalse(budget.take(1_000_000));
  budget.reset();assertTrue(budget.take(24));
  for(var kind:EarthFormationMesh.Kind.values())for(var rock:EarthFormationMesh.layout(kind,0,0,1)){
   assertFalse(EarthFormationMesh.faces(rock.variant(),0,kind==EarthFormationMesh.Kind.WALL).isEmpty());
   if(kind!=EarthFormationMesh.Kind.SPIKE)assertTrue(Math.hypot(rock.x(),rock.z())>rock.width()+.8);
  }
 }
 @Test public void collisionSamplerHandlesSlopesLiquidsMissingChunksAndBounds(){
  var flat=new TestColumn();
  assertEquals(.5,EarthTerrainSampler.surface(flat,.25,1,.25).orElseThrow(),0);
  assertTrue(flat.queries<=EarthTerrainSampler.MAX_COLUMN_QUERIES);
  flat.loaded=false;flat.queries=0;assertTrue(EarthTerrainSampler.surface(flat,.25,1,.25).isEmpty());assertEquals(0,flat.queries);
  flat.loaded=true;flat.liquid=true;assertTrue(EarthTerrainSampler.surface(flat,.25,1,.25).isEmpty());
  flat.liquid=false;assertTrue(EarthTerrainSampler.surface(flat,Double.NaN,1,.25).isEmpty());
  assertTrue(EarthTerrainSampler.surface(flat,30_000_001,1,.25).isEmpty());
  var slope=new TestColumn(){public double top(int x,int y,int z,double localX,double localZ){queries++;return y==0?localX:Double.NaN;}};
  assertEquals(.25,EarthTerrainSampler.surface(slope,.25,1,.25).orElseThrow(),0);
  assertTrue(EarthTerrainSampler.surface(flat,.25,-1000,.25).isEmpty());
 }
 @Test public void meshWindingFacesOutwardAndHasTopAndBottom(){
  for(boolean wall:new boolean[]{true,false})for(var face:EarthFormationMesh.faces(0,2,wall)){
   var p=face.points().getFirst();
   assertTrue("outward side or cap",face.nx()*p.x()+face.nz()*p.z()+face.ny()*(p.y()-.5)>-.01);
  }
 }
 @Test public void footprintsBuryTheirBaseOnSlopesAndRespectCeilings(){
  var slope=new TestColumn(){public double top(int x,int y,int z,double lx,double lz){queries++;return y==0?x*.2+.5:Double.NaN;}};
  var ground=EarthTerrainSampler.support(slope,1,1,0,.8,.8,3).orElseThrow();
  assertEquals(.38,ground.base(),.00001);assertEquals(3,ground.height(),.00001);
  var cave=new TestColumn(){public double clearance(double x,double y,double z,double limit){return Math.min(limit,1.2);}};
  var fitted=EarthTerrainSampler.support(cave,.25,1,.25,.5,.5,6).orElseThrow();
  assertTrue(fitted.height()<1.5);
 }
 @Test public void cliffsAndLiquidFootprintsNeverProduceFloatingStone(){
  var cliff=new TestColumn(){public double top(int x,int y,int z,double lx,double lz){return y==(x<0?0:3)?.5:Double.NaN;}};
  assertTrue(EarthTerrainSampler.support(cliff,0,3,0,1,1,3).isEmpty());
  var water=new TestColumn(){public boolean fluid(int x,int y,int z){return x<0&&y==1;}};
  assertTrue(EarthTerrainSampler.support(water,0,1,0,1,1,3).isEmpty());
 }
 private static class TestColumn implements EarthTerrainSampler.Column {
  boolean loaded=true,liquid;int queries;
  public boolean loaded(int x,int z){return loaded;}
  public int bottom(){return -64;}public int topExclusive(){return 320;}
  public boolean fluid(int x,int y,int z){return liquid&&y==1;}
  public double top(int x,int y,int z,double localX,double localZ){queries++;return y==0?.5:Double.NaN;}
  public double clearance(double x,double y,double z,double limit){return limit;}
 }
}

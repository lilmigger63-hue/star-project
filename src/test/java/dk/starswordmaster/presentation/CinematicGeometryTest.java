package dk.starswordmaster.presentation;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class CinematicGeometryTest {
 @Test public void celestialDomainKeepsSwordHiltsAboveTheGround(){for(var part:CinematicGeometry.ALL.get("heavenly_sword_domain").parts())for(var face:part.faces())for(var p:face.vertices())assertTrue("Crossguards and grips need to remain visible",p.y()>=-.1f);}
 @Test public void eclipseCoreLeavesTheCenterReadable(){
  var core=CinematicGeometry.ALL.get("eclipse_nova").parts().getFirst();
  for(var f:core.faces())for(int i=1;i<3;i++)assertFalse("Viewing ray crosses solid eclipse center",centerRayHits(f.vertices().get(0),f.vertices().get(i),f.vertices().get(i+1)));
 }
 private static boolean centerRayHits(CinematicGeometry.Point a,CinematicGeometry.Point b,CinematicGeometry.Point c){
  double ux=b.x()-a.x(),uy=b.y()-a.y(),uz=b.z()-a.z(),vx=c.x()-a.x(),vy=c.y()-a.y(),vz=c.z()-a.z();
  double nx=uy*vz-uz*vy,ny=uz*vx-ux*vz,nz=ux*vy-uy*vx;if(Math.abs(nz)<1e-9)return false;
  double t=(nx*a.x()+ny*(a.y()-.28)+nz*(a.z()+3))/nz;if(t<0||t>6)return false;
  double wx=-a.x(),wy=.28-a.y(),wz=-3+t-a.z();
  double d00=ux*ux+uy*uy+uz*uz,d01=ux*vx+uy*vy+uz*vz,d11=vx*vx+vy*vy+vz*vz,d20=wx*ux+wy*uy+wz*uz,d21=wx*vx+wy*vy+wz*vz,denom=d00*d11-d01*d01;
  if(Math.abs(denom)<1e-12)return false;double u=(d11*d20-d01*d21)/denom,v=(d00*d21-d01*d20)/denom;return u>=-1e-6&&v>=-1e-6&&u+v<=1.000001;
 }
 @Test public void primaryMeshesAreClosedVolumesRatherThanCrossedRibbons(){
  for(var mesh:CinematicGeometry.ALL.values())assertClosed(mesh.id(),mesh.faces());
 }
 private static void assertClosed(String id,List<CinematicGeometry.Face> faces){
  var edges=new HashMap<String,Integer>();double volume=0;
  for(var face:faces){
   var v=face.vertices();
   for(int i=0;i<v.size();i++){
    var a=v.get(i);var b=v.get((i+1)%v.size());if(a.equals(b))continue;
    String sa=a.toString(),sb=b.toString(),key=sa.compareTo(sb)<0?sa+sb:sb+sa;edges.merge(key,1,Integer::sum);
   }
   var a=v.getFirst();
   for(int i=1;i<v.size()-1;i++){
    var b=v.get(i);var c=v.get(i+1);
    volume+=(double)a.x()*(b.y()*c.z()-b.z()*c.y())+(double)a.y()*(b.z()*c.x()-b.x()*c.z())+(double)a.z()*(b.x()*c.y()-b.y()*c.x());
   }
  }
  for(var count:edges.values())assertEquals(id+" has an open or nonmanifold edge",2,count.intValue());
  assertTrue(id+" needs actual enclosed volume",Math.abs(volume)>0.00001);
 }
 @Test public void all54AbilitiesHaveCachedDistinctBoundedGeometry(){
  assertEquals(AbilityPresentation.ALL.keySet(),CinematicGeometry.ALL.keySet());var signatures=new HashSet<String>();
  for(var mesh:CinematicGeometry.ALL.values()){
   assertSame(mesh,CinematicGeometry.ALL.get(mesh.id()));assertFalse(mesh.faces().isEmpty());assertTrue(mesh.faces().size()<=96);
   for(var face:mesh.faces())for(var p:face.vertices()){assertTrue(Float.isFinite(p.x())&&Float.isFinite(p.y())&&Float.isFinite(p.z()));assertTrue(Math.abs(p.x())<=2&&Math.abs(p.y())<=2&&Math.abs(p.z())<=2);}
   assertTrue(mesh.id(),signatures.add(mesh.faces().toString()));
  }
 }
 @Test public void wholePartLodPreservesClosedVolumesAndCachedNormals(){
  for(var mesh:CinematicGeometry.ALL.values()){
   int previous=0;
   for(int quality=0;quality<3;quality++){
    var faces=new ArrayList<CinematicGeometry.Face>();for(var part:mesh.parts())if(part.minQuality()<=quality)faces.addAll(part.faces());
    assertFalse(mesh.id(),faces.isEmpty());assertTrue(faces.size()>=previous&&faces.size()<=96);previous=faces.size();assertClosed(mesh.id()+quality,faces);
    for(var f:faces){var n=f.normal();assertEquals(1,n.x()*n.x()+n.y()*n.y()+n.z()*n.z(),.0001);}
   }
   assertEquals(mesh.faces(),mesh.parts().stream().flatMap(p->p.faces().stream()).toList());
  }
 }
 @Test public void choreographyIsFiniteAndRevealsThenDissolves(){
  for(var style:AbilityPresentation.ALL.values()){
   assertEquals(0,CinematicGeometry.envelope(-1,style.ticks()),0);assertEquals(0,CinematicGeometry.envelope(style.ticks()+8,style.ticks()),0);
   assertTrue(CinematicGeometry.envelope(style.ticks()/2f,style.ticks())>0.8f);
   for(int i=0;i<100;i++)assertTrue(Float.isFinite(CinematicGeometry.envelope(i/2f,style.ticks())));
  }
 }
 @Test public void curseSealTracksConfirmedTargetWhileDefensiveWardsFollowCaster(){assertEquals(CinematicGeometry.Anchor.IMPACT,CinematicGeometry.ALL.get("curse_mark").anchor());assertEquals(CinematicGeometry.Anchor.CASTER,CinematicGeometry.ALL.get("frost_ward").anchor());}
 @Test public void defensiveAndElementalShapesAreNotAllRecoloredRings(){
  assertNotEquals(CinematicGeometry.ALL.get("chain_lightning").form(),CinematicGeometry.ALL.get("water_bolt").form());
  assertNotEquals(CinematicGeometry.ALL.get("tidal_wave").form(),CinematicGeometry.ALL.get("ocean_prison").form());
  assertNotEquals(CinematicGeometry.ALL.get("guard").form(),CinematicGeometry.ALL.get("quick_cut").form());
 }
}

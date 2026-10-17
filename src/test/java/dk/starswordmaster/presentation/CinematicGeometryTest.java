package dk.starswordmaster.presentation;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class CinematicGeometryTest {
 @Test public void all54AbilitiesHaveCachedDistinctBoundedGeometry(){
  assertEquals(AbilityPresentation.ALL.keySet(),CinematicGeometry.ALL.keySet());var signatures=new HashSet<String>();
  for(var mesh:CinematicGeometry.ALL.values()){
   assertSame(mesh,CinematicGeometry.ALL.get(mesh.id()));assertFalse(mesh.faces().isEmpty());assertTrue(mesh.faces().size()<=96);
   for(var face:mesh.faces())for(var p:face.vertices()){assertTrue(Float.isFinite(p.x())&&Float.isFinite(p.y())&&Float.isFinite(p.z()));assertTrue(Math.abs(p.x())<=2&&Math.abs(p.y())<=2&&Math.abs(p.z())<=2);}
   assertTrue(mesh.id(),signatures.add(mesh.faces().toString()));
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

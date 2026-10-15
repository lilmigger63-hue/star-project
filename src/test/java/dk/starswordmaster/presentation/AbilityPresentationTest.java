package dk.starswordmaster.presentation;
import dk.starswordmaster.progression.Ability;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class AbilityPresentationTest {
 @Test public void coversExactlyEveryActiveAbility(){
  var expected=new HashSet<String>();Ability.ALL.values().stream().filter(Ability::active).forEach(a->expected.add(a.id()));
  assertEquals(32,expected.size());assertEquals(expected,AbilityPresentation.ALL.keySet());
  for(var p:AbilityPresentation.ALL.values()){assertTrue(p.ticks()>=8&&p.ticks()<=24);assertTrue(p.radius()>0&&p.radius()<=6);assertTrue(p.samples()<=12);}
 }
 @Test public void representativeAbilitiesHaveDistinctAppropriateActions(){
  assertEquals(AbilityPresentation.Clip.THRUST,AbilityPresentation.ALL.get("piercing_thrust").clip());
  assertEquals(AbilityPresentation.Element.FIRE,AbilityPresentation.ALL.get("flame_blade").element());
  assertEquals(AbilityPresentation.Element.FROST,AbilityPresentation.ALL.get("glacier_sweep").element());
  assertEquals(AbilityPresentation.Element.STAR,AbilityPresentation.ALL.get("aura_wave").element());
  assertEquals(AbilityPresentation.Clip.HEAL,AbilityPresentation.ALL.get("restoration").clip());
  assertEquals(AbilityPresentation.Clip.GUARD,AbilityPresentation.ALL.get("guardian_stance").clip());
  assertFalse(AbilityPresentation.ALL.containsKey("sword_mastery"));
  assertEquals("SWEEP",AbilityPresentation.ALL.get("heavy_sweep").clip().name());
  assertEquals("RAPID",AbilityPresentation.ALL.get("rapid_slash").clip().name());
  assertEquals("CHOP",AbilityPresentation.ALL.get("heavy_cut").pattern().name());
  assertEquals("CAGE",AbilityPresentation.ALL.get("ice_prison").pattern().name());
 }
 @Test public void lateEventsKeepTheirOriginalLifetime(){
  var p=AbilityPresentation.ALL.get("quick_cut");assertEquals(0,p.elapsed(100,99));assertEquals(3,p.elapsed(100,103));assertEquals(p.ticks(),p.elapsed(100,130));
 }
 @Test public void eventLedgerRejectsDuplicatesOldEventsAndExpires(){
  var ledger=new PresentationLedger();UUID p=UUID.randomUUID();
  assertTrue(ledger.accept(p,1,100,100));assertFalse(ledger.accept(p,1,100,101));assertFalse(ledger.accept(p,0,100,101));
  assertTrue(ledger.accept(p,2,101,102));assertFalse(ledger.accept(UUID.randomUUID(),1,0,100));
  assertFalse(ledger.accept(UUID.randomUUID(),1,110,100));ledger.clear();assertTrue(ledger.accept(p,1,100,100));
 }
 @Test public void timedParticleGeometryIsFiniteBoundedAndEnds(){
  for(var p:AbilityPresentation.ALL.values())for(int tick=0;tick<=p.ticks();tick++){
   var points=ParticlePattern.points(p,tick);assertTrue(points.size()<=24);
   for(var v:points){assertTrue(Double.isFinite(v.x())&&Double.isFinite(v.y())&&Double.isFinite(v.z()));assertTrue(Math.abs(v.x())<=6.1);assertTrue(Math.abs(v.z())<=6.1);}
   if(tick==p.ticks())assertTrue(points.isEmpty());
  }
 }
}

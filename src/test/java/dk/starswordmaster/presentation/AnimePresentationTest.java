package dk.starswordmaster.presentation;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class AnimePresentationTest {
 @Test public void everyAbilityHasItsOwnSpatialChoreography(){
  var signatures=new HashMap<String,String>();
  AbilityPresentation.ALL.forEach((id,p)->{var signature=new StringBuilder();for(int t=0;t<p.ticks();t+=2)for(var v:ParticlePattern.points(p,t))signature.append(String.format(Locale.ROOT,"%.4f,%.4f,%.4f:%s;",v.x(),v.y(),v.z(),v.space()));assertNull("Duplicate VFX geometry for "+id,signatures.put(signature.toString(),id));});assertEquals(54,signatures.size());
 }
 @Test public void particleFrameBudgetAndGeometryStayBounded(){
  for(var p:AbilityPresentation.ALL.values())for(int t=-1;t<=p.ticks()+1;t++){
   var points=ParticlePattern.points(p,t);assertTrue(points.size()<=12);
   for(var v:points){assertTrue(Double.isFinite(v.x())&&Double.isFinite(v.y())&&Double.isFinite(v.z()));assertTrue(Math.abs(v.x())<=6.1&&Math.abs(v.y())<=6.1&&Math.abs(v.z())<=6.1);}
   if(t<0||t>=p.ticks())assertTrue(points.isEmpty());
  }
 }
 @Test public void shortSparseAccentsCoverEveryInkAndCoordinateSpace(){
  var inks=EnumSet.noneOf(ParticlePattern.Ink.class);var spaces=EnumSet.noneOf(ParticlePattern.Space.class);
  for(var p:AbilityPresentation.ALL.values())for(int t=0;t<p.ticks();t++)for(var v:ParticlePattern.points(p,t)){inks.add(v.ink());spaces.add(v.space());assertTrue(v.size()>0&&v.size()<=1.5);assertTrue(v.life()>=2&&v.life()<=12);assertTrue(Float.isFinite(v.angle()));if(v.space()==ParticlePattern.Space.RAY)assertTrue(v.z()>=0&&v.z()<=1);}
  assertEquals(EnumSet.allOf(ParticlePattern.Ink.class),inks);assertEquals(EnumSet.allOf(ParticlePattern.Space.class),spaces);
 }
 @Test public void immediateImpactAccentsNeverExceedOldEightParticleLimit(){for(var p:AbilityPresentation.ALL.values()){var impact=ParticlePattern.impact(p);assertTrue(impact.size()<=8);for(var v:impact){assertEquals(ParticlePattern.Space.IMPACT,v.space());assertTrue(v.life()<=4&&v.size()<=0.55f);}}}
}

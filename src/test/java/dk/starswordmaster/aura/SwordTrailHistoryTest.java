package dk.starswordmaster.aura;
import org.junit.Test;
import static org.junit.Assert.*;
public class SwordTrailHistoryTest {
 private void sample(SwordTrailHistory h,double age){h.add(age,Math.sin(age)*.2,1,Math.cos(age)*.2,Math.sin(age),1+Math.sin(age)*.1,Math.cos(age));}
 @Test public void boundedActualMovementProducesCurvedThreeDimensionalRibbon(){var h=new SwordTrailHistory();for(int i=0;i<1000;i++)sample(h,i*.1);assertTrue(h.size()<=SwordTrailHistory.MAX_SAMPLES);var faces=h.mesh(99.9,2);assertFalse(faces.isEmpty());assertTrue(faces.size()<=24);boolean depth=false;for(var f:faces){assertTrue(f.alpha()>0&&f.alpha()<=1);for(var p:f.points()){assertTrue(Double.isFinite(p.x()+p.y()+p.z()));if(Math.abs(p.z())>.01)depth=true;}}assertTrue(depth);}
 @Test public void qualityReducesGeometryAndExpiredHistoryIsEmpty(){var h=new SwordTrailHistory();for(int i=0;i<10;i++)sample(h,i*.3);assertTrue(h.mesh(2.7,0).size()<h.mesh(2.7,2).size());assertTrue(h.mesh(10,2).isEmpty());}
 @Test public void duplicateTimesInvalidTransformsAndTeleportsCannotMakeHugeTrails(){var h=new SwordTrailHistory();sample(h,1);sample(h,1);assertEquals(1,h.size());h.add(2,Double.NaN,0,0,0,0,0);assertEquals(1,h.size());h.add(2,100,0,0,101,0,0);assertEquals(1,h.size());h.clear();assertEquals(0,h.size());}
 @Test public void stationarySwordAndBackwardTimeDoNotLeaveRibbons(){var h=new SwordTrailHistory();for(int i=0;i<20;i++)h.add(i*.3,0,0,0,0,1,0);assertTrue(h.mesh(5.7,2).isEmpty());sample(h,1);assertEquals(1,h.size());}
}

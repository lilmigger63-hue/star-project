package dk.starswordmaster.ui;
import org.junit.Test;
import java.math.BigInteger;
import static org.junit.Assert.*;
public class TreeScreenLayoutTest {
 @Test public void regionsAndControlsFitSmallAndWideScreens(){
  for(int[] size:new int[][]{{320,240},{427,240},{640,360},{853,481},{1280,720}}){
   var l=TreeScreenLayout.of(size[0],size[1]);
   assertTrue(l.treeWidth()>100);assertTrue(l.bodyBottom()>l.bodyTop());
   assertTrue(l.bodyBottom()<=size[1]-48);assertTrue(l.detailWidth()>=150);
   assertTrue(l.detailX()>=0&&l.detailX()+l.detailWidth()<=size[0]);
   assertEquals(size[0]>=500,l.sidePanel());
   if(l.sidePanel())assertTrue(l.treeWidth()<=l.detailX());
  }
 }
 @Test public void hugeNumbersStayShortAndNonnegative(){
  assertEquals("999999",ProgressNumbers.compact(new BigInteger("999999")));
  assertEquals("1.234e30",ProgressNumbers.compact(new BigInteger("1234567890123456789012345678901")));
  assertEquals("0",ProgressNumbers.compact(BigInteger.ZERO));
 }
}

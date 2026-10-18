package dk.starswordmaster.progression;

import org.junit.Test;
import static org.junit.Assert.*;

public class OwnershipPolicyTest {
 private static final class Owner {
  Owner owner;boolean protectedOwner;
  Owner(boolean protectedOwner){this.protectedOwner=protectedOwner;}
 }
 private boolean excluded(Owner entity){return OwnershipPolicy.hasProtectedOwner(entity,n->n.owner,n->n.protectedOwner);}
 @Test public void hostileOwnerDoesNotExcludeItsSummon(){
  var evoker=new Owner(false);var vex=new Owner(false);vex.owner=evoker;
  assertFalse(excluded(vex));assertFalse(excluded(evoker));
 }
 @Test public void playerAndIndirectPlayerOwnedSummonsAreExcluded(){
  var player=new Owner(true);var summon=new Owner(false);summon.owner=player;
  var secondSummon=new Owner(false);secondSummon.owner=summon;
  assertTrue(excluded(summon));assertTrue(excluded(secondSummon));
 }
 @Test public void protectedReferenceExcludesPetWithoutAResolvedOwner(){
  var offlineTamedPet=new Owner(true);assertTrue(excluded(offlineTamedPet));
  var summon=new Owner(false);summon.owner=offlineTamedPet;assertTrue(excluded(summon));
 }
 @Test public void malformedCyclesAndExcessivelyLongChainsAreBoundedAndExcluded(){
  var a=new Owner(false);var b=new Owner(false);a.owner=b;b.owner=a;assertTrue(excluded(a));
  var chain=new Owner(false);for(int i=0;i<100;i++){var next=new Owner(false);next.owner=chain;chain=next;}
  assertTrue(excluded(chain));
 }
}

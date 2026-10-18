package dk.starswordmaster.progression;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** Bounded ownership classification independent of entity implementation details. */
public final class OwnershipPolicy {
 private OwnershipPolicy(){}
 public static <T> boolean hasProtectedOwner(T entity,Function<T,T> owner,Predicate<T> protectedOwner){
  Set<T> visited=Collections.newSetFromMap(new IdentityHashMap<>());
  T current=entity;
  for(int depth=0;current!=null&&depth<64;depth++){
   if(!visited.add(current)||protectedOwner.test(current))return true;
   current=owner.apply(current);
  }
  // Refuse malformed cycles or pathological chains without hanging the server death callback.
  return current!=null;
 }
}

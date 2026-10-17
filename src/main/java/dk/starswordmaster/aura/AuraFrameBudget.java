package dk.starswordmaster.aura;

import java.util.HashMap;
import java.util.Map;

/** One render frame: independent of particle budgets and bounded even in crowded multiplayer scenes. */
public final class AuraFrameBudget {
 public static final int MAX_QUADS=1024,MAX_CASTERS=64;
 private final Map<Integer,Integer> hands=new HashMap<>();
 private int remaining=MAX_QUADS;
 public void reset(){hands.clear();remaining=MAX_QUADS;}
 public int take(int entityId,int hand,int requested,double distanceSquared){
  if(!Double.isFinite(distanceSquared)||distanceSquared<0||distanceSquared>4096||requested<=0||hand<0||hand>1)return 0;
  int mask=hands.getOrDefault(entityId,0),bit=1<<hand;
  if((mask&bit)!=0||(mask==0&&hands.size()>=MAX_CASTERS))return 0;
  int granted=Math.min(requested,BladeAuraMesh.MAX_QUADS);
  if(granted<4||granted>remaining)return 0;
  hands.put(entityId,mask|bit);remaining-=granted;return granted;
 }
 public int remaining(){return remaining;}
 public int casters(){return hands.size();}
}

package dk.starswordmaster.circles;

/** At most 64 casters, two physical hands and 384 textured quads in one native frame. */
public final class CircleFrameBudget {
 public static final int MAX_CASTERS=64,MAX_CIRCLES=128,MAX_QUADS=MAX_CIRCLES*MagicCircleMesh.MAX_LAYERS;
 private final int[] entities=new int[MAX_CASTERS];
 private final byte[] hands=new byte[MAX_CASTERS];
 private int casters,circles;
 public boolean take(int entityId,int hand){
  if(hand<0||hand>1||circles==MAX_CIRCLES)return false;
  int index=0;while(index<casters&&entities[index]!=entityId)index++;
  if(index==casters){if(casters==MAX_CASTERS)return false;entities[casters]=entityId;hands[casters++]=0;}
  int bit=1<<hand;if((hands[index]&bit)!=0)return false;
  hands[index]|=(byte)bit;circles++;return true;
 }
 public int circles(){return circles;}
 public void reset(){casters=circles=0;}
}

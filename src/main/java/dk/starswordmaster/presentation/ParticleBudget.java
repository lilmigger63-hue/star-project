package dk.starswordmaster.presentation;
/** Shared client limits, kept at the existing 256 particles/tick and 64-block draw distance. */
public final class ParticleBudget {
 public static final int MAX_NEW=256,MAX_CASTERS=64,MAX_DISTANCE=64;
 private int remaining=MAX_NEW;
 public void reset(){remaining=MAX_NEW;}
 public int remaining(){return remaining;}
 public boolean take(double distanceSquared){if(!Double.isFinite(distanceSquared)||distanceSquared<0||distanceSquared>MAX_DISTANCE*MAX_DISTANCE||remaining==0)return false;remaining--;return true;}
}

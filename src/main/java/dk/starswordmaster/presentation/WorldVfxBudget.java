package dk.starswordmaster.presentation;
/** Combined limit equals the previous two independent 2048-face renderer caps. */
public final class WorldVfxBudget {
 public static final int PER_RENDERER=2048,TOTAL=4096;
 public static final WorldVfxBudget SHARED=new WorldVfxBudget();
 private int terrain,cinematic;
 public void reset(){terrain=cinematic=0;}
 public int used(){return terrain+cinematic;}
 public boolean takeTerrain(int count){if(count<0||count>PER_RENDERER-terrain||count>TOTAL-used())return false;terrain+=count;return true;}
 public boolean takeCinematic(int count){if(count<0||count>PER_RENDERER-cinematic||count>TOTAL-used())return false;cinematic+=count;return true;}
}

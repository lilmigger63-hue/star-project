package dk.starswordmaster.ui;
/** Scaled HUD placement, retaining the original design coordinates for uniform pixel-art rendering. */
public record SkillHudLayout(int x,int y) {
 public static final int WIDTH=182,HEIGHT=22,SLOT_PITCH=20,ICON_SIZE=16,SLOTS=5,GAP=2;
 public static final float SCALE=0.75f,SCALED_WIDTH=WIDTH*SCALE,SCALED_HEIGHT=HEIGHT*SCALE;
 public static final int VANILLA_WIDTH=182,VANILLA_HEIGHT=22,VANILLA_OFFHAND_WIDTH=29,VANILLA_TOP_OFFSET=23;
 public static final int VANILLA_LIFT=29;
 public static SkillHudLayout forScreen(int scaledWidth,int scaledHeight){return forResources(ResourceHudLayout.forScreen(scaledWidth,scaledHeight));}
 public static SkillHudLayout forResources(ResourceHudLayout resources){return new SkillHudLayout(resources.barX(),resources.barY(2)+ResourceHudLayout.BAR_HEIGHT+GAP);}
 public int slotX(int slot){return x+1+(2+slot)*SLOT_PITCH;}
 public int iconX(int slot){return slotX(slot)+2;}
 public int iconY(){return y+3;}
}

package dk.starswordmaster.ui;
/** Minecraft 1.21.10 InGameHud geometry, in GUI units rather than framebuffer pixels. */
public record SkillHudLayout(int x,int y) {
 public static final int WIDTH=182,HEIGHT=22,SLOT_PITCH=20,ICON_SIZE=16,SLOTS=5,GAP=0;
 // Baseline lift for vanilla indicators when the resource portrait fits alongside the skill bar.
 public static final int VANILLA_LIFT=44;
 // The selected-slot border and offhand frame start one GUI unit above the 22-unit background.
 public static final int VANILLA_TOP_OFFSET=HEIGHT+1;
 public static SkillHudLayout forScreen(int scaledWidth,int scaledHeight){return new SkillHudLayout(scaledWidth/2-WIDTH/2,scaledHeight-VANILLA_TOP_OFFSET-HEIGHT-GAP);}
 public int slotX(int slot){return x+1+(2+slot)*SLOT_PITCH;}
 public int iconX(int slot){return slotX(slot)+2;}
 public int iconY(){return y+3;}
}

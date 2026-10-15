package dk.starswordmaster.ui;
/** Minecraft 1.21.10 InGameHud geometry, in GUI units rather than framebuffer pixels. */
public record SkillHudLayout(int x,int y) {
 public static final int WIDTH=182,HEIGHT=22,SLOT_PITCH=20,ICON_SIZE=16,SLOTS=5,GAP=4;
 // Keep vanilla XP, hearts, armor, food, air and mount indicators above the resource strips.
 public static final int VANILLA_LIFT=HEIGHT+GAP+18;
 public static SkillHudLayout forScreen(int scaledWidth,int scaledHeight){return new SkillHudLayout(scaledWidth/2-WIDTH/2,scaledHeight-HEIGHT*2-GAP);}
 public int slotX(int slot){return x+1+(2+slot)*SLOT_PITCH;}
 public int iconX(int slot){return slotX(slot)+2;}
 public int iconY(){return y+3;}
 public int manaY(){return y-12;}
 public int staminaY(){return y-6;}
}

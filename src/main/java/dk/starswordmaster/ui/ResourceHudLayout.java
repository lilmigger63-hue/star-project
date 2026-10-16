package dk.starswordmaster.ui;
/** Bottom-left resources; narrow windows stack above the compact native indicators. All units are scaled GUI units. */
public record ResourceHudLayout(int x,int y,boolean stacked,int vanillaLift) {
 public static final int WIDTH=176,HEIGHT=40,PORTRAIT_SIZE=40,BAR_WIDTH=132,BAR_HEIGHT=12;
 public static final int MARGIN=6,SIDE_GAP=8,STACK_GAP=4;
 public static ResourceHudLayout forScreen(int scaledWidth,int scaledHeight){return forScreen(scaledWidth,scaledHeight,0);}
 public static ResourceHudLayout forScreen(int scaledWidth,int scaledHeight,int indicatorExtra){
  float skillRight=MARGIN+PORTRAIT_SIZE+4+SkillHudLayout.SCALED_WIDTH;
  int offhandLeft=scaledWidth/2-SkillHudLayout.VANILLA_WIDTH/2-SkillHudLayout.VANILLA_OFFHAND_WIDTH;
  boolean stacked=offhandLeft<skillRight+SIDE_GAP;
  int lift=SkillHudLayout.VANILLA_LIFT;
  // Reserve armor and air above the compact hunger/XP group, including extra native rows.
  int groupHeight=HEIGHT+SkillHudLayout.GAP+(int)Math.ceil(SkillHudLayout.SCALED_HEIGHT);
  int y=stacked?scaledHeight-59-lift-Math.max(0,indicatorExtra)-STACK_GAP-groupHeight:scaledHeight-SkillHudLayout.VANILLA_HEIGHT-4-HEIGHT;
  return new ResourceHudLayout(MARGIN,Math.max(MARGIN,y),stacked,lift);
 }
 public int reservedTop(int height,int indicatorExtra){return Math.min(y,height-59-vanillaLift-Math.max(0,indicatorExtra));}
 public int tooltipLift(int height,int indicatorExtra){return tooltipLift(height,indicatorExtra,true);}
 public int tooltipLift(int height,int indicatorExtra,boolean statusBars){return (statusBars?0:14)+ Math.max(vanillaLift,height-50-reservedTop(height,indicatorExtra)+STACK_GAP);}
 // Reserve the queued-message button below chat and native item/actionbar text above the HUD.
 public int chatLift(int height,int indicatorExtra){return Math.max(0,height-28-reservedTop(height,indicatorExtra)+30);}
 public int chatHeight(int height,int requested,double scale,int indicatorExtra){return Math.min(requested,Math.max(9,(int)Math.floor((height-40-chatLift(height,indicatorExtra)-MARGIN)/scale)));}
 /** Vanilla puts armor above its health rows; the selected item label otherwise stays at height - 59. */
 public static int tooltipExtraLift(double maxHealth,double absorption){
  return tooltipExtraLift(maxHealth,0,0,absorption);
 }
 public static int tooltipExtraLift(double maxHealth,double health,int retainedHealth,double absorption){
  double displayed=Math.max(maxHealth,Math.max(health,retainedHealth));
  int rows=Math.max(1,(int)Math.ceil((displayed+Math.ceil(absorption))/20));
  return (rows-1)*Math.max(10-(rows-2),3);
 }
 public int barX(){return x+PORTRAIT_SIZE+4;}
 public int barY(int index){return y+14*index;}
}

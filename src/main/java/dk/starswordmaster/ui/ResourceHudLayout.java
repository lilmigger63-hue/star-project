package dk.starswordmaster.ui;
/** Resource portrait and meters in scaled GUI units, beside the existing skill bar when space permits. */
public record ResourceHudLayout(int x,int y,boolean stacked,int vanillaLift) {
 public static final int WIDTH=176,HEIGHT=40,PORTRAIT_SIZE=40,BAR_WIDTH=132,BAR_HEIGHT=12;
 public static ResourceHudLayout forScreen(int scaledWidth,int scaledHeight){
  var skills=SkillHudLayout.forScreen(scaledWidth,scaledHeight);boolean stacked=skills.x()<WIDTH+8+6;
  int x=stacked?scaledWidth/2-WIDTH/2:skills.x()-8-WIDTH;
  // Resource portrait stays at its existing screen position as only the ability bar moves down.
  int resourceAnchor=scaledHeight-2*SkillHudLayout.HEIGHT-4;
  int y=stacked?resourceAnchor-4-HEIGHT:resourceAnchor+SkillHudLayout.HEIGHT-HEIGHT;
  // The locator's lowest arrow reaches height - 18; leave two units above the stacked panel.
  int lift=stacked?scaledHeight-18-y+2:SkillHudLayout.VANILLA_LIFT;
  return new ResourceHudLayout(x,y,stacked,lift);
 }
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

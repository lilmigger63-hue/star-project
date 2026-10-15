package dk.starswordmaster.ui;
/** GUI-scaled bounds shared by rendering and scrolling. */
public record StatusLayout(int x,int y,int width,int height) {
 public static final int CHARACTER_HEIGHT=122,STATS_HEIGHT=146;
 public static StatusLayout forScreen(int screenWidth,int screenHeight){
  int margin=Math.min(8,Math.min(screenWidth,screenHeight)/8);
  int w=Math.max(1,Math.min(520,screenWidth-2*margin)),h=Math.max(1,Math.min(392,screenHeight-2*margin));
  return new StatusLayout((screenWidth-w)/2,(screenHeight-h)/2,w,h);
 }
 public int bodyX(){return x+12;}
 public int bodyWidth(){return Math.max(1,width-24);}
 public int bodyTop(){return y+30;}
 public int bodyBottom(){return y+height-28;}
 public int bodyHeight(){return Math.max(1,bodyBottom()-bodyTop());}
 public boolean twoColumns(){return bodyWidth()>=420;}
 public int columnWidth(){return twoColumns()?(bodyWidth()-8)/2:bodyWidth();}
 public int statsX(){return twoColumns()?bodyX()+columnWidth()+8:bodyX();}
 public int statsTop(){return twoColumns()?bodyTop():bodyTop()+CHARACTER_HEIGHT+8;}
 public int summaryHeight(){return twoColumns()?Math.max(CHARACTER_HEIGHT,STATS_HEIGHT)+8:CHARACTER_HEIGHT+STATS_HEIGHT+16;}
 public int statButtonX(){return statsX()+columnWidth()-26;}
 public int statButtonY(int stat,double scroll){return statsTop()+36+26*stat-(int)scroll;}
 public boolean statButtonInView(int buttonY){return buttonY>=bodyTop()&&buttonY+18<=bodyBottom();}
 public double clampScroll(double scroll,int contentHeight){return Math.max(0,Math.min(Math.max(0,contentHeight-bodyHeight()),scroll));}
 public int thumbHeight(int contentHeight){return Math.min(bodyHeight(),Math.max(12,bodyHeight()*bodyHeight()/Math.max(1,contentHeight)));}
 public int thumbTop(double scroll,int contentHeight){return bodyTop()+(int)((bodyHeight()-thumbHeight(contentHeight))*clampScroll(scroll,contentHeight)/Math.max(1,contentHeight-bodyHeight()));}
 public double scrollForThumb(double mouseY,int contentHeight,double grabOffset){int travel=bodyHeight()-thumbHeight(contentHeight);return travel<=0?0:clampScroll((mouseY-bodyTop()-grabOffset)*(contentHeight-bodyHeight())/travel,contentHeight);}
}

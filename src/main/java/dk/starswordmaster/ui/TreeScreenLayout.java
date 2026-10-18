package dk.starswordmaster.ui;
/** Shared screen regions; narrow GUIs switch between full-width tree and details pages. */
public record TreeScreenLayout(int width,int height,boolean sidePanel,int treeWidth,int detailX,int detailWidth,int bodyTop,int bodyBottom) {
 public static TreeScreenLayout of(int width,int height){
  boolean side=width>=500;int detail=side?180:width;
  return new TreeScreenLayout(width,height,side,side?width-detail:width,side?width-detail:0,detail,54,height-50);
 }
 public double centerX(){return treeWidth/2.0;}
 public double centerY(){return (bodyTop+bodyBottom)/2.0;}
 public boolean inTree(double x,double y){return x>=0&&x<treeWidth&&y>=bodyTop&&y<bodyBottom;}
}

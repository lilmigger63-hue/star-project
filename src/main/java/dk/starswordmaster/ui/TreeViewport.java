package dk.starswordmaster.ui;
public final class TreeViewport {
 private double zoom=0.35,panX,panY;
 public double zoom(){return zoom;}
 public void pan(double x,double y){if(Double.isFinite(x)&&Double.isFinite(y)){panX+=x;panY+=y;}}
 public double screenX(double world,double center){return center+panX+world*zoom;}
 public double screenY(double world,double center){return center+panY+world*zoom;}
 public double worldX(double screen,double center){return (screen-center-panX)/zoom;}
 public double worldY(double screen,double center){return (screen-center-panY)/zoom;}
 public void zoomAt(double x,double y,double centerX,double centerY,double delta){
  if(!Double.isFinite(delta))return;
  double next=Math.max(0.12,Math.min(1.8,zoom*Math.pow(1.15,delta)));
  double wx=worldX(x,centerX),wy=worldY(y,centerY);zoom=next;panX=x-centerX-wx*zoom;panY=y-centerY-wy*zoom;
 }
 public void fit(double width,double height){zoom=Math.max(0.12,Math.min(0.75,Math.min((width-32)/1040,(height-32)/930)));panX=50*zoom;panY=-20*zoom;}
}

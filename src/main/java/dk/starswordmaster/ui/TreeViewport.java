package dk.starswordmaster.ui;

/** Reversible world/screen projection with pointer-anchored zoom and cached layout framing. */
public final class TreeViewport {
 private double zoom=0.35,panX,panY,minimumZoom=0.12;
 public double zoom(){return zoom;}
 public void pan(double x,double y){if(Double.isFinite(x)&&Double.isFinite(y)){panX+=x;panY+=y;}}
 public double screenX(double world,double center){return center+panX+world*zoom;}
 public double screenY(double world,double center){return center+panY+world*zoom;}
 public double worldX(double screen,double center){return (screen-center-panX)/zoom;}
 public double worldY(double screen,double center){return (screen-center-panY)/zoom;}
 public void zoomAt(double x,double y,double centerX,double centerY,double delta){
  if(!Double.isFinite(delta)||!Double.isFinite(x)||!Double.isFinite(y))return;
  double next=Math.max(minimumZoom,Math.min(1.8,zoom*Math.pow(1.15,delta)));
  double wx=worldX(x,centerX),wy=worldY(y,centerY);zoom=next;panX=x-centerX-wx*zoom;panY=y-centerY-wy*zoom;
 }
 public void fit(double width,double height){fit(SkillTreeLayout.BOUNDS,width,height);}
 /** Center a world point without changing the chosen zoom. */
 public void focus(double worldX,double worldY){
  if(Double.isFinite(worldX)&&Double.isFinite(worldY)){panX=-worldX*zoom;panY=-worldY*zoom;}
 }
 public void focus(SkillTreeLayout.Sector sector,double width,double height){fit(SkillTreeLayout.bounds(sector),width,height);}
 private void fit(SkillTreeLayout.Bounds bounds,double width,double height){
  if(!Double.isFinite(width)||!Double.isFinite(height)||width<=0||height<=0)return;
  double margin=Math.min(12,Math.min(width,height)/4);
  zoom=Math.min(0.75,Math.min((width-2*margin)/bounds.width(),(height-2*margin)/bounds.height()));
  minimumZoom=Math.min(0.12,zoom);
  focus((bounds.left()+bounds.right())/2,(bounds.top()+bounds.bottom())/2);
 }
}

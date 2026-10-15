package dk.starswordmaster.presentation;
import java.util.*;
/** Deterministic local coordinates. Ray z is the fraction from muzzle to impact. */
public final class ParticlePattern {
 public record Point(double x,double y,double z){}
 private ParticlePattern(){}
 public static List<Point> points(AbilityPresentation p,int tick){
  if(tick<0||tick>=p.ticks())return List.of();
  var points=new ArrayList<Point>();double t=(double)tick/p.ticks(),r=p.radius();int n=p.samples();
  for(int i=0;i<n;i++){
   double u=(double)i/n,a=u*Math.PI*2+t*3;
   switch(p.pattern()){
    case SLASH->{double phase=p.clip()==AbilityPresentation.Clip.RAPID?(t*2)%1:t;double sweep=-1.1+2.2*phase+(u-0.5)*0.5;points.add(new Point(Math.sin(sweep)*r,1.1+0.35*Math.cos(a),Math.cos(sweep)*r));}
    case CHOP->{double sweep=0.2+t*2.5+(u-0.5)*0.35;points.add(new Point((u-0.5)*0.2,1.15+Math.cos(sweep)*r*0.5,Math.sin(sweep)*r));}
    case CAGE->{double side=(i%4)*Math.PI/2;points.add(new Point(Math.cos(side)*r,0.1+u*2,Math.sin(side)*r));}
    case THRUST->points.add(new Point((u-0.5)*0.15,1.25,(0.3+u*0.7)*r));
    case RAY->{if(tick==0)points.add(new Point(0,0,u));}
    case BURST->points.add(new Point(Math.cos(a)*r*(0.3+t*0.7),0.3+u*1.7,Math.sin(a)*r*(0.3+t*0.7)));
    case RING->{double radius=r*(0.2+t*0.8);points.add(new Point(Math.cos(a)*radius,0.15,Math.sin(a)*radius));}
    case WARD->points.add(new Point(Math.cos(a)*r,1.1+Math.sin(a)*r,0.7));
    case AURA,HEAL->points.add(new Point(Math.cos(a)*r,0.2+1.8*t,Math.sin(a)*r));
    case DASH->points.add(new Point(Math.cos(a)*r,0.3+u,-0.5-t));
   }
  }
  return List.copyOf(points);
 }
}

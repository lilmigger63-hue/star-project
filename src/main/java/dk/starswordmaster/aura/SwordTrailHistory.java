package dk.starswordmaster.aura;
import java.util.*;
/** Bounded actual blade samples; no entity references, physics, particles or gameplay state. */
public final class SwordTrailHistory {
 public static final int MAX_SAMPLES=16,MAX_QUADS=24;
 public record Point(double x,double y,double z){Point add(Point p){return new Point(x+p.x,y+p.y,z+p.z);}Point sub(Point p){return new Point(x-p.x,y-p.y,z-p.z);}Point scale(double s){return new Point(x*s,y*s,z*s);}double length(){return Math.sqrt(x*x+y*y+z*z);}}
 public record Face(List<Point> points,float alpha,boolean highlight){}
 private final double[][] data=new double[MAX_SAMPLES][7];private int head,count,revision,cachedRevision=-1,cachedQuality=-1;private double cachedAge;private List<Face> cached=List.of();
 public int size(){return count;}
 public double lastAge(){return count==0?Double.NEGATIVE_INFINITY:value(count-1,0);}
 public void clear(){head=count=0;revision++;cached=List.of();}
 private double value(int index,int channel){return data[(head+index)%MAX_SAMPLES][channel];}
 public void add(double age,double bx,double by,double bz,double tx,double ty,double tz){
  if(!Double.isFinite(age)||!Double.isFinite(bx)||!Double.isFinite(by)||!Double.isFinite(bz)||!Double.isFinite(tx)||!Double.isFinite(ty)||!Double.isFinite(tz))return;
  if(Math.hypot(Math.hypot(tx-bx,ty-by),tz-bz)>4)return;
  if(count>0){double previous=lastAge();if(age<previous||age-previous>4)clear();else if(age-previous<.25)return;
   if(count>0&&Math.hypot(Math.hypot(bx-value(count-1,1),by-value(count-1,2)),bz-value(count-1,3))>6)clear();}
  if(count==MAX_SAMPLES){head=(head+1)%MAX_SAMPLES;count--;}
  revision++;
  var row=data[(head+count++)%MAX_SAMPLES];row[0]=age;row[1]=bx;row[2]=by;row[3]=bz;row[4]=tx;row[5]=ty;row[6]=tz;
 }
 private Point curve(double at,int channel,int first){
  int i=(int)at;double t=at-i;int a=Math.max(first,i-1),b=Math.min(count-1,i),c=Math.min(count-1,i+1),d=Math.min(count-1,i+2);double[] xyz=new double[3];
  for(int j=0;j<3;j++){double p=value(a,channel+j),q=value(b,channel+j),r=value(c,channel+j),s=value(d,channel+j);xyz[j]=.5*((2*q)+(-p+r)*t+(2*p-5*q+4*r-s)*t*t+(-p+3*q-3*r+s)*t*t*t);}
  return new Point(xyz[0],xyz[1],xyz[2]);
 }
 public List<Face> mesh(double now,int quality){
  if(count<2||!Double.isFinite(now))return List.of();int q=Math.clamp(quality,0,2);double quantum=Math.floor(now*4)/4;if(cachedRevision==revision&&cachedQuality==q&&cachedAge==quantum)return cached;double life=q==0?1.5:q==1?2.5:4;int first=0;while(first<count&&now-value(first,0)>life)first++;
  if(count-first<2)return List.of();int segments=Math.min(q==0?4:q==1?8:12,(count-first-1)*2);var out=new ArrayList<Face>(MAX_QUADS);
  for(int n=0;n<segments;n++){
   double a=first+(count-first-1)*(double)n/segments,b=first+(count-first-1)*(double)(n+1)/segments;
   Point baseA=curve(a,1,first),tipA=curve(a,4,first),baseB=curve(b,1,first),tipB=curve(b,4,first);Point motion=tipB.sub(tipA);
   if(motion.length()+baseB.sub(baseA).length()<.001)continue;
   double sampleAge=value((int)b,0);float alpha=(float)(.72*Math.pow(Math.clamp(1-(now-sampleAge)/life,0,1),1.4));if(alpha<=.01f)continue;
   out.add(new Face(List.of(baseA,tipA,tipB,baseB),alpha,false));
   if(q>0){Point axis=tipA.sub(baseA);Point normal=new Point(axis.y*motion.z-axis.z*motion.y,axis.z*motion.x-axis.x*motion.z,axis.x*motion.y-axis.y*motion.x);double len=normal.length();if(len>.00001){normal=normal.scale(.012/len);out.add(new Face(List.of(tipA.sub(axis.scale(.22)).add(normal),tipA.add(normal),tipB.add(normal),tipB.sub(tipB.sub(baseB).scale(.22)).add(normal)),alpha*.8f,true));}}
  }
  cachedRevision=revision;cachedQuality=q;cachedAge=quantum;return cached=List.copyOf(out);
 }
}

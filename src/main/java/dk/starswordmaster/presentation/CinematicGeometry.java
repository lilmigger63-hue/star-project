package dk.starswordmaster.presentation;
import java.util.*;
/** Original, reusable local-space meshes. Generated once per ability, never per rendered frame. */
public final class CinematicGeometry {
 public enum Form { CRESCENT,LANCE,BOLT,HELIX,WAVE,CAGE,RING,SHIELD,BLOOM,HALO,DASH }
 public enum Anchor { CASTER,IMPACT,RAY,ORIGIN }
 public record Point(float x,float y,float z){}
 public record Face(List<Point> vertices,int tone,float alpha){}
 public record Mesh(String id,Form form,Anchor anchor,List<Face> faces,float radius,int ticks,float spin){}
 public static final int MAX_FACES=96,FRAME_FACES=2048;
 public static final Map<String,Mesh> ALL;
 static{
  var map=new LinkedHashMap<String,Mesh>();int variant=0;
  for(var p:AbilityPresentation.ALL.values()){
   Form f=switch(p.pattern()){
    case SLASH,CHOP->p.element()==AbilityPresentation.Element.WATER?Form.WAVE:Form.CRESCENT;
    case THRUST->Form.LANCE;case CAGE->Form.CAGE;case DASH->Form.DASH;
    case RAY->p.element()==AbilityPresentation.Element.LIGHTNING?Form.BOLT:p.element()==AbilityPresentation.Element.WATER||p.element()==AbilityPresentation.Element.DARK?Form.HELIX:Form.LANCE;
    case WARD->Form.SHIELD;case BURST->Form.BLOOM;case RING->Form.RING;case AURA,HEAL->Form.HALO;
   };
   if(p.element()==AbilityPresentation.Element.EARTH&&p.pattern()==AbilityPresentation.Pattern.BURST)f=Form.RING;
   if(p.abilityId().equals("heavens_wrath"))f=Form.BOLT;
   Anchor a=switch(p.pattern()){case RAY->Anchor.RAY;case CAGE,BURST->Anchor.IMPACT;case WARD->p.abilityId().equals("curse_mark")?Anchor.IMPACT:Anchor.CASTER;case DASH->Anchor.ORIGIN;default->Anchor.CASTER;};
   if(p.abilityId().equals("worldbreaker"))a=Anchor.CASTER;
   var faces=build(f,variant++);map.put(p.abilityId(),new Mesh(p.abilityId(),f,a,List.copyOf(faces),(float)p.radius(),p.ticks(),.012f+(variant%7)*.003f));
  }
  ALL=Collections.unmodifiableMap(map);
 }
 private CinematicGeometry(){}
 public static float envelope(float age,int duration){
  if(!Float.isFinite(age)||age<0||age>=duration+8)return 0;
  return Math.min(1,age/Math.max(2,duration*.16f))*Math.min(1,(duration+8-age)/8);
 }
 private static Point p(double x,double y,double z){return new Point((float)x,(float)y,(float)z);}
 private static void quad(List<Face> out,Point a,Point b,Point c,Point d,int tone,float alpha){out.add(new Face(List.of(a,b,c,d),tone,alpha));}
 private static void strip(List<Face> out,Point a,Point b,float width,int tone,float alpha){
  // Crossed ribbons retain a recognizable core from different view directions.
  quad(out,p(a.x-width,a.y,a.z),p(b.x-width,b.y,b.z),p(b.x+width,b.y,b.z),p(a.x+width,a.y,a.z),tone,alpha);
  quad(out,p(a.x,a.y-width,a.z),p(b.x,b.y-width,b.z),p(b.x,b.y+width,b.z),p(a.x,a.y+width,a.z),tone,alpha*.7f);
 }
 private static List<Face> build(Form form,int variant){
  var out=new ArrayList<Face>();double phase=variant*.137,twist=1+(variant%5)*.11;float w=.025f+(variant%4)*.006f;
  switch(form){
   case CRESCENT->{for(int i=0;i<22;i++){
    double a=-1.25+i*2.5/22,b=-1.25+(i+1)*2.5/22,tilt=.15*Math.sin(phase);
    float width=(float)(.05+.14*Math.sin((i+.5)*Math.PI/22));
    quad(out,p(Math.sin(a),Math.cos(a)*tilt,Math.cos(a)),p(Math.sin(b),Math.cos(b)*tilt,Math.cos(b)),p(Math.sin(b)*(1-width),Math.cos(b)*tilt,Math.cos(b)*(1-width)),p(Math.sin(a)*(1-width),Math.cos(a)*tilt,Math.cos(a)*(1-width)),i%3==0?1:0,.64f);
   }}
   case LANCE->{strip(out,p(0,0,0),p(0,0,1),w,1,.8f);for(int i=0;i<6;i++){double a=phase+i*Math.PI/3;strip(out,p(Math.cos(a)*.13,Math.sin(a)*.13,.16),p(0,0,.9),w*.55f,0,.42f);}}
   case BOLT->{for(int i=0;i<12;i++){double a=i/12.,b=(i+1)/12.;strip(out,p(i==0?0:Math.sin(i*2.6+phase)*.08,Math.cos(i*3.1)*.035,a),p(i==11?0:Math.sin((i+1)*2.6+phase)*.08,Math.cos((i+1)*3.1)*.035,b),w, i%3==0?1:0,.88f);if(i%4==2)strip(out,p(0,0,a),p(Math.sin(i+phase)*.24,.13,b),w*.6f,1,.5f);}}
   case HELIX->{for(int i=0;i<24;i++){double a=i/24.,b=(i+1)/24.,ta=a*Math.PI*4*twist+phase,tb=b*Math.PI*4*twist+phase;strip(out,p(Math.cos(ta)*.13,Math.sin(ta)*.13,a),p(Math.cos(tb)*.13,Math.sin(tb)*.13,b),w,i%4==0?1:0,.6f);}}
   case WAVE->{for(int i=0;i<24;i++){double span=.95+(variant%9)*.045,a=(-1.2+i*.1)*span,b=a+.1*span,ha=.2+.6*Math.cos(a*twist+phase*.25),hb=.2+.6*Math.cos(b*twist+phase*.25);quad(out,p(a,-.15,.15*Math.cos(a)),p(b,-.15,.15*Math.cos(b)),p(b,hb,.15*Math.cos(b)),p(a,ha,.15*Math.cos(a)),i%4==0?1:0,.4f);}}
   case CAGE->{for(int i=0;i<8;i++){double a=i*Math.PI/4+phase;strip(out,p(Math.cos(a)*.68,-.75,Math.sin(a)*.68),p(Math.cos(a)*.5,.9,Math.sin(a)*.5),w, i%2,.55f);}ring(out,phase,.7,-.65,w,0);ring(out,-phase,.6,.65,w,1);}
   case RING->{ring(out,phase,1,0,w*2,0);ring(out,-phase,.72,.05,w,1);for(int i=0;i<8;i++){double a=phase+i*Math.PI/4;strip(out,p(Math.cos(a)*.75,0,Math.sin(a)*.75),p(Math.cos(a)*1.15,.09,Math.sin(a)*1.15),w,1,.5f);}}
   case SHIELD->{for(int i=0;i<24;i++){double a=phase+i*Math.PI/12,b=a+Math.PI/12;quad(out,p(Math.cos(a),Math.sin(a),0),p(Math.cos(b),Math.sin(b),0),p(Math.cos(b)*.93,Math.sin(b)*.93,0),p(Math.cos(a)*.93,Math.sin(a)*.93,0),i%4==0?1:0,.65f);}for(int i=0;i<4;i++){double a=phase+i*Math.PI/2;strip(out,p(0,0,.02),p(Math.cos(a)*.7,Math.sin(a)*.7,.02),w,1,.35f);}}
   case BLOOM->{for(int i=0;i<12;i++){double a=phase+i*Math.PI/6,h=.4+(i%3)*.2;Point tip=p(Math.cos(a)*.8,h,Math.sin(a)*.8);quad(out,p(0,0,0),p(Math.cos(a-.12)*.45,0,Math.sin(a-.12)*.45),tip,tip,i%3==0?1:0,.65f);quad(out,p(0,0,0),tip,p(Math.cos(a+.12)*.45,0,Math.sin(a+.12)*.45),p(Math.cos(a+.12)*.45,0,Math.sin(a+.12)*.45),i%3==0?1:0,.4f);}}
   case HALO->{ring(out,phase,.8,-.5,w,0);ring(out,-phase,.55,.7,w,1);for(int i=0;i<8;i++){double a=phase+i*Math.PI/4;strip(out,p(Math.cos(a)*.6,-.6,Math.sin(a)*.6),p(Math.cos(a+.5)*.7,.6,Math.sin(a+.5)*.7),w, i%3==0?1:0,.33f);}}
   case DASH->{for(int i=0;i<8;i++){double a=phase+i*Math.PI/4;strip(out,p(Math.cos(a)*.35,Math.sin(a)*.5,-1.2),p(Math.cos(a)*.45,Math.sin(a)*.6,.2),w, i%3==0?1:0,.45f);}}
  }
  if(out.isEmpty()||out.size()>MAX_FACES)throw new IllegalStateException("Invalid cinematic mesh");return out;
 }
 private static void ring(List<Face> out,double phase,double r,double y,float width,int tone){
  for(int i=0;i<24;i++){double a=phase+i*Math.PI/12,b=a+Math.PI/12;quad(out,p(Math.cos(a)*r,y,Math.sin(a)*r),p(Math.cos(b)*r,y,Math.sin(b)*r),p(Math.cos(b)*(r-width),y,Math.sin(b)*(r-width)),p(Math.cos(a)*(r-width),y,Math.sin(a)*(r-width)),tone,.55f);}
 }
}

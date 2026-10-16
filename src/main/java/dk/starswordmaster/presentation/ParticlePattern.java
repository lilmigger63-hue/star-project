package dk.starswordmaster.presentation;
import java.util.*;
/** Deterministic cosmetic choreography. One point is one particle, never a gameplay projectile. */
public final class ParticlePattern {
 public enum Ink { STREAK,ARC,SIGIL,SHARD,FLASH,EMBER }
 public enum Space { BODY,HAND,RAY,IMPACT,ORIGIN }
 public record Point(double x,double y,double z,Space space,Ink ink,float size,int life,float angle,int tone){
  public Point(double x,double y,double z){this(x,y,z,Space.BODY,Ink.STREAK,0.2f,6,0,0);}
 }
 private ParticlePattern(){}
 private static Point mark(double x,double y,double z,Space space,Ink ink,double size,int life,double angle,int tone){return new Point(x,y,z,space,ink,(float)size,life,(float)angle,tone);}
 public static List<Point> points(AbilityPresentation p,int tick){
  if(tick<0||tick>=p.ticks())return List.of();
  var out=new ArrayList<Point>();double t=(double)tick/p.ticks(),r=p.radius();int n=p.samples();
  for(int i=0;i<n;i++){
   double u=(double)i/n,a=u*Math.PI*2,spin=a+t*4,wave=Math.sin(a+t*6),sweep=-1.15+2.3*t+(u-0.5)*0.45;
   Point v=switch(p.abilityId()){
    case "spark"->mark(Math.sin(a+t*8)*0.08,Math.cos(a+t*8)*0.08,Math.min(1,t*1.7+u*0.22),Space.RAY,i%3==0?Ink.FLASH:Ink.EMBER,0.16+u*0.07,5,a, i%2);
    case "frost_touch"->t<0.4?mark((i%2==0?-1:1)*0.12*(1-u),0.15*Math.sin(a),Math.min(1,t*2+u*0.35),Space.RAY,Ink.SHARD,0.22,6,a,1):mark(Math.cos(a)*0.55,0.4+u*1.1,Math.sin(a)*0.55,Space.IMPACT,Ink.SHARD,0.28,6,a+Math.PI/4,0);
    case "light"->{double petal=0.55+0.15*Math.cos(5*a);yield mark(Math.cos(spin)*petal,0.65+0.8*t,Math.sin(spin)*petal,Space.BODY,i==0?Ink.SIGIL:Ink.FLASH,i==0?0.65:0.18,8,-spin, i%2);}
    case "mend"->mark(Math.cos(a+t*5)*r,0.35+1.3*t,Math.sin(a+t*5)*r,Space.BODY,i%2==0?Ink.SIGIL:Ink.SHARD,i%2==0?0.28:0.18,7,-a,1);
    case "renew"->{double turn=a+(i%2==0?1:-1)*t*6;yield mark(Math.cos(turn)*r*(0.6+0.3*Math.sin(t*Math.PI)),0.3+1.6*t,Math.sin(turn)*r,Space.BODY,i%3==0?Ink.FLASH:Ink.ARC,0.27,7,turn,i%2);}
    case "quick_cut"->i<5?mark(Math.sin(sweep)*r,1.05+0.25*Math.sin(sweep),Math.cos(sweep)*r,Space.BODY,Ink.ARC,0.5,4,-sweep-0.5,1):mark((u-0.5)*0.5,1.0+u*0.25,-0.25-t,Space.BODY,Ink.STREAK,0.32,4,-0.35,0);
    case "quick_dash"->mark((i%2==0?-1:1)*(0.2+u*0.2),0.45+u*0.9,-0.3-t*1.5-u*0.3,Space.ORIGIN,Ink.STREAK,0.45,5,-0.4,i%2);
    case "heavy_cut"->{double arc=0.15+t*2.6+u*0.25;yield mark((u-0.5)*0.12,1.1+Math.cos(arc)*r*0.5,Math.sin(arc)*r,Space.BODY,i%3==0?Ink.ARC:Ink.STREAK,i%3==0?0.9:0.38,5,Math.PI/2-arc,i%2);}
    case "heavy_sweep"->{double turn=-1.45+t*2.9+u*0.4;yield mark(Math.sin(turn)*r,0.85+0.22*Math.cos(a),Math.cos(turn)*r,Space.BODY,i%3==0?Ink.STREAK:Ink.ARC,0.65,5,turn+Math.PI/2,i%2);}
    case "guard"->{double diamond=(i%4)*Math.PI/2+Math.PI/4;yield mark(Math.cos(diamond)*r*(0.65+0.15*t),1.1+Math.sin(diamond)*r*0.7,0.7,Space.BODY,i==0?Ink.SIGIL:Ink.STREAK,i==0?0.7:0.25,6,diamond,1);}
    case "riposte"->mark((u-0.5)*0.12,1.15+(i%2==0?0.08:-0.08),r*(0.25+u*0.75),Space.BODY,Ink.STREAK,0.4,4,0,i%2);
    case "aura_wave"->mark(Math.sin(a)*0.4*(1-t),Math.cos(a)*0.35,Math.min(1,t*1.4+u*0.15),Space.RAY,i%2==0?Ink.ARC:Ink.FLASH,0.42,5,a+t, i%2);
    case "blade_focus"->{double star=(i%2==0?1:0.65)*r;yield mark(Math.cos(spin)*star,1.6+0.35*Math.sin(a+t*2),Math.sin(spin)*star,Space.BODY,i%3==0?Ink.SIGIL:Ink.STREAK,0.35,6,spin+Math.PI/2,1);}
    case "fire_burst"->{double bloom=r*(0.2+0.65*t)*(0.75+0.2*Math.sin(3*a));yield mark(Math.cos(a)*bloom,0.35+u*1.4*(1-t*0.5),Math.sin(a)*bloom,Space.IMPACT,i%3==0?Ink.ARC:Ink.EMBER,0.4+0.2*u,6,a-t, i%2);}
    case "ice_lance"->mark((i%2==0?-1:1)*0.055*(1-u),0.06*Math.cos(a),Math.min(1,t*1.7+u*0.35),Space.RAY,i%3==0?Ink.STREAK:Ink.SHARD,0.34,5,Math.PI/2,1);
    case "sanctuary"->{double halo=r*(i%2==0?1:0.55);yield mark(Math.cos(a)*halo,0.15+0.2*Math.sin(t*Math.PI),Math.sin(a)*halo,Space.BODY,i==0?Ink.SIGIL:Ink.FLASH,i==0?1.1:0.25,8,a+t,1);}
    case "frost_nova"->{double ring=r*(0.15+0.85*t)*(i%2==0?1:0.82);yield mark(Math.cos(a)*ring,0.12+0.25*Math.sin(3*a+t),Math.sin(a)*ring,Space.BODY,Ink.SHARD,0.35,7,a, i%2);}
    case "flame_blade"->{double flame=sweep+0.12*Math.sin(a+t*4);yield mark(Math.sin(flame)*r,1.0+0.35*Math.sin(a+t*5),Math.cos(flame)*r,Space.BODY,i%3==0?Ink.ARC:Ink.EMBER,0.42,5,-flame,i%2);}
    case "frost_blade"->{double tooth=-1.05+2.1*t+(i%2==0?0.1:-0.1);yield mark(Math.sin(tooth)*r,0.85+u*0.5,Math.cos(tooth)*r*(0.85+0.1*Math.cos(a)),Space.BODY,i%2==0?Ink.ARC:Ink.SHARD,0.44,5,Math.PI/3+tooth,1);}
    case "rapid_slash"->{int cut=(int)(t*3);double phase=(t*3)%1,turn=(cut%2==0?-1:1)*(-1.1+2.2*phase)+u*0.18;yield mark(Math.sin(turn)*r,0.8+u*0.55+(cut%2==0?1:-1)*Math.sin(turn)*0.15,Math.cos(turn)*r,Space.BODY,i%3==0?Ink.STREAK:Ink.ARC,0.45,3,turn+(cut-1)*0.55, i%2);}
    case "crushing_edge"->{double arc=0.1+t*2.8;yield mark((i%2==0?-1:1)*u*0.4,1.2+Math.cos(arc)*r*0.45,Math.sin(arc)*r*(0.8+u*0.15),Space.BODY,i%4==0?Ink.FLASH:Ink.ARC,0.8,5,Math.PI/2+(i%2==0?0.2:-0.2),i%2);}
    case "mirror_guard"->{double turn=a+t*1.5;yield mark(Math.cos(turn)*r*0.75,1.1+Math.sin(turn)*r*0.7,0.75+0.1*Math.cos(a*2),Space.BODY,i%3==0?Ink.ARC:Ink.SHARD,0.4,7,-turn,1);}
    case "flame_lance"->mark(Math.cos(a+t*9)*0.14,Math.sin(a+t*9)*0.14,Math.min(1,t*1.6+u*0.25),Space.RAY,i%4==0?Ink.STREAK:Ink.EMBER,0.28,6,a+t*4,i%2);
    case "frost_ward"->mark(Math.cos(a)*r,0.95+Math.abs(Math.sin(a))*r,0.45+0.3*Math.sin(a),Space.BODY,i==0?Ink.SIGIL:Ink.SHARD,0.44,7,a+Math.PI/4,1);
    case "arcane_step"->mark(Math.cos(a+t*5)*r,0.7+0.5*Math.sin(a),-0.25-t*1.25, i%2==0?Space.ORIGIN:Space.BODY,i==0?Ink.SIGIL:Ink.STREAK,i==0?0.8:0.4,5,spin,i%2);
    case "glacier_sweep"->{double fan=-1.4+u*2.8+t*0.45,reach=r*(0.2+0.75*t);yield mark(Math.sin(fan)*reach,0.15+(i%2)*0.2,Math.cos(fan)*reach,Space.BODY,i%3==0?Ink.ARC:Ink.SHARD,0.45,6,fan,1);}
    case "piercing_thrust"->mark(Math.cos(a)*0.26*(1-u),1.2+Math.sin(a)*0.26*(1-u),r*(0.2+0.8*u),Space.BODY,i%2==0?Ink.FLASH:Ink.STREAK,0.3+0.2*u,4,a,1);
    case "seismic_cut"->{double spoke=(i%3)*Math.PI*2/3+0.1*Math.sin(t*8),reach=r*(0.15+0.8*t)*(0.5+u*0.5);yield mark(Math.cos(spoke)*reach,0.08+(i%2)*0.14,Math.sin(spoke)*reach,Space.BODY,i%4==0?Ink.FLASH:Ink.SHARD,0.5,6,spoke, i%2);}
    case "guardian_stance"->{double sector=(i%3)*Math.PI*2/3+t*0.4;yield mark(Math.cos(sector)*r*0.75,0.3+u*1.6,0.35+Math.sin(sector)*r*0.7,Space.BODY,i%3==0?Ink.SIGIL:Ink.STREAK,0.42,7,Math.PI/2,i%2);}
    case "ember_ring"->{double flame=r*(0.2+0.8*t),petal=1-0.12*Math.cos(4*a+t*5);yield mark(Math.cos(a+t*0.8)*flame*petal,0.18+0.45*Math.abs(wave),Math.sin(a+t*0.8)*flame*petal,Space.BODY,i%4==0?Ink.SIGIL:Ink.EMBER,0.42,7,-a,i%2);}
    case "ice_prison"->{double corner=(i%4)*Math.PI/2+Math.PI/4;yield mark(Math.cos(corner)*r,0.15+u*2.1,Math.sin(corner)*r,Space.IMPACT,i%4==0?Ink.SIGIL:Ink.SHARD,0.4,7,Math.PI/2+corner,i%2);}
    case "restoration"->{double flower=r*(0.25+0.75*Math.sin(t*Math.PI))* (0.8+0.15*Math.cos(5*a));yield mark(Math.cos(a-t*4)*flower,0.2+1.8*t,Math.sin(a-t*4)*flower,Space.BODY,i%3==0?Ink.SIGIL:Ink.FLASH,0.35,8,a-t*3,1);}
    default->mark(Math.cos(spin)*r,0.2+1.8*t,Math.sin(spin)*r,Space.BODY,Ink.STREAK,0.2,5,a,0);
   };
   // Only cosmetic anticipation, after the server-confirmed instantaneous gameplay effect.
   if(t<0.2&&p.element()!=AbilityPresentation.Element.STEEL&&p.clip()!=AbilityPresentation.Clip.SLASH&&p.clip()!=AbilityPresentation.Clip.DASH&&i<2)
    v=mark((i==0?-1:1)*0.3,0.08*Math.sin(t*9),0.5,Space.HAND,i==0?Ink.SIGIL:Ink.FLASH,0.24+t,5,t*3,1);
   out.add(v);
  }
  return List.copyOf(out);
 }
 /** Immediate impact accents remain separate from subsequent traces; at most the previous eight points. */
 public static List<Point> impact(AbilityPresentation p){
  var out=new ArrayList<Point>();boolean heavy=p.clip()==AbilityPresentation.Clip.HEAVY||p.clip()==AbilityPresentation.Clip.CHANNEL;int n=Math.min(8,p.samples());
  for(int i=0;i<n;i++){double a=i*Math.PI*2/n,r=heavy?0.42:0.18;Ink ink=switch(p.element()){case FIRE->Ink.EMBER;case FROST->Ink.SHARD;case HEALING->Ink.SIGIL;default->Ink.FLASH;};out.add(mark(Math.cos(a)*r,Math.sin(a)*r,0,Space.IMPACT,ink,heavy?0.55:0.25,4,a,1));}
  return List.copyOf(out);
 }
 public static int color(AbilityPresentation.Element element,int tone){return switch(element){case STEEL->tone==0?0xD0E7FF:0xFFF0B0;case FIRE->tone==0?0xFF5926:0xFFD46A;case FROST->tone==0?0x44CFFF:0xD9FBFF;case STAR->tone==0?0xA97CFF:0xF4E2A3;case HEALING->tone==0?0x44E79A:0xD8FFC4;};}
}

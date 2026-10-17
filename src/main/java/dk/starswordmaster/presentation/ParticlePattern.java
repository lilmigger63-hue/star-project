package dk.starswordmaster.presentation;
import java.util.*;
/** Deterministic cosmetic choreography. One point is one particle, never a gameplay projectile. */
public final class ParticlePattern {
 public enum Ink { STREAK,ARC,SIGIL,SHARD,FLASH,EMBER,BOLT,DROPLET,ROCK,SHADOW,RUNE,BLADE }
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
    case "static_spark"->mark((i%2==0?-1:1)*(.08+.08*Math.sin(a+t*19)),.06*Math.cos(a),Math.min(1,t*1.9+u*.2),Space.RAY,Ink.BOLT,.25,3,a,1);
    case "chain_lightning"->mark((i%3-1)*.16*Math.sin(t*31+a),Math.cos(a+t*17)*.12,u,Space.RAY,i%4==0?Ink.FLASH:Ink.BOLT,.3,3,a+t*8,i%2);
    case "thunder_step"->mark((i%2==0?-1:1)*(.25+u*.3),.4+u,-.3-t*2-u*.4,Space.ORIGIN,Ink.BOLT,.45,4,Math.PI/3,i%2);
    case "heavens_wrath"->{double turn=a+t*.8,reach=r*(.3+.6*u);yield mark(Math.cos(turn)*reach,.2+(1-t)*4+Math.sin(a*3+t*12)*.3,Math.sin(turn)*reach,Space.IMPACT,i%4==0?Ink.RUNE:Ink.BOLT,.6,4,Math.PI/2,i%2);}
    case "water_bolt"->mark(Math.cos(a+t*8)*.11,Math.sin(a+t*8)*.11,Math.min(1,t*1.7+u*.26),Space.RAY,Ink.DROPLET,.24,6,a+t*3,1);
    case "tidal_wave"->{double fan=-1+u*2;yield mark(Math.sin(fan)*r*(.4+t*.5),.2+1.5*Math.sin(u*Math.PI)*(1-t*.5),t*r+.35*Math.cos(fan),Space.BODY,i%3==0?Ink.ARC:Ink.DROPLET,.55,7,fan+t,i%2);}
    case "aqua_blade"->{double turn=sweep+.2*Math.sin(a+t*5);yield mark(Math.sin(turn)*r,.9+.25*Math.cos(a+t*4),Math.cos(turn)*r,Space.BODY,i%3==0?Ink.BLADE:Ink.DROPLET,.5,5,-turn,i%2);}
    case "ocean_prison"->{double turn=a+t*7;yield mark(Math.cos(turn)*r*(.55+.2*Math.sin(t*Math.PI)),.15+u*2.4,Math.sin(turn)*r*(.55+.2*Math.sin(t*Math.PI)),Space.IMPACT,i%3==0?Ink.RUNE:Ink.DROPLET,.4,7,-turn,1);}
    case "stone_spike"->mark(Math.cos(a)*.4,.1+u*1.9*Math.sin(t*Math.PI),Math.sin(a)*.4,Space.IMPACT,i==0?Ink.RUNE:Ink.ROCK,.38,6,a,i%2);
    case "earthen_wall"->mark((u-.5)*2.5,.3+(i%3)*.55,1.0+.1*Math.sin(t*4+a),Space.BODY,i%4==0?Ink.RUNE:Ink.ROCK,.62,7,a,1);
    case "quake_blade"->{double fan=(i%3)*Math.PI*2/3,reach=r*(.15+.8*t)*(.5+u*.5);yield mark(Math.cos(fan)*reach,.1+Math.sin(t*Math.PI)*.4,Math.sin(fan)*reach,Space.BODY,i%3==0?Ink.RUNE:Ink.ROCK,.6,7,fan+t,i%2);}
    case "worldbreaker"->{double reach=r*(.15+.8*t)*(i%2==0?1:.6);yield mark(Math.cos(a)*reach,.15+Math.sin(t*Math.PI)*(1+u*1.8),Math.sin(a)*reach,Space.BODY,i%3==0?Ink.RUNE:Ink.ROCK,.85,8,a+t*3,i%2);}
    case "shadow_bolt"->mark(Math.sin(a+t*5)*(.08+.12*u),Math.cos(a+t*5)*.12,Math.min(1,t*1.5+u*.35),Space.RAY,i%3==0?Ink.SHADOW:Ink.FLASH,.3,7,-a,0);
    case "curse_mark"->{double turn=a-t*3;yield mark(Math.cos(turn)*.65,1+Math.sin(turn)*.65,.2*Math.sin(a*3),Space.IMPACT,i%2==0?Ink.RUNE:Ink.SHADOW,.35,8,-turn,i%2);}
    case "shadow_edge"->mark(Math.sin(sweep)*r,1.05+.4*Math.sin(a),Math.cos(sweep)*r*(.7+.25*u),Space.BODY,i%3==0?Ink.BLADE:Ink.SHADOW,.55,6,sweep-Math.PI/2,i%2);
    case "eclipse_nova"->{double reach=r*(.2+.75*t),petal=.8+.15*Math.cos(6*a);yield mark(Math.cos(a-t)*reach*petal,.3+.9*Math.sin(a+t*3)*(1-t),Math.sin(a-t)*reach*petal,Space.BODY,i%3==0?Ink.RUNE:Ink.SHADOW,.7,8,a-t, i%2);}
    case "astral_crescent"->{double turn=-1.4+t*2.8+u*.25;yield mark(Math.sin(turn)*r,.8+.3*Math.sin(turn),Math.cos(turn)*r,Space.BODY,i%3==0?Ink.BLADE:Ink.ARC,.65,5,-turn,1);}
    case "falling_star"->{double reach=r*(.2+.7*t),star=i%2==0?1:.45;yield mark(Math.cos(a)*reach*star,.12+(1-t)*.6,Math.sin(a)*reach*star,Space.BODY,i%3==0?Ink.RUNE:Ink.FLASH,.55,6,a+t,i%2);}
    case "phantom_blades"->{int phase=(int)(t*4);double turn=(phase%2==0?1:-1)*(-1+2*(t*4%1));yield mark(Math.sin(turn)*r*(.65+u*.3),.75+u*.65,Math.cos(turn)*r,Space.BODY,Ink.BLADE,.5,3,turn+(phase-1)*.6,i%2);}
    case "celestial_rush"->mark((i%2==0?-1:1)*(.15+u*.4),.35+u*1.2,-.2-t*2.4-u*.6,Space.ORIGIN,i%3==0?Ink.BLADE:Ink.STREAK,.55,5,.2+t,i%2);
    case "sovereign_judgment"->{double drop=.2+t*2.8;yield mark((u-.5)*.5,1.1+Math.cos(drop)*r*.5,Math.sin(drop)*r*(.6+.35*u),Space.BODY,i%3==0?Ink.RUNE:Ink.BLADE,.95,6,Math.PI/2-drop,i%2);}
    case "heavenly_sword_domain"->{double orbit=a+t*2,reach=r*(.25+.65*Math.sin(t*Math.PI));yield mark(Math.cos(orbit)*reach,.2+(i%3==0?1.6:0),Math.sin(orbit)*reach,Space.BODY,i%3==0?Ink.BLADE:Ink.RUNE,.85,8,-orbit+Math.PI/2,i%2);}
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
  var out=new ArrayList<Point>();boolean heavy=p.clip()==AbilityPresentation.Clip.HEAVY||p.clip()==AbilityPresentation.Clip.CHANNEL||p.ticks()>=22;int n=Math.min(8,p.samples());
  for(int i=0;i<n;i++){double a=i*Math.PI*2/n,r=heavy?0.42:0.18;Ink ink=switch(p.element()){case FIRE->Ink.EMBER;case FROST->Ink.SHARD;case HEALING->Ink.SIGIL;case LIGHTNING->Ink.BOLT;case WATER->Ink.DROPLET;case EARTH->Ink.ROCK;case DARK->Ink.SHADOW;default->Ink.FLASH;};out.add(mark(Math.cos(a)*r,Math.sin(a)*r,0,Space.IMPACT,ink,heavy?0.55:0.25,4,a,1));}
  return List.copyOf(out);
 }
 public static int color(AbilityPresentation.Element element,int tone){return FantasyPalette.color(element,tone);}
}

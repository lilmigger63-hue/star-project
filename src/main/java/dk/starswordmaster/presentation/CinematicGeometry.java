package dk.starswordmaster.presentation;

import java.util.*;
import static dk.starswordmaster.presentation.CinematicMeshBuilder.*;

/** Original cached solids, assembled deliberately per ability. No per-frame mesh generation. */
public final class CinematicGeometry {
 public enum Form { CRESCENT,LANCE,BOLT,HELIX,WAVE,CAGE,RING,SHIELD,BLOOM,HALO,DASH }
 public enum Anchor { CASTER,IMPACT,RAY,ORIGIN }
 public enum Motion { STILL,ORBIT,RISE,TRAVEL,OPEN,STAGGER,PULSE,FALL }
 public record Point(float x,float y,float z){}
 public record Face(List<Point> vertices,int tone,float alpha,Point normal){
  public Face(List<Point> vertices,int tone,float alpha){this(List.copyOf(vertices),tone,alpha,faceNormal(vertices));}
  public Face {vertices=List.copyOf(vertices);}
 }
 private static Point faceNormal(List<Point> v){
  var a=v.get(0);var b=v.get(1);var c=v.get(2);
  float ux=b.x()-a.x(),uy=b.y()-a.y(),uz=b.z()-a.z(),vx=c.x()-a.x(),vy=c.y()-a.y(),vz=c.z()-a.z();
  float x=uy*vz-uz*vy,y=uz*vx-ux*vz,z=ux*vy-uy*vx,length=(float)Math.sqrt(x*x+y*y+z*z);
  return length>1e-8f?new Point(x/length,y/length,z/length):new Point(0,0,1);
 }
 /** Minimum quality is 0=Low, 1=Normal, 2=Ultra. Delays are fractions of the primary-effect phase. */
 public record Part(List<Face> faces,int minQuality,Motion motion,float delay){
  public Part { faces=List.copyOf(faces);if(faces.isEmpty()||minQuality<0||minQuality>2||!Float.isFinite(delay)||delay<0||delay>1)throw new IllegalArgumentException("Invalid cinematic part"); }
 }
 public record Mesh(String id,Form form,Anchor anchor,List<Face> faces,float radius,int ticks,float spin,List<Part> parts){
  public Mesh {faces=List.copyOf(faces);parts=List.copyOf(parts);}
 }
 public static final int MAX_FACES=96,FRAME_FACES=2048;
 public static final Map<String,Mesh> ALL;
 static {
  var map=new LinkedHashMap<String,Mesh>();
  for(var style:AbilityPresentation.ALL.values()){
   String id=style.abilityId();var parts=build(id);var faces=new ArrayList<Face>();for(var part:parts)faces.addAll(part.faces());
   if(faces.isEmpty()||faces.size()>MAX_FACES)throw new IllegalStateException(id+" exceeds mesh face budget: "+faces.size());
   map.put(id,new Mesh(id,form(style),anchor(style),faces,(float)style.radius(),style.ticks(),spin(style.element()),parts));
  }
  ALL=Collections.unmodifiableMap(map);
 }
 private CinematicGeometry(){}
 private static float spin(AbilityPresentation.Element element){return switch(element){case STEEL,FROST,EARTH->0;case LIGHTNING->.009f;case WATER->.018f;case DARK->-.02f;default->.012f;};}
 private static Form form(AbilityPresentation s){
  if(s.abilityId().equals("heavens_wrath"))return Form.BOLT;
  if(s.element()==AbilityPresentation.Element.EARTH&&s.pattern()==AbilityPresentation.Pattern.BURST)return Form.RING;
  return switch(s.pattern()){
   case SLASH,CHOP->s.element()==AbilityPresentation.Element.WATER?Form.WAVE:Form.CRESCENT;
   case THRUST->Form.LANCE;case CAGE->Form.CAGE;case DASH->Form.DASH;
   case RAY->s.element()==AbilityPresentation.Element.LIGHTNING?Form.BOLT:s.element()==AbilityPresentation.Element.WATER||s.element()==AbilityPresentation.Element.DARK?Form.HELIX:Form.LANCE;
   case WARD->Form.SHIELD;case BURST->Form.BLOOM;case RING->Form.RING;case AURA,HEAL->Form.HALO;
  };
 }
 private static Anchor anchor(AbilityPresentation s){
  if(s.abilityId().equals("worldbreaker"))return Anchor.CASTER;
  if(s.abilityId().equals("sovereign_judgment")||s.abilityId().equals("phantom_blades"))return Anchor.IMPACT;
  return switch(s.pattern()){case RAY->Anchor.RAY;case CAGE,BURST->Anchor.IMPACT;case WARD->s.abilityId().equals("curse_mark")?Anchor.IMPACT:Anchor.CASTER;case DASH->Anchor.ORIGIN;default->Anchor.CASTER;};
 }
 public static float envelope(float age,int duration){
  if(!Float.isFinite(age)||age<0||age>=duration+8)return 0;
  return Math.min(1,age/Math.max(2,duration*.16f))*Math.min(1,(duration+8-age)/8);
 }

 private static Part part(List<Face> f,int quality,Motion motion,double delay){return new Part(f,quality,motion,(float)delay);}
 private static Part core(List<Face> f,Motion motion){return part(f,0,motion,0);}
 private static List<Face> at(List<Face> f,double x,double y,double z){return move(f,x,y,z,0,0,1);}
 private static List<Face> swordAt(double x,double y,double z,double h,double w,double turn,double tilt){return move(sword(h,w,.91f),x,y,z,turn,tilt,1);}
 private static List<Face> shard(double x,double y,double z,double h,double w,double turn){return move(crystal(h,w,.10,.86f),x,y,z,turn,0,1);}
 private static List<Face> forwardFlame(double height,double width,double lean){return move(flame(height,width,lean,.91f),0,0,0,0,Math.PI/2,1);}
 private static List<Face> forwardCrystal(double h,double w){return move(crystal(h,w,0,.9f),0,0,0,0,Math.PI/2,1);}
 private static List<Part> build(String id){
  var out=new ArrayList<Part>();
  switch(id){
   // Fire: closed turbulent masses, long condensed spear, petal eruption and circular flame wall.
   case "spark"->{out.add(core(forwardFlame(.78,.19,.10),Motion.TRAVEL));out.add(part(at(flame(.32,.09,-.09,.74f),.16,-.08,.15),1,Motion.PULSE,.05));out.add(part(at(orb(.07,.14,.94f),-.21,.10,.29),2,Motion.FALL,.24));}
   case "fire_burst"->{out.add(core(flame(1.14,.37,.17,.94f),Motion.RISE));for(int i=0;i<3;i++){double a=i*Math.PI*2/3;out.add(part(move(flame(.68,.24,.32,.84f),Math.cos(a)*.42,0,Math.sin(a)*.42,0,-.4,1),0,Motion.OPEN,.06*i));}out.add(part(ring(.91,-.14,.065,6,.78f),1,Motion.OPEN,.14));out.add(part(at(flame(.43,.13,-.18,.8f),-.6,.15,.22),2,Motion.FALL,.35));}
   case "flame_lance"->{out.add(core(forwardFlame(1.55,.16,.08),Motion.TRAVEL));out.add(part(move(tendril(.17,1.3,1.2,0,5,.77f),0,0,0,0,Math.PI/2,1),1,Motion.ORBIT,0));out.add(part(at(forwardFlame(.6,.085,-.10),.21,.08,.25),2,Motion.TRAVEL,.14));}
   case "flame_blade"->{out.add(core(arc(1,2.8,.16,.8,.05),Motion.TRAVEL));out.add(part(move(flame(.64,.16,.23,.87f),.56,0,.68,-.8,0,1),1,Motion.RISE,.12));out.add(part(move(flame(.42,.12,-.15,.8f),-.59,.03,.66,.9,0,1),2,Motion.FALL,.23));}
   case "ember_ring"->{out.add(core(ring(.76,-.12,.13,6,.86f),Motion.OPEN));for(int i=0;i<5;i++){double a=i*Math.PI*2/5;out.add(part(at(flame(.50+(i%2)*.19,.15,.16,.88f),Math.cos(a)*.76,-.12,Math.sin(a)*.76),i<2?0:i<4?1:2,Motion.RISE,i*.04));}}
   // Frost: every piece is a sealed faceted crystal or substantial defensive plate.
   case "frost_touch"->{out.add(core(forwardCrystal(.68,.22),Motion.TRAVEL));out.add(part(at(forwardCrystal(.36,.11),-.22,.04,.09),1,Motion.STAGGER,.12));out.add(part(at(forwardCrystal(.29,.09),.21,-.03,.2),2,Motion.FALL,.22));}
   case "ice_lance"->{out.add(core(forwardCrystal(1.65,.14),Motion.TRAVEL));out.add(part(at(forwardCrystal(.70,.18),0,0,.25),1,Motion.STILL,0));out.add(part(join(at(forwardCrystal(.3,.07),.21,.02,.28),at(forwardCrystal(.3,.07),-.21,-.02,.28)),2,Motion.FALL,.2));}
   case "frost_nova"->{for(int i=0;i<8;i++){double a=i*Math.PI/4;out.add(part(shard(Math.cos(a)*.64,-.2,Math.sin(a)*.64,.75+(i%3)*.21,.17,-Math.cos(a)*.26),i%2==0?0:i<5?1:2,Motion.RISE,(i%3)*.08));}out.add(part(at(orb(.12,.10,.85f),0,-.20,0),0,Motion.PULSE,0));}
   case "frost_blade"->{out.add(core(arc(.92,2.4,.12,.75,.13),Motion.TRAVEL));for(int i=0;i<5;i++){double a=-1+i*.5;out.add(part(shard(Math.sin(a)*.88,.03,Math.cos(a)*.88,.34,.12,-a*.65),i<2?0:i<4?1:2,Motion.STAGGER,i*.025));}}
   case "frost_ward"->{for(int i=0;i<5;i++)out.add(part(move(box(0,-.4,.19,.8,.12,0,.80f),(i-2)*.30,Math.abs(i-2)*-.07,.42-Math.abs(i-2)*.06,(i-2)*-.20,0,1),i==2||i==0||i==4?0:1,Motion.RISE,Math.abs(i-2)*.07));out.add(part(shard(0,.38,.42,.31,.15,0),2,Motion.PULSE,.1));}
   case "glacier_sweep"->{for(int i=0;i<7;i++)out.add(part(shard((i-3)*.26,-.27,.36-Math.abs(i-3)*.06,.62+(3-Math.abs(i-3))*.21,.17,(i-3)*-.16),i%2==1?0:i<4?1:2,Motion.STAGGER,Math.abs(i-3)*.05));}
   case "ice_prison"->{for(int i=0;i<8;i++){double a=i*Math.PI/4;out.add(part(shard(Math.cos(a)*.73,-.60,Math.sin(a)*.73,1.45,.19,Math.cos(a)*.13),i%2==0?0:i<5?1:2,Motion.RISE,(i%4)*.04));}out.add(part(ring(.70,.61,.035,6,.74f),2,Motion.PULSE,.15));}
   // Electricity consists of capped jagged prisms, never camera-facing strips.
   case "static_spark"->{out.add(core(bolt(p(0,0,0),p(0,0,.95),.055,.16),Motion.TRAVEL));out.add(part(beam(p(.12,0,.3),p(-.32,.20,.55),.04,3,1),1,Motion.PULSE,.03));out.add(part(beam(p(-.12,0,.6),p(.28,-.16,.82),.035,1,.95f),2,Motion.PULSE,.09));}
   case "chain_lightning"->{out.add(core(bolt(p(0,0,0),p(0,0,1),.048,.19),Motion.PULSE));out.add(part(bolt(p(.19,0,.25),p(.40,.23,.65),.028,.10),0,Motion.PULSE,.06));out.add(part(bolt(p(-.19,0,.75),p(-.43,-.16,.94),.026,.09),1,Motion.PULSE,.13));out.add(part(beam(p(.19,0,.25),p(.46,-.10,.31),.025,3,1),2,Motion.PULSE,.08));}
   case "thunder_step"->{out.add(core(bolt(p(-.3,-.3,-1.1),p(-.1,.26,.25),.053,.22),Motion.TRAVEL));out.add(core(bolt(p(.32,.20,-.95),p(.15,-.20,.3),.045,-.17),Motion.TRAVEL));out.add(part(join(beam(p(-.2,-.3,-.7),p(-.6,-.4,-.45),.04,1,1),beam(p(.18,.14,-.4),p(.48,.5,-.17),.034,3,1)),1,Motion.PULSE,.1));out.add(part(orb(.14,.27,1),2,Motion.PULSE,.25));}
   case "heavens_wrath"->{out.add(core(ring(.75,1.20,.07,5,.96f),Motion.ORBIT));for(int i=0;i<3;i++){double a=i*Math.PI*2/3;out.add(part(bolt(p(Math.cos(a)*.60,1.15,Math.sin(a)*.60),p(Math.cos(a)*.66,-.25,Math.sin(a)*.66),.055,.10),0,Motion.FALL,i*.08));}out.add(part(join(beam(p(-.52,1.17,-.3),p(-.82,.28,-.45),.03,3,1),beam(p(.52,1.17,.3),p(.86,.28,.45),.03,3,1)),1,Motion.PULSE,.20));out.add(part(orb(.23,.35,1),2,Motion.PULSE,.36));}
   // Water: compact flowing projectile, hooked rolling crests, and a twisting walled prison.
   case "water_bolt"->{out.add(core(move(tendril(.16,.95,1.05,0,5,.76f),0,0,0,0,Math.PI/2,1),Motion.TRAVEL));out.add(core(at(orb(.25,.44,.77f),0,-.12,.72),Motion.TRAVEL));out.add(part(at(orb(.09,.17,.83f),.31,.09,.45),1,Motion.FALL,.14));out.add(part(at(orb(.055,.12,.87f),-.24,-.14,.24),2,Motion.FALL,.27));}
   case "tidal_wave"->{out.add(core(wave(1.85,.94,.48,5),Motion.TRAVEL));out.add(part(at(wave(1.25,.42,.29,3),0,-.18,-.37),1,Motion.FALL,.14));out.add(part(join(at(orb(.09,.17,.8f),-.65,.84,.35),at(orb(.075,.15,.8f),.55,.97,.31)),2,Motion.FALL,.25));}
   case "aqua_blade"->{out.add(core(wave(1.65,.46,.34,4),Motion.TRAVEL));out.add(part(move(tendril(.17,.71,.7,0,3,.7f),-.65,.1,0,-.7,.5,1),1,Motion.ORBIT,.1));out.add(part(join(at(orb(.085,.15,.84f),.81,.39,.2),at(orb(.07,.12,.84f),-.75,.43,.3)),2,Motion.FALL,.22));}
   case "ocean_prison"->{for(int i=0;i<4;i++){double a=i*Math.PI/2;out.add(part(move(tendril(.21,1.4,.55,a,4,.73f),Math.cos(a)*.59,-.55,Math.sin(a)*.59,0,0,1),0,Motion.RISE,i*.04));}out.add(part(ring(.76,.74,.07,6,.79f),1,Motion.ORBIT,.16));}
   // These secondary chips/ripples complement the retained terrain-sampled Earth renderer.
   case "stone_spike"->{out.add(core(ring(.58,-.18,.075,6,.83f),Motion.OPEN));out.add(part(join(shard(-.36,.06,.20,.23,.14,.6),shard(.31,.02,-.2,.18,.12,-.5)),1,Motion.FALL,.14));out.add(part(shard(.13,.31,.42,.16,.11,.3),2,Motion.FALL,.25));}
   case "earthen_wall"->{out.add(core(box(0,-.35,.80,.12,.17,1,.87f),Motion.RISE));out.add(part(join(shard(-.65,.14,.08,.21,.14,.4),shard(.65,.10,.06,.25,.14,-.5)),1,Motion.FALL,.19));out.add(part(beam(p(-.72,-.1,.19),p(.72,-.1,.19),.025,1,.95f),2,Motion.PULSE,.12));}
   case "quake_blade"->{for(int i=0;i<5;i++)out.add(part(shard(i%2==0?-.12:.12,-.1,-.55+i*.33,.18+i*.025,.13,i%2==0?.45:-.45),i%2==0?0:i==1?1:2,Motion.STAGGER,i*.055));out.add(part(beam(p(0,-.11,-.65),p(0,-.11,1),.055,1,.92f),1,Motion.TRAVEL,.09));}
   case "worldbreaker"->{out.add(core(ring(.91,-.18,.095,8,.90f),Motion.OPEN));for(int i=0;i<6;i++){double a=i*Math.PI/3;out.add(part(shard(Math.cos(a)*.68,.20+(i%2)*.27,Math.sin(a)*.68,.31,.20,a*.3),i<2?0:i<4?1:2,Motion.FALL,i*.045));}}
   // Dark constructs: solid tapering coils, sealed spectral edges and an eclipse body.
   case "shadow_bolt"->{out.add(core(move(tendril(.19,1.05,1.1,0,6,.90f),0,0,0,0,Math.PI/2,1),Motion.TRAVEL));out.add(core(forwardCrystal(.96,.12),Motion.TRAVEL));out.add(part(move(tendril(.28,.73,.65,Math.PI,4,.61f),0,0,.1,0,Math.PI/2,1),1,Motion.ORBIT,.08));out.add(part(at(orb(.09,.18,.74f),-.32,.16,.36),2,Motion.FALL,.21));}
   case "curse_mark"->{out.add(core(ring(.48,.08,.075,6,.85f),Motion.ORBIT));out.add(core(tendril(.30,.82,.85,0,4,.74f),Motion.RISE));out.add(part(move(tendril(.34,.77,.8,Math.PI,4,.66f),0,0,0,0,0,1),1,Motion.ORBIT,.1));out.add(part(at(orb(.11,.18,.9f),0,.66,0),2,Motion.PULSE,.23));}
   case "shadow_edge"->{out.add(core(arc(1.07,2.9,.13,.9,-.06),Motion.TRAVEL));out.add(part(move(tendril(.21,.90,.8,0,5,.78f),-.50,-.04,.65,-.9,.5,1),1,Motion.ORBIT,.12));out.add(part(swordAt(.51,-.3,.68,.58,.11,-.8,.2),2,Motion.STAGGER,.19));}
   case "eclipse_nova"->{out.add(core(move(ring(.68,0,.12,6,.91f),0,.28,0,0,Math.PI/2,1),Motion.ORBIT));out.add(core(ring(.81,.16,.07,4,.84f),Motion.OPEN));for(int i=0;i<3;i++){double a=i*Math.PI*2/3;out.add(part(swordAt(Math.cos(a)*.66,-.24,Math.sin(a)*.66,.64,.11,-a,.3),i==0?0:i==1?1:2,Motion.OPEN,i*.045));}}
   // Healing blooms use closed light petals, with braided ascending ribbons for Renew.
   case "mend"->{out.add(core(at(orb(.18,.45,.86f),0,-.10,0),Motion.RISE));for(int i=0;i<5;i++){double a=i*Math.PI*2/5;out.add(part(move(crystal(.62,.18,.11,.74f),Math.cos(a)*.25,-.16,Math.sin(a)*.25,Math.cos(a)*-.65,Math.sin(a)*.65,1),i<2?0:i<4?1:2,Motion.OPEN,i*.03));}}
   case "renew"->{out.add(core(tendril(.40,1.04,.85,0,6,.78f),Motion.RISE));out.add(core(tendril(.40,1.04,.85,Math.PI,6,.78f),Motion.RISE));out.add(part(join(shard(.38,.2,.24,.22,.10,.4),shard(-.35,.63,-.1,.22,.10,-.4)),1,Motion.ORBIT,.1));out.add(part(at(orb(.12,.22,.86f),0,1.08,0),2,Motion.PULSE,.18));}
   case "sanctuary"->{out.add(core(ring(.85,-.36,.065,6,.85f),Motion.OPEN));for(int i=0;i<4;i++){double a=i*Math.PI/2;out.add(part(shard(Math.cos(a)*.68,-.28,Math.sin(a)*.68,1.01,.15,0),i%2==0?0:1,Motion.RISE,i*.04));}out.add(part(ring(.53,.72,.045,6,.74f),2,Motion.ORBIT,.14));}
   case "restoration"->{out.add(core(at(orb(.27,.80,.94f),0,-.22,0),Motion.RISE));for(int i=0;i<8;i++){double a=i*Math.PI/4;out.add(part(move(crystal(i%2==0?.83:.57,.20,.12,.82f),Math.cos(a)*.42,-.18,Math.sin(a)*.42,-Math.cos(a)*.65,Math.sin(a)*.65,1),i%2==0?0:i<5?1:2,Motion.OPEN,(i%3)*.045));}out.add(part(ring(.77,-.21,.045,6,.80f),2,Motion.ORBIT,.19));}
   // Astral forms: distinct stars, protective panels and actual three-dimensional celestial swords.
   case "light"->{out.add(core(join(crystal(.72,.17,0,.94f),move(crystal(.72,.17,0,.94f),0,.35,0,Math.PI/2,0,1)),Motion.PULSE));out.add(part(ring(.62,.31,.04,6,.8f),1,Motion.ORBIT,0));out.add(part(join(shard(.63,.24,0,.14,.07,0),shard(-.63,.24,0,.14,.07,0)),2,Motion.ORBIT,.06));}
   case "mirror_guard"->{out.add(core(move(box(0,-.43,.35,.86,.075,0,.65f),0,0,.47,Math.PI/4,0,1),Motion.OPEN));out.add(part(join(at(box(0,-.31,.17,.62,.06,1,.7f),-.5,0,.36),at(box(0,-.31,.17,.62,.06,1,.7f),.5,0,.36)),1,Motion.OPEN,.1));out.add(part(ring(.63,.1,.035,6,.78f),2,Motion.ORBIT,.14));}
   case "guardian_stance"->{out.add(core(at(box(0,-.4,.38,.83,.11,0,.75f),0,0,.50),Motion.RISE));out.add(core(swordAt(-.58,-.41,.28,1.05,.105,-.16,0),Motion.RISE));out.add(core(swordAt(.58,-.41,.28,1.05,.105,.16,0),Motion.RISE));out.add(part(ring(.74,-.39,.045,6,.86f),1,Motion.ORBIT,.08));out.add(part(shard(0,.54,.50,.26,.13,0),2,Motion.PULSE,.13));}
   case "aura_wave"->{out.add(core(arc(.87,2.2,.14,.75,.15),Motion.TRAVEL));out.add(core(swordAt(0,-.31,.67,.65,.12,0,Math.PI/2),Motion.TRAVEL));out.add(part(at(arc(.66,1.8,.065,.7,.1),0,.12,-.2),1,Motion.TRAVEL,.13));out.add(part(orb(.09,.2,.92f),2,Motion.FALL,.26));}
   case "blade_focus"->{out.add(core(swordAt(-.36,-.3,.16,.76,.12,-.25,0),Motion.ORBIT));out.add(core(swordAt(.36,-.3,-.16,.76,.12,.25,0),Motion.ORBIT));out.add(part(ring(.58,-.18,.04,6,.79f),1,Motion.ORBIT,.05));out.add(part(shard(0,.57,0,.24,.12,0),2,Motion.PULSE,.18));}
   case "arcane_step"->{out.add(core(swordAt(-.32,-.35,-.65,.76,.095,-.35,.6),Motion.TRAVEL));out.add(core(swordAt(.32,-.25,-.95,.65,.095,.35,.6),Motion.TRAVEL));out.add(part(move(ring(.39,0,.045,6,.83f),0,0,-.48,0,Math.PI/2,1),1,Motion.OPEN,.12));out.add(part(join(shard(-.5,.15,-.8,.20,.09,.6),shard(.5,-.10,-.3,.20,.09,-.6)),2,Motion.FALL,.2));}
   case "astral_crescent"->{out.add(core(arc(1.18,3.15,.105,.95,.12),Motion.TRAVEL));out.add(part(at(arc(.92,2.75,.045,.8,.1),0,.12,-.06),1,Motion.TRAVEL,.13));out.add(part(join(shard(-1,.12,.40,.22,.10,.7),shard(1,.12,.40,.22,.10,-.7)),2,Motion.FALL,.22));}
   case "falling_star"->{out.add(core(move(join(crystal(.90,.32,0,.96f),move(crystal(.68,.15,0,.96f),0,.18,0,Math.PI/2,0,1)),0,.25,0,0,Math.PI,1),Motion.FALL));out.add(part(ring(.76,-.22,.075,6,.91f),1,Motion.OPEN,.26));out.add(part(join(shard(-.50,.05,.3,.33,.11,.5),shard(.5,.05,-.3,.33,.11,-.5)),2,Motion.FALL,.33));}
   case "phantom_blades"->{for(int i=0;i<4;i++)out.add(part(swordAt((i-1.5)*.33,-.32+(i%2)*.15,.45+(i%2)*.2,.88,.10,(i-1.5)*-.3,.2),i<2?0:i==2?1:2,Motion.STAGGER,i*.09));}
   case "celestial_rush"->{out.add(core(swordAt(-.36,-.27,-.65,1.13,.12,-.18,Math.PI/2),Motion.TRAVEL));out.add(core(swordAt(.36,-.27,-.93,1.13,.12,.18,Math.PI/2),Motion.TRAVEL));out.add(part(beam(p(-.32,0,-1.3),p(-.25,.13,.15),.065,1,.84f),1,Motion.TRAVEL,.10));out.add(part(beam(p(.32,0,-1.3),p(.25,.13,.15),.065,1,.84f),2,Motion.TRAVEL,.16));}
   case "sovereign_judgment"->{out.add(core(swordAt(0,1.25,0,1.70,.26,Math.PI,0),Motion.FALL));out.add(part(ring(.79,-.35,.08,6,.92f),1,Motion.OPEN,.23));for(int i=0;i<4;i++){double a=i*Math.PI/2;out.add(part(shard(Math.cos(a)*.62,-.25,Math.sin(a)*.62,.42,.12,-Math.cos(a)*.5),i<2?0:2,Motion.RISE,.27+i*.025));}}
   case "heavenly_sword_domain"->{for(int i=0;i<5;i++){double a=i*Math.PI*2/5;out.add(part(swordAt(Math.cos(a)*.73,-.05,Math.sin(a)*.73,1.40,.13,0,-.12),i<3?0:i==3?1:2,Motion.RISE,i*.04));}}
   // Sword combat uses weighted closed blade edges and solid sword afterimages.
   case "quick_cut"->{out.add(core(arc(.83,1.8,.055,.7,.11),Motion.TRAVEL));out.add(part(swordAt(.24,-.24,.64,.59,.07,-.75,.12),1,Motion.STAGGER,.08));out.add(part(beam(p(-.27,.08,.51),p(.60,.02,.73),.023,1,.8f),2,Motion.TRAVEL,.12));}
   case "rapid_slash"->{out.add(core(move(arc(.88,2.1,.06,.75,.12),0,0,0,-.45,0,1),Motion.TRAVEL));out.add(part(move(arc(.78,2.0,.055,.75,.11),.05,.13,0,.48,0,1),1,Motion.STAGGER,.14));out.add(part(swordAt(-.31,-.22,.66,.65,.075,-.55,.2),2,Motion.STAGGER,.25));}
   case "quick_dash"->{out.add(core(swordAt(0,-.20,-.68,.93,.075,-.12,Math.PI/2),Motion.TRAVEL));out.add(part(join(beam(p(-.26,.10,-1.2),p(-.26,.10,.1),.035,1,.82f),beam(p(.26,-.1,-1.1),p(.26,-.1,.2),.035,1,.82f)),1,Motion.TRAVEL,.08));out.add(part(at(arc(.37,1.6,.035,.7,0),0,0,-.3),2,Motion.STAGGER,.15));}
   case "heavy_cut"->{out.add(core(move(arc(1.05,2.75,.135,1,.12),0,0,0,Math.PI/2,0,1),Motion.TRAVEL));out.add(core(swordAt(0,-.36,.65,.92,.16,-.15,.15),Motion.TRAVEL));out.add(part(shard(-.39,-.32,.65,.24,.14,.6),1,Motion.FALL,.2));out.add(part(shard(.37,-.32,.65,.21,.12,-.6),2,Motion.FALL,.27));}
   case "heavy_sweep"->{out.add(core(arc(1.27,2.9,.12,.85,-.10),Motion.TRAVEL));out.add(part(at(arc(1.0,2.4,.055,.7,-.10),0,-.18,-.08),1,Motion.STAGGER,.12));out.add(part(join(shard(-.72,-.22,.61,.18,.12,.6),shard(.72,-.22,.61,.18,.12,-.6)),2,Motion.FALL,.22));}
   case "crushing_edge"->{out.add(core(move(arc(.93,2.35,.13,.9,.11),-.18,0,0,-.55,0,1),Motion.TRAVEL));out.add(core(move(arc(.93,2.35,.13,.9,.11),.18,0,0,.55,0,1),Motion.TRAVEL));out.add(part(at(orb(.24,.46,.93f),0,-.12,.74),1,Motion.PULSE,.15));out.add(part(shard(0,-.22,.91,.23,.17,0),2,Motion.FALL,.24));}
   case "guard"->{out.add(core(swordAt(0,-.35,.42,.89,.10,-.55,0),Motion.STILL));out.add(part(beam(p(-.36,.14,.43),p(.33,-.20,.43),.045,1,.84f),1,Motion.PULSE,.06));out.add(part(at(orb(.11,.20,.93f),0,.12,.47),2,Motion.PULSE,.12));}
   case "riposte"->{out.add(core(swordAt(0,-.14,.13,.98,.095,0,Math.PI/2),Motion.TRAVEL));out.add(part(move(arc(.44,1.75,.07,.85,.05),0,0,.17,.65,0,1),1,Motion.OPEN,.04));out.add(part(at(forwardCrystal(.43,.07),.17,0,.59),2,Motion.TRAVEL,.13));}
   case "piercing_thrust"->{out.add(core(swordAt(0,-.12,.05,1.48,.105,0,Math.PI/2),Motion.TRAVEL));out.add(part(join(beam(p(-.15,0,.15),p(-.04,0,1.30),.038,1,.85f),beam(p(.15,0,.15),p(.04,0,1.30),.038,1,.85f)),1,Motion.TRAVEL,.06));out.add(part(at(forwardCrystal(.48,.075),0,0,.85),2,Motion.TRAVEL,.13));}
   case "seismic_cut"->{out.add(core(move(arc(.87,2.3,.13,.9,.08),0,-.12,0,Math.PI/2,0,1),Motion.TRAVEL));for(int i=0;i<5;i++)out.add(part(shard((i%2==0?-.15:.15),-.23,-.23+i*.26,.24+i*.04,.16,(i%2==0?.25:-.25)),i<2?0:i<4?1:2,Motion.STAGGER,i*.05));}
   default->throw new IllegalArgumentException("Missing cinematic ability: "+id);
  }
  return List.copyOf(out);
 }
}

package dk.starswordmaster.ui;
import java.util.*;
/** Shared tree positions for rendering, hit testing, initial framing and coverage checks. */
public final class SkillTreeLayout {
 public record Node(String id,double x,double y){}
 private static final List<Node> nodes=new ArrayList<>();
 public static final List<Node> NODES;
 static {
  node("piercing_thrust",-420,110);node("seismic_cut",-90,450);node("guardian_stance",410,90);node("ember_ring",-510,-300);node("ice_prison",-140,-410);node("restoration",260,-310);
  node("flow_training",-320,210);node("impact_training",-100,320);node("guard_training",260,20);node("fire_affinity",-230,-80);node("frost_affinity",60,-190);node("restorative_affinity",320,-20);
  node("spark",-160,-90);node("mana_control",-250,-180);node("fire_burst",-360,-260);node("arcane_mastery",-250,-330);
  node("frost_touch",0,-140);node("ice_lance",-90,-260);node("frost_nova",20,-330);
  node("light",160,-100);node("mend",230,-70);node("sanctuary",190,-240);
  node("quick_cut",-180,80);node("quick_dash",-290,150);node("aura_awaken",-240,230);node("aura_wave",-340,310);node("blade_focus",-170,330);node("sword_mastery",-260,420);
  node("heavy_cut",0,140);node("heavy_sweep",-60,240);
  node("rapid_slash",-400,180);node("crushing_edge",-20,370);node("mirror_guard",390,170);node("flame_lance",-410,-380);node("frost_ward",110,-390);node("arcane_step",100,420);node("glacier_sweep",350,420);node("renew",350,-160);
  node("guard",190,70);node("riposte",290,140);node("element_binding",200,240);node("flame_blade",140,330);node("frost_blade",300,320);node("spellblade_mastery",220,420);
  node("astral_sword_sovereign",-350,630);node("celestial_archon",-260,-570);node("eclipse_sovereign",300,630);
  node("strength_training_1",-10,550);node("strength_training_2",-10,650);node("strength_training_3",-10,750);
  node("agility_training_1",-560,260);node("agility_training_2",-650,320);node("agility_training_3",-740,380);
  node("endurance_training_1",530,60);node("endurance_training_2",630,120);node("endurance_training_3",730,180);
  node("spirit_training_1",-490,-100);node("spirit_training_2",-600,-150);node("spirit_training_3",-710,-200);
  node("restorative_training_1",530,-120);node("restorative_training_2",630,-190);node("restorative_training_3",730,-260);
  NODES=List.copyOf(nodes);nodes.clear();
 }
 private SkillTreeLayout(){}
 private static void node(String id,double x,double y){nodes.add(new Node(id,x,y));}
}

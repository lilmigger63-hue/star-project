package dk.starswordmaster.progression;
import java.util.*;
import static dk.starswordmaster.progression.PlayerProgress.PlayerClass.*;
public record Ability(String id, Skill skill, int requirement, String parent,
 PlayerProgress.PlayerClass exclusiveClass, boolean active, double mana, double stamina, int cooldownTicks) {
 public static final Map<String,Ability> ALL;
 static {
  var a=new LinkedHashMap<String,Ability>();
  add(a,"spark",Skill.FIRE,1,"",null,true,12,0,40);
  add(a,"frost_touch",Skill.FROST,1,"",null,true,12,0,60);
  add(a,"light",Skill.SUPPORT,1,"",null,true,8,0,40);
  add(a,"mend",Skill.SUPPORT,1,"",null,true,22,0,160);
  add(a,"quick_cut",Skill.SWORD,1,"",null,true,10,4,40);
  add(a,"quick_dash",Skill.SWORD,3,"quick_cut",null,true,14,6,80);
  add(a,"heavy_cut",Skill.SWORD,1,"",null,true,14,6,70);
  add(a,"heavy_sweep",Skill.SWORD,3,"heavy_cut",null,true,18,8,100);
  add(a,"guard",Skill.SWORD,1,"",null,true,10,4,80);
  add(a,"riposte",Skill.SWORD,3,"guard",null,true,16,6,100);
  add(a,"aura_awaken",Skill.SWORD,5,"quick_cut",SWORDSMAN,false,0,0,0);
  add(a,"aura_wave",Skill.SWORD,5,"aura_awaken",SWORDSMAN,true,26,8,140);
  add(a,"blade_focus",Skill.SWORD,5,"aura_awaken",SWORDSMAN,true,24,5,240);
  add(a,"sword_mastery",Skill.SWORD,15,"aura_awaken",SWORDSMAN,false,0,0,0);
  add(a,"mana_control",Skill.FIRE,5,"spark",MAGE,false,0,0,0);
  add(a,"fire_burst",Skill.FIRE,5,"mana_control",MAGE,true,32,0,140);
  add(a,"ice_lance",Skill.FROST,5,"mana_control",MAGE,true,28,0,120);
  add(a,"sanctuary",Skill.SUPPORT,5,"mana_control",MAGE,true,36,0,240);
  add(a,"frost_nova",Skill.FROST,10,"ice_lance",MAGE,true,40,0,200);
  add(a,"arcane_mastery",Skill.FIRE,15,"mana_control",MAGE,false,0,0,0);
  add(a,"element_binding",Skill.SWORD,5,"heavy_cut",SPELLBLADE,false,0,0,0);
  add(a,"flame_blade",Skill.SWORD,5,"element_binding",SPELLBLADE,true,24,6,140);
  add(a,"frost_blade",Skill.SWORD,5,"element_binding",SPELLBLADE,true,24,6,140);
  add(a,"spellblade_mastery",Skill.SWORD,15,"element_binding",SPELLBLADE,false,0,0,0);
  add(a,"rapid_slash",Skill.SWORD,8,"quick_dash",SWORDSMAN,true,18,5,60);
  add(a,"crushing_edge",Skill.SWORD,8,"heavy_sweep",SWORDSMAN,true,24,9,120);
  add(a,"mirror_guard",Skill.SWORD,8,"guard",SWORDSMAN,true,20,4,200);
  add(a,"flame_lance",Skill.FIRE,8,"fire_burst",MAGE,true,34,0,160);
  add(a,"frost_ward",Skill.FROST,8,"ice_lance",MAGE,true,26,0,220);
  add(a,"arcane_step",Skill.SWORD,8,"flame_blade",SPELLBLADE,true,22,5,140);
  add(a,"glacier_sweep",Skill.SWORD,8,"frost_blade",SPELLBLADE,true,30,8,180);
  add(a,"renew",Skill.SUPPORT,5,"mend",null,true,28,0,220);
  add(a,"piercing_thrust",Skill.SWORD,5,"quick_dash",null,true,16,5,80);
  add(a,"seismic_cut",Skill.SWORD,10,"heavy_sweep",SWORDSMAN,true,30,10,160);
  add(a,"guardian_stance",Skill.SWORD,8,"riposte",null,true,18,5,180);
  add(a,"ember_ring",Skill.FIRE,10,"flame_lance",MAGE,true,32,0,180);
  add(a,"ice_prison",Skill.FROST,10,"ice_lance",MAGE,true,32,0,160);
  add(a,"restoration",Skill.SUPPORT,8,"sanctuary",MAGE,true,35,0,240);
  add(a,"flow_training",Skill.SWORD,5,"quick_dash",null,false,0,0,0);
  add(a,"impact_training",Skill.SWORD,5,"heavy_sweep",null,false,0,0,0);
  add(a,"guard_training",Skill.SWORD,5,"guard",null,false,0,0,0);
  add(a,"fire_affinity",Skill.FIRE,5,"spark",null,false,0,0,0);
  add(a,"frost_affinity",Skill.FROST,5,"frost_touch",null,false,0,0,0);
  add(a,"restorative_affinity",Skill.SUPPORT,5,"mend",null,false,0,0,0);
  ALL=Collections.unmodifiableMap(a);
 }
 private static void add(Map<String,Ability> a,String id,Skill s,int req,String parent,PlayerProgress.PlayerClass c,boolean active,double mana,double stamina,int cooldown){
  a.put(id,new Ability(id,s,req,parent,c,active,mana,stamina,cooldown));
 }
}

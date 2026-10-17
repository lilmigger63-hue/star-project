package dk.starswordmaster.progression;
import java.util.*;
import static dk.starswordmaster.progression.PlayerProgress.PlayerClass.*;
public record Ability(String id, Skill skill, int requirement, String parent,
 PlayerProgress.PlayerClass exclusiveClass, boolean active, double mana, double stamina, int cooldownTicks,
 int characterRequirement,PlayerProgress.Stat statBonus) {
 public Ability(String id,Skill skill,int requirement,String parent,PlayerProgress.PlayerClass exclusiveClass,boolean active,double mana,double stamina,int cooldownTicks){this(id,skill,requirement,parent,exclusiveClass,active,mana,stamina,cooldownTicks,1,null);}
 public enum DamageKind { NONE,SWORD,MAGIC,ENCHANTED_SWORD }
 public DamageKind damageKind(){return switch(id){
  case "astral_crescent","falling_star","phantom_blades","celestial_rush","sovereign_judgment","heavenly_sword_domain","quick_cut","heavy_cut","heavy_sweep","riposte","aura_wave","rapid_slash","crushing_edge","piercing_thrust","seismic_cut"->DamageKind.SWORD;
  case "static_spark","chain_lightning","heavens_wrath","water_bolt","tidal_wave","ocean_prison","stone_spike","worldbreaker","shadow_bolt","curse_mark","eclipse_nova","spark","frost_touch","fire_burst","ice_lance","frost_nova","flame_lance","ember_ring","ice_prison"->DamageKind.MAGIC;
  case "aqua_blade","quake_blade","shadow_edge","flame_blade","frost_blade","glacier_sweep"->DamageKind.ENCHANTED_SWORD;
  default->DamageKind.NONE;
 };}
 public boolean classAllowed(PlayerProgress.PlayerClass cls){
  if(id.equals("quake_blade"))return cls==SWORDSMAN||cls==SPELLBLADE;
  return exclusiveClass==null||exclusiveClass==cls;
 }
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
  a.put("astral_sword_sovereign",new Ability("astral_sword_sovereign",Skill.SWORD,20,"sword_mastery",SWORDSMAN,false,0,0,0,25,null));
  a.put("celestial_archon",new Ability("celestial_archon",Skill.FIRE,20,"arcane_mastery",MAGE,false,0,0,0,25,null));
  a.put("eclipse_sovereign",new Ability("eclipse_sovereign",Skill.SWORD,20,"spellblade_mastery",SPELLBLADE,false,0,0,0,25,null));
  training(a,"strength_training",Skill.SWORD,"impact_training",PlayerProgress.Stat.STRENGTH);
  training(a,"agility_training",Skill.SWORD,"flow_training",PlayerProgress.Stat.AGILITY);
  training(a,"endurance_training",Skill.SWORD,"guard_training",PlayerProgress.Stat.ENDURANCE);
  training(a,"spirit_training",Skill.FIRE,"fire_affinity",PlayerProgress.Stat.SPIRIT);
  training(a,"restorative_training",Skill.SUPPORT,"restorative_affinity",PlayerProgress.Stat.SPIRIT);
  // Basics remain open to every class; advanced elemental and sword branches retain class gates.
  expansion(a,"aura_sense",Skill.SWORD,3,"quick_cut",null,false,0,0,0,10);
  expansion(a,"static_spark",Skill.LIGHTNING,1,"spark",null,true,14,0,50,1);
  expansion(a,"chain_lightning",Skill.LIGHTNING,5,"static_spark",null,true,30,0,140,10);
  expansion(a,"thunder_step",Skill.LIGHTNING,8,"chain_lightning",SPELLBLADE,true,22,6,160,10);
  expansion(a,"heavens_wrath",Skill.LIGHTNING,20,"chain_lightning",MAGE,true,65,0,500,25);
  expansion(a,"water_bolt",Skill.WATER,1,"frost_touch",null,true,14,0,50,1);
  expansion(a,"tidal_wave",Skill.WATER,5,"water_bolt",null,true,30,0,160,10);
  expansion(a,"aqua_blade",Skill.WATER,8,"tidal_wave",SPELLBLADE,true,26,6,140,10);
  expansion(a,"ocean_prison",Skill.WATER,15,"tidal_wave",MAGE,true,40,0,240,20);
  expansion(a,"stone_spike",Skill.EARTH,1,"heavy_cut",null,true,16,0,60,1);
  expansion(a,"earthen_wall",Skill.EARTH,5,"stone_spike",null,true,24,0,220,10);
  expansion(a,"quake_blade",Skill.EARTH,10,"earthen_wall",null,true,34,10,180,15);
  expansion(a,"worldbreaker",Skill.EARTH,20,"earthen_wall",MAGE,true,70,0,600,25);
  expansion(a,"shadow_bolt",Skill.DARK,1,"light",null,true,16,0,60,1);
  expansion(a,"curse_mark",Skill.DARK,5,"shadow_bolt",null,true,24,0,140,10);
  expansion(a,"shadow_edge",Skill.DARK,8,"curse_mark",SPELLBLADE,true,28,6,160,10);
  expansion(a,"eclipse_nova",Skill.DARK,20,"curse_mark",MAGE,true,70,0,600,25);
  expansion(a,"astral_crescent",Skill.SWORD,8,"aura_wave",SWORDSMAN,true,24,7,100,12);
  expansion(a,"falling_star",Skill.SWORD,12,"seismic_cut",SWORDSMAN,true,34,10,180,18);
  expansion(a,"phantom_blades",Skill.SWORD,15,"rapid_slash",SWORDSMAN,true,32,9,160,20);
  expansion(a,"celestial_rush",Skill.SWORD,12,"quick_dash",SWORDSMAN,true,28,8,160,18);
  expansion(a,"sovereign_judgment",Skill.SWORD,25,"astral_sword_sovereign",SWORDSMAN,true,50,14,320,30);
  expansion(a,"heavenly_sword_domain",Skill.SWORD,30,"sovereign_judgment",SWORDSMAN,true,80,18,700,40);
  ALL=Collections.unmodifiableMap(a);
 }
 private static void expansion(Map<String,Ability> a,String id,Skill s,int req,String parent,PlayerProgress.PlayerClass cls,boolean active,double mana,double stamina,int cooldown,int character){a.put(id,new Ability(id,s,req,parent,cls,active,mana,stamina,cooldown,character,null));}
 private static void training(Map<String,Ability> a,String prefix,Skill skill,String parent,PlayerProgress.Stat stat){
  for(int tier=1;tier<=3;tier++){String id=prefix+"_"+tier;a.put(id,new Ability(id,skill,tier*5,parent,null,false,0,0,0,1,stat));parent=id;}
 }
 private static void add(Map<String,Ability> a,String id,Skill s,int req,String parent,PlayerProgress.PlayerClass c,boolean active,double mana,double stamina,int cooldown){
  a.put(id,new Ability(id,s,req,parent,c,active,mana,stamina,cooldown));
 }
}

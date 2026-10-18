"""Bounded Minecraft retarget blend of verified CC0 samples into original skill choreography."""
import json,math
from pathlib import Path
DATA=json.loads((Path(__file__).parent/'assets/retargeted_motion.json').read_text())['curves']
MOTION={'aqua_blade': 'souls_main:SS_Main_Roll_Attack',
 'arcane_step': 'ual1:Roll',
 'astral_crescent': 'souls_main:SS_Main_Light_Attack_02',
 'aura_wave': 'ual1:Sword_Attack',
 'blade_focus': 'arms:Block_Idle',
 'celestial_rush': 'ual2:Sword_Dash',
 'chain_lightning': 'ual2:OverhandThrow',
 'crushing_edge': 'souls_heavy:SS_TH_Light_Attack_01',
 'curse_mark': 'ual1:Spell_Simple_Enter',
 'earthen_wall': 'hero:defend',
 'eclipse_nova': 'ual1:Spell_Simple_Exit',
 'ember_ring': 'ual1:Spell_Simple_Enter',
 'falling_star': 'ual2:OverhandThrow',
 'fire_burst': 'ual2:OverhandThrow',
 'flame_blade': 'souls_main:SS_Main_Charge01_Heavy',
 'flame_lance': 'ual2:OverhandThrow',
 'frost_blade': 'arms:Attack_04',
 'frost_nova': 'ual1:Push_Loop',
 'frost_touch': 'ual1:Spell_Simple_Enter',
 'frost_ward': 'ual1:Spell_Simple_Enter',
 'glacier_sweep': 'ual2:Sword_Heavy_Combo',
 'guard': 'hero:defend',
 'guardian_stance': 'hero:defend',
 'heavenly_sword_domain': 'ual2:Sword_Heavy_Combo',
 'heavens_wrath': 'ual1:Spell_Simple_Exit',
 'heavy_cut': 'souls_heavy:SS_TH_Charge01_Heavy',
 'heavy_sweep': 'souls_main:SS_Main_Light_Attack_02',
 'ice_lance': 'ual1:Spell_Simple_Shoot',
 'ice_prison': 'ual1:Spell_Simple_Shoot',
 'light': 'ual1:Spell_Simple_Exit',
 'mend': 'arms:Skill_Heal',
 'mirror_guard': 'ual2:Sword_Block',
 'ocean_prison': 'ual1:Spell_Simple_Exit',
 'phantom_blades': 'ual2:Sword_Regular_Combo',
 'piercing_thrust': 'arms:Attack_02',
 'quake_blade': 'souls_heavy:SS_TH_Charge01_Heavy',
 'quick_cut': 'souls_main:SS_Main_Light_Attack_01',
 'quick_dash': 'souls_main:SS_Main_Run_Attack',
 'rapid_slash': 'ual2:Sword_Regular_Combo',
 'renew': 'arms:Skill_Heal',
 'restoration': 'ual1:Spell_Simple_Exit',
 'riposte': 'arms:Attack_03',
 'sanctuary': 'hero:defend',
 'seismic_cut': 'ual2:Sword_Heavy_Combo',
 'shadow_bolt': 'ual1:Spell_Simple_Shoot',
 'shadow_edge': 'arms:Attack_01',
 'sovereign_judgment': 'souls_heavy:SS_TH_Charge01_Heavy',
 'spark': 'ual1:Spell_Simple_Shoot',
 'static_spark': 'ual1:Spell_Simple_Shoot',
 'stone_spike': 'ual2:OverhandThrow',
 'thunder_step': 'ual2:Sword_Dash',
 'tidal_wave': 'ual1:Push_Loop',
 'water_bolt': 'ual1:Spell_Simple_Shoot',
 'worldbreaker': 'ual2:Sword_Heavy_Combo'}

def sample(key,t):
 samples=DATA[key]['samples'];at=min(len(samples)-1,max(0,t)*(len(samples)-1));a=int(at);b=min(len(samples)-1,a+1);fraction=at-a
 return [x+(y-x)*fraction for x,y in zip(samples[a],samples[b])]

def blend(ability,moves,duration):
 key=MOTION[ability]
 for move in moves:
  t=move['tick']/duration
  if t<=0 or t>=1:continue
  # Original skill silhouette dominates. Ease source influence out fully before neutral recovery.
  weight=.24*math.sin(math.pi*t)**.8;source=sample(key,t)
  for bone_index,bone in enumerate(('rightArm','leftArm','torso')):
   for axis_index,axis in enumerate(('pitch','yaw','roll')):
    limit=28 if bone=='torso' else 160
    amount=weight*(.35 if bone=='torso' else 1)
    move[bone][axis]=round(max(-limit,min(limit,move[bone][axis]+source[bone_index*3+axis_index]*amount)),3)
 return moves

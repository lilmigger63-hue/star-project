"""Run with Blender --background --factory-startup --disable-autoexec --python this.py -- ASSET_CACHE.

Extract evaluated upper-body rotations only. No imported models, root motion, scripts,
leg or head channels are shipped. PAL generation blends these CC0 curves with the
original authored performances; native look/locomotion and exact durations remain.
"""
import bpy,sys,json,math,hashlib
from pathlib import Path
from mathutils import Matrix
cache=Path(sys.argv[sys.argv.index('--')+1]);root=Path(__file__).resolve().parents[1]
SOURCES=[
 ('souls_main','SS_Main.fbx',('upper_arm.R','upper_arm.L','spine.003'),['SS_Main_Light_Attack_01','SS_Main_Light_Attack_02','SS_Main_Run_Attack','SS_Main_Roll_Attack','SS_Main_Charge01_Heavy']),
 ('souls_heavy','SS_TH.fbx',('upper_arm.R','upper_arm.L','spine.003'),['SS_TH_Charge01_Heavy','SS_TH_Light_Attack_01']),
 ('ual1','quaternius1.glb',('upperarm_r','upperarm_l','spine_03'),['Sword_Attack','Spell_Simple_Enter','Spell_Simple_Shoot','Spell_Simple_Exit','Roll','Sprint_Loop','Push_Loop']),
 ('ual2','quaternius2.glb',('upperarm_r','upperarm_l','spine_03'),['Sword_Regular_Combo','Sword_Heavy_Combo','Sword_Block','Sword_Dash','Shield_Dash','OverhandThrow']),
 ('arms','sword_arms.blend',('Right_Ombro.002','Left_Ombro.002','Root'),['Attack_01','Attack_02','Attack_03','Attack_04','Block_GetHit','Block_Idle','Skill_Heal']),
 ('hero','shield_hero.blend',('shoulder.r','shoulder.l','body'),['attack','defend','hurt']),
]
basis=Matrix(((1,0,0),(0,0,-1),(0,1,0)));inverse=basis.inverted();curves={}
for pack,filename,bones,names in SOURCES:
 bpy.ops.wm.read_factory_settings(use_empty=True)
 if filename.endswith('.blend'):bpy.ops.wm.open_mainfile(filepath=str(cache/filename),load_ui=False,use_scripts=False)
 elif filename.endswith('.fbx'):bpy.ops.import_scene.fbx(filepath=str(cache/filename))
 else:bpy.ops.import_scene.gltf(filepath=str(cache/filename))
 rig=next(o for o in bpy.data.objects if o.type=='ARMATURE');rig.animation_data_create();rig.animation_data.use_nla=False
 for desired in names:
  actions=[a for a in bpy.data.actions if a.name==desired or a.name.endswith('|'+desired) or a.name==desired+'_Armature']
  if len(actions)!=1:raise RuntimeError((filename,desired,[a.name for a in actions]))
  action=actions[0];rig.animation_data.action=action;lo,hi=action.frame_range;samples=[];previous=None
  for i in range(33):
   position=lo+(hi-lo)*i/32;bpy.context.scene.frame_set(int(position),subframe=position-int(position));evaluated=rig.evaluated_get(bpy.context.evaluated_depsgraph_get());row=[]
   for name in bones:
    # Global-space orientation delta includes evaluated IK, then changes Z-up to Minecraft coordinates.
    rest=rig.data.bones[name].matrix_local.to_3x3().normalized();pose=evaluated.pose.bones[name].matrix.to_3x3().normalized();delta=basis@(pose@rest.inverted())@inverse
    euler=delta.to_quaternion().to_euler('XYZ');row.extend(math.degrees(x) for x in euler)
   if previous:
    row=[a+360*round((b-a)/360) for a,b in zip(row,previous)]
   samples.append([round(max(-180,min(180,v)),4) for v in row]);previous=row
  curves[pack+':'+desired]={'source':filename,'action':action.name,'sourceFrames':[lo,hi],'bones':list(bones),'sha256':hashlib.sha256((cache/filename).read_bytes()).hexdigest(),'samples':samples}
output=root/'tools/assets/retargeted_motion.json';output.write_text(json.dumps({'license':'CC0-1.0','sampleCount':33,'curves':curves},separators=(',',':'))+'\n');print('EXTRACTED',len(curves),'verified source curves to',output)

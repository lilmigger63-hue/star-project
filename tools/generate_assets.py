"""Generate original placeholder pixel-art assets; no external artwork is copied."""
from PIL import Image, ImageDraw
from pathlib import Path
import random, math, json
root=Path(__file__).resolve().parents[1]/'src/main/resources/assets/starswordmaster'
icons=root/'textures/skills';icons.mkdir(parents=True,exist_ok=True)
names={
'spark':('Gnister','Lille flamme mod en fjende.', 'Spark'),
'frost_touch':('Frostgreb','Skader og gør fjenden langsom.','Frost Touch'),
'light':('Stjernelys','Giver nattesyn i 45 sekunder.','Starlight'),
'mend':('Lægende lys','Gendanner 3 livspoint.','Mending Light'),
'quick_cut':('Lynsnit','Hurtigt slag mod en fjende.','Quick Cut'),
'quick_dash':('Sværdskridt','Et hurtigt spring fremad.','Swordstep'),
'heavy_cut':('Kraftslag','Tungt, kraftfuldt sværdslag.','Heavy Cut'),
'heavy_sweep':('Stormfejning','Ram fjender tæt omkring dig.','Storm Sweep'),
'guard':('Stjerneparade','Kortvarig skadesmodstand.','Star Guard'),
'riposte':('Modhug','Slag med kort forsvarsbonus.','Riposte'),
'aura_awaken':('Auraopvågning','Åbner sværdklassens Adept-rang.','Aura Awakening'),
'aura_wave':('Aurabølge','Et kraftigt angreb på afstand.','Aura Wave'),
'blade_focus':('Sværdets vilje','Forstærker almindelige slag.','Blade Focus'),
'sword_mastery':('Sværdmester','Låser Mester-rangen op.','Sword Mastery'),
'mana_control':('Manakontrol','Åbner magikerens Adept-rang.','Mana Control'),
'fire_burst':('Flammeudbrud','Ild rammer fjender ved målet.','Fire Burst'),
'ice_lance':('Islanse','Kraftig frost mod en fjende.','Ice Lance'),
'sanctuary':('Helligdom','Stærkere heling og absorption.','Sanctuary'),
'frost_nova':('Frostnova','Frost rammer fjender omkring dig.','Frost Nova'),
'arcane_mastery':('Magimester','Låser Mester-rangen op.','Arcane Mastery'),
'element_binding':('Elementbinding','Åbner magisk sværds Adept-rang.','Element Binding'),
'flame_blade':('Flammesværd','Sværdslag der antænder fjenden.','Flame Blade'),
'frost_blade':('Frostsværd','Sværdslag der sænker fjenden.','Frost Blade'),
'spellblade_mastery':('Stjernesværdmester','Låser Mester-rangen op.','Starblade Mastery')}
names.update({
'rapid_slash':('Lynpres','Hurtigt slag der svækker fjenden.','Rapid Slash'),
'crushing_edge':('Knusende klinge','Tungt områdeslag mod fjender.','Crushing Edge'),
'mirror_guard':('Spejlparade','Absorption og skadesmodstand.','Mirror Guard'),
'flame_lance':('Flammelanse','Kraftig ild mod en fjende.','Flame Lance'),
'frost_ward':('Frostværn','Modstand mod ild og skade.','Frost Ward'),
'arcane_step':('Magisk sværdskridt','Spring frem med kort beskyttelse.','Arcane Step'),
'glacier_sweep':('Gletsjerfejning','Sværdslag med frost i et område.','Glacier Sweep'),
'renew':('Fornyelse','Regenererer liv over tid.','Renewal')})
names.update({
'piercing_thrust':('Gennemborende stød','Sværdstød med længere rækkevidde.','Piercing Thrust'),
'seismic_cut':('Jordrystende hug','Tungt områdeslag der sinker fjender.','Seismic Cut'),
'guardian_stance':('Vogterstilling','Modstand og kort absorption.','Guardian Stance'),
'ember_ring':('Glødering','Ild rammer fjender omkring dig.','Ember Ring'),
'ice_prison':('Frostlænker','Skade og kraftig sløvning i 5 sek.','Ice Shackles'),
'restoration':('Genoprettelse','Heling og regeneration.','Restoration'),
'flow_training':('Flydende klinge','Hurtig stil: 10% billigere/hurtigere.','Flow Training'),
'impact_training':('Kraftens vej','Tung stil: 15% mere evneskade.','Impact Training'),
'guard_training':('Vogterens vej','Defensiv stil: 25% mindre stamina.','Guard Training'),
'fire_affinity':('Ildtilknytning','Ildbesværgelser: 10% mindre mana.','Fire Affinity'),
'frost_affinity':('Frosttilknytning','Frostevner: 20% kortere cooldown.','Frost Affinity'),
'restorative_affinity':('Lægende tilknytning','Direkte heling er 25% stærkere.','Restorative Affinity')})
for index,(name,labels) in enumerate(names.items()):
 fire=name in ['spark','fire_burst','flame_blade','flame_lance','ember_ring','fire_affinity'];ice=name in ['frost_touch','ice_lance','frost_nova','frost_blade','frost_ward','glacier_sweep','ice_prison','frost_affinity'];support=name in ['light','mend','sanctuary','renew','restoration','restorative_affinity'];arcane='aura' in name or 'mana' in name or 'mastery' in name or 'binding' in name
 color=(255,116,35) if fire else (53,197,255) if ice else (72,239,146) if support else (175,93,255) if arcane else (255,203,76)
 im=Image.new('RGBA',(32,32),(6,12,22,255));d=ImageDraw.Draw(im)
 for r in range(14,3,-1):d.ellipse((16-r,16-r,16+r,16+r),fill=tuple(int(v*(0.12+(14-r)*0.035)) for v in color)+(255,))
 if support:
  if name=='light':
   for angle in range(0,360,45):
    t=math.radians(angle);d.line((16,16,16+12*math.cos(t),16+12*math.sin(t)),fill=color,width=2)
   d.ellipse((11,11,21,21),fill=(220,255,218))
  else:d.rectangle((13,6,19,26),fill=color);d.rectangle((6,13,26,19),fill=color);d.rectangle((15,9,17,23),fill=(225,255,239))
 elif ice:
  for angle in range(0,360,60):
   t=math.radians(angle);x=16+12*math.cos(t);y=16+12*math.sin(t);d.line((16,16,x,y),fill=color,width=2)
   for a in [-0.5,0.5]:d.line((16+8*math.cos(t),16+8*math.sin(t),16+12*math.cos(t+a),16+12*math.sin(t+a)),fill=(160,235,255),width=1)
 elif fire:
  d.polygon([(7,26),(5,19),(12,11),(12,4),(20,13),(23,8),(28,20),(24,27)],fill=color)
  d.polygon([(12,25),(12,18),(16,12),(22,23),(19,27)],fill=(255,215,72));d.polygon([(15,25),(17,19),(20,26)],fill=(255,250,214))
 else:
  d.polygon([(8,26),(25,5),(27,5),(27,9),(11,28)],fill=(219,233,255));d.line((10,24,25,8),fill=color,width=2);d.line((5,20,15,29),fill=color,width=3);d.line((8,25,5,29),fill=(159,107,58),width=3)
  if arcane:d.arc((3,3,28,28),20,285,fill=color,width=2)
 if index%3==0:d.rectangle((4,4,5,5),fill=(250,246,192));d.rectangle((26,25,27,26),fill=color)
 d.rectangle((0,0,31,31),outline=(83,103,126));im.save(icons/(name+'.png'))
# Original starry mountain landscape.
rng=random.Random(701);bg=Image.new('RGB',(1024,768));d=ImageDraw.Draw(bg)
for y in range(768):d.line((0,y,1024,y),fill=(8+int(y/100),19+int(y/65),35+int(y/35)))
for _ in range(500):
 x=rng.randrange(1024);y=rng.randrange(640);v=rng.randrange(70,180);d.rectangle((x,y,x+1,y+1),fill=(v,v,min(255,v+25)))
for base,color in [(450,(19,35,51)),(560,(12,25,39)),(680,(7,17,29))]:
 pts=[(0,768),(0,base)]+[(x,base+rng.randrange(-125,70)) for x in range(0,1050,75)]+[(1024,768)];d.polygon(pts,fill=color)
(root/'textures/gui').mkdir(parents=True,exist_ok=True);bg.save(root/'textures/gui/constellation.png')
(root/'lang').mkdir(exist_ok=True)
for lang in ['da_dk','en_us']:
 values={'key.category.starswordmaster.skills':'Star Swordmaster','key.starswordmaster.tree':'Færdighedstræ' if lang=='da_dk' else 'Skill tree'}
 for i in range(1,6):values['key.starswordmaster.slot'+str(i)]=('Evneplads ' if lang=='da_dk' else 'Skill slot ')+str(i)
 for id,(da,desc,en) in names.items():values['ability.starswordmaster.'+id]=da if lang=='da_dk' else en;values['description.starswordmaster.'+id]=desc
 (root/'lang'/(lang+'.json')).write_text(json.dumps(values,ensure_ascii=False,indent=2))
print('Generated',len(names),'original skill icons and a background')

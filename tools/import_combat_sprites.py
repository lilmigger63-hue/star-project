"""Compile only selected CC0 VFX source images into native particle-atlas frame resources.
Usage: python tools/import_combat_sprites.py /workspace/assets-cache/bdo-0.10
Preserves source alpha and frame timing; nearest-neighbor resizing keeps pixel edges sharp.
"""
import sys,zipfile,io,json,hashlib
from pathlib import Path
from PIL import Image,ImageOps
CACHE=Path(sys.argv[1]);ROOT=Path(__file__).resolve().parents[1];ASSETS=ROOT/'src/main/resources/assets/starswordmaster'

def image(archive,name):
 if archive:
  with zipfile.ZipFile(CACHE/archive) as z:return Image.open(io.BytesIO(z.read(name))).convert('RGBA')
 return Image.open(CACHE/name).convert('RGBA')

def strip(im,cols,rows):
 w,h=im.width//cols,im.height//rows
 return [im.crop((x*w,y*h,(x+1)*w,(y+1)*h)) for y in range(rows) for x in range(cols)]

sources=[
 ('slash_light','Cethiel','https://opengameart.org/content/weapon-slash-effect','weapon_classic.zip',[f'Classic/1/Classic_{i:02}.png' for i in range(1,7)],None),
 ('slash_curve','MetaShinryu','https://opengameart.org/content/slash-effect',None,['slash.png'],None),
 ('slash_finish','MetaShinryu','https://opengameart.org/content/slash-effect-collection','slash_collection.zip',['Slash Effect Collection/Big/Circular.png'],None),
 ('slash_heavy','MetaShinryu','https://opengameart.org/content/high-resolution-slash-effects','highres.zip',['High res slashes/Leap/Leap big.png'],None),
 ('slash_pixel','tbbk','https://opengameart.org/content/pixel-art-sword-slash-effect',None,['pixel_slash.png'],(3,3)),
 ('impact_spark','SpritePilot','https://spritepilot.com/free-game-assets/combat','spritepilot.zip',['spritepilot-combat-pack/32px/parry-flash-32px_strip7.png'],(7,1)),
 ('magic_flare','Kenney','https://kenney.nl/assets/particle-pack','kenney.zip',['PNG (Transparent)/flare_01.png'],None),
]
manifest=[]
for ink,author,url,archive,names,grid in sources:
 frames=[]
 for name in names:
  im=image(archive,name);frames.extend(strip(im,*grid) if grid else [im])
 textures=[]
 for i,im in enumerate(frames):
  # White luminance tint retains transparency; runtime element/personal aura color remains authoritative.
  alpha=im.getchannel('A');rgb=ImageOps.grayscale(im).point(lambda p:max(180,p));im=Image.merge('RGBA',(rgb,rgb,rgb,alpha))
  im.thumbnail((60,60),Image.Resampling.NEAREST);canvas=Image.new('RGBA',(64,64));canvas.alpha_composite(im,((64-im.width)//2,(64-im.height)//2))
  target=ASSETS/f'textures/particle/combat/{ink}_{i:02}.png';target.parent.mkdir(parents=True,exist_ok=True);canvas.save(target);target.with_suffix(".png.mcmeta").write_text('{"texture":{"blur":false}}\n');textures.append('starswordmaster:combat/'+target.stem)
 (ASSETS/'particles'/f'{ink}.json').write_text(json.dumps({'textures':textures},indent=2)+'\n')
 input_path=CACHE/(archive or names[0]);manifest.append({'ink':ink,'author':author,'source':url,'license':'CC0-1.0','download':input_path.name,'sha256':hashlib.sha256(input_path.read_bytes()).hexdigest(),'selectedFiles':names,'frames':len(frames),'outputTextures':textures})
(ROOT/'tools/assets/combat_sprite_manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
# Original archive notices are preserved, even though attribution is not required by CC0.
licenses=ASSETS/'licenses';licenses.mkdir(exist_ok=True)
for archive,name,target in [('kenney.zip','License.txt','kenney-particles.txt'),('spritepilot.zip','spritepilot-combat-pack/LICENSE.txt','spritepilot.txt')]:
 with zipfile.ZipFile(CACHE/archive) as z:(licenses/target).write_bytes(z.read(name))
for pack in ['quaternius1','quaternius2']:(licenses/(pack+'.txt')).write_bytes((CACHE/(pack+'_LICENSE.txt')).read_bytes())
print('Imported',sum(m['frames'] for m in manifest),'frames from all seven verified CC0 VFX sources')

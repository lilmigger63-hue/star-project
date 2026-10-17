#!/usr/bin/env python3
"""Reproduce only the 0.5 expansion's original PAL performances and pixel art.

No downloaded art, image resizing, randomized pixels or old-resource rewrites.
Style poses are shared choreography anchors; skill-specific gestures and timing
give each performance its own anticipation, release, follow-through and recovery.
Rotations use PAL v3 upper-body semantics; the runtime handles handedness/tint.
Requires Pillow, already used by the repository's other artwork generators.
"""
import json
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/starswordmaster'
ZERO = ((0, 0, 0),) * 3

# Seven authored poses per style: right arm, left arm, torso (pitch/yaw/roll).
STYLES = {
 'dual_cast': (20, [
  ((-25,22,12),(-25,-22,-12),(3,0,0)),
  ((-62,38,26),(-62,-38,-26),(5,0,0)),
  ((-92,28,38),(-92,-28,-38),(1,0,0)),
  ((-105,-16,52),(-105,16,-52),(-6,0,0)),
  ((-88,-30,68),(-88,30,-68),(-3,0,0)),
  ((-61,-16,40),(-61,16,-40),(-1,0,0)),
  ((-24,-4,16),(-24,4,-16),(0,0,0))]),
 'ground_slam': (22, [
  ((-45,-18,28),(-36,20,-26),(-2,-6,0)),
  ((-112,-24,36),(-98,28,-38),(-6,-12,2)),
  ((-158,-16,26),(-148,22,-32),(-12,-8,1)),
  ((-20,18,-28),(-28,-14,-30),(26,14,-3)),
  ((-36,24,-38),(-42,-20,-36),(17,20,-2)),
  ((-66,-6,20),(-54,12,-26),(6,-3,1)),
  ((-28,2,8),(-22,4,-12),(1,1,0))]),
 'overhead_cast': (30, [
  ((-42,30,20),(-42,-30,-20),(3,0,0)),
  ((-96,34,38),(-92,-34,-38),(-3,-2,0)),
  ((-152,12,30),(-152,-12,-30),(-12,0,0)),
  ((-144,-18,64),(-144,18,-64),(-8,0,0)),
  ((-92,-46,94),(-92,46,-94),(6,0,0)),
  ((-76,-28,58),(-68,28,-62),(2,4,-1)),
  ((-32,-8,22),(-30,8,-24),(0,1,0))]),
 'sword_release': (16, [
  ((-42,-32,44),(-26,18,-20),(3,-10,2)),
  ((-94,-48,78),(-54,28,-32),(-4,-24,4)),
  ((-142,-28,54),(-72,20,-38),(-9,-16,2)),
  ((-78,30,-58),(-42,-20,-34),(8,18,-3)),
  ((-55,48,-96),(-30,-28,-24),(5,27,-4)),
  ((-82,14,-30),(-36,4,-22),(0,8,-1)),
  ((-30,2,-6),(-16,2,-10),(0,1,0))]),
 'lightning_channel': (20, [
  ((-48,-24,26),(-30,18,-16),(-2,-6,1)),
  ((-104,-36,54),(-62,32,-38),(-6,-16,3)),
  ((-145,-16,38),(-108,-28,-58),(-10,-8,-2)),
  ((-88,14,-12),(-118,26,-46),(8,8,1)),
  ((-118,-18,34),(-78,12,-36),(-4,-10,2)),
  ((-80,24,-28),(-56,-18,-26),(4,12,-1)),
  ((-30,-4,12),(-20,4,-10),(0,-1,0))]),
 'water_weave': (22, [
  ((-34,18,32),(-48,-22,-34),(4,5,1)),
  ((-78,38,48),(-100,-16,-30),(1,12,2)),
  ((-130,12,44),(-74,-42,-70),(-6,-8,-2)),
  ((-90,-34,72),(-126,6,-40),(-3,-16,-1)),
  ((-78,-20,46),(-98,30,-64),(5,8,2)),
  ((-60,22,28),(-56,-14,-34),(2,6,1)),
  ((-24,8,12),(-26,-6,-14),(0,2,0))]),
 'earth_raise': (20, [
  ((-22,14,34),(-22,-14,-34),(9,0,0)),
  ((-52,20,48),(-52,-20,-48),(12,0,0)),
  ((-98,16,42),(-98,-16,-42),(2,0,0)),
  ((-140,-8,32),(-140,8,-32),(-9,0,0)),
  ((-106,28,54),(-106,-28,-54),(3,0,0)),
  ((-62,18,34),(-62,-18,-34),(1,0,0)),
  ((-26,6,14),(-26,-6,-14),(0,0,0))]),
 'shadow_summon': (26, [
  ((-36,30,32),(-44,-28,-32),(6,-4,1)),
  ((-82,48,54),(-78,-46,-52),(8,-9,2)),
  ((-126,34,66),(-114,-38,-60),(-5,-12,3)),
  ((-104,-36,84),(-132,18,-52),(-7,14,-3)),
  ((-88,24,42),(-96,-30,-68),(4,8,-2)),
  ((-68,32,30),(-62,-24,-38),(2,-6,1)),
  ((-28,8,14),(-30,-8,-16),(0,-1,0))]),
}

# Three or more bespoke gestures replace shared anchors. Every rhythm is unique,
# including against the existing 32 clips (which have fewer keyframes).
# id: duration, style, seven absolute ticks, description, pose replacements.
CLIPS = {
 'static_spark': (12,'lightning_channel',[1,2,3,4,6,8,10],
  'Pinch a charge beside the shoulder, snap the wrist, then discharge a second tiny recoil.', {
   1:((-102,-30,42),(-28,12,-18),(-4,-12,2)),
   2:((-78,18,-20),(-40,-8,-22),(6,11,-2)),
   4:((-94,-12,26),(-34,6,-16),(-2,-6,1))}),
 'chain_lightning': (20,'lightning_channel',[1,3,6,8,11,14,17],
  'Lift the conductor hand and relay three alternating arcs between the open palms.', {
   2:((-132,-34,58),(-78,-18,-44),(-6,-18,2)),
   3:((-74,28,-32),(-136,12,-32),(5,16,-2)),
   4:((-124,-14,44),(-68,-32,-66),(-3,-13,3)),
   5:((-72,38,-42),(-104,14,-44),(3,20,-2))}),
 'thunder_step': (12,'lightning_channel',[1,2,4,5,7,9,11],
  'Cross a charged guard, tuck both elbows through the step and flare an electric exit.', {
   0:((-66,34,40),(-68,-32,-38),(5,-8,2)),
   2:((-46,-34,36),(-58,28,-40),(20,-16,2)),
   3:((-96,24,-48),(-92,-18,-62),(13,17,-3)),
   4:((-70,-8,32),(-64,12,-30),(6,-4,0))}),
 'heavens_wrath': (30,'overhead_cast',[2,5,9,13,18,23,27],
  'Stack stormclouds overhead, split their crown and conduct descending thunder with both hands.', {
   1:((-120,-18,46),(-88,-38,-52),(-5,-14,2)),
   3:((-158,-8,30),(-132,28,-70),(-11,8,-3)),
   4:((-46,38,-42),(-110,-26,-88),(16,23,-4)),
   5:((-108,-24,62),(-52,30,-56),(6,-16,2))}),
 'water_bolt': (14,'dual_cast',[1,2,4,6,8,10,12],
  'Cup a droplet above the support palm, corkscrew it forward and collect the spent stream.', {
   1:((-78,36,28),(-68,-22,-40),(4,9,1)),
   3:((-100,4,-8),(-62,16,-36),(8,-6,-1)),
   4:((-90,18,-22),(-50,-18,-42),(4,9,-2))}),
 'tidal_wave': (22,'water_weave',[2,4,7,10,13,17,20],
  'Scoop a low tide, roll the crest between the hands and push the whole wave outward.', {
   0:((-20,16,50),(-28,-18,-50),(11,-6,1)),
   2:((-140,24,54),(-122,-12,-52),(-8,11,-2)),
   3:((-88,-40,88),(-86,40,-88),(10,-8,0)),
   4:((-70,-30,72),(-78,28,-78),(6,4,1))}),
 'aqua_blade': (16,'sword_release',[1,3,5,7,9,12,14],
  'Weave water beneath the hilt, carve a flowing rising edge and roll its crescent away.', {
   0:((-38,-32,60),(-82,30,-32),(5,-12,2)),
   2:((-116,-42,80),(-96,-18,-50),(-3,-22,3)),
   3:((-148,26,-56),(-70,32,-74),(-7,15,-4)),
   5:((-84,32,-52),(-60,-10,-38),(1,16,-2))}),
 'ocean_prison': (26,'water_weave',[2,5,8,12,16,20,23],
  'Raise offset spirals into a high water cage, then close its gate with crossed palms.', {
   1:((-96,-28,50),(-68,36,-62),(5,-9,2)),
   2:((-154,-14,42),(-130,20,-54),(-10,-4,0)),
   3:((-130,28,58),(-150,-16,-38),(-6,10,-2)),
   4:((-96,46,48),(-104,-44,-50),(4,4,0))}),
 'stone_spike': (14,'earth_raise',[1,3,4,6,9,11,13],
  'Find the earth with a low palm and lever a narrow stone point up from beneath the target.', {
   0:((-18,-12,24),(-34,16,-38),(12,-6,1)),
   2:((-112,-18,30),(-62,24,-44),(-2,-10,2)),
   3:((-148,4,18),(-78,-12,-52),(-8,5,-1))}),
 'earthen_wall': (20,'earth_raise',[2,4,6,9,12,15,18],
  'Set two foundations, lift a heavy slab and brace the barrier with square open palms.', {
   1:((-62,-22,60),(-62,22,-60),(14,0,0)),
   3:((-128,-28,52),(-128,28,-52),(-5,0,0)),
   4:((-98,-18,68),(-102,20,-68),(7,-2,0))}),
 'quake_blade': (22,'ground_slam',[1,4,7,9,12,16,20],
  'Take a weighted crown guard, drive the sword into the low line and absorb two ground pulses.', {
   1:((-126,-20,34),(-114,30,-40),(-8,-12,2)),
   3:((-16,24,-36),(-32,-18,-34),(28,18,-4)),
   5:((-50,20,-30),(-48,-12,-26),(13,14,-2))}),
 'worldbreaker': (30,'ground_slam',[3,6,10,14,18,23,28],
  'Draw the mountain overhead, suspend its weight, then shatter the seal with a monumental double slam.', {
   0:((-30,-24,50),(-30,24,-50),(13,0,0)),
   2:((-160,-24,42),(-154,26,-46),(-14,-12,2)),
   3:((-12,32,-46),(-18,-28,-48),(28,24,-4)),
   4:((-78,-26,56),(-72,24,-58),(-4,-14,2)),
   5:((-30,16,-28),(-38,-12,-30),(22,12,-2))}),
 'shadow_bolt': (14,'shadow_summon',[1,2,5,7,9,11,13],
  'Hook a shadow from the opposite palm, twist it into a dart and cast it from a half-open guard.', {
   1:((-90,46,34),(-62,-26,-44),(6,-12,2)),
   3:((-98,6,-16),(-84,32,-52),(9,14,-2)),
   4:((-80,24,-38),(-64,-22,-48),(3,18,-1))}),
 'curse_mark': (18,'shadow_summon',[1,3,5,8,11,14,16],
  'Trace a hooked sigil, seal its center with two fingers and pull the curse thread taut.', {
   0:((-58,-18,34),(-32,28,-26),(2,-8,1)),
   2:((-116,16,58),(-68,-36,-58),(-4,8,-2)),
   3:((-94,4,10),(-108,-26,-42),(7,-6,1)),
   5:((-72,40,38),(-54,-12,-30),(2,12,0))}),
 'shadow_edge': (16,'sword_release',[1,2,5,7,10,12,15],
  'Draw shadow along the blade, cut a low hooked arc and fold the trailing darkness inward.', {
   0:((-58,-36,46),(-94,36,-32),(4,-14,2)),
   2:((-130,-46,72),(-110,-24,-54),(-8,-24,3)),
   3:((-42,40,-84),(-68,28,-58),(13,24,-4)),
   5:((-98,32,-46),(-76,-32,-40),(2,12,-1))}),
 'eclipse_nova': (30,'shadow_summon',[2,6,10,15,19,24,28],
  'Cup a black sun, eclipse it behind crossed arms, then tear the horizon into an open nova.', {
   1:((-100,46,52),(-100,-46,-52),(9,0,0)),
   2:((-152,34,68),(-148,-32,-68),(-12,0,0)),
   3:((-92,-54,102),(-92,54,-102),(-7,0,0)),
   4:((-118,-24,72),(-126,18,-64),(3,-8,2)),
   5:((-78,38,52),(-82,-36,-54),(4,3,0))}),
 'astral_crescent': (16,'sword_release',[2,3,5,8,10,12,15],
  'Draw a low celestial edge, sweep a clean rising moon and stop with the point held high.', {
   0:((-32,-38,72),(-36,22,-28),(6,-14,3)),
   2:((-78,-50,92),(-56,28,-40),(3,-25,4)),
   3:((-154,24,-44),(-76,-14,-42),(-11,16,-3)),
   4:((-138,42,-72),(-60,-22,-32),(-7,25,-4))}),
 'falling_star': (22,'ground_slam',[2,5,8,11,14,17,20],
  'Set the star above the crown, draw it downward through the blade and catch its falling weight.', {
   0:((-76,-16,26),(-52,18,-26),(-4,-7,0)),
   2:((-158,-10,22),(-124,28,-38),(-14,-10,2)),
   3:((-26,16,-44),(-48,-18,-38),(25,16,-3)),
   4:((-46,34,-62),(-42,-20,-30),(18,23,-2))}),
 'phantom_blades': (20,'sword_release',[1,4,6,9,11,14,18],
  'Mark three floating sword lanes with alternating cuts and gather their phantom edges into guard.', {
   1:((-118,-36,64),(-80,-28,-56),(-6,-20,3)),
   2:((-60,38,-82),(-110,24,-42),(7,22,-3)),
   3:((-132,26,-50),(-64,-34,-72),(-5,14,-2)),
   4:((-52,-42,88),(-100,-20,-54),(8,-24,4)),
   5:((-94,-24,36),(-90,26,-38),(0,-10,1))}),
 'celestial_rush': (14,'sword_release',[1,3,5,6,8,10,12],
  'Tuck a star-point guard, drive the shoulders through a straight rush and flare the trailing blade.', {
   0:((-48,-38,32),(-44,24,-34),(9,-12,1)),
   2:((-64,-46,26),(-62,30,-42),(22,-20,2)),
   3:((-96,4,-8),(-26,-24,-42),(19,14,-2)),
   4:((-72,26,-60),(-36,-18,-32),(10,20,-3))}),
 'sovereign_judgment': (28,'overhead_cast',[2,5,9,12,17,22,26],
  'Raise the sovereign blade like a scepter, hold its verdict aloft and cast a single commanding stroke.', {
   1:((-126,-18,24),(-66,32,-42),(-6,-8,1)),
   2:((-160,-8,14),(-112,20,-36),(-13,-4,0)),
   3:((-142,12,-18),(-118,-24,-62),(-8,10,-2)),
   4:((-44,24,-66),(-58,-28,-44),(22,20,-3))}),
 'heavenly_sword_domain': (32,'overhead_cast',[3,7,11,16,21,26,30],
  'Draw an oath across the heart, crown the domain overhead and unfold a cathedral of celestial swords.', {
   0:((-78,36,38),(-74,-34,-38),(4,0,0)),
   1:((-118,42,56),(-112,-40,-54),(-4,-6,1)),
   2:((-160,8,32),(-156,-8,-36),(-14,0,0)),
   3:((-146,-30,82),(-146,30,-82),(-9,0,0)),
   4:((-74,-60,106),(-74,60,-106),(7,0,0)),
   5:((-106,-22,62),(-100,22,-66),(-2,0,0))}),
}


def frame(tick, pose=ZERO):
    value = {'tick': tick, 'easing': 'EASEINOUTSINE'}
    for bone, angles in zip(('rightArm', 'leftArm', 'torso'), pose):
        value[bone] = dict(zip(('pitch', 'yaw', 'roll'), angles))
    return value


def save_clip(name, duration, ticks, poses, description):
    data = {'version': 3, 'name': name, 'author': 'Star Swordmaster',
            'description': 'Original expansion choreography. ' + description,
            'emote': {'isLoop': False, 'degrees': True, 'beginTick': 0,
                      'returnTick': 0, 'endTick': ticks[-1], 'stopTick': duration,
                      'moves': [frame(0)] + [frame(t, p) for t, p in zip(ticks, poses)] + [frame(duration)]}}
    write_json(ROOT / 'player_animations' / (name + '.json'), data)


def write_json(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2) + '\n', encoding='utf-8')


def animations():
    for name, (duration, poses) in STYLES.items():
        ticks = [max(1, round(duration * p)) for p in (.08, .22, .37, .50, .65, .79, .91)]
        save_clip('style_' + name, duration, ticks, poses, 'Reusable ' + name.replace('_', ' ') + ' anchors.')
    for name, (duration, style, ticks, description, overrides) in CLIPS.items():
        poses = list(STYLES[style][1])
        for index, pose in overrides.items():
            poses[index] = pose
        save_clip('skill_' + name, duration, ticks, poses, description + ' Builds on ' + style + '.')


# Hard-edged, locally authored pixel symbols. Light and dark facets create depth
# at native resolution; no resampling or soft glow is baked into the artwork.
PALETTES = {
 'lightning': ((90,110,250), (92,213,255), (231,251,255)),
 'water': ((26,104,166), (41,211,225), (200,255,255)),
 'earth': ((118,74,40), (207,156,65), (255,232,153)),
 'dark': ((60,33,98), (157,75,207), (240,157,225)),
 'sword': ((149,100,40), (242,196,79), (251,247,218)),
}


def star(d, x, y, color, r=3):
    d.line((x-r,y,x+r,y), fill=color)
    d.line((x,y-r,x,y+r), fill=color)
    d.point((x-1,y-1), fill=color); d.point((x+1,y+1), fill=color)


def bolt(d, x, y, dark, mid, light, scale=1):
    points = [(0,0),(-5,10),(0,10),(-3,20),(7,6),(1,6),(4,0)]
    d.polygon([(x+int(a*scale),y+int(b*scale)) for a,b in points], fill=mid)
    d.line([(x+int(a*scale),y+int(b*scale)) for a,b in [(0,2),(-3,8),(2,8),(-1,15)]],fill=light)


def droplet(d, x, y, mid, light, size=8):
    d.polygon([(x,y-size),(x+size//2,y-1),(x+size//2,y+3),(x+2,y+5),
               (x-2,y+5),(x-size//2,y+3),(x-size//2,y-1)], fill=mid)
    d.line((x-1,y-size+4,x-3,y+1,x-2,y+3), fill=light)


def sword(d, x=0, y=0, mid=(242,196,79), light=(251,247,218)):
    d.polygon([(8+x,23+y),(23+x,5+y),(26+x,4+y),(26+x,8+y),(12+x,26+y)],fill=light)
    d.line((11+x,23+y,24+x,7+y),fill=(141,174,192))
    d.line((6+x,19+y,15+x,27+y),fill=mid,width=3)
    d.line((9+x,25+y,5+x,29+y),fill=(152,101,52),width=3)
    d.point((5+x,29+y),fill=mid)


def wave(d, y, mid, light, offset=0):
    pts=[(4,y+3),(8,y+2),(11,y-3),(15,y-6),(20,y-6),(23,y-3),
         (20,y-2),(18,y-4),(15,y-2),(15,y+1),(20,y+3),(27,y+1),
         (27,y+5),(19,y+7),(11,y+5),(4,y+6)]
    d.polygon([(x+offset,a) for x,a in pts],fill=mid)
    d.line([(5+offset,y+3),(10+offset,y+2),(13+offset,y-2),(17+offset,y-4),(20+offset,y-3)],fill=light)


def rock(d, x, y, mid, light, r=5):
    d.polygon([(x-r,y+3),(x-r+1,y-3),(x,y-r),(x+r,y-2),(x+r-1,y+4),(x,y+r)],fill=mid)
    d.polygon([(x-r+1,y-3),(x,y-r),(x+1,y),(x-r,y+3)],fill=light)
    d.line((x+1,y,x+r,y-2),fill=(80,61,46))


def ring(d, box, mid, light, width=1):
    d.ellipse(box,outline=mid,width=width)
    d.arc(box,195,285,fill=light,width=width)


def icons():
    for index, name in enumerate([*CLIPS, 'aura_sense']):
        group = 'lightning' if index<4 else 'water' if index<8 else 'earth' if index<12 else 'dark' if index<16 else 'sword'
        dark, mid, light = PALETTES[group]
        im=Image.new('RGBA',(32,32),(9,14,25,255)); d=ImageDraw.Draw(im)
        d.rectangle((1,1,30,30),outline=(42,54,70));d.line((2,2,29,2),fill=dark)
        d.rectangle((3,3,28,28),fill=tuple(max(12,c//7) for c in dark))
        ultimate=name in ('heavens_wrath','ocean_prison','worldbreaker','eclipse_nova','sovereign_judgment','heavenly_sword_domain')
        if ultimate:
            for x,y,sx,sy in [(2,2,1,1),(29,2,-1,1),(2,29,1,-1),(29,29,-1,-1)]:
                d.line((x+sx*5,y,x,y,x,y+sy*5),fill=mid)
                d.point((x+sx*2,y+sy*2),fill=light)
        if name=='static_spark':
            bolt(d,15,5,dark,mid,light);star(d,7,11,light,2);d.line((23,20,26,23,23,26),fill=dark)
        elif name=='chain_lightning':
            for x,y in [(8,9),(24,8),(18,24)]:ring(d,(x-3,y-3,x+3,y+3),mid,light)
            d.line((10,10,15,7,16,14,22,10,20,18,15,19,18,22),fill=light,width=2)
        elif name=='thunder_step':
            d.polygon([(15,9),(21,10),(19,19),(25,22),(24,26),(13,26),(11,23),(13,16),(10,15)],fill=mid)
            d.line((16,11,15,21,22,23),fill=light,width=2)
            for y in (9,15,21):d.line((4,y,10,y-1),fill=dark if y==15 else mid)
            bolt(d,24,4,dark,mid,light,.45)
        elif name=='heavens_wrath':
            d.polygon([(5,10),(8,6),(13,7),(16,4),(21,6),(23,9),(27,10),(26,13),(5,13)],fill=dark)
            d.line((7,9,10,8,13,9,17,6,21,9,24,10),fill=mid)
            for x,y,s in [(9,13,.55),(17,11,.7),(24,14,.5)]:bolt(d,x,y,dark,mid,light,s)
            d.line((5,27,27,27),fill=mid);star(d,16,3,light,2)
        elif name=='water_bolt':
            droplet(d,17,16,mid,light,10)
            d.line((6,22,9,20),fill=dark,width=2);d.line((7,27,12,24),fill=mid)
            d.point((25,9),fill=light)
        elif name=='tidal_wave':
            wave(d,19,mid,light);d.line((5,27,11,26,18,28,25,26),fill=dark,width=2)
            droplet(d,8,8,mid,light,3);droplet(d,25,7,mid,light,3)
        elif name=='aqua_blade':
            sword(d,mid=mid,light=light);d.arc((3,7,29,27),15,205,fill=mid,width=3)
            d.arc((5,9,27,25),15,155,fill=light);droplet(d,8,7,mid,light,3)
        elif name=='ocean_prison':
            ring(d,(6,5,25,28),mid,light,2);ring(d,(4,10,27,23),dark,mid)
            d.line((11,8,9,16,12,25),fill=light);d.line((21,8,23,16,20,25),fill=mid)
            droplet(d,16,17,mid,light,5);star(d,16,3,light,2)
        elif name=='stone_spike':
            d.polygon([(7,26),(13,8),(17,5),(24,26)],fill=mid)
            d.polygon([(7,26),(13,8),(17,5),(15,22)],fill=light)
            d.polygon([(15,22),(17,5),(20,18),(24,26)],fill=dark)
            d.line((4,27,11,28,16,26,23,28,28,26),fill=mid)
        elif name=='earthen_wall':
            for x,y in [(5,11),(14,10),(23,12),(8,20),(19,20)]:
                d.rectangle((x,y,x+6,y+6),fill=mid);d.line((x,y+5,x,y,x+5,y),fill=light)
            d.line((5,9,16,5,27,9),fill=dark,width=2);d.point((16,3),fill=light)
        elif name=='quake_blade':
            sword(d,-1,-2,mid,light)
            d.line((4,27,11,23,14,27,19,24,26,27),fill=mid,width=2)
            d.line((16,26,14,29),fill=light);rock(d,6,14,dark,mid,3)
        elif name=='worldbreaker':
            d.polygon([(4,25),(7,17),(12,21),(15,8),(18,20),(24,14),(28,25)],fill=mid)
            d.polygon([(11,25),(15,8),(16,23),(23,18),(20,26)],fill=light)
            for x,y,r in [(7,9,3),(24,7,3),(25,26,2)]:rock(d,x,y,mid,light,r)
            d.line((5,28,13,26,16,29,22,27,27,28),fill=dark)
            star(d,16,4,light,2)
        elif name=='shadow_bolt':
            d.polygon([(5,25),(9,17),(6,16),(16,7),(25,6),(26,15),(18,24),(17,21)],fill=dark)
            d.polygon([(8,23),(15,13),(24,8),(22,17),(16,21),(17,17)],fill=mid)
            d.line((14,17,21,10),fill=light,width=2);d.point((6,28),fill=mid)
        elif name=='curse_mark':
            d.line((7,7,16,11,25,7,21,18,16,26,11,18,7,7),fill=mid,width=2)
            d.line((8,16,24,16),fill=dark,width=3);d.polygon([(12,16),(16,12),(20,16),(16,20)],fill=light)
            d.point((16,5),fill=(220,68,112));d.line((16,23,16,28),fill=(220,68,112))
        elif name=='shadow_edge':
            sword(d,mid=mid,light=light)
            d.line((5,16,8,11,14,10,14,5,20,5),fill=mid,width=2)
            d.line((18,27,23,24,24,18,28,16),fill=dark,width=3)
            d.point((7,8),fill=(219,70,114))
        elif name=='eclipse_nova':
            ring(d,(5,5,27,27),mid,light,2);d.ellipse((9,9,23,23),fill=(10,6,19))
            d.arc((7,7,25,25),210,325,fill=(232,80,136),width=2)
            for x,y in [(16,3),(28,16),(16,28),(3,16)]:star(d,x,y,light,2)
            d.line((5,25,8,22),fill=mid);d.line((24,8,27,5),fill=mid)
        elif name=='astral_crescent':
            d.arc((3,3,28,28),270,85,fill=mid,width=5)
            d.arc((5,5,26,26),280,65,fill=light,width=2)
            sword(d,-3,1,mid,light);star(d,9,7,light,3)
        elif name=='falling_star':
            d.line((9,4,16,18),fill=dark,width=3);d.line((17,3,20,14),fill=mid,width=2)
            d.polygon([(19,11),(21,18),(28,20),(21,22),(19,28),(16,22),(10,20),(16,18)],fill=mid)
            star(d,19,20,light,4);d.line((5,12,10,20),fill=light)
        elif name=='phantom_blades':
            for x,y,c in [(1,-4,dark),(-7,1,mid),(-1,1,light)]:
                d.polygon([(10+x,23+y),(21+x,8+y),(24+x,6+y),(24+x,10+y),(13+x,25+y)],fill=c)
                d.line((8+x,21+y,14+x,26+y),fill=mid,width=2)
            d.arc((3,10,28,29),20,160,fill=dark);star(d,7,6,light,2)
        elif name=='celestial_rush':
            sword(d,mid=mid,light=light)
            for x,y,w in [(3,10,10),(3,16,7),(4,22,4)]:d.line((x,y,x+w,y-2),fill=mid)
            star(d,26,5,light,3);d.line((18,28,28,16),fill=dark)
        elif name=='sovereign_judgment':
            d.polygon([(13,8),(16,5),(19,8),(18,22),(16,25),(14,22)],fill=light)
            d.line((16,8,16,21),fill=mid)
            d.line((10,22,22,22),fill=mid,width=3);d.rectangle((15,24,17,28),fill=dark)
            d.polygon([(7,7),(7,3),(12,6),(16,2),(20,6),(25,3),(25,7)],fill=mid)
            d.line((4,12,4,19,9,25),fill=dark);d.line((28,12,28,19,23,25),fill=dark)
        elif name=='heavenly_sword_domain':
            ring(d,(4,4,28,28),mid,light);d.line((5,18,27,18),fill=dark)
            for x,y in [(8,12),(16,6),(24,12)]:
                d.polygon([(x,y),(x+2,y+3),(x+1,y+12),(x-1,y+12),(x-2,y+3)],fill=light)
                d.line((x-3,y+9,x+3,y+9),fill=mid);d.line((x,y+11,x,y+15),fill=mid)
            d.polygon([(9,7),(9,4),(13,6),(16,2),(19,6),(23,4),(23,7)],fill=mid)
            star(d,5,26,light,2);star(d,27,26,light,2)
        else:  # aura_sense: an open eye perceiving a small, neutral golden blade.
            d.polygon([(4,15),(10,10),(16,8),(23,11),(28,15),(22,20),(16,22),(9,19)],fill=dark)
            d.line((5,15,11,11,16,10,22,12,27,15,22,18,16,20,10,18,5,15),fill=mid)
            d.polygon([(16,11),(19,15),(16,19),(13,15)],fill=light)
            d.line((16,12,16,18),fill=mid);star(d,24,5,light,2)
        target=ROOT/'textures/skills'/f'{name}.png';target.parent.mkdir(parents=True,exist_ok=True);im.save(target)


def particles():
    white=(255,255,255,255);mid=(185,185,185,255);shade=(118,118,118,255)
    for name in ('bolt','droplet','rock','shadow','rune','blade'):
        size=32 if name in ('rune','blade') else 16
        im=Image.new('RGBA',(size,size),(0,0,0,0));d=ImageDraw.Draw(im)
        if name=='bolt':
            d.polygon([(8,1),(3,8),(7,8),(5,14),(13,5),(9,5),(11,1)],fill=mid)
            d.line((8,2,5,7,9,7,7,11),fill=white)
        elif name=='droplet':
            d.polygon([(8,1),(12,7),(13,10),(11,13),(8,14),(4,12),(3,9),(5,6)],fill=mid)
            d.line((7,5,5,8,5,10,7,12),fill=white)
            d.line((11,9,10,12,8,13),fill=shade)
        elif name=='rock':
            d.polygon([(3,5),(8,2),(12,4),(14,9),(11,13),(5,14),(2,10)],fill=shade)
            d.polygon([(3,5),(8,2),(8,7),(2,10)],fill=white)
            d.polygon([(8,7),(12,4),(14,9),(11,13),(6,11)],fill=mid)
        elif name=='shadow':
            d.polygon([(4,3),(8,2),(10,5),(9,8),(12,7),(14,10),(11,13),(6,14),(3,11),(5,8),(2,7)],fill=shade)
            d.polygon([(5,4),(8,3),(8,6),(6,9),(9,11),(12,10),(10,13),(6,12),(4,10),(6,7)],fill=mid)
            d.line((6,5,6,6,5,7),fill=white);d.point((9,12),fill=white)
        elif name=='rune':
            d.line((16,3,24,11,22,20,16,28,8,20,10,11,16,3),fill=mid,width=2)
            d.line((9,11,22,20),fill=white)
            d.line((23,11,10,20),fill=shade)
            d.line((5,7,5,11,8,11),fill=white)
            d.line((27,21,27,25,24,25),fill=white)
        else:
            d.polygon([(12,21),(24,5),(27,3),(27,7),(15,24)],fill=mid)
            d.line((14,21,25,6),fill=white,width=2)
            d.line((8,18,18,27),fill=shade,width=2)
            d.line((12,24,7,29),fill=mid,width=2)
        target=ROOT/'textures/particle'/f'{name}.png';target.parent.mkdir(parents=True,exist_ok=True);im.save(target)
        write_json(target.with_name(target.name+'.mcmeta'),{'texture':{'blur':False,'clamp':False}})
        write_json(ROOT/'particles'/f'{name}.json',{'textures':['starswordmaster:'+name]})


def main():
    animations(); icons(); particles()
    print('Generated expansion only: 22 skill clips, 8 reusable styles, 23 skill icons, 6 particle silhouettes')


if __name__=='__main__':
    main()

"""Generate six original, unfiltered pixel silhouettes for ability presentation.

All artwork is constructed here from basic geometry, with no external images.
White / gray texels retain the ability palette when tinted by the native particle.
Run from any directory; writes only this particle family's PNG and JSON files.
"""
from pathlib import Path
import json
import math
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/starswordmaster'
TEXTURES = ROOT / 'textures/particle'
DEFINITIONS = ROOT / 'particles'
TEXTURES.mkdir(parents=True, exist_ok=True)
DEFINITIONS.mkdir(parents=True, exist_ok=True)
WHITE = (255, 255, 255, 255)
MID = (185, 185, 185, 255)
SHADE = (118, 118, 118, 255)


def save(name, image):
    image.save(TEXTURES / f'{name}.png')
    (DEFINITIONS / f'{name}.json').write_text(
        json.dumps({'textures': [f'starswordmaster:{name}']}, indent=2) + '\n')
    # Explicit metadata documents the intended hard pixels without global atlas changes.
    (TEXTURES / f'{name}.png.mcmeta').write_text(
        json.dumps({'texture': {'blur': False, 'clamp': False}}, indent=2) + '\n')


def canvas(size=16):
    im = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    return im, ImageDraw.Draw(im)


im, d = canvas()
d.polygon([(1, 8), (6, 6), (13, 6), (14, 7), (10, 9), (4, 9)], fill=MID)
d.line([(3, 8), (8, 7), (13, 7)], fill=WHITE, width=1)
save('streak', im)

# A tapered crescent: the changing inner radius leaves pointed ends.
im, d = canvas(32)
for y in range(1, 31):
    for x in range(1, 31):
        dx, dy = x - 14.5, y - 15.5
        r, a = math.hypot(dx, dy), math.atan2(dy, dx)
        half = 1.4 + 1.7 * max(0, math.cos(a / 1.2))
        if abs(a) < 2.05 and abs(r - 12) <= half * (1 - abs(a) / 2.15):
            im.putpixel((x, y), WHITE if r >= 11.7 else MID)
save('arc', im)

# Open center, four broken ring gates, and detached asymmetric rune marks.
im, d = canvas(32)
for y in range(2, 30):
    for x in range(2, 30):
        dx, dy = x - 15.5, y - 15.5
        r = math.hypot(dx, dy)
        if 10.4 <= r <= 11.7 and abs(dx) > 1.5 and abs(dy) > 1.5:
            im.putpixel((x, y), MID)
d.line([(14, 3), (14, 6), (17, 6), (17, 4)], fill=WHITE)
d.line([(25, 14), (28, 14), (28, 17), (26, 17)], fill=WHITE)
d.line([(14, 25), (17, 25), (17, 28)], fill=WHITE)
d.line([(3, 14), (6, 14), (6, 17), (4, 17)], fill=WHITE)
d.point((8, 8), fill=WHITE)
d.point((23, 23), fill=WHITE)
save('sigil', im)

im, d = canvas()
d.polygon([(8, 1), (12, 6), (10, 12), (6, 14), (4, 8)], fill=SHADE)
d.polygon([(8, 1), (9, 7), (6, 14), (4, 8)], fill=WHITE)
d.polygon([(9, 7), (12, 6), (10, 12), (6, 14)], fill=MID)
save('shard', im)

im, d = canvas()
d.polygon([(8, 1), (9, 6), (14, 8), (9, 9), (7, 14), (6, 9), (1, 7), (6, 6)], fill=MID)
d.polygon([(7, 4), (9, 7), (11, 8), (8, 10), (6, 8)], fill=WHITE)
d.point((3, 3), fill=WHITE)
d.point((12, 12), fill=WHITE)
save('flash', im)

im, d = canvas()
d.polygon([(6, 2), (9, 5), (9, 7), (12, 5), (12, 10), (10, 13), (5, 14), (3, 11), (4, 7), (6, 9)], fill=SHADE)
d.polygon([(6, 5), (9, 8), (10, 7), (10, 11), (8, 13), (5, 12), (5, 9), (7, 10)], fill=MID)
d.polygon([(7, 9), (9, 11), (8, 13), (6, 12)], fill=WHITE)
save('ember', im)

"""Original transparent pixel sigils. Rebuild with Pillow; no downloaded/source artwork."""
from pathlib import Path
from math import sin, cos, pi
from PIL import Image, ImageDraw

SIZE = 128
PALETTES = [('#ba3b29', '#f6b65e'), ('#579cac', '#cceaf1'), ('#7877c9', '#c4cfff'),
            ('#318f9e', '#a0e0da'), ('#a27e4b', '#ddc48a'), ('#633654', '#bc6c91'),
            ('#d4c78a', '#4d9b7c'), ('#a39468', '#f5e7b8')]
image = Image.new('RGBA', (SIZE * 8, SIZE * 3))

def polar(radius, angle):
    return (round(64 + cos(angle) * radius), round(64 + sin(angle) * radius))

def path(draw, points, color, width=2, close=False):
    draw.line(points + ([points[0]] if close else []), fill=color, width=width)

def polygon(draw, radius, sides, color, offset=0):
    path(draw, [polar(radius, offset + i * 2 * pi / sides) for i in range(sides)], color, close=True)

for element, (shade, glow) in enumerate(PALETTES):
    for layer in range(3):
        cell = Image.new('RGBA', (SIZE, SIZE))
        draw = ImageDraw.Draw(cell)
        if layer == 1:
            # Broken outer bands and individually constructed, original geometric runes.
            for i in range(12):
                start = i * 30 + element * 3
                draw.arc((8, 8, 120, 120), start + 3, start + 25, fill=shade, width=2)
                a = i * pi / 6
                glyph = [polar(47, a - .047), polar(51, a + .034), polar(43, a + .06)]
                path(draw, glyph, glow)
                if (i + element) % 2 == 0:
                    path(draw, [polar(45, a - .07), polar(48, a + .07)], shade, 1)
            polygon(draw, 39, 12 if element != 4 else 4, shade, pi / 4)
        elif layer == 2:
            polygon(draw, 27, 8 if element in (5, 7) else 6, shade, pi / 8)
            for i in range(8):
                a = pi * i / 4
                path(draw, [polar(32, a), polar(36, a), polar(34, a + .07)], glow, 1)
            draw.ellipse((59, 59, 69, 69), outline=glow, width=2)
        else:
            draw.ellipse((18, 18, 110, 110), outline=shade, width=1)
            if element == 0:  # Ember crown: angular licks surrounding a red diamond.
                for i in range(8):
                    a = i * pi / 4
                    path(draw, [polar(27, a), polar(47, a + .09), polar(37, a + .20), polar(31, a + .36)], glow)
                polygon(draw, 21, 4, shade)
                polygon(draw, 12, 4, glow, pi / 4)
            elif element == 1:  # Six crystalline arms, each with branching ice needles.
                for i in range(6):
                    a = i * pi / 3
                    path(draw, [polar(7, a), polar(44, a)], glow)
                    for radius in (25, 35):
                        path(draw, [polar(radius + 3, a - .24), polar(radius - 5, a), polar(radius + 3, a + .24)], shade)
                polygon(draw, 16, 6, glow)
            elif element == 2:  # Six discontinuous, sharp electric forks.
                for i in range(6):
                    a = i * pi / 3
                    path(draw, [polar(43, a), polar(27, a + .15), polar(31, a - .17), polar(12, a + .08)], glow)
                polygon(draw, 10, 3, shade, pi / 2)
            elif element == 3:  # Three fluid spirals.
                for i in range(3):
                    a = i * pi * 2 / 3
                    path(draw, [polar(8 + n * .50, a + n * .052) for n in range(75)], glow)
                    path(draw, [polar(10 + n * .39, a + n * .052 + .15) for n in range(75)], shade, 1)
            elif element == 4:  # Fractured square stone seal.
                for radius in (24, 39, 47):
                    polygon(draw, radius, 4, glow if radius == 39 else shade, pi / 4)
                for i in range(4):
                    a = pi / 4 + i * pi / 2
                    path(draw, [polar(48, a), polar(34, a + .12), polar(29, a - .07), polar(18, a)], shade)
                polygon(draw, 11, 4, glow)
            elif element == 5:  # Open eclipse with forbidden radial needles.
                draw.arc((35, 35, 93, 93), 35, 320, fill=glow, width=3)
                draw.arc((47, 38, 89, 88), 70, 285, fill=shade, width=2)
                for i in range(7):
                    a = i * pi * 2 / 7
                    path(draw, [polar(34, a - .05), polar(48, a), polar(34, a + .05)], shade)
            elif element == 6:  # Restrained gold compass with emerald petals.
                polygon(draw, 38, 4, shade)
                for i in range(4):
                    a = i * pi / 2
                    path(draw, [polar(9, a), polar(25, a - .28), polar(34, a), polar(25, a + .28), polar(9, a)], glow)
                polygon(draw, 9, 4, shade, pi / 4)
            else:  # Celestial interlocked squares and orbiting stars.
                polygon(draw, 39, 4, glow, pi / 4)
                polygon(draw, 39, 4, shade)
                for i in range(8):
                    a = i * pi / 4
                    x, y = polar(46, a)
                    path(draw, [(x - 2, y), (x + 2, y)], glow, 1)
                    path(draw, [(x, y - 2), (x, y + 2)], glow, 1)
                polygon(draw, 13, 8, glow, pi / 8)
        image.paste(cell, (element * SIZE, layer * SIZE))

output = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/starswordmaster/textures/effect/magic_circles.png'
image.save(output)
print(output)

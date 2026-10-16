"""Original 32x32 rank and training pixel art; no resampling or external artwork."""
from pathlib import Path
from PIL import Image, ImageDraw
import math
import json

ROOT = Path(__file__).resolve().parents[1] / 'src/main/resources/assets/starswordmaster'
ICONS = ROOT / 'textures/skills'


def canvas(color):
    im = Image.new('RGBA', (32, 32), (6, 10, 22, 255))
    d = ImageDraw.Draw(im)
    d.rectangle((0, 0, 31, 31), outline=(44, 50, 75))
    for r in range(13, 2, -1):
        d.ellipse((16-r, 16-r, 16+r, 16+r), fill=tuple(int(v * (.08 + (13-r)*.022)) for v in color))
    return im, d


def star(d, x, y, color, r=2):
    d.line((x-r, y, x+r, y), fill=color)
    d.line((x, y-r, x, y+r), fill=color)
    d.point((x, y), fill=(255, 249, 226))


def blade(d, gold=False):
    edge = (255, 216, 93) if gold else (201, 141, 255)
    core = (255, 253, 211) if gold else (236, 219, 255)
    d.polygon(((12, 21), (22, 6), (26, 4), (25, 9), (16, 24)), fill=edge)
    d.line((15, 21, 24, 7), fill=core, width=2)
    d.line((10, 20, 19, 26), fill=(96, 48, 29), width=3)
    d.line((10, 19, 20, 25), fill=(255, 187, 55), width=2)
    d.line((13, 24, 9, 29), fill=(144, 95, 55), width=3)
    d.point((9, 28), fill=edge)


def save(name, im):
    im.save(ICONS / f'{name}.png')
    (ICONS / f'{name}.png.mcmeta').write_text('{"texture": {"blur": false, "clamp": false}}\n')


def ranks():
    im, d = canvas((255, 206, 64))
    d.arc((7, 5, 28, 27), 190, 340, fill=(141, 106, 47))
    for x, y, r in ((7, 6, 2), (4, 17, 2), (23, 25, 2), (28, 15, 1), (14, 3, 1), (5, 26, 1)):
        star(d, x, y, (255, 221, 94), r)
    blade(d, True)
    save('astral_sword_sovereign', im)

    im, d = canvas((176, 83, 255))
    d.ellipse((4, 4, 28, 28), outline=(99, 46, 161))
    d.ellipse((6, 6, 26, 26), outline=(171, 92, 255))
    d.polygon(((16, 5), (24, 13), (21, 24), (11, 24), (8, 13)), outline=(210, 147, 255))
    d.line(((16, 7), (16, 25)), fill=(245, 215, 255), width=2)
    d.line(((10, 12), (22, 18), (10, 22), (21, 11)), fill=(195, 115, 255), width=2)
    star(d, 16, 16, (255, 239, 255), 3)
    for x, y in ((4, 6), (28, 9), (3, 25), (27, 27)):
        star(d, x, y, (175, 134, 255), 1)
    save('celestial_archon', im)

    im, d = canvas((114, 52, 187))
    d.ellipse((3, 3, 29, 29), fill=(61, 25, 101), outline=(120, 64, 188))
    d.ellipse((5, 5, 27, 27), fill=(193, 105, 255))
    d.ellipse((8, 4, 28, 25), fill=(10, 8, 23), outline=(44, 23, 68))
    d.arc((4, 4, 28, 28), 55, 160, fill=(239, 198, 255), width=2)
    blade(d)
    for x, y in ((6, 8), (27, 20), (20, 29)):
        star(d, x, y, (173, 104, 245), 1)
    save('eclipse_sovereign', im)


TRAINING = {
    'strength_training': ('Klingetræning', 'Blade Training', 'Styrke', 'Strength', (241, 190, 80)),
    'agility_training': ('Flydetræning', 'Flow Drills', 'Smidighed', 'Agility', (98, 213, 237)),
    'endurance_training': ('Vogtertræning', 'Guardian Drills', 'Udholdenhed', 'Endurance', (106, 202, 141)),
    'spirit_training': ('Glødemeditation', 'Ember Meditation', 'Ånd', 'Spirit', (228, 133, 255)),
    'restorative_training': ('Livsmeditation', 'Life Meditation', 'Ånd', 'Spirit', (112, 239, 191)),
}


def training():
    for kind, (_, _, _, _, color) in TRAINING.items():
        for tier in range(1, 4):
            im, d = canvas(color)
            if kind == 'strength_training':
                d.polygon(((14, 19), (14, 7), (17, 3), (20, 7), (20, 19)), fill=color)
                d.line((17, 6, 17, 19), fill=(255, 250, 219))
                d.line((10, 20, 24, 20), fill=color, width=2)
                d.line((17, 21, 17, 25), fill=(177, 121, 60), width=2)
            elif kind == 'agility_training':
                d.line(((6, 17), (13, 10), (25, 10), (20, 5)), fill=color, width=2)
                d.line(((18, 16), (25, 10)), fill=color, width=2)
                d.line((5, 21, 17, 21), fill=(174, 239, 255), width=2)
                d.line((5, 25, 12, 25), fill=color)
            elif kind == 'endurance_training':
                d.polygon(((8, 7), (16, 4), (24, 7), (23, 18), (16, 25), (9, 18)), fill=(38, 86, 68), outline=color)
                d.line((16, 7, 16, 21), fill=(210, 250, 221), width=2)
                d.line((11, 13, 21, 13), fill=color, width=2)
            elif kind == 'spirit_training':
                d.polygon(((16, 3), (22, 12), (22, 21), (16, 25), (9, 20), (10, 14), (14, 17)), fill=color)
                d.polygon(((16, 12), (19, 19), (16, 23), (13, 20)), fill=(246, 217, 255))
            else:
                d.ellipse((9, 7, 23, 21), fill=(40, 103, 79), outline=color)
                d.line((11, 23, 21, 9), fill=(218, 255, 231), width=2)
                d.line((12, 13, 17, 15, 20, 19), fill=color)
            for i in range(tier):
                d.rectangle((7+i*7, 28, 11+i*7, 29), fill=color)
            save(f'{kind}_{tier}', im)


def translations():
    rank_names = {
        'astral_sword_sovereign': ('Astralsværdets Suveræn', 'Astral Sword Sovereign', '+3 effektiv Styrke, +2 effektiv Smidighed og +5% sværdevneskade. Bevarer Mastery-bonus.', '+3 effective Strength, +2 effective Agility and +5% sword ability damage. Retains Mastery bonuses.'),
        'celestial_archon': ('Den Himmelske Arkon', 'Celestial Archon', '+4 effektiv Ånd, +1 effektiv Udholdenhed og +5% offensiv magisk skade. Bevarer Mastery-bonus.', '+4 effective Spirit, +1 effective Endurance and +5% offensive magic damage. Retains Mastery bonuses.'),
        'eclipse_sovereign': ('Formørkelsens Suveræn', 'Eclipse Sovereign', '+2 effektiv Styrke, +2 effektiv Ånd, +1 effektiv Smidighed og +5% fortryllet sværdevneskade. Bevarer Mastery-bonus.', '+2 effective Strength, +2 effective Spirit, +1 effective Agility and +5% enchanted sword ability damage. Retains Mastery bonuses.'),
    }
    for lang, english in (('da_dk', False), ('en_us', True)):
        path = ROOT / f'lang/{lang}.json'
        data = json.loads(path.read_text())
        for key, (da, en, dd, ed) in rank_names.items():
            data[f'ability.starswordmaster.{key}'] = en if english else da
            data[f'description.starswordmaster.{key}'] = ed if english else dd
        for key, (da, en, stat_da, stat_en, _) in TRAINING.items():
            for tier in range(1, 4):
                data[f'ability.starswordmaster.{key}_{tier}'] = f'{en if english else da} {tier}'
                data[f'description.starswordmaster.{key}_{tier}'] = (f'+1 effective {stat_en}. Permanent passive; no spendable stat points.' if english else f'+1 effektiv {stat_da}. Permanent passiv; giver ingen statpoint.')
        data.update({
            'status.starswordmaster.effective_value': '%s (+%s)',
            'status.starswordmaster.effective_explanation': ('Effective %s = allocated %s + passive %s. Passive bonuses cost no stat points.' if english else 'Effektiv %s = fordelt %s + passiv %s. Passive bonusser bruger ingen statpoint.'),
            'status.starswordmaster.rank_damage.swordsman': ('Rank: +5% sword ability damage' if english else 'Rang: +5% sværdevneskade'),
            'status.starswordmaster.rank_damage.mage': ('Rank: +5% offensive magic damage' if english else 'Rang: +5% offensiv magisk skade'),
            'status.starswordmaster.rank_damage.spellblade': ('Rank: +5% enchanted sword damage' if english else 'Rang: +5% fortryllet sværdevneskade'),
            'screen.starswordmaster.passive_level': ('Passive · Level %s' if english else 'Passiv · Level %s'),
            'screen.starswordmaster.character_requirement': ('Character level %s · Yours: %s' if english else 'Karakterlevel %s · Dit: %s'),
        })
        path.write_text(json.dumps(data, ensure_ascii=False, indent=2)+'\n')


if __name__ == '__main__':
    ICONS.mkdir(parents=True, exist_ok=True)
    ranks()
    training()
    translations()

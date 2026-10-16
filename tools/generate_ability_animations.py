#!/usr/bin/env python3
"""Reproduce the 32 original, hand-keyed PAL v3 ability performances.

Each row is tick, right-arm rotation, left-arm rotation, torso rotation.
Angles are degrees in pitch/yaw/roll order. Only upper-body rotations are
written: no root motion, head control, leg control or model-part disabling.
The performances are authored individually, rather than retiming a stock pose.
"""
import json
from pathlib import Path

# id: (duration, choreography, [(tick, right arm, left arm, torso), ...])
CLIPS = {
    "spark": (14, "Draw a spark to the shoulder, flick it forward, recoil and settle.", [
        (2, (-55,-22,28),(-22,8,-12),(-3,-7,2)),
        (4, (-105,-35,42),(-38,12,-20),(-5,-13,3)),
        (5, (-87,12,-12),(-28,-8,-16),(5,10,-2)),
        (8, (-72,5,-8),(-18,-4,-10),(2,5,-1)),
        (11, (-30,-5,10),(-10,0,-5),(0,-2,0)),
    ]),
    "frost_touch": (14, "Gather a cold palm inward, press forward, then draw frost back.", [
        (1, (-30,18,-8),(-42,-25,-28),(-2,5,-2)),
        (3, (-74,35,-26),(-85,-32,-42),(-4,9,-3)),
        (6, (-95,-8,8),(-72,18,-14),(4,-6,1)),
        (9, (-91,-12,16),(-45,12,-22),(2,-9,1)),
        (11, (-38,6,0),(-26,-8,-12),(0,3,0)),
    ]),
    "light": (18, "Unfold a small star from the chest and lift it above an open palm.", [
        (2, (-48,25,18),(-48,-25,-18),(-2,0,0)),
        (5, (-92,18,30),(-78,-24,-35),(-4,-3,1)),
        (8, (-142,-6,24),(-60,4,-48),(-6,3,2)),
        (12, (-125,-15,38),(-46,8,-35),(-3,5,1)),
        (15, (-60,-4,20),(-25,0,-18),(0,1,0)),
    ]),
    "mend": (20, "Cup a wound, gather both hands close and release a gentle healing pulse.", [
        (3, (-42,32,10),(-44,-30,-12),(5,0,0)),
        (6, (-76,38,22),(-74,-36,-24),(7,-2,0)),
        (9, (-88,12,32),(-86,-10,-34),(2,0,0)),
        (13, (-66,-5,35),(-70,7,-38),(-2,2,0)),
        (17, (-27,0,12),(-29,0,-14),(0,0,0)),
    ]),
    "renew": (20, "Spiral one hand over the heart, lift the other, and open into renewal.", [
        (2, (-45,36,18),(-22,-15,-12),(3,-4,1)),
        (5, (-85,42,36),(-58,-22,-26),(4,-8,2)),
        (10, (-116,5,42),(-96,12,-48),(-4,5,-1)),
        (14, (-84,-18,30),(-118,8,-30),(-2,8,-2)),
        (17, (-34,-4,14),(-50,3,-20),(0,2,0)),
    ]),
    "quick_cut": (12, "Tight shoulder draw, explosive diagonal cut and a short edge recoil.", [
        (1, (-72,-25,38),(-20,12,-12),(-3,-9,2)),
        (3, (-132,-38,62),(-38,18,-26),(-5,-18,3)),
        (4, (-50,28,-72),(-25,-12,-16),(7,19,-4)),
        (6, (-38,34,-84),(-18,-8,-12),(5,22,-3)),
        (9, (-60,4,-20),(-12,0,-6),(0,4,0)),
    ]),
    "rapid_slash": (12, "Coil into two crossed cuts, reverse the edge and snap back to guard.", [
        (1, (-115,-32,62),(-45,16,-24),(-3,-14,2)),
        (3, (-48,34,-78),(-25,-14,-16),(4,18,-3)),
        (4, (-110,28,-48),(-36,-12,-22),(-2,12,-2)),
        (6, (-54,-36,76),(-28,18,-14),(5,-18,3)),
        (8, (-82,-16,32),(-34,8,-18),(2,-8,1)),
        (10, (-30,4,-8),(-12,0,-6),(0,2,0)),
    ]),
    "quick_dash": (10, "Tuck the blade beside the ribs, drive the shoulders and catch the exit.", [
        (1, (-42,-24,22),(-38,20,-26),(9,-8,0)),
        (2, (-62,-38,38),(-56,28,-34),(15,-14,1)),
        (4, (-28,12,-16),(-20,-12,-28),(19,6,-2)),
        (6, (-48,6,-26),(-28,-6,-12),(8,8,-1)),
        (8, (-20,-4,8),(-12,4,-6),(2,-2,0)),
    ]),
    "heavy_cut": (18, "Raise a two-handed crown guard, drop a decisive chop and absorb the weight.", [
        (3, (-88,-16,28),(-72,22,-30),(-5,-7,0)),
        (7, (-154,-12,28),(-138,20,-30),(-9,-12,1)),
        (9, (-45,8,-12),(-50,-6,-18),(17,9,-2)),
        (11, (-28,16,-22),(-38,-10,-20),(20,13,-2)),
        (15, (-58,2,10),(-42,4,-14),(3,2,0)),
    ]),
    "heavy_sweep": (18, "Load a wide side guard, sweep the horizon and let the blade wrap past.", [
        (2, (-72,-38,58),(-42,20,-26),(-3,-14,2)),
        (6, (-101,-56,82),(-68,32,-38),(-4,-26,3)),
        (8, (-90,5,-8),(-50,-8,-30),(4,-2,-1)),
        (10, (-61,54,-94),(-36,-20,-22),(6,26,-4)),
        (14, (-74,24,-48),(-24,-8,-12),(2,14,-2)),
        (16, (-30,4,-10),(-12,0,-6),(0,2,0)),
    ]),
    "crushing_edge": (18, "Stack the hilt high, pause under its weight and crush through the low line.", [
        (2, (-72,-12,18),(-66,14,-24),(-3,-4,0)),
        (5, (-136,-20,36),(-124,24,-40),(-8,-10,2)),
        (8, (-158,-28,44),(-146,28,-44),(-11,-15,3)),
        (10, (-34,20,-30),(-40,-14,-30),(23,18,-3)),
        (13, (-22,24,-38),(-30,-12,-22),(18,20,-2)),
        (16, (-48,6,-6),(-36,4,-14),(3,4,0)),
    ]),
    "guard": (16, "Snap the sword diagonally across the chest and give a compact parry beat.", [
        (1, (-42,-12,22),(-26,18,-18),(-2,-4,1)),
        (3, (-104,-30,52),(-64,25,-32),(-5,-11,2)),
        (6, (-97,-18,68),(-72,18,-30),(3,-5,3)),
        (9, (-110,-35,42),(-58,28,-26),(-2,-14,1)),
        (13, (-55,-10,22),(-32,10,-14),(0,-3,0)),
    ]),
    "mirror_guard": (16, "Cross the hands into a star mirror, tilt the reflection and unfold it.", [
        (2, (-62,26,24),(-62,-26,-24),(-2,0,0)),
        (4, (-112,40,48),(-108,-38,-48),(-5,3,0)),
        (7, (-102,26,68),(-120,-28,-58),(2,-5,3)),
        (10, (-116,34,46),(-96,-34,-62),(0,7,-3)),
        (13, (-50,12,24),(-56,-12,-28),(0,0,0)),
    ]),
    "guardian_stance": (16, "Set a broad sentinel guard, brace once and recover behind the blade.", [
        (1, (-62,-18,30),(-54,24,-35),(2,-5,0)),
        (4, (-124,-28,38),(-92,14,-58),(-4,-9,1)),
        (6, (-113,-20,48),(-108,10,-62),(6,-5,1)),
        (10, (-121,-25,34),(-96,18,-52),(1,-8,0)),
        (14, (-58,-6,16),(-46,8,-24),(0,-2,0)),
    ]),
    "riposte": (12, "Catch the incoming line, retract the point and answer with a sharp thrust.", [
        (1, (-90,-28,48),(-52,24,-28),(-3,-10,2)),
        (3, (-70,-46,20),(-42,28,-22),(-5,-17,1)),
        (5, (-94,4,-4),(-26,-14,-28),(10,12,-2)),
        (7, (-98,8,-10),(-18,-18,-22),(12,16,-2)),
        (10, (-46,-8,14),(-22,8,-12),(0,-3,0)),
    ]),
    "piercing_thrust": (12, "Draw the point far beside the waist, align it, then drive a long straight finish.", [
        (2, (-55,-42,18),(-35,26,-24),(-4,-18,1)),
        (3, (-73,-32,8),(-46,18,-18),(-3,-13,0)),
        (6, (-92,2,0),(-20,-20,-34),(14,14,-1)),
        (8, (-96,4,-6),(-16,-24,-30),(16,18,-1)),
        (10, (-59,-12,10),(-25,8,-18),(4,-5,0)),
    ]),
    "aura_wave": (12, "Raise a luminous edge, cut upward and send the wave out of an open guard.", [
        (1, (-54,-32,50),(-28,14,-24),(3,-10,2)),
        (2, (-80,-42,68),(-44,22,-34),(5,-18,3)),
        (5, (-137,24,-42),(-76,-8,-52),(-6,14,-3)),
        (7, (-125,34,-60),(-62,-12,-42),(-5,20,-2)),
        (9, (-70,14,-24),(-38,-2,-24),(-1,6,0)),
    ]),
    "blade_focus": (18, "Bring the blade to the center, check its edge and draw its power to the heart.", [
        (3, (-68,-14,20),(-52,18,-24),(-2,-3,0)),
        (5, (-114,-10,24),(-86,20,-28),(-4,-5,0)),
        (9, (-126,8,16),(-94,12,-36),(-3,4,-1)),
        (12, (-100,24,30),(-88,-12,-30),(1,8,1)),
        (16, (-42,8,12),(-38,-4,-14),(0,2,0)),
    ]),
    "fire_burst": (14, "Compress a fireball between both hands, then punch the palms out explosively.", [
        (2, (-52,34,28),(-52,-34,-28),(4,0,0)),
        (5, (-86,42,40),(-84,-42,-42),(7,-3,0)),
        (7, (-104,-24,56),(-100,22,-58),(-7,3,-1)),
        (9, (-82,-36,70),(-80,32,-68),(-5,5,-1)),
        (12, (-32,-8,24),(-34,8,-24),(0,0,0)),
    ]),
    "ice_lance": (12, "Shape a narrow lance above the left palm and spear it forward from the shoulder.", [
        (1, (-58,-22,14),(-72,20,-32),(-3,-7,1)),
        (4, (-124,-30,24),(-88,26,-38),(-6,-13,2)),
        (5, (-94,3,-6),(-65,8,-28),(8,8,-1)),
        (8, (-99,6,-12),(-44,-4,-22),(9,12,-1)),
        (10, (-44,-8,10),(-30,6,-12),(0,-2,0)),
    ]),
    "sanctuary": (20, "Fold the hands in prayer, raise an arch and spread a sheltering canopy.", [
        (3, (-80,38,24),(-80,-38,-24),(-4,0,0)),
        (7, (-142,20,26),(-142,-20,-26),(-8,0,0)),
        (10, (-124,-20,64),(-124,20,-64),(-5,0,0)),
        (14, (-84,-30,78),(-84,30,-78),(-2,0,0)),
        (18, (-30,-8,30),(-30,8,-30),(0,0,0)),
    ]),
    "restoration": (20, "Draw healing from below, cross it over the heart and cast the renewed light outward.", [
        (2, (-24,18,32),(-24,-18,-32),(7,0,0)),
        (6, (-82,42,48),(-90,-38,-44),(2,-5,2)),
        (11, (-140,18,30),(-126,-12,-44),(-7,4,-1)),
        (15, (-94,-24,64),(-102,20,-58),(-3,6,-1)),
        (18, (-34,-6,22),(-40,8,-26),(0,0,0)),
    ]),
    "frost_nova": (20, "Draw the cold inward, lift the shoulders and break both hands into a radial release.", [
        (3, (-48,32,24),(-48,-32,-24),(6,0,0)),
        (7, (-108,36,32),(-108,-36,-32),(3,0,0)),
        (11, (-138,12,44),(-138,-12,-44),(-8,0,0)),
        (13, (-75,-42,92),(-75,42,-92),(-4,0,0)),
        (17, (-54,-24,62),(-54,24,-62),(-1,0,0)),
    ]),
    "flame_blade": (12, "Wind the flaming edge low, whip an ascending cut and let the ember trail flare.", [
        (1, (-38,-28,52),(-30,14,-22),(4,-9,2)),
        (3, (-58,-44,78),(-46,22,-34),(7,-19,4)),
        (5, (-146,22,-58),(-64,-12,-36),(-8,17,-4)),
        (8, (-128,36,-80),(-42,-18,-26),(-4,23,-3)),
        (10, (-68,12,-26),(-22,-4,-12),(0,7,-1)),
    ]),
    "frost_blade": (12, "Set a high frozen edge, carve a crisp descending diagonal and arrest its weight.", [
        (2, (-120,-24,44),(-48,20,-24),(-5,-12,2)),
        (4, (-148,-32,58),(-70,26,-32),(-7,-19,3)),
        (6, (-42,28,-66),(-44,-12,-28),(10,17,-3)),
        (7, (-35,34,-76),(-32,-16,-22),(11,21,-2)),
        (10, (-74,6,-10),(-30,4,-14),(1,3,0)),
    ]),
    "flame_lance": (12, "Draw a burning spear behind the shoulder, drive it and roll the spent hand away.", [
        (1, (-70,-34,28),(-40,24,-28),(-4,-12,2)),
        (3, (-138,-46,48),(-62,34,-36),(-8,-22,3)),
        (5, (-90,8,-10),(-28,-18,-38),(12,16,-2)),
        (8, (-104,16,-28),(-18,-24,-28),(9,22,-3)),
        (11, (-34,-6,14),(-18,4,-12),(0,-2,0)),
    ]),
    "frost_ward": (16, "Trace an icy pane with the off hand and lock the sword behind its center.", [
        (2, (-54,-12,22),(-70,-18,-34),(-2,-5,-1)),
        (5, (-104,-24,34),(-118,-32,-54),(-5,-10,-2)),
        (8, (-98,-16,46),(-96,-40,-72),(3,-6,-3)),
        (11, (-110,-28,30),(-108,-20,-44),(0,-11,-1)),
        (14, (-48,-6,14),(-52,-10,-24),(0,-2,0)),
    ]),
    "arcane_step": (10, "Cross the hands over the chest, shear them apart in a short magical step and re-center.", [
        (1, (-72,38,42),(-70,-38,-40),(3,-8,2)),
        (3, (-110,48,54),(-105,-46,-56),(8,-16,3)),
        (4, (-60,-32,68),(-82,30,-72),(12,15,-3)),
        (7, (-45,-14,36),(-54,16,-42),(4,8,-1)),
        (9, (-16,2,8),(-22,-2,-12),(0,0,0)),
    ]),
    "glacier_sweep": (18, "Hold a cold high side guard, draw a broad level arc and finish with the off palm spread.", [
        (3, (-110,-34,54),(-65,22,-36),(-5,-14,2)),
        (6, (-130,-48,74),(-82,30,-44),(-7,-24,3)),
        (9, (-94,10,-18),(-76,-12,-54),(2,5,-1)),
        (12, (-72,48,-88),(-92,24,-78),(4,25,-3)),
        (15, (-84,24,-42),(-56,12,-48),(0,13,-1)),
        (17, (-26,4,-8),(-26,4,-18),(0,2,0)),
    ]),
    "seismic_cut": (18, "Lift the blade above the crown, strike low and brace through a ground-shock recoil.", [
        (2, (-82,-8,24),(-70,12,-28),(-3,-3,0)),
        (6, (-151,-18,34),(-138,22,-38),(-10,-10,2)),
        (9, (-24,12,-18),(-34,-8,-26),(24,12,-2)),
        (11, (-40,18,-30),(-46,-14,-32),(17,16,-2)),
        (14, (-70,-8,22),(-62,10,-26),(5,-4,1)),
        (16, (-38,0,8),(-30,2,-14),(1,0,0)),
    ]),
    "ember_ring": (20, "Gather embers in crossed hands, circle them out and lift the ring's flame crest.", [
        (2, (-58,34,34),(-58,-34,-34),(4,-3,0)),
        (6, (-98,44,50),(-92,-40,-46),(2,-8,2)),
        (10, (-78,-38,86),(-88,34,-82),(-3,10,-2)),
        (13, (-128,-18,54),(-118,16,-62),(-6,5,0)),
        (17, (-62,-12,38),(-58,12,-40),(-1,0,0)),
    ]),
    "ice_prison": (20, "Mark two frozen pillars, raise them with offset hands and close the cage across the chest.", [
        (3, (-56,-28,46),(-42,24,-52),(4,-5,1)),
        (6, (-112,-32,54),(-92,28,-60),(-3,-8,1)),
        (10, (-144,-12,34),(-132,10,-42),(-7,-3,0)),
        (14, (-96,42,56),(-108,-40,-52),(3,7,-2)),
        (18, (-42,16,24),(-50,-16,-28),(0,2,0)),
    ]),
}

NEUTRAL = (0, 0, 0)

def frame(tick, right=NEUTRAL, left=NEUTRAL, torso=NEUTRAL, easing="EASEINOUTSINE"):
    result = {"tick": tick, "easing": easing}
    for bone, angles in zip(("rightArm", "leftArm", "torso"), (right, left, torso)):
        result[bone] = dict(zip(("pitch", "yaw", "roll"), angles))
    return result


def main():
    target = Path(__file__).resolve().parents[1] / "src/main/resources/assets/starswordmaster/player_animations"
    for ability, (duration, description, keys) in CLIPS.items():
        moves = [frame(0)]
        moves.extend(frame(*key) for key in keys)
        moves.append(frame(duration))
        data = {
            "version": 3,
            "name": f"skill_{ability}",
            "author": "Star Swordmaster",
            "description": f"Original anime fantasy choreography. {description}",
            "emote": {
                "isLoop": False,
                "returnTick": 0,
                "beginTick": 0,
                "endTick": keys[-1][0],
                "stopTick": duration,
                "degrees": True,
                "moves": moves,
            },
        }
        target.joinpath(f"skill_{ability}.json").write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
    print(f"Generated {len(CLIPS)} original PAL ability clips")


if __name__ == "__main__":
    main()

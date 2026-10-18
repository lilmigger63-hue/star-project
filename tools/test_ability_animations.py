"""Regression checks for the shipped, original PAL performances (stdlib only).

Run from the checkout with python3 -m unittest discover -s tools -p 'test_*.py'.
The old authored sources deliberately remain independent reference poses.
"""
import json
import math
from pathlib import Path
import unittest

from generate_ability_animations import CLIPS
from generate_expansion_assets import CLIPS as EXPANSION, STYLES

ROOT = Path(__file__).resolve().parents[1]
DIRECTORY = ROOT / 'src/main/resources/assets/starswordmaster/player_animations'
BONES = ('rightArm', 'leftArm', 'torso')
AXES = ('pitch', 'yaw', 'roll')


def originals():
    result = dict(CLIPS)
    for name, (duration, style, ticks, description, replacements) in EXPANSION.items():
        poses = list(STYLES[style][1])
        for index, pose in replacements.items():
            poses[index] = pose
        result[name] = duration, description, [(tick, *pose) for tick, pose in zip(ticks, poses)]
    return result


class AbilityAnimationRefinementTest(unittest.TestCase):
    def test_all_54_have_decisive_release_during_visual_activation(self):
        for name, (duration, _, _) in originals().items():
            with self.subTest(ability=name):
                data = json.loads((DIRECTORY / f'skill_{name}.json').read_text())
                releases = [m for m in data['emote']['moves'] if m['easing'] == 'EASEOUTCUBIC']
                self.assertEqual(1, len(releases), 'One crisp release, distinct from soft recovery')
                self.assertLessEqual(abs(releases[0]['tick'] / duration - .22), .051)

    def test_all_54_refine_actual_arm_poses_and_preserve_original_endpoints(self):
        fingerprints = set()
        self.assertEqual(54, len(originals()))
        self.assertEqual(73, len(list(DIRECTORY.glob('*.json'))))
        for name, (duration, _, keys) in originals().items():
            with self.subTest(ability=name):
                data = json.loads((DIRECTORY / f'skill_{name}.json').read_text())
                emote = data['emote']
                self.assertEqual(f'skill_{name}', data['name'])
                self.assertEqual(3, data['version'])
                self.assertEqual((keys[-1][0], duration), (emote['endTick'], emote['stopTick']))
                self.assertFalse(emote['isLoop'])
                moves = emote['moves']
                self.assertEqual((0, duration), (moves[0]['tick'], moves[-1]['tick']))
                self.assertEqual(sorted(set(m['tick'] for m in moves)), [m['tick'] for m in moves])
                old_arms = [tuple(key[b][a] for b in (1, 2) for a in range(3)) for key in keys]
                refined = [tuple(m[b][a] for b in BONES[:2] for a in AXES) for m in moves[1:-1]]
                self.assertTrue(any(min(max(abs(a-b) for a,b in zip(pose, old)) for old in old_arms) >= 6
                                    for pose in refined), 'Refine arm shapes, not just metadata or timing')
                values = tuple(v for pose in refined for v in pose)
                scale = max(map(abs, values))
                fingerprint = tuple(round(v / scale, 6) for v in values)
                self.assertNotIn(fingerprint, fingerprints)
                fingerprints.add(fingerprint)
                for move in moves:
                    self.assertEqual({'tick', 'easing', *BONES}, set(move))
                    for bone in BONES:
                        self.assertEqual(set(AXES), set(move[bone]))
                        for value in move[bone].values():
                            self.assertTrue(math.isfinite(value))
                            self.assertLessEqual(abs(value), 28 if bone == 'torso' else 160)
                            if move['tick'] in (0, duration):
                                self.assertEqual(0, value)


if __name__ == '__main__':
    unittest.main()

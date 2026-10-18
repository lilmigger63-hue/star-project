"""Validate reproducible CC0 retarget data and imported atlas frames without raw downloads."""
import json,math,unittest
from pathlib import Path
from blend_combat_motion import DATA,MOTION,sample
from generate_ability_animations import authored_clips
ROOT=Path(__file__).resolve().parents[1]
class CombatAssetTest(unittest.TestCase):
 def test_all_54_use_actual_finite_cc0_motion_samples_from_all_five_packs(self):
  self.assertEqual(set(authored_clips()),set(MOTION));sources=set()
  for name,key in MOTION.items():
   curve=DATA[key];sources.add(curve['source']);self.assertEqual(33,len(curve['samples']));self.assertEqual(64,len(curve['sha256']));values=[v for row in curve['samples'] for v in row];self.assertTrue(all(math.isfinite(v) and abs(v)<=180 for v in values));self.assertGreater(max(map(abs,values)),.1,key);self.assertEqual(9,len(sample(key,.5)))
  self.assertTrue({'SS_Main.fbx','SS_TH.fbx','quaternius1.glb','quaternius2.glb','sword_arms.blend','shield_hero.blend'}<=sources)
 def test_every_selected_sprite_source_is_credited_and_has_runtime_frames(self):
  manifest=json.loads((ROOT/'tools/assets/combat_sprite_manifest.json').read_text());self.assertEqual(7,len(manifest));credits=(ROOT/'ASSET_CREDITS.md').read_text()
  for pack in manifest:
   self.assertEqual('CC0-1.0',pack['license']);self.assertIn(pack['source'],credits);self.assertEqual(pack['frames'],len(pack['outputTextures']))
   for texture in pack['outputTextures']:self.assertTrue((ROOT/'src/main/resources/assets/starswordmaster/textures/particle'/ (texture.split(':',1)[1]+'.png')).is_file())
if __name__=='__main__':unittest.main()

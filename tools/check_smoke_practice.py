"""Check real server persistence after the smoke client casts one fire spell."""
from pathlib import Path
import uuid,hashlib
player=uuid.UUID(bytes=hashlib.md5(b'OfflinePlayer:StarSmoke').digest(),version=3)
p=Path('run/world/data/starswordmaster')/(str(player)+'.properties')
values=dict(line.split('=',1) for line in p.read_text().splitlines() if '=' in line and not line.startswith('#'))
assert int(values['practice.FIRE'])>10000,'Casting a fire spell must train FIRE even while holding a sword'
print('PASS: real fire spell trained FIRE')

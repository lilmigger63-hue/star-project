"""Stop only Fabric dev-launch JVMs associated with this project's config."""
import subprocess,os,signal
for row in subprocess.check_output(['ps','-eo','pid,comm,args'],text=True).splitlines()[1:]:
 parts=row.strip().split(None,2)
 if len(parts)==3 and parts[1]=='java' and 'net.fabricmc.devlaunchinjector.Main' in parts[2] and '/workspace/star-swordmaster/' in parts[2]:
  try:os.kill(int(parts[0]),signal.SIGINT);print('Stopped our Fabric development process',parts[0])
  except ProcessLookupError:pass

package dk.starswordmaster;
import dk.starswordmaster.progression.PlayerProgress;
import java.nio.file.*;
import java.io.*;
import java.util.*;
public final class PlayerStore {
 private final Path directory;
 private final Map<UUID,PlayerProgress> players=new HashMap<>();
 public PlayerStore(Path directory){this.directory=directory;}
 public PlayerProgress get(UUID id){return players.computeIfAbsent(id,this::load);}
 private PlayerProgress load(UUID id){
  Path path=directory.resolve(id+".properties");
  if(!Files.exists(path))return new PlayerProgress();
  Properties p=new Properties();
  try(var reader=Files.newBufferedReader(path)){p.load(reader);return PlayerProgress.fromProperties(p);}
  catch(IOException|IllegalArgumentException e){throw new IllegalStateException("Cannot load player data: "+path,e);}
 }
 public void save(UUID id){
  var progress=players.get(id);if(progress==null)return;
  try{Files.createDirectories(directory);Path temp=directory.resolve(id+".tmp");
   try(var writer=Files.newBufferedWriter(temp)){progress.toProperties().store(writer,"Star Swordmaster player data v1");}
   try{Files.move(temp,directory.resolve(id+".properties"),StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}
   catch(AtomicMoveNotSupportedException e){Files.move(temp,directory.resolve(id+".properties"),StandardCopyOption.REPLACE_EXISTING);}
  }catch(IOException e){throw new IllegalStateException("Cannot save player data "+id,e);}
 }
 public void saveAll(){for(UUID id:List.copyOf(players.keySet()))save(id);}
 public void forget(UUID id){save(id);players.remove(id);}
}

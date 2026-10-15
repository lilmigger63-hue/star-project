import dk.starswordmaster.progression.*;
import java.nio.file.*;
import java.util.*;
public class CreateSmokeSave {
 public static void main(String[] args) throws Exception {
  var p=new PlayerProgress();for(Skill s:Skill.values())for(int i=0;i<500;i++)p.awardPractice(s,20);
  Path path=Path.of(args[0]);Files.createDirectories(path.getParent());
  try(var w=Files.newBufferedWriter(path)){p.toProperties().store(w,"Local smoke test only");}
  System.out.println("Prepared smoke profile at level "+p.level());
 }
}

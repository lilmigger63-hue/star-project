package dk.starswordmaster.smoke;
import dk.starswordmaster.*;
import dk.starswordmaster.progression.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import java.util.*;
public final class TreePerfServer implements ModInitializer {
 private final Set<UUID> prepared=new HashSet<>();
 @Override public void onInitialize(){if(!Boolean.getBoolean("starsmoke.treeperf"))return;
  ServerTickEvents.END_SERVER_TICK.register(s->{for(var p:s.getPlayerManager().getPlayerList())if(p.isLoaded()&&prepared.add(p.getUuid())){
   p.getAbilities().invulnerable=true;p.setNoGravity(true);
   Properties props=new Properties();props.setProperty("xp","103950");props.setProperty("xpCurveVersion","2");props.setProperty("class","SWORDSMAN");for(var skill:Skill.values())props.setProperty("practice."+skill,"250000");props.setProperty("learned",String.join(",",Ability.ALL.keySet()));
   var data=PlayerProgress.fromProperties(props);if(data.rank()!=4)throw new AssertionError("TREE_PERF invalid fixture");
   try{var sf=StarSwordmaster.class.getDeclaredField("store");sf.setAccessible(true);var store=(PlayerStore)sf.get(null);var pf=PlayerStore.class.getDeclaredField("players");pf.setAccessible(true);@SuppressWarnings("unchecked")var players=(Map<UUID,PlayerProgress>)pf.get(store);players.put(p.getUuid(),data);}catch(ReflectiveOperationException e){throw new AssertionError(e);}
   StatAttributes.apply(p,data);StarSwordmaster.sync(p);
  }});
 }
}

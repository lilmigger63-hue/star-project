package dk.starswordmaster.client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import java.lang.reflect.*;
import java.util.*;
/** Smoke-only observations of the actual deferred ribbon vertices and GPU layer. */
public final class CombatSmokeCapture {
 private static long rendered,vertices,gpu,first,third,remote;private static boolean finite=true;private static RenderLayer layer;private static final Set<Integer> QUALITIES=new HashSet<>();
 private CombatSmokeCapture(){}
 public static OrderedRenderCommandQueue.Custom wrap(NativeSwordTrails.Batch batch,RenderLayer renderLayer,OrderedRenderCommandQueue.Custom action){
  layer=renderLayer;QUALITIES.add(VfxQuality.level());return (entry,target)->{
   int[] count={0};var proxy=(VertexConsumer)Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),new Class<?>[]{VertexConsumer.class},(p,method,args)->{
    if(method.getName().equals("vertex")&&args!=null&&args.length>=3){count[0]++;for(Object a:args)if(a instanceof Float f)finite&=Float.isFinite(f)&&Math.abs(f)<80;}
    try{var result=method.invoke(target,args);return result instanceof VertexConsumer?p:result;}catch(InvocationTargetException e){throw e.getCause();}
   });action.render(entry,proxy);rendered++;vertices+=count[0];if(count[0]>96)throw new AssertionError("COMBAT native per-hand trail budget exceeded");if(batch.first())first++;else third++;
   var client=MinecraftClient.getInstance();if(client.player!=null&&!client.player.getUuid().equals(batch.caster()))remote++;
  };
 }
 public static void gpu(RenderLayer actual){if(Boolean.getBoolean("starsmoke.bdo")&&actual==layer)gpu++;}
 public static int complete(boolean observer){
  if(rendered==0||vertices==0||gpu==0||!finite||(!observer&&(first==0||third==0))||(observer&&remote==0))throw new AssertionError("COMBAT missing native ribbons: rendered="+rendered+" vertices="+vertices+" gpu="+gpu+" first="+first+" third="+third+" remote="+remote+" finite="+finite);
  System.out.println("COMBAT_NATIVE COMPLETE rendered="+rendered+" vertices="+vertices+" gpu="+gpu+" first="+first+" third="+third+" remote="+remote+" qualities="+QUALITIES);return 6;
 }
 public static void cleared(){try{var f=NativeSwordTrails.class.getDeclaredField("PLAYERS");f.setAccessible(true);if(!((Map<?,?>)f.get(null)).isEmpty())throw new AssertionError("COMBAT stale world/entity ribbon state");}catch(ReflectiveOperationException e){throw new AssertionError(e);}}
}

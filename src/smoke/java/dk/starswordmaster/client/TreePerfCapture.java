package dk.starswordmaster.client;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
/** Measures only actual native tree render submissions, never GPU FPS. Smoke-only. */
public final class TreePerfCapture {
 public static final boolean ENABLED=Boolean.getBoolean("starsmoke.treeperf");
 private static final com.sun.management.ThreadMXBean BEAN=ManagementFactory.getThreadMXBean() instanceof com.sun.management.ThreadMXBean b&&b.isThreadAllocatedMemorySupported()?b:null;
 private static final long[] times=new long[120],allocations=new long[120],fills=new long[120];
 private static int warmup,count;private static long start,allocated,fillCount;private static boolean inside,active;
 public static String phase;
 public static void start(String name){phase=name;warmup=count=0;active=true;}
 public static boolean done(){return !active;}
 public static void begin(){if(!ENABLED||!active)return;inside=true;fillCount=0;allocated=BEAN==null?0:BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId());start=System.nanoTime();}
 public static void fill(){if(inside)fillCount++;}
 public static void end(){if(!inside)return;long elapsed=System.nanoTime()-start;inside=false;if(warmup++<30)return;
  times[count]=elapsed;allocations[count]=BEAN==null?0:BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId())-allocated;fills[count]=fillCount;
  if(++count==times.length){active=false;long[] sorted=times.clone();Arrays.sort(sorted);System.out.printf(java.util.Locale.ROOT,"TREE_PERF_RESULT label=%s phase=%s frames=%d mean_us=%.3f p95_us=%.3f fills=%.1f alloc_bytes=%.1f%n",System.getProperty("starsmoke.treeperf.label","baseline"),phase,count,Arrays.stream(times).average().orElseThrow()/1000,sorted[113]/1000.0,Arrays.stream(fills).average().orElseThrow(),Arrays.stream(allocations).average().orElseThrow());}
 }
}

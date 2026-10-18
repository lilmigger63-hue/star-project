package dk.starswordmaster.network;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/** Stable network representation; disk saves continue to use ordinary Properties.store. */
public final class SnapshotWire {
 private SnapshotWire() {}

 public static String encode(Properties source) {
  // Delegate all escaping to the JDK, including whitespace, separators and line breaks.
  var sorted=new TreeMap<Object,Object>();
  for(String key:source.stringPropertyNames())sorted.put(key,source.getProperty(key));
  var properties=new Properties() {
   @Override public Set<Map.Entry<Object,Object>> entrySet() {
    return new LinkedHashSet<>(sorted.entrySet());
   }
  };
  var writer=new StringWriter();
  try { properties.store(writer,null); }
  catch(IOException e) { throw new UncheckedIOException(e); }
  String stored=writer.toString();
  // store(writer, null) always prepends exactly one generated date comment.
  return stored.substring(stored.indexOf('\n')+1);
 }

 /** One connection's last successfully sent or decoded body. Access on the owning game thread. */
 public static final class Cache {
  private String previous;
  public boolean isUnchanged(String wire,boolean ready) {return ready&&wire.equals(previous);}
  public void remember(String wire) {previous=wire;}
  public void clear() {previous=null;}
 }

 /** Compare owned player properties before paying for escaping and wire allocation. */
 public static final class Sender {
  private Properties previous;
  public String changed(Properties properties) {return properties.equals(previous)?null:encode(properties);}
  public void remember(Properties properties) {previous=(Properties)properties.clone();}
 }
}

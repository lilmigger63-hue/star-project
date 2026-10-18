package dk.starswordmaster.network;

import dk.starswordmaster.progression.Ability;
import dk.starswordmaster.progression.PlayerProgress;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

public class SnapshotWireTest {
 @Test public void canonicalBodyIsSortedWithoutTimestampAndIndependentOfInsertionOrder() throws Exception {
  var first=new Properties();first.setProperty("z","last");first.setProperty("a","first");
  var reversed=new Properties();reversed.setProperty("a","first");reversed.setProperty("z","last");
  assertEquals("a=first\nz=last\n",SnapshotWire.encode(first));
  assertEquals(SnapshotWire.encode(first),SnapshotWire.encode(reversed));
  assertEquals("",SnapshotWire.encode(new Properties()));
 }

 @Test public void standardPropertiesEscapingRoundTripsUnicodeAndSpecialCharacters() throws Exception {
  var properties=new Properties();
  properties.setProperty(" key:=#!\\\n\t\r\f雪", " leading :=#!\\\n\t\r\f Ω 😀");
  properties.setProperty("", "");properties.setProperty("comment", "#not a comment\n!still a value");
  var loaded=new Properties();loaded.load(new StringReader(SnapshotWire.encode(properties)));
  assertEquals(properties,loaded);
 }

 @Test public void defaultsAreIncludedWithoutMutatingSaveProperties() throws Exception {
  var defaults=new Properties();defaults.setProperty("default","inherited");
  var properties=new Properties(defaults);properties.setProperty("own","value");
  var loaded=new Properties();loaded.load(new StringReader(SnapshotWire.encode(properties)));
  assertEquals("inherited",loaded.getProperty("default"));assertEquals("value",loaded.getProperty("own"));
  assertEquals(1,properties.size());
  var save=new StringWriter();properties.store(save,null);assertTrue(save.toString().startsWith("#"));
 }

 @Test public void completePlayerSnapshotRestoresEveryPersistedField() throws Exception {
  var player=playerWithCast();var original=player.toProperties();
  var loaded=new Properties();loaded.load(new StringReader(SnapshotWire.encode(original)));
  assertEquals(original,loaded);
  assertEquals(original,PlayerProgress.fromProperties(loaded).toProperties());
 }

 @Test public void repeatedUnchangedWireIsSuppressedOnlyAfterSuccessfulPublication() {
  var cache=new SnapshotWire.Cache();String wire=SnapshotWire.encode(new PlayerProgress().toProperties());
  assertFalse(cache.isUnchanged(wire,true));
  cache.remember(wire);
  for(int i=0;i<100;i++)assertTrue(cache.isUnchanged(wire,true));
  assertFalse(cache.isUnchanged(wire+"extra=value\n",true));
 }

 @Test public void unchangedServerStateSkipsEncodingUntilPublicationSucceeds() {
  var sender=new SnapshotWire.Sender();var properties=new PlayerProgress().toProperties();
  String first=sender.changed(properties);assertNotNull(first);
  assertEquals(first,sender.changed(properties));
  sender.remember(properties);
  for(int i=0;i<100;i++)assertNull(sender.changed(new PlayerProgress().toProperties()));
  properties.setProperty("mana","50");assertNotNull(sender.changed(properties));
  assertNotNull(new SnapshotWire.Sender().changed(new PlayerProgress().toProperties()));
 }

 @Test public void invalidOrDisconnectedClientMustReconstructEvenIdenticalWire() {
  var cache=new SnapshotWire.Cache();String wire=SnapshotWire.encode(new PlayerProgress().toProperties());
  cache.remember(wire);assertFalse(cache.isUnchanged(wire,false));
  cache.clear();assertFalse(cache.isUnchanged(wire,true));
  cache.remember(wire);assertTrue(cache.isUnchanged(wire,true));
 }

 @Test public void separatePlayersAndReconnectDoNotShareSuppressionState() {
  var first=new SnapshotWire.Cache();var second=new SnapshotWire.Cache();
  first.remember("same");assertFalse(second.isUnchanged("same",true));
  second.remember("same");first.clear();
  assertFalse(first.isUnchanged("same",true));assertTrue(second.isUnchanged("same",true));
 }

 @Test public void cooldownResourceAndCounterChangesRemainObservable() {
  var player=playerWithCast();var cache=new SnapshotWire.Cache();
  String cast=SnapshotWire.encode(player.toProperties());cache.remember(cast);
  int cooldown=player.cooldown(player.slot(0));double mana=player.mana(),stamina=player.stamina();
  player.tick(false);
  assertEquals(cooldown-1,player.cooldown(player.slot(0)));assertTrue(player.mana()>mana);assertTrue(player.stamina()>=stamina);
  assertFalse(cache.isUnchanged(SnapshotWire.encode(player.toProperties()),true));
  cache.remember(SnapshotWire.encode(player.toProperties()));player.awardXp(BigInteger.ONE);
  assertFalse(cache.isUnchanged(SnapshotWire.encode(player.toProperties()),true));
 }

 @Test public void immediateStatUnlockResetAndCastChangesAreNotSuppressed() {
  var player=new PlayerProgress();player.awardXp(BigInteger.valueOf(200000));
  assertTrue(player.chooseClass(PlayerProgress.PlayerClass.MAGE));
  var cache=new SnapshotWire.Cache();cache.remember(SnapshotWire.encode(player.toProperties()));
  assertTrue(player.spendStat(PlayerProgress.Stat.SPIRIT));assertChanged(cache,player);
  assertTrue(player.unlock("quick_cut"));assertChanged(cache,player);
  assertTrue(player.assign(0,"quick_cut"));assertChanged(cache,player);
  assertTrue(player.cast(0));assertChanged(cache,player);
  assertTrue(player.resetLearnedSkills());assertChanged(cache,player);
 }

 private static void assertChanged(SnapshotWire.Cache cache,PlayerProgress player) {
  String wire=SnapshotWire.encode(player.toProperties());assertFalse(cache.isUnchanged(wire,true));cache.remember(wire);
 }

 private static PlayerProgress playerWithCast() {
  var player=new PlayerProgress();player.awardXp(BigInteger.valueOf(200000));
  assertTrue(player.chooseClass(PlayerProgress.PlayerClass.MAGE));assertTrue(player.unlock("quick_cut"));
  assertTrue(player.assign(0,"quick_cut"));assertTrue(player.cast(0));assertTrue(Ability.ALL.containsKey(player.slot(0)));
  return player;
 }
}

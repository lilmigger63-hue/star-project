package dk.starswordmaster.ui;
import org.junit.Test;
import static org.junit.Assert.*;
public class SnapshotActionGuardTest {
 @Test public void changedAuthoritativeSnapshotReleasesRequestImmediately(){var guard=new SnapshotActionGuard(20);var snapshot=new Object();guard.start(snapshot);assertTrue(guard.waitingFor(snapshot));assertFalse(guard.waitingFor(new Object()));}
 @Test public void rejectedRequestWithUnchangedSnapshotExpiresWithoutSendingOrChangingData(){var guard=new SnapshotActionGuard(20);var snapshot=new Object();guard.start(snapshot);for(int tick=0;tick<19;tick++){guard.tick();assertTrue(guard.waitingFor(snapshot));}guard.tick();assertFalse(guard.waitingFor(snapshot));guard.start(snapshot);assertTrue(guard.waitingFor(snapshot));}
 @Test public void repeatedRenderPollingDoesNotConsumeTickTimeout(){var guard=new SnapshotActionGuard(2);var snapshot=new Object();guard.start(snapshot);for(int i=0;i<10000;i++)assertTrue(guard.waitingFor(snapshot));guard.tick();assertTrue(guard.waitingFor(snapshot));guard.tick();assertFalse(guard.waitingFor(snapshot));}
}

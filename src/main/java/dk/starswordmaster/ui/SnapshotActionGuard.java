package dk.starswordmaster.ui;
import java.util.Objects;
/** UI-only request debounce; a rejected request may legitimately produce no changed snapshot. */
public final class SnapshotActionGuard {
 private final int timeoutTicks;
 private Object pending;
 private int remaining;
 public SnapshotActionGuard(int timeoutTicks){if(timeoutTicks<=0)throw new IllegalArgumentException("Positive timeout required");this.timeoutTicks=timeoutTicks;}
 public void start(Object snapshot){pending=Objects.requireNonNull(snapshot);remaining=timeoutTicks;}
 public boolean waitingFor(Object snapshot){if(pending!=snapshot)pending=null;return pending!=null;}
 public void tick(){if(pending!=null&&--remaining<=0)pending=null;}
}

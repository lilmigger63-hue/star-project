package dk.starswordmaster.smoke;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import dk.starswordmaster.PresentationCodecChecks;
/** Isolated registry-backed protocol checks; stops itself after success. */
public final class OverhaulCodecSmokeServer implements ModInitializer {
 public void onInitialize(){if(Boolean.getBoolean("starsmoke.overhaul.codec"))ServerLifecycleEvents.SERVER_STARTED.register(server->{PresentationCodecChecks.run(server.getOverworld());System.out.println("OVERHAUL_CODEC COMPLETE");server.stop(false);});}
}

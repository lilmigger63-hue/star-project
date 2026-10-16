package dk.starswordmaster.presentation;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Shared identifiers only; tint and motion stay entirely on the observing client. */
public final class PresentationParticleTypes {
    private static final Map<ParticlePattern.Ink, SimpleParticleType> TYPES =
            new EnumMap<>(ParticlePattern.Ink.class);

    private PresentationParticleTypes() {}

    public static void register() {
        if (!TYPES.isEmpty()) return;
        for (var ink : ParticlePattern.Ink.values()) {
            var id = Identifier.of("starswordmaster", ink.name().toLowerCase(Locale.ROOT));
            TYPES.put(ink, Registry.register(Registries.PARTICLE_TYPE, id, FabricParticleTypes.simple()));
        }
    }

    public static SimpleParticleType type(ParticlePattern.Ink ink) {
        return TYPES.get(ink);
    }
}

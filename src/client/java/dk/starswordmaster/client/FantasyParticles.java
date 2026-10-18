package dk.starswordmaster.client;

import dk.starswordmaster.presentation.ParticlePattern.Ink;
import dk.starswordmaster.presentation.PresentationParticleTypes;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

/** Native particle-atlas quads. Each emit queues exactly one finite, collision-free particle. */
public final class FantasyParticles {
    private static final Map<Ink, SpriteProvider> SPRITES = new EnumMap<>(Ink.class);
    private static boolean registered;

    private FantasyParticles() {}

    public static void register() {
        if (registered) return;
        registered = true;
        for (var ink : Ink.values()) {
            ParticleFactoryRegistry.getInstance().register(PresentationParticleTypes.type(ink), provider -> {
                SPRITES.put(ink, provider);
                // Safe vanilla /particle behavior; ability-specific parameters never use the protocol.
                return (effect, world, x, y, z, vx, vy, vz, random) -> {
                    var sprite = readySprite(provider);
                    return sprite == null ? null : new InkParticle(world, provider, sprite, 0xFFFFFF,
                            new Vec3d(x, y, z), new Vec3d(vx, vy, vz), 8, 0.32f, 0);
                };
            });
        }
    }

    /** Size is the full quad width in blocks; angle is its camera-facing rotation in radians. */
    public static boolean emit(MinecraftClient client, Ink ink, int rgb, Vec3d position,
            Vec3d velocity, int life, float size, float angle) {
        if (client == null || client.world == null || ink == null || position == null || velocity == null
                || !finite(position) || !finite(velocity) || !Float.isFinite(size) || !Float.isFinite(angle)) {
            return false;
        }
        var sprite = readySprite(SPRITES.get(ink));
        if (sprite == null) return false;
        client.particleManager.addParticle(new InkParticle(client.world, SPRITES.get(ink), sprite, rgb, position,
                velocity, life, size, angle));
        return true;
    }

    private static boolean finite(Vec3d v) {
        return Double.isFinite(v.x) && Double.isFinite(v.y) && Double.isFinite(v.z);
    }

    private static Sprite readySprite(SpriteProvider provider) {
        if (provider == null) return null;
        final Sprite sprite;
        try {
            // Fabric supplies the provider before the first resource reload binds its sprite list.
            sprite = provider.getFirst();
        } catch (IllegalStateException | IndexOutOfBoundsException | NullPointerException notLoadedYet) {
            return null;
        }
        return sprite == null || MissingSprite.getMissingSpriteId().equals(sprite.getContents().getId())
                ? null : sprite;
    }

    private static final class InkParticle extends BillboardParticle {
        private final SpriteProvider frames;
        InkParticle(ClientWorld world, SpriteProvider frames, Sprite sprite, int rgb, Vec3d p, Vec3d v,
                int life, float size, float angle) {
            super(world, p.x, p.y, p.z, sprite);
            this.frames=frames;
            maxAge = Math.clamp(life, 2, VfxQuality.level()==0?6:12);
            scale = Math.clamp(size, 0.08f, dk.starswordmaster.presentation.VisualPowerScaling.MAX_SPRITE) * 0.5f;
            setColor(((rgb >> 16) & 255) / 255f, ((rgb >> 8) & 255) / 255f, (rgb & 255) / 255f);
            setVelocity(Math.clamp(v.x, -0.35, 0.35), Math.clamp(v.y, -0.35, 0.35),
                    Math.clamp(v.z, -0.35, 0.35));
            velocityMultiplier = 0.92f;
            gravityStrength = 0;
            collidesWithWorld = false;
            zRotation = lastZRotation = angle;
            alpha = 0.9f;
        }

        @Override
        protected RenderType getRenderType() {
            return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
        }

        @Override
        public void render(BillboardParticleSubmittable batch, Camera camera, float tickProgress) {
            try{updateSprite(frames);}catch(IllegalStateException | IndexOutOfBoundsException ignored){}
            float t = Math.clamp((age + tickProgress) / maxAge, 0, 1);
            // Smoothstep fade preserves the hard pixel silhouette while softening its disappearance.
            alpha = 0.9f * (1 - t * t * (3 - 2 * t));
            super.render(batch, camera, tickProgress);
        }

        @Override
        public float getSize(float tickProgress) {
            return scale * (1 - 0.18f * Math.clamp((age + tickProgress) / maxAge, 0, 1));
        }

        @Override
        protected int getBrightness(float tickProgress) {
            return 0xF000F0;
        }
    }
}

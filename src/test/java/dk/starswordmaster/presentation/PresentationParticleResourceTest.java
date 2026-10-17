package dk.starswordmaster.presentation;

import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/** Verifies atlas resource references and pixel constraints without booting a GL client. */
public class PresentationParticleResourceTest {
    private static final Path ROOT = Path.of("src/main/resources/assets/starswordmaster");

    @Test
    public void everyRegisteredInkHasItsOwnBoundedPixelSprite() throws Exception {
        assertEquals(12, ParticlePattern.Ink.values().length);
        Set<Integer> silhouettes = new HashSet<>();
        for (var ink : ParticlePattern.Ink.values()) {
            String id = ink.name().toLowerCase(Locale.ROOT);
            var definition = JsonParser.parseString(Files.readString(ROOT.resolve("particles/" + id + ".json")))
                    .getAsJsonObject().getAsJsonArray("textures");
            assertEquals("One point must use one sprite", 1, definition.size());
            assertEquals("starswordmaster:" + id, definition.get(0).getAsString());
            BufferedImage image = ImageIO.read(ROOT.resolve("textures/particle/" + id + ".png").toFile());
            assertNotNull(id, image);
            assertTrue(id, image.getColorModel().hasAlpha());
            assertTrue(id, image.getWidth() == 16 || image.getWidth() == 32);
            assertEquals(id, image.getWidth(), image.getHeight());
            int filled = 0;
            int hash = 1;
            Set<Integer> shades = new HashSet<>();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgba = image.getRGB(x, y);
                    int alpha = rgba >>> 24;
                    assertTrue("Hard pixel alpha: " + id, alpha == 0 || alpha == 255);
                    boolean border = x == 0 || y == 0 || x == image.getWidth() - 1 || y == image.getHeight() - 1;
                    if (border) assertEquals("Transparent atlas gutter: " + id, 0, alpha);
                    if (alpha > 0) {
                        filled++;
                        int red = (rgba >> 16) & 255;
                        assertEquals("Neutral art preserves ability tint", red, (rgba >> 8) & 255);
                        assertEquals("Neutral art preserves ability tint", red, rgba & 255);
                        shades.add(red);
                    }
                    hash = 31 * hash + alpha;
                }
            }
            assertTrue("Visible silhouette: " + id, filled > 15);
            assertTrue("Sparse world-space effect: " + id, filled < image.getWidth() * image.getHeight() / 2);
            assertTrue("Pixel facets: " + id, shades.size() >= 2);
            assertTrue("Distinct silhouette: " + id, silhouettes.add(hash));
            var texture = JsonParser.parseString(Files.readString(ROOT.resolve("textures/particle/" + id + ".png.mcmeta")))
                    .getAsJsonObject().getAsJsonObject("texture");
            assertFalse("Native hard pixels", texture.get("blur").getAsBoolean());
        }
    }

    @Test
    public void sigilLeavesItsCenterOpenAndStreakRemainsNarrow() throws Exception {
        var sigil = ImageIO.read(ROOT.resolve("textures/particle/sigil.png").toFile());
        for (int y = 9; y <= 22; y++) {
            for (int x = 9; x <= 22; x++) assertEquals(0, sigil.getRGB(x, y) >>> 24);
        }
        var streak = ImageIO.read(ROOT.resolve("textures/particle/streak.png").toFile());
        for (int y = 0; y < 16; y++) {
            if (y >= 6 && y <= 9) continue;
            for (int x = 0; x < 16; x++) assertEquals(0, streak.getRGB(x, y) >>> 24);
        }
    }
}

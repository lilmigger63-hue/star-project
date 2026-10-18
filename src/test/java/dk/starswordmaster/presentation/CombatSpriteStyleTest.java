package dk.starswordmaster.presentation;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class CombatSpriteStyleTest {
 @Test public void allImportedSpriteStylesHaveActualAbilityUses(){var seen=EnumSet.noneOf(ParticlePattern.Ink.class);for(var style:AbilityPresentation.ALL.values())for(var ink:List.of(ParticlePattern.Ink.ARC,ParticlePattern.Ink.FLASH))seen.add(CombatSpriteStyle.ink(style,ink));for(var ink:ParticlePattern.Ink.values())if(ink.name().startsWith("SLASH_")||ink==ParticlePattern.Ink.IMPACT_SPARK||ink==ParticlePattern.Ink.MAGIC_FLARE)assertTrue(ink.name(),seen.contains(ink));}
 @Test public void nonAccentParticlesKeepExistingIdentity(){for(var p:AbilityPresentation.ALL.values()){assertEquals(ParticlePattern.Ink.RUNE,CombatSpriteStyle.ink(p,ParticlePattern.Ink.RUNE));assertEquals(ParticlePattern.Ink.DROPLET,CombatSpriteStyle.ink(p,ParticlePattern.Ink.DROPLET));assertEquals(ParticlePattern.Ink.ROCK,CombatSpriteStyle.ink(p,ParticlePattern.Ink.ROCK));}}
}

package dk.starswordmaster.client;
import java.nio.file.*;
import java.io.IOException;
import net.fabricmc.loader.api.FabricLoader;
import dk.starswordmaster.StarSwordmaster;
/** Local-only VFX option; hard budgets apply at every setting. */
public final class VfxQuality {
 private static int quality=1;
 private VfxQuality(){}
 public static int level(){return quality;}
 private static Path path(){return FabricLoader.getInstance().getConfigDir().resolve("starswordmaster-vfx.txt");}
 public static void load(){try{if(Files.exists(path()))quality=Math.clamp(Integer.parseInt(Files.readString(path()).trim()),0,2);}catch(IOException|NumberFormatException e){StarSwordmaster.LOG.warn("Cannot load VFX quality; using Normal");quality=1;}}
 public static void cycle(){quality=(quality+1)%3;try{Files.createDirectories(path().getParent());Files.writeString(path(),Integer.toString(quality));}catch(IOException e){StarSwordmaster.LOG.warn("Cannot save VFX quality",e);}}
}

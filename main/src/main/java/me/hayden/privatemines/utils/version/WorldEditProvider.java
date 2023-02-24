package me.hayden.privatemines.utils.version;

import org.bukkit.Location;

import java.io.File;
import java.util.HashMap;

public abstract class WorldEditProvider {

    public abstract WorldEditRegionProvider pasteSchematic(File file, Location location);

    public abstract void fillRegion(HashMap<String, Integer> filling, Location min, Location max);
}

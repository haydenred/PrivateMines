package me.hayden.privatemines.utils.version;

import org.bukkit.Location;

public abstract class WorldEditRegionProvider {

    private final Location max;
    private final Location min;

    public WorldEditRegionProvider(Location min, Location max) {
        this.max = max;
        this.min = min;
    }

    public Location getMax() {
        return max;
    }

    public Location getMin() {
        return min;
    }

    public abstract Iterable<Location> locationIterable();

}

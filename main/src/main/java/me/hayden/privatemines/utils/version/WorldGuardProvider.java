package me.hayden.privatemines.utils.version;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class WorldGuardProvider {

    public abstract List<String> getRegions(Player player);

    public abstract List<String> getRegions(Location location);

    public abstract Map<String, Object> getFlags(World world, String region);

    public abstract void setFlag(World world, String region, String flg, String value);

    public abstract void clearRegion(World world, String name);

    public abstract void createRegion(World world, String name, Location min, Location max);

    public abstract void removeRegion(World world, String name);
}

package me.hayden.privatemines;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.hayden.privatemines.utils.version.WorldGuardProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class LegacyWorldGuard extends WorldGuardProvider {

    @Override
    public List<String> getRegions(Player player) {
        return getRegions(player.getLocation());
    }

    @Override
    public List<String> getRegions(Location location) {

        RegionContainer regionContainer = WGBukkit.getPlugin().getRegionContainer();
        RegionManager regionManager = regionContainer.get(location.getWorld());

        if (regionManager == null) {
            return null;
        }

        List<String> regionList = new ArrayList<>();
        Set<ProtectedRegion> set = regionManager.getApplicableRegions(location).getRegions();
        for (ProtectedRegion r : set) {
            regionList.add(r.getId());
        }

        return regionList;
    }

    @Override
    public Map<String, Object> getFlags(World world, String region) {
        RegionContainer regionContainer = WGBukkit.getPlugin().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);
        ProtectedRegion protectedRegion = regionManager.getRegion(region);

        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<Flag<?>, Object> entry : protectedRegion.getFlags().entrySet()) {
            map.put(entry.getKey().getName(), entry.getValue());
        }

        return map;
    }

    @Override
    public void setFlag(World world, String region, String flg, String value) {
        RegionContainer regionContainer = WGBukkit.getPlugin().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);
        ProtectedRegion protectedRegion = regionManager.getRegion(region);
        FlagRegistry flagRegistry = WGBukkit.getPlugin().getFlagRegistry();

        Flag flag = WGBukkit.getPlugin().getFlagRegistry().get(flg);

        if (flag instanceof BooleanFlag) protectedRegion.setFlag(flag, Boolean.valueOf(value));
        if (flag instanceof StateFlag) protectedRegion.setFlag(flag, StateFlag.State.valueOf(value.toUpperCase()));
        if (flag instanceof StringFlag) protectedRegion.setFlag(flag, value);
        if (flag instanceof IntegerFlag) protectedRegion.setFlag(flag, Integer.valueOf(value));

    }


    @Override
    public void clearRegion(World world, String name) {
        System.out.println("Legacy: " + name);
    }

    @Override
    public void createRegion(World world, String name, Location min, Location max) {
        RegionContainer regionContainer = WGBukkit.getPlugin().getRegionContainer();
        RegionManager regionManager = regionContainer.get(world);
        ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(name, LegacyUtil.getBlockVector(min), LegacyUtil.getBlockVector(max));

        regionManager.addRegion(protectedCuboidRegion);
    }

    @Override
    public void removeRegion(World world, String name) {

    }


}

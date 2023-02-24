package me.hayden.privatemines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.hayden.privatemines.utils.version.WorldGuardProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class ModernWorldGuard extends WorldGuardProvider {

    @Override
    public List<String> getRegions(Player player) {
        return getRegions(player.getLocation());
    }

    @Override
    public List<String> getRegions(Location location) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager == null) {
            return null;
        }

        List<String> regionList = new ArrayList<>();
        Set<ProtectedRegion> set = regionManager.getApplicableRegions(ModernUtil.getBlockVector(location)).getRegions();
        for (ProtectedRegion r : set) {
            regionList.add(r.getId());
        }

        return regionList;
    }

    @Override
    public Map<String, Object> getFlags(World world, String region) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
        ProtectedRegion protectedRegion = regionManager.getRegion(region);

        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<Flag<?>, Object> entry : protectedRegion.getFlags().entrySet()) {
            map.put(entry.getKey().getName(), entry.getValue());
        }

        return map;
    }

    @Override
    public void setFlag(World world, String region, String flg, String value) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
        ProtectedRegion protectedRegion = regionManager.getRegion(region);
        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
        Flag flag = Flags.fuzzyMatchFlag(flagRegistry, flg);

        if (flag instanceof BooleanFlag) protectedRegion.setFlag(flag, Boolean.valueOf(value));
        if (flag instanceof StateFlag) protectedRegion.setFlag(flag, StateFlag.State.valueOf(value.toUpperCase()));
        if (flag instanceof StringFlag) protectedRegion.setFlag(flag, value);
        if (flag instanceof IntegerFlag) protectedRegion.setFlag(flag, Integer.valueOf(value));

    }


    @Override
    public void clearRegion(World world, String name) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion(name);

        HashMap<String, Integer> filling = new HashMap<>();
        filling.put("AIR", 100);
        new ModernWorldEdit().fillRegion(filling, BukkitAdapter.adapt(world, region.getMaximumPoint()), BukkitAdapter.adapt(world, region.getMinimumPoint()));

    }


    @Override
    public void createRegion(World world, String name, Location min, Location max) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));

        ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(name, ModernUtil.getBlockVector(min), ModernUtil.getBlockVector(max));

        regionManager.addRegion(protectedCuboidRegion);
    }

    @Override
    public void removeRegion(World world, String name) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
        regionManager.removeRegion(name);
    }


}

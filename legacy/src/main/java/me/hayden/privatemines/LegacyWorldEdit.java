package me.hayden.privatemines;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import me.hayden.privatemines.utils.version.WorldEditProvider;
import me.hayden.privatemines.utils.version.WorldEditRegionProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LegacyWorldEdit extends WorldEditProvider {

    @Override
    public WorldEditRegionProvider pasteSchematic(File file, Location location) {
        try {
            Schematic schematic = FaweAPI.load(file);
            Clipboard clipboard = schematic.getClipboard();

            //location.setY(clipboard.getOrigin().getBlockY());
            System.out.println("Origin: " + clipboard.getOrigin());
            Vector center = BukkitUtil.toVector(location);

            EditSession editSession = schematic.paste(FaweAPI.getWorld(location.getWorld().getName()), new Vector(location.getX(), location.getY(), location.getZ()), false, true, null);
            editSession.setFastMode(true);

            Region region = schematic.getClipboard().getRegion();
            region.setWorld(FaweAPI.getWorld(location.getWorld().getName()));

            try {
                region.shift(center.subtract(clipboard.getOrigin()));
            } catch (RegionOperationException e) {
                e.printStackTrace();
            }

            return new LegacyWorldEditRegion(new Location(Bukkit.getWorld(region.getWorld().getName()), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ()),
                    new Location(Bukkit.getWorld(region.getWorld().getName()), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void fillRegion(HashMap<String, Integer> filling, Location min, Location max) {

    }


}

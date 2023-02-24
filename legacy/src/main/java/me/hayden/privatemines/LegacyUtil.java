package me.hayden.privatemines;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import org.bukkit.Location;

public class LegacyUtil {

    public static Vector getVector(Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    public static BlockVector getBlockVector(Location location) {
        return new BlockVector(location.getX(), location.getY(), location.getZ());
    }


}

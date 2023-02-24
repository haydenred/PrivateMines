package me.hayden.privatemines;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;

public class ModernUtil {

    public static BlockVector3 getBlockVector(Location location) {
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }
}

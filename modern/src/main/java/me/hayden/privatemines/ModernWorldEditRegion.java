package me.hayden.privatemines;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.hayden.privatemines.utils.version.WorldEditRegionProvider;
import org.bukkit.Location;

import java.util.Iterator;

public class ModernWorldEditRegion extends WorldEditRegionProvider {

    public ModernWorldEditRegion(Location min, Location max) {
        super(min, max);
    }

    @Override
    public Iterable<Location> locationIterable() {
        final Iterator<BlockVector3> vectors = new CuboidRegion(
                BukkitAdapter.adapt(getMin().getWorld()),
                ModernUtil.getBlockVector(getMin()),
                ModernUtil.getBlockVector(getMax())
        ).iterator();

        final Iterator<Location> weVecIterator = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return vectors.hasNext();
            }

            @Override
            public Location next() {
                return BukkitAdapter.adapt(getMin().getWorld(), vectors.next());
            }
        };

        return () -> weVecIterator;
    }

}

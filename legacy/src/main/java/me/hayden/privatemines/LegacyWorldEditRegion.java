package me.hayden.privatemines;

import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.hayden.privatemines.utils.version.WorldEditRegionProvider;
import org.bukkit.Location;

import java.util.Iterator;

public class LegacyWorldEditRegion extends WorldEditRegionProvider {


    public LegacyWorldEditRegion(Location min, Location max) {
        super(min, max);
    }

    @Override
    public Iterable<Location> locationIterable() {
        final Iterator<BlockVector> vectors = new CuboidRegion(
                FaweAPI.getWorld(getMin().getWorld().getName()),
                LegacyUtil.getVector(getMin()),
                LegacyUtil.getVector(getMax())
        ).iterator();

        final Iterator<Location> weVecIterator = new Iterator<Location>() {
            @Override
            public boolean hasNext() {
                return vectors.hasNext();
            }

            @Override
            public Location next() {
                BlockVector v = vectors.next();
                return new Location(getMax().getWorld(), v.getX(), v.getY(), v.getZ());
            }
        };

        return () -> weVecIterator;
    }
}

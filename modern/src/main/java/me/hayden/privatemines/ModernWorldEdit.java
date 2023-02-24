package me.hayden.privatemines;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.hayden.privatemines.utils.version.WorldEditProvider;
import me.hayden.privatemines.utils.version.WorldEditRegionProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ModernWorldEdit extends WorldEditProvider {

    @Override
    public WorldEditRegionProvider pasteSchematic(File schematicFile, Location location) {
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        Clipboard clipboard;

        if (clipboardFormat == null) {
            return null;
        }
        if (location.getWorld() == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not paste schematic - invalid world");
            return null;
        }

        try {
            ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile));
            World worldEditWorld = BukkitAdapter.adapt(location.getWorld());
            EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(worldEditWorld).build();

            clipboard = clipboardReader.read();
            editSession.setFastMode(true);

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(ModernUtil.getBlockVector(location))
                    .ignoreAirBlocks(true)
                    .build();

            //Complete & close operation and edit session
            try {
                Operations.complete(operation);
                editSession.close();
            } catch (WorldEditException worldEditException) {

                worldEditException.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Region region = clipboard.getRegion();
        region.setWorld(BukkitAdapter.adapt(location.getWorld()));
        try {
            region.shift(ModernUtil.getBlockVector(location).subtract(clipboard.getOrigin()));
        } catch (RegionOperationException e) {
            throw new RuntimeException(e);
        }

        return new ModernWorldEditRegion(
                BukkitAdapter.adapt(location.getWorld(), region.getMinimumPoint()),
                BukkitAdapter.adapt(location.getWorld(), region.getMaximumPoint())
        );

    }

    @Override
    public void fillRegion(HashMap<String, Integer> filling, Location min, Location max) {
        org.bukkit.World world = min.getWorld();
        Region region = new CuboidRegion(BukkitAdapter.adapt(world), ModernUtil.getBlockVector(max), ModernUtil.getBlockVector(min));
        RandomPattern randomPattern = new RandomPattern();
        for (Map.Entry<String, Integer> entry : filling.entrySet()) {
            randomPattern.add(BukkitAdapter.adapt(Material.valueOf(entry.getKey()).createBlockData()), entry.getValue());
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(BukkitAdapter.adapt(world)).build()) {
            try {
                editSession.setBlocks(region, randomPattern);
            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
            editSession.flushSession();
        }
    }
}


package me.hayden.privatemines.mines;

import com.cryptomorin.xseries.XMaterial;
import me.hayden.privatemines.Main;
import me.hayden.privatemines.utils.version.WorldEditRegionProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrivateMineFactory {

    private final Main plugin;

    public PrivateMineFactory(Main plugin) {
        this.plugin = plugin;
    }

    public PrivateMine createPrivateMine(Location location, String mineName, MineType mineType) {

        Material mineMarker = XMaterial.matchXMaterial(plugin.getConfig().getString("settings.mines.blocks.mine-region")).get().parseMaterial();
        Material spawnMarker = XMaterial.matchXMaterial(plugin.getConfig().getString("settings.mines.blocks.spawn-point")).get().parseMaterial();

        //Paste schematics / create regions here
        List<Location> minePos = new ArrayList<>();
        Location spawnPoint = null;

        WorldEditRegionProvider structure = plugin.getWorldEdit().pasteSchematic(new File(mineType.getSchematic_path()), location);
        for (Location l : structure.locationIterable()) {
            Block b = l.getBlock();
            if (b.getType().equals(mineMarker)) {
                if (!minePos.contains(l)) {
                    l.getBlock().setType(Material.AIR);
                    minePos.add(l);
                }
                continue;
            }
            if (b.getType().equals(spawnMarker)) {
                l.getBlock().setType(Material.AIR);
                spawnPoint = l;
            }
        }

        //Assign names
        String mineRegionName = mineName + "-mine-region";
        String structureRegionName = mineName + "-structure-region";


        //Create mine region
        plugin.getWorldGuard().createRegion(location.getWorld(), mineRegionName, minePos.get(0), minePos.get(1));
        //Set flags for mine region
        plugin.getConfig().getConfigurationSection("settings.mines.flags.mine").getKeys(true).forEach(key -> {
            String value = plugin.getConfig().getString("settings.mines.flags.mine." + key);
            plugin.getWorldGuard().setFlag(location.getWorld(), mineRegionName, key, value);
        });


        //Create structure region
        plugin.getWorldGuard().createRegion(location.getWorld(), structureRegionName, structure.getMin(), structure.getMax());
        //Set flags for structure region
        plugin.getConfig().getConfigurationSection("settings.mines.flags.structure").getKeys(true).forEach(key -> {
            String value = plugin.getConfig().getString("settings.mines.flags.structure." + key);
            plugin.getWorldGuard().setFlag(location.getWorld(), structureRegionName, key, value);
        });


        //Create private-mine object....
        PrivateMine privateMine = new PrivateMine(mineName, spawnPoint, mineType, minePos.get(0), minePos.get(1), structureRegionName, mineRegionName);
        plugin.getMineStorage().addMine(privateMine);

        //reset mine so its filled when created
        privateMine.resetMine();

        return privateMine;
    }


}

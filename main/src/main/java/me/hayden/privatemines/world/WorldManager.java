package me.hayden.privatemines.world;

import me.hayden.privatemines.Main;
import org.bukkit.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldManager {

    private final String DEFAULT_WORLD_NAME = "privatemines";
    private final Main plugin;
    private World world;
    private int distance = 100;

    public WorldManager(Main plugin) {
        this.plugin = plugin;
        world = Bukkit.getWorld(DEFAULT_WORLD_NAME);
        if (world == null) {
            createWorld();
        }
    }

    public World getWorld() {
        return world;
    }

    public synchronized Location getNextFreeLocation() {
        int n = plugin.getConfig().getInt("settings.mines.mines-created");
        int[] point = PointUtil.findPoint(n);
        Location location = new Location(world, point[0] * distance, 50, point[1] * distance);
        plugin.getConfig().set("settings.mines.mines-created", n + 1);
        plugin.saveConfig();
        return location;
    }

    private void createWorld() {
        Logger logger = plugin.getLogger();

        logger.log(Level.INFO, "Loading mine world...");
        try {
            world = Bukkit.createWorld(new WorldCreator(DEFAULT_WORLD_NAME).type(WorldType.FLAT).generator(new EmptyChunkGenerator()));
            Bukkit.getServer().getWorlds().add(world);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "World loading failed :(");
            e.printStackTrace();
            return;
        }
        logger.log(Level.INFO, "World successfully loaded!");
    }

}



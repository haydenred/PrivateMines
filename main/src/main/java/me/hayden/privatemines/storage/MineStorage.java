package me.hayden.privatemines.storage;

import me.hayden.privatemines.Main;
import me.hayden.privatemines.mines.PrivateMine;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MineStorage {

    private final Main plugin;
    private final String MINE_STORAGE_PATH = "/storage/";


    public MineStorage(Main plugin) {
        this.plugin = plugin;

        new File(plugin.getDataFolder(), MINE_STORAGE_PATH).mkdirs();
    }


    public void addMine(PrivateMine privateMine) {
        File file = new File(plugin.getDataFolder(), MINE_STORAGE_PATH + privateMine.getOwner().getUniqueId() + ".yml");
        //Create file....
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("mine", privateMine);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void removeMine(UUID uuid) {
        File file = new File(plugin.getDataFolder(), MINE_STORAGE_PATH + uuid.toString() + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        PrivateMine privateMine = (PrivateMine) yamlConfiguration.get("mine");
        plugin.getWorldGuard().clearRegion(privateMine.getSpawn().getWorld(), privateMine.getStructureRegion());
        plugin.getWorldGuard().clearRegion(privateMine.getSpawn().getWorld(), privateMine.getMine().getMineRegion());
        plugin.getWorldGuard().removeRegion(privateMine.getSpawn().getWorld(), privateMine.getStructureRegion());
        plugin.getWorldGuard().removeRegion(privateMine.getSpawn().getWorld(), privateMine.getMine().getMineRegion());
        file.delete();
    }

    public boolean hasMine(UUID uuid) {
        File file = new File(plugin.getDataFolder(), MINE_STORAGE_PATH + uuid.toString() + ".yml");
        return file.exists();
    }

    public PrivateMine getMine(UUID uuid) {
        File file = new File(plugin.getDataFolder(), MINE_STORAGE_PATH + uuid.toString() + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        return (PrivateMine) yamlConfiguration.get("mine");
    }

}

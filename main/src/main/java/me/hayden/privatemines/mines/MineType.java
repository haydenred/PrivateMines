package me.hayden.privatemines.mines;

import me.hayden.privatemines.Main;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class MineType implements ConfigurationSerializable {

    private final String schematic_path;
    private final Integer priority;
    private String name;

    public MineType(String name) {
        JavaPlugin plugin = Main.plugin;
        this.schematic_path = plugin.getDataFolder().getPath() + "/" + plugin.getConfig().getString("mine_types." + name + ".schematic");
        this.name = name;
        this.priority = plugin.getConfig().getInt("mine_types." + name + ".priority");
    }

    public MineType(Map<String, Object> config) {
        this.schematic_path = (String) config.get("schematic_path");
        this.name = (String) config.get("name");
        this.priority = (Integer) config.get("priority");
    }

    public static MineType getTypeByPriority(Integer priority) {
        for (String type : Main.plugin.getConfig().getConfigurationSection("mine_types").getKeys(false)) {
            if (Main.plugin.getConfig().getInt("mine_types." + type + ".priority") == priority) {
                return new MineType(type);
            }
        }
        return null;
    }

    //Checks if there is a mine with matching priority / can be used to check if user has reached end of track
    public static boolean priorityExists(Integer priority) {
        for (String type : Main.plugin.getConfig().getConfigurationSection("mine_types").getKeys(false)) {
            if (Main.plugin.getConfig().getInt("mine_types." + type + ".priority") == priority) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getSchematic_path() {
        return schematic_path;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("name", name);
        result.put("priority", priority);
        result.put("schematic_path", schematic_path);
        return result;
    }
}

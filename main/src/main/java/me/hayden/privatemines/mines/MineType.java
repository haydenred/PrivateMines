package me.hayden.privatemines.mines;

import me.hayden.privatemines.Main;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class MineType implements ConfigurationSerializable {

    private final String schematic_path;
    //HashMap<Material,Chance>
    private final String name;

    public MineType(String name) {
        JavaPlugin plugin = Main.plugin;
        this.schematic_path = plugin.getDataFolder().getPath() + "/" + plugin.getConfig().getString("mine_types." + name + ".schematic");
        this.name = name;
    }

    public MineType(Map<String, Object> config) {
        this.schematic_path = (String) config.get("schematic_path");
        this.name = (String) config.get("name");
    }

    public String getName() {
        return name;
    }

    public String getSchematic_path() {
        return schematic_path;
    }


    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("name", name);
        result.put("schematic_path", schematic_path);
        return result;
    }
}

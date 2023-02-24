package me.hayden.privatemines.mines;

import me.hayden.privatemines.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Mine implements ConfigurationSerializable {

    private final HashMap<String, Integer> filling;
    private final String mineRegionName;
    private final Location pos1;
    private final Location pos2;


    public Mine(String mineRegionName, Location pos1, Location pos2, PrivateMine privateMine) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.mineRegionName = mineRegionName;

        filling = new HashMap<>();
        for (String s : Main.plugin.getConfig().getStringList("mine_types." + privateMine.getType().getName() + ".filling")) {
            Material m = Material.getMaterial(s.split(":")[0]);
            Integer c = Integer.valueOf(s.split(":")[1]);
            filling.put(m.name(), c);
        }
    }

    public Mine(Map<String, Object> config) {
        this.mineRegionName = (String) config.get("mine_region");
        this.filling = (HashMap<String, Integer>) config.get("filling");
        this.pos1 = (Location) config.get("pos1");
        this.pos2 = (Location) config.get("pos2");
    }


    public HashMap<String, Integer> getFilling() {
        return filling;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public String getMineRegion() {
        return mineRegionName;
    }


    public void reset() {
        Main.plugin.getWorldEdit().fillRegion(filling, pos1, pos2);
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("mine_region", mineRegionName);
        result.put("pos1", this.pos1);
        result.put("pos2", this.pos2);
        result.put("filling", this.filling);
        return result;
    }
}

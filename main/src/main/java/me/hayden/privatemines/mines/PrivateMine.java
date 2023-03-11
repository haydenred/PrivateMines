package me.hayden.privatemines.mines;

import me.hayden.privatemines.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static me.hayden.privatemines.Main.plugin;

public class PrivateMine implements ConfigurationSerializable {

    private final OfflinePlayer owner;
    private final Location spawn;
    private final MineType type;

    private final Mine mine;
    private final String structureRegionName;

    public PrivateMine(String uuid, Location spawn, MineType mineType, Location minePos1, Location minePos2, String structureRegionName, String mineRegionName) {
        this.owner = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        this.spawn = spawn;
        this.type = mineType;
        this.structureRegionName = structureRegionName;
        this.mine = new Mine(mineRegionName, minePos1, minePos2, this);

    }

    public PrivateMine(Map<String, Object> config) {
        this.owner = Bukkit.getOfflinePlayer(UUID.fromString((String) config.get("owner")));
        this.spawn = (Location) config.get("spawn");
        this.type = (MineType) config.get("mine_type");
        this.mine = (Mine) config.get("mine");
        this.structureRegionName = (String) config.get("structure_region");
    }


    public OfflinePlayer getOwner() {
        return owner;
    }

    public Location getSpawn() {
        return spawn;
    }

    public MineType getType() {
        return type;
    }

    public String getStructureRegion() {
        return structureRegionName;
    }

    public Mine getMine() {
        return mine;
    }

    public void resetMine() {
        getMine().reset();
        plugin.getMineStorage().addMine(this);
    }
    public void upgrade() {
        int priority = type.getPriority();
        if (MineType.priorityExists(priority + 1)) {
            plugin.getMineStorage().removeMine(owner.getUniqueId());
            plugin.getPrivateMineFactory().createPrivateMine(plugin.getWorldManger().getNextFreeLocation(), owner.getUniqueId().toString(), MineType.getTypeByPriority(priority + 1));
        }
    }

    //Save changes made to privatemine
    public void save() {
        plugin.getMineStorage().addMine(this);
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap result = new LinkedHashMap();
        result.put("owner", owner.getUniqueId().toString());
        result.put("structure_region", structureRegionName);
        result.put("spawn", spawn);
        result.put("mine_type", type);
        result.put("mine", mine);

        return result;
    }


}

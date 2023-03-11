package me.hayden.privatemines.ui;

import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import me.hayden.privatemines.ui.util.MenuUtil;
import me.hayden.privatemines.utils.ChatUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    private final UserInterfaceAPI userInterfaceAPI;
    private final ConfigurationSection path;
    private final ItemStack item;
    private final Material material;
    private final String display_name;
    private final List<String> lore;
    private final int slot;
    private final boolean glowing;
    private final List<String> actions;
    private final List<RequirementImplementation> requirements;

    public MenuItem(ConfigurationSection path, UserInterfaceAPI userInterfaceAPI) {
        this.path = path;
        this.userInterfaceAPI = userInterfaceAPI;

        this.material = MenuUtil.getMaterial(path.getString("material"));
        this.display_name = path.getString("displayname");
        this.lore = MenuUtil.colorList(path.getStringList("lore"));
        this.slot = path.getInt("slot");
        this.glowing = path.getBoolean("glowing");
        this.actions = path.getStringList("actions");
        this.requirements = new ArrayList<>();

        if (path.contains("requirements")) {
            for (String s : path.getConfigurationSection("requirements").getKeys(false)) {
                if (s.equalsIgnoreCase("deny-commands")) {
                    continue;
                }
                ConfigurationSection section = path.getConfigurationSection("requirements." + s);
                //We don't want it counting the deny commands as a requirement section
                RequirementImplementation r = userInterfaceAPI.getRequirement(section);
                r.setPath(section);
                requirements.add(r);
            }
        }

        //Build item
        this.item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();

        if (this.glowing) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setDisplayName(ChatUtil.color(this.display_name));
        meta.setLore(this.lore);

        this.item.setItemMeta(meta);

    }

    public ConfigurationSection getPath() {
        return path;
    }

    public void runActions(Player player) {
        if (actions != null) {
            for (String s : actions) {
                userInterfaceAPI.getActionHandler().handle(player, s);
            }
        }
    }


    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    public List<RequirementImplementation> getRequirements() {
        return requirements;
    }

}

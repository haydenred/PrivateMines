package me.hayden.privatemines.ui;

import me.hayden.privatemines.ui.util.MenuUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Menu {

    private final UserInterfaceAPI userInterfaceAPI;
    private final ConfigurationSection menu_path;
    private final String name;
    private final int size;
    private final HashMap<MenuItem, Integer> contents;
    private Player player; //Player is only assigned when the menu is opened


    private Inventory menuInventory;

    public Menu(UserInterfaceAPI userInterfaceAPI, ConfigurationSection path) {
        this.userInterfaceAPI = userInterfaceAPI;
        this.menu_path = path;

        contents = new HashMap<>();
        size = path.getInt("size");
        name = path.getString("name");

        putContents();
    }

    private void putContents() {

        for (String s : menu_path.getConfigurationSection("contents").getKeys(false)) {
            final ConfigurationSection item_path = menu_path.getConfigurationSection("contents." + s);
            MenuItem menuItem = new MenuItem(item_path, userInterfaceAPI);

            contents.put(menuItem, menuItem.getSlot());
        }
    }

    public Menu build() {
        menuInventory = Bukkit.createInventory(null, size, MenuUtil.color(name));

        for (Map.Entry<MenuItem, Integer> entry : contents.entrySet()) {
            MenuItem item = entry.getKey();
            Integer slot = entry.getValue();

            menuInventory.setItem(slot, item.getItem());
        }

        return this;

    }

    public void open(Player player) {
        player.openInventory(menuInventory);
        InventoryManager.activeInventories.put(menuInventory, this);
    }

    public void close() {
        player.closeInventory();
        InventoryManager.activeInventories.remove(menuInventory, this);
    }

    public MenuItem getItemAtSlot(int targetSlot) {
        for (Map.Entry<MenuItem, Integer> entry : contents.entrySet()) {
            MenuItem item = entry.getKey();
            Integer slot = entry.getValue();

            if (slot == targetSlot) {
                return item;
            }
        }
        return null;
    }

    public void getRequirement(String type) {

    }


}

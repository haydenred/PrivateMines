package me.hayden.privatemines.ui;

import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InventoryManager implements Listener {

    protected static HashMap<Inventory, Menu> activeInventories = new HashMap<>();
    private final UserInterfaceAPI userInterfaceAPI;

    public InventoryManager(UserInterfaceAPI userInterfaceAPI) {
        this.userInterfaceAPI = userInterfaceAPI;
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (activeInventories.containsKey(event.getInventory())) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Menu menu = activeInventories.get(event.getInventory());
            MenuItem item = menu.getItemAtSlot(event.getRawSlot());

            //Make sure user has not clicked on air/null
            if (item == null) {
                return;
            }
            for (RequirementImplementation requirementImplementation : item.getRequirements()) {
                if (!requirementImplementation.parseRequirement(player)) {
                    //Player does not meet the requirement to click on item / perform action
                    item.getPath().getStringList("requirements.deny-commands").forEach(e -> {
                        //Use action handler to handle requirement deny commands, keep things uniform
                        userInterfaceAPI.getActionHandler().handle(player, e);
                    });
                    return;
                }
            }

            item.runActions(player);
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        activeInventories.remove(event.getInventory());
    }

}

package me.hayden.privatemines.commands;

import me.hayden.privatemines.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

    private Main plugin;

    public PlayerCommandListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage();

        for (String e : plugin.getMenuConfig().getKeys(false)) {
            String c = plugin.getMenuConfig().getString(e + ".command");
            if (cmd.trim().equalsIgnoreCase("/" + c)) {
                plugin.getUserInterfaceAPI().getMenu(plugin.getMenuConfig().getConfigurationSection(e)).build().open(player);
                event.setCancelled(true);
            }
        }
    }

}

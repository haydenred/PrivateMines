package me.hayden.privatemines.ui;

import me.hayden.privatemines.ui.util.MenuUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class ActionHandler {


    public void handle(Player p, String s) {
        //Global Placeholders
        s = s.replace("%player%", p.getName());

        s = handleDefaultString(p, s);
        s = handleCustomAction(p, s);
    }

    public String handleDefaultString(Player player, String s) {
        if (s.startsWith("[message]")) {
            String[] split = s.split("\\[message] ");
            player.sendMessage(MenuUtil.color(split[1]));
        }

        if (s.startsWith("[broadcast]")) {
            String[] split = s.split("\\[broadcast] ");
            Bukkit.broadcastMessage(MenuUtil.color(split[1]));
        }

        if (s.startsWith("[close]")) {
            player.closeInventory();
        }

        if (s.startsWith("[player]")) {
            String[] split = s.split("\\[player] ");
            String command = split[1];
            player.performCommand(command);
        }


        if (s.startsWith("[console]")) {
            String[] split = s.split("\\[console] ");
            String command = split[1];
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        return s;
    }

    //Handle custom string supposed to be for implementation as an API/Plugin Specific Actions
    public abstract String handleCustomAction(Player player, String s);

}

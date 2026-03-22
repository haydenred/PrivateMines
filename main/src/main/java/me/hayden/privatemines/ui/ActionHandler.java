package me.hayden.privatemines.ui;

import me.hayden.privatemines.ui.util.MenuUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

        if (s.startsWith("[sound]")) {
            String[] split = s.split("\\[sound] ");
            if (split.length > 1) {
                String[] parts = split[1].split(":");
                String soundName = parts[0].trim().toUpperCase();
                float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                try {
                    Sound sound = Sound.valueOf(soundName);
                    player.playSound(player.getLocation(), sound, volume, pitch);
                } catch (IllegalArgumentException ignored) {
                    // Unknown sound name — skip silently
                }
            }
        }

        if (s.startsWith("[title]")) {
            String[] split = s.split("\\[title] ");
            if (split.length > 1) {
                String[] parts = split[1].split(";", 2);
                String title = MenuUtil.color(parts[0]);
                String subtitle = parts.length > 1 ? MenuUtil.color(parts[1]) : "";
                player.sendTitle(title, subtitle, 10, 70, 20);
            }
        }

        if (s.startsWith("[actionbar]")) {
            String[] split = s.split("\\[actionbar] ");
            if (split.length > 1) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(MenuUtil.color(split[1])));
            }
        }

        return s;
    }

    //Handle custom string supposed to be for implementation as an API/Plugin Specific Actions
    public abstract String handleCustomAction(Player player, String s);

}

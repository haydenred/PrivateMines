package me.hayden.privatemines.commands;

import me.hayden.privatemines.Main;
import me.hayden.privatemines.mines.MineType;
import me.hayden.privatemines.mines.PrivateMine;
import me.hayden.privatemines.storage.Messages;
import me.hayden.privatemines.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class Command implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public Command(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                plugin.getMessagesConfig().getStringList("help").forEach(s-> {
                    player.sendMessage(ChatUtil.color(s));
                });
                //plugin.getUserInterfaceAPI().getMenu(plugin.getMenuConfig().getConfigurationSection("menu")).build().open(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (!player.hasPermission("privatemines.create")) { player.sendMessage(Messages.PERMISSION_DENIED.toString()); return true; };
                if (plugin.getMineStorage().hasMine(player.getUniqueId())) {
                    player.sendMessage(Messages.ALREADY_HAS_MINE.toString());
                    return true;
                }
                player.sendMessage(Messages.CREATE_SUCCESS.toString());
                plugin.getPrivateMineFactory().createPrivateMine(plugin.getWorldManger().getNextFreeLocation(), player.getUniqueId().toString(), MineType.getTypeByPriority(1));
                player.teleport(plugin.getMineStorage().getMine(player.getUniqueId()).getSpawn());
                return true;
            }
            if (args[0].equalsIgnoreCase("teleport")) {
                if (!player.hasPermission("privatemines.teleport")) { player.sendMessage(Messages.PERMISSION_DENIED.toString()); return true;};

                if (!plugin.getMineStorage().hasMine(player.getUniqueId())) {
                    player.sendMessage(Messages.DOESNT_OWN_MINE.toString());
                    return true;
                }
                player.sendMessage(Messages.TELEPORT_SUCCESS.toString());
                PrivateMine privateMine = plugin.getMineStorage().getMine(player.getUniqueId());
                player.teleport(privateMine.getSpawn().setDirection(player.getLocation().getDirection()));
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (!player.hasPermission("privatemines.delete")) { player.sendMessage(Messages.PERMISSION_DENIED.toString());return true; };
                if (!plugin.getMineStorage().hasMine(player.getUniqueId())) {
                    player.sendMessage(Messages.DOESNT_OWN_MINE.toString());
                    return true;
                }
                plugin.getMineStorage().removeMine(player.getUniqueId());
                player.sendMessage(Messages.DELETE_SUCCESS.toString());
                return true;
            }

            if (args[0].equalsIgnoreCase("admin")) {
                if (!player.hasPermission("privatemines.admin")) {
                    player.sendMessage(Messages.PERMISSION_DENIED.toString());
                    return true;
                }

                if (args[1].equalsIgnoreCase("upgrade")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                    if (!plugin.getMineStorage().hasMine(target.getUniqueId())) {
                        player.sendMessage("Target doesn't own a mine");
                        return true;
                    }
                    PrivateMine privateMine = plugin.getMineStorage().getMine(target.getUniqueId());
                    if (MineType.priorityExists(privateMine.getType().getPriority() + 1)) {
                        privateMine.upgrade();
                        if (target.isOnline()) {
                            ((Player) target).teleport(plugin.getMineStorage().getMine(target.getUniqueId()).getSpawn());
                        }
                    }
                    return true;
                }
                if (args[1].equalsIgnoreCase("teleport")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                    if (!plugin.getMineStorage().hasMine(target.getUniqueId())) {
                        player.sendMessage("Target doesn't own a mine");
                        return true;
                    }
                    PrivateMine privateMine = plugin.getMineStorage().getMine(target.getUniqueId());
                    player.teleport(privateMine.getSpawn());
                    return true;
                }
                if (args[1].equalsIgnoreCase("delete")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                    if (!plugin.getMineStorage().hasMine(target.getUniqueId())) {
                        player.sendMessage("Target doesn't own a mine");
                        return true;
                    }
                    plugin.getMineStorage().removeMine(target.getUniqueId());
                    player.sendMessage("Target's mine has been deleted");
                    return true;
                }
                if (args[1].equalsIgnoreCase("reset")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                    if (!plugin.getMineStorage().hasMine(target.getUniqueId())) {
                        player.sendMessage("Target doesn't own a mine");
                        return true;
                    }
                    PrivateMine privateMine = plugin.getMineStorage().getMine(target.getUniqueId());
                    privateMine.resetMine();
                    return true;
                }
            }

            //player.teleport(new Location(Bukkit.getWorld("privatemines"), 0, 50, 0));
            //plugin.getPrivateMineFactory().createPrivateMine(new Location(Bukkit.getWorld("privatemines"), 0, 50, 0), player.getUniqueId(), "test");
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) { return Collections.EMPTY_LIST; }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("privatemines")) {
            final List<String> completions = new ArrayList<>();
            //Default COmmands
            if (args.length == 1) {
                final List<String> argList = new ArrayList<>();
                if (player.hasPermission("privatemines.create")) { argList.add("create"); }
                if (player.hasPermission("privatemines.teleport")) { argList.add("teleport"); }
                if (player.hasPermission("privatemines.delete")) { argList.add("delete"); }
                if (player.hasPermission("privatemines.admin")) { argList.add("admin"); }
                StringUtil.copyPartialMatches(args[0], argList, completions);
            }

            //Admin commands
            if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
                final List<String> argList = new ArrayList<>();
                argList.add("upgrade");
                argList.add("teleport");
                argList.add("delete");
                argList.add("reset");
                StringUtil.copyPartialMatches(args[1], argList, completions);
            }

            //Auto complete with player names for all admin commands
            if (args.length == 3 && args[0].equalsIgnoreCase("admin")) {
                final List<String> argList = new ArrayList<>();
                Bukkit.getOnlinePlayers().forEach(a -> {
                    argList.add(a.getName());
                });
                StringUtil.copyPartialMatches(args[2], argList, completions);
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }


}

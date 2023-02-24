package me.hayden.privatemines;

import me.hayden.privatemines.mines.MineType;
import me.hayden.privatemines.mines.PrivateMine;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                player.sendMessage("Default command // send command list");
                plugin.getUserInterfaceAPI().getMenu(plugin.getMenuConfig().getConfigurationSection("menu")).build().open(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (plugin.getMineStorage().hasMine(player.getUniqueId())) {
                    player.sendMessage("You already have a mine dipshit");
                    return true;
                }
                player.sendMessage("Creating mine....");
                player.sendMessage("Your uuid is " + player.getUniqueId());
                plugin.getPrivateMineFactory().createPrivateMine(plugin.getWorldManger().getNextFreeLocation(), player.getUniqueId().toString(), new MineType("test"));
                player.teleport(plugin.getMineStorage().getMine(player.getUniqueId()).getSpawn());
                return true;
            }
            if (args[0].equalsIgnoreCase("teleport")) {
                if (!plugin.getMineStorage().hasMine(player.getUniqueId())) {
                    player.sendMessage("You don't have a mine to teleport to");
                    return true;
                }
                PrivateMine privateMine = plugin.getMineStorage().getMine(player.getUniqueId());
                player.teleport(privateMine.getSpawn().setDirection(player.getLocation().getDirection()));
                return true;
            }

            if (args[0].equalsIgnoreCase("fill")) {
                if (!plugin.getMineStorage().hasMine(player.getUniqueId())) {
                    player.sendMessage("You don't have a mine");
                    return true;
                }
                PrivateMine privateMine = plugin.getMineStorage().getMine(player.getUniqueId());
                privateMine.getMine().reset();
                return true;
            }
            if (args[0].equalsIgnoreCase("delete")) {
                if (!plugin.getMineStorage().hasMine(player.getUniqueId())) {
                    player.sendMessage("You don't have a mine");
                    return true;
                }
                plugin.getMineStorage().removeMine(player.getUniqueId());
                player.sendMessage("Your mine has been deleted");
                return true;
            }

            //player.teleport(new Location(Bukkit.getWorld("privatemines"), 0, 50, 0));
            //plugin.getPrivateMineFactory().createPrivateMine(new Location(Bukkit.getWorld("privatemines"), 0, 50, 0), player.getUniqueId(), "test");
        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String alias, String[] args) {
        System.out.println(cmd.getName().toLowerCase());
        if (cmd.getName().equalsIgnoreCase("privatemines")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();

            if (args.length == 1){
                oneArgList.add("create");
                oneArgList.add("teleport");
                oneArgList.add("fill");
                oneArgList.add("delete");
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }


}

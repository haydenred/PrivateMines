package me.hayden.privatemines;

import me.hayden.privatemines.mines.PrivateMine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class MineResetTask extends BukkitRunnable {

    private static Main plugin;
    public MineResetTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getMineStorage().hasMine(player.getUniqueId())) {
                PrivateMine privateMine = plugin.getMineStorage().getMine(player.getUniqueId());

                int resetDelay = plugin.getConfig().getInt("settings.mines.reset-delay");
                Long lastReset = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - privateMine.getMine().getLastReset());
                //System.out.println(lastReset.intValue());
                if (!(lastReset.intValue() >= resetDelay)) return;
                privateMine.resetMine();
            }
        }
    }
}

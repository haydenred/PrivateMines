package me.hayden.privatemines;

import me.hayden.privatemines.commands.Command;
import me.hayden.privatemines.commands.PlayerCommandListener;
import me.hayden.privatemines.mines.Mine;
import me.hayden.privatemines.mines.MineType;
import me.hayden.privatemines.mines.PrivateMine;
import me.hayden.privatemines.mines.PrivateMineFactory;
import me.hayden.privatemines.storage.Messages;
import me.hayden.privatemines.storage.MineStorage;
import me.hayden.privatemines.ui.ActionHandler;
import me.hayden.privatemines.ui.UserInterfaceAPI;
import me.hayden.privatemines.ui.requirements.HasMineRequirement;
import me.hayden.privatemines.utils.version.WorldEditProvider;
import me.hayden.privatemines.utils.version.WorldGuardProvider;
import me.hayden.privatemines.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Main plugin;

    //Menu Configuration
    private File menuConfigFile;
    private FileConfiguration menuConfig;

    //Messages Configuration
    private File messagesConfigFile;
    private FileConfiguration messagesConfig;

    private PrivateMineFactory privateMineFactory;
    private WorldManager worldManger;
    private MineStorage mineStorage;
    private WorldEditProvider worldEditProvider;
    private WorldGuardProvider worldGuardProvider;
    private UserInterfaceAPI userInterfaceAPI;

    public FileConfiguration getMenuConfig() {
        return menuConfig;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        loadDependencies();
        //Enable legacy material support
        Material.getMaterial("DIRT", true);
        //Instantiate userinterfaceapi
        ActionHandler actionHandler = new ActionHandler() {
            @Override
            public String handleCustomAction(Player player, String s) {
                if (s.startsWith("[mine-create]")) {
                    if (!player.hasPermission("privatemines.create")) {
                        player.sendMessage(Messages.PERMISSION_DENIED.toString());
                        return s;
                    }
                    if (getMineStorage().hasMine(player.getUniqueId())) {
                        player.sendMessage(Messages.ALREADY_HAS_MINE.toString());
                        return s;
                    }
                    player.sendMessage(Messages.CREATE_SUCCESS.toString());
                    getPrivateMineFactory().createPrivateMine(
                            getWorldManger().getNextFreeLocation(),
                            player.getUniqueId().toString(),
                            MineType.getTypeByPriority(1));
                    player.teleport(getMineStorage().getMine(player.getUniqueId()).getSpawn());
                }

                if (s.startsWith("[mine-teleport]")) {
                    if (!getMineStorage().hasMine(player.getUniqueId())) {
                        player.sendMessage(Messages.DOESNT_OWN_MINE.toString());
                        return s;
                    }
                    player.sendMessage(Messages.TELEPORT_SUCCESS.toString());
                    PrivateMine mine = getMineStorage().getMine(player.getUniqueId());
                    player.teleport(mine.getSpawn().setDirection(player.getLocation().getDirection()));
                }

                if (s.startsWith("[mine-delete]")) {
                    if (!getMineStorage().hasMine(player.getUniqueId())) {
                        player.sendMessage(Messages.DOESNT_OWN_MINE.toString());
                        return s;
                    }
                    getMineStorage().removeMine(player.getUniqueId());
                    player.sendMessage(Messages.DELETE_SUCCESS.toString());
                }

                if (s.startsWith("[mine-reset]")) {
                    if (!getMineStorage().hasMine(player.getUniqueId())) {
                        player.sendMessage(Messages.DOESNT_OWN_MINE.toString());
                        return s;
                    }
                    PrivateMine mine = getMineStorage().getMine(player.getUniqueId());
                    mine.resetMine();
                    player.sendMessage(Messages.RESET_SUCCESS.toString());
                }

                if (s.startsWith("[mine-upgrade]")) {
                    if (!getMineStorage().hasMine(player.getUniqueId())) {
                        player.sendMessage(Messages.DOESNT_OWN_MINE.toString());
                        return s;
                    }
                    PrivateMine mine = getMineStorage().getMine(player.getUniqueId());
                    if (!MineType.priorityExists(mine.getType().getPriority() + 1)) {
                        player.sendMessage(Messages.MAX_MINE_LEVEL.toString());
                        return s;
                    }
                    mine.upgrade();
                    player.sendMessage(Messages.UPGRADE_SUCCESS.toString());
                    if (getMineStorage().hasMine(player.getUniqueId())) {
                        player.teleport(getMineStorage().getMine(player.getUniqueId()).getSpawn());
                    }
                }

                if (s.startsWith("[openmenu]")) {
                    String[] split = s.split("\\[openmenu] ");
                    if (split.length > 1) {
                        String menuKey = split[1].trim();
                        ConfigurationSection section = getMenuConfig().getConfigurationSection(menuKey);
                        if (section != null) {
                            getUserInterfaceAPI().getMenu(section).build().open(player);
                        }
                    }
                }

                return s;
            }
        };
        userInterfaceAPI = new UserInterfaceAPI(this, actionHandler);
        userInterfaceAPI.registerRequirement(new HasMineRequirement());

        //Initalize commands
        this.getCommand("privatemines").setExecutor(new Command(this));
        this.getCommand("privatemines").setTabCompleter(new Command(this));

        //Initalize Listeners
        getServer().getPluginManager().registerEvents(new PlayerCommandListener(this), this);


        privateMineFactory = new PrivateMineFactory(this);
        worldManger = new WorldManager(this);
        mineStorage = new MineStorage(this);

        ConfigurationSerialization.registerClass(PrivateMine.class);
        ConfigurationSerialization.registerClass(MineType.class);
        ConfigurationSerialization.registerClass(Mine.class);

        //Create schematic folder
        new File(getDataFolder(), "/schematics/").mkdirs();

        //Create custom configuration files
        createMenuConfig();
        createMessagesConfig();

        //Start mine reset task
        new MineResetTask(this).runTaskTimer(this, 0, 60);

    }

    private void loadDependencies() {
        Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
        Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (we == null || wg == null) {
            getLogger().log(Level.SEVERE, "Please install WorldEdit and WorldGuard on your server");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Going off WorldEdits version to tell if server is legacy
        if (we.getDescription().getVersion().startsWith("2") || we.getDescription().getVersion().startsWith("1.14") || we.getDescription().getVersion().startsWith("1.13")) {
            try {
                String packageName = Main.class.getPackage().getName();
                worldEditProvider = (WorldEditProvider) Class.forName(packageName + "." + "ModernWorldEdit").newInstance();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Could not load WorldEdit hook");
                e.printStackTrace();
                getServer().getPluginManager().disablePlugin(this);
            }

            try {
                String packageName = Main.class.getPackage().getName();
                worldGuardProvider = (WorldGuardProvider) Class.forName(packageName + "." + "ModernWorldGuard").newInstance();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Could not load WorldGuard hook");
                e.printStackTrace();
                getServer().getPluginManager().disablePlugin(this);
            }

            return;

        }
        try {
            String packageName = Main.class.getPackage().getName();
            worldEditProvider = (WorldEditProvider) Class.forName(packageName + "." + "LegacyWorldEdit").newInstance();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not load WorldEdit hook");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        try {
            String packageName = Main.class.getPackage().getName();
            worldGuardProvider = (WorldGuardProvider) Class.forName(packageName + "." + "LegacyWorldGuard").newInstance();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not load WorldGuard hook");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void createMenuConfig() {
        menuConfigFile = new File(getDataFolder(), "menus.yml");
        if (!menuConfigFile.exists()) {
            menuConfigFile.getParentFile().mkdirs();
            saveResource("menus.yml", false);
        }

        menuConfig = YamlConfiguration.loadConfiguration(menuConfigFile);
    }

    private void createMessagesConfig() {
        messagesConfigFile = new File(getDataFolder(), "messages.yml");
        if (!messagesConfigFile.exists()) {
            messagesConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
    }

    public WorldEditProvider getWorldEdit() {
        return worldEditProvider;
    }

    public WorldGuardProvider getWorldGuard() {
        return worldGuardProvider;
    }

    public PrivateMineFactory getPrivateMineFactory() {
        return privateMineFactory;
    }

    public MineStorage getMineStorage() {
        return mineStorage;
    }

    public WorldManager getWorldManger() {
        return worldManger;
    }

    public UserInterfaceAPI getUserInterfaceAPI() {
        return userInterfaceAPI;
    }
}

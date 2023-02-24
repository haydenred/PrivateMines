package me.hayden.privatemines;

import me.hayden.privatemines.mines.Mine;
import me.hayden.privatemines.mines.MineType;
import me.hayden.privatemines.mines.PrivateMine;
import me.hayden.privatemines.mines.PrivateMineFactory;
import me.hayden.privatemines.storage.MineStorage;
import me.hayden.privatemines.ui.ActionHandler;
import me.hayden.privatemines.ui.UserInterfaceAPI;
import me.hayden.privatemines.utils.version.WorldEditProvider;
import me.hayden.privatemines.utils.version.WorldGuardProvider;
import me.hayden.privatemines.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
                return s;
            }
        };
        userInterfaceAPI = new UserInterfaceAPI(this, actionHandler);

        //Initalize commands
        this.getCommand("privatemines").setExecutor(new Command(this));
        this.getCommand("privatemines").setTabCompleter(new Command(this));


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

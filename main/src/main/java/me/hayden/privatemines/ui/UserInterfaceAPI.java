package me.hayden.privatemines.ui;

import me.hayden.privatemines.ui.requirements.GreaterThanEqualRequirement;
import me.hayden.privatemines.ui.requirements.PermissionRequirement;
import me.hayden.privatemines.ui.requirements.PlaceholderRequirement;
import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class UserInterfaceAPI {

    private final JavaPlugin plugin;
    private final ActionHandler actionHandler;
    private final List<RequirementImplementation> requirements;


    public UserInterfaceAPI(JavaPlugin plugin, ActionHandler actionHandler) {
        this.plugin = plugin;
        this.actionHandler = actionHandler;
        this.requirements = new ArrayList<>();

        registerDefaultRequirements();
        plugin.getServer().getPluginManager().registerEvents(new InventoryManager(this), plugin);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public ActionHandler getActionHandler() {
        return actionHandler;
    }

    public Menu getMenu(ConfigurationSection path) {
        return new Menu(this, path);
    }

    private void registerDefaultRequirements() {
        this.requirements.add(new PermissionRequirement());
        this.requirements.add(new PlaceholderRequirement());
        this.requirements.add(new GreaterThanEqualRequirement());
    }

    public RequirementImplementation getRequirement(ConfigurationSection section) {
        if (section.getName().equalsIgnoreCase("deny-commands")) {
            return null;
        }
        String type = section.getString("type");
        System.out.println("type: " + type);
        List<RequirementImplementation> clonedList = new ArrayList<>(requirements);
        for (RequirementImplementation i : clonedList) {
            if (i.getType().equalsIgnoreCase(type)) {
                return i;
            }
        }
        return null;
    }

}

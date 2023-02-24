package me.hayden.privatemines.ui.requirements.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class RequirementImplementation {

    private ConfigurationSection path;

    public ConfigurationSection getPath() {
        return path;
    }

    public void setPath(ConfigurationSection path) {
        this.path = path;
    }

    public abstract String getType();

    public abstract boolean parseRequirement(Player player);

}

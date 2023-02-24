package me.hayden.privatemines.ui.requirements.impl;

import org.bukkit.configuration.ConfigurationSection;

public class Requirement {

    private final String type;
    private final String input;
    private final String output;
    private final RequirementImplementation implementation;

    public Requirement(ConfigurationSection path, RequirementImplementation implementation) {
        this.type = path.getString("type");
        this.input = path.getString("input");
        this.output = path.getString("output");
        this.implementation = implementation;
    }


    public String getType() {
        return type;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

}

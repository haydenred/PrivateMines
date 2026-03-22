package me.hayden.privatemines.ui.requirements;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hayden.privatemines.ui.requirements.impl.Requirement;
import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import org.bukkit.entity.Player;

public class StringEqualsRequirement extends RequirementImplementation {

    @Override
    public String getType() {
        return "==";
    }

    @Override
    public boolean parseRequirement(Player player) {
        Requirement requirement = new Requirement(getPath(), this);
        String input = PlaceholderAPI.setPlaceholders(player, requirement.getInput());
        return input.equalsIgnoreCase(requirement.getOutput());
    }
}

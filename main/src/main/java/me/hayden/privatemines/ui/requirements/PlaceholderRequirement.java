package me.hayden.privatemines.ui.requirements;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hayden.privatemines.ui.requirements.impl.Requirement;
import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import org.bukkit.entity.Player;

public class PlaceholderRequirement extends RequirementImplementation {
    @Override
    public String getType() {
        return "placeholder";
    }

    @Override
    public boolean parseRequirement(Player player) {
        Requirement requirement = new Requirement(getPath(), this);
        String input = PlaceholderAPI.setPlaceholders(player, requirement.getInput());

        return input.toLowerCase().equals(requirement.getOutput());
    }
}

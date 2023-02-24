package me.hayden.privatemines.ui.requirements;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hayden.privatemines.ui.requirements.impl.Requirement;
import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import org.bukkit.entity.Player;

public class GreaterThanEqualRequirement extends RequirementImplementation {
    @Override
    public String getType() {
        return ">=";
    }

    @Override
    public boolean parseRequirement(Player player) {
        Requirement requirement = new Requirement(getPath(), this);
        Integer input = Math.round(Float.parseFloat(PlaceholderAPI.setPlaceholders(player, requirement.getInput())));

        return input >= Math.round(Float.parseFloat(requirement.getOutput()));
    }
}

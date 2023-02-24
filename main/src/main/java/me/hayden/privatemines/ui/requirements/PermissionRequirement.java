package me.hayden.privatemines.ui.requirements;

import me.hayden.privatemines.ui.requirements.impl.Requirement;
import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import org.bukkit.entity.Player;

public class PermissionRequirement extends RequirementImplementation {


    @Override
    public String getType() {
        return "permission";
    }

    @Override
    public boolean parseRequirement(Player player) {
        Requirement requirement = new Requirement(getPath(), this);
        if (!Boolean.parseBoolean(requirement.getOutput())) {
            return !player.hasPermission(requirement.getInput());
        }
        return player.hasPermission(requirement.getInput());
    }
}

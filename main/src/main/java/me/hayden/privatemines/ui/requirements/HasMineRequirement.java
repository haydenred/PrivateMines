package me.hayden.privatemines.ui.requirements;

import me.hayden.privatemines.Main;
import me.hayden.privatemines.ui.requirements.impl.Requirement;
import me.hayden.privatemines.ui.requirements.impl.RequirementImplementation;
import org.bukkit.entity.Player;

public class HasMineRequirement extends RequirementImplementation {

    @Override
    public String getType() {
        return "has-mine";
    }

    @Override
    public boolean parseRequirement(Player player) {
        Requirement requirement = new Requirement(getPath(), this);
        boolean hasMine = Main.plugin.getMineStorage().hasMine(player.getUniqueId());
        boolean expected = Boolean.parseBoolean(requirement.getOutput());
        return hasMine == expected;
    }
}

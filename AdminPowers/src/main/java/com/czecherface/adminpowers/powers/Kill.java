package com.czecherface.adminpowers.powers;

import com.czecherface.adminpowers.AdminPowers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Kill extends _Power {

    public Kill() {
        super(_PowerEnum.KILL, _Interaction.NONE, 0, 1);
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        if (params.length == 0) {
            Location l = invoker.getLocation();
            invoker.setHealth(0);
            return "Suicide was the way to go! [" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "]";
        }

        Player victim = AdminPowers.ap.getPlayer(params[0]);
        if (victim == null) {
            return "I could not find your victim.";
        }
        Location l = victim.getLocation();
        victim.setHealth(0);
        System.out.println("\n\n[AdminPowers] " + invoker.getName() + " killed " + victim.getName() + "\n\n");
        return "I killed them at [" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "]";
    }
}

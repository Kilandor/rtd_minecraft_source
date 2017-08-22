package com.czecherface.adminpowers.powers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Teleport extends _Power {

    public Teleport() {
        super(_PowerEnum.TELEPORT, _Interaction.NONE, 2, 3);
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        int x, y, z;
        try {
            x = Integer.parseInt(params[0]);
            y = Integer.parseInt(params[1]);
            if (params.length == 2) {
                z = y;
                y = -1;
            } else {
                z = Integer.parseInt(params[2]);
            }
        } catch (NumberFormatException ex) {
            return "Bad coordinates specified.";
        }

        if (y < 0) {
            World w = invoker.getWorld();
            y = w.getHighestBlockYAt(x, z);
            w.loadChunk(w.getBlockAt(x, y, z).getChunk());
            y += 2;
        }
        invoker.teleport(new Location(invoker.getWorld(), x + 0.5, y, z + 0.5));
        return "Whoosh!  Teleported to [" + x + "," + y + "," + z + "].";
    }
}

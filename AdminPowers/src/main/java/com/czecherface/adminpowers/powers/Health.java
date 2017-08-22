package com.czecherface.adminpowers.powers;

import org.bukkit.entity.Player;

import com.czecherface.adminpowers.AdminPowers;

/**
 * I DO NOT ALLOW SETTING ARBITRARY HEALTH ON OTHER PLAYERS!
 * IT'S MEAN AND ANNOYING SOMETIMES SO FUCK OFF >:P
 */
public class Health extends _Power {

    public Health() {
        super(_PowerEnum.HEALTH, _Interaction.NONE, 0, 1);
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        if (params.length == 0) {
            invoker.setHealth(20);
            return "Boom! Full health.";
        }

        Integer health;
        try {
            health = Integer.parseInt(params[0]);
        } catch (NumberFormatException nfe) {
            //See if they tried to give health to a player
            Player target = AdminPowers.ap.getPlayer(params[0]);
            if (target == null) {
                return "I want an integer value please.";
            }
            target.setHealth(20);
            return "Well, that was nice of you :D";
        }

        if (health < 1) {
            return "Value must be positive. Just use \"/ap kill\" instead.";
        }
        if (health > 200) {
            health = 200;
        }

        invoker.setHealth(health);
        return "Health set to {green}" + health + "{white}.";
    }
}

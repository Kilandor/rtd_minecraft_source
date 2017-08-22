package com.czecherface.adminpowers.powers;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Weather extends _Power {

    public Weather() {
        super(_PowerEnum.WEATHER, _Interaction.NONE, 1, 2);
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (invoker == null) {
            throw new RuntimeException("Invoker cannot be null!");
        }
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        if (params[0].length() != 1) {
            return "The weather type must be one character.";
        }

        char type = Character.toLowerCase(params[0].charAt(0)); //Weather type
        if (type != 'n' && type != 's' && type != 't') {
            return "The weather type must be one of n, s, or t.";
        }

        int duration = 0;
        if (params.length == 2) {
            try {
                duration = Integer.parseInt(params[1]);
            } catch (NumberFormatException nfe) {
                return "Bad duration specified!";
            }
        }

        World world = invoker.getWorld();
        switch (type) {
            case 'n':
                world.setStorm(false);
                world.setThundering(false);
                if (duration != 0) {
                    world.setWeatherDuration(duration);
                }
                return "Rain, rain, go away, come again another day!";
            case 't':
                world.setStorm(true);
                world.setThundering(true);
                if (duration != 0) {
                    world.setWeatherDuration(duration);
                    world.setThunderDuration(duration);
                }
                return "Beware, electrical storm rolling in!?";
            case 's':
                world.setStorm(true);
                if (duration != 0) {
                    world.setWeatherDuration(duration);
                }
                world.setThundering(false);
                return "Should be storming in a few seconds.";
            default:
                return "The weather type must be one of n, s, or t.";
        }
    }
}

package com.czecherface.adminpowers.powers;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Jump extends _Power {

    private Player invoker;
    private double jumpPower;
    private boolean lunge;

    public Jump() {
        super(_PowerEnum.JUMP, _Interaction.LEFT_CLICK, 1, 2);
    }

    @Override
    public String getStatus() {
        return (lunge ? "{red}Lunge": "{blue}Jump") + "{white} activated at power level {red}" + jumpPower + "{white}.";
    }

    @Override
    public void activate(_Interaction action) {
        if (lunge)
        {
            Vector direction = invoker.getLocation().getDirection();
            direction.normalize().multiply(jumpPower);
            //Add a fraction of to the up direction so the player doesn't have to look up as much
            direction.setY(direction.getY() + jumpPower * .1);
            invoker.setVelocity(direction);
            return;
        }
        
        Vector vel = invoker.getVelocity();
        vel.setY(vel.getY() + jumpPower);
        invoker.setVelocity(vel);
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        this.invoker = invoker;
        
        try {
            jumpPower = Double.parseDouble(params[0]);
        } catch (NumberFormatException nfe) {
            return "Give me numbers please :/";
        }
        
        lunge = params.length > 1 ? params[1].equalsIgnoreCase("true") : false;
        
        if (jumpPower <= 0)
            return "Your Up value must be {green}positive{white}.";
        if (jumpPower > 6)
            return "Be realistic, you don't need to go above {red}6{white}.";
        return null;
    }
}

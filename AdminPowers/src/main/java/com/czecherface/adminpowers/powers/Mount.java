package com.czecherface.adminpowers.powers;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Mount extends _Power {
    private Player invoker;
    
    public Mount()
    {
        super(_PowerEnum.MOUNT, _Interaction.BOTH_CLICK, 0, 0);
    }

    @Override
    public String getStatus()
    {
        return "{blue}Mount{white} is now {green}ON{white}.";
    }

    @Override
    public void activate(_Interaction action)
    {
        List<Entity> entities = invoker.getNearbyEntities(4.0, 4.0, 4.0);
        for (Entity e : entities)
        {
            if (action == _Interaction.LEFT_CLICK)
            {
                if (e.getPassenger() != null && e.getPassenger().equals(invoker))
                    continue;
            }
            else if (invoker.getPassenger() != null && invoker.getPassenger().equals(e))
                continue;
            
            if (action == _Interaction.LEFT_CLICK)
                invoker.setPassenger(e);
            else
                e.setPassenger(invoker);
        }
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length))
            return getArgumentRequirementString();
        this.invoker = invoker;
        return null;
    }
    
    public Entity getPassenger()
    {
        return invoker.getPassenger();
    }
}

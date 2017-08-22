package com.czecherface.adminpowers;

import com.czecherface.adminpowers.powers.Mount;
import com.czecherface.adminpowers.powers._Power;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;

import com.czecherface.adminpowers.powers._PowerEnum;
import org.bukkit.entity.Entity;

public class EntityListener extends org.bukkit.event.entity.EntityListener {

    private final AdminPowers plugin;

    public EntityListener(AdminPowers instance) {
        plugin = instance;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        
        //First we are going to check the special case on if an admin used
        //the Mount command on an entity.
        Mount[] mount_powers = plugin.getPowers(Mount.class);
        for (Mount m : mount_powers)
            if (m.getPassenger() == e)
            {
                event.setCancelled(true);
                return;
            }
        
        //Now we only care about admins and their powers from here on
        if (!(e instanceof Player)) {
            return;
        }

        Player player = (Player) e;
        _Power power = plugin.getPlayerPower(player.getName());
        if (power == null)
            return;
        
        _PowerEnum pe = power.getEnum();
        switch (event.getCause()) {
            case FALL:
                if (pe == _PowerEnum.JUMP) {
                    event.setCancelled(true);
                }
                break;
            case LIGHTNING:
                if (pe == _PowerEnum.LIGHTNING) {
                    event.setCancelled(true);
                }
                break;
        }
    }
}

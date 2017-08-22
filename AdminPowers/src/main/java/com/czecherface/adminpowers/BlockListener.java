package com.czecherface.adminpowers;

import com.czecherface.adminpowers.powers.Place;
import com.czecherface.adminpowers.powers.Replace;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockListener extends org.bukkit.event.block.BlockListener {
    
    private final AdminPowers plugin;

    public BlockListener(AdminPowers instance) {
        plugin = instance;
    }
    
    @Override
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        Block block = event.getBlock();
        Place[] place_powers = plugin.getPowers(Place.class);
        Replace[] replace_powers = plugin.getPowers(Replace.class);
        Block[] moddedBlocks = new Block[place_powers.length + replace_powers.length];
        for (int i = 0; i < place_powers.length; i++)
            moddedBlocks[i] = place_powers[i].getLastPlacedBlock();
        for (int i = 0; i < replace_powers.length; i++)
            moddedBlocks[place_powers.length + i] = replace_powers[i].getLastReplacedBlock();
        
        for (BlockFace f : Constants.MAIN_FACES)
        {
            Block relative = block.getRelative(f);
            for (int i = 0; i < moddedBlocks.length; i++)
                if (relative.equals(moddedBlocks[i]))
                {
                    event.setCancelled(true);
                    return;
                }
        }
    }
}

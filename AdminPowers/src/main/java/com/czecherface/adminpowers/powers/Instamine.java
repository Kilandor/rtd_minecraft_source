package com.czecherface.adminpowers.powers;

import com.czecherface.adminpowers.Constants;
import java.util.HashSet;
import java.util.LinkedList;

import java.util.Queue;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Instamine extends _Power {

    private static final int MIN_CHAIN = 1, MAX_CHAIN = 60;
    private static final Material[] UNMINABLE = {
        Material.AIR,
        Material.WATER, Material.STATIONARY_WATER,
        Material.LAVA, Material.STATIONARY_LAVA,
        Material.CHEST
    };
    
    private Player invoker;
    private boolean breakBedrock;
    private int chainDistance;

    public Instamine() {
        super(_PowerEnum.INSTAMINE, _Interaction.LEFT_CLICK, 0, 1);
    }

    @Override
    public String getStatus() {
        return "{blue}Instamine{white} set to "
                + (chainDistance != 1 ? "{green}" + chainDistance : breakBedrock ? "{red}FULL POWER" : "{green}HALF POWER")
                + "{white}.";
    }

    @Override
    public void activate(_Interaction action) {
        Block targetBlock = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.LONG.value);
        Material m = targetBlock.getType();
        if (m == Material.BEDROCK && !breakBedrock)
            return;
        for (Material m2 : UNMINABLE)
            if (m == m2)
                return;
        
        byte data = targetBlock.getData();
        Queue<Block> toMine = new LinkedList<Block>();
        HashSet<Block> inQueue = new HashSet<Block>();
        toMine.add(targetBlock);
        inQueue.add(targetBlock);
        int blocksMined = 0;
        for (int currentDistance = 0, elementsAdded = 1; currentDistance < chainDistance; currentDistance++)
        {
            int passes = elementsAdded;
            elementsAdded = 0;
            for (int pass = 0; pass < passes; pass++)
            {
                Block b = toMine.poll();
                inQueue.remove(b);
                b.setType(Material.AIR);
                blocksMined++;
                
                for (BlockFace f : Constants.MAIN_FACES)
                {
                    Block toAdd = b.getRelative(f);
                    if (toAdd.getType() == m && inQueue.add(toAdd))
                    {
                        toMine.add(toAdd);
                        elementsAdded++;
                    }
                }
            }
        }
        
        targetBlock.getWorld().dropItemNaturally(targetBlock.getLocation(), new ItemStack(m, blocksMined, data));
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        chainDistance = 1;
        this.invoker = invoker;

        if (params.length == 0)
            return null;
        
        try {
            chainDistance = Integer.parseInt(params[0]); 
        } catch (NumberFormatException nfe) {
            if (params[0].equalsIgnoreCase("true"))
            {
                breakBedrock = true;
                return null;
            }
            return "I Could not understand the supplied argument, try again.";
        }
        
        if (chainDistance < MIN_CHAIN || chainDistance > MAX_CHAIN)
            return "The chain distance must be between "+MIN_CHAIN+" and "+MAX_CHAIN+" inclusively.";
        return null;
    }
}

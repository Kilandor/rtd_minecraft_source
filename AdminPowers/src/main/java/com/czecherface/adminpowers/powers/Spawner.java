package com.czecherface.adminpowers.powers;

import com.czecherface.adminpowers.Constants;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Spawner extends _Power {
    private Player invoker;
    private CreatureType creature;
    private Random rand;

    public Spawner()
    {
        super(_PowerEnum.SPAWNER, _Interaction.RIGHT_CLICK, 1, 1);
    }

    @Override
    public String getStatus()
    {
        return "{blue}Spawner{white} set to {red}"+creature.getName()+"{white}.";
    }

    @Override
    public void activate(_Interaction action)
    {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.LONG.value);
        Block above = b.getRelative(BlockFace.UP);
        if (b.getType() == Material.AIR || b.getType() == Material.BEDROCK || above.getType() != Material.AIR)
                return;
        LivingEntity le = invoker.getWorld().spawnCreature(above.getLocation(), creature);
        double x = (rand.nextInt(200) - 100) / 100;
        double y = rand.nextInt(100) / 100;
        double z = (rand.nextInt(200) - 100) / 100;
        le.setVelocity(new Vector(x, y, z));
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length))
                return getArgumentRequirementString();

        rand = new Random();
        this.invoker = invoker;

        try {
                int chosen = Integer.parseInt(params[0]);
                CreatureType[] values = CreatureType.values();
                if (chosen < 0 || chosen > values.length)
                        throw new NumberFormatException();
                creature = values[chosen];
        } catch (NumberFormatException nfe) {
                return "Your input is bad or that creature ID does not exist!";
        }

        return null;
    }
}

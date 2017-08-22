package com.czecherface.adminpowers.powers;

import com.czecherface.adminpowers.Constants;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class ClickPort extends _Power {

    private Player invoker;

    public ClickPort() {
        super(_PowerEnum.CLICKPORT, _Interaction.RIGHT_CLICK, 0, 0);
    }

    @Override
    public String getStatus() {
        return "{blue}ClickPort{white} is {green}ON{white}.";
    }

    @Override
    public void activate(_Interaction action) {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.VISION.value);
        if (b.getType() != Material.AIR && b.getType() != Material.BEDROCK) {
            if (clickPortValid(b)) {
                Location invokerLoc = invoker.getLocation();
                Location teleportLoc = b.getRelative(BlockFace.UP).getLocation();
                teleportLoc.setX(teleportLoc.getX() + 0.5);
                teleportLoc.setZ(teleportLoc.getZ() + 0.5);
                teleportLoc.setPitch(invokerLoc.getPitch());
                teleportLoc.setYaw(invokerLoc.getYaw());
                invoker.teleport(teleportLoc);
            } else {
                invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
            }
        } else {
            invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
        }
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }
        this.invoker = invoker;
        return null;
    }
    /**
     * Used to determine if a player can clickport to a block.
     */
    static final Material[] allowedBlocks = {
        Material.AIR,
        Material.WATER,
        Material.POWERED_RAIL,
        Material.DETECTOR_RAIL,
        Material.YELLOW_FLOWER,
        Material.RED_ROSE,
        Material.BROWN_MUSHROOM,
        Material.RED_MUSHROOM,
        Material.TORCH,
        Material.FIRE,
        Material.REDSTONE_WIRE,
        Material.CROPS,
        Material.SIGN,
        Material.SIGN_POST,
        Material.LEVER,
        Material.LADDER,
        Material.RAILS,
        Material.REDSTONE_TORCH_OFF,
        Material.REDSTONE_TORCH_ON,
        Material.DIODE_BLOCK_OFF,
        Material.DIODE_BLOCK_ON,
        Material.SNOW
    };

    public boolean clickPortValid(Block b) {
        Block t1 = b.getRelative(BlockFace.UP);
        Block t2 = t1.getRelative(BlockFace.UP);

        boolean t1_found = false, t2_found = false;
        for (Material m : allowedBlocks) {
            if (t1.getType() == m) {
                t1_found = true;
            }
            if (t2.getType() == m) {
                t2_found = true;
            }
        }
        return t1_found && t2_found;
    }
}

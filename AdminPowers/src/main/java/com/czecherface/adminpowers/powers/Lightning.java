package com.czecherface.adminpowers.powers;

import com.czecherface.adminpowers.Constants;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Lightning extends _Power {

    private Player invoker;
    private boolean superStrike;

    public Lightning() {
        super(_PowerEnum.LIGHTNING, _Interaction.LEFT_CLICK, 0, 1);
    }

    @Override
    public String getStatus() {
        return "{blue}Lightning{white} " + (superStrike ? "with {red}Super Strike{white} " : "") + "enabled.";
    }

    @Override
    public void activate(_Interaction action) {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.VISION.value);
        if (b.getY() < 4) {
            return;
        }

        World w = invoker.getWorld();
        w.strikeLightning(b.getLocation());

        if (!superStrike) {
            return;
        }

        int distance = 3;
        for (int x = -distance; x < distance; x++) {
            for (int z = -distance; z < distance; z++) {
                Block block = b.getRelative(x, 0, z);
                if (block.getType() == Material.CHEST) {
                    continue;
                }
                w.strikeLightningEffect(block.getLocation());
            }
        }
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        this.invoker = invoker;

        if (params.length == 1) {
            superStrike = params[0].equalsIgnoreCase("true");
        }

        return null;
    }
}

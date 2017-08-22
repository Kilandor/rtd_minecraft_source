package com.czecherface.adminpowers.powers;

import com.czecherface.adminpowers.*;
import com.kilandor.chat.Chat;
import java.util.List;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Place extends _Power {

    private Player invoker;
    private TrueMaterial material;
    private Block lastPlaced, alterWith;

    public Place() {
        super(_PowerEnum.PLACE, _Interaction.BOTH_CLICK, 1, 1);
    }

    @Override
    public String getStatus() {
        return "{blue}Place{white} turned on with {green}" + material.name() + "{white}.";
    }

    @Override
    public void activate(_Interaction action) {
        List<Block> l = invoker.getLastTwoTargetBlocks(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.LONG.value);
        if (l.size() < 2 || l.get(1).getY() < 3) {
            return;
        }
        lastPlaced = l.get(0);
        Block endTarget = l.get(1);
        if (action == _Interaction.LEFT_CLICK) {
            alterWith = null;
            material.applyTo(lastPlaced);
        } else if (action == _Interaction.RIGHT_CLICK) {
            Chat chat = AdminPowers.ap.getChat();
            if (alterWith == null || !alterWith.equals(endTarget))
            {
                alterWith = endTarget;
                chat.playerMsg(invoker, Constants.TITLE, "Right-click again to set with " + TrueMaterial.getAsString(endTarget) + ".", false);
            }
            else
            {
                material = TrueMaterial.get(endTarget);
                alterWith = null;
                chat.playerMsg(invoker, Constants.TITLE, getStatus(), false);
            }
            invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
        }
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }
        this.invoker = invoker;

        material = TrueMaterial.get(params[0]);
        if (material == null) {
            return "Could not interpret the material ID for placement.";
        } else if (material.equals(Material.BEDROCK)) {
            return "Material ID specified for the placement not allowed.";
        }
        return null;
    }
    
    public Block getLastPlacedBlock()
    {
        return lastPlaced;
    }
}

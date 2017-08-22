package com.czecherface.adminpowers.powers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.czecherface.adminpowers.AdminPowers;
import com.czecherface.adminpowers.Constants;

public class BlockData extends _Power {

    private Player invoker;
    private byte data;

    public BlockData() {
        super(_PowerEnum.BLOCKDATA, _Interaction.BOTH_CLICK, 1, 1);
    }

    @Override
    public String getStatus() {
        return "{blue}BlockData{white} modifier set to {green}" + data + "{white}.";
    }

    @Override
    public void activate(_Interaction action) {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.MEDIUM.value);
        Material m = b.getType();
        if (m == Material.CHEST || m == Material.BEDROCK) {
            return;
        }

        if (action == _Interaction.LEFT_CLICK) {
            b.setData(data);
        } else if (action == _Interaction.RIGHT_CLICK) {
            AdminPowers.ap.getChat().playerMsg(invoker, Constants.TITLE, "{blue}BlockData{white} of this block is {red}" + b.getData() + "{white}.", false);
        }
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }

        this.invoker = invoker;

        try {
            data = Byte.parseByte(params[0]);
        } catch (NumberFormatException nfe) {
            return "Bad data specified!";
        }

        return null;
    }
}
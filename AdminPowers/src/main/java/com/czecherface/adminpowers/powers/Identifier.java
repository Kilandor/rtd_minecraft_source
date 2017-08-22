package com.czecherface.adminpowers.powers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.czecherface.adminpowers.AdminPowers;
import com.czecherface.adminpowers.Constants;

public class Identifier extends _Power {

    Player invoker;

    public Identifier() {
        super(_PowerEnum.IDENTIFIER, _Interaction.RIGHT_CLICK, 0, 0);
    }

    @Override
    public String getStatus() {
        return "{blue}Identifier{white} is {green}ON{white}.";
    }

    @Override
    public void activate(_Interaction action) {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.VISION.value);
        String s = b == null ? "No block encountered." : AdminPowers.blockToString(b, true);
        AdminPowers.ap.getChat().playerMsg(invoker, Constants.TITLE, s, false);
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }
        this.invoker = invoker;
        return null;
    }
}

package com.czecherface.adminpowers.powers;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.kilandor.chat.Chat;

import com.czecherface.adminpowers.*;
import org.bukkit.Effect;

public class Fill extends _Power {
    private Player invoker;
    private boolean overrideLimit;
    private TrueMaterial fillWith, replaceOnly;
    private Block selectedBlock, alterFill;

    public Fill() {
        super(_PowerEnum.FILL, _Interaction.BOTH_CLICK, 1, 3);
    }

    @Override
    public String getStatus() {
        return "{blue}Fill{white} turned on with {green}" + fillWith.name() + "{white}" + (replaceOnly != null ? " replace {red}" + replaceOnly.name() + "{white}." : ".") + (overrideLimit ? " {red}(No Limit){white}" : "");
    }

    @Override
    public void activate(_Interaction action) {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.MEDIUM.value);
        Material m = b.getType();
        if (b.getY() < 2 || m == Material.AIR || m == Material.WATER || m == Material.STATIONARY_WATER || m == Material.LAVA || m == Material.STATIONARY_LAVA) {
            return;
        }

        Chat chat = AdminPowers.ap.getChat();
        if (action == _Interaction.LEFT_CLICK) {
            alterFill = null;
            if (selectedBlock == b)
            {
                chat.playerMsg(invoker, Constants.TITLE, "First Block {blue}Cleared{white}: " + AdminPowers.blockToString(selectedBlock), false);
                selectedBlock = null;
            }
            else
            {
                String toChat = "First Block " + (selectedBlock == null ? "{green}Selected" : "{red}Overriden") + "{white}: " + AdminPowers.blockToString(b);
                chat.playerMsg(invoker, Constants.TITLE, toChat, false);
                selectedBlock = b;
            }
        } else if (action == _Interaction.RIGHT_CLICK) {
            if (selectedBlock == null) {
                if (alterFill == null || !alterFill.equals(b))
                {
                    alterFill = b;
                    chat.playerMsg(invoker, Constants.TITLE, "Right-click again to fill with " + TrueMaterial.getAsString(b) + ".", false);
                }
                else
                {
                    fillWith = TrueMaterial.get(b);
                    alterFill = null;
                    chat.playerMsg(invoker, Constants.TITLE, getStatus(), false);
                    //Give some feedback if the interaction worked properly.
                    invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
                }
                return;
            }
            if (!selectedBlock.getWorld().equals(b.getWorld())) {
                chat.playerMsg(invoker, Constants.TITLE, "The fill cannot stretch over worlds, dummy...", false);
                return;
            }

            int x1 = selectedBlock.getX(), x2 = b.getX();
            int y1 = selectedBlock.getY(), y2 = b.getY();
            int z1 = selectedBlock.getZ(), z2 = b.getZ();
            boolean xmod = x1 < x2;
            boolean ymod = y1 < y2;
            boolean zmod = z1 < z2;

            if (!overrideLimit && ((long) Math.abs(x1 - x2)) * ((long) Math.abs(y1 - y2)) * ((long) Math.abs(z1 - z2)) >= Constants.BLOCK_FILL_LIMIT) {
                chat.playerMsg(invoker, Constants.TITLE, "The fill volume is too large; try again or set the override to true.", false);
                return;
            }

            //If I don't do this here, then Block b will be altered later when I call createStringFromBlock(b)
            String textToPlayer = "Endpoint Defined: " + AdminPowers.blockToString(b) + ". {green}Success!{white}.";

            World w = selectedBlock.getWorld();
            for (int x = x1; xmod ? x <= x2 : x >= x2; x += (xmod ? 1 : -1)) {
                for (int y = y1; ymod ? y <= y2 : y >= y2; y += (ymod ? 1 : -1)) {
                    for (int z = z1; zmod ? z <= z2 : z >= z2; z += (zmod ? 1 : -1)) {
                        Block block = w.getBlockAt(x, y, z);
                        if (block.getType() == Material.BEDROCK || block.getType() == Material.CHEST || (replaceOnly != null && !replaceOnly.equals(block))) {
                            continue;
                        }
                        fillWith.applyTo(block);
                    }
                }
            }

            chat.playerMsg(invoker, Constants.TITLE, textToPlayer, false);
            selectedBlock = null;
        }
        
        //Give some feedback if the interaction worked properly.
        invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }
        this.invoker = invoker;

        fillWith = TrueMaterial.get(params[0]);
        if (fillWith == null) {
            return "Could not interpret the material for fill.";
        } else if (fillWith.equals(Material.BEDROCK)) {
            return "Material ID specified for the fill not allowed.";
        }

        if (params.length < 2) {
            return null;
        }

        replaceOnly = TrueMaterial.get(params[1]);
        if (replaceOnly == null) {
            if (!params[1].equalsIgnoreCase("true")) {
                return "Could not interpret the material for replacement.";
            } else {
                overrideLimit = true;
            }
        } else if (replaceOnly.equals(Material.BEDROCK)) {
            return "Material ID specified for the replace not allowed.";
        }

        if (params.length < 3 || overrideLimit == true) {
            return null;
        }

        overrideLimit = params[2].equalsIgnoreCase("true");
        return null;
    }
}

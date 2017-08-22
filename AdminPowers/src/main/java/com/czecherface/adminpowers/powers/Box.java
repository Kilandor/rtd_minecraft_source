package com.czecherface.adminpowers.powers;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.kilandor.chat.Chat;

import com.czecherface.adminpowers.*;
import org.bukkit.Effect;

public class Box extends _Power
{
    private Player invoker;
    private boolean overrideLimit;
    private TrueMaterial outer, inner;
    private Block selectedBlock;

    public Box() {
        super(_PowerEnum.BOX, _Interaction.BOTH_CLICK, 1, 3);
    }

    @Override
    public String getStatus() {
        return "{blue}Box{white} turned on with {green}" + outer.name() + "{white}" + (inner != null ? " and {red}" + inner.name() + "{white}." : ".") + (overrideLimit ? " {red}(No Limit){white}" : "");
    }

    @Override
    public void activate(_Interaction action) {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.MEDIUM.value);
        Material m = b.getType();
        if (m == Material.AIR || m == Material.WATER || m == Material.STATIONARY_WATER || m == Material.LAVA || m == Material.STATIONARY_LAVA) {
            return;
        }

        Chat chat = AdminPowers.ap.getChat();
        if (b.getY() < 4) {
            chat.playerMsg(invoker, Constants.TITLE, "Please choose a block higher up.", false);
        }

        if (action == _Interaction.LEFT_CLICK) {
            if (selectedBlock != null) {
                chat.playerMsg(invoker, Constants.TITLE, "First Block Overriden: " + AdminPowers.blockToString(b), false);
            } else {
                chat.playerMsg(invoker, Constants.TITLE, "First Block Selected: " + AdminPowers.blockToString(b), false);
            }
            selectedBlock = b;
            invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
        } else if (action == _Interaction.RIGHT_CLICK) {
            if (selectedBlock == null) {
                chat.playerMsg(invoker, Constants.TITLE, "Left-click instead to select the first block.", false);
                return;
            }
            if (!selectedBlock.getWorld().equals(b.getWorld())) {
                chat.playerMsg(invoker, Constants.TITLE, "The box cannot stretch over worlds, dummy...", false);
                return;
            }

            int x1 = selectedBlock.getX(), x2 = b.getX();
            int y1 = selectedBlock.getY(), y2 = b.getY();
            int z1 = selectedBlock.getZ(), z2 = b.getZ();
            int xabs = Math.abs(x1 - x2);
            int yabs = Math.abs(y1 - y2);
            int zabs = Math.abs(z1 - z2);

            if (xabs < 3 || yabs < 3 || zabs < 3) {
                chat.playerMsg(invoker, Constants.TITLE, "The box (x,y,z) must be at least 4x4x4.", false);
                return;
            }

            if (!overrideLimit && ((long) xabs) * ((long) yabs) * ((long) zabs) >= Constants.BLOCK_FILL_LIMIT) {
                chat.playerMsg(invoker, Constants.TITLE, "The box volume is too large; try again or set the override to true.", false);
                return;
            }

            //Swap the values into order
            if (x1 > x2) {
                int t = x1;
                x1 = x2;
                x2 = t;
            }
            if (y1 > y2) {
                int t = y1;
                y1 = y2;
                y2 = t;
            }
            if (z1 > z2) {
                int t = z1;
                z1 = z2;
                z2 = t;
            }

            World w = selectedBlock.getWorld();
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        Block block = w.getBlockAt(x, y, z);
                        if (block == null || block.getType() == Material.BEDROCK || block.getType() == Material.CHEST) {
                            continue;
                        }
                        if (x == x1 || y == y1 || z == z1 || x == x2 || y == y2 || z == z2) {
                            outer.applyTo(block);
                        } else if (inner != null && ((x == x1 + 1 && y == y1 + 1) || (x == x1 + 1 && z == z1 + 1) || (y == y1 + 1 && z == z1 + 1) || (x == x2 - 1 && y == y2 - 1) || (x == x2 - 1 && z == z2 - 1) || (y == y2 - 1 && z == z2 - 1))) {
                            inner.applyTo(block);
                        } else {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }

            chat.playerMsg(invoker, Constants.TITLE, "Endpoint Defined: " + AdminPowers.blockToString(b) + ". {green}Success!{white}.", false);
            selectedBlock = null;
            invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
        }
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length)) {
            return getArgumentRequirementString();
        }
        this.invoker = invoker;

        outer = TrueMaterial.get(params[0]);
        if (outer == null) {
            return "Material for outer box could not be recognized.";
        } else if (outer.equals(Material.BEDROCK)) {
            return "Material ID specified for the outer box not allowed.";
        }

        if (params.length < 2) {
            return null;
        }

        inner = TrueMaterial.get(params[1]);
        if (inner == null) {
            if (!params[1].equalsIgnoreCase("true")) {
                return "Material for inner box could not be recognized.";
            } else {
                overrideLimit = true;
            }
        } else if (inner.equals(Material.BEDROCK)) {
            return "Material ID specified for the inner box not allowed.";
        }

        if (params.length < 3 || overrideLimit == true) {
            return null;
        }

        overrideLimit = params[2].equalsIgnoreCase("true");
        return null;
    }
}

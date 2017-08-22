package com.czecherface.adminpowers.powers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.czecherface.adminpowers.*;
import com.kilandor.chat.Chat;
import java.util.Random;
import org.bukkit.Effect;

public class Replace extends _Power {

    private Player invoker;
    private Random random;
    private TrueMaterial replaceWith, replaceOnly;
    private Block lastReplacedBlock, alterWith;

    public Replace() {
        super(_PowerEnum.REPLACE, _Interaction.BOTH_CLICK, 1, 2);
    }

    @Override
    public String getStatus() {
        if (random != null) {
            final String[] options = {
                "{blue}", "{green}", "{aqua}", 
                "{darkblue}", "{darkgreen}", "{darkaqua}",
                "{red}", "{lightpurple}", "{gold}",
                "{darkred}", "{darkpurple}", "{yellow}"
            };
            String toReturn = "{blue}Replace{white} turned on ";
            for (int i = 0; i < "Random".length(); i++)
                toReturn += options[random.nextInt(options.length)] + "Random".charAt(i);
            return toReturn + "{white}.";            
        }
        return "{blue}Replace{white} turned on with {green}" + replaceWith.name() + "{white}" + (replaceOnly != null ? " and {red}" + replaceOnly.name() + "{white}." : ".");
    }

    @Override
    public void activate(_Interaction action) {
        Block b = invoker.getTargetBlock(Constants.LOS_TRACE_TRANSPARENT_MATERIALS, _Range.MEDIUM.value);
        Material m = b.getType();
        if (action == _Interaction.LEFT_CLICK) {
            if (m == Material.CHEST)
                return;
            alterWith = null;
            if (random != null)
            {
                lastReplacedBlock = b;
                b.setType(getRandomMaterial());
                return;
            }
            if ((replaceOnly == null || replaceOnly.equals(m)) && m != Material.AIR && m != Material.BEDROCK) {
                lastReplacedBlock = b;
                replaceWith.applyTo(b);
            }
        } else if (action == _Interaction.RIGHT_CLICK) {
            Chat chat = AdminPowers.ap.getChat();
            if (alterWith == null || !alterWith.equals(b))
            {
                alterWith = b;
                chat.playerMsg(invoker, Constants.TITLE, "Right-click again to set with " + TrueMaterial.getAsString(b) + ".", false);
            }
            else
            {
                replaceWith = TrueMaterial.get(b);
                alterWith = null;
                chat.playerMsg(invoker, Constants.TITLE, getStatus(), false);
            }
            invoker.playEffect(invoker.getLocation(), Effect.CLICK1, 0);
        }
    }

    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length))
            return getArgumentRequirementString();
        this.invoker = invoker;
        
        if (params[0].equals("true"))
        {
            random = new Random();
            return null;
        }

        replaceWith = TrueMaterial.get(params[0]);
        if (replaceWith == null)
            return "Could not interpret the material ID for replacement.";
        else if (replaceWith.equals(Material.BEDROCK))
            return "Material ID specified for the replacement not allowed.";

        if (params.length < 2)
            return null;

        replaceOnly = TrueMaterial.get(params[1]);
        if (replaceOnly == null)
            return "Could not interpret the material ID for replacement.";
        else if (replaceOnly.equals(Material.BEDROCK))
            return "Material ID specified for the replacement not allowed.";
        return null;
    }
    
    public Block getLastReplacedBlock()
    {
        return lastReplacedBlock;
    }
    
    public static final int[] ALLOWED_MATERIALS = {
        1, 2, 3, 4, 5, 6, 12, 14, 15, 16,
        17, 18, 20, 21, 22, 23, 24, 25, 29, 33,
        35, 37, 38, 39, 40, 41, 42, 43, 44, 45,
        46, 47, 48, 49, 53, 56, 57, 58, 61, 67,
        73, 80, 81, 82, 84, 85, 86, 87, 88, 89,
        91
    };
    
    private Material getRandomMaterial()
    {
        return Material.getMaterial(ALLOWED_MATERIALS[random.nextInt(ALLOWED_MATERIALS.length)]);
    }
}

package com.czecherface.adminpowers.powers;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class _Power {

    private _PowerEnum powerEnum;
    private _Interaction interaction;
    private int minArguments, maxArguments;
    private boolean mustCleanUp;

    /****************/
    /* Constructors */
    /****************/
    protected _Power(_PowerEnum p, _Interaction i) {
        this(p, i, 0, 0);
    }

    protected _Power(_PowerEnum p, _Interaction i, int argsRequired) {
        this(p, i, argsRequired, argsRequired);
    }

    protected _Power(_PowerEnum p, _Interaction i, int minArgs, int maxArgs) {
        //Consistency checks
        if (p == null) {
            throw new NullPointerException("A power does not tie to some _PowerEnum!");
        }
        if (i == null) {
            throw new NullPointerException("Interaction type is null for \"" + p.getName() + "\"!");
        }
        if (minArgs < 0 || maxArgs < 0) {
            throw new RuntimeException("Argument count must be non-negative for \"" + p.getName() + "\"!");
        }
        if (maxArgs < minArgs) {
            throw new RuntimeException("Argument minimum is greater than the maximum for \"" + p.getName() + "\"!");
        }

        powerEnum = p;
        interaction = i;
        minArguments = minArgs;
        maxArguments = maxArgs;
    }

    /************************************/
    /* Abstract / Override-able methods */
    /************************************/
    /**
     * WARNING: DO NOT USE AN INSTANCE OF _Power OR CALL ANY OTHER METHOD FROM IT (other than setData(...)) IF THIS METHOD RETURNS A NON-NULL STRING.  The object's integrity is not guaranteed after such an event.
     * @return Null on success, error string otherwise.
     */
    public abstract String setData(Player invoker, String[] params);

    /**
     * Should never be called for powers that have Interaction.NONE set for 'interaction' as such classes shouldn't need to use this method.  Usually, the command itself triggers the power once and then the instance is left to die like in Health and Weather.
     * @param action If a power accepts more than one type of interaction (as defined by its personal constructor), then this is the variable to check for such a clarification.
     */
    public void activate(_Interaction action) {
    }

    /**
     * Should only be called for powers that have their interaction type set to Interaction.ITEM_DROP.
     * @param stack The ItemStack that was dropped by the player.
     * @return true if this power should be removed, false otherwise.
     */
    public boolean dropped(Material m) {
        return false;
    }

    /**
     * Only call after setData(Player,String[]) returns TRUE!
     * If the interaction type for this power is NONE, then DO NOT OVERRIDE this method.
     * @return The status (configuration) of this power.
     */
    public String getStatus() {
        return null;
    }

    /**
     * If you ever store information and require a cleanup before this
     * power can be removed, then call this method in your power class
     * and implement the cleanUp() method.
     */
    protected void setMustCleanUp(boolean b) {
        mustCleanUp = b;
    }

    public boolean mustCleanUp() {
        return mustCleanUp;
    }

    public void cleanUp() {
        throw new RuntimeException("Could not clean up " + powerEnum + ", please implement the method!");
    }

    /* Base methods */
    public _PowerEnum getEnum() {
        return powerEnum;
    }

    public _Interaction getAcceptedInteraction() {
        return interaction;
    }

    protected boolean validArgumentCount(int count) {
        return minArguments <= count && count <= maxArguments;
    }

    protected String getArgumentRequirementString() {
        String name = powerEnum.getName();
        if (maxArguments == 0) {
            return name + " does not accept arguments.";
        }
        if (minArguments == maxArguments) {
            return "You must provide {green}" + minArguments + "{white} argument" + (minArguments > 1 ? "s" : "") + " for " + name + ".";
        }
        return "You must provide between {green}" + minArguments + "{white} and {red}" + maxArguments + "{white} arguments for " + name + ".";
    }
}

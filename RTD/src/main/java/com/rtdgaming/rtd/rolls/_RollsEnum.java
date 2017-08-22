package com.rtdgaming.rtd.rolls;

import java.util.Random;

import org.bukkit.entity.Player;

import com.rtdgaming.rtd.Roller;

/**
 * Enumeration of all the rolls within RTD.
 * This file must be updated with every new roll.
 * Keep code here to a minimum, create a class for
 * the roll if you need more space to code.
 * @author Czech Erface
 */
public enum _RollsEnum
{
	//See the methods you can override after the list.
	/* ****************** */
	/* *** Good Rolls *** */
	/* ****************** */
	BUFFED_HEALTH			(BuffedHealth.getInstance()),
	EGG_ADDICT				(EggAddict.getInstance()),
	SNOW_BLASTER			(SnowBlaster.getInstance()),
	DEJA_VU					(DejaVu.getInstance()),
	FALL_IMMUNITY			(FallImmunity.getInstance()),
	WATER_BREATHING			(WaterBreathing.getInstance()),
	FIRE_IMMUNITY			(FireImmunity.getInstance()),
	BLOCK_PARTY				(BlockParty.getInstance()),
	INSTANT_TREE			(InstantTree.getInstance()),
	ESCAPE_ROPE				(EscapeRope.getInstance()),
	SURFACE_DWELLER			(SurfaceDweller.getInstance()),
	/* ***************** */
	/* *** Bad Rolls *** */
	/* ***************** */
	_BAD_ROLLS				(null), //Place-holder to check if roll is good or not
	IGNITE					(Ignite.getInstance()),
	THE_SHAFT				(TheShaft.getInstance()),
	WAIT_MORE				(WaitMore.getInstance()),
	INVENTORY_EXPLOSION		(InventoryExplosion.getInstance()),
	HYDROPHOBIA				(Hydrophobia.getInstance());
	
	private _Roll rollInstance;

	private _RollsEnum(_Roll rollInstance)
	{
		this.rollInstance = rollInstance;
	}

	public _Roll getInstance()
	{
		return rollInstance;
	}

	/**
	 * Used to get a random roll for the specified player.
	 * @param p The player that will receive the roll.
	 * @param good Map over good rolls?
	 * @param doChecks Only return rolls that <i>can</i> be rolled?
	 */
	public static _RollsEnum getRandomRoll(Player p, boolean good)
	{
		/* We probably need to memoize some of this computation later, but
		 * seeing as this method is only called once per "/rtd" command, it's
		 * not too expensive at the moment.  There are also plenty of
		 * optimizations that can be done, but like I said, that problem is
		 * for later.
		 */

		_RollsEnum[] allRolls = _RollsEnum.values();
		_RollsEnum[] potentialRolls = new _RollsEnum[allRolls.length];
		int count = 0;

		//Okay, we are going to make an array of potential rolls
		if(good)
		{
			for(int i = 0; i < _RollsEnum._BAD_ROLLS.ordinal(); i++)
				if(allRolls[i].getInstance().enabled() && !Roller.getRoller().isActiveRoll(p, allRolls[i]))
					potentialRolls[count++] = allRolls[i];
		}
		else
			for(int i = _RollsEnum._BAD_ROLLS.ordinal() + 1; i < allRolls.length; i++)
				if(allRolls[i].getInstance().enabled() && !Roller.getRoller().isActiveRoll(p, allRolls[i]))
					potentialRolls[count++] = allRolls[i];

		//This should never happen. If it does, you fucked up by disabling too many rolls, rolling too fast, etc...
		if(count == 0)
			throw new RuntimeException("There are no " + (good ? "good" : "bad") + " rolls to award!");

		//Sweet, now randomly pick a roll and return it.  Done.
		Random rand = new Random();
		return potentialRolls[rand.nextInt(count)];
	}

	/**
	 * Returns a string with the enumeration of all the rolls in this file.
	 */
	public static String getRollsString()
	{
		_RollsEnum[] daVals = _RollsEnum.values();
		String toReturn = "Good Rolls:\n";
		for(int i = 0; i < daVals.length; i++)
			if(daVals[i] == _RollsEnum._BAD_ROLLS)
				toReturn += "\nBad Rolls:\n";
			else
				toReturn += i + (daVals[i].getInstance().enabled() ? "\tEnabled \t" : "\tDisabled\t") + daVals[i] + "\n";
		return toReturn;
	}

	/**
	 * Is this roll a good roll?
	 */
	public boolean isGood()
	{
		return this.compareTo(_BAD_ROLLS) < 0;
	}

	/**
	 * Wrapper class for data passing.
	 */
	public class Data
	{
		public Player player;

		public Data(Player p)
		{
			player = p;
		}
	}
}

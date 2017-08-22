package com.rtdgaming.rtd;

import org.bukkit.Location;

import com.rtdgaming.rtd.rolls._RollsEnum;

/**
 * Wrapper class for transferring roll data.
 * @author Kilandor
 */
public class RollInfo
{
	private _RollsEnum roll;
	private long timestamp;
	private Location locRolled;

	public RollInfo(_RollsEnum roll, long timestamp, Location locRolled)
	{
		this.roll = roll;
		this.timestamp = timestamp;
		this.locRolled = locRolled;
	}

	public _RollsEnum getRoll()
	{
		return roll;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public Location getLocationRolled()
	{
		return locRolled;
	}

	public String toString()
	{
		return "RollInfo{roll(" + roll + "),time(" + timestamp + ")}";
	}
}

package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public abstract class _Roll
{
	private boolean enabled;
	private boolean isActive;
	private int duration;

	/**
	 * Sets up this _Roll object.
	 * @param enabled Is this roll enabled?
	 * @param isActive Is this a timed roll?
	 * @param duration Time for roll to last: -1 for unlimited.
	 */
	protected _Roll(boolean enabled, boolean isActive, int duration)
	{
		this.enabled = enabled;
		this.isActive = isActive;
		this.duration = duration;
	}

	/**
	 * Sets up this _Roll object assuming a default duration.
	 * @param enabled Is this roll enabled?
	 * @param isActive Is this a timed roll?
	 */
	protected _Roll(boolean enabled, boolean isActive)
	{
		this(enabled, isActive, RTD.rtd.settings.rollDuration);
	}

	public boolean enabled()
	{
		return enabled;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public int getDuration()
	{
		return duration;
	}

	/** Called when rolled */
	public abstract void setup(Data data);

	/** Called on timer update */
	public abstract void update(Data data);

	/** Called on timer finished */
	public abstract void expire(Data data);
}

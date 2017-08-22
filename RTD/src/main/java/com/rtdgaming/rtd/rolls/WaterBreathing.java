package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class WaterBreathing extends _Roll
{
	private static WaterBreathing instance;

	public static WaterBreathing getInstance()
	{
		if(instance == null)
			instance = new WaterBreathing();
		return instance;
	}

	private WaterBreathing()
	{
		super(true, true);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Water Breathing{white}!", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You no longer have {darkgreen}Water Breathing", false);
	}

	public void update(Data data)
	{
		data.player.setRemainingAir(data.player.getMaximumAir());
	}
}

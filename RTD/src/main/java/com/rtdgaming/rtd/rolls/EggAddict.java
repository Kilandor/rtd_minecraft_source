package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class EggAddict extends _Roll
{
	private static EggAddict instance;

	public static EggAddict getInstance()
	{
		if(instance == null)
			instance = new EggAddict();
		return instance;
	}

	private EggAddict()
	{
		super(false, true, 30);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Egg Addict{white}!", false);
	}

	public void expire(Data data) {}

	public void update(Data data)
	{
		data.player.throwEgg();
	}
}

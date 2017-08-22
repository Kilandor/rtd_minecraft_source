package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class SnowBlaster extends _Roll
{
	private static SnowBlaster instance;

	public static SnowBlaster getInstance()
	{
		if(instance == null)
			instance = new SnowBlaster();
		return instance;
	}

	private SnowBlaster()
	{
		super(false, true, 30);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Snow Blaster{white}!", false);
	}

	public void expire(Data data) {}

	public void update(Data data)
	{
		data.player.throwSnowball();
	}
}

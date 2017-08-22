package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class WaitMore extends _Roll
{
	private static WaitMore instance;

	public static WaitMore getInstance()
	{
		if(instance == null)
			instance = new WaitMore();
		return instance;
	}

	private WaitMore()
	{
		super(true, false);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {red}Wait +2 Minutes{white}!", false);
	}

	public void expire(Data data) {}
	public void update(Data data) {}
}

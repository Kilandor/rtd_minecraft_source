package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class BuffedHealth extends _Roll
{
	private static BuffedHealth instance;

	public static BuffedHealth getInstance()
	{
		if(instance == null)
			instance = new BuffedHealth();
		return instance;
	}

	private BuffedHealth()
	{
		super(true, false);
	}

	public void setup(Data d)
	{
		d.player.setHealth(50);
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + d.player.getName() + "{white} has rolled {darkgreen}Buffed health{white}!", false);
	}

	public void expire(Data data) {}
	public void update(Data data) {}
}

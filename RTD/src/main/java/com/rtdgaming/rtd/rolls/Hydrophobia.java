package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class Hydrophobia extends _Roll {
	private static Hydrophobia instance;

	public static Hydrophobia getInstance()
	{
		if(instance == null)
			instance = new Hydrophobia();
		return instance;
	}

	private Hydrophobia()
	{
		super(true, true);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {red}Hydrophobia{white}!", false);
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "Do not go underwater D:", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You no longer have {darkgreen}Hydrophobia{white}, yay!", false);
	}

	public void update(Data data)
	{
		data.player.setRemainingAir(0);
	}
}

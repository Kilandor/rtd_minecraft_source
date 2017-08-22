package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class FireImmunity extends _Roll
{
	private static FireImmunity instance;

	public static FireImmunity getInstance()
	{
		if(instance == null)
			instance = new FireImmunity();
		return instance;
	}

	private FireImmunity()
	{
		super(true, true);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Fire Immunity{white}!", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You no longer have {darkgreen}Fire Immunity", false);
	}

	public void update(Data data) {}
}

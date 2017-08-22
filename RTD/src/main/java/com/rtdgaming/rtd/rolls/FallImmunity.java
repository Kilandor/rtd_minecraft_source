package com.rtdgaming.rtd.rolls;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

//Class is quite empty at the moment
public class FallImmunity extends _Roll
{
	private static FallImmunity instance;

	public static FallImmunity getInstance()
	{
		if(instance == null)
			instance = new FallImmunity();
		return instance;
	}

	private FallImmunity()
	{
		super(true, true);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Fall Immunity{white}!", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You no longer have {darkgreen}Fall Immunity", false);
	}

	public void update(Data data) {}
	
	public static final String activateText = "{darkgray}Your Fall Immunity saved you from a deadly fall!";
}

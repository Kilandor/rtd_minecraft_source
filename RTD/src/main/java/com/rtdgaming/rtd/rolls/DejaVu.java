package com.rtdgaming.rtd.rolls;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.rtdgaming.rtd.*;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class DejaVu extends _Roll
{
	private static DejaVu instance;

	public static DejaVu getInstance()
	{
		if(instance == null)
			instance = new DejaVu();
		return instance;
	}

	private DejaVu()
	{
		super(true, true, 600);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Deja Vu{white}!", false);
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "Typing {red}/port{white} will place you in a once familiar surrounding.", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "Your {green}Deja Vu{white} roll ran out {gray}:(", false);
	}

	public void update(Data data) {}

	public void activate(Player player)
	{
		RollInfo info = Roller.getRoller().getRollInfo(player, _RollsEnum.DEJA_VU);
		if(info == null)
		{
			RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "You do not currently have {green}Deja Vu", false);
			return;
		}
		Location targetLocation = info.getLocationRolled();
		
		//Hack to fix cross-world teleportation bug
		if (player.getWorld() != targetLocation.getWorld())
		{
			player.teleport(targetLocation.getWorld().getSpawnLocation());
			player.teleport(targetLocation);
		}
		else
		{
			player.teleport(targetLocation);
		}
		
		RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "You have {green}Deja Vu{white} all of a sudden ><", false);
		Roller.getRoller().remPlayerRoll(player.getName(), _RollsEnum.DEJA_VU); //Should be == info.getRoll()
	}
}

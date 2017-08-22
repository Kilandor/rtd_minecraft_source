package com.rtdgaming.rtd.rolls;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.rtdgaming.rtd.*;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class EscapeRope extends _Roll
{
	private static EscapeRope instance;

	public static EscapeRope getInstance()
	{
		if(instance == null)
			instance = new EscapeRope();
		return instance;
	}

	private EscapeRope()
	{
		super(true, true, 60);
	}
	public static final Location spawn = new Location(RTD.rtd.defaultWorld, 122.0, 66.0, -181.0);

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled an {darkgreen}Escape Rope{white}!", false);
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "Typing {red}/rope{white} will allow you to escape back to spawn.", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "Your {green}Escape Rope{white} was taken away.", false);
	}

	public void update(Data data) {}

	public void activate(Player player)
	{
		RollInfo info = Roller.getRoller().getRollInfo(player, _RollsEnum.ESCAPE_ROPE);
		if(info == null)
		{
			RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "You do not currently have an {green}Escape Rope", false);
			return;
		}
		
		//Hack to fix cross-world teleportation bug
		if (player.getWorld() != spawn.getWorld())
		{
			player.teleport(spawn.getWorld().getSpawnLocation());
			player.teleport(spawn);
		}
		else
		{
			player.teleport(spawn);
		}
		
		RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "You used your {green}Escape Rope{white}", false);
		Roller.getRoller().remPlayerRoll(player.getName(), _RollsEnum.ESCAPE_ROPE); //Should be == info.getRoll()
	}
}

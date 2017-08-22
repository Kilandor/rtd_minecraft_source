package com.rtdgaming.rtd.rolls;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.RollInfo;
import com.rtdgaming.rtd.Roller;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class SurfaceDweller extends _Roll {
	private static SurfaceDweller instance;

	public static SurfaceDweller getInstance()
	{
		if(instance == null)
			instance = new SurfaceDweller();
		return instance;
	}

	private SurfaceDweller()
	{
		super(true, true, 60);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Surface Dweller{white}!", false);
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "Typing {red}/rise{white} will place you on the surface of the world.", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You no longer have {darkgreen}Surface Dweller", false);
	}

	public void update(Data data) {}
	
	public void activate(Player player)
	{
		System.out.println(player.getWorld().getTime());
		RollInfo info = Roller.getRoller().getRollInfo(player, _RollsEnum.SURFACE_DWELLER);
		if(info == null)
		{
			RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "You do not currently have the {green}Surface Dweller{white} roll", false);
			return;
		}
		Location playerLoc = player.getLocation();
		World world = player.getWorld();
		
		//This roll should not work in the nether
		if (world != EscapeRope.spawn.getWorld())
		{
			RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "Mysterious forces in this dimension are preventing you  D:", false);
			return;
		}
		
		long curTime = world.getTime();
		double newY = (double)player.getWorld().getHighestBlockYAt(playerLoc);
		if (curTime >= 0 && curTime <= 12000 && playerLoc.getY() < newY - 10.0)
			RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "The sunlight {red}burns{white} your eyes as you are flung to the surface!", false);
		else
			RTD.rtd.getChat().playerMsg(player, RTD.CHATTITLE, "You were moved to the surface!", false);
		playerLoc.setY(newY);
		player.teleport(playerLoc);
		Roller.getRoller().remPlayerRoll(player.getName(), _RollsEnum.SURFACE_DWELLER);
	}
}

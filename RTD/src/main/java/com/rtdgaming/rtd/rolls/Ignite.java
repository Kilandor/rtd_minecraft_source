package com.rtdgaming.rtd.rolls;

import org.bukkit.Location;
import org.bukkit.Material;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class Ignite extends _Roll
{
	private static Ignite instance;

	public static Ignite getInstance()
	{
		if(instance == null)
			instance = new Ignite();
		return instance;
	}

	private Ignite()
	{
		super(false, false);
	}

	public void setup(Data data)
	{
		Location l = data.player.getLocation();
		data.player.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()).setType(Material.FIRE);
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {red}Ignite{white}!", false);
	}

	public void expire(Data data) {}
	public void update(Data data) {}
}

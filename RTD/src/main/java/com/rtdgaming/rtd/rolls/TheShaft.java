package com.rtdgaming.rtd.rolls;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class TheShaft extends _Roll
{
	private static TheShaft instance;

	public static TheShaft getInstance()
	{
		if(instance == null)
			instance = new TheShaft();
		return instance;
	}

	private TheShaft()
	{
		super(false, false);
	}
	private static final int MIN_SHAFT_DEPTH = 5;
	private static final int MAX_SHAFT_DEPTH = 15;

	public boolean isActive()
	{
		return false;
	}

	public void setup(Data data)
	{
		int depth = shaftPlayer(data.player);
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {red}The Shaft{white} and fell " + depth + " blocks!", false);
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You just got {red}shafted{white}!", false);
	}

	public void expire(Data data) {}
	public void update(Data data) {}

	public int shaftPlayer(Player player)
	{
		Random rand = new Random();
		int shaftSize = rand.nextInt(MAX_SHAFT_DEPTH + 1);
		if(shaftSize < MIN_SHAFT_DEPTH)
			shaftSize = MIN_SHAFT_DEPTH;
		World world = player.getWorld();
		Location loc = player.getLocation();
		for(int i = 0; i < shaftSize; i++)
		{
			Block block = world.getBlockAt(loc.getBlockX(), loc.getBlockY() - i, loc.getBlockZ());
			if(block.getType() == Material.BEDROCK
					|| block.getType() == Material.WATER
					|| block.getType() == Material.LAVA
					|| block.getType() == Material.CHEST)
				break; //Stop at the first chance
			block.setType(Material.AIR);
		}
		player.teleport(new Location(world, (double) loc.getBlockX() + 0.5, (double) loc.getBlockY(), (double) loc.getBlockZ() + 0.5, loc.getYaw(), -90.0f));
		return shaftSize - 1;
	}
}

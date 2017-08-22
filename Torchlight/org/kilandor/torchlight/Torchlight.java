package org.kilandor.torchlight;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.minecraft.server.EnumSkyBlock;
import org.bukkit.Location;
import org.bukkit.Server;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Torchlight extends JavaPlugin
{
	private Logger log;
	public final String name = getDescription().getName();
	public final String version = getDescription().getVersion();

	public static HashMap<String, HashMap<Location, Integer>> oldBlocks = new HashMap();
	private TLPlayerListener playerListener;

	public void onDisable() { }

	public void onEnable()
	{
		this.log = Logger.getLogger("Minecraft");
		this.log.info(this.name + " " + this.version + " initialized");

		this.playerListener = new TLPlayerListener(this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, this.playerListener, Event.Priority.Normal, this);
	}

	public void lightingCheck(Player player)
	{
		Location location = player.getLocation();
		CraftWorld world = (CraftWorld)player.getWorld();
		boolean isHoldingTorch = false;
		
		
		HashMap playerBlocks = oldBlocks.get(player.getName());

		if(!oldBlocks.containsKey(player.getName()))
			oldBlocks.put(player.getName(), null);
		if(player.getItemInHand().getType() == Material.TORCH)
			isHoldingTorch = true;

		//boolean isPrevLit = isPrevLit(playerBlock, playerBlocks, (CraftWorld)player.getWorld());
		if(isHoldingTorch)
		{
			Block playerBlock = world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			Block playerBlockBelow = world.getBlockAt(location.getBlockX(), location.getBlockY()-1, location.getBlockZ());

			if(playerBlockBelow.getType() == Material.AIR)
				playerBlockBelow = world.getBlockAt(location.getBlockX(), location.getBlockY()-2, location.getBlockZ());

			if(playerBlock.getLightLevel() <= 9 && playerBlockBelow.getType() == Material.ICE)
				propogateLightAround(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 9, 1, world, player);
			else if(isHoldingTorch && playerBlock.getLightLevel() <= 12)
				propogateLightAround(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 12, 3, world, player);
			else
			{
				if(playerBlocks != null)
				{
					resetLight(playerBlocks, (CraftWorld)player.getWorld());
					oldBlocks.clear();
				}
			}
		}
		else
		{
			if(playerBlocks != null)
			{
				resetLight(playerBlocks, (CraftWorld)player.getWorld());
				oldBlocks.clear();
			}
		}
	}
	private void propogateLightAround(int x, int y, int z, int level, int spacing, CraftWorld world, Player player)
	{
		int radius = level / spacing;
		HashMap playerBlocks = oldBlocks.get(player.getName());
		if (playerBlocks != null)
		{
			resetLight(playerBlocks, world);
			playerBlocks.clear();
		}
		else
		{
			playerBlocks = new HashMap();
			oldBlocks.put(player.getName(), playerBlocks);
		}
		for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++)
				for (int k = -radius; k <= radius; k++)
				{
					int oldLevel = world.getHandle().j(x + i, y + j, z + k);
					int actLevel = level - (Math.abs(i) + Math.abs(j) + Math.abs(k));
					if (actLevel > oldLevel)
					{
						playerBlocks.put(new Location(world, x + i, y + j, z + k), Integer.valueOf(oldLevel));
						world.getHandle().b(EnumSkyBlock.BLOCK, x + i, y + j, z + k, actLevel);
					}
				}
	}

	private void resetLight(HashMap<Location, Integer> playerBlocks, CraftWorld world)
	{
		for (Map.Entry entry : playerBlocks.entrySet())
		{
			Location location = (Location)entry.getKey();
			world.getHandle().b(EnumSkyBlock.BLOCK, location.getBlockX(), location.getBlockY(), location.getBlockZ(), ((Integer)entry.getValue()).intValue());
		}
	}

	private boolean isPrevLit(Block playerBlock, HashMap<Location, Integer> playerBlocks, CraftWorld world)
	{
		if(playerBlocks == null)
			return true;
		for (Map.Entry entry : playerBlocks.entrySet())
		{
			Location location = (Location)entry.getKey();
			Block prevLitBlock = world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			if(playerBlock == prevLitBlock)
				return true;
		}
		return false;
	}
}
package com.kilandor.antihack;

import com.kilandor.chat.Chat;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.event.block.BlockListener;
import org.bukkit.Location;


/**
 * Handle events for all Player related events
 * @author Kilandor
 */
public class BlockDamaged extends BlockListener
{
	private final AntiHack plugin;
	private Player player;
	private Server server;
	private Chat chat;
	private long digStartStamp;
	private boolean digStarted = false;
	private Location digLastLoc;
	private MinMax minMax;

	public BlockDamaged(AntiHack instance)
	{
		plugin = instance;
		server = plugin.getServer();
		chat = new Chat(server);
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event)
	{
		player = event.getPlayer();
		BlockDamageLevel blockDamageLevel = event.getDamageLevel();

		if(plugin.getMinMax(event.getBlock().getType().toString()) != null)
		{
			//chat.playerMsg(player, plugin.CHATTITLE, event.getBlock().getType().toString() + " exists in map", false);
			minMax = (MinMax) plugin.getMinMax(event.getBlock().getType().toString());
		}
		else
		{
			minMax = new MinMax();
			plugin.addMinMax(event.getBlock().getType().toString(), minMax);
			//chat.playerMsg(player, plugin.CHATTITLE, event.getBlock().getType().toString() + " does not exists in map", false);
		}

		if(blockDamageLevel == BlockDamageLevel.STARTED)
		{
			if(digStarted && !digLastLoc.equals(event.getBlock().getLocation()))
			{
				chat.playerMsg(player, plugin.CHATTITLE, "Block Location Diff, restarting ", false);
				digStartStamp = plugin.getCurMillis();
				digStarted = true;
				digLastLoc = event.getBlock().getLocation();
			}
			else if(!digStarted)
			{
				digStartStamp = plugin.getCurMillis();
				digStarted = true;
				digLastLoc = event.getBlock().getLocation();
			}
		}
		if(blockDamageLevel == BlockDamageLevel.STOPPED)
		{
			digStarted = false;
			digStartStamp = 0;
			//chat.playerMsg(player, plugin.CHATTITLE, "{green}Stoped {red}Broken{white}, duration: " + plugin.getMillisDiff(digStartStamp), false);
		}

		if(blockDamageLevel == BlockDamageLevel.BROKEN)
		{
			digStarted = false;
			chat.playerMsg(player, plugin.CHATTITLE, "{red}Stoped {green}Broken {white} name: {blue}" + event.getBlock().getType().toString() + "{white}, duration: " + plugin.getMillisDiff(digStartStamp), false);
			minMax.check(plugin.getMillisDiff(digStartStamp));
			digStartStamp = 0;
		}
	}
}

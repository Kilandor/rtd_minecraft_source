package com.rtdgaming.rtd.eventcatchers;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.Roller;
import com.rtdgaming.rtd.SQL;

/**
 * Handle events for all Player related events
 * 
 * @author Kilandor
 */
public class PlayerJoin extends PlayerListener
{
	private final RTD plugin;
	private Player player;
	@SuppressWarnings("unused")
	private Server server;

	public PlayerJoin(RTD instance)
	{
		plugin = instance;
		server = plugin.getServer();
	}

	public void onPlayerJoin(PlayerJoinEvent event)
	{
		SQL sql = SQL.getInstance();
		player = event.getPlayer();
		if(!sql.sExistsPlayer(player.getName()))
			sql.iNewPlayer(player.getName());

		//Add a new empty list for their rolls
		Roller.getRoller().addPlayer(player.getName());
	}
}

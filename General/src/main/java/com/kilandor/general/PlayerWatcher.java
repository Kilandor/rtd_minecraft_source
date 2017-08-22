package com.kilandor.general;

import com.kilandor.chat.Chat;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author Kilandor
 */
public class PlayerWatcher extends PlayerListener
{
	private final General plugin;
	private Player player;
	private Server server;
	private Chat chat;

	public PlayerWatcher(General instance)
	{
		plugin = instance;
		server = plugin.getServer();
		chat = new Chat(server);
	}

	public void onPlayerJoin(PlayerJoinEvent event)
	{
		player = event.getPlayer();
		SQL sql = new SQL(plugin);

		String dispName = sql.sDisplay(player.getName(), true);
		if(!dispName.isEmpty() && !player.hasPermission("general.op"))
			player.setDisplayName(dispName);
		else if(!dispName.isEmpty() && player.hasPermission("general.op"))
			player.setDisplayName(plugin.getChat().colorize("{darkaqua}OP{white} ")+dispName);
		else if(player.hasPermission("general.op"))
			player.setDisplayName(plugin.getChat().colorize("{darkaqua}OP{white} ")+player.getName());

		plugin.motd(player);
		
		chat.playerMsg(player, General.CHATTITLE, "Online Players:", false);
		chat.playerMsg(player, General.CHATTITLE, plugin.getOnlinePlayers(), false);
	}
}

package com.kilandor.antihack;

import com.kilandor.chat.Chat;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * Handle events for all Player related events
 * @author Kilandor
 */
public class PlayerMove extends PlayerListener
{
	private final AntiHack plugin;
	private Player player;
	private Server server;
	private Chat chat;

	public PlayerMove(AntiHack instance)
	{
		plugin = instance;
		server = plugin.getServer();
		chat = new Chat(server);
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		player = event.getPlayer();
		chat.playerMsg(player, plugin.CHATTITLE, "Move Type: " + event.getType().toString(), false);
	}
}

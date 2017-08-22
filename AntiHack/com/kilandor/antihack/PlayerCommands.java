package com.kilandor.antihack;

import com.kilandor.chat.Chat;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * Handle events for all Player related events
 * @author Kilandor
 */
public class PlayerCommands extends PlayerListener
{
	private final AntiHack plugin;
	private Player player;
	private Server server;
	private Chat chat;
	private MinMax minMax;

	public PlayerCommands(AntiHack instance)
	{
		plugin = instance;
		server = plugin.getServer();
		chat = new Chat(server);
	}

	@Override
	public void onPlayerCommand(PlayerChatEvent event)
	{
		player = event.getPlayer();
		String[] command = event.getMessage().split(" ");

		if(command[0].compareToIgnoreCase("/antihack") == 0 && command.length > 1)
		{
			if(plugin.getMinMax(command[1].toUpperCase()) != null)
			{
				minMax = (MinMax) plugin.getMinMax(command[1].toUpperCase());
				chat.playerMsg(player, plugin.CHATTITLE, "Block {blue}" + command[1].toUpperCase() + " {white}MinTime: {green}" + minMax.getMin() + "{white} MaxTime: {gold}" + minMax.getMax(), false);
			}
			else
				chat.playerMsg(player, plugin.CHATTITLE, "Block not found in list", false);
			
		}
	}
}

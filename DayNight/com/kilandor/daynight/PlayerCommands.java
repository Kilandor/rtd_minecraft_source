package com.kilandor.daynight;

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
	private final DayNight plugin;
	private Player player;
	private Server server;
	private Chat chat;

	public PlayerCommands(DayNight instance)
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

		if(command[0].compareToIgnoreCase("/daynight") == 0)
		{
			if(command[1].compareToIgnoreCase("help") == 0)
			{
				chat.playerMsg(player, plugin.CHATTITLE, "Commands", false);
				if(player.isOp())
				{
					chat.playerMsg(player, plugin.CHATTITLE, "{green}/day {white}- Changes the time to day.", false);
					chat.playerMsg(player, plugin.CHATTITLE, "{green}/night {white}- Changes the time to night.", false);
					chat.playerMsg(player, plugin.CHATTITLE, "{green}/gettime {white}- Outputs the current time.", false);
					chat.playerMsg(player, plugin.CHATTITLE, "{green}/settime {lightpurple}<time> {white}- Changes the time to the specified value.", false);
				}
				chat.playerMsg(player, plugin.CHATTITLE, "{green}/daynight version {white}- Displays DayNight Version.", false);
			}
			else if(command[1].compareToIgnoreCase("version") == 0)
				chat.playerMsg(player, plugin.CHATTITLE, "{green}Ver: {lightpurple}" + plugin.VERSION, false);
		}
		if(player.isOp())
		{
			if(command[0].compareToIgnoreCase("/day") == 0)
				plugin.setRelativeTime(0);
			else if(command[0].compareToIgnoreCase("/night") == 0)
				plugin.setRelativeTime(14000);
			else if(command[0].compareToIgnoreCase("/gettime") == 0)
				chat.playerMsg(player, plugin.CHATTITLE, "{green}Time: {lightpurple}" + server.getWorlds().get(0).getTime(), false);
			else if(command[0].compareToIgnoreCase("/settime") == 0 && command[1].length() > 1 && command[1].matches("([0-9]+)"))
				plugin.setRelativeTime(Long.parseLong(command[1]));
			else if(command[0].compareToIgnoreCase("/timecycle") == 0 && command[1].length() > 1 && command[1].matches("(none|day|night)"))
				plugin.settings.timeCycle = command[1].toString();

		}
	}
}

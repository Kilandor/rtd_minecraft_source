package com.kilandor.chat;

/**
 * Chat and Color handler for Bukkit
 *
 * @version 1.1
 * @author Jason Booth (Kilandor) http://www.kilandor.com/
 * @copyright Copyright (c) 2010-2011 Jason Booth (Kilandor)
 * @license BSD
 */

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;

public class Chat
{
	private Player	player;
	private Server	server;
	private ColouredConsoleSender console;

	private String	message;
	private String	title;
	private boolean	random;
	private boolean	rainbow;
	private boolean	word;
	private int		seed;

	private final String[] rainbowColors = { /*color.BLACK.toString(),*/ ChatColor.DARK_BLUE.toString(), ChatColor.DARK_GREEN.toString(), ChatColor.DARK_AQUA.toString(), ChatColor.DARK_RED.toString(), ChatColor.DARK_PURPLE.toString(), ChatColor.GOLD.toString(), ChatColor.GRAY.toString(), ChatColor.DARK_GRAY.toString(), ChatColor.BLUE.toString(), ChatColor.GREEN.toString(), ChatColor.AQUA.toString(), ChatColor.RED.toString(), ChatColor.LIGHT_PURPLE.toString(), ChatColor.YELLOW.toString(), ChatColor.WHITE.toString() };

	public Chat(Server servertmp)
	{
		server = servertmp;
		console = new ColouredConsoleSender((CraftServer) server);
	}

	/**
	 * Send global worldwide message
	 *
	 * @param title Message Title(prefix)
	 * @param message Chat Message
	 * @param random Use Random Color
	 */
	public void globalMsg(String title, String message, boolean random)
	{
		this.title = title;
		this.message = message;
		this.random = random;
		this.rainbow = false;
		this.send(1);
	}

	/**
	 * Send global worldwide message
	 *
	 * @param title Message Title(prefix)
	 * @param message Chat Message
	 * @param rainbow Rainbow Colorize(per character)
	 * @param word Rainbow Colorize(per word)(requires rainbow)
	 * @param seed Value to see Rainbow to start with
	 */
	public void globalMsg(String title, String message, boolean rainbow, boolean word, int seed)
	{
		this.title = title;
		this.message = message;
		this.random = false;
		this.rainbow = rainbow;
		this.word = word;
		this.seed = seed;
		this.send(1);
	}

	/**
	 * Send message to specific player
	 *
	 * @param player Player Object
	 * @param title Message Title(prefix)
	 * @param message Chat Message
	 * @param random Use Random Color
	 */
	public void playerMsg(Player player, String title, String message, boolean random)
	{
		this.player = player;
		this.title = title;
		this.message = message;
		this.random = random;
		this.rainbow = false;
		this.send(2);
	}

	/**
	 * Send message to specific player
	 *
	 * @param player Player Object
	 * @param title Message Title(prefix)
	 * @param message Chat Message
	 * @param rainbow Rainbow Colorize(per character)
	 * @param word Rainbow Colorize(per word)(requires rainbow)
	 * @param seed Value to see Rainbow to start with
	 */
	public void playerMsg(Player player, String title,  String message, boolean rainbow, boolean word, int seed)
	{
		this.player = player;
		this.title = title;
		this.message = message;
		this.random = false;
		this.rainbow = rainbow;
		this.word = word;
		this.seed = seed;
		this.send(2);
	}

	/**
	 * Send message to the server console
	 *
	 * @param title Message Title(prefix)
	 * @param message Chat Message
	 * @param random Use Random Color
	 */
	public void consoleMsg(String title, String message, boolean random)
	{
		this.title = title;
		this.message = message;
		this.random = random;
		this.rainbow = false;
		this.send(3);
	}

	/**
	 * Send message to the server console
	 *
	 * @param player Player Object
	 * @param title Message Title(prefix)
	 * @param message Chat Message
	 * @param rainbow Rainbow Colorize(per character)
	 * @param word Rainbow Colorize(per word)(requires rainbow)
	 * @param seed Value to see Rainbow to start with
	 */
	public void consoleMsg(String title,  String message, boolean rainbow, boolean word, int seed)
	{
		this.title = title;
		this.message = message;
		this.random = false;
		this.rainbow = rainbow;
		this.word = word;
		this.seed = seed;
		this.send(3);
	}
	/**
	 * Clean string of colors
	 *
	 * @param message Chat Message
	 * @return String
	 */
	public String clean(String message)
	{
		message = message.replaceAll("\\{([a-z]+)\\}", "");
		return message;
	}

	/**
	 * Handles Color Replacements
	 *
	 * @param message Chat Message
	 * @return String
	 */
	public String colorize(String message)
	{
		message = message.replaceAll("\\{black\\}",			ChatColor.BLACK.toString());
		message = message.replaceAll("\\{darkblue\\}",		ChatColor.DARK_BLUE.toString());
		message = message.replaceAll("\\{darkgreen\\}",		ChatColor.DARK_GREEN.toString());
		message = message.replaceAll("\\{darkaqua\\}",		ChatColor.DARK_AQUA.toString());
		message = message.replaceAll("\\{darkred\\}",		ChatColor.DARK_RED.toString());
		message = message.replaceAll("\\{darkpurple\\}",	ChatColor.DARK_PURPLE.toString());
		message = message.replaceAll("\\{gold\\}",			ChatColor.GOLD.toString());
		message = message.replaceAll("\\{gray\\}",			ChatColor.GRAY.toString());
		message = message.replaceAll("\\{darkgray\\}",		ChatColor.DARK_GRAY.toString());
		message = message.replaceAll("\\{blue\\}",			ChatColor.BLUE.toString());
		message = message.replaceAll("\\{green\\}",			ChatColor.GREEN.toString());
		message = message.replaceAll("\\{aqua\\}",			ChatColor.AQUA.toString());
		message = message.replaceAll("\\{red\\}",			ChatColor.RED.toString());
		message = message.replaceAll("\\{lightpurple\\}",	ChatColor.LIGHT_PURPLE.toString());
		message = message.replaceAll("\\{yellow\\}",		ChatColor.YELLOW.toString());
		message = message.replaceAll("\\{white\\}",			ChatColor.WHITE.toString());

		//Strips out any left over incorrect blocks
		message = this.clean(message);

		return message;
	}

	/**
	 * Handles Rainbow Colorize
	 *
	 * @param message Chat Message
	 * @return
	 */
	public String rainbow(String message)
	{
		message = this.clean(message);

		String messages[];
		if(!word)
			messages = message.split("");
		else
			messages = message.split(" ");
		message = "";
		int i = seed - 1;
		for(String msg : messages)
		{
			if(msg.contains(" "))
			{
				message += msg;
				continue;
			}

			i++;
			if(i >= rainbowColors.length)
				i = 0;
			
			if(!word)
				message += rainbowColors[i] + msg;
			else
				message += " " + rainbowColors[i] + msg;
		}
		return message;
	}

	/**
	 * Randomly picks a color for the message
	 *
	 * @param message Chat message
	 * @return
	 */
	public String random(String message)
	{
		Random generator = new Random();
		int i = generator.nextInt(rainbowColors.length);
		message = this.clean(message);
		message = rainbowColors[i] + message;

		return message;
	}
	
	/**
	 * Sends the message
	 *
	 * @param type Type of message to send
	 */
	private void send(int type)
	{
		title = this.colorize(title);

		if(random)
			message = this.random(message);
		else if(!rainbow)
			message = this.colorize(message);
		else
			message = this.rainbow(message);

		switch(type)
		{
			case 1:
			{
				server.broadcastMessage(title + message);
				break;
			}
			case 2:
			{
				player.sendMessage(title + message);
				break;
			}
			case 3:
			{
				console.sendMessage(title + message);
				break;
			}
		}
	}
}
package com.kilandor.general;

import com.kilandor.chat.Chat;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.Timer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Roll the Dice(RTD) for Bukkit
 *
 * @author Kilandor
 */
public class General extends JavaPlugin
{
	private PlayerWatcher playerWatcher;

	public static final String VERSION = "1.1";
	public static final String CHATTITLE = "[{blue}General{white}] ";

	private Chat chat;
	public static Logger log;
	private File folderPath;
	private final Timer timer = new Timer();

	Settings settings = new Settings();


	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");

		folderPath = getDataFolder();
		Settings.config_file = folderPath.getAbsolutePath() + File.separator + Settings.config_file;

		initializeVariables();
		registerHooks();

		PluginManager pm = getServer().getPluginManager();

		System.out.println("General version " + VERSION + " is enabled!" );

		timer.scheduleAtFixedRate(new GenericTimerTask(this), 1000, 1000);
		
		//Create folders and files
		if(!folderPath.exists())
			folderPath.mkdir();
		File configFile = new File(Settings.config_file);
		if(!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
				settings.saveDefaultSettings();
			} catch (IOException ex){ }
		}
		settings.loadSettings();
		chat = new Chat(getServer());
	}
	public void initializeVariables()
	{

		//Create all the listeners
		playerWatcher = new PlayerWatcher(this);
	}

	public void registerHooks()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerWatcher, Event.Priority.Low, this);

	}
	public void onDisable()
	{
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Player player = null;
		Player toPlayer = null;
		boolean isServer = false;
		if (sender instanceof Player)
			player = (Player) sender;
		else
			isServer = true;

		String command = cmd.getName();

		if(command.equalsIgnoreCase("info"))
		{
			if(isServer)
			{
				getChat().consoleMsg(CHATTITLE, "Commands - Key {green}cmd {aqua}[optional] {lightpurple}<req>", false);
				getChat().consoleMsg("", "{green}motd {white}- Displays the Message of the Day.", false);
				getChat().consoleMsg("", "{green}list {white}- List all online players.", false);
				getChat().consoleMsg("", "{green}dispname {lightpurple}<name> {white}- Change your Display name.", false);
				getChat().consoleMsg("", "{green}gift {lightpurple}[name] <id> {aqua}[qty] [stacks] [durab] {white}- Give items to somone", false);
				getChat().consoleMsg("", "{green}dispname history {lightpurple}<name>{white}- List the history of somones names.", false);
				getChat().consoleMsg("", "{green}dispname name {lightpurple}<displayname> {white}- Get the real account name.", false);
				getChat().consoleMsg("", "{green}general reload {white}- Reloads the settings file(motd/mysql).", false);
				getChat().consoleMsg("", "{green}general save-players {white}- Saves all current players inventory.", false);
				getChat().consoleMsg("", "{green}tp {lightpurple}<name> <name> {white}- Teleport first player to second.", false);
			}
			else
			{
				getChat().playerMsg(player, CHATTITLE, "Commands - Key {green}cmd {aqua}[optional] {lightpurple}<req>", false);
				getChat().playerMsg(player, "", "{green}/motd {white}- Displays the Message of the Day.", false);
				getChat().playerMsg(player, "", "{green}/list {white}- List all online players.", false);
				getChat().playerMsg(player, "", "{green}/dispname {lightpurple}<name>{white}- Change your Display name.", false);
				
				if(player.hasPermission("general.gift"))
					getChat().playerMsg(player, "", "{green}/gift {aqua}[name] {lightpurple}<id> {aqua}[qty] [stacks] [durab] {white}- Give items to somone", false);
				if(player.hasPermission("general.dispname"))
				{
					getChat().playerMsg(player, "", "{green}/dispname history {lightpurple}<name>{white}- List the history of somones names.", false);
					getChat().playerMsg(player, "", "{green}/dispname name {lightpurple}<displayname>{white}- Get the real account name.", false);
				}
				if(player.hasPermission("general.general"))
				{
					getChat().playerMsg(player, "", "{green}/general reload{white}- Reloads the settings file(motd/mysql).", false);
					getChat().playerMsg(player, "", "{green}/general save-players{white}- Saves all current players inventory.", false);
				}
				if(player.hasPermission("general.tele"))
				{
					getChat().playerMsg(player, "", "{green}/tp {lightpurple}<name> {aqua}<name> {white}- Teleport you to player, or player to player.", false);
					getChat().playerMsg(player, "", "{green}/tpme {lightpurple}<name> {white}- Teleport player to you.", false);
				}
			}
			return true;
		}

		if(command.equalsIgnoreCase("motd"))
		{
			if(isServer)
				motd(null);
			else
				motd(player);
			return true;
		}

		if(command.equalsIgnoreCase("general") && args.length == 1)
		{
			if(isServer)
			{
				if(args[0].equalsIgnoreCase("reload"))
				{
					settings.loadSettings();
					getChat().consoleMsg(CHATTITLE, "Settings Reloaded", false);
					return true;
				}
				else if(args[0].equalsIgnoreCase("save-players"))
				{
					getServer().savePlayers();
					getChat().consoleMsg(CHATTITLE, "{gold}Console{white} - All Players saved.", false);
					getChat().globalMsg(CHATTITLE, "{gold}Console{white} - All Players saved.", false);
					return true;
				}
			}
			else if(player.hasPermission("general.general"))
			{
				if(args[0].equalsIgnoreCase("reload"))
				{
					settings.loadSettings();
					getChat().playerMsg(player, CHATTITLE, "Settings Reloaded", false);
					return true;
				}
				else if(args[0].equalsIgnoreCase("save-players"))
				{
					getServer().savePlayers();
					getChat().globalMsg(CHATTITLE, "{lightpurple}Manual{white} - All Players saved.", false);
					return true;
				}
			}
			return false;
		}

		if(command.equalsIgnoreCase("gift") && args.length >= 1 && (isServer || player.hasPermission("general.gift")))
		{
			String name;
			int id;
			int qty = 1;
			int stacks = 1;
			short durability = 0;

			if(args.length >= 1 && args[0].matches("([0-9]+)") && !isServer)
			{
				id = Integer.parseInt(args[0]);
				if(args.length >= 2)
					qty = Integer.parseInt(args[1]);
				if(args.length >= 3)
					stacks = Integer.parseInt(args[2]);
				give(player, id, qty, stacks, durability);
				
				getChat().consoleMsg(CHATTITLE, "{green}"+player.getName()+"{white} his/her self Item: {lightpurple}"+Material.getMaterial(id)+"{white} Qty: {lightpurple}"+qty+"{white} Stacks: {lightpurple}"+stacks+"{white} Dura: {lightpurple}"+durability, false);
				
				return true;
			}
			else if(args.length >= 2)
			{
				toPlayer = getPlayer(args[0]);
				if(toPlayer == null)
				{
					if(!isServer)
						getChat().playerMsg(player, CHATTITLE, "{red}No player found with the name {ligtpurple}"+args[0], false);
					else
						getChat().consoleMsg(CHATTITLE, "{red}No player found with the name {ligtpurple}"+args[0], false);
					return true;
				}
				else if(!toPlayer.isOnline())
				{
					if(!isServer)
						getChat().playerMsg(player, CHATTITLE, "{red}Player is no longer online.", false);
					else
						getChat().consoleMsg(CHATTITLE, "{red}Player is no longer online.", false);
					return true;
				}

				id = Integer.parseInt(args[1]);
				if(args.length >= 3)
					qty = Integer.parseInt(args[2]);
				if(args.length >= 4)
					stacks = Integer.parseInt(args[3]);
				if(args.length >= 5)
					durability = Short.parseShort(args[4]);
				give(toPlayer, id, qty, stacks, durability);
				
				getChat().consoleMsg(CHATTITLE, "{green}"+player.getName()+"{white} gave {aqua}"+toPlayer.getName()+"{white} Item: {lightpurple}"+Material.getMaterial(id)+"{white} Qty: {lightpurple}"+qty+"{white} Stacks: {lightpurple}"+stacks+"{white} Dura: {lightpurple}"+durability, false);
				
				return true;
			}
			return false;

		}
		else if(command.equalsIgnoreCase("gift") && args.length == 0  && isServer)
		{
			getChat().consoleMsg(CHATTITLE, "{red}Invalid Syntax {green}/gift {aqua}[name] {lightpurple}<id> {aqua}[qty] [stacks]", false);
			return true;
		}
		else if(command.equalsIgnoreCase("gift") && args.length == 0 && player.hasPermission("general.gift") && !isServer)
		{
			getChat().playerMsg(player, CHATTITLE, "{red}Invalid Syntax please review {green}/info admin{white}.", false);
			return true;
		}

		if(command.equalsIgnoreCase("list"))
		{
			if(isServer)
			{
				getChat().consoleMsg(CHATTITLE, "Online Players:", false);
				getChat().consoleMsg(CHATTITLE, getOnlinePlayers(), false);
			}
			else
			{
				getChat().playerMsg(player, CHATTITLE, "Online Players:", false);
				getChat().playerMsg(player, CHATTITLE, getOnlinePlayers(), false);
			}
			return true;
		}

		if(command.equalsIgnoreCase("dispname") && args.length >= 1)
		{
			SQL sql = new SQL(this);

			if((isServer || player.hasPermission("general.dispname")) && args.length >= 2)
			{
				if(args[0].equalsIgnoreCase("history"))
				{
					String history = sql.sDisplay(args[1], false);
					if(!history.isEmpty())
					{
						if(isServer)
						{
							getChat().consoleMsg(CHATTITLE, "Name History of {green}" + args[1], false);
							getChat().consoleMsg(CHATTITLE, history, true, true, 0);
						}
						else
						{
							getChat().playerMsg(player, CHATTITLE, "Name History of {green}" + args[1], false);
							getChat().playerMsg(player, CHATTITLE, history, true, true, 1);
						}
					}
					else
					{
						if(isServer)
							getChat().consoleMsg(CHATTITLE, "{red}Error{white} No history for player {lightpurple}" + args[1] + "{white}.", false);
						else
							getChat().playerMsg(player, CHATTITLE, "{red}Error{white} No history for player {lightpurple}" + args[1] + "{white}.", false);
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("name"))
				{
					String playerName = sql.sDisplayPlayer(args[1]);
					if(!playerName.isEmpty())
					{
						if(isServer)
							getChat().consoleMsg(CHATTITLE, "{green}" + args[1] + "'s {white real name is {lightpurple}" + playerName + "{white}.", false);
						else
							getChat().playerMsg(player, CHATTITLE, "{green}" + args[1] + "'s {white}real name is {lightpurple}" + playerName + "{white}.", false);
					}
					else
					{
						if(isServer)
							getChat().consoleMsg(CHATTITLE, "{red}Error{white} Unable to find player named {lightpurple}" + args[1] + "{white}.", false);
						else
							getChat().playerMsg(player, CHATTITLE, "{red}Error{white} Unable to find player named {lightpurple}" + args[1] + "{white}.", false);
					}
					return true;
				}
			}
			else if(!isServer && args.length == 1)
			{
				String history = sql.sDisplay(player.getName(), false);
				if(!history.isEmpty())
				{
					if(!history.contains(player.getDisplayName()))
						history += ", " + player.getDisplayName();

					sql.uDisplay(player.getName(), args[0], history);
					getChat().globalMsg(CHATTITLE, "{green}" +player.getDisplayName()+" {white}has changed names to {lightpurple}" + args[0] +"{white}.", false);
				}
				else
				{
					history = player.getName();
					sql.iDisplay(player.getName(), args[0], history);
					getChat().globalMsg(CHATTITLE, "{green}" +player.getName()+" {white}has changed names to {lightpurple}" + args[0] +"{white}.", false);
				}

				player.setDisplayName(args[0]);
				return true;
			}
			return false;
		}
		
		if(command.equalsIgnoreCase("tp") && args.length >= 1 && (isServer || player.hasPermission("general.tele")))
		{
			if(args.length == 1 && !isServer)
			{
				toPlayer = getPlayer(args[0]);
				if(!handlePlayerCheck(player, isServer, toPlayer, args[0]))
					return true;
			}
			else if(args.length == 2)
			{
				toPlayer = getPlayer(args[1]);
				if(!handlePlayerCheck(player, isServer, toPlayer, args[1]))
					return true;
			}
			
			if(args.length == 1 && !isServer)
			{
				player.teleport(toPlayer);
				getChat().globalMsg(CHATTITLE, "{green}"+player.getDisplayName()+" {white}teleported to {lightpurple}"+toPlayer.getDisplayName(), isServer);
				return true;
			}
			else if(args.length == 2)
			{
				Player telePlayer = getPlayer(args[0]);
				if(!handlePlayerCheck(player, isServer, telePlayer, args[0]))
					return true;
				telePlayer.teleport(toPlayer);
				getChat().globalMsg(CHATTITLE, "{green}"+telePlayer.getDisplayName()+" {white}was teleported to {lightpurple}"+toPlayer.getDisplayName(), isServer);
				return true;
			}
			return false;			
		}
		
		if(command.equalsIgnoreCase("tpme") && args.length == 1 && !isServer && player.hasPermission("general.tele"))
		{
			toPlayer = getPlayer(args[0]);
			if(!handlePlayerCheck(player, isServer, toPlayer, args[0]))
				return true;
			
			toPlayer.teleport(player);
			getChat().globalMsg(CHATTITLE, "{green}"+toPlayer.getDisplayName()+" {white}was teleported to {lightpurple}"+player.getDisplayName(), isServer);
			return true;
		}
		return false;
	}

	public Chat getChat()
	{
		return chat;
	}

	public long getCurTimestamp()
	{
		return System.currentTimeMillis()/1000;
	}

	public String getOnlinePlayers()
	{
		String playersList = "";
		Player[] onlinePlayers = this.getServer().getOnlinePlayers();

		for (Player onlinePlayer: onlinePlayers)
		{
			playersList += (onlinePlayer.hasPermission("general.op")) ? "{darkaqua}" + onlinePlayer.getDisplayName() + ", " : "{darkgreen}" + onlinePlayer.getDisplayName() + ", ";
		}
		
		if(onlinePlayers.length > 0)
		{
			playersList = playersList.substring(0, playersList.lastIndexOf(", "));
			return playersList;
		}
		else
			return "No Players Found";
	}

	public String motdFilter(String motd, Player player)
	{
		if(player == null)
		{
			motd = motd.replaceAll("%player", "Console");
			motd = motd.replaceAll("%display", "Console");
		}
		else
		{
			motd = motd.replaceAll("%player", player.getName());
			motd = motd.replaceAll("%display", player.getDisplayName());
		}
		return motd;
	}

	public void motd(Player player)
	{
		if(!settings.motd_1.isEmpty())
			if(player == null)
				getChat().consoleMsg( "", motdFilter(settings.motd_1, player), false);
			else
				getChat().playerMsg(player, "", motdFilter(settings.motd_1, player), false);
		if(!settings.motd_2.isEmpty())
			if(player == null)
				getChat().consoleMsg("", motdFilter(settings.motd_2, player), false);
			else
				getChat().playerMsg(player, "", motdFilter(settings.motd_2, player), false);
		if(!settings.motd_3.isEmpty())
			if(player == null)
				getChat().consoleMsg("",motdFilter(settings.motd_3, player), false);
			else
				getChat().playerMsg(player, "",motdFilter(settings.motd_3, player), false);
	}

	public void give(Player player, int id, int qty, int stacks, short durability)
	{
		if(Material.getMaterial(id) == null)
		{
			getChat().playerMsg(player, CHATTITLE, "{red}Invalid item id {lightpurple}"+id, false);
			return;
		}
		for(int i=0;i<stacks;i++)
		{
			ItemStack itemstack = new ItemStack(id);
			itemstack.setAmount(qty);
			if (durability != 0)
				itemstack.setDurability(durability);
			player.getInventory().addItem(new ItemStack[] { itemstack });
		}
	}

	public String getRealName(String name)
	{
		SQL sql = new SQL(this);
		String playerName = sql.sDisplayPlayer(name);
		if(!playerName.isEmpty())
			return playerName;
		return "";
	}
	
	public Player getPlayer(String name)
	{
		Player player = getServer().getPlayer(name);
		if(player == null)
		{
			name = getRealName(name);
			if(!name.isEmpty())
				 player = getServer().getPlayer(name);
		}
		return player;
	}
	
	public boolean handlePlayerCheck(Player cmdPlayer, boolean isServer, Player player, String playerName)
	{
		if(player == null)
		{
			if(!isServer)
				getChat().playerMsg(cmdPlayer, CHATTITLE, "{red}No player found with the name {ligtpurple}"+playerName, false);
			else
				getChat().consoleMsg(CHATTITLE, "{red}No player found with the name {ligtpurple}"+playerName, false);
			return false;
		}
		else if(!player.isOnline())
		{
			if(!isServer)
				getChat().playerMsg(cmdPlayer, CHATTITLE, "{red}Player is no longer online.", false);
			else
				getChat().consoleMsg(CHATTITLE, "{red}Player is no longer online.", false);
			return false;
		}
		return true;
	}
}

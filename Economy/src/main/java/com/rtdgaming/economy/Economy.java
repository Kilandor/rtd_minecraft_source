package com.rtdgaming.economy;

import java.io.File;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

//import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;

import com.kilandor.chat.Chat;
import com.rtdgaming.credits.Credits;
import com.rtdgaming.economy.Transaction.TransactionType;
import com.rtdgaming.economy.eventcatchers.*;
import org.bukkit.Location;

/**
 * Economy for Bukkit
 *
 * @author Kilandor, CzechErface
 */
@SuppressWarnings("unused")
public class Economy extends JavaPlugin
{
	public static final String VERSION = "1.0";
	public static final String CHATTITLE = "[{blue}Economy{white}] ";

	public static final Logger log = Logger.getLogger("Minecraft");

	private TransactionHandler transactionHandler;

	private Configuration config;
	private Chat chat;

	private PlayerInteract playerInteract;
	private BlockPlace blockPlace;

	public void onEnable()
	{
		System.out.println("Economy version " + VERSION + " is enabled!");

		chat = new Chat(this.getServer());
		Transaction.setChat(chat);

		config = new Configuration(new File(this.getDataFolder(), "economy.cfg"));
		config.load();
		ConfigurationParser.getInstance().setConfiguration(config);

		SQL.initialize(this, config.getNode("mysql"));

		initializeVariables();
		registerHooks();
	}
	
	public void onDisable()
	{
		SQL.destroy();
	}

	public void initializeVariables()
	{
		transactionHandler = TransactionHandler.getInstance();
		transactionHandler.initialize(this);
		//Create all the listeners
		playerInteract = new PlayerInteract(this);
		blockPlace = new BlockPlace(this);

	}
	public void registerHooks()
	{
		PluginManager pm = getServer().getPluginManager();
		//pm.registerEvent(Event.Type.PLAYER_COMMAND, playerCommands, Priority.Monitor, this);
		//pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityDamaged, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockPlace, Event.Priority.Normal, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Player player = null;
		boolean isServer = false;
		if (sender instanceof Player)
			player = (Player) sender;
		else
			isServer = true;

		String command = cmd.getName();

		if (isServer && command.equalsIgnoreCase("econ") && args.length == 1 && args[0].equalsIgnoreCase("info"))
		{
			transactionHandler.printBlockConfig();
			return true;
		}
		
		//Player only commands begin here
		if (isServer) return false;
		
		/** BEGIN ALL PLAYER COMMANDS **/
		
		if(command.equalsIgnoreCase("buy") && player != null)
		{
			TransactionType type = transactionHandler.getTransactionType(player.getName());
			if (type != TransactionType.BUY_MANUAL_PENDING && type != TransactionType.BUY_MANUAL_READY) {
				chat.playerMsg(player, Economy.CHATTITLE, "You do not have a {red}manual buy{white} transaction pending.", false);
				return false;
			}
			
			if (args.length == 0) {
				chat.playerMsg(player, Economy.CHATTITLE, "Usage: /buy Item_ID Amount [Modifier]", false);
				return false;
			} else if (args.length < 2 || args.length > 4) {
				transactionHandler.explainManualBuy(player);
				return false;
			}
			
			try
			{
				short durability = args.length == 3 ? Short.parseShort(args[2]) : 0;
				transactionHandler.updateTransaction(player.getName(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), durability);
			}
			catch (NumberFormatException nfe)
			{
				chat.playerMsg(player, Economy.CHATTITLE, "You messed up and wrote some strange stuff.  Try again.", false);
				return false;
			}
			
			return true;
		}
		
		if (!player.isOp())
			return false;
		
		/** BEGIN OP ONLY COMMANDS **/
		
		if (command.equalsIgnoreCase("econ") && args.length >= 1)
		{
			if (args[0].equalsIgnoreCase("reload"))
			{
				config.load();
				transactionHandler.reload();
				Transaction.resetDuraBlockConfig();
				return true;
			}
		}
		
		return false;
	}

	public Chat getChat()
	{
		return chat;
	}
	
	public boolean isInExchange(Location loc)
	{
		double x1 = loc.getX();
		double y1 = loc.getY();
		double z1 = loc.getZ();

		List<ConfigurationNode> exchanges = config.getNodeList("exchanges", null);
		if(exchanges == null)
			return false;

		for(ConfigurationNode blocks : exchanges)
		{
			ConfigurationNode location = blocks.getNode("location");

			double maxDist = location.getDouble("maxdistance", -1);
			double x2 = location.getDouble("x", -1);
			double y2 = location.getDouble("y", -1);
			double z2 = location.getDouble("z", -1);

			double distance = Math.sqrt(Math.pow((x1-x2),2.0) + Math.pow((y1-y2),2.0) + Math.pow((z1-z2),2.0));
			if(distance <= maxDist)
				return true;
		}
		return false;
	}
}

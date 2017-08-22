package com.rtdgaming.rtd;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Timer;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.kilandor.chat.Chat;

import com.rtdgaming.rtd.eventcatchers.*;
import com.rtdgaming.rtd.rolls.*;
import com.rtdgaming.rtd.spout.*;

import com.rtdgaming.rtd.spout.RollTimerHandler;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * Roll the Dice(RTD) for Bukkit
 * 
 * @author Kilandor
 */
public class RTD extends JavaPlugin
{
	/* Special variables */
	public static final String VERSION = "0.1";
	public static final String CHATTITLE = "[{blue}RTD{white}] ";
	public static Logger log;
	public static RTD rtd;
	//TODO: Rework to use Bukkit Config
	public Settings settings;
	private final Timer timer = new Timer();
	/* Objects for hooked events */
	private PlayerCommands playerCommands;
	private PlayerJoin playerJoin;
	private EntityDamag entityDamage;
	private BlockPlace blockPlace;
	private SpoutWatcher spoutWatcher;
	/* Other objects */
	private File folderPath;
	private Chat chat;
	public World defaultWorld;
	
	//TODO: Expand later simple for now
	private HashMap<String, RollTimerHandler> spoutWidgets = new HashMap<String, RollTimerHandler>();

	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		folderPath = getDataFolder();
		Settings.config_file = folderPath.getAbsolutePath() + File.separator
				+ Settings.config_file;
		chat = new Chat(this.getServer());

		initializeVariables();
		//Spammy Annoying
		//System.out.println("RTD loaded with the following rolls:\n------------------------------\n" + _RollsEnum.getRollsString() + "------------------------------");
		registerHooks();

		timer.scheduleAtFixedRate(new GenericTimerTask(this), 1000, 1000);

		// Create folders and files
		if(!folderPath.exists())
			folderPath.mkdir();
		File configFile = new File(Settings.config_file);
		if(!configFile.exists())
			try
			{
				configFile.createNewFile();
				settings.saveDefaultSettings();
			}
			catch(IOException ex)
			{
			}
		settings.loadSettings();

		SQL.initialize(this);
	}

	public void initializeVariables()
	{
		//The core stuff
		rtd = this;
		settings = new Settings();

		//Create all the listeners
		playerCommands = new PlayerCommands(this);
		playerJoin = new PlayerJoin(this);
		entityDamage = new EntityDamag(this);
		blockPlace = new BlockPlace(this);
		spoutWatcher = new SpoutWatcher(this);

		//Set up all the worlds
		defaultWorld = this.getServer().getWorlds().get(0);

		//Initialize static stuff
		Roller.initialize(this);
		Block.initialize();
		SoundHandler.initialize(this);
	}

	public void registerHooks()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerJoin, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityDamage, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_COMBUST, entityDamage, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockPlace, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, spoutWatcher, Event.Priority.Low, this);
	}

	public void onDisable()
	{
		timer.cancel();
		SQL.destroy();
		//Cleanup and remove widgets on player for this plugin
		Player[] onlinePlayers = this.getServer().getOnlinePlayers();
		for(Player onlinePlayer: onlinePlayers)
		{
			SpoutPlayer sPlayer = (SpoutPlayer)onlinePlayer;
			if(sPlayer.isSpoutCraftEnabled() && spoutWidgets.containsKey(sPlayer.getName()))
				spoutWidgets.get(sPlayer.getName()).removeTimer();
		}
		spoutWidgets.clear();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		//This class is necessary to keep the player commands modular.
		return playerCommands.onCommand(sender, cmd, commandLabel, args);
	}

	public long getCurTimestamp()
	{
		return System.currentTimeMillis() / 1000;
	}

	public Chat getChat()
	{
		return chat;
	}
	
	public HashMap getWidgetMap()
	{
		return spoutWidgets;
	}
	
	public Color newColor(int r, int g, int b)
	{
		return new Color(r/255.0f, g/255.0f, b/255.0f);
	}
}

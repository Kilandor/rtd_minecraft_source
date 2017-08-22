package com.kilandor.antihack;

import com.kilandor.chat.Chat;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Roll the Dice(RTD) for Bukkit
 *
 * @author Kilandor
 */
public class AntiHack extends JavaPlugin
{
	private final PlayerCommands playerCommands = new PlayerCommands(this);
	//private final PlayerMove playerMove = new PlayerMove(this);
	private final BlockDamaged blockDamaged = new BlockDamaged(this);

	public static final String VERSION = "0.1";
	public static final String CHATTITLE = "[{blue}AntiHack{white}] ";

	private Chat chat;
	public static Logger log;
	private Timer tick;
	private File folderPath;

	public HashMap<String, MinMax> minMaxHash = new HashMap<String, MinMax>();

	Settings settings = new Settings();

	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");

		//folderPath = getDataFolder();
		//settings.config_file = folderPath.getAbsolutePath() + File.separator + settings.config_file;

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_COMMAND, playerCommands, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED,  blockDamaged, Event.Priority.Monitor, this);
		//pm.registerEvent(Event.Type.PLAYER_MOVE, playerMove, Event.Priority.Monitor, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

		//Timer timer = new Timer(this);
		//this.tick = new Timer();
		//this.tick.schedule(timer, 0L, 1000);
		/*
		//Create folders and files
		if(!folderPath.exists())
			folderPath.mkdir();
		File configFile = new File(settings.config_file);
		if(!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
				settings.saveDefaultSettings();
			} catch (IOException ex){ }
		}
		settings.loadSettings();
		 * 
		 */
		chat = new Chat(this.getServer());
	}
	public void onDisable()
	{
	}

	public long getCurMillis()
	{
		return System.currentTimeMillis();
	}

	public long getMillisDiff(long startMillis)
	{
		return getCurMillis() - startMillis;
	}

	public void addMinMax(String block, MinMax minMax)
	{
		minMaxHash.put(block, minMax);
	}

	public MinMax getMinMax(String block)
	{
		if(minMaxHash.containsKey(block))
			return (MinMax) minMaxHash.get(block);
		else
			return null;
	}


}

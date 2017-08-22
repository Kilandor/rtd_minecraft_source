package com.kilandor.daynight;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * DayNight for Bukkit
 *
 * @author Kilandor
 */
public class DayNight extends JavaPlugin
{
	private final PlayerCommands playerListener = new PlayerCommands(this);
	//private final DayNightBlockListener blockListener = new DayNightBlockListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	public static final String VERSION = "1.0";
	public static final String CHATTITLE = "[{blue}DayNight{white}] ";
	private Timer tick;
	private File folderPath;

	Settings settings = new Settings();

	public void onEnable()
	{
		folderPath = getDataFolder();
		settings.config_file = folderPath.getAbsolutePath() + File.separator + settings.config_file;

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Monitor, this);

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );

		TimeCycle timeCycle = new TimeCycle(this);
		this.tick = new Timer();
		this.tick.schedule(timeCycle, 0L, 1000);
		
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
	}
	public void onDisable()
	{
		// TODO: Place any custom disable code here

		// NOTE: All registered events are automatically unregistered when a plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		System.out.println("Goodbye world!");
	}
	public boolean isDebugging(final Player player)
	{
		if (debugees.containsKey(player))
			return debugees.get(player);
		else
			return false;
	}

	public void setDebugging(final Player player, final boolean value)
	{
		debugees.put(player, value);
	}

	public long getRelativeTime()
	{
		return (getServer().getWorlds().get(0).getTime() % 24000);
	}

	public void setRelativeTime(long time)
	{
		long margin = (time-getServer().getWorlds().get(0).getTime()) % 24000;

		if (margin < 0)
			margin += 24000;

		getServer().getWorlds().get(0).setTime(getServer().getWorlds().get(0).getTime()+margin);
	}
}

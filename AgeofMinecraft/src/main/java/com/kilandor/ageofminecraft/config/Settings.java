package com.kilandor.ageofminecraft.config;

import com.kilandor.ageofminecraft.AgeofMinecraft;
import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class Settings
{
	private AgeofMinecraft plugin;
	
	private ConfigHelper config;
	private ConfigHelper townCfg;
	private ConfigHelper outpostCfg;
	private ConfigHelper hutCfg;
	
	private Map<String, Object> configMap = new LinkedHashMap<String,Object>();
	private Map<String, Object> townMap = new LinkedHashMap<String,Object>();
	private Map<String, Object> outpostMap = new LinkedHashMap<String,Object>();
	private Map<String, Object> hutMap = new LinkedHashMap<String,Object>();
	
	public Settings(AgeofMinecraft plugin)
	{
		this.plugin = plugin;
		
		loadConfig();
		loadTowns();
		loadOutposts();
		loadHuts();
	}
	
	public void loadDefault(File file, String jarPath)
	{
		try
		{
			file.createNewFile();
			File jarLoc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
			if(jarLoc.isFile())
			{
				JarFile jar = new JarFile(jarLoc);
				JarEntry entry = jar.getJarEntry(jarPath);
				if(entry!=null && !entry.isDirectory())
				{
					InputStream in = jar.getInputStream(entry);

					FileOutputStream out = new FileOutputStream(file);
					byte[] tempbytes = new byte[512];
					int readbytes = in.read(tempbytes,0,512);
					while(readbytes>-1)
					{
						out.write(tempbytes,0,readbytes);
						readbytes = in.read(tempbytes,0,512);
					}
					out.close();
					in.close();
					plugin.getChat().consoleMsg(AgeofMinecraft.CHATTITLE, "Created default config file {green}" + file, false);
				}
			}
			else
				plugin.getChat().consoleMsg(AgeofMinecraft.CHATTITLE, "{red}Error{white}: Unable to find {green}" + jarPath + " {white}in jar.", false);
		}
		catch (Exception ex)
		{
			plugin.getChat().consoleMsg(AgeofMinecraft.CHATTITLE, "{red}Error: {white}Failed to write file {green}" + jarPath + " from jar. {red}" + ex, false);
		}
	}
	
	public void loadConfig()
	{
		File file = new File(plugin.getDataFolder(), "ageofminecraft.cfg");
		if(!file.exists())
			loadDefault(file, "config/ageofminecraft.cfg");
		config = new ConfigHelper(file);
		config.load();
	}
	
	public void loadTowns()
	{
		File file = new File(plugin.getDataFolder(), "towns.cfg");
		if(!file.exists())
			loadDefault(file, "config/towns.cfg");
		townCfg = new ConfigHelper(file);
		townCfg.load();
		
		townMap = townCfg.getMap("Towns");
		if(townMap == null)
			townMap = new LinkedHashMap<String, Object>();
	}
	
	public void loadOutposts()
	{
		File file = new File(plugin.getDataFolder(), "outposts.cfg");
		if(!file.exists())
			loadDefault(file, "config/outposts.cfg");
		outpostCfg = new ConfigHelper(file);
		outpostCfg.load();
		
		outpostMap = outpostCfg.getMap("Outposts");
		if(outpostMap == null)
			outpostMap = new LinkedHashMap<String, Object>();
	}
	
	public void loadHuts()
	{
		File file = new File(plugin.getDataFolder(), "huts.cfg");
		if(!file.exists())
			loadDefault(file, "config/huts.cfg");
		hutCfg = new ConfigHelper(file);
		hutCfg.load();
		
		hutMap = hutCfg.getMap("Huts");
		if(hutMap == null)
			hutMap = new LinkedHashMap<String, Object>();
	}
	
	public ConfigHelper getConfig()
	{
		return config;
	}
	
	public ConfigHelper getTowns()
	{
		return townCfg;
	}
	
	public ConfigHelper getOutposts()
	{
		return outpostCfg;
	}
	
	public ConfigHelper getHuts()
	{
		return hutCfg;
	}
	
	public void handleTown(String townName, Location loc1, Location loc2, Player player, String size, boolean update)
	{
		Map<String,Object> newTown = new LinkedHashMap<String,Object>();
		
		if(update)
			if(townMap.containsKey(townName))
				newTown = (Map) townMap.get(townName);
		
		newTown.put("Name", townName);
		newTown.put("Owner", player.getName());
		newTown.put("Size", size);
		newTown.put("x1", loc1.getBlockX());
		newTown.put("y1", loc1.getBlockY());
		newTown.put("z1", loc1.getBlockZ());
		newTown.put("x2", loc2.getBlockX());
		newTown.put("y2", loc2.getBlockY());
		newTown.put("z2", loc2.getBlockZ());
		
		townMap.put(townName.toLowerCase(), newTown);

		townCfg.addMap("Towns", townMap);
		townCfg.save();
	}
	
	public void delTown(String townName)
	{
		Map<String, Object> tmpTown = townCfg.getMap("Towns");
		
		if(tmpTown == null)
			tmpTown = new LinkedHashMap<String, Object>();
		
		tmpTown.remove(townName.toLowerCase());
		
		townCfg.addMap("Towns", tmpTown);
		townCfg.save();
	}
}

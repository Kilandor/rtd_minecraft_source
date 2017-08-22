package com.kilandor.ageofminecraft;

import com.kilandor.ageofminecraft.config.Settings;
import com.kilandor.chat.Chat;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.ConfigurationNode;

/**
 * Age of Minecraft (AoM) for Bukkit
 *
 * @author Kilandor
 */
public class AgeofMinecraft extends JavaPlugin
{

	public static final String VERSION = "0.1";
	public static final String CHATTITLE = "[{blue}AoM{white}] ";

	public static AgeofMinecraft AoM;
	private Chat chat;
	private Settings settings;
	public static Logger log;
	//private final Timer timer = new Timer();

	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		chat = new Chat(getServer());
		
		initializeVariables();
		registerHooks();

		PluginManager pm = getServer().getPluginManager();
		
		if(!getDataFolder().isDirectory())
			getDataFolder().mkdirs();
		
		chat.consoleMsg(CHATTITLE, "Version {blue}" + VERSION + " {green}Successfully Enabled", false);

		//timer.scheduleAtFixedRate(new GenericTimerTask(this), 1000, 1000);
	}

	public void initializeVariables()
	{
		AoM = this;
		settings = new Settings(this);
	}

	public void registerHooks()
	{
		PluginManager pm = getServer().getPluginManager();
	}

	public void onDisable()
	{
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
		
		if(command.equalsIgnoreCase("town") && !isServer && args.length >= 1)
		{
			if(args.length == 3 && args[0].equalsIgnoreCase("create"))
			{
				String type = getStructureType(player);
				if(!args[1].matches("(small|medium|large)"))
				{
					chat.playerMsg(player, CHATTITLE, "Town size must be {gold}small{white}, {gold}medium{white} or {gold}large{white}", false);
					return false;
				}
				else if(args[2].matches("([a-zA-Z0-9_-]+)"))
				{
					chat.playerMsg(player, CHATTITLE, "Town names may only include a-Z, 0-9, \'-\', and \'_\'", false);
					return false;
				}
				else if(!settings.getConfig().existsMap("Town.Templates."+player.getWorld().getName()))
				{
					chat.playerMsg(player, CHATTITLE, "{red}Error:{white} No template found for world {gold}"+player.getWorld().getName(), false);
					return false;
				}
				else if(!settings.getConfig().existsMap("Town.Templates."+player.getWorld().getName()+"."+args[1]))
				{
					chat.playerMsg(player, CHATTITLE, "{red}Error:{white} No template found for size {gold}"+args[1], false);
					return false;
				}
				else if(!settings.getConfig().existsMap("Town.Templates."+player.getWorld().getName()+"."+args[1]+"."+type))
				{
					chat.playerMsg(player, CHATTITLE, "{red}Error:{white} No template found for type {gold}"+type, false);
					return false;
				}
				Town.create(player, args[1], args[2], type);
				return true;
			}
			else if(args[0].equalsIgnoreCase("delete"))
			{
				if(args[1].matches("([a-zA-Z0-9_-]+)"))
					Town.delete(player, args[1]);
				else
				{
					chat.playerMsg(player, CHATTITLE, "Town names may only include a-Z, 0-9, \'-\', and \'_\'", false);
					return false;
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("checkexists"))
			{
				if(args[1].matches("([a-zA-Z0-9_-]+)"))
				{
					if(Town.checkExists(args[1]))
						chat.playerMsg(player, CHATTITLE, "Town named {gold}" + args[1] + " {green}exists", false);
					else
						chat.playerMsg(player, CHATTITLE, "Town named {gold}" + args[1] + " {red}does not exist", false);
				}
				else
				{
					chat.playerMsg(player, CHATTITLE, "Town names may only include a-Z, 0-9, \'-\', and \'_\'", false);
					return false;
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("checktype"))
			{
				chat.playerMsg(player, CHATTITLE, "Your current location would create a {gold}"+getStructureType(player)+" Town", false);
				return true;
			}
		}
		return false;
	}

	public Chat getChat()
	{
		return chat;
	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public LinkedList<Location> copyStructure(String building, String type, String size, Location center, World world)
	{
		int templateX1 = settings.getConfig().getInt(building+".Templates."+world.getName()+"."+size+"."+type+".x1", -1);	int templateX2 = settings.getConfig().getInt(building+".Templates."+world.getName()+"."+size+"."+type+".x1", -1);
		int templateY1 = settings.getConfig().getInt(building+".Templates."+world.getName()+"."+size+"."+type+".y1", -1);	int templateY2 = settings.getConfig().getInt(building+".Templates."+world.getName()+"."+size+"."+type+".y2", -1);
		int templateZ1 = settings.getConfig().getInt(building+".Templates."+world.getName()+"."+size+"."+type+".z1", -1);	int templateZ2 = settings.getConfig().getInt(building+".Templates."+world.getName()+"."+size+"."+type+".z2", -1);
		int templateCY = settings.getConfig().getInt(building+".Templates."+world.getName()+"."+size+"."+type+".cY", -1);

		Location dest1 = new Location(world, center.getBlockX() - Math.abs((templateX1 - templateX2) /2), center.getBlockY() - Math.abs(templateY1 - templateCY), center.getBlockZ() - Math.abs((templateZ1 - templateZ2) /2));
		Location dest2 = new Location(world, center.getBlockX() + Math.abs((templateX1 - templateX2) /2), center.getBlockY() + Math.abs(templateY2 - templateCY), center.getBlockZ() + Math.abs((templateZ1 - templateZ2) /2));
		
		//getChat().consoleMsg(AgeofMinecraft.CHATTITLE, "Dest1 |"+dest1.getBlockX()+" "+dest1.getBlockY()+" "+dest1.getBlockZ(), false);
		//getChat().consoleMsg(AgeofMinecraft.CHATTITLE, "Dest2 |"+dest2.getBlockX()+" "+dest2.getBlockY()+" "+dest2.getBlockZ(), false);
		
		boolean destXMod = dest1.getBlockX() < dest2.getBlockX();	boolean templateXMod = templateX1 < templateX2;
		boolean destYMod = dest1.getBlockY() < dest2.getBlockY();	boolean templateYMod = templateY1 < templateY2;
		boolean destZMod = dest1.getBlockZ() < dest2.getBlockZ();	boolean templateZMod = templateZ1 < templateZ2;
		
		int sizeX = Math.abs(templateX1 - templateX2);
		int sizeY = Math.abs(templateY1 - templateY2);
		int sizeZ = Math.abs(templateX1 - templateX2);
		
		for (int destX = 0; destXMod ? destX <= sizeX : destX >= sizeX; destX += (destXMod ? 1 : -1))
			for (int destY = 0; destYMod ? destY <= sizeY: destY >= sizeY; destY += (destYMod ? 1 : -1))
				for (int destZ = 0; destZMod ? destZ <= sizeZ : destZ >= sizeZ; destZ += (destZMod ? 1 : -1))
				{
					int templateX = (destXMod == templateXMod) ? destX : (destX * -1);
					int templateY = (destYMod == templateYMod) ? destY : (destY * -1);
					int templateZ = (destZMod == templateZMod) ? destZ : (destZ * -1);
					
					Block templateBlock = center.getWorld().getBlockAt(templateX1 + templateX, templateY1 + templateY, templateZ1 + templateZ);
					Block destBlock = center.getWorld().getBlockAt(dest1.getBlockX() + destX, dest1.getBlockY() + destY, dest1.getBlockZ() + destZ);
					
					if((destBlock.getType() == Material.BEDROCK) || (destBlock.getType() == Material.CHEST))
						continue;
					
					destBlock.setTypeIdAndData(templateBlock.getTypeId(), templateBlock.getData(), false);
					//getChat().consoleMsg(AgeofMinecraft.CHATTITLE, "Dest |"+destBlock.getX()+" "+destBlock.getY()+" "+destBlock.getZ(), false);
				}
		LinkedList<Location> destinations = new LinkedList<Location>();
		destinations.add(dest1);
		destinations.add(dest2);
		return destinations;
	}
	
	public String getStructureType(Player player)
	{
		int x1 = player.getLocation().getBlockX();
		int z1 = player.getLocation().getBlockZ();
		
		x1 =  x1 - 1;	int x2 = x1 + 2;
		int y = player.getLocation().getBlockY();
		z1 = z1 - 1;	int z2 = z1 + 2;;
		
		boolean xMod = (x1 < x2) ? true : false;
		boolean zMod = (z1 < z2) ? true : false;

		if(y >= 54 && y <= 74)
			return "Land";
		else if (y > 100)
			return "Air";
		else if(y < 40)
			return "Cave";
		else if(y > 74)
		{
			for (int x = x1; xMod ? x <= x2 : x >= x2; x += (xMod ? 1 : -1))
				for (int z = z1; zMod ? z <= z2 : z >= z2; z += (zMod ? 1 : -1))
				{
					Location blockLoc = new Location(player.getWorld(), (double)x, (double)y, (double)z);
					if(!blockLoc.getBlock().getType().equals(Material.AIR))
						return "Land";
				}
			return "Air";
		}
		else if(y < 54)
		{
			for (int x = x1; xMod ? x <= x2 : x >= x2; x += (xMod ? 1 : -1))
				for (int z = z1; zMod ? z <= z2 : z >= z2; z += (zMod ? 1 : -1))
				{
					Location blockLoc = new Location(player.getWorld(), (double)x, (double)y, (double)z);
					if(!blockLoc.getBlock().getType().equals(Material.STONE) && (player.getLocation().getBlockX() != x && player.getLocation().getBlockZ() != z))
						return "Land";
				}
			return "Cave";
		}
		return null;
	}
	
	public double getDistance(Location loc1, Location loc2)
	{
		loc1.setY(0);
		loc2.setY(0);
		return loc1.distance(loc2);
	}
	
	public boolean getValidDistance(String type, Location playerLoc)
	{
		switch(type)
		return false;
	}
}

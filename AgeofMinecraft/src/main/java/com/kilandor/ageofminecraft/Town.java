/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kilandor.ageofminecraft;

import com.kilandor.ageofminecraft.config.ConfigHelper;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Kilandor
 */
public class Town
{
	private static AgeofMinecraft plugin = AgeofMinecraft.AoM;
	private static ConfigHelper townMap = plugin.getSettings().getTowns();
	
	public static void create(Player player, String size, String townName, String type)
	{
		if(checkExists(townName))
		{
			plugin.getChat().playerMsg(player, AgeofMinecraft.CHATTITLE, "{red}Error: {white}Town named {gold}"+townName+" {white}already exists", false);
			return;
		}
		else if(!plugin.getValidDistance("Town", player.getLocation()))
		{
			plugin.getChat().playerMsg(player, AgeofMinecraft.CHATTITLE, "{red}Error: {white}You are to close to an existing town", false);
			return;
		}
		
		LinkedList<Location> destinations = plugin.copyStructure("Town", type, size, player.getLocation(), player.getWorld());
		
		player.teleport(player.getLocation().add(0, 1, 0));
		
		Location dest1 = destinations.getFirst();
		Location dest2 = destinations.getLast();
		
		plugin.getSettings().handleTown(townName, dest1, dest2, player, "small", false);
		
		plugin.getChat().playerMsg(player, AgeofMinecraft.CHATTITLE, "Successfully {green}created {white}town named {gold}"+townName, false);
	}
	
	public static void delete(Player player, String townName)
	{
		if(!checkExists(townName))
		{
			plugin.getChat().playerMsg(player, AgeofMinecraft.CHATTITLE, "{red}Error: {white}Unknown Town named {gold}"+townName, false);
			return;
		}
		if(checkOwner(player, townName))
		{
			plugin.getSettings().delTown(townName);
			plugin.getChat().playerMsg(player, AgeofMinecraft.CHATTITLE, "Successfully {red}deleted {white}town named {gold}"+townName, false);
		}
		else
			plugin.getChat().playerMsg(player, AgeofMinecraft.CHATTITLE, "{red}Error: {white}You are not the owner of {gold}"+townName, false);
	}
	
	public static boolean checkOwner(Player player, String townName)
	{
		if(townMap.getString("Towns." + townName.toLowerCase() + ".Owner").equals(player.getName()))
			return true;
		return false;
	}
	
	public static boolean checkExists(String townName)
	{
		if(townMap.getNode("Towns." + townName.toLowerCase()) != null)
			return true;
		return false;
	}
}
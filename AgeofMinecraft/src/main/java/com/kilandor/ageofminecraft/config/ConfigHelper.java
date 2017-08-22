/*
 * Based on YMLSaveHelper by bekvon
 * https://github.com/bekvon/Residence/blob/master/src/com/bekvon/bukkit/residence/persistance/YMLSaveHelper.java
 */

package com.kilandor.ageofminecraft.config;

import java.io.File;
import java.util.Map;
import java.util.LinkedHashMap;

import org.bukkit.util.config.Configuration;

public class ConfigHelper extends Configuration
{
	public ConfigHelper(File infile)
	{
		super(infile);
		root = new LinkedHashMap<String,Object>();
	}

	public boolean existsMap(String name)
	{
		return root.containsKey(name);
	}
	
	public void addMap(String name, Map map)
	{
		root.put(name, map);
	}

	public Map getMap(String name)
	{
		return (Map) root.get(name);
	}

	public Map getRoot()
	{
		return root;
	}

	public void setRoot(Map<String,Object> newroot)
	{
		if(newroot != null);
			root = newroot;
	}
}
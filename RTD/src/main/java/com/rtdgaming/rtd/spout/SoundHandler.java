package com.rtdgaming.rtd.spout;

import org.bukkit.Server;

import com.rtdgaming.rtd.RTD;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SoundHandler
{
	private static SoundHandler soundHandler;
	private RTD plugin;
	private Server server;

	public static SoundHandler initialize(RTD instance)
	{
		if(soundHandler != null)
			throw new RuntimeException("You may not intialize the SoundHandler twice!");
		if(instance == null)
			throw new RuntimeException("Invalid RTD instance!");
		soundHandler = new SoundHandler(instance);
		return soundHandler;
	}

	public static SoundHandler getSoundHandler()
	{
		if(soundHandler == null)
			throw new RuntimeException("Please initialize the Sound Handler first.");
		return soundHandler;
	}
	
	private SoundHandler(RTD instance)
	{
		plugin = instance;
	}
	
	public void playURLSoundPlayer(SpoutPlayer player, String sound, boolean notify)
	{
		SpoutManager.getSoundManager().playCustomSoundEffect(plugin, player, "http://fastdl.rtdgaming.com/minecraft/sounds/rtd/"+sound, notify);
	}
}

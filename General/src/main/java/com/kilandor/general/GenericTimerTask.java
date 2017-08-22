package com.kilandor.general;

import java.util.TimerTask;

import org.bukkit.Server;



class GenericTimerTask extends TimerTask
{
	private final General plugin;
	private Server server;
	private long lastPlayerSave = 0;

	public GenericTimerTask(General instance)
	{
		plugin = instance;
		server = plugin.getServer();
	}

	public void run()
	{
		long timeStamp = plugin.getCurTimestamp();
		if(lastPlayerSave < timeStamp)
		{
			lastPlayerSave = timeStamp + 300; //TODO: use setting
			server.savePlayers();
			plugin.getChat().globalMsg(General.CHATTITLE, "{green}Auto{white} - All Players saved.", false);
		}
		
	}
}

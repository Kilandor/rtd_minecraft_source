package com.kilandor.daynight;

import java.util.TimerTask;
import org.bukkit.Server;

class TimeCycle extends TimerTask
{
	private final DayNight plugin;
	private Server server;
	private String timeCycle;

	public TimeCycle(DayNight instance)
	{
		plugin = instance;
		server = plugin.getServer();
		
	}

	public void run()
	{
		timeCycle = plugin.settings.timeCycle;
		long time = plugin.getRelativeTime();
		if(timeCycle.contentEquals("day"))
			if(time > 12000)
				plugin.setRelativeTime(0);
		else if(timeCycle.contentEquals("night"))
			if(time < 14000 || time > 22000)
				plugin.setRelativeTime(14000);
	}
}
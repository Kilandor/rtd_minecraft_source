package com.rtdgaming.rtd.spout;

import org.bukkit.Server;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.rtdgaming.rtd.RTD;
import org.getspout.spoutapi.gui.Color;

public class RollTimerHandler
{
	private final RTD plugin;
	private Server server;
	private SpoutPlayer sPlayer;
	
	private GenericLabel rtdTimer = new GenericLabel();

	public RollTimerHandler(RTD instance, SpoutPlayer player)
	{
		plugin = instance;
		server = plugin.getServer();
		sPlayer = player;
	}
	
	public void addTimer(Color color, String text, int x, int y)
	{
		x = 315;
		y = 230;
		rtdTimer.setText(text).setTextColor(color).setX(x).setY(y);
		sPlayer.getMainScreen().attachWidget(plugin, rtdTimer);
	}
	
	public boolean updateTimer(Color color, String text)
	{
		if(!rtdTimer.getText().equals(text))
		{
			rtdTimer.setText(text).setTextColor(color);
			rtdTimer.setDirty(true);
			return true;
		}
		return false;
	}
	
	public void removeTimer()
	{
		sPlayer.getMainScreen().removeWidget(rtdTimer);
	}
	
}

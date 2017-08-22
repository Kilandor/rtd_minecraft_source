package com.rtdgaming.rtd.spout;

import org.bukkit.Server;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.rtdgaming.rtd.RTD;
import org.getspout.spoutapi.gui.Color;

public class SpoutWatcher extends SpoutListener
{
	private final RTD plugin;
	private Server server;
	private Color color;
	
	public SpoutWatcher(RTD instance)
	{
		plugin = instance;
		server = plugin.getServer();
	}
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event)
	{
		if(!event.getPlayer().isSpoutCraftEnabled())
			return;
		SpoutPlayer sPlayer = event.getPlayer();
		
		RollTimerHandler rth = new RollTimerHandler(plugin, sPlayer);
		
		color = plugin.newColor(0, 227, 11);
		rth.addTimer(color, "", 1, 1);
		
		plugin.getWidgetMap().put(sPlayer.getName(), rth);
		
		if(event.getPlayer().hasPermission("general.op"))
			SpoutManager.getAppearanceManager().setGlobalCloak(event.getPlayer(), "http://fastdl.rtdgaming.com/minecraft/images/cloak/rtd.png");
	}
}
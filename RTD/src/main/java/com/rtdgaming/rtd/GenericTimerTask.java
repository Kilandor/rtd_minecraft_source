package com.rtdgaming.rtd;

import java.util.TimerTask;
import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;

import com.rtdgaming.rtd.rolls._RollsEnum;
import com.rtdgaming.rtd.spout.RollTimerHandler;
import com.rtdgaming.rtd.spout.SoundHandler;

class GenericTimerTask extends TimerTask
{
	private final RTD plugin;
	private Server server;

	public GenericTimerTask(RTD instance)
	{
		plugin = instance;
		server = plugin.getServer();
	}

	public void run()
	{
		// long timeStamp = plugin.getCurTimestamp();
		for(Player player : server.getOnlinePlayers())
		{
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			if(sPlayer.isSpoutCraftEnabled())
			{
				Color color = null;
				if(plugin.getWidgetMap().containsKey(player.getName()))
				{
					RollTimerHandler rth = (RollTimerHandler)plugin.getWidgetMap().get(player.getName());
					
					int nextRoll = Roller.getRoller().getLastRoll(player);
					if(nextRoll <= plugin.getCurTimestamp())
					{
						color = plugin.newColor(0, 227, 11);
						boolean first = rth.updateTimer(color, "Ready to RTD");
						if(first)
							SoundHandler.getSoundHandler().playURLSoundPlayer(sPlayer, "item_acquired.wav", false);
					}
					else
					{
						
						int timeLeft = (int) (nextRoll - plugin.getCurTimestamp());
						int minLeft = timeLeft / 60;
						int secLeft = timeLeft - (minLeft * 60);
						String sSecLeft = (Integer.toString(secLeft).length() == 1) ? "0" + Integer.toString(secLeft) : Integer.toString(secLeft);
						color = plugin.newColor(255, 255, 255);
						rth.updateTimer(color, "Timer: " + minLeft + ":" + sSecLeft);
					}
				}
			}
					
			LinkedList<RollInfo> rollsToRemove = new LinkedList<RollInfo>();
			ArrayList<RollInfo> rollInfoList = Roller.getRoller().getPlayerRolls(player.getName());
			if(rollInfoList == null)
				continue;

			//Do the necessary roll updates
			for(RollInfo rollInfo : rollInfoList)
			{
				_RollsEnum roll = rollInfo.getRoll();

				int duration = roll.getInstance().getDuration();
				if(duration < 0)
					continue;

				if(rollInfo.getTimestamp() + duration < plugin.getCurTimestamp())
				{
					_RollsEnum.Data d = rollInfo.getRoll().new Data(player);
					rollInfo.getRoll().getInstance().expire(d);
					rollsToRemove.add(rollInfo);
				}
				else
					roll.getInstance().update(roll.new Data(player));
			}

			// Remove expired rolls
			rollInfoList.removeAll(rollsToRemove);
		}
	}
}

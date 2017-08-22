package com.rtdgaming.economy.eventcatchers;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.rtdgaming.economy.Economy;
import com.rtdgaming.economy.TransactionHandler;

public class BlockPlace extends BlockListener
{
	@SuppressWarnings("unused")
	private final Economy plugin;

	public BlockPlace(Economy instance)
	{
		plugin = instance;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
                //If the player just right-clicked on a sign for conducting a transaction
		if(TransactionHandler.getInstance().isUsingSign(player.getName()))
			event.setCancelled(true);
	}
}

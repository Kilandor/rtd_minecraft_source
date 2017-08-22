package com.rtdgaming.economy.eventcatchers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.rtdgaming.economy.Economy;
import com.rtdgaming.economy.Transaction;
import com.rtdgaming.economy.TransactionHandler;

public class PlayerInteract extends PlayerListener
{
	private Economy plugin;
	private TransactionHandler transactionHandler;

	public PlayerInteract(Economy instance)
	{
		plugin = instance;
		transactionHandler = TransactionHandler.getInstance();
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		//We only process right-click events
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Material blockType = block.getType();
		if(blockType == Material.SIGN_POST || blockType == Material.WALL_SIGN)
		{
			if(!plugin.isInExchange(block.getLocation()))
				return;

			event.setCancelled(true);

			Transaction transaction = Transaction.generateTransaction(player, (Sign)block.getState(), transactionHandler);
			if (transaction != null)
				transactionHandler.process(transaction);
		}
	}
}

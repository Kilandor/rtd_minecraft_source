package com.rtdgaming.rtd.eventcatchers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.Roller;
import com.rtdgaming.rtd.rolls.InstantTree;
import com.rtdgaming.rtd.rolls._RollsEnum;

public class BlockPlace extends BlockListener
{
	@SuppressWarnings("unused")
	private final RTD plugin;

	public BlockPlace(RTD instance)
	{
		plugin = instance;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();
		if(Roller.getRoller().isActiveRoll(player, _RollsEnum.INSTANT_TREE) && block.getType() == Material.SAPLING)
			InstantTree.getInstance().activate(player, block);
	}
}

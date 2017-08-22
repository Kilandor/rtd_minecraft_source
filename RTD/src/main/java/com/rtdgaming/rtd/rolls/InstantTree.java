package com.rtdgaming.rtd.rolls;

import java.util.Random;

import org.bukkit.TreeType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.Roller;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class InstantTree extends _Roll
{
	private static InstantTree instance;

	public static InstantTree getInstance()
	{
		if(instance == null)
			instance = new InstantTree();
		return instance;
	}

	private InstantTree()
	{
		super(true, true);
	}

	public void setup(Data data)
	{
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {darkgreen}Instant Tree{white}!", false);
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "Plant a tree and watch it instantly grow!", false);
	}

	public void expire(Data data)
	{
		RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You no longer have {darkgreen}Instant Tree", false);
	}

	public void update(Data data) {}

	public void activate(Player p, Block b)
	{
		TreeType[] treeTypes = TreeType.values();
		b.setType(Material.AIR);
		Random rand = new Random();

		p.getWorld().generateTree(b.getLocation(), treeTypes[rand.nextInt(treeTypes.length)]);

		RTD.rtd.getChat().playerMsg(p, RTD.CHATTITLE, "Your sapling grew into a beautiful tree!", false);
		Roller.getRoller().remPlayerRoll(p.getName(), _RollsEnum.INSTANT_TREE);
	}
}

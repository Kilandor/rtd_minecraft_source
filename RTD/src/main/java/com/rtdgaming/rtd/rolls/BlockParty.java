package com.rtdgaming.rtd.rolls;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.rtdgaming.rtd.Block;
import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.Block.BlockProperty;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class BlockParty extends _Roll
{
	private static BlockParty instance;

	public static BlockParty getInstance()
	{
		if(instance == null)
			instance = new BlockParty();
		return instance;
	}

	private BlockParty()
	{
		super(true, false);
	}

	public void setup(Data data)
	{
		//Find a block to give the user (also prints the RTD to clients)
		awardRandom(data.player);
	}

	public void awardRandom(Player p)
	{
		//Get the block that the player will receive
		int award = Block.getRandomRollable();
		BlockProperty prop = Block.getBlockProp(award);

		//Calculate the amount they should get given the block's value
		int max;
		switch(prop.getValue())
		{
			case VERY_LOW:
				max = 64;
				break;
			case LOW:
				max = 32;
				break;
			case MEDIUM:
				max = 16;
				break;
			case HIGH:
				max = 8;
				break;
			case VERY_HIGH:
				max = 4;
				break;
			case SUPER_HIGH:
				max = 2;
				break;
			case SPECIAL:
			case PRICELESS:
				max = 1;
				break;
			case UNROLLABLE:
			default:
				throw new RuntimeException(prop.getName() + " CANNOT BE ROLLED AS A GIFT FROM THE BLOCK PARTY ROLL!");
		}
		Random rand = new Random();
		int amount = 1 + rand.nextInt(max);

		//Give the player the block(s)
		ItemStack stack = new ItemStack(award, amount);
		//Special case to give a type of colored wool or dye
		if(award == Material.WOOL.getId() || award == Material.INK_SACK.getId())
			stack.setDurability((short) rand.nextInt(16));
		HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(stack);

		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + p.getDisplayName() + "{white} has rolled {darkgreen}Block Party{white}!", false);
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, p.getDisplayName() + " was given {gold}" + prop.getName() + "{green} x" + amount, false);

		if(!leftover.isEmpty())
			for(ItemStack i : leftover.values())
			{
				p.getWorld().dropItem(p.getLocation(), new ItemStack(i.getTypeId(), i.getAmount()));
				RTD.rtd.getChat().playerMsg(p, RTD.CHATTITLE, "Dropped {green}" + i.getAmount() + "{white} of {gold}" + Block.getBlockProp(i.getTypeId()).getName() + "{white} because you need more space.", false);
			}
	}

	public void expire(Data data) {}
	public void update(Data data) {}
}

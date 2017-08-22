package com.rtdgaming.rtd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.rtdgaming.rtd.Block.BlockProperty.BlockValue;

public class Block
{
	//Block-specific data
	public static class BlockProperty
	{
		public static enum BlockValue
		{
			VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, SUPER_HIGH, SPECIAL, PRICELESS, UNROLLABLE
		}
		private BlockValue value;
		private String name;

		public BlockProperty(String n, BlockValue v)
		{
			name = n;
			value = v;
		}

		public BlockValue getValue()
		{
			return value;
		}

		public String getName()
		{
			return name;
		}
	}
	private static Integer[] blockIDs;
	private static HashMap<Integer, BlockProperty> blocks;

	public static void initialize()
	{
		blocks = new HashMap<Integer, BlockProperty>();

		//Begin super long list of every block :(
		blocks.put(0, new BlockProperty("Air", BlockValue.UNROLLABLE));
		blocks.put(1, new BlockProperty("Stone", BlockValue.LOW));
		blocks.put(2, new BlockProperty("Grass", BlockValue.MEDIUM));
		blocks.put(3, new BlockProperty("Dirt", BlockValue.VERY_LOW));
		blocks.put(4, new BlockProperty("Cobblestone", BlockValue.VERY_LOW));
		blocks.put(5, new BlockProperty("Wood", BlockValue.VERY_LOW));
		blocks.put(6, new BlockProperty("Sapling", BlockValue.VERY_LOW));
		blocks.put(7, new BlockProperty("Bedrock", BlockValue.UNROLLABLE));
		blocks.put(8, new BlockProperty("Water", BlockValue.UNROLLABLE));
		blocks.put(9, new BlockProperty("Water", BlockValue.UNROLLABLE));
		blocks.put(10, new BlockProperty("Lava", BlockValue.UNROLLABLE));
		blocks.put(11, new BlockProperty("Lava", BlockValue.UNROLLABLE));
		blocks.put(12, new BlockProperty("Sand", BlockValue.VERY_LOW));
		blocks.put(13, new BlockProperty("Gravel", BlockValue.VERY_LOW));
		blocks.put(14, new BlockProperty("Raw Gold Block", BlockValue.HIGH));
		blocks.put(15, new BlockProperty("Raw Iron Block", BlockValue.MEDIUM));
		blocks.put(16, new BlockProperty("Coal Block", BlockValue.LOW));
		blocks.put(17, new BlockProperty("Log", BlockValue.LOW));
		blocks.put(18, new BlockProperty("Leaves", BlockValue.UNROLLABLE));
		blocks.put(19, new BlockProperty("Sponge", BlockValue.UNROLLABLE));
		blocks.put(20, new BlockProperty("Glass", BlockValue.LOW));
		blocks.put(21, new BlockProperty("Raw Lapis Block", BlockValue.HIGH));
		blocks.put(22, new BlockProperty("Lapis Block", BlockValue.VERY_HIGH));
		blocks.put(23, new BlockProperty("Dispenser", BlockValue.MEDIUM));
		blocks.put(24, new BlockProperty("Sandstone", BlockValue.LOW));
		blocks.put(25, new BlockProperty("Note Block", BlockValue.MEDIUM));

		/* 26 - 24 not implemented yet */

		blocks.put(35, new BlockProperty("Wool", BlockValue.LOW));

		/* 36 not implemented yet */

		blocks.put(37, new BlockProperty("Flower", BlockValue.LOW));
		blocks.put(38, new BlockProperty("Rose", BlockValue.LOW));
		blocks.put(39, new BlockProperty("Mushroom", BlockValue.MEDIUM));
		blocks.put(40, new BlockProperty("Mushroom", BlockValue.MEDIUM));
		blocks.put(41, new BlockProperty("Gold Block", BlockValue.VERY_HIGH));
		blocks.put(42, new BlockProperty("Iron Block", BlockValue.VERY_HIGH));
		blocks.put(43, new BlockProperty("Unknown_Stone", BlockValue.UNROLLABLE));
		blocks.put(44, new BlockProperty("Half-step", BlockValue.LOW));
		blocks.put(45, new BlockProperty("Brick", BlockValue.MEDIUM));
		blocks.put(46, new BlockProperty("TNT", BlockValue.HIGH));
		blocks.put(47, new BlockProperty("Bookcase", BlockValue.SUPER_HIGH));
		blocks.put(48, new BlockProperty("Mossy Cobblestone", BlockValue.HIGH));
		blocks.put(49, new BlockProperty("Obsidian", BlockValue.VERY_HIGH));
		blocks.put(50, new BlockProperty("Torch", BlockValue.VERY_LOW));
		blocks.put(51, new BlockProperty("Fire", BlockValue.UNROLLABLE));
		blocks.put(52, new BlockProperty("Mob Spawner", BlockValue.UNROLLABLE));
		blocks.put(53, new BlockProperty("Wood Step", BlockValue.MEDIUM));
		blocks.put(54, new BlockProperty("Chest", BlockValue.MEDIUM));
		blocks.put(55, new BlockProperty("Redstone", BlockValue.UNROLLABLE));
		blocks.put(56, new BlockProperty("Raw Diamond Block", BlockValue.VERY_HIGH));
		blocks.put(57, new BlockProperty("Diamond Block", BlockValue.SUPER_HIGH));
		blocks.put(58, new BlockProperty("Crafting Table", BlockValue.LOW));
		blocks.put(59, new BlockProperty("Sprouts", BlockValue.UNROLLABLE));
		blocks.put(60, new BlockProperty("Tilled Dirt", BlockValue.UNROLLABLE));
		blocks.put(61, new BlockProperty("Furnace", BlockValue.MEDIUM));
		blocks.put(62, new BlockProperty("Furnace", BlockValue.UNROLLABLE));
		blocks.put(63, new BlockProperty("Raw Sign Part", BlockValue.UNROLLABLE));
		blocks.put(64, new BlockProperty("Wood Door Piece", BlockValue.UNROLLABLE));
		blocks.put(65, new BlockProperty("Ladder", BlockValue.LOW));
		blocks.put(66, new BlockProperty("Railroad Track", BlockValue.HIGH));
		blocks.put(67, new BlockProperty("StoneStep", BlockValue.MEDIUM));
		blocks.put(68, new BlockProperty("Raw Sign Part", BlockValue.UNROLLABLE));
		blocks.put(69, new BlockProperty("Switch", BlockValue.MEDIUM));
		blocks.put(70, new BlockProperty("Stone Pressure Plate", BlockValue.MEDIUM));
		blocks.put(71, new BlockProperty("Iron Door Piece", BlockValue.UNROLLABLE));
		blocks.put(72, new BlockProperty("Wood Pressure Plate", BlockValue.MEDIUM));
		blocks.put(73, new BlockProperty("Redstone", BlockValue.MEDIUM));
		blocks.put(74, new BlockProperty("Redstone", BlockValue.UNROLLABLE));
		blocks.put(75, new BlockProperty("Redstone Torch", BlockValue.UNROLLABLE));
		blocks.put(76, new BlockProperty("Redstone Torch", BlockValue.MEDIUM));
		blocks.put(77, new BlockProperty("Stone Button", BlockValue.MEDIUM));
		blocks.put(78, new BlockProperty("Snow", BlockValue.UNROLLABLE));
		blocks.put(79, new BlockProperty("Ice", BlockValue.HIGH));
		blocks.put(80, new BlockProperty("Snow Block", BlockValue.MEDIUM));
		blocks.put(81, new BlockProperty("Cactus", BlockValue.LOW));
		blocks.put(82, new BlockProperty("Clay Block", BlockValue.MEDIUM));
		blocks.put(83, new BlockProperty("Reed Block", BlockValue.UNROLLABLE));
		blocks.put(84, new BlockProperty("Record Player", BlockValue.VERY_HIGH));
		blocks.put(85, new BlockProperty("Fence", BlockValue.MEDIUM));
		blocks.put(86, new BlockProperty("Pumpkin", BlockValue.MEDIUM));
		blocks.put(87, new BlockProperty("Netherstone", BlockValue.HIGH));
		blocks.put(88, new BlockProperty("Slowsand", BlockValue.HIGH));
		blocks.put(89, new BlockProperty("Glowstone", BlockValue.HIGH));
		blocks.put(90, new BlockProperty("Portal", BlockValue.UNROLLABLE));
		blocks.put(91, new BlockProperty("Jack O' Lantern", BlockValue.MEDIUM));
		blocks.put(92, new BlockProperty("Cake Block", BlockValue.UNROLLABLE));

		/* 93 - 255 not implemented yet */

		blocks.put(256, new BlockProperty("Iron Shovel", BlockValue.HIGH));
		blocks.put(257, new BlockProperty("Iron Pick", BlockValue.HIGH));
		blocks.put(258, new BlockProperty("Iron Axe", BlockValue.HIGH));
		blocks.put(259, new BlockProperty("Flint and Steel", BlockValue.HIGH));
		blocks.put(260, new BlockProperty("Apple", BlockValue.VERY_HIGH));
		blocks.put(261, new BlockProperty("Bow", BlockValue.HIGH));
		blocks.put(262, new BlockProperty("Arrow", BlockValue.MEDIUM));
		blocks.put(263, new BlockProperty("Coal", BlockValue.LOW));
		blocks.put(264, new BlockProperty("Diamond", BlockValue.PRICELESS));
		blocks.put(265, new BlockProperty("Iron", BlockValue.VERY_HIGH));
		blocks.put(266, new BlockProperty("Gold", BlockValue.SUPER_HIGH));
		blocks.put(267, new BlockProperty("Iron Sword", BlockValue.HIGH));
		blocks.put(268, new BlockProperty("Wood Sword", BlockValue.MEDIUM));
		blocks.put(269, new BlockProperty("Wood Shovel", BlockValue.MEDIUM));
		blocks.put(270, new BlockProperty("Wood Pick", BlockValue.MEDIUM));
		blocks.put(271, new BlockProperty("Wood Axe", BlockValue.MEDIUM));
		blocks.put(272, new BlockProperty("Stone Sword", BlockValue.MEDIUM));
		blocks.put(273, new BlockProperty("Stone Shovel", BlockValue.MEDIUM));
		blocks.put(274, new BlockProperty("Stone Pick", BlockValue.MEDIUM));
		blocks.put(275, new BlockProperty("Stone Axe", BlockValue.MEDIUM));
		blocks.put(276, new BlockProperty("Diamond Sword", BlockValue.SUPER_HIGH));
		blocks.put(277, new BlockProperty("Diamond Shovel", BlockValue.SUPER_HIGH));
		blocks.put(278, new BlockProperty("Diamond Pick", BlockValue.SUPER_HIGH));
		blocks.put(279, new BlockProperty("Diamond Axe", BlockValue.SUPER_HIGH));
		blocks.put(280, new BlockProperty("Stick", BlockValue.VERY_LOW));
		blocks.put(281, new BlockProperty("Bowl", BlockValue.LOW));
		blocks.put(282, new BlockProperty("Bowl of Soup", BlockValue.HIGH));
		blocks.put(283, new BlockProperty("Gold Sword", BlockValue.MEDIUM));
		blocks.put(284, new BlockProperty("Gold Shovel", BlockValue.MEDIUM));
		blocks.put(285, new BlockProperty("Gold Pick", BlockValue.MEDIUM));
		blocks.put(286, new BlockProperty("Gold Axe", BlockValue.MEDIUM));
		blocks.put(287, new BlockProperty("String", BlockValue.HIGH));
		blocks.put(288, new BlockProperty("Feather", BlockValue.HIGH));
		blocks.put(289, new BlockProperty("Gunpowder", BlockValue.HIGH));
		blocks.put(290, new BlockProperty("Wood Hoe", BlockValue.MEDIUM));
		blocks.put(291, new BlockProperty("Stone Hoe", BlockValue.MEDIUM));
		blocks.put(292, new BlockProperty("Iron Hoe", BlockValue.HIGH));
		blocks.put(293, new BlockProperty("Diamond Hoe", BlockValue.SUPER_HIGH));
		blocks.put(294, new BlockProperty("Gold Hoe", BlockValue.MEDIUM));
		blocks.put(295, new BlockProperty("Seed", BlockValue.LOW));
		blocks.put(296, new BlockProperty("Wheat", BlockValue.LOW));
		blocks.put(297, new BlockProperty("Bread", BlockValue.MEDIUM));
		blocks.put(298, new BlockProperty("Leather Helmet", BlockValue.MEDIUM));
		blocks.put(299, new BlockProperty("Leather Chest", BlockValue.MEDIUM));
		blocks.put(300, new BlockProperty("Leather Pants", BlockValue.MEDIUM));
		blocks.put(301, new BlockProperty("Leather Shoes", BlockValue.MEDIUM));
		blocks.put(302, new BlockProperty("Steel Helmet", BlockValue.UNROLLABLE));
		blocks.put(303, new BlockProperty("Steel Chest", BlockValue.UNROLLABLE));
		blocks.put(304, new BlockProperty("Steel Pants", BlockValue.UNROLLABLE));
		blocks.put(305, new BlockProperty("Steel Shoes", BlockValue.UNROLLABLE));
		blocks.put(306, new BlockProperty("Iron Helmet", BlockValue.VERY_HIGH));
		blocks.put(307, new BlockProperty("Iron Chest", BlockValue.VERY_HIGH));
		blocks.put(308, new BlockProperty("Iron Pants", BlockValue.VERY_HIGH));
		blocks.put(309, new BlockProperty("Iron Shoes", BlockValue.VERY_HIGH));
		blocks.put(310, new BlockProperty("Diamond Helmet", BlockValue.SUPER_HIGH));
		blocks.put(311, new BlockProperty("Diamond Chest", BlockValue.SUPER_HIGH));
		blocks.put(312, new BlockProperty("Diamond Pants", BlockValue.SUPER_HIGH));
		blocks.put(313, new BlockProperty("Diamond Shoes", BlockValue.SUPER_HIGH));
		blocks.put(314, new BlockProperty("Gold Helmet", BlockValue.MEDIUM));
		blocks.put(315, new BlockProperty("Gold Chest", BlockValue.MEDIUM));
		blocks.put(316, new BlockProperty("Gold Pants", BlockValue.MEDIUM));
		blocks.put(317, new BlockProperty("Gold Shoes", BlockValue.MEDIUM));
		blocks.put(318, new BlockProperty("Flint", BlockValue.MEDIUM));
		blocks.put(319, new BlockProperty("Porkchop", BlockValue.MEDIUM));
		blocks.put(320, new BlockProperty("Cooked Porkchop", BlockValue.HIGH));
		blocks.put(321, new BlockProperty("Painting", BlockValue.HIGH));
		blocks.put(322, new BlockProperty("Golden Apple", BlockValue.VERY_HIGH));
		blocks.put(323, new BlockProperty("Sign", BlockValue.MEDIUM));
		blocks.put(324, new BlockProperty("Wood Door", BlockValue.MEDIUM));
		blocks.put(325, new BlockProperty("Bucket", BlockValue.HIGH));
		blocks.put(326, new BlockProperty("Bucket of Water", BlockValue.VERY_HIGH));
		blocks.put(327, new BlockProperty("Bucket of Lava", BlockValue.VERY_HIGH));
		blocks.put(328, new BlockProperty("Minecart", BlockValue.VERY_HIGH));
		blocks.put(329, new BlockProperty("Saddle", BlockValue.VERY_HIGH));
		blocks.put(330, new BlockProperty("Iron Door", BlockValue.HIGH));
		blocks.put(331, new BlockProperty("Redstone Ore", BlockValue.MEDIUM));
		blocks.put(332, new BlockProperty("Snowball", BlockValue.MEDIUM));
		blocks.put(333, new BlockProperty("Boat", BlockValue.HIGH));
		blocks.put(334, new BlockProperty("Leather", BlockValue.MEDIUM));
		blocks.put(335, new BlockProperty("Bucket of Milk", BlockValue.VERY_HIGH));
		blocks.put(336, new BlockProperty("Clay Brick", BlockValue.HIGH));
		blocks.put(337, new BlockProperty("Clay", BlockValue.MEDIUM));
		blocks.put(338, new BlockProperty("Reed", BlockValue.MEDIUM));
		blocks.put(339, new BlockProperty("Paper", BlockValue.HIGH));
		blocks.put(340, new BlockProperty("Book", BlockValue.VERY_HIGH));
		blocks.put(341, new BlockProperty("Slime", BlockValue.HIGH));
		blocks.put(342, new BlockProperty("Chest Minecart", BlockValue.VERY_HIGH));
		blocks.put(343, new BlockProperty("Powered Minecart", BlockValue.VERY_HIGH));
		blocks.put(344, new BlockProperty("Egg", BlockValue.HIGH));
		blocks.put(345, new BlockProperty("Compass", BlockValue.SUPER_HIGH));
		blocks.put(346, new BlockProperty("Fishing Rod", BlockValue.VERY_HIGH));
		blocks.put(347, new BlockProperty("Watch", BlockValue.SUPER_HIGH));
		blocks.put(348, new BlockProperty("Glowstone Dust", BlockValue.MEDIUM));
		blocks.put(349, new BlockProperty("Fish", BlockValue.HIGH));
		blocks.put(350, new BlockProperty("Fish", BlockValue.HIGH));
		blocks.put(351, new BlockProperty("Dye", BlockValue.MEDIUM));
		blocks.put(352, new BlockProperty("Bone", BlockValue.VERY_HIGH));
		blocks.put(353, new BlockProperty("Sugar", BlockValue.HIGH));
		blocks.put(354, new BlockProperty("Cake", BlockValue.SUPER_HIGH));

		/* 355 - 2255 not implemented yet */

		blocks.put(2256, new BlockProperty("Record 1", BlockValue.SUPER_HIGH));
		blocks.put(2257, new BlockProperty("Record 2", BlockValue.SUPER_HIGH));

		//Now we will generate the blockIDs array
		//'blocks' IS NOT ALLOWED TO CHANGE IN SIZE ANYMORE!
		int maxSize = blocks.keySet().size();
		blockIDs = new Integer[maxSize];
		Iterator<Integer> keyIter = blocks.keySet().iterator();
		for(int i = 0; i < maxSize; i++)
			blockIDs[i] = keyIter.next();
		//Do a slow quadratic time sort on the blockIDs from least->greatest
		for(int i = 0; i < blockIDs.length; i++)
			for(int j = i + 1; j < blockIDs.length; j++)
				if(blockIDs[j] < blockIDs[i])
				{
					//Swap
					Integer temp = blockIDs[j];
					blockIDs[j] = blockIDs[i];
					blockIDs[i] = temp;
				}
	}

	public static boolean isRollable(int block)
	{
		return getBlockProp(block).value != BlockValue.UNROLLABLE;
	}

	public static BlockProperty getBlockProp(int block)
	{
		return getBlockProp(new Integer(block));
	}

	public static BlockProperty getBlockProp(Integer block)
	{
		if(!blocks.containsKey(block))
			throw new RuntimeException("Block " + block.intValue() + " does not exist!");
		return blocks.get(block);
	}

	public static int getRandomRollable()
	{
		Random rand = new Random();
		int item;
		//I hope to God that this never infinite loops...
		do
		{
			item = blockIDs[rand.nextInt(blockIDs.length)].intValue();
		}
		while(!isRollable(item));
		return item;
	}
}

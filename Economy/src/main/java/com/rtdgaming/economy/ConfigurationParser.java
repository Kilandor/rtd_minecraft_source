package com.rtdgaming.economy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class ConfigurationParser {
	/* BEGIN SINGLETON PATTERN */
	private static ConfigurationParser instance;
	public static ConfigurationParser getInstance()
	{
		if(instance == null)
			instance = new ConfigurationParser();
		return instance;
	}
	private ConfigurationParser() {}
	/* END SINGLETON PATTERN */
	
	private enum BlockProperty {
		//The ID of the block in the form "ID" or "ID-Durability"
		BlockType("blocktype"),
		
		//ONLY DEFINED FOR RAW BLOCKS
		MinimumQuantity("minquantity"),
		MaximumQuantity("maxquantity"),
		PriceMultiplier("pricemultiplier"),
		
		//ONLY DEFINED FOR NON-RAW BLOCKS
		Formula("formula"),
		Production("produces"),
		
		//0 - disabled, 1 - buy only, 2 - sell only, 3 - buy and sell
		//Default is 3
		SaleType("saletype");
		
		private String string;
		BlockProperty(String s) {string = s;}
		public String s() {return string;}
	}
	
	private Configuration config;
	public void setConfiguration(Configuration config)
	{
		this.config = config;
	}

	public TreeMap<String, BlockData> getBlockData()
	{
		if (config == null)
			throw new NullPointerException("You forgot to set the ConfigurationParser's configuration first!");
		
		TreeMap<String, BlockData> toReturn = new TreeMap<String, BlockData>();
		parseRawBlocks(toReturn);
		parseNonRawBlocks(toReturn);
		return toReturn;
	}
	
	/**
	 * Parses a "blocktype" string from the configuration file
	 * and returns an object that gives us the name of that block
	 * as a string in the form of "id-durability".
	 * @param s The string from the configuration file
	 * @return The fully qualified BlockName; null if a null string is passed.
	 */
	private BlockName getBlockNameObject(String s)
	{
		if (s == null)
			return null;
		String[] vals = s.split("-");
		if (vals.length > 2 || vals.length < 1)
			throw new RuntimeException("Bad blocktype data: " + Arrays.toString(vals) + "\n  It must be \"ID\" or \"ID-DURABILITY\"");
		int id = Integer.parseInt(vals[0]);
		int durability = vals.length == 2 ? Integer.parseInt(vals[1]) : 0;
		return new BlockName(id, durability);
	}
	private String getBlockNameString(String s)
	{
		if (s == null)
			return null;
		String[] vals = s.split("-");
		if (vals.length > 2 || vals.length < 1)
			throw new RuntimeException("Bad blocktype data: " + Arrays.toString(vals) + "\n  It must be \"ID\" or \"ID-DURABILITY\"");
		int id = Integer.parseInt(vals[0]);
		int durability = vals.length == 2 ? Integer.parseInt(vals[1]) : 0;
		return id + "-" + durability;
	}
	
	private void parseRawBlocks(TreeMap<String, BlockData> b)
	{
		List<ConfigurationNode> blockList = config.getNodeList("rawblocks", null);
		for(ConfigurationNode blocks : blockList)
		{
			ConfigurationNode block = blocks.getNode("block");
			BlockName blockName = getBlockNameObject(block.getString(BlockProperty.BlockType.s()));
			if(blockName == null)
				continue;
			
			int minQty = block.getInt(BlockProperty.MinimumQuantity.s(), -1);
			int maxQty = block.getInt(BlockProperty.MaximumQuantity.s(), -1);
			int priceMultiplier = block.getInt(BlockProperty.PriceMultiplier.s(), -1);
			int saleType = block.getInt(BlockProperty.SaleType.s(), 3);
                        if (minQty < 0 || maxQty < 0 || priceMultiplier <= 0)
				throw new RuntimeException("The price multiplier or quanties are broken for " + blockName);
			if (saleType < 0 || saleType > 3)
				throw new RuntimeException("The saletype for " + blockName + " is invalid.");
				
			b.put(blockName.toString(), new BlockData(blockName, minQty, maxQty, priceMultiplier, saleType));
		}
	}
	
	/**
	 * Parses the blocks that have recipes.  CALL AFTER parseRawBlocks(...)!
	 * @param b
	 */
	private void parseNonRawBlocks(TreeMap<String, BlockData> b)
	{
		HashMap<String, String[]> formulas = new HashMap<String, String[]>();
		List<ConfigurationNode> blockList = config.getNodeList("nonrawblocks", null);
		for(ConfigurationNode blocks : blockList)
		{
			ConfigurationNode block = blocks.getNode("block");
			BlockName blockName = getBlockNameObject(block.getString(BlockProperty.BlockType.s()));
			if(blockName == null)
				continue;
			
			//Store the formulas for later
			formulas.put(blockName.toString(), block.getString(BlockProperty.Formula.s()).split(" "));
			
			int produces = block.getInt(BlockProperty.Production.s(), 1);
			if (produces <= 0)
				throw new RuntimeException("\"produces\" must be positive for " + blockName);
			int saleType = block.getInt(BlockProperty.SaleType.s(), 3);
			if (saleType < 0 || saleType > 3)
				throw new RuntimeException("The saletype for " + blockName + " is invalid.");
			
			b.put(blockName.toString(), new BlockData(blockName, produces, saleType));
		}
		
		//Okay, now we have to go over all of the blocks and generate the formula objects
		for (String block : formulas.keySet())
		{
			String[] equation = formulas.get(block);
			if (equation.length == 0 || equation.length % 2 != 0)
				throw new RuntimeException("Bad formula length for " + block);
			if (!b.containsKey(block))
				throw new RuntimeException("Somehow block " + block + " was never added to the hashmap.");
			
			BlockData[] ingredients = new BlockData[equation.length / 2];
			double[] quantities = new double[equation.length / 2];
			for (int i = 0; i < equation.length; i += 2) {
				String ingredientName = getBlockNameString(equation[i + 1]);
				if (!b.containsKey(ingredientName))
					throw new RuntimeException(block + " requests an ingredient (" + ingredientName + ") that is not in the map.");
				ingredients[i / 2] = b.get(ingredientName);
				quantities[i / 2] = Double.parseDouble(equation[i]);
			}
			
			b.get(block).setFormula(new Formula(ingredients, quantities));
		}		
	}
	
	public int[] getDuraBlocks() {
		List<Integer> blockList = config.getIntList("durablocks", new ArrayList<Integer>());
		int[] toReturn = new int[blockList.size()];
		int i = 0;
		for (Iterator<Integer> iter = blockList.iterator(); iter.hasNext(); i++)
			toReturn[i] = iter.next().intValue();
		return toReturn;
	}
        
        public float getPerLevelModifier() {
            float value = config.getInt("per_level_modifier", 5) / 100.0f;
            if (value < 0)
                throw new RuntimeException("Bad per_level_modifier property in economy configuration! [0, infinity)");
            return value;
        }

        public float getSellPriceModifier() {
            float value = config.getInt("sell_price_modifier", 60) / 100.0f;
            if (value < 0 || value > 100)
                throw new RuntimeException("Bad sell_price_modifier property in economy configuration! [0, 100]");
            return value;
        }
}

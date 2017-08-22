package com.rtdgaming.economy;

import org.bukkit.Material;

public class BlockData
{
        private static float perLevelModifier, sellPriceModifier;
    
	//Raw Only
	private int minQty, maxQty, minPrice, maxPrice;
	
	//NonRaw Only
	private int produces;
	private Formula formula;
	
	//Both
	private BlockName blockName;
	private Material blockType;
	private int level;
	private int saleType;
	
	private boolean finalized;

	public BlockData(BlockName n, int mnQ, int mxQ, int pM, int st)
	{
		blockName = n;
		blockType = Material.getMaterial(blockName.getId());
		minQty = mnQ;
		maxQty = mxQ;
		minPrice = pM - pM / 10;
		maxPrice = pM + pM / 10;
		saleType = st;
		level = 0;
	}
	
	public BlockData(BlockName n, int p, int st)
	{
		blockName = n;
		blockType = Material.getMaterial(blockName.getId());
		produces = p;
		saleType = st;
		level = -1; //When finalize() is called this will be updated properly
	}
	
        /**
	 * Leads to a recursive function.  Any loops you have in the configuration graph will shine on this call :P
	 * @param amount Amount of this block that you want
	 * @param isBuyOrder True if this is calculating for a buy order, false otherwise.
	 * @return The value considering the input.
	 */
        public long getValue(double amount, boolean isBuyOrder)
        {
            if (!finalized)
                throw new RuntimeException("BlockData instance was not finalized for block " + blockName.toString());
            
            double levelModifier = 1.0 - level * getPerLevelModifier();
            if (levelModifier <= 0 || levelModifier > 1)
                throw new RuntimeException("The level modifier is too low/high: " + levelModifier);
            long value = getValueRecursive(amount, isBuyOrder);
            
            if (isBuyOrder)
               return (long)(value / levelModifier);
            else
                return (long)(value * levelModifier * getSellPriceModifier());
        }
        
        /**
	 * Recursive function.  Only call from getValue(double, boolean).
	 * @param amount Amount of this block that you want
	 * @param isBuyOrder True if this is calculating for a buy order, false otherwise.
	 * @return The value...
	 */
	private long getValueRecursive(double amount, boolean isBuyOrder)
	{
            if (amount < 0)
                throw new RuntimeException("Cannot compute for amount <= 0 on material " + getAbsoluteId());
            int calcAmount = (int)(amount+0.5);
            if (calcAmount == 0)
                return 0;
            
            /* Ending recursive case.  I've heard that the math here
             * can look scary at first. */
            if (isRaw())
            {
                int blockQty = SQL.getInstance().sQuantity(blockName.toString());

                int leftover, end, start = blockQty;
                if (isBuyOrder) {
                    leftover = calcAmount - (blockQty - minQty);
                    end = leftover <= 0 ? blockQty - calcAmount + 1 : minQty + 1;
                } else {
                    leftover = calcAmount - (maxQty - blockQty);
                    end = leftover <= 0 ? blockQty + calcAmount - 1 : maxQty - 1;
                }

                double total;
                if (start == end)
                    total = getPrice(start);
                else {
                    total = getPrice(start) + getPrice(end);
                    int entries;
                    if (start > end)
                        entries = start - end + 1;
                    else
                        entries = end - start + 1;

                    if (entries % 2 == 0)
                        total *= entries / 2;
                    else
                        total = total * ((entries - 1) / 2) + (total / 2);
                }
                if (leftover > 0)
                    if (isBuyOrder)
                        total += maxPrice * leftover;
                    else
                        total += minPrice * leftover;

                return (long)total;
            }

            //Recursive Step
            long costPerOne = 0;
            int ingredients = formula.getIngredientsCount();
            for (int i = 0; i < ingredients; i++)
                    costPerOne += formula.getIngredient(i).getValueRecursive(formula.getIngredientCount(i), isBuyOrder);
            costPerOne /= produces;
            return calcAmount * costPerOne;
	}

	/**
	 * Gets the price for 1 of this block as if it were at the specified quantity
	 */
	public double getPrice(int quantity) {
		if (minPrice == maxPrice)
			return minPrice;
		return maxPrice - (double)(quantity - minQty) / (maxQty - minQty) * (maxPrice - minPrice);
	}
	
	public int getMinQuantity()
	{
		return minQty;
	}
	
	public int getMedianQuantity() {
		return maxQty - (maxQty - minQty) / 2;
	}

	public int getMaxQuantity()
	{
		return maxQty;
	}
	
	public int getMinPrice()
	{
		return minPrice;
	}
	
	public int getMaxPrice()
	{
		return maxPrice;
	}

	public String getName()
	{
		return blockType.name();
	}
	
	public boolean canBeBought()
	{
		return saleType == 1 || saleType == 3;
	}
	
	public boolean canBeSold()
	{
		return saleType == 2 || saleType == 3;
	}
	
	public String getAbsoluteId() {
		return blockName.toString();
	}

	public int getId()
	{
		return blockType.getId();
	}
	
	public Formula getFormula()
	{
		return isRaw() ? null : formula;
	}
	
	public boolean isRaw()
	{
		return level == 0;
	}
	
	public int getLevel() {
		return level;
	}

	public void setFormula(Formula f) {
		if (isRaw())
			throw new RuntimeException("Somehow formula " + f.toString() + " is being applied to raw block " + blockName.toString());
		formula = f;
	}
	
	public void finalize(TransactionHandler handler) {
		if (isRaw())
			finalized = true;
		if (finalized)
			return;
		
		//Now is the perfect time determine the item levels of non-raw items
		level = recursive(formula, 0);
		
		finalized = true;
	}
	
	private int recursive(Formula f, int depth)
	{
		if (f == null)
			return depth;
		
		int count = f.getIngredientsCount();
		int max = 0;
		for (int i = 0; i < count; i++) {
			int result = recursive(f.getIngredient(i).getFormula(), depth + 1);
			if (result > max)
				max = result;
		}
		return max;
	}

	public String getFormulaString() {
		if (formula == null)
			return "N/A";
			
		String toReturn = "";
		int count = formula.getIngredientsCount();
		for (int i = 0; i < count; i++)
			toReturn += (i == 0 ? "" : " ") + formula.getIngredientCount(i) + " " + formula.getIngredient(i).getAbsoluteId();
		return toReturn;
	}

	public int getProduction() {
		return produces;
	}

    private float getPerLevelModifier() {
        if (perLevelModifier == 0)
            perLevelModifier = ConfigurationParser.getInstance().getPerLevelModifier();
        return perLevelModifier;
    }

    private float getSellPriceModifier() {
        if (sellPriceModifier == 0)
            sellPriceModifier = ConfigurationParser.getInstance().getSellPriceModifier();
        return sellPriceModifier;
    }
}

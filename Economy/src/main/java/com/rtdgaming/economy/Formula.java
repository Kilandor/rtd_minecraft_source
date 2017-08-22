package com.rtdgaming.economy;

import java.text.DecimalFormat;

public class Formula {
	private BlockData[] ingredients;
	private double[] quantities;
	
	public Formula(BlockData[] i, double[] q)
	{
		ingredients = i;
		quantities = q;
	}
	
	public int getIngredientsCount() {
		return ingredients.length;
	}
	
	public BlockData getIngredient(int index) {
		if (index < 0 || index > ingredients.length)
			return null;
		return ingredients[index];
	}
	
	public double getIngredientCount(int index) {
		if (index < 0 || index > quantities.length)
			return 0.0;
		return quantities[index];
	}
	
	public String toString() {
		String s = "";
		DecimalFormat myFormatter = new DecimalFormat("###.###");
		for (int i = 0; i < ingredients.length; i++)
			s += " ("+ingredients[i].getAbsoluteId()+", "+myFormatter.format(quantities[i])+")";
		return s;
	}
}

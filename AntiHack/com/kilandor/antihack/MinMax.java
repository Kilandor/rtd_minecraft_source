package com.kilandor.antihack;

public class MinMax
{
	private long min;
	private long max;

	public MinMax()
	{

	}

	public void check(long value)
	{
		if(value < min || min == 0)
			min = value;
		if(value > max)
			max = value;
	}
	public long getMin()
	{
		return min;
	}

	public long getMax()
	{
		return max;
	}

}

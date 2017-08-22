package com.rtdgaming.economy;

public class SignInfo
{
	public String[] lines;
	public int itemId, qty, value;
	short durability;
	public long timestamp;
	boolean waitingOnInput;
	public SignInfo(String[] lines, int itemId, int qty, int value)
	{
		this.lines = lines;
		this.itemId = itemId;
		this.qty = qty;
		this.value = value;
		timestamp = System.currentTimeMillis()+30000;
	}
	public SignInfo(String[] lines, int itemId, int qty, short durability, boolean waitingOnInput, int value)
	{
		this(lines, itemId, qty, value);
		this.durability = durability;
		this.waitingOnInput = waitingOnInput;
	}
}

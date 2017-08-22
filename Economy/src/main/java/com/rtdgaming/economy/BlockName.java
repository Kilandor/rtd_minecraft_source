package com.rtdgaming.economy;

public class BlockName {
	private int id, durability;
	public BlockName(int id, int durability) {
		this.id = id;
		this.durability = durability;
	}
	public String toString() {
		return id + "-" + durability;
	}
	public int getId() {
		return id;
	}
	public short getDurability() {
		return (short)durability;
	}
}

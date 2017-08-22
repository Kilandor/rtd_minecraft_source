package com.rtdgaming.rtd.rolls;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.rolls._RollsEnum.Data;

public class InventoryExplosion extends _Roll
{
	private static InventoryExplosion instance;

	public static InventoryExplosion getInstance()
	{
		if(instance == null)
			instance = new InventoryExplosion();
		return instance;
	}

	private InventoryExplosion()
	{
		super(true, false);
	}

	public void setup(Data data)
	{
		Location l = data.player.getLocation();
		double orig_x = l.getX();
		double orig_y = l.getY();
		double orig_z = l.getZ();
		World w = data.player.getWorld();

		//Get the possible locations to throw blocks (can potentially go through walls)
		Location locs[] = new Location[64];
		int count = 0;
		for(int x = -4; x <= 4; x++)
		{
			if(x == 0)
				continue;
			for(int z = -4; z <= 4; z++)
			{
				Material blocktype = w.getBlockAt((int) orig_x + x, (int) orig_y, (int) orig_z + z).getType();
				if(z == 0 || (blocktype != Material.AIR && blocktype != Material.WATER && blocktype != Material.STATIONARY_WATER))
					continue;
				locs[count++] = new Location(w, orig_x + x, orig_y, orig_z + z);
			}
		}

		//Boo players that avoid this roll
		if(count == 0)
		{
			RTD.rtd.getChat().playerMsg(data.player, RTD.CHATTITLE, "You managed to dodge the Inventory Explosion roll, keep hiding in your stupid hole :(", false);
			return;
		}

		//Explode their inventory all over the place
		Random rand = new Random();
		ItemStack[] items = data.player.getInventory().getContents();
		for(ItemStack i : items)
			if(i != null)
				data.player.getWorld().dropItemNaturally(locs[rand.nextInt(count)], i);
		data.player.getInventory().clear();
		RTD.rtd.getChat().globalMsg(RTD.CHATTITLE, "{green}" + data.player.getName() + "{white} has rolled {red}Inventory Explosion{white}!", false);
	}

	public void expire(Data data) {}
	public void update(Data data) {}
}

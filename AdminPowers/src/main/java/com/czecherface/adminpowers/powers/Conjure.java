package com.czecherface.adminpowers.powers;

import com.czecherface.adminpowers.TrueMaterial;
import java.util.Iterator;
import java.util.LinkedList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Conjure extends _Power {
    private static final int INVENTORY_SLOTS = 36, BEGINNING_SLOT = 9;
    
    private ItemStack[] items = new ItemStack[INVENTORY_SLOTS];
    private Player invoker;
    
    public Conjure() {
        super(_PowerEnum.CONJURE, _Interaction.ITEM_DROP, 1, 1);
    }
    
    @Override
    public String getStatus() {
        return "{blue}Conjure{white} active, hit {red}E{white}. Drop an item or {green}/ap{white} to restore.";
    }
    
    @Override
    public void activate(_Interaction action)
    {
        cleanUp();
    }
    
    @Override
    public String setData(Player invoker, String[] params) {
        if (!validArgumentCount(params.length))
            return getArgumentRequirementString();
        this.invoker = invoker;
        
        PlayerInventory inventory = invoker.getInventory();
        System.out.println(inventory.firstEmpty());
        if (inventory.firstEmpty() < 0)
            return "Please free at least one inventory slot.";
        
        if (params[0].length() < 3)
            return "A minimum of 3 letters is required.";
        
        //First let's see if this is an ID
        TrueMaterial tm = TrueMaterial.get(params[0]);
        if (tm != null)
        {
            //Easy, we know what they want
            Material m = tm.getMaterial();
            byte data = tm.getData();
            inventory.addItem(new ItemStack(m, 64, data));
            return "Added a stack of {green}" + m.toString() + "(" + data + "){white} to your inventory.";
        }
        
        //Since no dice on the ID let's look for a/some name/s
        LinkedList<Material> matches = new LinkedList<Material>();
        String search = params[0].toUpperCase();
        for (Material m : Material.values())
            if (m != Material.AIR && m.toString().indexOf(search) >= 0)
                matches.add(m);
        
        if (matches.size() == 0)
            return "No such material found.";
        if (matches.size() == 1)
        {
            Material m = matches.getFirst();
            inventory.addItem(new ItemStack(m, 64));
            return "Added a stack of {green}" + m.toString() + "{white} to your inventory.";
        }
        
        /** Populate the player's inventory **/
        Iterator<Material> mats = matches.iterator();
        for (int i = 0; i < INVENTORY_SLOTS; i++)
        {
            //Back it up first
            items[i] = inventory.getItem(i);
            inventory.setItem(i, null);
            if (i < BEGINNING_SLOT) continue;
            //Start putting matches in the player's inventory
            if (mats.hasNext())
                inventory.setItem(i, new ItemStack(mats.next(), 64));
        }
        
        //Now we just wait for the player to pick an item or cancel
        setMustCleanUp(true);
        return null;
    }
    
    @Override
    public void cleanUp()
    {
        if (!mustCleanUp())
            return;
        PlayerInventory inventory = invoker.getInventory();
        for (int i = 0; i < INVENTORY_SLOTS; i++)
            inventory.setItem(i, items[i]);
        setMustCleanUp(false);
    }
}

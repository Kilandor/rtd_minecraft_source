package com.czecherface.adminpowers;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class TrueMaterial {

    private int id;
    private byte data;

    private TrueMaterial(int id, byte data) {
        this.id = id;
        this.data = data;
    }

    /*
     * Accepts only strings of the from "{INTEGER}-{BYTE}" or "{INTEGER}", so something like "17-2" or "17".
     * @return TrueMaterial on success, null on failure.
     */
    public static TrueMaterial get(String material) {
        String[] result = material.split("-");
        if (result.length < 1 || result.length > 2) {
            return null;
        }
        try {
            int id = Integer.parseInt(result[0]);
            if (Material.getMaterial(id) != null) {
                return new TrueMaterial(id, result.length == 1 ? 0 : Byte.parseByte(result[1]));
            }
        } catch (NumberFormatException nfe) {
        }
        return null;
    }
    
    public static TrueMaterial get(Block block) {
        return new TrueMaterial(block.getTypeId(), block.getData());
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public String name() {
        return getMaterial().name() + (data != 0 ? " [" + data + "]" : "");
    }

    public void applyTo(Block b) {
        b.setTypeId(id);
        if (data != 0) {
            b.setData(data);
        }
    }

    //Convenience
    public Material getMaterial() {
        return Material.getMaterial(id);
    }

    //Convenience
    public boolean equals(Material m) {
        return getMaterial() == m;
    }

    //Convenience
    public static String getAsString(Block block) {
        return block == null ? null : block.getTypeId() + "-" + block.getData();
    }
    
    public boolean equals(Block b) {
        return b.getTypeId() == id && b.getData() == data;
    }
}

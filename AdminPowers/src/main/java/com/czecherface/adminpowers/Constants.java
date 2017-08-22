package com.czecherface.adminpowers;

import java.util.HashSet;
import org.bukkit.block.BlockFace;

public class Constants {
    public static final String TITLE = "[{gold}AP{white}] ";
    public static final String VERSION = "2.27";
    public static final int TEXT_LINES_PER_PAGE = 7;
    
    public static final BlockFace[] MAIN_FACES = {
        BlockFace.UP, BlockFace.DOWN,
        BlockFace.EAST, BlockFace.WEST,
        BlockFace.SOUTH, BlockFace.NORTH
    };
    
    public static final long BLOCK_FILL_LIMIT = 64000;
    
    public static final HashSet<Byte> LOS_TRACE_TRANSPARENT_MATERIALS;
    
    
    /* Any other initialization code */
    static 
    {
        final byte[] mats = {0,8,9,10,11,51};
        LOS_TRACE_TRANSPARENT_MATERIALS = new HashSet<Byte>(mats.length);
        for (int i = 0; i < mats.length; i++)
            LOS_TRACE_TRANSPARENT_MATERIALS.add(mats[i]);
    }
}

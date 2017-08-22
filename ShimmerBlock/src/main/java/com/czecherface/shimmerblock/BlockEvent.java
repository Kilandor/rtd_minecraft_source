package com.czecherface.shimmerblock;

import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.kilandor.chat.Chat;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class BlockEvent extends BlockListener {

    private Hashtable<Player, Location> inProgress;
    private final ShimmerBlock plugin;
    private final Chat chat;
    private final SQL sql;

    public BlockEvent(ShimmerBlock shimmerBlock, Chat chat) {
        plugin = shimmerBlock;
        this.chat = chat;
        sql = SQL.getInstance();
        inProgress = new Hashtable<Player, Location>();
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block placedBlock = event.getBlock();

        if (!plugin.playerAllowed(player) || placedBlock.getType() != Material.SPONGE) {
            return;
        }

        Location locA = inProgress.get(player);
        if (locA == null || locA.getBlock().getType() != Material.SPONGE || locA.getBlock().equals(placedBlock)) {
            inProgress.put(player, placedBlock.getLocation());
            chat.playerMsg(player, ShimmerBlock.CHATTITLE, "Now choose a destination with another sponge.", false);
            return;
        }

        if (sql.createLink(locA, placedBlock.getLocation())) {
            inProgress.remove(player);
            chat.playerMsg(player, ShimmerBlock.CHATTITLE, "Link successfully created!", false);
            return;
        }

        chat.playerMsg(player, ShimmerBlock.CHATTITLE, "DATABASE ERROR: Could not create your link!", false);
    }

    @Override
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks()) {
            if (b.getType() == Material.SPONGE) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @Override
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isSticky() && event.getRetractLocation().getBlock().getType() == Material.SPONGE) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (b.getType() != Material.SPONGE) {
            return;
        }

        Location exit_loc = sql.getExit(b.getLocation());
        if (exit_loc == null) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!plugin.playerAllowed(player)) {
            chat.playerMsg(player, ShimmerBlock.CHATTITLE, "This block is protected.", false);
            return;
        }

        sql.deleteLink(b.getLocation());
        b.setType(Material.AIR);
        Block exit_block = exit_loc.getBlock();
        if (exit_block.getType() == Material.SPONGE) {
            exit_block.setType(Material.AIR);
        }
        chat.playerMsg(event.getPlayer(), ShimmerBlock.CHATTITLE, "Successfully deleted a link!", false);
    }
}

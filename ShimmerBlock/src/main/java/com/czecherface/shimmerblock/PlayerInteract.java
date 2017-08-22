package com.czecherface.shimmerblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.kilandor.chat.Chat;

public class PlayerInteract extends PlayerListener {

    private ShimmerBlock plugin;
    private Chat chat;

    public PlayerInteract(ShimmerBlock instance) {
        plugin = instance;
        chat = plugin.getChat();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock.getType() != Material.SPONGE) {
            return;
        }

        Location exitLoc = SQL.getInstance().getExit(clickedBlock.getLocation());
        if (exitLoc == null) {
            return;
        }

        //Alright, the user was correct to attempt to right-click this block, cancel it.
        event.setCancelled(true);

        //Check to make sure the exit block actually exists
        Player player = event.getPlayer();
        Block exitBlock = exitLoc.getBlock();
        if (exitBlock.getType() != Material.SPONGE) {
            SQL.getInstance().deleteLink(clickedBlock.getLocation());
            clickedBlock.setType(Material.AIR);
            chat.playerMsg(player, ShimmerBlock.CHATTITLE, "The destination no longer exists! Link deleted.", false);
            return;
        }

        //Okay, now we need to check for air around the block to teleport the player to
        Block destination = null;
        BlockFace chosen = null;
        for (BlockFace f : BlockFace.values()) {
            if (f == BlockFace.DOWN) {
                continue;
            }
            Block subject = exitBlock.getRelative(f);
            Material a = subject.getType(), b = subject.getRelative(BlockFace.UP).getType();
            if ((b == Material.AIR || b == Material.STATIONARY_WATER || b == Material.WATER)
                    && (a == Material.AIR || a == Material.STATIONARY_WATER || a == Material.WATER)) {
                destination = subject;
                chosen = f;
                break;
            }
        }

        if (destination == null) {
            chat.playerMsg(player, ShimmerBlock.CHATTITLE, "The exit of this link is blocked.", false);
            return;
        }

        //Success!  Teleport them to the specialized location.
        Location playerLoc = player.getLocation();
        float yaw = 0.0f;
        switch (chosen) {
            case NORTH:
                yaw = 270.0f;
                break;
            case SOUTH:
                yaw = 90.0f;
                break;
            case WEST:
                yaw = 180.0f;
                break;
        }
        Location finalDestination = new Location(
                destination.getWorld(),
                destination.getX() + 0.5,
                destination.getY(),
                destination.getZ() + 0.5,
                yaw,
                chosen == BlockFace.UP ? 90.0f : playerLoc.getPitch());
        finalDestination.getWorld().loadChunk(finalDestination.getBlock().getChunk());
        player.teleport(finalDestination);
    }
}

package com.czecherface.adminpowers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.kilandor.chat.Chat;

import com.czecherface.adminpowers.powers._Interaction;
import com.czecherface.adminpowers.powers._Power;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {

    private AdminPowers plugin;
    @SuppressWarnings("unused")
    private Chat chat;

    public PlayerListener(AdminPowers instance) {
        plugin = instance;
        chat = plugin.getChat();
    }

    @Override
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        _Power p = plugin.getPlayerPower(player.getName());
        if (p == null) {
            return;
        }
        if (p.getAcceptedInteraction() != _Interaction.ITEM_DROP)
            return;
        event.setCancelled(true);
        p.activate(_Interaction.ITEM_DROP);
        plugin.removePlayerPower(player.getName());
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getItemInHand().getType() != Material.BOOK) {
            return;
        }

        _Power p = plugin.getPlayerPower(player.getName());
        if (p == null) {
            return;
        }

        Action a = event.getAction();
        //What is this physical action and when will I need it?
        if (a == Action.PHYSICAL) {
            return;
        }

        _Interaction i;
        if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
            i = _Interaction.LEFT_CLICK;
        } else if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            i = _Interaction.RIGHT_CLICK;
        } else {
            return;
        }

        _Interaction accepted = p.getAcceptedInteraction();
        if (accepted != _Interaction.BOTH_CLICK && accepted != i) {
            return;
        }

        event.setCancelled(true);
        p.activate(i);
    }
}

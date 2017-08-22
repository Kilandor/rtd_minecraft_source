package org.kilandor.torchlight;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerAnimationEvent;

public class TLPlayerListener extends PlayerListener {
	private Torchlight plugin;

	public TLPlayerListener(Torchlight instance) {
		plugin = instance;
	}

	public void onPlayerMove(PlayerMoveEvent event) {
		plugin.lightingCheck(event.getPlayer());
	}

	public void onPlayerAnimation(PlayerAnimationEvent event) {
		plugin.lightingCheck(event.getPlayer());
	}
}

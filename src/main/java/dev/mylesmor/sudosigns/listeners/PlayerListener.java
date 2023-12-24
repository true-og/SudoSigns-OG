package dev.mylesmor.sudosigns.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.mylesmor.sudosigns.SudoSigns;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		SudoSigns.users.remove(e.getPlayer().getUniqueId());
	}
}
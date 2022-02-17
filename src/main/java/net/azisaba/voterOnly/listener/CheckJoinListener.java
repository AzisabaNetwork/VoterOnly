package net.azisaba.voterOnly.listener;

import net.azisaba.voterOnly.VoterOnly;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class CheckJoinListener implements Listener {
    private final VoterOnly plugin;

    public CheckJoinListener(VoterOnly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (Bukkit.getOnlinePlayers().size() < plugin.freeUntil) {
            return;
        }
        if (plugin.hasPermission(e.getUniqueId(), "voteronly.bypass")) {
            return;
        }
        if (!plugin.isAllowed(e.getName())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', plugin.disallowMessage));
        }
    }
}

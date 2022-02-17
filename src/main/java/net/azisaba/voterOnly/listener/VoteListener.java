package net.azisaba.voterOnly.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.azisaba.voterOnly.VoterOnly;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {
    private final VoterOnly plugin;

    public VoteListener(VoterOnly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(VotifierEvent e) {
        plugin.addToAllowedList(e.getVote().getUsername());
    }
}

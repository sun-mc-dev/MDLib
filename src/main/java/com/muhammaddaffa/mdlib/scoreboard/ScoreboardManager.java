package com.muhammaddaffa.mdlib.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager implements Listener {

    private final Map<String, SimpleScoreboard> scoreboards = new HashMap<>();

    public void register(String id, SimpleScoreboard scoreboard) {
        scoreboards.put(id, scoreboard);
    }

    public SimpleScoreboard get(String id) {
        return scoreboards.get(id);
    }

    public void show(String id, Player player) {
        SimpleScoreboard sb = scoreboards.get(id);
        if (sb != null) sb.show(player);
    }

    public void hide(String id, Player player) {
        SimpleScoreboard sb = scoreboards.get(id);
        if (sb != null) sb.hide(player);
    }

    public void hideAll(Player player) {
        scoreboards.values().forEach(sb -> sb.hide(player));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        hideAll(e.getPlayer());
    }

    public void shutdown() {
        scoreboards.values().forEach(sb -> {
            sb.stopAutoUpdate();
            sb.removeAll();
        });
        scoreboards.clear();
    }
}
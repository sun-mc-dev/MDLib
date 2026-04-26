package com.muhammaddaffa.mdlib.util;

import com.muhammaddaffa.mdlib.MDLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VisibilityManager {

    private static final Set<UUID> vanished = new HashSet<>();

    /**
     * Hides a player from everyone else.
     */
    public static void vanish(@NotNull Player player) {
        vanished.add(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            other.hidePlayer(MDLib.instance(), player);
        }
    }

    /**
     * Shows a vanished player to everyone.
     */
    public static void unvanish(@NotNull Player player) {
        vanished.remove(player.getUniqueId());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(player)) continue;
            other.showPlayer(MDLib.instance(), player);
        }
    }

    /**
     * Hides all players from a specific player except a set of "allowed" players.
     * Useful for arenas.
     *
     * @param viewer  The player who should have limited vision.
     * @param allowed The players that should remain visible.
     */
    public static void isolate(@NotNull Player viewer, @NotNull Set<Player> allowed) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(viewer) || allowed.contains(other)) continue;
            viewer.hidePlayer(MDLib.instance(), other);
        }
    }

    /**
     * Restores visibility for a player, showing everyone again.
     */
    public static void restore(@NotNull Player viewer) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.equals(viewer)) continue;
            viewer.showPlayer(MDLib.instance(), other);
        }
    }

    /**
     * Checks if a player is currently vanished.
     */
    public static boolean isVanished(@NotNull Player player) {
        return vanished.contains(player.getUniqueId());
    }

}

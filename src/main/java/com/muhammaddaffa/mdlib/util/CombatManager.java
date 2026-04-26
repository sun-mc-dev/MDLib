package com.muhammaddaffa.mdlib.util;

import com.muhammaddaffa.mdlib.MDLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager implements Listener {

    private static final Map<UUID, CombatTag> tags = new ConcurrentHashMap<>();
    private static int combatSeconds = 10;

    public CombatManager(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Sets the default combat duration.
     * @param seconds Duration in seconds.
     */
    public static void setCombatDuration(int seconds) {
        combatSeconds = seconds;
    }

    /**
     * Tags a player in combat with another player.
     */
    public static void tag(@NotNull Player victim, @NotNull Player attacker) {
        long end = System.currentTimeMillis() + (combatSeconds * 1000L);
        tags.put(victim.getUniqueId(), new CombatTag(attacker.getUniqueId(), end));
        tags.put(attacker.getUniqueId(), new CombatTag(victim.getUniqueId(), end));
    }

    /**
     * Checks if a player is currently in combat.
     */
    public static boolean isInCombat(@NotNull Player player) {
        CombatTag tag = tags.get(player.getUniqueId());
        if (tag == null) return false;
        if (System.currentTimeMillis() > tag.expireTime) {
            tags.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    /**
     * Gets the last attacker of a player.
     */
    public static @Nullable UUID getLastAttacker(@NotNull Player player) {
        CombatTag tag = tags.get(player.getUniqueId());
        return tag != null ? tag.attacker : null;
    }

    /**
     * Gets the remaining combat time for a player.
     */
    public static long getRemainingMillis(@NotNull Player player) {
        CombatTag tag = tags.get(player.getUniqueId());
        if (tag == null) return 0;
        long remaining = tag.expireTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(@NonNull EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            tag(victim, attacker);
        }
    }

    @EventHandler
    public void onQuit(@NonNull PlayerQuitEvent event) {
        tags.remove(event.getPlayer().getUniqueId());
    }

    private record CombatTag(UUID attacker, long expireTime) {}

}

package com.muhammaddaffa.mdlib.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private static final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    /**
     * Sets a cooldown for a player.
     *
     * @param player  The player.
     * @param key     The cooldown key (e.g., "enderpearl").
     * @param seconds The duration in seconds.
     */
    public static void set(@NotNull Player player, @NotNull String key, int seconds) {
        set(player.getUniqueId(), key, seconds);
    }

    /**
     * Sets a cooldown for a UUID.
     *
     * @param uuid    The UUID.
     * @param key     The cooldown key.
     * @param seconds The duration in seconds.
     */
    public static void set(@NotNull UUID uuid, @NotNull String key, int seconds) {
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(uuid, k -> new HashMap<>());
        playerCooldowns.put(key, System.currentTimeMillis() + (seconds * 1000L));
    }

    /**
     * Checks if a player has an active cooldown.
     *
     * @param player The player.
     * @param key    The cooldown key.
     * @return true if the cooldown is still active.
     */
    public static boolean has(@NotNull Player player, @NotNull String key) {
        return has(player.getUniqueId(), key);
    }

    /**
     * Checks if a UUID has an active cooldown.
     *
     * @param uuid The UUID.
     * @param key  The cooldown key.
     * @return true if the cooldown is still active.
     */
    public static boolean has(@NotNull UUID uuid, @NotNull String key) {
        return getRemainingMillis(uuid, key) > 0;
    }

    /**
     * Gets the remaining cooldown time in milliseconds.
     *
     * @param uuid The UUID.
     * @param key  The cooldown key.
     * @return The remaining time in ms, or 0 if no cooldown.
     */
    public static long getRemainingMillis(@NotNull UUID uuid, @NotNull String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return 0;

        Long end = playerCooldowns.get(key);
        if (end == null) return 0;

        long remaining = end - System.currentTimeMillis();
        if (remaining <= 0) {
            playerCooldowns.remove(key);
            return 0;
        }
        return remaining;
    }

    /**
     * Gets the remaining cooldown time in seconds (rounded up).
     *
     * @param player The player.
     * @param key    The cooldown key.
     * @return The remaining time in seconds.
     */
    public static int getRemainingSeconds(@NotNull Player player, @NotNull String key) {
        return (int) Math.ceil(getRemainingMillis(player.getUniqueId(), key) / 1000.0);
    }

    /**
     * Removes a cooldown for a player.
     *
     * @param player The player.
     * @param key    The cooldown key.
     */
    public static void remove(@NotNull Player player, @NotNull String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns != null) {
            playerCooldowns.remove(key);
        }
    }

    /**
     * Clears all cooldowns for a player.
     *
     * @param player The player.
     */
    public static void clear(@NotNull Player player) {
        cooldowns.remove(player.getUniqueId());
    }

}
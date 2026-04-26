package com.muhammaddaffa.mdlib.util;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BossBarUtils {

    /**
     * Creates a simple BossBar.
     */
    public static @NotNull BossBar create(@NotNull String title, float progress, BossBar.Color color, BossBar.Overlay overlay) {
        return BossBar.bossBar(MiniMessageUtils.parse(title), progress, color, overlay);
    }

    /**
     * Shows a BossBar to a player.
     */
    public static void show(@NotNull Player player, @NotNull BossBar bossBar) {
        player.showBossBar(bossBar);
    }

    /**
     * Hides a BossBar from a player.
     */
    public static void hide(@NotNull Player player, @NotNull BossBar bossBar) {
        player.hideBossBar(bossBar);
    }

    /**
     * Updates a BossBar's title.
     */
    public static void updateTitle(@NotNull BossBar bossBar, @NotNull String title) {
        bossBar.name(MiniMessageUtils.parse(title));
    }
}
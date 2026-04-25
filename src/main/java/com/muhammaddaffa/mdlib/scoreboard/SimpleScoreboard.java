package com.muhammaddaffa.mdlib.scoreboard;

import com.muhammaddaffa.mdlib.task.ExecutorManager;
import com.muhammaddaffa.mdlib.task.handleTask.HandleTask;
import com.muhammaddaffa.mdlib.util.Placeholder;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class SimpleScoreboard {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private String title;
    private List<String> lines;
    private Function<Player, Placeholder> placeholderSupplier;
    private long updateInterval = 20L;
    private HandleTask task;

    public static SimpleScoreboard create() {
        return new SimpleScoreboard();
    }

    public SimpleScoreboard title(String title) {
        this.title = title;
        return this;
    }

    public SimpleScoreboard lines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        return this;
    }

    public SimpleScoreboard lines(String... lines) {
        return lines(Arrays.asList(lines));
    }

    public SimpleScoreboard updateInterval(long ticks) {
        this.updateInterval = ticks;
        return this;
    }

    public SimpleScoreboard placeholders(Function<Player, Placeholder> supplier) {
        this.placeholderSupplier = supplier;
        return this;
    }

    public void show(Player player) {
        FastBoard board = new FastBoard(player);
        boards.put(player.getUniqueId(), board);
        update(player);
    }

    public void hide(Player player) {
        FastBoard board = boards.remove(player.getUniqueId());
        if (board != null && !board.isDeleted()) board.delete();
    }

    public void update(Player player) {
        FastBoard board = boards.get(player.getUniqueId());
        if (board == null || board.isDeleted()) return;

        Placeholder ph = placeholderSupplier != null ? placeholderSupplier.apply(player) : null;

        String resolvedTitle = ph != null ? ph.translate(title) : title;
        board.updateTitle(LEGACY.deserialize(resolvedTitle != null ? resolvedTitle : ""));

        if (lines != null) {
            List<Component> components = new ArrayList<>();
            for (String line : lines) {
                String resolved = ph != null ? ph.translate(line) : line;
                components.add(LEGACY.deserialize(resolved != null ? resolved : ""));
            }
            board.updateLines(components);
        }
    }

    public void updateAll() {
        for (UUID uuid : boards.keySet()) {
            FastBoard board = boards.get(uuid);
            if (board == null || board.isDeleted()) continue;
            Player p = org.bukkit.Bukkit.getPlayer(uuid);
            if (p != null) update(p);
        }
    }

    public void startAutoUpdate() {
        stopAutoUpdate();
        task = ExecutorManager.getProvider().syncTimer(updateInterval, updateInterval, this::updateAll);
    }

    public void stopAutoUpdate() {
        if (task != null && !task.isCancelled()) task.cancel();
        task = null;
    }

    public void removeAll() {
        boards.values().forEach(b -> {
            if (!b.isDeleted()) b.delete();
        });
        boards.clear();
    }

    public boolean isShowing(Player player) {
        return boards.containsKey(player.getUniqueId());
    }

    public void toggle(Player player) {
        if (isShowing(player)) hide(player);
        else show(player);
    }
}

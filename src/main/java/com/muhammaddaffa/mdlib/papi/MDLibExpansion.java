package com.muhammaddaffa.mdlib.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class MDLibExpansion extends PlaceholderExpansion {

    private final Map<String, BiFunction<OfflinePlayer, String, String>> handlers = new HashMap<>();

    protected void register(@NotNull String identifier, BiFunction<OfflinePlayer, String, String> handler) {
        handlers.put(identifier.toLowerCase(), handler);
    }

    protected void register(@NotNull String identifier, Function<OfflinePlayer, String> handler) {
        handlers.put(identifier.toLowerCase(), (player, params) -> handler.apply(player));
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String lower = params.toLowerCase();
        for (Map.Entry<String, BiFunction<OfflinePlayer, String, String>> entry : handlers.entrySet()) {
            if (lower.equals(entry.getKey()) || lower.startsWith(entry.getKey() + "_")) {
                String sub = lower.equals(entry.getKey()) ? "" : params.substring(entry.getKey().length() + 1);
                return entry.getValue().apply(player, sub);
            }
        }
        return null;
    }

    public void registerExpansion() {
        registerPH();
    }

    protected abstract void registerPH();
}
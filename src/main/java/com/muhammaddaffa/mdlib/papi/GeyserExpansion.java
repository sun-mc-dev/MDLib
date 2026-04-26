package com.muhammaddaffa.mdlib.papi;

import com.muhammaddaffa.mdlib.geyser.GeyserUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GeyserExpansion extends MDLibExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "muhammaddaffa";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "geyser";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.3.6";
    }

    @Override
    protected void registerPH() {
        register("is_bedrock", player -> {
            if (player instanceof Player p) {
                return String.valueOf(GeyserUtils.isBedrockPlayer(p));
            }
            return "false";
        });

        register("platform", player -> {
            if (player instanceof Player p) {
                return GeyserUtils.getPlatformName(p);
            }
            return "Java";
        });

        register("input_mode", player -> {
            if (player instanceof Player p) {
                var mode = GeyserUtils.getInputMode(p);
                return mode != null ? mode.name() : "N/A";
            }
            return "N/A";
        });

        register("xuid", player -> {
            if (player instanceof Player p) {
                var xuid = GeyserUtils.getXuid(p);
                return xuid != null ? xuid : "";
            }
            return "";
        });

        register("version", player -> {
            if (player instanceof Player p) {
                var version = GeyserUtils.getVersion(p);
                return version != null ? version : "";
            }
            return "";
        });

        register("is_console", player -> {
            if (player instanceof Player p) {
                return String.valueOf(GeyserUtils.isConsolePlayer(p));
            }
            return "false";
        });

        register("is_touch", player -> {
            if (player instanceof Player p) {
                return String.valueOf(GeyserUtils.isTouchPlayer(p));
            }
            return "false";
        });
    }
}

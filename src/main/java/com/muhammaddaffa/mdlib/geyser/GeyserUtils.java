package com.muhammaddaffa.mdlib.geyser;

import com.muhammaddaffa.mdlib.MDLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.api.util.ApiVersion;
import org.geysermc.api.util.BedrockPlatform;
import org.geysermc.api.util.InputMode;
import org.geysermc.cumulus.form.Form;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.bedrock.camera.CameraShake;
import org.geysermc.geyser.api.command.CommandSource;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.util.MinecraftVersion;
import org.geysermc.geyser.api.util.PlatformType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GeyserUtils {

    /**
     * Checks if Geyser is installed and enabled.
     *
     * @return true if Geyser is installed.
     */
    public static boolean isGeyserInstalled() {
        return MDLib.hasGeyser();
    }

    /**
     * Checks if a player is connected via Bedrock Edition.
     *
     * @param player The player to check.
     * @return true if the player is a Bedrock player.
     */
    public static boolean isBedrockPlayer(@NotNull Player player) {
        return isBedrockPlayer(player.getUniqueId());
    }

    /**
     * Checks if a UUID belongs to a Bedrock player.
     *
     * @param uuid The UUID to check.
     * @return true if the UUID belongs to a Bedrock player.
     */
    public static boolean isBedrockPlayer(@NotNull UUID uuid) {
        if (!isGeyserInstalled()) return false;
        return GeyserApi.api().isBedrockPlayer(uuid);
    }

    /**
     * Gets the connection information for a Bedrock player.
     *
     * @param player The player.
     * @return The GeyserConnection, or null if not a Bedrock player.
     */
    public static @Nullable GeyserConnection getConnection(@NotNull Player player) {
        return getConnection(player.getUniqueId());
    }

    /**
     * Gets the connection information for a Bedrock player.
     *
     * @param uuid The UUID.
     * @return The GeyserConnection, or null if not a Bedrock player.
     */
    public static @Nullable GeyserConnection getConnection(@NotNull UUID uuid) {
        if (!isGeyserInstalled()) return null;
        return GeyserApi.api().connectionByUuid(uuid);
    }

    /**
     * Gets the platform of a Bedrock player.
     *
     * @param player The player.
     * @return The Platform, or null if not a Bedrock player.
     */
    public static @Nullable BedrockPlatform getPlatform(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null ? connection.platform() : null;
    }

    /**
     * Gets the platform name of a Bedrock player.
     *
     * @param player The player.
     * @return The platform name (e.g., "Android", "iOS"), or "Java" if not a Bedrock player.
     */
    public static @NotNull String getPlatformName(@NotNull Player player) {
        BedrockPlatform platform = getPlatform(player);
        return platform != null ? platform.toString() : "Java";
    }

    /**
     * Gets the Xbox User ID (XUID) of a Bedrock player.
     *
     * @param player The player.
     * @return The XUID, or null if not a Bedrock player.
     */
    public static @Nullable String getXuid(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null ? connection.xuid() : null;
    }

    /**
     * Gets the Bedrock version of a player.
     *
     * @param player The player.
     * @return The version string, or null if not a Bedrock player.
     */
    public static @Nullable String getVersion(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null ? connection.version() : null;
    }

    /**
     * Gets the language code of a Bedrock player.
     *
     * @param player The player.
     * @return The language code (e.g., "en_US"), or null if not a Bedrock player.
     */
    public static @Nullable String getLanguageCode(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null ? connection.languageCode() : null;
    }

    /**
     * Gets the input mode of a Bedrock player.
     *
     * @param player The player.
     * @return The InputMode, or null if not a Bedrock player.
     */
    public static @Nullable InputMode getInputMode(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null ? connection.inputMode() : null;
    }

    /**
     * Checks if a player is using a touch device.
     *
     * @param player The player.
     * @return true if the player is using touch controls.
     */
    public static boolean isTouchPlayer(@NotNull Player player) {
        return getInputMode(player) == InputMode.TOUCH;
    }

    /**
     * Checks if a player is on a console (PS4, Xbox, Switch).
     *
     * @param player The player.
     * @return true if the player is on a console.
     */
    public static boolean isConsolePlayer(@NotNull Player player) {
        BedrockPlatform platform = getPlatform(player);
        if (platform == null) return false;
        return platform == BedrockPlatform.PS4 || platform == BedrockPlatform.XBOX || platform == BedrockPlatform.NX;
    }

    /**
     * Transfers a Bedrock player to another server.
     *
     * @param player  The player.
     * @param address The server address.
     * @param port    The server port.
     * @return true if the transfer request was sent.
     */
    public static boolean transfer(@NotNull Player player, @NotNull String address, int port) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) {
            connection.transfer(address, port);
            return true;
        }
        return false;
    }

    public static boolean sendEmote(@NotNull Player player, @NotNull String emoteId) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) {
            connection.showEmote(connection.playerEntity(), emoteId);
            return true;
        }
        return false;
    }

    /**
     * Gets the current ping of a Bedrock player.
     *
     * @param player The player.
     * @return The ping, or -1 if not a Bedrock player.
     */
    public static int getPing(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null ? connection.ping() : -1;
    }

    /**
     * Checks if a Bedrock player currently has a form open.
     *
     * @param player The player.
     * @return true if a form is open.
     */
    public static boolean hasFormOpen(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null && connection.hasFormOpen();
    }

    /**
     * Closes the currently open form for a Bedrock player.
     *
     * @param player The player.
     */
    public static void closeForm(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) connection.closeForm();
    }

    /**
     * Gets the Bedrock protocol version of a player.
     *
     * @param player The player.
     * @return The protocol version, or -1 if not a Bedrock player.
     */
    public static int getProtocolVersion(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        return connection != null ? connection.protocolVersion() : -1;
    }

    /**
     * Gets the address the player used to join.
     *
     * @param player The player.
     * @return The join address, or null if not a Bedrock player.
     */
    public static @Nullable String getJoinAddress(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        try {
            return connection != null ? connection.joinAddress() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Gets the port the player used to join.
     *
     * @param player The player.
     * @return The join port, or -1 if not a Bedrock player.
     */
    public static int getJoinPort(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        try {
            return connection != null ? connection.joinPort() : -1;
        } catch (Exception ignored) {
            return -1;
        }
    }

    /**
     * Shakes the Bedrock player's camera.
     *
     * @param player    The player.
     * @param intensity The intensity (0-4).
     * @param duration  The duration in seconds.
     * @param type      The shake type.
     */
    public static void shakeCamera(@NotNull Player player, float intensity, float duration, @NotNull CameraShake type) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) {
            connection.camera().shakeCamera(intensity, duration, type);
        }
    }

    /**
     * Stops all camera shake for a Bedrock player.
     *
     * @param player The player.
     */
    public static void stopCameraShake(@NotNull Player player) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) {
            connection.camera().stopCameraShake();
        }
    }

    /**
     * Sends fog to a Bedrock player.
     *
     * @param player The player.
     * @param fogIds The fog IDs to send.
     */
    public static void sendFog(@NotNull Player player, String... fogIds) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) {
            connection.camera().sendFog(fogIds);
        }
    }

    /**
     * Removes fog from a Bedrock player.
     *
     * @param player The player.
     * @param fogIds The fog IDs to remove.
     */
    public static void removeFog(@NotNull Player player, String... fogIds) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) {
            connection.camera().removeFog(fogIds);
        }
    }

    /**
     * Sends a form to a Bedrock player.
     *
     * @param player The player.
     * @param form   The form to send.
     * @return true if the form was sent.
     */
    public static boolean sendForm(@NotNull Player player, @NotNull Form form) {
        GeyserConnection connection = getConnection(player);
        if (connection != null) {
            connection.sendForm(form);
            return true;
        }
        return false;
    }

    /**
     * Gets a list of all currently online Bedrock players.
     *
     * @return A list of online Bedrock players.
     */
    public static @NotNull List<Player> getOnlineBedrockPlayers() {
        if (!isGeyserInstalled()) return Collections.emptyList();
        return GeyserApi.api().onlineConnections().stream()
                .map(conn -> Bukkit.getPlayer(conn.javaUuid()))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Gets all active Geyser connections.
     *
     * @return A list of Geyser connections.
     */
    public static @NotNull List<? extends GeyserConnection> getOnlineBedrockConnections() {
        if (!isGeyserInstalled()) return Collections.emptyList();
        return GeyserApi.api().onlineConnections();
    }

    /**
     * Gets the list of supported Bedrock versions.
     *
     * @return A list of supported versions.
     */
    public static @NotNull List<MinecraftVersion> getSupportedBedrockVersions() {
        if (!isGeyserInstalled()) return Collections.emptyList();
        return GeyserApi.api().supportedBedrockVersions();
    }

    /**
     * Gets the platform type Geyser is running on.
     *
     * @return The platform type, or null if Geyser is not installed.
     */
    public static @Nullable PlatformType getPlatformType() {
        if (!isGeyserInstalled()) return null;
        return GeyserApi.api().platformType();
    }

    /**
     * Checks if a player name belongs to a Bedrock player.
     *
     * @param name The player name.
     * @return true if the player is on Bedrock.
     */
    public static boolean isBedrockPlayer(@NotNull String name) {
        Player player = Bukkit.getPlayer(name);
        return player != null && isBedrockPlayer(player);
    }

    /**
     * Gets the console command source for Geyser.
     *
     * @return The console command source, or null if Geyser is not installed.
     */
    public static @Nullable CommandSource getConsoleCommandSource() {
        if (!isGeyserInstalled()) return null;
        return GeyserApi.api().consoleCommandSource();
    }

    /**
     * Gets the supported Java Minecraft version.
     *
     * @return The supported version, or null if Geyser is not installed.
     */
    public static @Nullable MinecraftVersion getSupportedJavaVersion() {
        if (!isGeyserInstalled()) return null;
        return GeyserApi.api().supportedJavaVersion();
    }

    /**
     * Gets the current Geyser API version.
     *
     * @return The API version, or null if Geyser is not installed.
     */
    public static @Nullable ApiVersion getGeyserApiVersion() {
        if (!isGeyserInstalled()) return null;
        return GeyserApi.api().geyserApiVersion();
    }
}
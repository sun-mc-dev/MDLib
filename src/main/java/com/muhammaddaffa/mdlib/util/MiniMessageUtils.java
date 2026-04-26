package com.muhammaddaffa.mdlib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MiniMessageUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Parses a MiniMessage string into a Component.
     *
     * @param message The MiniMessage string.
     * @return The parsed Component.
     */
    public static @NotNull Component parse(@Nullable String message) {
        if (message == null || message.isEmpty()) return Component.empty();
        return MINI_MESSAGE.deserialize(message);
    }

    /**
     * Parses a MiniMessage string into a Component with custom tag resolvers.
     *
     * @param message   The MiniMessage string.
     * @param resolvers The tag resolvers to use.
     * @return The parsed Component.
     */
    public static @NotNull Component parse(@Nullable String message, @NotNull TagResolver... resolvers) {
        if (message == null || message.isEmpty()) return Component.empty();
        return MINI_MESSAGE.deserialize(message, resolvers);
    }

    /**
     * Parses a list of MiniMessage strings into a list of Components.
     *
     * @param messages The list of MiniMessage strings.
     * @return The list of parsed Components.
     */
    public static @NotNull List<Component> parse(@NotNull List<String> messages) {
        return messages.stream().map(MiniMessageUtils::parse).collect(Collectors.toList());
    }

    /**
     * Sends a MiniMessage to a CommandSender.
     *
     * @param sender  The sender to send the message to.
     * @param message The MiniMessage string.
     */
    public static void sendMessage(@NotNull CommandSender sender, @Nullable String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(parse(message));
    }

    /**
     * Sends a MiniMessage to a CommandSender with custom tag resolvers.
     *
     * @param sender    The sender to send the message to.
     * @param message   The MiniMessage string.
     * @param resolvers The tag resolvers to use.
     */
    public static void sendMessage(@NotNull CommandSender sender,
                                   @Nullable String message, @NotNull TagResolver... resolvers) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(parse(message, resolvers));
    }

    /**
     * Sends a MiniMessage to a Player as an action bar.
     *
     * @param player  The player to send the message to.
     * @param message The MiniMessage string.
     */
    public static void sendActionBar(@NotNull Player player, @Nullable String message) {
        if (message == null || message.isEmpty()) return;
        player.sendActionBar(parse(message));
    }

    /**
     * Broadcasts a MiniMessage to all players and console.
     *
     * @param message The MiniMessage string.
     */
    public static void broadcast(@Nullable String message) {
        if (message == null || message.isEmpty()) return;
        Bukkit.broadcast(parse(message));
    }

}

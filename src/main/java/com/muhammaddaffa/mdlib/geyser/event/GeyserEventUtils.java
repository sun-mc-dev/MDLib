package com.muhammaddaffa.mdlib.geyser.event;

import com.muhammaddaffa.mdlib.MDLib;
import org.geysermc.event.Event;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.bedrock.ClientEmoteEvent;
import org.geysermc.geyser.api.event.connection.ConnectionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GeyserEventUtils {

    private static final EventRegistrar REGISTRAR = new EventRegistrar() {
    };

    /**
     * Subscribes to a Geyser event.
     *
     * @param eventClass The class of the event to subscribe to.
     * @param handler    The handler for the event.
     * @param <T>        The event type.
     */
    public static <T extends Event> void subscribe(@NotNull Class<T> eventClass,
                                                   @NotNull Consumer<T> handler) {
        if (!MDLib.hasGeyser()) return;
        GeyserApi.api().eventBus().subscribe(REGISTRAR, eventClass, handler);
    }

    /**
     * Subscribes an object with @Subscribe annotated methods to Geyser events.
     *
     * @param listener The listener object.
     */
    public static void registerListener(@NotNull Object listener) {
        if (!MDLib.hasGeyser()) return;
        GeyserApi.api().eventBus().register(REGISTRAR, listener);
    }

    /**
     * Shorthand for subscribing to the ConnectionEvent.
     * Fired when a Bedrock player has successfully connected to Geyser.
     *
     * @param handler The handler for the event.
     */
    public static void onBedrockJoin(@NotNull Consumer<ConnectionEvent> handler) {
        subscribe(ConnectionEvent.class, handler);
    }

    /**
     * Shorthand for subscribing to the ClientEmoteEvent.
     * Fired when a Bedrock player performs an emote.
     *
     * @param handler The handler for the event.
     */
    public static void onEmote(@NotNull Consumer<ClientEmoteEvent> handler) {
        subscribe(ClientEmoteEvent.class, handler);
    }
}
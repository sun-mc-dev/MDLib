package com.muhammaddaffa.mdlib.util;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InteractionBuilder {

    private final Location location;
    private float width = 1.0f;
    private float height = 1.0f;
    private boolean responsive = true;
    private Consumer<Interaction> consumer;

    private InteractionBuilder(Location location) {
        this.location = location;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull InteractionBuilder at(Location location) {
        return new InteractionBuilder(location);
    }

    public InteractionBuilder width(float width) {
        this.width = width;
        return this;
    }

    public InteractionBuilder height(float height) {
        this.height = height;
        return this;
    }

    public InteractionBuilder responsive(boolean responsive) {
        this.responsive = responsive;
        return this;
    }

    public InteractionBuilder apply(Consumer<Interaction> consumer) {
        this.consumer = consumer;
        return this;
    }

    public Interaction spawn() {
        Interaction interaction = location.getWorld().spawn(location, Interaction.class);
        interaction.setInteractionWidth(width);
        interaction.setInteractionHeight(height);
        interaction.setResponsive(responsive);
        if (consumer != null) {
            consumer.accept(interaction);
        }
        return interaction;
    }

}

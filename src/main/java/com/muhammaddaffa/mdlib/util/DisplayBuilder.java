package com.muhammaddaffa.mdlib.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class DisplayBuilder<T extends Display> {

    private final Location location;
    private final EntityType type;
    private final Class<T> clazz;
    private Consumer<T> consumer;

    private DisplayBuilder(Location location, EntityType type, Class<T> clazz) {
        this.location = location;
        this.type = type;
        this.clazz = clazz;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull DisplayBuilder<TextDisplay> text(Location location) {
        return new DisplayBuilder<>(location, EntityType.TEXT_DISPLAY, TextDisplay.class);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull DisplayBuilder<ItemDisplay> item(Location location) {
        return new DisplayBuilder<>(location, EntityType.ITEM_DISPLAY, ItemDisplay.class);
    }

    public DisplayBuilder<T> apply(Consumer<T> consumer) {
        this.consumer = consumer;
        return this;
    }

    public DisplayBuilder<T> transformation(Vector3f translation, Quaternionf leftRotation,
                                            Vector3f scale, Quaternionf rightRotation) {
        return apply(display -> {
            Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
            display.setTransformation(transformation);
        });
    }

    public DisplayBuilder<T> scale(float x, float y, float z) {
        return apply(display -> {
            Transformation t = display.getTransformation();
            display.setTransformation(new Transformation(t.getTranslation(),
                    t.getLeftRotation(), new Vector3f(x, y, z), t.getRightRotation()));
        });
    }

    public DisplayBuilder<T> billboard(Display.Billboard billboard) {
        return apply(display -> display.setBillboard(billboard));
    }

    public DisplayBuilder<T> shadowRadius(float radius) {
        return apply(display -> display.setShadowRadius(radius));
    }

    public DisplayBuilder<T> shadowStrength(float strength) {
        return apply(display -> display.setShadowStrength(strength));
    }

    public DisplayBuilder<T> viewRange(float range) {
        return apply(display -> display.setViewRange(range));
    }

    public DisplayBuilder<T> text(Component component) {
        return apply(display -> {
            if (display instanceof TextDisplay textDisplay) {
                textDisplay.text(component);
            }
        });
    }

    public DisplayBuilder<T> text(String miniMessage) {
        return text(MiniMessageUtils.parse(miniMessage));
    }

    public DisplayBuilder<T> backgroundColor(Color color) {
        return apply(display -> {
            if (display instanceof TextDisplay textDisplay) {
                textDisplay.setBackgroundColor(color);
            }
        });
    }

    public DisplayBuilder<T> item(ItemStack itemStack) {
        return apply(display -> {
            if (display instanceof ItemDisplay itemDisplay) {
                itemDisplay.setItemStack(itemStack);
            }
        });
    }

    public DisplayBuilder<T> displayContext(ItemDisplay.ItemDisplayTransform context) {
        return apply(display -> {
            if (display instanceof ItemDisplay itemDisplay) {
                itemDisplay.setItemDisplayTransform(context);
            }
        });
    }

    public T spawn() {
        T display = location.getWorld().spawn(location, clazz);
        if (consumer != null) {
            consumer.accept(display);
        }
        return display;
    }

}

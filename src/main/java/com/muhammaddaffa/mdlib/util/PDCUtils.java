package com.muhammaddaffa.mdlib.util;

import com.muhammaddaffa.mdlib.MDLib;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PDCUtils {

    /**
     * Sets a string value in the holder's persistent data container.
     */
    public static void set(@NotNull PersistentDataHolder holder, @NotNull String key, @NotNull String value) {
        holder.getPersistentDataContainer().set(getKey(key), PersistentDataType.STRING, value);
    }

    /**
     * Sets an integer value in the holder's persistent data container.
     */
    public static void set(@NotNull PersistentDataHolder holder, @NotNull String key, int value) {
        holder.getPersistentDataContainer().set(getKey(key), PersistentDataType.INTEGER, value);
    }

    /**
     * Sets a double value in the holder's persistent data container.
     */
    public static void set(@NotNull PersistentDataHolder holder, @NotNull String key, double value) {
        holder.getPersistentDataContainer().set(getKey(key), PersistentDataType.DOUBLE, value);
    }

    /**
     * Sets a boolean value in the holder's persistent data container.
     */
    public static void set(@NotNull PersistentDataHolder holder, @NotNull String key, boolean value) {
        holder.getPersistentDataContainer().set(getKey(key), PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    /**
     * Sets a UUID value in the holder's persistent data container.
     */
    public static void set(@NotNull PersistentDataHolder holder, @NotNull String key, @NotNull UUID value) {
        holder.getPersistentDataContainer().set(getKey(key), PersistentDataType.STRING, value.toString());
    }

    /**
     * Gets a string value from the holder's persistent data container.
     */
    public static @Nullable String getString(@NotNull PersistentDataHolder holder, @NotNull String key) {
        return holder.getPersistentDataContainer().get(getKey(key), PersistentDataType.STRING);
    }

    /**
     * Gets an integer value from the holder's persistent data container.
     */
    public static int getInt(@NotNull PersistentDataHolder holder, @NotNull String key, int defaultValue) {
        Integer value = holder.getPersistentDataContainer().get(getKey(key), PersistentDataType.INTEGER);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a double value from the holder's persistent data container.
     */
    public static double getDouble(@NotNull PersistentDataHolder holder, @NotNull String key, double defaultValue) {
        Double value = holder.getPersistentDataContainer().get(getKey(key), PersistentDataType.DOUBLE);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a boolean value from the holder's persistent data container.
     */
    public static boolean getBoolean(@NotNull PersistentDataHolder holder, @NotNull String key) {
        Byte value = holder.getPersistentDataContainer().get(getKey(key), PersistentDataType.BYTE);
        return value != null && value == 1;
    }

    /**
     * Gets a UUID value from the holder's persistent data container.
     */
    public static @Nullable UUID getUUID(@NotNull PersistentDataHolder holder, @NotNull String key) {
        String value = getString(holder, key);
        return value != null ? UUID.fromString(value) : null;
    }

    /**
     * Checks if the holder has a value for the given key.
     */
    public static boolean has(@NotNull PersistentDataHolder holder, @NotNull String key) {
        return holder.getPersistentDataContainer().has(getKey(key));
    }

    /**
     * Removes a key from the holder's persistent data container.
     */
    public static void remove(@NotNull PersistentDataHolder holder, @NotNull String key) {
        holder.getPersistentDataContainer().remove(getKey(key));
    }

    @Contract("_ -> new")
    private static @NotNull NamespacedKey getKey(String key) {
        return new NamespacedKey(MDLib.instance(), key);
    }
}
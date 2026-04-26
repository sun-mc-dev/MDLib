package com.muhammaddaffa.mdlib.util;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReadableKitSerializer {

    /**
     * Converts an inventory (contents + armor) into a readable YAML string.
     * Perfect for sharing kits on a website.
     *
     * @param contents The player's main contents (36 slots).
     * @param armor    The player's armor (4 slots).
     * @return A readable YAML string.
     */
    public static String serialize(ItemStack[] contents, ItemStack[] armor) {
        YamlConfiguration yaml = new YamlConfiguration();
        
        List<String> contentList = new ArrayList<>();
        for (ItemStack item : contents) {
            contentList.add(ItemParser.serialize(item));
        }
        yaml.set("contents", contentList);

        List<String> armorList = new ArrayList<>();
        for (ItemStack item : armor) {
            armorList.add(ItemParser.serialize(item));
        }
        yaml.set("armor", armorList);

        return yaml.saveToString();
    }

    /**
     * Deserializes a kit from a YAML string.
     *
     * @param yamlString The kit YAML.
     * @return A result object containing contents and armor.
     */
    public static @Nullable KitData deserialize(String yamlString) {
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(yamlString);

            List<String> contentStrings = yaml.getStringList("contents");
            ItemStack[] contents = new ItemStack[contentStrings.size()];
            for (int i = 0; i < contentStrings.size(); i++) {
                contents[i] = ItemParser.parse(contentStrings.get(i));
            }

            List<String> armorStrings = yaml.getStringList("armor");
            ItemStack[] armor = new ItemStack[armorStrings.size()];
            for (int i = 0; i < armorStrings.size(); i++) {
                armor[i] = ItemParser.parse(armorStrings.get(i));
            }

            return new KitData(contents, armor);
        } catch (Exception e) {
            return null;
        }
    }

    public record KitData(ItemStack[] contents, ItemStack[] armor) {}

}

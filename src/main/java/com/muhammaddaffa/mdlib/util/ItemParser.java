package com.muhammaddaffa.mdlib.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemParser {

    @Nullable
    public static ItemStack parse(String input) {
        if (input == null || input.isBlank()) return null;

        String[] parts = input.split("\\s+");
        if (parts.length == 0) return null;

        ItemBuilder builder;
        try {
            builder = ItemBuilder.retrieveItemBuilder(parts[0]);
        } catch (Exception e) {
            return null;
        }

        int amount = 1;
        List<String> enchants = new ArrayList<>();
        List<String> flags = new ArrayList<>();
        String name = null;

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("amount:")) {
                try {
                    amount = Integer.parseInt(part.substring(7));
                } catch (NumberFormatException ignored) {
                }
            } else if (part.startsWith("enchant:")) {
                enchants.add(part.substring(8));
            } else if (part.startsWith("flag:")) {
                flags.add(part.substring(5));
            } else if (part.startsWith("name:")) {
                name = part.substring(5).replace("_", " ");
            } else if (part.startsWith("cmd:")) {
                try {
                    builder.customModelData(Integer.parseInt(part.substring(4)));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        builder.amount(Math.max(1, amount));

        if (name != null) builder.name(name);

        for (String ench : enchants) {
            String[] es = ench.split(":");
            if (es.length == 2) {
                Enchantment e = Enchantment.getByName(es[0].toUpperCase());
                if (e != null) {
                    try {
                        builder.enchant(e, Integer.parseInt(es[1]));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        for (String flag : flags) {
            try {
                builder.flags(ItemFlag.valueOf(flag.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return builder.build();
    }

    public static String serialize(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return "AIR";
        StringBuilder sb = new StringBuilder(item.getType().name());
        sb.append(" amount:").append(item.getAmount());
        if (item.hasItemMeta()) {
            var meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                sb.append(" name:").append(meta.getDisplayName().replace(" ", "_"));
            }
            meta.getEnchants().forEach((ench, lvl) ->
                    sb.append(" enchant:").append(ench.displayName(lvl)).append(":").append(lvl));
            meta.getItemFlags().forEach(f -> sb.append(" flag:").append(f.name()));
            if (meta.hasCustomModelData()) sb.append(" cmd:").append(meta.getCustomModelData());
        }
        return sb.toString();
    }
}
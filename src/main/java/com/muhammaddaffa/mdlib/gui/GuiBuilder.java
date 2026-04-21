package com.muhammaddaffa.mdlib.gui;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GuiBuilder {

    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
    private String title;
    private int rows = 3;
    private Consumer<InventoryOpenEvent> openHandler;
    private Consumer<InventoryCloseEvent> closeHandler;
    private boolean cancelClicks = true;

    public static GuiBuilder create() {
        return new GuiBuilder();
    }

    public GuiBuilder title(String title) {
        this.title = title;
        return this;
    }

    public GuiBuilder rows(int rows) {
        this.rows = rows;
        return this;
    }

    public GuiBuilder cancelClicks(boolean cancel) {
        this.cancelClicks = cancel;
        return this;
    }

    public GuiBuilder item(int slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }

    public GuiBuilder item(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        items.put(slot, item);
        handlers.put(slot, handler);
        return this;
    }

    public GuiBuilder onClick(int slot, Consumer<InventoryClickEvent> handler) {
        handlers.put(slot, handler);
        return this;
    }

    public GuiBuilder onOpen(Consumer<InventoryOpenEvent> handler) {
        this.openHandler = handler;
        return this;
    }

    public GuiBuilder onClose(Consumer<InventoryCloseEvent> handler) {
        this.closeHandler = handler;
        return this;
    }

    public FastInv build() {
        boolean doCancel = cancelClicks;
        Map<Integer, Consumer<InventoryClickEvent>> h = Map.copyOf(handlers);

        FastInv inv = new FastInv(rows * 9, title != null ? title : "") {
            @Override
            public void onOpen(InventoryOpenEvent event) {
                if (openHandler != null) openHandler.accept(event);
            }

            @Override
            public void onClose(InventoryCloseEvent event) {
                if (closeHandler != null) closeHandler.accept(event);
            }

            @Override
            public void onClick(InventoryClickEvent event) {
                if (doCancel) event.setCancelled(true);
                Consumer<InventoryClickEvent> handler = h.get(event.getSlot());
                if (handler != null) handler.accept(event);
            }
        };

        items.forEach(inv::setItem);
        return inv;
    }

    public void open(Player player) {
        build().open(player);
    }
}
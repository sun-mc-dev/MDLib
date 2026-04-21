package com.muhammaddaffa.mdlib.gui;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PaginatedGui {

    private final List<ItemStack> contents = new ArrayList<>();
    private final List<Integer> contentSlots = new ArrayList<>();
    private String title = "Menu";
    private int rows = 6;
    private int page = 0;

    private ItemStack prevItem;
    private ItemStack nextItem;
    private int prevSlot = 48;
    private int nextSlot = 50;

    private BiConsumer<InventoryClickEvent, ItemStack> clickHandler;
    private Consumer<InventoryClickEvent> prevHandler;
    private Consumer<InventoryClickEvent> nextHandler;

    public static PaginatedGui create() {
        return new PaginatedGui();
    }

    public PaginatedGui title(String title) {
        this.title = title;
        return this;
    }

    public PaginatedGui rows(int rows) {
        this.rows = rows;
        return this;
    }

    public PaginatedGui addItem(ItemStack item) {
        contents.add(item);
        return this;
    }

    public PaginatedGui addItems(List<ItemStack> items) {
        contents.addAll(items);
        return this;
    }

    public PaginatedGui contentSlots(List<Integer> slots) {
        contentSlots.clear();
        contentSlots.addAll(slots);
        return this;
    }

    public PaginatedGui prevButton(ItemStack item, int slot) {
        prevItem = item;
        prevSlot = slot;
        return this;
    }

    public PaginatedGui nextButton(ItemStack item, int slot) {
        nextItem = item;
        nextSlot = slot;
        return this;
    }

    public PaginatedGui onContentClick(BiConsumer<InventoryClickEvent, ItemStack> handler) {
        this.clickHandler = handler;
        return this;
    }

    public PaginatedGui onPrev(Consumer<InventoryClickEvent> handler) {
        this.prevHandler = handler;
        return this;
    }

    public PaginatedGui onNext(Consumer<InventoryClickEvent> handler) {
        this.nextHandler = handler;
        return this;
    }

    private List<Integer> getContentSlots() {
        if (!contentSlots.isEmpty()) return contentSlots;
        List<Integer> slots = new ArrayList<>();
        int max = rows * 9 - 9;
        for (int i = 0; i < max; i++) slots.add(i);
        return slots;
    }

    public FastInv buildPage(Player player) {
        List<Integer> slots = getContentSlots();
        int pageSize = slots.size();
        int totalPages = (int) Math.ceil((double) contents.size() / pageSize);
        if (page >= totalPages && page > 0) page = totalPages - 1;

        GuiBuilder builder = GuiBuilder.create()
                .title(title + " §8(" + (page + 1) + "/" + Math.max(1, totalPages) + ")")
                .rows(rows);

        int start = page * pageSize;
        for (int i = 0; i < slots.size(); i++) {
            int idx = start + i;
            if (idx >= contents.size()) break;
            ItemStack item = contents.get(idx);
            int slot = slots.get(i);
            builder.item(slot, item, e -> {
                if (clickHandler != null) clickHandler.accept(e, item);
            });
        }

        if (page > 0 && prevItem != null) {
            builder.item(prevSlot, prevItem, e -> {
                page--;
                if (prevHandler != null) prevHandler.accept(e);
                buildPage(player).open(player);
            });
        }
        if (page < totalPages - 1 && nextItem != null) {
            builder.item(nextSlot, nextItem, e -> {
                page++;
                if (nextHandler != null) nextHandler.accept(e);
                buildPage(player).open(player);
            });
        }

        return builder.build();
    }

    public void open(Player player) {
        buildPage(player).open(player);
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        int pageSize = getContentSlots().size();
        return (int) Math.ceil((double) contents.size() / pageSize);
    }
}
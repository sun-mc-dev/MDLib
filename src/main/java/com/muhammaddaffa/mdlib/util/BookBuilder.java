package com.muhammaddaffa.mdlib.util;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BookBuilder {

    private Component title = Component.empty();
    private Component author = Component.empty();
    private final List<Component> pages = new ArrayList<>();

    @Contract(" -> new")
    public static @NotNull BookBuilder create() {
        return new BookBuilder();
    }

    public BookBuilder title(@NotNull String title) {
        this.title = MiniMessageUtils.parse(title);
        return this;
    }

    public BookBuilder author(@NotNull String author) {
        this.author = MiniMessageUtils.parse(author);
        return this;
    }

    public BookBuilder addPage(@NotNull String content) {
        this.pages.add(MiniMessageUtils.parse(content));
        return this;
    }

    public BookBuilder addPage(@NotNull Component content) {
        this.pages.add(content);
        return this;
    }

    public Book build() {
        return Book.book(title, author, pages);
    }

    public void open(@NotNull Player player) {
        player.openBook(build());
    }

    public ItemStack buildAsItem() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        item.editMeta(BookMeta.class, meta -> {
            meta.title(title);
            meta.author(author);
            meta.pages(pages);
        });
        return item;
    }

}

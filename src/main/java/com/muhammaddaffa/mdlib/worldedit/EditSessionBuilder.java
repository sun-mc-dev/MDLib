package com.muhammaddaffa.mdlib.worldedit;

import com.muhammaddaffa.mdlib.MDLib;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.SideEffectSet;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class EditSessionBuilder {

    private final World world;
    private org.bukkit.entity.Player player;
    private boolean fastMode = true;
    private int blockLimit = -1;

    private EditSessionBuilder(World world) {
        this.world = world;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NonNull EditSessionBuilder forWorld(World world) {
        return new EditSessionBuilder(world);
    }

    public EditSessionBuilder fastMode(boolean fastMode) {
        this.fastMode = fastMode;
        return this;
    }

    public EditSessionBuilder blockLimit(int limit) {
        this.blockLimit = limit;
        return this;
    }

    public EditSessionBuilder player(org.bukkit.entity.Player player) {
        this.player = player;
        return this;
    }

    public EditSession build() {
        EditSession session;
        if (MDLib.hasFAWE()) {
            com.sk89q.worldedit.EditSessionBuilder builder = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(world));
            if (player != null) {
                builder.actor(BukkitAdapter.adapt(player));
            }
            session = builder.build();
            session.setSideEffectApplier(fastMode ? SideEffectSet.none() : SideEffectSet.defaults());
        } else {
            session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
            session.setSideEffectApplier(fastMode ? SideEffectSet.none() : SideEffectSet.defaults());
        }

        if (blockLimit > 0) {
            session.setBlockChangeLimit(blockLimit);
        }

        return session;
    }

    public void execute(@NonNull Consumer<EditSession> consumer) {
        try (EditSession session = build()) {
            consumer.accept(session);
        }
    }
}
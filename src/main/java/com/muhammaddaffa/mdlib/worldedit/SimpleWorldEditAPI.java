package com.muhammaddaffa.mdlib.worldedit;

import com.muhammaddaffa.mdlib.MDLib;
import com.muhammaddaffa.mdlib.util.Executor;
import com.muhammaddaffa.mdlib.util.Logger;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SimpleWorldEditAPI {

    public static @Nullable Clipboard loadSchematic(File file) {
        if (!MDLib.hasWorldEdit()) {
            Logger.warning("WorldEdit is not installed, cannot load schematic!");
            return null;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return null;

        try (FileInputStream fis = new FileInputStream(file);
             ClipboardReader reader = format.getReader(fis)) {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveSchematic(Location loc1, Location loc2, File file) {
        if (!MDLib.hasWorldEdit()) return false;

        CuboidRegion region = new CuboidRegion(
                BukkitAdapter.adapt(loc1.getWorld()),
                BlockVector3.at(loc1.getX(), loc1.getY(), loc1.getZ()),
                BlockVector3.at(loc2.getX(), loc2.getY(), loc2.getZ())
        );
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        EditSessionBuilder.forWorld(loc1.getWorld()).execute(session -> {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(session, region, clipboard, region.getMinimumPoint());
            try {
                Operations.complete(forwardExtentCopy);

                ClipboardFormat format = ClipboardFormats.findByAlias("sponge");
                if (format == null) return;

                try (FileOutputStream fos = new FileOutputStream(file);
                     ClipboardWriter writer = format.getWriter(fos)) {
                    writer.write(clipboard);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return file.exists();
    }

    public static boolean pasteSchematic(Clipboard clipboard, Location location, double rotation,
                                         boolean flipX, boolean flipY, boolean flipZ, boolean ignoreAir) {
        if (clipboard == null || location == null || !MDLib.hasWorldEdit()) return false;

        EditSessionBuilder.forWorld(location.getWorld()).execute(session -> {
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            AffineTransform transform = new AffineTransform();

            if (rotation != 0) transform = transform.rotateY(rotation);
            if (flipX) transform = transform.scale(-1, 1, 1);
            if (flipY) transform = transform.scale(1, -1, 1);
            if (flipZ) transform = transform.scale(1, 1, -1);

            holder.setTransform(transform);

            Operation operation = holder.createPaste(session)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .ignoreAirBlocks(ignoreAir)
                    .build();
            try {
                Operations.complete(operation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    public static boolean pasteSchematic(Clipboard clipboard, Location location, double rotation, boolean ignoreAir) {
        return pasteSchematic(clipboard, location, rotation, false, false, false, ignoreAir);
    }

    public static boolean pasteSchematic(Clipboard clipboard, Location location, boolean ignoreAir) {
        return pasteSchematic(clipboard, location, 0, ignoreAir);
    }

    public static boolean pasteSchematic(File file, Location location, boolean ignoreAir) {
        Clipboard clipboard = loadSchematic(file);
        return pasteSchematic(clipboard, location, ignoreAir);
    }

    public static void fill(Location loc1, Location loc2, Material material) {
        new WERegion(loc1, loc2).fill(material);
    }

    public static void clear(Location loc1, Location loc2) {
        new WERegion(loc1, loc2).clear();
    }

    public static @Nullable WERegion getSelection(Player player) {
        if (!MDLib.hasWorldEdit()) return null;
        try {
            Region region = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player))
                    .getSelection(BukkitAdapter.adapt(player.getWorld()));
            return new WERegion(player.getWorld(), region);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setBiome(Location loc1, Location loc2, Biome biome) {
        new WERegion(loc1, loc2).setBiome(biome);
    }

    @Contract("_, _, _ -> new")
    public static @NonNull CompletableFuture<Boolean> pasteSchematicAsync(File file, Location location, boolean ignoreAir) {
        return CompletableFuture.supplyAsync(() -> pasteSchematic(file, location, ignoreAir), Executor.getExecutor());
    }

    @Contract("_, _, _ -> new")
    public static @NonNull CompletableFuture<Void> fillAsync(Location loc1, Location loc2, Material material) {
        return CompletableFuture.runAsync(() -> fill(loc1, loc2, material), Executor.getExecutor());
    }

    @Contract("_, _ -> new")
    public static @NonNull CompletableFuture<Void> clearAsync(Location loc1, Location loc2) {
        return CompletableFuture.runAsync(() -> clear(loc1, loc2), Executor.getExecutor());
    }
}
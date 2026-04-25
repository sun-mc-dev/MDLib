package com.muhammaddaffa.mdlib.worldedit;

import com.muhammaddaffa.mdlib.MDLib;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Collections;
import java.util.function.Consumer;

public class WERegion {

    private final World world;
    private final Region region;

    public WERegion(World world, Region region) {
        this.world = world;
        this.region = region;
    }

    public WERegion(Location loc1, Location loc2) {
        this.world = loc1.getWorld();
        this.region = new CuboidRegion(
                BukkitAdapter.adapt(world),
                BlockVector3.at(loc1.getX(), loc1.getY(), loc1.getZ()),
                BlockVector3.at(loc2.getX(), loc2.getY(), loc2.getZ())
        );
    }

    public static WERegion fromWorldGuard(World world, ProtectedRegion protectedRegion) {
        Region region = new CuboidRegion(
                BukkitAdapter.adapt(world),
                protectedRegion.getMinimumPoint(),
                protectedRegion.getMaximumPoint()
        );
        return new WERegion(world, region);
    }

    public void fill(Material material) {
        EditSessionBuilder.forWorld(world).execute(session -> {
            BlockState block = BukkitAdapter.adapt(material.createBlockData());
            try {
                session.setBlocks(region, block);
            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void clear() {
        fill(Material.AIR);
    }

    public void replace(Material from, Material to) {
        EditSessionBuilder.forWorld(world).execute(session -> {
            BlockState fromBlock = BukkitAdapter.adapt(from.createBlockData());
            BlockState toBlock = BukkitAdapter.adapt(to.createBlockData());
            try {
                session.replaceBlocks(region, Collections.singleton(fromBlock.toBaseBlock()), toBlock);
            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setBiome(Biome biome) {
        EditSessionBuilder.forWorld(world).execute(session -> {
            BiomeType biomeType = BukkitAdapter.adapt(biome);
            for (BlockVector3 vector : region) {
                session.setBiome(vector, biomeType);
            }
        });
    }

    public void forEachBlock(Consumer<BlockVector3> action) {
        for (BlockVector3 vector : region) {
            action.accept(vector);
        }
    }

    public int getBlockCount(Material material) {
        if (!MDLib.hasWorldEdit()) return 0;
        BlockState target = BukkitAdapter.adapt(material.createBlockData());
        int count = 0;
        for (BlockVector3 vector : region) {
            if (BukkitAdapter.adapt(world).getBlock(vector).equals(target)) {
                count++;
            }
        }
        return count;
    }

    public Region getHandle() {
        return region;
    }

    public World getWorld() {
        return world;
    }
}

package com.muhammaddaffa.mdlib;

import com.jeff_media.customblockdata.CustomBlockData;
import com.muhammaddaffa.mdlib.commands.CommandRegistry;
import com.muhammaddaffa.mdlib.hooks.VaultEconomy;
import com.muhammaddaffa.mdlib.papi.MDLibExpansion;
import com.muhammaddaffa.mdlib.scoreboard.ScoreboardManager;
import com.muhammaddaffa.mdlib.utils.Logger;
import com.muhammaddaffa.mdlib.worldguards.listeners.RegionListener;
import com.muhammaddaffa.mdlib.worldguards.listeners.entity.EntityRegionListener;
import com.muhammaddaffa.mdlib.worldguards.listeners.entity.EntityRemoveListenerV1_20_4;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public final class MDLib {

    private static JavaPlugin instance;

    public static boolean LISTEN_VAULT = true;
    public static boolean LISTEN_WORLDGUARD = false;
    public static boolean CUSTOM_BLOCK_DATA = false;

    private static boolean PLACEHOLDER_API, VAULT, WORLD_GUARD;

    private static CommandRegistry commands;
    private static ScoreboardManager scoreboardManager;

    public static void inject(JavaPlugin plugin) {
    }

    public static void onEnable(JavaPlugin plugin) {
        instance = plugin;
        commands = new CommandRegistry(plugin);
        scoreboardManager = new ScoreboardManager();
        PLACEHOLDER_API = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        VAULT = Bukkit.getPluginManager().getPlugin("Vault") != null;
        WORLD_GUARD = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        registerListeners();
    }

    public static void shutdown() {
        if (scoreboardManager != null) scoreboardManager.shutdown();
    }

    private static void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        if (WORLD_GUARD && LISTEN_WORLDGUARD) {
            pm.registerEvents(new RegionListener(), instance);
            Logger.info("WorldGuard hook from MDLib is enabled, registering it...");
            if (is1_20_4OrNewer()) {
                pm.registerEvents(new EntityRemoveListenerV1_20_4(), instance);
                Logger.info("Version is v1.20.4 or newer, registering EntityRemoveEvent listener!");
            }
            if (isPaper()) {
                pm.registerEvents(new EntityRegionListener(), instance);
                Logger.info("Using paper, an enhanced MDLib WorldGuard hook has been installed!");
            }
        }

        if (VAULT && LISTEN_VAULT) VaultEconomy.init();
        if (CUSTOM_BLOCK_DATA) CustomBlockData.registerListener(instance);

        pm.registerEvents(scoreboardManager, instance);
        FastInvManager.register(instance);
    }

    public static void registerExpansion(MDLibExpansion expansion) {
        if (!PLACEHOLDER_API) {
            Logger.warning("PlaceholderAPI is not installed, skipping expansion registration.");
            return;
        }
        expansion.register();
    }

    public static CommandRegistry getCommandRegistry() {
        return commands;
    }

    public static ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public static void registerWorldGuard() {
        LISTEN_WORLDGUARD = true;
    }

    public static void registerCustomBlockData() {
        CUSTOM_BLOCK_DATA = true;
    }

    public static boolean hasPlaceholderAPI() {
        return PLACEHOLDER_API;
    }

    public static boolean hasVault() {
        return VAULT;
    }

    public static boolean hasWorldGuard() {
        return WORLD_GUARD;
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static boolean is1_20_4OrNewer() {
        try {
            Class.forName("org.bukkit.event.entity.EntityRemoveEvent;");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }


    public static boolean isFolia() {
        String serverName = Bukkit.getServer().getName();
        if (serverName != null && serverName.equalsIgnoreCase("Folia")) return true;
        String version = Bukkit.getVersion();
        return version != null && version.toLowerCase(Locale.ROOT).contains("folia");
    }

    public static JavaPlugin instance() {
        return instance;
    }
}
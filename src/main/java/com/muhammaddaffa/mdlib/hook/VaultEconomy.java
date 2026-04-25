package com.muhammaddaffa.mdlib.hook;

import com.muhammaddaffa.mdlib.event.PlayerBalanceChangeEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy {

    private static Economy economy;

    public static void init() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;
        economy = rsp.getProvider();
    }

    public static double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public static EconomyResponse deposit(OfflinePlayer player, double amount) {
        double old = getBalance(player);
        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(player, old, old + amount, PlayerBalanceChangeEvent.ChangeType.DEPOSIT);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return new EconomyResponse(0, old, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        return economy.depositPlayer(player, event.getNewBalance() - old);
    }

    public static EconomyResponse withdraw(OfflinePlayer player, double amount) {
        double old = getBalance(player);
        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(player, old, old - amount, PlayerBalanceChangeEvent.ChangeType.WITHDRAW);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return new EconomyResponse(0, old, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        return economy.withdrawPlayer(player, old - event.getNewBalance());
    }

    public static void set(OfflinePlayer player, double amount) {
        double old = getBalance(player);
        PlayerBalanceChangeEvent event = new PlayerBalanceChangeEvent(player, old, amount, PlayerBalanceChangeEvent.ChangeType.SET);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        economy.withdrawPlayer(player, old);
        economy.depositPlayer(player, event.getNewBalance());
    }

    public static boolean isAvailable() {
        return economy != null;
    }
}
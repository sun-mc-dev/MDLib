package com.muhammaddaffa.mdlib.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerBalanceChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final OfflinePlayer player;
    private final double oldBalance;
    private final ChangeType changeType;
    private double newBalance;
    private boolean cancelled;
    public PlayerBalanceChangeEvent(OfflinePlayer player, double oldBalance,
                                    double newBalance, ChangeType changeType) {
        this.player = player;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.changeType = changeType;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public double getDifference() {
        return newBalance - oldBalance;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public enum ChangeType {DEPOSIT, WITHDRAW, SET}
}
package com.muhammaddaffa.mdlib.worldguards.events;

import com.muhammaddaffa.mdlib.worldguards.MovementWay;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class RegionEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ProtectedRegion region;
    private final LivingEntity entity;
    private final MovementWay movement;

    public RegionEvent(ProtectedRegion region, LivingEntity entity, MovementWay movement) {
        this.region = region;
        this.entity = entity;
        this.movement = movement;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public MovementWay getMovementWay() {
        return this.movement;
    }

}

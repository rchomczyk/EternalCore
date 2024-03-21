package com.eternalcode.core.feature.home.event;

import com.eternalcode.core.feature.home.Home;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Event called when player is teleported to home.
 */
public class HomeTeleportEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final UUID playerUniqueId;
    private final Home home;
    private boolean cancelled;

    public HomeTeleportEvent(UUID playerUniqueId, Home home) {
        super(false);

        this.playerUniqueId = playerUniqueId;
        this.home = home;
    }

    public Home getHome() {
        return home;
    }

    public UUID getPlayerUniqueId() {
        return this.playerUniqueId;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
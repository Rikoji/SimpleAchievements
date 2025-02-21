package de.hilsmann.simpleAchievements.events;

import de.hilsmann.simpleAchievements.models.Achievement;
import de.hilsmann.simpleAchievements.storage.PlayerAchievementStorage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GiveAchievementEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Achievement achievement;
    private boolean cancelled;

    public GiveAchievementEvent(Player player, Achievement achievement) {
        this.player = player;
        this.achievement = achievement;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}

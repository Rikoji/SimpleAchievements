package de.hilsmann.simpleAchievements.listeners;

import de.hilsmann.simpleAchievements.commands.AchievementCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {

    private final AchievementCommand achievementCommand;

    public ChatInputListener(AchievementCommand achievementCommand) {
        this.achievementCommand = achievementCommand;
    }

    @EventHandler
    public void onChatInput(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (achievementCommand.isCreatingAchievement(player)) {
            event.setCancelled(true);
            achievementCommand.handleChatInput(player, event.getMessage());
        }
    }
}

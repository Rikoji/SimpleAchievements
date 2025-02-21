package de.hilsmann.simpleAchievements.managers;

import de.hilsmann.simpleAchievements.events.GiveAchievementEvent;
import de.hilsmann.simpleAchievements.models.Achievement;
import de.hilsmann.simpleAchievements.storage.PlayerAchievementStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExplorationManager implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        for (Achievement achievement : AchievementManager.getAllAchievements().values()) {
            if (achievement.getType().equals(de.hilsmann.simpleAchievements.types.AchievementType.EXPLORATION)) {
                if (achievement.isInside(loc) && !PlayerAchievementStorage.hasAchievement(player.getUniqueId(), achievement.getId())) {
                    Bukkit.getPluginManager().callEvent(new GiveAchievementEvent(player, achievement));
                }
            }
        }
    }
}

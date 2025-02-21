package de.hilsmann.simpleAchievements.listeners;

import de.hilsmann.simpleAchievements.events.GiveAchievementEvent;
import de.hilsmann.simpleAchievements.storage.PlayerAchievementStorage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AchievementListener implements Listener {

    @EventHandler
    public void onAchievementUnlock(GiveAchievementEvent event) {
        Player player = event.getPlayer();
        if (PlayerAchievementStorage.hasAchievement(player.getUniqueId(), event.getAchievement().getId())) {
            return; // Erfolg bereits erhalten, nichts tun
        }

        // Erfolg speichern
        PlayerAchievementStorage.giveAchievement(player.getUniqueId(), event.getAchievement().getId());

        // 🎵 Sound-Effekt
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

        // 🏆 Titel-Anzeige
        player.sendTitle("§6§l🏆 §r§6Erfolg freigeschaltet!", "§a" + event.getAchievement().getName(), 10, 60, 10);

        // ✨ Actionbar-Nachricht
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacy("§e✔ Du hast den Erfolg §a" + event.getAchievement().getName() + " §eerhalten!"));


        // 💬 Nachricht im Chat
        player.sendMessage("§6[Erfolg] §aDu hast den Erfolg freigeschaltet: §e" + event.getAchievement().getName());

        // 🔔 Optional: Broadcast an alle Spieler
        Bukkit.broadcastMessage("§6[Erfolge] §e" + player.getName() + " §ahat den Erfolg §6" + event.getAchievement().getName() + " §aerhalten!");
    }
}

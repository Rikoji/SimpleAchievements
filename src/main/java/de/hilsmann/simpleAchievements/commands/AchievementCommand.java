package de.hilsmann.simpleAchievements.commands;

import de.hilsmann.simpleAchievements.gui.AchievementGUI;
import de.hilsmann.simpleAchievements.managers.AchievementManager;
import de.hilsmann.simpleAchievements.models.Achievement;
import de.hilsmann.simpleAchievements.types.AchievementType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AchievementCommand implements CommandExecutor {

    private final Map<UUID, AchievementCreationState> creationStates = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length < 1) {
            //player.sendMessage("§eVerwende: /achievements | /achievement create | /achievement list | /achievement remove <id>");
            AchievementGUI.openAchievements(player, 0);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                if (!player.hasPermission("achievements.admin")) {
                    player.sendMessage("§cDu hast keine Berechtigung, Erfolge zu erstellen.");
                    return true;
                }
                creationStates.put(uuid, new AchievementCreationState());
                player.sendMessage("§aErstellung gestartet! Gib jetzt den Namen des Erfolgs in den Chat ein.");
                return true;

            case "list":
                player.sendMessage("§eErfolge:");
                for (Achievement achievement : AchievementManager.getAllAchievements().values()) {
                    player.sendMessage("§7- " + achievement.getId() + " (§b" + achievement.getType() + "§7) in Welt: §6" + achievement.getWorldName());
                }
                return true;

            case "remove":
                if (!player.hasPermission("achievements.admin")) {
                    player.sendMessage("§cDu hast keine Berechtigung, Erfolge zu entfernen.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§cNutze: /achievement remove <id>");
                    return true;
                }
                String id = args[1];
                if (AchievementManager.getAchievementById(id) == null) {
                    player.sendMessage("§cErfolg nicht gefunden!");
                    return true;
                }
                AchievementManager.removeAchievement(id);
                player.sendMessage("§aErfolg " + id + " wurde entfernt!");
                return true;

            case "achievements":
                AchievementGUI.openAchievements(player, 0);
                return true;

            default:
                player.sendMessage("§cUnbekannter Befehl!");
                return true;
        }
    }

    public void handleChatInput(Player player, String message) {
        UUID uuid = player.getUniqueId();
        if (!creationStates.containsKey(uuid)) return;

        AchievementCreationState state = creationStates.get(uuid);

        if (state.getName() == null) {
            state.setName(message);
            player.sendMessage("§a✅ Name gesetzt! Gib jetzt die Beschreibung ein.");
            return;
        }

        if (state.getDescription() == null) {
            state.setDescription(message);
            player.sendMessage("§a✅ Beschreibung gesetzt! Wähle jetzt einen Typ: EXPLORATION, DEFAULT, COMBAT");
            return;
        }

        if (state.getType() == null) {
            try {
                state.setType(AchievementType.valueOf(message.toUpperCase()));
                player.sendMessage("§a✅ Typ gesetzt! Gehe jetzt zur ersten Position und gib 'pos1' in den Chat ein.");
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c❌ Ungültiger Typ! Wähle: EXPLORATION, DEFAULT, COMBAT");
            }
            return;
        }

        if (message.equalsIgnoreCase("pos1")) {
            setPosition(player, 1);
            player.sendMessage("§e📍 Position 1 gespeichert! Bewege dich zur zweiten Position und gib 'pos2' in den Chat ein.");
            return;
        }

        if (message.equalsIgnoreCase("pos2")) {
            setPosition(player, 2);
            player.sendMessage("§6✨ Erfolg wurde gespeichert! Gut gemacht.");
        }
    }


    public void setPosition(Player player, int posNumber) {
        UUID uuid = player.getUniqueId();
        if (!creationStates.containsKey(uuid)) return;

        AchievementCreationState state = creationStates.get(uuid);
        Location loc = player.getLocation();

        if (posNumber == 1) {
            state.setPos1(loc);
            state.setWorldName(loc.getWorld().getName()); // Welt setzen
            player.sendMessage("§aErste Position gespeichert in Welt: §6" + loc.getWorld().getName() + "§a! Gehe zur zweiten Position und nutze /achievement pos2");
        } else if (posNumber == 2) {
            if (!state.getWorldName().equals(loc.getWorld().getName())) {
                player.sendMessage("§cFehler: Beide Positionen müssen in der gleichen Welt sein!");
                return;
            }
            state.setPos2(loc);
            player.sendMessage("§aZweite Position gespeichert! Erfolg wird jetzt gespeichert...");
            completeAchievementCreation(player);
        }
    }

    private void completeAchievementCreation(Player player) {
        UUID uuid = player.getUniqueId();
        AchievementCreationState state = creationStates.remove(uuid);

        if (state == null || state.getPos1() == null || state.getPos2() == null || state.getWorldName() == null) {
            player.sendMessage("§cFehler! Bitte versuche es erneut.");
            return;
        }

        String id = state.getName().toLowerCase().replace(" ", "_");
        AchievementManager.registerAchievement(id, state.getName(), state.getDescription(), state.getType(), state.getPos1(), state.getPos2());

        player.sendMessage("§aErfolg wurde erstellt: " + state.getName() + " §ein der Welt §6" + state.getWorldName());
    }

    private static class AchievementCreationState {
        private String name;
        private String description;
        private AchievementType type;
        private Location pos1;
        private Location pos2;
        private String worldName;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public AchievementType getType() { return type; }
        public void setType(AchievementType type) { this.type = type; }

        public Location getPos1() { return pos1; }
        public void setPos1(Location pos1) { this.pos1 = pos1; }

        public Location getPos2() { return pos2; }
        public void setPos2(Location pos2) { this.pos2 = pos2; }

        public String getWorldName() { return worldName; }
        public void setWorldName(String worldName) { this.worldName = worldName; }
    }

    public boolean isCreatingAchievement(Player player) {
        return creationStates.containsKey(player.getUniqueId());
    }
}

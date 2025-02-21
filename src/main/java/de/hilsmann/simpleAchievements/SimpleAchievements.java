package de.hilsmann.simpleAchievements;

import de.hilsmann.simpleAchievements.commands.AchievementCommand;
import de.hilsmann.simpleAchievements.config.ConfigManager;
import de.hilsmann.simpleAchievements.gui.AchievementGUI;
import de.hilsmann.simpleAchievements.listeners.AchievementListener;
import de.hilsmann.simpleAchievements.listeners.ChatInputListener;
import de.hilsmann.simpleAchievements.managers.AchievementManager;
import de.hilsmann.simpleAchievements.managers.ExplorationManager;
import de.hilsmann.simpleAchievements.storage.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class SimpleAchievements extends JavaPlugin {

    private static SimpleAchievements instance;
    private AchievementCommand achievementCommand;

    @Override
    public void onEnable() {
        instance = this;

        // 📜 Lade die Konfiguration
        saveDefaultConfig();
        ConfigManager.loadConfig(this);

        // 🛢 Initialisiere die Datenbank
        DatabaseManager.initialize();

        // 🎮 Registriere Commands mit Instanzspeicherung
        achievementCommand = new AchievementCommand();
        getCommand("achievement").setExecutor(achievementCommand);
        getCommand("achievement").setAliases(Arrays.asList("erfolge", "erfolg", "ERFOLG", "ERFOLGE"));

        // 🎟 Registriere Event-Listener
        Bukkit.getPluginManager().registerEvents(new AchievementListener(), this);
        Bukkit.getPluginManager().registerEvents(new AchievementGUI(), this);
        Bukkit.getPluginManager().registerEvents(new ChatInputListener(achievementCommand), this);
        Bukkit.getPluginManager().registerEvents(new ExplorationManager(), this);

        // 📋 Lade gespeicherte Erfolge aus der Datenbank
        AchievementManager.loadAchievements();

        getLogger().info("✅ SimpleAchievements erfolgreich geladen!");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ SimpleAchievements deaktiviert.");
    }

    public static SimpleAchievements getInstance() {
        return instance;
    }
}

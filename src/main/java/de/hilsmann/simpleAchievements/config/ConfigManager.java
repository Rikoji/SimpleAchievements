package de.hilsmann.simpleAchievements.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private static File configFile;
    private static FileConfiguration config;

    public static void loadConfig(Plugin plugin) {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static boolean useMySQL() {
        return config.getBoolean("database.use-mysql");
    }

    public static String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }

    public static int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }

    public static String getMySQLDatabase() {
        return config.getString("database.mysql.database", "minecraft");
    }

    public static String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }

    public static String getMySQLPassword() {
        return config.getString("database.mysql.password", "password");
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}

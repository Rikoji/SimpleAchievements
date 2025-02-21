package de.hilsmann.simpleAchievements.storage;

import de.hilsmann.simpleAchievements.config.ConfigManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private static Connection connection;

    public static void initialize() {
        connect();
        setupTables();
    }

    private static void connect() {
        try {
            if (ConfigManager.useMySQL()) {
                String url = "jdbc:mysql://" + ConfigManager.getMySQLHost() + ":" + ConfigManager.getMySQLPort() +
                        "/" + ConfigManager.getMySQLDatabase() + "?autoReconnect=true&useSSL=false";
                connection = DriverManager.getConnection(url, ConfigManager.getMySQLUsername(), ConfigManager.getMySQLPassword());
            } else {
                connection = DriverManager.getConnection("jdbc:sqlite:plugins/SimpleAchievements/achievements.db");
            }
        } catch (SQLException e) {
            Logger.getLogger("SimpleAchievements").log(Level.SEVERE, "Datenbankverbindung fehlgeschlagen!", e);
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect(); // Verbindung neu aufbauen, falls sie geschlossen ist
            }
        } catch (SQLException e) {
            Logger.getLogger("SimpleAchievements").log(Level.SEVERE, "Fehler beim Überprüfen der Datenbankverbindung!", e);
        }
        return connection;
    }

    private static void setupTables() {
        String createAchievementsTable = "CREATE TABLE IF NOT EXISTS achievements ("
                + "id VARCHAR(255) PRIMARY KEY, "
                + "name TEXT NOT NULL, "
                + "description TEXT, "
                + "type TEXT NOT NULL, "
                + "world_name TEXT NOT NULL, "
                + "pos1_x DOUBLE, pos1_y DOUBLE, pos1_z DOUBLE, "
                + "pos2_x DOUBLE, pos2_y DOUBLE, pos2_z DOUBLE);";

        String createPlayerAchievementsTable = "CREATE TABLE IF NOT EXISTS player_achievements ("
                + "player_uuid VARCHAR(36) NOT NULL, "
                + "achievement_id VARCHAR(255) NOT NULL, "
                + "date_unlocked TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "PRIMARY KEY (player_uuid, achievement_id));";

        try (PreparedStatement stmt1 = getConnection().prepareStatement(createAchievementsTable);
             PreparedStatement stmt2 = getConnection().prepareStatement(createPlayerAchievementsTable)) {
            stmt1.execute();
            stmt2.execute();
        } catch (SQLException e) {
            Logger.getLogger("SimpleAchievements").log(Level.SEVERE, "Fehler beim Erstellen der Tabellen!", e);
        }
    }
}

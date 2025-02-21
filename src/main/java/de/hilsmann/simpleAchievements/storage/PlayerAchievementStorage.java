package de.hilsmann.simpleAchievements.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerAchievementStorage {

    public static boolean hasAchievement(UUID playerUUID, String achievementId) {
        String query = "SELECT 1 FROM player_achievements WHERE player_uuid = ? AND achievement_id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, achievementId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void giveAchievement(UUID playerUUID, String achievementId) {
        if (hasAchievement(playerUUID, achievementId)) return;

        String query = "INSERT INTO player_achievements (player_uuid, achievement_id) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, achievementId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

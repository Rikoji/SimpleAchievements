package de.hilsmann.simpleAchievements.managers;

import de.hilsmann.simpleAchievements.config.ConfigManager;
import de.hilsmann.simpleAchievements.models.Achievement;
import de.hilsmann.simpleAchievements.storage.DatabaseManager;
import de.hilsmann.simpleAchievements.types.AchievementType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AchievementManager {

    private static final Map<String, Achievement> achievements = new HashMap<>();

    public static void registerAchievement(String id, String name, String description, AchievementType type, Location pos1, Location pos2) {
        String worldName = pos1.getWorld().getName();
        achievements.put(id, new Achievement(id, name, description, type, pos1, pos2, worldName));

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql;

            if (ConfigManager.useMySQL()) {
                sql = "INSERT INTO achievements (id, name, description, type, world_name, pos1_x, pos1_y, pos1_z, pos2_x, pos2_y, pos2_z) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE name=?, description=?, type=?, world_name=?, pos1_x=?, pos1_y=?, pos1_z=?, pos2_x=?, pos2_y=?, pos2_z=?";
            } else {
                sql = "INSERT OR REPLACE INTO achievements (id, name, description, type, world_name, pos1_x, pos1_y, pos1_z, pos2_x, pos2_y, pos2_z) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                stmt.setString(2, name);
                stmt.setString(3, description);
                stmt.setString(4, type.name());
                stmt.setString(5, worldName);
                stmt.setDouble(6, pos1.getX());
                stmt.setDouble(7, pos1.getY());
                stmt.setDouble(8, pos1.getZ());
                stmt.setDouble(9, pos2.getX());
                stmt.setDouble(10, pos2.getY());
                stmt.setDouble(11, pos2.getZ());

                // Falls MySQL genutzt wird, setzen wir auch die Update-Werte
                if (ConfigManager.useMySQL()) {
                    stmt.setString(12, name);
                    stmt.setString(13, description);
                    stmt.setString(14, type.name());
                    stmt.setString(15, worldName);
                    stmt.setDouble(16, pos1.getX());
                    stmt.setDouble(17, pos1.getY());
                    stmt.setDouble(18, pos1.getZ());
                    stmt.setDouble(19, pos2.getX());
                    stmt.setDouble(20, pos2.getY());
                    stmt.setDouble(21, pos2.getZ());
                }

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadAchievements() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM achievements");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                AchievementType type = AchievementType.valueOf(rs.getString("type"));
                String worldName = rs.getString("world_name");

                double pos1_x = rs.getDouble("pos1_x");
                double pos1_y = rs.getDouble("pos1_y");
                double pos1_z = rs.getDouble("pos1_z");
                double pos2_x = rs.getDouble("pos2_x");
                double pos2_y = rs.getDouble("pos2_y");
                double pos2_z = rs.getDouble("pos2_z");

                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    Bukkit.getLogger().warning("⚠ Welt '" + worldName + "' existiert nicht! Achievement '" + name + "' wird übersprungen.");
                    continue;
                }

                Location pos1 = new Location(world, pos1_x, pos1_y, pos1_z);
                Location pos2 = new Location(world, pos2_x, pos2_y, pos2_z);

                achievements.put(id, new Achievement(id, name, description, type, pos1, pos2, worldName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Achievement getAchievementById(String id) {
        return achievements.get(id);
    }

    public static Map<String, Achievement> getAllAchievements() {
        return achievements;
    }

    public static void removeAchievement(String id) {
        achievements.remove(id);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM achievements WHERE id = ?")) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

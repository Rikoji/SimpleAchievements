package de.hilsmann.simpleAchievements.models;

import de.hilsmann.simpleAchievements.types.AchievementType;
import org.bukkit.Location;

public class Achievement {

    private final String id;
    private final String name;
    private final String description;
    private final AchievementType type;
    private final Location pos1;
    private final Location pos2;
    private final String worldName;

    public Achievement(String id, String name, String description, AchievementType type, Location pos1, Location pos2, String worldName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.worldName = worldName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public AchievementType getType() {
        return type;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isInside(Location location) {
        if (pos1 == null || pos2 == null || location == null) {
            return false;
        }
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
                location.getY() >= minY && location.getY() <= maxY &&
                location.getZ() >= minZ && location.getZ() <= maxZ;
    }
}

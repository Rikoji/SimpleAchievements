package de.hilsmann.simpleAchievements.gui;

import de.hilsmann.simpleAchievements.managers.AchievementManager;
import de.hilsmann.simpleAchievements.models.Achievement;
import de.hilsmann.simpleAchievements.storage.PlayerAchievementStorage;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AchievementGUI implements Listener {

    private static final int[] ACHIEVEMENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private static final int BACK_BUTTON_SLOT = 45;
    private static final int NEXT_BUTTON_SLOT = 53;
    private static final int INFO_SLOT = 4;

    private static final ItemStack GREEN_GLASS = createGlassPane(Material.GREEN_STAINED_GLASS_PANE, " ");
    private static final ItemStack GRAY_GLASS = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");

    private static final HeadDatabaseAPI headDatabase = new HeadDatabaseAPI();

    public static void openAchievements(Player player, int page) {
        Inventory gui = Bukkit.createInventory(player, 54, "§aDeine Erfolge");

        // Dekoration: Grüne Glasscheiben (blaue Slots)
        for (int i = 0; i < 9; i++) gui.setItem(i, GREEN_GLASS);
        for (int i = 45; i < 54; i++) gui.setItem(i, GREEN_GLASS);

        // Dekoration: Dunkelgraue Glasscheiben (graue Slots)
        gui.setItem(45, GRAY_GLASS);
        gui.setItem(46, GRAY_GLASS);
        gui.setItem(47, GRAY_GLASS);
        gui.setItem(48, GRAY_GLASS);
        gui.setItem(49, GRAY_GLASS);
        gui.setItem(50, GRAY_GLASS);
        gui.setItem(51, GRAY_GLASS);
        gui.setItem(52, GRAY_GLASS);
        gui.setItem(53, GRAY_GLASS);

        // Erfolge einfügen
        List<Achievement> achievements = new ArrayList<>(AchievementManager.getAllAchievements().values());
        int startIndex = page * ACHIEVEMENT_SLOTS.length;
        for (int i = 0; i < ACHIEVEMENT_SLOTS.length && (startIndex + i) < achievements.size(); i++) {
            Achievement achievement = achievements.get(startIndex + i);
            gui.setItem(ACHIEVEMENT_SLOTS[i], createAchievementItem(player, achievement));
        }

        // Seitenbuttons hinzufügen
        if (page > 0) gui.setItem(BACK_BUTTON_SLOT, getHead("8902", "§cVorherige Seite"));
        if ((startIndex + ACHIEVEMENT_SLOTS.length) < achievements.size()) gui.setItem(NEXT_BUTTON_SLOT, getHead("8899", "§aNächste Seite"));

        // Info-Item setzen
        gui.setItem(INFO_SLOT, getHead("38870", "§eErfolge-Übersicht", "§7Hier kannst du alle deine Erfolge sehen."));

        player.openInventory(gui);
    }

    private static ItemStack createAchievementItem(Player player, Achievement achievement) {
        boolean unlocked = PlayerAchievementStorage.hasAchievement(player.getUniqueId(), achievement.getId());
        Material material = unlocked ? Material.DIAMOND : Material.COAL;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName((unlocked ? "§a✔ " : "§c✖ ") + achievement.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7" + achievement.getDescription());
            lore.add(unlocked ? "§a✔ Bereits freigeschaltet" : "§c✖ Noch nicht freigeschaltet");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack getHead(String id, String name, String... lore) {
        ItemStack item = headDatabase.getItemHead(id);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String line : lore) loreList.add(line);
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createGlassPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("§aDeine Erfolge")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem.isSimilar(getHead("8902", "§cVorherige Seite"))) {
                openAchievements(player, getCurrentPage(player) - 1);
            } else if (clickedItem.isSimilar(getHead("8899", "§aNächste Seite"))) {
                openAchievements(player, getCurrentPage(player) + 1);
            }
        }
    }

    private int getCurrentPage(Player player) {
        String title = player.getOpenInventory().getTitle();
        if (title.contains("Seite ")) {
            try {
                return Integer.parseInt(title.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}

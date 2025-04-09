package org.ley.menu.templates.simple;

import com.sun.source.tree.BreakTree;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.ley.menu.MenuBrowser;

import java.util.HashMap;

@Data
public abstract class SimpleMenu implements Listener {

    private final String url;
    private Inventory inventory;
    private String title;
    private HashMap<String, String> args = new HashMap<>();

    public SimpleMenu(String url, int size) {
        this.url = url;
        this.inventory = Bukkit.createInventory(null, size, url);
        MenuBrowser.registerMenu(this);
    }

    public SimpleMenu(String url, InventoryType type) {
        this.url = url;
        this.inventory = Bukkit.createInventory(null, type, url);
        MenuBrowser.registerMenu(this);
    }

    public void open(Player player, HashMap<String, String> args) {
        this.inventory.clear();
        this.args = args;
        if (checkPlayerPlaytime(player)) {
            onMenuOpenTrigger(player);
        } else {
            Bukkit.getScheduler().runTaskLater(MenuBrowser.plugin, () -> onMenuOpenTrigger(player), 7);
        }

        MenuBrowser.registerOpenMenu(player, MenuBrowser.buildURL(url, args));
    }

    @EventHandler
    public void onMenuClickTrigger(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player clicker)) return;
        if (!MenuBrowser.getUrl(MenuBrowser.getPlayerMenu(clicker)).equalsIgnoreCase(url)) return;

        Inventory inventory = event.getInventory();
        HashMap<String,String> args = new HashMap<>();

        onMenuClick(clicker, event, inventory, args);
    }

    public void onMenuOpenTrigger(Player player) {
        if (!MenuBrowser.getUrl(MenuBrowser.getPlayerMenu(player)).equalsIgnoreCase(url)) return;

        args = MenuBrowser.getArgs(MenuBrowser.getPlayerMenu(player));
        player.openInventory(onMenuOpen(player, inventory, args));
    }

    public abstract void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args);
    public abstract Inventory onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args);

    private boolean checkPlayerPlaytime(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastJoinTime = player.getLastPlayed();
        long timeSinceJoinTicks = ((currentTime - lastJoinTime) * 20) / 1000;
        return timeSinceJoinTicks > 27;
    }
}

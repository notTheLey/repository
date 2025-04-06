package org.ley.menu.templates.simple;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.ley.menu.MenuBrowser;

import java.util.HashMap;

public abstract class CancelledMenu extends SimpleMenu {
    public CancelledMenu(String url, int size) {
        super(url, size);
    }

    public CancelledMenu(String url, InventoryType type) {
        super(url, type);
    }

    @Override
    public void onMenuClickTrigger(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player clicker)) return;
        if (!MenuBrowser.getUrl(MenuBrowser.getPlayerMenu(clicker)).equalsIgnoreCase(super.getUrl())) return;

        Inventory inventory = event.getInventory();
        HashMap<String,String> args = new HashMap<>();

        event.setCancelled(true);
        onMenuClick(clicker, event, inventory, args);
    }
}

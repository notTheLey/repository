package org.ley.menu.templates.simple;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.ley.menu.MenuBrowser;

import java.util.HashMap;
import java.util.List;

public abstract class PermissionMenu extends SimpleMenu {
    private final List<String> permissions;

    public PermissionMenu(String url, int size, String permission, List<String> permissions) {
        super(url, size);
        this.permissions = List.of(permission);
    }

    public PermissionMenu(String url, InventoryType type, String permission) {
        super(url, type);
        this.permissions = List.of(permission);
    }

    public PermissionMenu(String url, int size, List<String> permissions) {
        super(url, size);
        this.permissions = permissions;
    }

    public PermissionMenu(String url, InventoryType type, List<String> permissions) {
        super(url, type);
        this.permissions = permissions;
    }

    @Override
    public void onMenuClickTrigger(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player clicker)) return;
        if (!MenuBrowser.getUrl(MenuBrowser.getPlayerMenu(clicker)).equalsIgnoreCase(super.getUrl())) return;

        Inventory inventory = event.getInventory();
        HashMap<String,String> args = new HashMap<>();
        boolean isPermitted = isPermitted(clicker);

        onMenuClick(clicker, event, inventory, args, isPermitted);

        event.setCancelled(!isPermitted);
    }

    @Override
    public void onMenuOpenTrigger(Player player) {
        if (!MenuBrowser.getUrl(MenuBrowser.getPlayerMenu(player)).equalsIgnoreCase(super.getUrl())) return;

        HashMap<String, String> args = MenuBrowser.getArgs(MenuBrowser.getPlayerMenu(player));

        player.openInventory(onMenuOpen(player, super.getInventory(), args, !isPermitted(player)));
    }

    public boolean isPermitted(Player player) {
        for(String permission : permissions) {
            if (player.hasPermission(permission)) return true;
        }return false;
    }

    @Override
    public abstract void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args);
    public abstract void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args, boolean isPermitted);

    @Override
    public abstract Inventory onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args);
    public abstract Inventory onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args, boolean isPermitted);

}

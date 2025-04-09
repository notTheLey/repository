package org.ley.menu.templates.advanced;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.ley.menu.MenuBrowser;
import org.ley.menu.templates.simple.SimpleMenu;
import org.ley.menu.types.MenuComponent;

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

        event.setCancelled(!isPermitted);
        onMenuClick(clicker, event, inventory, args, isPermitted);
    }

    @Override
    public MenuComponent onMenuOpenTrigger(Player player) {
        if (!MenuBrowser.getUrl(MenuBrowser.getPlayerMenu(player)).equalsIgnoreCase(super.getUrl())) return null;

        HashMap<String, String> args = MenuBrowser.getArgs(MenuBrowser.getPlayerMenu(player));
        MenuComponent menu = onMenuOpen(player, super.getInventory(), args, !isPermitted(player));

        player.openInventory(menu.getInv());
        player.getOpenInventory().setTitle(menu.getDisplayTitle());

        return menu;
    }

    public boolean isPermitted(Player player) {
        for(String permission : permissions) {
            if (player.hasPermission(permission)) return true;
        }return false;
    }

    @Override
    public void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args) {
        onMenuClick(clicker, event, inventory, args, isPermitted(clicker));}

    @Override
    public MenuComponent onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args) {
        return onMenuOpen(player, inventory, args, isPermitted(player));}

    public abstract void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args, boolean isPermitted);
    public abstract MenuComponent onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args, boolean isPermitted);

}

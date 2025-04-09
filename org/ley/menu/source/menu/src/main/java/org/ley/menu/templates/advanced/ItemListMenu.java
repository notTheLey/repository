package org.ley.menu.templates.advanced;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ley.menu.templates.simple.SimpleMenu;
import org.ley.menu.types.ListMenuComponent;
import org.ley.menu.types.MenuComponent;

import java.util.HashMap;
import java.util.List;

public abstract class ItemListMenu extends SimpleMenu {

    public ItemListMenu(String url, int size) {
        super(url, size);
    }

    @Override
    public void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args) {
        onMenuClick(clicker, event, inventory, args, args.getOrDefault("search", "none"));}

    @Override
    public MenuComponent onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args) {

        String search = args.getOrDefault("search", "none");
        
        ListMenuComponent menu = onMenuOpen(player, inventory, args, search);
        Inventory modInventory = menu.getInv();

        List<ItemStack> items = menu.getItems();
        List<Integer> slots = menu.getSlots();

        int page = Math.max(0, Integer.parseInt(args.getOrDefault("page", "0").replaceAll("[^0-9-]", "0")));
        int ii = 0;

        for (ItemStack item : items) {
            if (items.indexOf(item) > (slots.size() * page)) {
                inventory.setItem(slots.get(ii), item);
                ii++;
            }
        }

        return new ListMenuComponent(menu.getInvHolder(), menu.getDisplayTitle(), modInventory, items, slots);
    }

    public abstract void onMenuClick(Player clicker, InventoryClickEvent event, Inventory inventory, HashMap<String, String> args, String s);
    public abstract ListMenuComponent onMenuOpen(Player player, Inventory inventory, HashMap<String, String> args,  String s);
}

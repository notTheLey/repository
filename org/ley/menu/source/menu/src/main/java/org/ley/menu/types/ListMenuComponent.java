package org.ley.menu.types;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ListMenuComponent extends MenuComponent{
    private final List<ItemStack> items;
    private final List<Integer> slots;

    public ListMenuComponent(String title, Inventory inventory, List<ItemStack> items, List<Integer> slots) {
        super(title, inventory);
        this.items = items;
        this.slots = slots;
    }

    public ListMenuComponent(InventoryHolder holder, String title, Inventory inventory, List<ItemStack> items, List<Integer> slots) {
        super(holder, title, inventory);
        this.items = items;
        this.slots = slots;
    }

    public List<ItemStack> getItems() {return items;}
    public List<Integer> getSlots() {return slots;}
}

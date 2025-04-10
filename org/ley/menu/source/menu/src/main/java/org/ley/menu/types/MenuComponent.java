package org.ley.menu.types;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuComponent {
    private final String title;
    private final Inventory inventory;
    private final InventoryHolder holder;

    public MenuComponent(String title, Inventory inventory) {
        this.holder = null;
        this.title = title;
        this.inventory = inventory;
    }

    public MenuComponent(InventoryHolder holder, String title, Inventory inventory) {
        this.holder = holder;
        this.title = title;
        this.inventory = inventory;
    }

    public String getDisplayTitle() {return title;}
    public Inventory getInv() {return inventory;}
    public InventoryHolder getInvHolder() {return holder;}


}

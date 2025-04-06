package org.ley.menu.templates.enhanced;

import org.bukkit.event.inventory.InventoryType;
import org.ley.menu.templates.simple.SimpleMenu;

public abstract class EnhancedMenu extends SimpleMenu {
    public EnhancedMenu(String url, int size) {
        super(url, size);
    }

    public EnhancedMenu(String url, InventoryType type) {
        super(url, type);
    }

    public void onMenuCloseTrigger(){}
}

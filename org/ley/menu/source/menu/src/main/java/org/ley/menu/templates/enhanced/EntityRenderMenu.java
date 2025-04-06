package org.ley.menu.templates.enhanced;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ley.menu.MenuBrowser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EntityRenderMenu extends EnhancedMenu {
    private Map<String, UUID> entityData = new HashMap<>();

    public EntityRenderMenu(String url, int size) {
        super(url, size);
    }

    public EntityRenderMenu(String url, InventoryType type) {
        super(url, type);
    }

    @Override
    public void onMenuOpenTrigger(Player player) {
        if (!MenuBrowser.getUrl(MenuBrowser.getPlayerMenu(player)).equalsIgnoreCase(super.getUrl())) {
            return;
        }

        HashMap<String, String> args = MenuBrowser.getArgs(MenuBrowser.getPlayerMenu(player));
        Location location = player.getLocation();
        World world = player.getWorld();

         // Spawn the mule entity with custom properties
        Mule mule = (Mule) world.spawnEntity(location.clone().add(0, 0.3, 0), EntityType.MULE,
                CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                    entity.setCustomNameVisible(false);
                    entity.setVisibleByDefault(true);
                    entity.setGravity(false);
                    entity.setInvulnerable(true);
                    entity.setVisualFire(false);
                    entity.addPassenger(player);

                    if (entity instanceof Mule m) {
                        m.setCarryingChest(true);
                        m.setTamed(true);
                        m.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(0.25);
                    }

                    if (entity instanceof LivingEntity l) {
                        l.setAI(false);
                        l.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 1, false, true));
                    }

                    for (Player other : Bukkit.getOnlinePlayers()) {
                        if (!other.getUniqueId().equals(player.getUniqueId())) {
                            other.hideEntity(MenuBrowser.plugin, entity);
                        }
                    }
                });

        Inventory menuInventory = onMenuOpen(player, mule.getInventory(), args);
        mule.getInventory().setContents(menuInventory.getContents());
        player.openInventory(mule.getInventory());
    }

    @Override
    public void onMenuCloseTrigger(){
        for (Map.Entry<String, UUID> entry : entityData.entrySet()) {
            Entity entity = Bukkit.getEntity(entry.getValue());
            if (entity != null) {
                entity.remove();
            }
        }
    }
}

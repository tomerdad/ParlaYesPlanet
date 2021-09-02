package me.tomerdad.parlayesplanet.listners;

import me.tomerdad.parlayesplanet.ParlaYesPlanet;
import me.tomerdad.parlayesplanet.commands.RingCommand;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class DespawnItems implements Listener {

    private static ParlaYesPlanet plugin;

    public DespawnItems(ParlaYesPlanet plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void OnItemDespawn(ItemDespawnEvent event) {

        if (!RingCommand.enable) {
            return;
        }

        ItemStack item = event.getEntity().getItemStack();
        Material itemType = item.getType();
        if (itemType.toString().equalsIgnoreCase(RingCommand.material.toString())) {
            int RingNum;
            //check name
            try {
                List<String> lore = Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getLore());
                if (lore.get(0) == null) {
                    return;
                }
                RingNum = Integer.parseInt(lore.get(0));
            } catch (Exception ignored) {
                return;
            }

            if (RingCommand.items.containsKey(RingNum)) {
                event.setCancelled(true);
            }
        }
    }
}

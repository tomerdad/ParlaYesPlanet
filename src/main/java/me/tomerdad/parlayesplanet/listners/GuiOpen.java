package me.tomerdad.parlayesplanet.listners;

import me.tomerdad.parlayesplanet.ParlaYesPlanet;
import me.tomerdad.parlayesplanet.commands.RingCommand;
import me.tomerdad.parlayesplanet.utilities.SqlConfig;
import me.tomerdad.parlayesplanet.utilities.Utilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class GuiOpen implements Listener {

    public static String title;

    @EventHandler
    public void onClick(InventoryClickEvent e){
//        if (Utilities.gui.equals(e.getClickedInventory())) {
        if (title == null){
            return;
        }

//        if (Utilities.gui.equals(e.getClickedInventory())) {
        if (e.getView().getTitle().equalsIgnoreCase(title)) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            player.updateInventory();
            if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;


            if (e.getCurrentItem().getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }

            Material material = RingCommand.material;
            boolean teleportPerm = player.hasPermission("ParlaYesPlanet.teleport");

            if (e.getCurrentItem().getType().toString().equalsIgnoreCase(material.toString())) {

                try {
                    int RingNum = Integer.parseInt(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().split(" ")[1]);

                    if (teleportPerm) {
                        RingCommand.items.get(RingNum).setWorld(player.getWorld());
                        player.closeInventory();
                        player.teleport(RingCommand.items.get(RingNum));
                    }
                } catch (Exception exception) {
                    ParlaYesPlanet.getPlugin().getLogger().info("error");
                    exception.printStackTrace();
                    return;
                }
            }
        }

    }
}

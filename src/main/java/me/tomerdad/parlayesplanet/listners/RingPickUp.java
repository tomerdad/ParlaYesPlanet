package me.tomerdad.parlayesplanet.listners;

import me.tomerdad.parlayesplanet.ParlaYesPlanet;
import me.tomerdad.parlayesplanet.commands.RingCommand;
import me.tomerdad.parlayesplanet.utilities.SqlConfig;
import me.tomerdad.parlayesplanet.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class RingPickUp implements Listener {

    private static ParlaYesPlanet plugin;

    public RingPickUp(ParlaYesPlanet plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void OnPlayerPickup(PlayerPickupItemEvent event)
    {
        if (!RingCommand.enable) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        Material itemType = item.getType();
        Material material = RingCommand.material;
        if (itemType == material)
        {
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
//            if (RingCommand.items.containsKey(RingNum)) {

            if (RingCommand.items.containsKey(RingNum)) {
                int RingsCount = RingCommand.items.size();
                String sql = "SELECT COUNT(*) FROM ringsFound WHERE `player` = '" + player.getUniqueId().toString() + "' AND `ring` = (" + RingNum + ") ;";

                event.setCancelled(true);

                if (SqlConfig.checkIfNull(sql)) {
                    sql = "INSERT INTO ringsFound (`player`, `ring`) VALUES ('" + player.getUniqueId().toString() + "', (" + RingNum + "));";
                    SqlConfig.updateQuery(sql);
                    String message = plugin.getConfig().getString("message");
                    if (message != null) {
                        int RingsFoundCount = SqlConfig.getCount("SELECT COUNT(*) FROM ringsFound WHERE `player` = '" + player.getUniqueId().toString() + "';");
                        Utilities.creatHologam(player, RingNum);
                        ConsoleCommandSender console = plugin.getServer().getConsoleSender();

                        if (RingsCount <=  RingsFoundCount) {
                            String msg = plugin.getConfig().getString("all-found-message");
                            if (msg != null) {
                                msg = msg.replace("%player%", player.getDisplayName()).replace("%ring%", String.valueOf(RingNum)).replace("&", "§").replace("%ringscount%", String.valueOf(RingsCount)).replace("%ringsfound%", String.valueOf(RingsFoundCount));
                                Bukkit.broadcastMessage(msg);
                            }

                            String command = plugin.getConfig().getString("general-command");
                            if (command != null) {
                                command = command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
                                Bukkit.dispatchCommand(console, command);
                            }
                        } else {
                            message = message.replace("%player%", player.getDisplayName()).replace("%ring%", String.valueOf(RingNum)).replace("&", "§").replace("%ringscount%", String.valueOf(RingsCount)).replace("%ringsfound%", String.valueOf(RingsFoundCount));
                            player.sendMessage(message);
//                            String command = plugin.getConfig().getString("single-command");
//                            if (command != null ) {
//                                command = command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
//                                Bukkit.dispatchCommand(console, command);
//                            }
                        }
                    }
                }
            }
        }
    }
}

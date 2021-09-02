package me.tomerdad.parlayesplanet.commands;

import me.tomerdad.parlayesplanet.ParlaYesPlanet;
import me.tomerdad.parlayesplanet.listners.GuiOpen;
import me.tomerdad.parlayesplanet.utilities.SqlConfig;
import me.tomerdad.parlayesplanet.utilities.Utilities;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.*;

public class RingCommand implements CommandExecutor {

    public static Map<Integer, Location> items = new HashMap<Integer, Location>();
    public static Boolean enable = false;
    public static Material material;
    private static ParlaYesPlanet plugin;

    public RingCommand(ParlaYesPlanet plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                if (!player.hasPermission("ParlaYesPlanet.GUI")) {
                    player.sendMessage("No permission");
                    return true;
                }
                if (null == RingCommand.material) {
                    player.sendMessage("no item set or wrong item");
                    return true;
                }

                if (items.size()==0) {
                    player.sendMessage("you need to add rings with /popcorn add");
                    return true;
                }
                Utilities.createGUI(player);
                player.openInventory(Utilities.gui);

            } else if (args[0].equalsIgnoreCase("reload")) {
                //reloads the config and database

                if (!player.hasPermission("ParlaYesPlanet.reload")) {
                    player.sendMessage("No permission");
                    return true;
                }
                Utilities.removeHolograms();
                Utilities.save();
                Utilities.removeItems(player.getWorld().getEntities());
                items = new HashMap<Integer, Location>();
                plugin.reloadConfig();
                SqlConfig.getSQLConnection();
                SqlConfig.load();
                GuiOpen.title = ParlaYesPlanet.getPlugin().getConfig().getString("gui-title");
                try {
                    material = Material.matchMaterial(Objects.requireNonNull(ParlaYesPlanet.getPlugin().getConfig().getString("item")));
                } catch (Exception e) {
                    material = Material.AIR;
                    player.sendMessage("no item set or wrong item");
                    return true;
                }
                Utilities.load();
                if (enable) {
                    Utilities.createItems(player);
                    for (Player player1 : player.getServer().getOnlinePlayers()){
                        Utilities.creatHologam(player1, -1);
                    }
                }

                player.sendMessage("§aReload Completed");
            } else if (args[0].equalsIgnoreCase("add")) {

                if (!player.hasPermission("ParlaYesPlanet.add")) {
                    player.sendMessage("No permission");
                    return true;
                }

                if (null == RingCommand.material) {
                    player.sendMessage("no item set or wrong item");
                    return true;
                }

                //add items
                int itemsCount = items.size();
                if (itemsCount >= 18) {
                    player.sendMessage("max rings is 18");
                    return true;
                }
                if (enable) {
                    player.sendMessage("enable so please disable with /popcorn stop");
                    return true;
                }
                items.put((itemsCount + 1), player.getLocation());
                Utilities.save();
                player.sendMessage("ring " + (itemsCount + 1) + " added");


            } else if (args[0].equalsIgnoreCase("change")) {

                if (!player.hasPermission("ParlaYesPlanet.change")) {
                    player.sendMessage("No permission");
                    return true;
                }

                try {
                    if (args[1] == null) {
                        player.sendMessage("Needs the ring number");
                        return true;
                    }
                } catch (Exception e) {
                    player.sendMessage("Needs the ring number");
                    return true;
                }

                if (null == RingCommand.material) {
                    player.sendMessage("no item set or wrong item");
                    return true;
                }

                if (enable) {
                    player.sendMessage("enable so please disable with /popcorn stop");
                    return true;
                }
                int ring;
                try {
                    ring = Integer.parseInt(args[1]);
                } catch (Exception e ) {
                    player.sendMessage("needs to be number");
                    return true;
                }

                if (!items.containsKey(ring)) {
                    player.sendMessage("the ring not exits");
                    return true;
                }
                items.remove(ring);


                SqlConfig.updateQuery("UPDATE rings SET (`world`, `LocX`, `LocY`, `LocZ`) VALUES ((" + Objects.requireNonNull(player.getLocation().getWorld()).getName() + "), (" + String.valueOf(player.getLocation().getX()) + "), (" + String.valueOf(player.getLocation().getY())+ "), (" + String.valueOf(player.getLocation().getZ()) + ") WHERE `ring` = (\" + ring + \");");
                items.put(ring, player.getLocation());
                Utilities.save();
                player.sendMessage("ring "+ ring +" changed");

            } else if (args[0].equalsIgnoreCase("unfind")) {

                if (!player.hasPermission("ParlaYesPlanet.unfind")) {
                    player.sendMessage("No permission");
                    return true;
                }

                try {
                    if (args[1] == null) {
                        player.sendMessage("Needs the player name");
                        return true;
                    }
                } catch (Exception e) {
                    player.sendMessage("Needs the player name");
                    return true;
                }

                Player toPlayer;
                try {
                    toPlayer = player.getServer().getPlayer(args[1]);
                    if (toPlayer == null) {

                        player.sendMessage("the player not found");
                        return true;
                    }
                } catch (Exception e) {
                    player.sendMessage("the player not found");
                    return true;
                }

                try {
                    if (args[2] == null) {
                        player.sendMessage("Needs the ring number");
                        return true;
                    }
                } catch (Exception e) {
                    player.sendMessage("Needs the ring number");
                    return true;
                }

                if (null == RingCommand.material) {
                    player.sendMessage("no item set or wrong item");
                    return true;
                }

                if (args[2].equalsIgnoreCase("all")) {
                    SqlConfig.updateQuery("DELETE FROM ringsFound WHERE `player` = '" + toPlayer.getUniqueId().toString() + "';");
                    player.sendMessage("removed all rings to " + toPlayer.getDisplayName());
                } else {
                    try {
                        int ringNum = Integer.parseInt(args[2]);
                        SqlConfig.updateQuery("DELETE FROM ringsFound WHERE `player` = '" + toPlayer.getUniqueId().toString() + "' AND ring = (" + ringNum + ");");
                        player.sendMessage("removed ring " + ringNum + " to " + toPlayer.getDisplayName());
                    } catch (Exception e) {
                        player.sendMessage("please Enter a ring number");
                    }
                }
                if (enable) {
                    Utilities.removeHologramsTo(player);
                    Utilities.creatHologam(player, -1);
                }




            } else if (args[0].equalsIgnoreCase("start")) {

                //create and spawn items

                if (!player.hasPermission("ParlaYesPlanet.start")) {
                    player.sendMessage("No permission");
                    return true;
                }

                if (null == RingCommand.material) {
                    player.sendMessage("no item set or wrong item");
                    return true;
                }

                if (enable) {
                    player.sendMessage("already enable");
                    return true;
                }
                Utilities.removeItems(player.getWorld().getEntities());
                Utilities.createItems(player);
                for (Player player1 : plugin.getServer().getOnlinePlayers()){
                    Utilities.creatHologam(player1, -1);
                }

                enable = true;
                Utilities.setMode();
                player.sendMessage("start the event");


            } else if (args[0].equalsIgnoreCase("stop")) {
                //cancel despawn and need to remove items.

                if (!player.hasPermission("ParlaYesPlanet.stop")) {
                    player.sendMessage("No permission");
                    return true;
                }

                if (null == RingCommand.material) {
                    player.sendMessage("no item set or wrong item");
                    return true;
                }

                if (enable) {
                    enable = false;
                    List<Entity> entList = player.getWorld().getEntities();//get all entities in the world
                    Utilities.removeItems(entList);
                    Utilities.setMode();
                    Utilities.removeHolograms();
                    player.sendMessage("Items removed and stop");
                    return true;
                } else {
                    player.sendMessage("the rings weren't enable");
                }


            } else if (args[0].equalsIgnoreCase("save")) {

                if (!player.hasPermission("ParlaYesPlanet.save")) {
                    player.sendMessage("No permission");
                    return true;
                }

                Utilities.save();
                Utilities.setMode();
                player.sendMessage("Rings Saved");

            } else {
            //help

            String helpMsg = "[HELP]\n"+
                "/popcorn - open gui\n"+
                "/popcorn stop\n"+
                "/popcorn unfind <player> <ring number> \n"+
                "/popcorn change <number> - change the item Location\n"+
                "/popcorn add - adds the item Location\n"+
                "/popcorn start - spawn items\n"+
                "/popcorn reload - reloads Config\n";
            player.sendMessage(helpMsg);
            return true;

            }
        }
        return true;
    }
}

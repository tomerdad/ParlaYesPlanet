package me.tomerdad.parlayesplanet.utilities;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.tomerdad.parlayesplanet.ParlaYesPlanet;
import me.tomerdad.parlayesplanet.commands.RingCommand;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.util.*;
import java.util.List;

public class Utilities {

    public static Inventory gui;

    //Creates the GUI function
    public static void createGUI(Player player) {
        String Titile = Objects.requireNonNull(ParlaYesPlanet.getPlugin().getConfig().getString("gui-title")).replace("&", "§");

        Material material = RingCommand.material;

        gui = Bukkit.createInventory(null, 9*3, Titile);

        String itemName = ParlaYesPlanet.getPlugin().getConfig().getString("item-name");
        if (itemName == null) {
            itemName = "popcorn";
        }

        for (var ring : RingCommand.items.entrySet()) {
            ChatColor color = ChatColor.RED;
            boolean enchant = false;
            if(!SqlConfig.checkIfNull("SELECT COUNT(*) FROM ringsFound WHERE `player` = '" + player.getUniqueId().toString() + "' AND `ring` = (" + ring.getKey() + ");")) {
                color = ChatColor.GREEN;
                enchant = true;
            }

            List<String> lore;
            try {
                lore = ParlaYesPlanet.getPlugin().getConfig().getStringList("lore." + ring.getKey());
            } catch (Exception e) {
                lore = null;
            }
            Integer loc = 0;
            try {
                loc = ParlaYesPlanet.getPlugin().getConfig().getInt("location." + ring.getKey());
                if (loc == null || loc == 0) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            addItemToGui(gui, material, itemName + " " + ring.getKey(), color, lore, enchant, loc-1);
        }
        addItemToGui(gui, Material.BARRIER, "יציאה", ChatColor.RED, null, false, 26);
    }
    //gui is public.
    public static void addItemToGui(Inventory gui, Material itemtype, String Name, ChatColor color, List<String> lore, boolean enchant ,Integer place) {
        //create Items
        ItemStack item = new ItemStack(itemtype);
//        assert meta != null;
        if (enchant) {
            item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
            ItemMeta Glowmeta = item.getItemMeta();
            Glowmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(Glowmeta);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + Name);

        //sets the lore
        meta.setLore(lore);

        item.setItemMeta(meta);
        gui.setItem(place, item);
    }

    public static void createItems(Player player) {
        Material material = RingCommand.material;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        for (var item1 : RingCommand.items.entrySet()) {
            int itemNum = item1.getKey();

            List<String> lore = new ArrayList<>();
            lore.add(String.valueOf(itemNum));

            meta.setDisplayName(String.valueOf(itemNum));
            meta.setLore(lore);
            item.setItemMeta(meta);

            item1.getValue().setWorld(player.getWorld());
            Location location = item1.getValue();
            Item dropitem = player.getWorld().dropItem(location, item);
            dropitem.setVelocity(dropitem.getVelocity().zero());

        }
    }

    public static void removeItems(List<Entity> entList) {
        for(Entity current : entList){//loop through the list

            if (current instanceof Item) {//make sure we aren't deleting mobs/players
                Item item = (Item) current;
                if (item.getItemStack().getType().toString().equalsIgnoreCase(RingCommand.material.toString())) {
                    int RingNum;
                    try {
                        List<String> lore = Objects.requireNonNull(Objects.requireNonNull(item.getItemStack().getItemMeta()).getLore());
                        if (lore.get(0) != null) {
                            RingNum = Integer.parseInt(lore.get(0));
                            if (RingCommand.items.containsKey(RingNum)) {
                                current.remove();
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public static void save() {
        for (var item1 : RingCommand.items.entrySet()) {
            if (SqlConfig.checkIfNull("SELECT COUNT(*) FROM rings WHERE `ring` = (" + item1.getKey() + ");")) {
                String sql = "INSERT INTO rings (`ring`, `world`, `LocX`, `LocY`, `LocZ`) VALUES ((" + item1.getKey() + "), '" + item1.getValue().getWorld().getName() + "', (" + String.valueOf(item1.getValue().getX()) + "), (" + String.valueOf(item1.getValue().getY())+ "), (" + String.valueOf(item1.getValue().getZ()) + "));";
                SqlConfig.updateQuery(sql);
            }
        }


    }

    public static void load() {

        ParlaYesPlanet plugin = ParlaYesPlanet.getPlugin();

        try {
            ResultSet data = SqlConfig.selectQuery("SELECT * FROM rings");

            if (data == null) {
                return;
            }
            while (data.next()) {
                World world = Bukkit.getWorld(data.getString("world"));
                Location location = new Location(world, data.getFloat("LocX"), data.getFloat("LocY"), data.getFloat("LocZ"));
                RingCommand.items.put(data.getInt("ring"), location);
            }

        } catch (Exception e) {
            plugin.getLogger().info("Error");
        }
    }

    public static void setMode() {

        ParlaYesPlanet plugin = ParlaYesPlanet.getPlugin();
        plugin.getConfig().set("mode", RingCommand.enable);
        plugin.saveConfig();

    }

    public static void creatHologam(Player player, int RingNum) {
        ParlaYesPlanet plugin = ParlaYesPlanet.getPlugin();
        String msg = plugin.getConfig().getString("found-hologram");
        String message;
        if (msg != null) {
            message = msg.replace("&", "§");
        } else {
            message = "§aמצאת";
        }
        if (RingNum == -1 ){
            for (var item : RingCommand.items.entrySet()) {
                String sql = "SELECT COUNT(*) FROM ringsFound WHERE `player` = '" + player.getUniqueId().toString() + "' AND `ring` = (" + item.getKey() + ") ;";
                if (!SqlConfig.checkIfNull(sql)) {
                    Hologram hologram = HologramsAPI.createHologram(ParlaYesPlanet.getPlugin(), RingCommand.items.get(item.getKey()).clone().add(0, 2, 0));
                    hologram.appendTextLine(message);
                    hologram.getVisibilityManager().setVisibleByDefault(false);
                    hologram.getVisibilityManager().showTo(player);
                }
            }
        } else {
            String sql = "SELECT COUNT(*) FROM ringsFound WHERE `player` = '" + player.getUniqueId().toString() + "' AND `ring` = (" + RingNum + ") ;";
            if (!SqlConfig.checkIfNull(sql)) {
                Hologram hologram = HologramsAPI.createHologram(ParlaYesPlanet.getPlugin(), RingCommand.items.get(RingNum).clone().add(0, 2, 0));
                hologram.appendTextLine(message);
                hologram.getVisibilityManager().setVisibleByDefault(false);
                hologram.getVisibilityManager().showTo(player);
            }
        }
    }

    public static void removeHolograms() {
        for (Hologram hologram : HologramsAPI.getHolograms(ParlaYesPlanet.getPlugin())) {
            hologram.delete();
        }
    }

    public static void removeHologramsTo(Player player) {
        for (Hologram hologram : HologramsAPI.getHolograms(ParlaYesPlanet.getPlugin())) {
            if (hologram.getVisibilityManager().isVisibleTo(player)) {
                hologram.delete();
            }
        }
    }

}

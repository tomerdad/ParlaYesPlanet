package me.tomerdad.parlayesplanet;

import me.tomerdad.parlayesplanet.commands.RingCommand;
import me.tomerdad.parlayesplanet.listners.DespawnItems;
import me.tomerdad.parlayesplanet.listners.GuiOpen;
import me.tomerdad.parlayesplanet.listners.OnPlayerJoin;
import me.tomerdad.parlayesplanet.listners.RingPickUp;
import me.tomerdad.parlayesplanet.utilities.Placeholders;
import me.tomerdad.parlayesplanet.utilities.SqlConfig;
import me.tomerdad.parlayesplanet.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ParlaYesPlanet extends JavaPlugin {

    //https://www.spigotmc.org/threads/remove-item-despawn.425314/

    private static ParlaYesPlanet plugin;
    public static String path;

    @Override
    public void onEnable() {
        path = this.getConfig().getCurrentPath();
        plugin = this;

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            // this returns a boolean, true if your placeholder is successfully registered, false if it isn't
            new Placeholders(plugin).register();
            plugin.getLogger().info("added placeholder");
        }

        this.getLogger().info("Starting plugin");
        this.saveDefaultConfig();
        SqlConfig.getSQLConnection();
        SqlConfig.load();
        try {
            RingCommand.material = Material.matchMaterial(Objects.requireNonNull(this.getConfig().getString("item")));
        } catch (Exception e) {
            RingCommand.material = Material.AIR;
        }

        GuiOpen.title = ParlaYesPlanet.getPlugin().getConfig().getString("gui-title");

        this.getCommand("popcorn").setExecutor(new RingCommand(this));
        Utilities.load();
//        this.getCommand("FindMe").setExecutor(new FindMeCommand());
        this.getServer().getPluginManager().registerEvents(new RingPickUp(this), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new DespawnItems(this), this);
        this.getServer().getPluginManager().registerEvents(new GuiOpen(), this);

        if (SqlConfig.checkIfNull("SELECT COUNT(*) FROM rings")) {
            RingCommand.enable = false;
        } else {
            if (this.getConfig().getBoolean("mode")) {
                RingCommand.enable=this.getConfig().getBoolean("mode");
            } else {
                Utilities.setMode();
            }
        }

        for (Player player : plugin.getServer().getOnlinePlayers()){
            Utilities.creatHologam(player, -1);
        }

    }

    @Override
    public void onDisable() {
        SqlConfig.Close();
        Utilities.save();
        Utilities.setMode();
    }

    public static ParlaYesPlanet getPlugin() {
        return plugin;
    }
}

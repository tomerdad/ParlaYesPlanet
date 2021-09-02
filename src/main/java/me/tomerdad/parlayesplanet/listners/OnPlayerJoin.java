package me.tomerdad.parlayesplanet.listners;

import me.tomerdad.parlayesplanet.ParlaYesPlanet;
import me.tomerdad.parlayesplanet.commands.RingCommand;
import me.tomerdad.parlayesplanet.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class OnPlayerJoin implements Listener {

    private static ParlaYesPlanet plugin;

    @EventHandler
    public void Onplayerjoin(PlayerJoinEvent event) {

        if (!RingCommand.enable) {
            return;
        }

        plugin = ParlaYesPlanet.getPlugin();

        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            plugin.getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            plugin.getLogger().severe("*** This plugin will be disabled. ***");
            return;
        }
        Player player = event.getPlayer();

        Utilities.creatHologam(player, -1);
    }
}

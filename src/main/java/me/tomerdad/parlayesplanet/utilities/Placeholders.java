package me.tomerdad.parlayesplanet.utilities;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.tomerdad.parlayesplanet.ParlaYesPlanet;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

    private final ParlaYesPlanet plugin;

    public Placeholders(ParlaYesPlanet plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "parlayesplanet";
    }

    @Override
    public String getAuthor() {
        return "tomerdad";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String identifier) {
        if (identifier.equalsIgnoreCase("ringsfound")) {
            int count = SqlConfig.getCount("SELECT COUNT(*) FROM ringsFound WHERE `player` = '" + p.getUniqueId() + "';");
            return String.valueOf(count);
        }
        if (identifier.equalsIgnoreCase("ringscount")) {
            int count = SqlConfig.getCount("SELECT COUNT(*) FROM rings;");
            return String.valueOf(count);
        }
        return null;
    }
}

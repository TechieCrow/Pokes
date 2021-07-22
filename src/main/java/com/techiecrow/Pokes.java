package com.techiecrow;

import com.techiecrow.commands.PokeCount;
import com.techiecrow.commands.PokePlayer;
import com.techiecrow.sql.MySQL;
import com.techiecrow.sql.SQLGetter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public class Pokes extends JavaPlugin implements Listener {

    public MySQL SQL;
    public SQLGetter data;

    public String prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("Prefix")));
    public String consolePrefix = "[Pokes] ";

    @Override
    public void onEnable() {

        // bStats
        int pluginId = 1054;
        Metrics metrics = new Metrics(this, pluginId);

        // A custom bstats simple pie chart to see if databases are enabled or disabled
        metrics.addCustomChart(new SimplePie("databases", () -> String.valueOf(getConfig().getBoolean("Enable Database"))));

        this.RegisterCommands();
        this.RegisterConfig();

        // Check if database is enabled in config
        if (getConfig().getBoolean("Enable Database")) {
            this.SQL = new MySQL(this);
            this.data = new SQLGetter(this);

            // Try to connect to the database
            try {
                SQL.connect();
            } catch (ClassNotFoundException | SQLException e) {
                Bukkit.getLogger().info(consolePrefix + "Database not connected");
                e.printStackTrace();
            }

            if (SQL.isConnected()) {
                Bukkit.getLogger().info(consolePrefix + "Database is connected!");
                data.createTable();
                getServer().getPluginManager().registerEvents(this, this);
            }
        }

        if (PokePlayer.setupEconomy()) {
            Bukkit.getLogger().info(consolePrefix + "Vault found!, players will receive money when poking!");
        }
        else if (!PokePlayer.setupEconomy()) {
            Bukkit.getLogger().info(consolePrefix + "Vault not found, players will not receive money when poking.");
        }
    }

    @Override
    public void onDisable() {

        // Need to close the database connection
        if (getConfig().getBoolean("Enable Database")) {
            SQL.disconnect();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();

        // When player joins the server we need to add them to the database, can probably do this when a player gets poked or pokes someone
        // But it's just easier to add them when they join the server
        if (getConfig().getBoolean("Enable Database")) {
            data.createPlayer(player);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("pokesreload") && sender.hasPermission("poke.reload")) {
            reloadConfig();
            getConfig();
            sender.sendMessage(prefix + ChatColor.GREEN + "Reloaded the config!");
            return true;
        } else {
            sender.sendMessage(prefix + ChatColor.RED + "You need the 'poke.reload' permission to use this command.");
            return false;
        }
    }

    // Makes commands work
    public void RegisterCommands() {
        Objects.requireNonNull(getCommand("poke")).setExecutor(new PokePlayer(this));
        Objects.requireNonNull(getCommand("pokes")).setExecutor(new PokeCount(this));
    }

    // Make default config and save it
    private void RegisterConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
package com.techiecrow;

import com.techiecrow.commands.PokeCount;
import com.techiecrow.commands.PokePlayer;
import com.techiecrow.sql.MySQL;
import com.techiecrow.sql.SQLGetter;
import org.bstats.bukkit.Metrics;
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

public class Pokes extends JavaPlugin implements Listener {

    public MySQL SQL;
    public SQLGetter data;

    @Override
    public void onEnable() {
        int pluginId = 1054;
        Metrics metrics = new Metrics(this, pluginId);

        this.RegisterCommands();
        this.RegisterConfig();
        if (this.getConfig().getBoolean("Enable Database")) {
            this.SQL = new MySQL(this);
            this.data = new SQLGetter(this);

            try {
                SQL.connect();
            } catch (ClassNotFoundException | SQLException e) {
                Bukkit.getLogger().info("Database not connected");
                e.printStackTrace();
            }

            if (SQL.isConnected()) {
                Bukkit.getLogger().info("Database is connected!");
                data.createTable();
                this.getServer().getPluginManager().registerEvents(this, this);
            }
        }
    }

    @Override
    public void onDisable() {
        if (this.getConfig().getBoolean("Enable Database")) {
            SQL.disconnect();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (this.getConfig().getBoolean("Enable Database")) {
            Player player = event.getPlayer();
            data.createPlayer(player);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Prefix"));
        if (label.equalsIgnoreCase("pokesreload") && sender.hasPermission("poke.reload")) {
            this.reloadConfig();
            this.getConfig();
            sender.sendMessage(prefix + ChatColor.GREEN + "Reloaded the config!");
            return true;
        } else {
            sender.sendMessage(prefix + ChatColor.RED + "You need the 'poke.reload' permission to use this command.");
            return false;
        }
    }

    // Makes commands work
    public void RegisterCommands() {
        this.getCommand("poke").setExecutor(new PokePlayer(this));
        this.getCommand("pokes").setExecutor(new PokeCount(this));
    }

    // Make default config and save it
    private void RegisterConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }
}
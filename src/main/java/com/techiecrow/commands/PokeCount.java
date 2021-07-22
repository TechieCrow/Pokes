package com.techiecrow.commands;

import com.techiecrow.Pokes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class PokeCount implements CommandExecutor {

    private final Pokes plugin;


    public PokeCount(Pokes pl) {
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("pokes") && sender.hasPermission("poke.display")) {
            if (args.length != 1) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /poke <player>");
                sender.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /pokes <player>");
                sender.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /pokesreload");
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + " '" + args[0] + "' is not currently online.");
                return true;
            }

            int count = plugin.getConfig().getInt("Players." + target.getUniqueId() + ".Pokes");
            String playerPoker = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("Count Message")).replace("%poked_name%", target.getDisplayName()).replace("%count%", String.valueOf(count)));
            sender.sendMessage(plugin.prefix + playerPoker);
        }

        return true;
    }
}
package com.techiecrow.commands;

import com.techiecrow.Pokes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PokeCount implements CommandExecutor {

    private Pokes plugin;


    public PokeCount(Pokes pl) {
        this.plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("Prefix"));
        if (label.equalsIgnoreCase("pokes") && sender.hasPermission("poke.display")) {
            if (args.length != 1) {
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /poke <player>");
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /pokes <player>");
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /pokesreload");
                return true;
            }

            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(prefix + ChatColor.RED + " '" + args[0] + "' is not currently online.");
                return true;
            }

            int count = this.plugin.getConfig().getInt("Players." + target.getUniqueId() + ".Pokes");
            String playerPoker = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("Count Message").replace("%poked_name%", target.getDisplayName()).replace("%count%", String.valueOf(count)));
            sender.sendMessage(prefix + playerPoker);
        }

        return true;
    }
}
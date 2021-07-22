package com.techiecrow.commands;

import com.techiecrow.Pokes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

public class PokePlayer implements CommandExecutor {

    public HashMap<String, Long> coolDowns = new HashMap<>();
    private final Pokes plugin;

    public PokePlayer(Pokes pl) {
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int coolDown = plugin.getConfig().getInt("CoolDown", 30);
        if (coolDowns.containsKey(sender.getName())) {
            long target = coolDowns.get(sender.getName()) / 1000L + (long) coolDown
                    - System.currentTimeMillis() / 1000L;
            if (target > 0L) {
                sender.sendMessage(
                        plugin.prefix + ChatColor.RED + "You need to wait " + target + " seconds before you can poke again!");
                return true;
            }
        }

        if (label.equalsIgnoreCase("poke") && sender.hasPermission("poke.poke")) {
            if (args.length != 1) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /poke <player>");
                sender.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /pokes <player>");
                sender.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /pokesreload");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + "You can only run this command as a player!");
                return false;
            }

            Player target1 = Bukkit.getServer().getPlayer(args[0]);
            if (target1 == null) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + " '" + args[0] + "' is not currently online.");
                return true;
            }

            if (target1 == sender) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + " You cannot poke yourself!");
                return true;
            }

            Player player = (Player) sender;

            String playerPoker = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("Poker Message")).replace("%poked_name%", target1.getDisplayName()));

            String playerPoked = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("Poked Message")).replace("%poker_name%", player.getDisplayName()));

            String playSound = plugin.getConfig().getString("Poke Sound");

            coolDowns.put(sender.getName(), System.currentTimeMillis());

            player.sendMessage(plugin.prefix + playerPoker);

            target1.playSound(target1.getLocation(), Sound.valueOf(playSound), 1.0F, 1.0F);

            target1.sendMessage(plugin.prefix + playerPoked);

            if(!plugin.getConfig().getBoolean("Enable Database")) {
                if (plugin.getConfig().getInt("Players." + target1.getUniqueId() + ".Pokes") <= 0) {
                    plugin.getConfig().set("Players." + target1.getUniqueId() + ".Pokes", 1);
                    plugin.saveConfig();
                } else if (plugin.getConfig().getInt("Players." + target1.getUniqueId() + ".Pokes") > 0) {
                    byte i = 1;
                    int pokeCount = plugin.getConfig().getInt("Players." + target1.getUniqueId() + ".Pokes");
                    plugin.getConfig().set("Players." + target1.getUniqueId() + ".Pokes", pokeCount + i);
                    plugin.saveConfig();
                }
            }
            if (plugin.getConfig().getBoolean("Enable Database")) {
                plugin.data.addCount(target1.getUniqueId(), 1);
            }
        } else {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "You need the 'poke.poke' permission to use this command.");
        }

        return false;
    }
}
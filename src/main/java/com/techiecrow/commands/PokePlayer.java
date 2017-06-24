package com.techiecrow.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.techiecrow.Pokes;

import java.util.HashMap;

public class PokePlayer implements CommandExecutor
{

    public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
    private Pokes plugin;

    public PokePlayer(Pokes pl)
    {
        this.plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        String prefix = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("Prefix"));
        int cooldown = this.plugin.getConfig().getInt("Cooldown", 30);
        if (this.cooldowns.containsKey(sender.getName()))
        {
            long target = this.cooldowns.get(sender.getName()).longValue() / 1000L + (long) cooldown
                    - System.currentTimeMillis() / 1000L;
            if (target > 0L)
            {
                sender.sendMessage(
                        prefix + ChatColor.RED + "You need to wait " + target + " seconds before you can poke again!");
                return true;
            }
        }

        if (label.equalsIgnoreCase("poke") && sender.hasPermission("poke.poke"))
        {
            if (args.length != 1)
            {
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /poke <player>");
                return true;
            }

            if (!(sender instanceof Player))
            {
                sender.sendMessage(prefix + ChatColor.RED + "You can only run this command as a player!");
                return false;
            }

            Player target1 = Bukkit.getServer().getPlayer(args[0]);
            if (target1 == null)
            {
                sender.sendMessage(prefix + ChatColor.RED + " \'" + args[0] + "\' is not currently online.");
                return true;
            }

            if (target1 == sender)
            {
                sender.sendMessage(prefix + ChatColor.RED + " You cannot poke yourself!");
                return true;
            }

            Player player = (Player) sender;
            String playerpoker = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig()
                    .getString("Poker Message").replace("%poked_name%", target1.getDisplayName()));
            String playerpoked = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig()
                    .getString("Poked Message").replace("%poker_name%", player.getDisplayName()));
            String playsound = this.plugin.getConfig().getString("Poke Sound");
            this.cooldowns.put(sender.getName(), Long.valueOf(System.currentTimeMillis()));
            player.sendMessage(prefix + playerpoker);
            target1.playSound(target1.getLocation(), Sound.valueOf(playsound), 1.0F, 1.0F);
            target1.sendMessage(prefix + playerpoked);
            if (this.plugin.getConfig().getInt("Players." + target1.getUniqueId() + ".Pokes") <= 0)
            {
                this.plugin.getConfig().set("Players." + target1.getUniqueId() + ".Pokes", Integer.valueOf(1));
                this.plugin.saveConfig();
            } else if (this.plugin.getConfig().getInt("Players." + target1.getUniqueId() + ".Pokes") > 0)
            {
                byte i = 1;
                int pokecount = this.plugin.getConfig().getInt("Players." + target1.getUniqueId() + ".Pokes");
                this.plugin.getConfig().set("Players." + target1.getUniqueId() + ".Pokes",
                        Integer.valueOf(pokecount + i));
                this.plugin.saveConfig();
            }
        } else
        {
            sender.sendMessage(prefix + ChatColor.RED + "You need the \'poke.poke\' permission to use this command.");
        }

        return false;
    }
}
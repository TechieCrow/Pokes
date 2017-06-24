package com.techiecrow;

import com.techiecrow.commands.PokeCount;
import com.techiecrow.commands.PokePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Pokes extends JavaPlugin
{
    public void onEnable()
    {
        this.RegisterCommands();
        this.RegisterConfig();
    }

    public void onDisable()
    {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        String prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Prefix"));
        if (label.equalsIgnoreCase("pokesreload") && sender.hasPermission("poke.reload"))
        {
            this.reloadConfig();
            this.getConfig();
            sender.sendMessage(prefix + ChatColor.GREEN + "Successfully reloaded the config!");
            return true;
        } else
        {
            sender.sendMessage(prefix + ChatColor.RED + "You need the \'poke.reload\' permission to use this command.");
            return false;
        }
    }

    public void RegisterCommands()
    {
        this.getCommand("poke").setExecutor(new PokePlayer(this));
        this.getCommand("pokes").setExecutor(new PokeCount(this));
    }

    private void RegisterConfig()
    {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }
}
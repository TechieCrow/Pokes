package com.techiecrow;

import com.techiecrow.commands.PokeCount;
import com.techiecrow.commands.PokePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class Pokes extends JavaPlugin
{

    //Connection vars
    private static Connection connection; //This is the variable we will use to connect to database

    public void onEnable()
    {
        this.RegisterCommands();
        this.RegisterConfig();

        //Below here is the SQL stuff.

        try
        { //We use a try catch to avoid errors, hopefully we don't get any.
            Class.forName("com.mysql.jdbc.Driver"); //this accesses Driver in jdbc.
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }
        try
        { //Another try catch to get any SQL errors (for example connections errors)
            String username = "YOUR DB USERNAME";
            String password = "YOUR DB PASSWORD";
            String url = "jdbc:mysql://db4free.net:3306/DataBaseName";
            connection = DriverManager.getConnection(url, username, password);
            //with the method getConnection() from DriverManager, we're trying to set
            //the connection's url, username, password to the variables we made earlier and
            //trying to get a connection at the same time. JDBC allows us to do this.
        } catch (SQLException e)
        { //catching errors)
            e.printStackTrace(); //prints out SQLException errors to the console (if any)
        }

        String sql = "CREATE TABLE IF NOT EXISTS myTable(Something varchar(64));";
// prepare the statement to be executed
        try
        {
            PreparedStatement stmt = connection.prepareStatement(sql);
            // I use executeUpdate() to update the databases table.
            stmt.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        sql = "SELECT * FROM myTable WHERE Something='Something'";
        PreparedStatement stmt = null;
        try
        {
            stmt = connection.prepareStatement(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        ResultSet results = null;
        try
        {
            if (stmt != null)
            {
                results = stmt.executeQuery();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            if (results != null)
            {
                if (!results.next())
                {
                    System.out.println("Failed");
                } else
                {
                    System.out.println("Success");
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        sql = "INSERT INTO myTable(Something) VALUES ('?');";
        stmt = null;
        try
        {
            stmt = connection.prepareStatement(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            if (stmt != null)
            {
                stmt.setString(1, "Something"); //I set the "?" to "Something"
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            if (stmt != null)
            {
                stmt.executeUpdate();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        sql = "SELECT * FROM myTable WHERE Something='Something'";
        try
        {
            stmt = connection.prepareStatement(sql);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            if (stmt != null)
            {
                results = stmt.executeQuery();
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        try
        {
            if (results != null)
            {
                if (!results.next())
                {
                    System.out.println("Failed");
                } else
                {
                    System.out.println("Success");
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void onDisable()
    {
        // invoke on disable.
        try
        { //using a try catch to catch connection errors (like wrong sql password...)
            if (connection != null && !connection.isClosed())
            { //checking if connection isn't null to
                //avoid receiving a nullpointer
                connection.close(); //closing the connection field variable.
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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
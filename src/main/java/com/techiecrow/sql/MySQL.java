package com.techiecrow.sql;

import com.techiecrow.Pokes;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private Connection connection;

    private Pokes plugin;

    public MySQL(Pokes plugin) {
        this.plugin = plugin;
    }

    public boolean isConnected()
    {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException
    {
        if(!isConnected()) {
            String host = this.plugin.getConfig().getString("SQL Host");
            int port = this.plugin.getConfig().getInt("SQL Port");
            String database = this.plugin.getConfig().getString("SQL Database");
            String username = this.plugin.getConfig().getString("SQL Username");
            String password = this.plugin.getConfig().getString("SQL Password");
            boolean useSSL = this.plugin.getConfig().getBoolean("SQL Use SSL");

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL + "&characterEncoding=utf8", username, password);
        }
    }

    public void disconnect()
    {
        if (isConnected())
        {
            try
            {
                connection.close();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection()
    {
        return connection;
    }
}
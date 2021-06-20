package com.techiecrow.sql;

import com.techiecrow.Pokes;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {

    private Pokes plugin;

    public SQLGetter(Pokes plugin) {
        this.plugin = plugin;
    }

    public void createTable() {
        PreparedStatement ps;
        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS pokes " + "(Name VARCHAR(100),UUID VARCHAR(100),Count INT(255),PRIMARY KEY (Name))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            if (!exists(uuid)) {
                PreparedStatement ps2 = plugin.SQL.getConnection().prepareStatement("INSERT IGNORE INTO pokes" + " (Name, UUID) VALUES (?,?)");
                ps2.setString(1, player.getName());
                ps2.setString(2, uuid.toString());
                ps2.executeUpdate();

                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM pokes WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addCount(UUID uuid, int count) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("UPDATE pokes SET Count=? WHERE UUID=?");
            ps.setInt(1, (getCount(uuid) + count));
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCount(UUID uuid) {
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement("SELECT Count FROM pokes WHERE UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("Count");
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
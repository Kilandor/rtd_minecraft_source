package com.czecherface.shimmerblock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.config.ConfigurationNode;

public class SQL {

    private final ShimmerBlock plugin;
    private ConfigurationNode config;
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private static SQL instance;
    private final static String sLink_DoubleInput = "SELECT * FROM shimmerblock WHERE (world_a = ? AND block_a_x = ? AND block_a_y = ? AND block_a_z = ?) OR (world_a = ? AND block_a_x = ? AND block_a_y = ? AND block_a_z = ?) OR (world_b = ? AND block_b_x = ? AND block_b_y = ? AND block_b_z = ?) OR (world_b = ? AND block_b_x = ? AND block_b_y = ? AND block_b_z = ?)";
    private final static String sLink_A = "SELECT * FROM shimmerblock WHERE world_a = ? AND block_a_x = ? AND block_a_y = ? AND block_a_z = ? LIMIT 1";
    private final static String sLink_B = "SELECT * FROM shimmerblock WHERE world_b = ? AND block_b_x = ? AND block_b_y = ? AND block_b_z = ? LIMIT 1";
    private final static String iLink = "INSERT INTO shimmerblock (world_a, block_a_x, block_a_y, block_a_z, world_b, block_b_x, block_b_y, block_b_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String dLink_SingleInput = "DELETE FROM shimmerblock WHERE (world_a = ? AND block_a_x = ? AND block_a_y = ? AND block_a_z = ?) OR (world_b = ? AND block_b_x = ? AND block_b_y = ? AND block_b_z = ?)";

    public static SQL getInstance() {
        if (instance == null) {
            throw new RuntimeException("The SQL singleton has not yet been initialized!");
        }
        return instance;
    }

    /**
     * Note that Econcomy's settings must be initialized before calling this or you may experience
     * exceptions being thrown about.
     */
    public static void initialize(ShimmerBlock shimmerblock, ConfigurationNode configNode) {
        if (instance != null) {
            throw new RuntimeException("The SQL singleton has already been initialized!");
        }
        if (shimmerblock == null) {
            throw new RuntimeException("Must pass a valid ShimmerBlock object.");
        }
        instance = new SQL(shimmerblock, configNode);
    }

    private SQL(ShimmerBlock shimmerblock, ConfigurationNode configNode) {
        plugin = shimmerblock;
        config = configNode;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Unable to find MYSQL Class: " + ex.getMessage());
        }
        connect();
    }

    private boolean connect() {
        try {
            conn = DriverManager.getConnection(config.getString("db"), config.getString("user", ""), config.getString("pass", ""));
            if (!conn.isValid(10)) {
                return false;
            }
            //Make sure the correct table(s) already exist
            CallableStatement cs = conn.prepareCall(
                    "CREATE TABLE IF NOT EXISTS `shimmerblock` ("
                    + "`link` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The unique link ID',"
                    + "`created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,"
                    + "`world_a` varchar(128) NOT NULL,"
                    + "`block_a_x` int(11) NOT NULL,"
                    + "`block_a_y` int(11) NOT NULL,"
                    + "`block_a_z` int(11) NOT NULL,"
                    + "`world_b` varchar(128) NOT NULL,"
                    + "`block_b_x` int(11) NOT NULL,"
                    + "`block_b_y` int(11) NOT NULL,"
                    + "`block_b_z` int(11) NOT NULL,"
                    + "PRIMARY KEY (`link`), "
                    + "UNIQUE KEY `uc_BlockA` (`world_a`,`block_a_x`,`block_a_y`,`block_a_z`),"
                    + "UNIQUE KEY `uc_BlockB` (`world_b`,`block_b_x`,`block_b_y`,`block_b_z`)"
                    + ") ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
            return cs.execute();
        } catch (SQLException ex) {
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Mysql Connection Failed: " + ex.getMessage());
        }
        return false;
    }

    /**
     * The connection must always be valid and this call ensures that.
     * This should be called by all methods in this class that query the database.
     */
    private void checkConnection() {
        boolean valid = false;
        try {
            valid = conn.isValid(10);
            if (!valid) {
                valid = connect();
            }
        } catch (SQLException ex) {
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Mysql Connection Check Failed: " + ex.getMessage());
        }
        if (!valid) {
            plugin.getChat().globalMsg(ShimmerBlock.CHATTITLE, "Having trouble connecting to the MySQL server.  Please report this!", false);
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Cannot connect to MySQL database!");
            throw new RuntimeException("Cannot connect to database -- BIG PROBLEM, FIX IT!");
        }
    }

    /**
     * Closes the current persistent MySQL connection.
     */
    public static void destroy() {
        if (instance == null) {
            throw new RuntimeException("The SQL instance was never created and therefore cannot be destroyed.");
        }
        instance.close();
        instance = null;
    }

    private void close() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Mysql Close Fail: " + ex.getMessage());
        }
    }

    public boolean createLink(Location locA, Location locB) {
        checkConnection();

        try {
            //Check if either location is in the database
            String wA = locA.getWorld().getName(), wB = locB.getWorld().getName();
            int xA = locA.getBlockX(), yA = locA.getBlockY(), zA = locA.getBlockZ();
            int xB = locB.getBlockX(), yB = locB.getBlockY(), zB = locB.getBlockZ();
            ps = conn.prepareStatement(sLink_DoubleInput);
            ps.setString(1, wA);
            ps.setInt(2, xA);
            ps.setInt(3, yA);
            ps.setInt(4, zA);
            ps.setString(5, wB);
            ps.setInt(6, xB);
            ps.setInt(7, yB);
            ps.setInt(8, zB);
            ps.setString(9, wA);
            ps.setInt(10, xA);
            ps.setInt(11, yA);
            ps.setInt(12, zA);
            ps.setString(13, wB);
            ps.setInt(14, xB);
            ps.setInt(15, yB);
            ps.setInt(16, zB);

            rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }

            //Okay, it's not in the database so lets add it
            ps = conn.prepareStatement(iLink);
            ps.setString(1, wA);
            ps.setInt(2, xA);
            ps.setInt(3, yA);
            ps.setInt(4, zA);
            ps.setString(5, wB);
            ps.setInt(6, xB);
            ps.setInt(7, yB);
            ps.setInt(8, zB);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Mysql Query createLink: " + ex.getMessage());
        }
        return false;
    }

    public void deleteLink(Location loc) {
        checkConnection();

        try {
            String w = loc.getWorld().getName();
            int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
            ps = conn.prepareStatement(dLink_SingleInput);
            ps.setString(1, w);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, w);
            ps.setInt(6, x);
            ps.setInt(7, y);
            ps.setInt(8, z);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Mysql Query handleBreak: " + ex.getMessage());
        }
    }

    public Location getExit(Location entrance) {
        checkConnection();

        try {
            String w = entrance.getWorld().getName();
            int x = entrance.getBlockX(), y = entrance.getBlockY(), z = entrance.getBlockZ();
            ps = conn.prepareStatement(sLink_A);
            ps.setString(1, w);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            rs = ps.executeQuery();
            if (rs.next()) {
                //We found the exit to be block B of this link
                World world = plugin.getServer().getWorld(rs.getString("world_b"));
                return new Location(world, rs.getInt("block_b_x"), rs.getInt("block_b_y"), rs.getInt("block_b_z"));
            }

            ps = conn.prepareStatement(sLink_B);
            ps.setString(1, w);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            rs = ps.executeQuery();
            if (rs.next()) {
                //We found the exit to be block A of this link
                World world = plugin.getServer().getWorld(rs.getString("world_a"));
                return new Location(world, rs.getInt("block_a_x"), rs.getInt("block_a_y"), rs.getInt("block_a_z"));
            }
        } catch (SQLException ex) {
            ShimmerBlock.log.log(Level.SEVERE, "[ShimmerBlock] Mysql Query getExit: " + ex.getMessage());
        }

        //No link found
        return null;
    }
}

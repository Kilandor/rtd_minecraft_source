package com.rtdgaming.economy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.util.config.ConfigurationNode;

public class SQL
{
	private final Economy plugin;
	private ConfigurationNode config;
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	private static SQL instance;
	private final static String sBlock = "SELECT * FROM economy WHERE blockid = ? LIMIT 1";
	private final static String sQuantity = "SELECT quantity FROM economy WHERE blockid = ? LIMIT 1";
	private final static String uQuantity = "UPDATE economy SET quantity = ? WHERE blockid = ? LIMIT 1";
	private final static String iNewBlock = "INSERT INTO economy (blockid, quantity) VALUES (?, ?)";

	public static SQL getInstance()
	{
		if(instance == null)
			throw new RuntimeException("The SQL singleton has not yet been initialized!");
		return instance;
	}

	/**
	 * Note that Econcomy's settings must be initialized before calling this or you may experience
	 * exceptions being thrown about.
	 */
	public static void initialize(Economy economy, ConfigurationNode configNode)
	{
		if(instance != null)
			throw new RuntimeException("The SQL singleton has already been initialized!");
		if(economy == null)
			throw new RuntimeException("Must pass a valid Econcomy object.");
		instance = new SQL(economy, configNode);
	}

	private SQL(Economy economy, ConfigurationNode configNode)
	{
		plugin = economy;
		config = configNode;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(ClassNotFoundException ex)
		{
			Economy.log.log(Level.SEVERE, "[Econcomy] Unable to find MYSQL Class: " + ex.getMessage());
		}
		connect();
	}

	private boolean connect()
	{
		try
		{
			conn = DriverManager.getConnection(config.getString("db"), config.getString("user", ""), config.getString("pass", ""));
			if (!conn.isValid(10))
				return false;
			//Make sure the correct table(s) already exist
			CallableStatement cs = conn.prepareCall(
					"CREATE TABLE IF NOT EXISTS `economy` (" +
					"`blockid` varchar(16) NOT NULL, " +
					"`quantity` int(11) NOT NULL, " +
					"UNIQUE KEY `id` (`blockid`)" +
					") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
					);
			return cs.execute();
		}
		catch(SQLException ex)
		{
			Economy.log.log(Level.SEVERE, "[Econcomy] Mysql Connection Failed: " + ex.getMessage());
		}
		return false;
	}

	/**
	 * The connection must always be valid and this call ensures that.
	 * This should be called by all methods in this class that query the database.
	 */
	private void checkConnection()
	{
		boolean valid = false;
		try
		{
			valid = conn.isValid(10);
			if(!valid)
				valid = connect();
		}
		catch(SQLException ex)
		{
			Economy.log.log(Level.SEVERE, "[Economy] Mysql Connection Check Failed: " + ex.getMessage());
		}
		if(!valid)
		{
			plugin.getChat().globalMsg(Economy.CHATTITLE, "Having trouble connecting to the MySQL server.  Please report this!", false);
			Economy.log.log(Level.SEVERE, "[Economy] Cannot connect to MySQL database!");
			throw new RuntimeException("Cannot connect to database -- BIG PROBLEM, FIX IT!");
		}
	}

	/**
	 * Closes the current persistent MySQL connection.
	 */
	public static void destroy()
	{
		if(instance == null)
			throw new RuntimeException("The SQL instance was never created and therefore cannot be destroyed.");
		instance.close();
		instance = null;
	}

	private void close()
	{
		try
		{
			if(rs != null)
				rs.close();
			if(ps != null)
				ps.close();
			if(conn != null)
				conn.close();
		}
		catch(SQLException ex)
		{
			Economy.log.log(Level.SEVERE, "[Economy] Mysql Close Fail: " + ex.getMessage());
		}
	}

	public int sQuantity(String absoluteBlockId)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(sQuantity);
			ps.setString(1, absoluteBlockId);
			rs = ps.executeQuery();
			if(rs.next())
				return rs.getInt("quantity");
			else if(TransactionHandler.getInstance().blockExists(absoluteBlockId))
			{
				int qty = TransactionHandler.getInstance().getBlockData(absoluteBlockId).getMedianQuantity();
				iNewBlock(absoluteBlockId, qty);
				return qty;
			}
		}
		catch(SQLException ex)
		{
			Economy.log.log(Level.SEVERE, "[Economy] Mysql Query sQuantity: " + ex.getMessage());
		}
		return 0;
	}

	public void uQuantity(String absoluteBlockId, int quantity)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(uQuantity);
			ps.setInt(1, quantity);
			ps.setString(2, absoluteBlockId);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			Economy.log.log(Level.SEVERE, "[Economy] Mysql Query uCredits: " + ex.getMessage());
		}
	}

	public boolean sExistsBlock(int blockId)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(sBlock);
			ps.setInt(1, blockId);
			rs = ps.executeQuery();

			return rs.next();
		}
		catch(SQLException ex)
		{
			Economy.log.log(Level.SEVERE, "[Economy] Mysql Query sExistsBlock: " + ex.getMessage());
		}
		return false;
	}

	public void iNewBlock(String absoluteBlockId, int qty)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(iNewBlock);
			ps.setString(1, absoluteBlockId);
			ps.setInt(2, qty);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			Economy.log.log(Level.SEVERE, "[Economy] Mysql Query iNewBlock: " + ex.getMessage());
		}
	}
}

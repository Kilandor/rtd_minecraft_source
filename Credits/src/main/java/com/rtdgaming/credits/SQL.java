package com.rtdgaming.credits;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.util.config.ConfigurationNode;

public class SQL
{
	private final Credits plugin;
	private ConfigurationNode config;
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	private static SQL instance;
	private final static String sPlayer = "SELECT * FROM credits WHERE player = ? LIMIT 1";
	private final static String sCredits = "SELECT credits FROM credits WHERE player = ? LIMIT 1";
	private final static String uCredits = "UPDATE credits SET credits = ? WHERE player = ? LIMIT 1";
	private final static String iNewPlayer = "INSERT INTO credits (player) VALUES (?)";

	public static SQL getInstance()
	{
		if(instance == null)
			throw new RuntimeException("The SQL singleton has not yet been initialized!");
		return instance;
	}

	/**
	 * Note that rtd's settings must be initialized before calling this or you may experience
	 * exceptions being thrown about.
	 */
	public static void initialize(Credits credits, ConfigurationNode configNode)
	{
		if(instance != null)
			throw new RuntimeException("The SQL singleton has already been initialized!");
		if(credits == null)
			throw new RuntimeException("Must pass a valid RTD object.");
		instance = new SQL(credits, configNode);
	}

	private SQL(Credits credits, ConfigurationNode configNode)
	{
		plugin = credits;
		config = configNode;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(ClassNotFoundException ex)
		{
			Credits.log.log(Level.SEVERE, "[RTD] Unable to find MYSQL Class: " + ex.getMessage());
		}
		connect();
	}

	private boolean connect()
	{
		try
		{
			conn = DriverManager.getConnection(config.getString("db"), config.getString("user"), config.getString("pass"));
			return conn.isValid(10);
		}
		catch(SQLException ex)
		{
			Credits.log.log(Level.SEVERE, "[RTD] Mysql Connection Failed: " + ex.getMessage());
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
			Credits.log.log(Level.SEVERE, "[Credits] Mysql Connection Check Failed: " + ex.getMessage());
		}
		if(!valid)
		{
			Credits.log.log(Level.SEVERE, "[Credits] Cannot connect to MySQL database!");
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
			Credits.log.log(Level.SEVERE, "[Credits] Mysql Close Fail: " + ex.getMessage());
		}
	}

	public long sCredits(String player)
	{
		try
		{
			ps = conn.prepareStatement(sCredits);
			ps.setString(1, player);
			rs = ps.executeQuery();
			rs.next();
			return rs.getLong("credits");
		}
		catch(SQLException ex)
		{
			Credits.log.log(Level.SEVERE, "[Credits] Mysql Query sCredits: " + ex.getMessage());
		}
		return 0;
	}

	public void uCredits(String player, long credits)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(uCredits);
			ps.setLong(1, credits);
			ps.setString(2, player);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			Credits.log.log(Level.SEVERE, "[Credits] Mysql Query uCredits: " + ex.getMessage());
		}
	}

	public boolean sExistsPlayer(String player)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(sPlayer);
			ps.setString(1, player);
			rs = ps.executeQuery();

			return rs.next();
		}
		catch(SQLException ex)
		{
			Credits.log.log(Level.SEVERE, "[Credits] Mysql Query sExistsPlayer: " + ex.getMessage());
		}
		return false;
	}

	public void iNewPlayer(String player)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(iNewPlayer);
			ps.setString(1, player);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			Credits.log.log(Level.SEVERE, "[Credits] Mysql Query iNewPlayer: " + ex.getMessage());
		}
	}
}

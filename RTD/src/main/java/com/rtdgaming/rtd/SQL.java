package com.rtdgaming.rtd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQL
{
	private final RTD plugin;
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	private static SQL instance;
	public final static String sPlayer = "SELECT * FROM players WHERE player = ? LIMIT 1";
	public final static String sLastRoll = "SELECT lastroll FROM players WHERE player = ? LIMIT 1";
	public final static String uLastRoll = "UPDATE players SET lastroll = ? WHERE player = ? LIMIT 1";
	public final static String iNewPlayer = "INSERT INTO players (player) VALUES (?)";

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
	public static void initialize(RTD rtd)
	{
		if(instance != null)
			throw new RuntimeException("The SQL singleton has already been initialized!");
		if(rtd == null)
			throw new RuntimeException("Must pass a valid RTD object.");
		instance = new SQL(rtd);
	}

	private SQL(RTD rtd)
	{
		plugin = rtd;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(ClassNotFoundException ex)
		{
			RTD.log.log(Level.SEVERE, "[RTD] Unable to find MYSQL Class: " + ex.getMessage());
		}
		connect();
	}

	private boolean connect()
	{
		try
		{
			conn = DriverManager.getConnection(plugin.settings.mysql_db, plugin.settings.mysql_user, plugin.settings.mysql_pass);
			return conn.isValid(10);
		}
		catch(SQLException ex)
		{
			RTD.log.log(Level.SEVERE, "[RTD] Mysql Connection Failed: " + ex.getMessage());
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
			RTD.log.log(Level.SEVERE, "[RTD] Mysql Connection Check Failed: " + ex.getMessage());
		}
		if(!valid)
		{
			plugin.getChat().globalMsg(RTD.CHATTITLE, "Having trouble connecting to the MySQL server.  Please report this!", false);
			RTD.log.log(Level.SEVERE, "[RTD] Cannot connect to MySQL database!");
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
			RTD.log.log(Level.SEVERE, "[RTD] Mysql Close Fail: " + ex.getMessage());
		}
	}

	public int sNextRoll(String player)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(sLastRoll);
			ps.setString(1, player);
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt("lastroll");
		}
		catch(SQLException ex)
		{
			RTD.log.log(Level.SEVERE, "[RTD] Mysql Query sLastRoll: " + ex.getMessage());
		}
		return Integer.MAX_VALUE;
	}

	public void uNextRoll(String player, long time)
	{
		checkConnection();

		try
		{
			ps = conn.prepareStatement(uLastRoll);
			ps.setLong(1, time);
			ps.setString(2, player);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			RTD.log.log(Level.SEVERE, "[RTD] Mysql Query uLastRoll: " + ex.getMessage());
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
			RTD.log.log(Level.SEVERE, "[RTD] Mysql Query sExistsPlayer: " + ex.getMessage());
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
			RTD.log.log(Level.SEVERE, "[RTD] Mysql Query iNewPlayer: " + ex.getMessage());
		}
	}
}

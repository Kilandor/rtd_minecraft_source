package com.kilandor.general;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQL
{
	private final General plugin;
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;

	public final static String sDisplay = "SELECT display, history FROM displayname WHERE player = ? LIMIT 1";
	public final static String sDisplayPlayer = "SELECT player FROM displayname WHERE display = ? LIMIT 1";
	public final static String uDisplay = "UPDATE displayname SET display = ?, history = ? WHERE player = ? LIMIT 1";
	public final static String iDisplay = "INSERT INTO displayname (player, display, history) VALUES (?, ?, ?)";

	public SQL(General instance)
	{
		plugin = instance;

		/*
		if(sqlite)
		{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(BBSettings.liteDb);
		}
		else
		{*/
			try
				{ Class.forName("com.mysql.jdbc.Driver"); }
			catch (ClassNotFoundException ex)
				{ General.log.log(Level.SEVERE, "[General] Unable to find MYSQL Class: " + ex.getMessage()); }
			try
			{
				conn = DriverManager.getConnection(plugin.settings.mysql_db, plugin.settings.mysql_user, plugin.settings.mysql_pass);
			}
			catch (SQLException ex)
				{ General.log.log(Level.SEVERE, "[General] Mysql Connection Failed: " + ex.getMessage()); }
		//}

	}

	public void close()
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
		catch (SQLException ex)
			{ General.log.log(Level.SEVERE, "[General] Mysql Close Fail: " + ex.getMessage()); }
	}

	public String sDisplay(String player, boolean name)
	{
		try
		{
			ps = conn.prepareStatement(sDisplay);
			ps.setString(1, player);
			rs = ps.executeQuery();
			if(rs.next())
			{
				if(name)
					return rs.getString("display");
				else
					return rs.getString("history");
			}
			else
			{
				return "";
			}
		}
		catch (SQLException ex)
			{ General.log.log(Level.SEVERE, "[General] Mysql Query sDisplay: " + ex.getMessage()); }
		return "";
	}

	public String sDisplayPlayer(String display)
	{
		try
		{
			ps = conn.prepareStatement(sDisplayPlayer);
			ps.setString(1, display);
			rs = ps.executeQuery();
			if(rs.next())
				return rs.getString("player");
			else
				return "";
		}
		catch (SQLException ex)
			{ General.log.log(Level.SEVERE, "[General] Mysql Query sDisplay: " + ex.getMessage()); }
		return "";
	}

	public void uDisplay(String player, String display, String history)
	{
		try
		{
			ps = conn.prepareStatement(uDisplay);
			ps.setString(1, display);
			ps.setString(2, history);
			ps.setString(3, player);
			ps.executeUpdate();
		}
		catch (SQLException ex)
			{ General.log.log(Level.SEVERE, "[General] Mysql Query uDisplay: " + ex.getMessage()); }
	}

	public void iDisplay(String player, String display, String history)
	{
		try
		{
			ps = conn.prepareStatement(iDisplay);
			ps.setString(1, player);
			ps.setString(2, display);
			ps.setString(3, history);
			ps.executeUpdate();
		}
		catch (SQLException ex)
			{ General.log.log(Level.SEVERE, "[General] Mysql Query iDisplay: " + ex.getMessage()); }
	}
}
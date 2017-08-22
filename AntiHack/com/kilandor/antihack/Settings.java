package com.kilandor.antihack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class Settings
{
	public static String config_file = "antihack.cfg";
	public static String config_comment = "Antihack Configuration File.";

	public String mysql_user = "";
	public String mysql_pass = "";
	public String mysql_db = "jdbc:mysql://localhost:3306/minecraft_general";
	
	public String motd_1 = "";
	public String motd_2 = "";
	public String motd_3 = "";
	

	public void saveDefaultSettings()
	{
		Properties props = new Properties();

		props.setProperty("mysql_user", mysql_user);
		props.setProperty("mysql_pass", mysql_pass);
		props.setProperty("mysql_db", mysql_db);

		props.setProperty("motd_1", motd_1);
		props.setProperty("motd_2", motd_2);
		props.setProperty("motd_3", motd_3);

		try
		{
			OutputStream propOut = new FileOutputStream(new File(config_file));
			props.store(propOut, config_comment);
		}
		catch (IOException ioe)
		{
			System.out.print(ioe.getMessage());
		}
	}

	public void saveSettings()
	{
		Properties props = new Properties();

		try
		{
			OutputStream propOut = new FileOutputStream(new File(config_file));
			props.store(propOut, config_comment);
		}
		catch (IOException ioe)
		{
			System.out.print(ioe.getMessage());
		}
	}

	public void loadSettings()
	{
		Properties props = new Properties();
		try
		{
			props.load(new FileInputStream(config_file));

			mysql_user = props.getProperty("mysql_user");
			mysql_pass = props.getProperty("mysql_pass");
			mysql_db = props.getProperty("mysql_db");

			motd_1 = props.getProperty("motd_1");
			motd_2 = props.getProperty("motd_2");
			motd_3 = props.getProperty("motd_3");

		}
		catch (IOException ioe)
		{
			props.clear();
			saveDefaultSettings();
		}
	}
}
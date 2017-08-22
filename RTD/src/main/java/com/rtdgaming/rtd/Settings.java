package com.rtdgaming.rtd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Settings
{
	public static String config_file = "rtd.cfg";
	public static String config_comment = "Roll the Dice(RTD) Configuration File.";
	public String mysql_user = "root";
	public String mysql_pass = "";
	public String mysql_db = "jdbc:mysql://localhost:3306/minecraft_general";
	public int rollTime = 600;
	public int rollDuration = 300;

	public void saveDefaultSettings()
	{
		Properties props = new Properties();

		props.setProperty("mysql_user", mysql_user);
		props.setProperty("mysql_pass", mysql_pass);
		props.setProperty("mysql_db", mysql_db);
		props.setProperty("rollTime", Integer.toString(rollTime));
		props.setProperty("rollDuration", Integer.toString(rollDuration));

		try
		{
			OutputStream propOut = new FileOutputStream(new File(config_file));
			props.store(propOut, config_comment);
		}
		catch(IOException ioe)
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
		catch(IOException ioe)
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

			rollTime = Integer.parseInt(props.getProperty("rollTime"));
			rollDuration = Integer.parseInt(props.getProperty("rollDuration"));

		}
		catch(IOException ioe)
		{
			props.clear();
			saveDefaultSettings();
		}
	}
}

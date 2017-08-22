package com.kilandor.daynight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class Settings
{
	public static String config_file = "DayNight.cfg";
	public static String config_comment = "Daynight Configuration File.";

	public String timeCycle = "none";

	public void saveDefaultSettings()
	{
		Properties props = new Properties();

		props.setProperty("timecycle", "none");
		this.timeCycle = "none";

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
		props.setProperty("timecycle", this.timeCycle);

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
			this.timeCycle = props.getProperty("timecycle");
		}
		catch (IOException ioe)
		{
			props.clear();
			saveDefaultSettings();
		}
	}
}
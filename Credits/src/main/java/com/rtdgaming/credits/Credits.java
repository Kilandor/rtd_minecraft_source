package com.rtdgaming.credits;

import com.kilandor.chat.Chat;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;

//import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * Credits for Bukkit
 *
 * @author Kilandor
 */
public class Credits extends JavaPlugin
{
	public static final String VERSION = "1.0";
	public static final String CHATTITLE = "[{blue}Credits{white}] ";

	public static final Logger log = Logger.getLogger("Minecraft");
        private NumberFormat formatter;
        
	public Configuration config;
        private Chat chat;

	public void onEnable()
	{
		System.out.println("Credits version " + VERSION + " is enabled!");

		config = new Configuration(new File(this.getDataFolder(), "credits.cfg"));
		config.load();

		SQL.initialize(this, config.getNode("mysql"));
                chat = new Chat(getServer());
                formatter = new DecimalFormat("#,###,###,###,###,###,###");
	}
	
	public void onDisable()
	{
		SQL.destroy();
	}

        @Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
            Player p = null;
            if (sender instanceof Player)
                p = (Player)sender;
            if (p == null || !cmd.getName().equalsIgnoreCase("credits"))
                return true;
            if (!p.isOp() || args.length == 0)
            {
                chat.playerMsg(p, Credits.CHATTITLE, "You have {gold}" + formatter.format(Credits.getCredits(p.getName())) + " credits.", false);
                return true;
            }
            
            if (args[0].equalsIgnoreCase("set") && args.length == 3)
            {
                try {
                    long credits = Long.parseLong(args[2]);
                    if (credits < 0)
                            throw new NumberFormatException();
                    if (!Credits.modifyCredits(args[1], credits - Credits.getCredits(args[1]))) {
                            chat.playerMsg(p, Credits.CHATTITLE, "The player specified does not exist.", false);
                            return false;
                    }
                } catch (NumberFormatException nfe) {
                    chat.playerMsg(p, Credits.CHATTITLE, "Bad credit amount passed.  Must be a non-negative integer.", false);
                    return false;
                }
            }
            else if (args[0].equalsIgnoreCase("add") && args.length == 3)
            {
                long credits = 0;
                try {
                    credits = Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    chat.playerMsg(p, Credits.CHATTITLE, "Bad credit amount passed.  Must be a non-negative integer.", false);
                }
                if (!Credits.modifyCredits(args[1], credits)) {
                    chat.playerMsg(p, Credits.CHATTITLE, "The player specified does not exist.", false);
                    return false;
                }
            }
            else if (!args[0].equalsIgnoreCase("get") || args.length < 2)
                return false;
            
            chat.playerMsg(p, Credits.CHATTITLE, args[1] + " has {gold}" + formatter.format(Credits.getCredits(args[1])) + " credits.", false);
                return true;
        }
        
	/**
	 * Returns the number of Credits a Player has
	 */
	public static long getCredits(String player)
	{
		long credits;
		
		if(existsPlayer(player))
			credits = SQL.getInstance().sCredits(player);
		else
			credits = 0;

		return credits;
	}
	
	/**
	 * Modify the number of Credits a Player has with an offset
	 *
	 * @param player Player Name
	 * @param offset Number of credits to add/subtract
	 * @return boolean True if player exists
	 */
	public static boolean modifyCredits(String player, long offset)
	{
		if(!existsPlayer(player))
			return false;

                long oldCredits = getCredits(player);
		long newCredits = oldCredits + offset;
                if (oldCredits > 0 && offset > 0 && newCredits < 0)
                    throw new RuntimeException("Time to refactor this method.  I found an long overflow.  Should now return true/false if it succeeds or fails.");
		if (newCredits < 0)
                    newCredits = 0;
		SQL.getInstance().uCredits(player, newCredits);
		return true;
	}

	/**
	 * Checks if a Player exists, and if not to add them
	 *
	 * @param player Player Name
	 * @return boolean
	 */
	private static boolean existsPlayer(String player)
	{
		boolean exists = SQL.getInstance().sExistsPlayer(player);

		if(exists)
			return exists;
		else
		{
			SQL.getInstance().iNewPlayer(player);
			exists = SQL.getInstance().sExistsPlayer(player);
		}

		return exists;
	}
}

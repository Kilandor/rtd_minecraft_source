package com.rtdgaming.rtd.eventcatchers;

import com.kilandor.chat.Chat;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.rtdgaming.rtd.RTD;
import com.rtdgaming.rtd.Roller;
import com.rtdgaming.rtd.rolls.*;

/**
 * Handle events for all Player related events
 * 
 * @author Kilandor
 */
public class PlayerCommands
{
	private final RTD plugin;
	private Chat chat;

	/*
	 * Add the condition and return in getCommand(String)
	 * and add the action in onPlayerCommand(PlayerChatEvent)
	 * when you create more commands.
	 */
	private enum RTDCommand
	{
		/* Special Commands */
		UNKNOWN, RTD, RTD_ADMIN, REPORT_ACTIVE, AWARD,
		/* Roll Commands */
		ROLL_PORT, ROLL_ROPE, ROLL_SURFACE
	}

	public PlayerCommands(RTD instance)
	{
		plugin = instance;
		chat = instance.getChat();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Roller roller = Roller.getRoller();

		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;
		else
			return false;

		String command = cmd.getName();

		switch(getCommand(command, args.length))
		{

			/* **************** */
			/* SPECIAL COMMANDS */
			/* **************** */

			case RTD:
				Roller.getRoller().roll(player);
				break;

			case RTD_ADMIN:
				if(!player.isOp())
					break;
				plugin.settings.loadSettings();
				chat.playerMsg(player, RTD.CHATTITLE, "Settings Reloaded", false);
				break;

			case REPORT_ACTIVE:
				Roller.getRoller().reportRollInfo(player);
				break;

			case AWARD:
				if(!player.isOp())
				{
					chat.playerMsg(player, RTD.CHATTITLE, "You don't have access to this command, silly :P", false);
					break;
				}
				if(args.length == 0)
				{
					chat.playerMsg(player, RTD.CHATTITLE, "Usage: /a roll_id", false);
					break;
				}

				int roll_ordinal = Integer.parseInt(args[0]);
				if(roll_ordinal < 0 || roll_ordinal >= _RollsEnum.values().length || roll_ordinal == _RollsEnum._BAD_ROLLS.ordinal())
				{
					chat.playerMsg(player, RTD.CHATTITLE, "No such roll.", false);
					break;
				}

				_RollsEnum roll = _RollsEnum.values()[roll_ordinal];
				if(roller.isActiveRoll(player, roll))
				{
					chat.playerMsg(player, RTD.CHATTITLE, "You already have " + roll.toString(), false);
					break;
				}

				Roller.getRoller().giveAward(player, roll);
				break;

			/* ********************** */
			/* ROLL-SPECIFIC COMMANDS */
			/* ********************** */

			case ROLL_PORT:
				DejaVu.getInstance().activate(player);
				break;

			case ROLL_ROPE:
				EscapeRope.getInstance().activate(player);
				break;
				
			case ROLL_SURFACE:
				SurfaceDweller.getInstance().activate(player);
				break;
		}
		return true;
	}

	private RTDCommand getCommand(String command, int argsLength)
	{
		if (command.equalsIgnoreCase("rtd"))
		{
			if (argsLength == 0)
				return RTDCommand.RTD;
			return RTDCommand.RTD_ADMIN;
		}
		else if (command.equalsIgnoreCase("a"))
			return RTDCommand.AWARD;
		else if (command.equalsIgnoreCase("port"))
			return RTDCommand.ROLL_PORT;
		else if (command.equalsIgnoreCase("rtdinfo"))
			return RTDCommand.REPORT_ACTIVE;
		else if (command.equalsIgnoreCase("rope"))
			return RTDCommand.ROLL_ROPE;
		else if (command.equalsIgnoreCase("rise"))
			return RTDCommand.ROLL_SURFACE;

		return RTDCommand.UNKNOWN;
	}
}

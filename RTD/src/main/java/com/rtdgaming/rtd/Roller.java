package com.rtdgaming.rtd;

import com.kilandor.chat.Chat;

import org.bukkit.entity.Player;

import com.rtdgaming.rtd.rolls._RollsEnum;
import com.rtdgaming.rtd.spout.SoundHandler;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import org.getspout.spoutapi.SpoutManager;

/**
 * Singleton class, use Roller.initialize and Roller.getRoller()
 */
public class Roller
{
	private static Roller roller;
	private HashMap<String, ArrayList<RollInfo>> playerRolls;
	private HashMap<String, Integer> playerLastRoll;
	private RTD plugin;
	private Chat chat;
	private Random randomGen;

	public static Roller initialize(RTD instance)
	{
		if(roller != null)
			throw new RuntimeException("You may not intialize the roller twice!");
		if(instance == null)
			throw new RuntimeException("Invalid RTD instance!");
		roller = new Roller(instance);
		return roller;
	}

	public static Roller getRoller()
	{
		if(roller == null)
			throw new RuntimeException("Please initialize the roller first.");
		return roller;
	}

	private Roller(RTD instance)
	{
		plugin = instance;
		chat = instance.getChat();
		randomGen = new Random();
		playerRolls = new HashMap<String, ArrayList<RollInfo>>();
		playerLastRoll = new HashMap<String, Integer>();
	}

	/**
	 * Rolls the dice.
	 * @param player player that wishes to roll the dice.
	 */
	public void roll(Player player)
	{
		if(!checkLastRoll(player))
			return;

		boolean goodRoll = randomGen.nextBoolean();
		_RollsEnum roll = _RollsEnum.getRandomRoll(player, goodRoll);
		giveAward(player, roll);
		
		int nextroll = (int)plugin.getCurTimestamp() + (roll == _RollsEnum.WAIT_MORE ? 120 : plugin.settings.rollTime);
		SQL.getInstance().uNextRoll(player.getName(), nextroll);
		
		playerLastRoll.put(player.getName(), nextroll);
		SoundHandler.getSoundHandler().playURLSoundPlayer(SpoutManager.getPlayer(player), "diceroll.wav", false);
	}

	/**
	 * Actually activates a specified roll on the player.
	 * @param player
	 * @param roll
	 */
	public void giveAward(Player player, _RollsEnum roll)
	{
		roll.getInstance().setup(roll.new Data(player));
		if(roll.getInstance().isActive())
			addPlayerRoll(player.getName(), new RollInfo(roll, plugin.getCurTimestamp(), player.getLocation()));
	}

	private boolean checkLastRoll(Player player)
	{
		int nextroll = SQL.getInstance().sNextRoll(player.getName());
		boolean canRoll = nextroll <= plugin.getCurTimestamp();
		
		playerLastRoll.put(player.getName(), nextroll);
				
		if(!canRoll)
			chat.playerMsg(player, RTD.CHATTITLE, "{green}You must wait "
					+ (nextroll - plugin.getCurTimestamp()) + "s to roll.", false);
		return canRoll;
	}
	
	public int getLastRoll(Player player)
	{
		if(playerLastRoll.containsKey(player.getName()))
			return playerLastRoll.get(player.getName());
		return Integer.MAX_VALUE;
	}

	public boolean isActiveRoll(Player player, _RollsEnum roll)
	{
		if(getRollInfo(player, roll) != null)
			return true;
		return false;
	}

	public RollInfo getRollInfo(Player player, _RollsEnum roll)
	{
		ArrayList<RollInfo> rollInfoList = getPlayerRolls(player.getName());
		for(RollInfo rollInfo : rollInfoList)
			if(rollInfo.getRoll().ordinal() == roll.ordinal())
				return rollInfo;
		return null;
	}

	public void reportRollInfo(Player player)
	{
		Chat chat = RTD.rtd.getChat();
		ArrayList<RollInfo> rollInfoList = getPlayerRolls(player.getName());
		if(rollInfoList == null || rollInfoList.isEmpty())
		{
			chat.playerMsg(player, RTD.CHATTITLE, "{gray}You currently have no active rolls :(", false);
			return;
		}
		chat.playerMsg(player, RTD.CHATTITLE, "Your active rolls:", false);
		for(RollInfo rollInfo : rollInfoList)
		{
			String toPrint = "   " + (rollInfo.getRoll().isGood() ? "{blue}" : "{red}") + rollInfo.getRoll();
			int duration = rollInfo.getRoll().getInstance().getDuration();
			if(duration >= 0)
				toPrint += "{white} : {gray}" + (rollInfo.getTimestamp() + duration - plugin.getCurTimestamp()) + "s";
			chat.playerMsg(player, RTD.CHATTITLE, toPrint, false);
		}
	}

	public void addPlayer(String player)
	{
		if(!playerRolls.containsKey(player))
			playerRolls.put(player, new ArrayList<RollInfo>());
		int nextroll = SQL.getInstance().sNextRoll(player);
		playerLastRoll.put(player, nextroll);
	}

	public void addPlayerRoll(String p, RollInfo r)
	{
		playerRolls.get(p).add(r);
	}

	public ArrayList<RollInfo> getPlayerRolls(String player)
	{
		return playerRolls.get(player);
	}

	public void remPlayerRoll(String p, _RollsEnum r)
	{
		RollInfo toRemove = null;
		for(RollInfo rollInfo : playerRolls.get(p))
			if(rollInfo.getRoll() == r)
				toRemove = rollInfo;
		if(toRemove != null)
			playerRolls.get(p).remove(toRemove);
	}

	public void remPlayerRolls(String player)
	{
		if(playerRolls.containsKey(player))
			playerRolls.get(player).clear();
	}
}

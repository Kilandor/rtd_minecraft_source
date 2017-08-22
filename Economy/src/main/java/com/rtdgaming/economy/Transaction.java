package com.rtdgaming.economy;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kilandor.chat.Chat;
import com.rtdgaming.credits.Credits;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Holds transaction session information.
 */
public class Transaction
{
	//Nasty hack to get chat into this object
	private static Chat chat;
	
	//Holds the list of blocks with depend on their durability being defined (ex. Wool).
	private static int[] duraBlocks;

	public static void setChat(Chat c)
	{
		if(chat == null)
			chat = c;
	}

	public static enum TransactionType
	{
		/**
		 * Warning: Changing this enumeration may require you to change the switch statement
		 * in Transaction.matches(Transaction).
		 */
		NONE,
		SELL_INHAND,
		SELL_INVENTORY, //TODO: Implement
		__SELL_or_BUY_MARKER__, //To check if this is a buy or sell transaction
		BUY_INHAND,
		BUY_MANUAL_PENDING, BUY_MANUAL_READY;
		
		public boolean isBuyOrder() {
			return this.ordinal() > TransactionType.__SELL_or_BUY_MARKER__.ordinal();
		}
		public boolean isSellOrder() {
			return this != TransactionType.NONE && this.ordinal() < TransactionType.__SELL_or_BUY_MARKER__.ordinal();
		}
	}
	static final int MAX_AMOUNT = 512;
	private static NumberFormat formatter = new DecimalFormat("#,###,###,###,###,###,###");
	public static void resetDuraBlockConfig()
	{
		Transaction.duraBlocks = null;
	}
	
	/**
	 * Factory responsible for creating Transaction objects.
	 * @param p
	 * @param sign The sign block that the player clicked.
	 * @return Hopefully a Transaction object; null on failure.
	 */
	public static Transaction generateTransaction(Player player, Sign sign, TransactionHandler handler)
	{
		if (Transaction.duraBlocks == null)
			Transaction.duraBlocks = ConfigurationParser.getInstance().getDuraBlocks();
		
		String[] lines = sign.getLines();
		if(lines[0].equalsIgnoreCase("check balance"))
		{
			long creditCount = Credits.getCredits(player.getName());
			chat.playerMsg(player, Economy.CHATTITLE, "Your balance is currently {gold}"+formatter.format(creditCount)+" {white}credits.", false);
			return null;
		}
		
		//Get the type of transaction
		TransactionType type = TransactionType.NONE;
		if(lines[0].equalsIgnoreCase("sell"))
		{
			if(lines[1].equalsIgnoreCase("inhand"))
				type = TransactionType.SELL_INHAND;
			else if(lines[1].equalsIgnoreCase("inventory"))
				type = TransactionType.SELL_INVENTORY;
		}
		else if(lines[0].equalsIgnoreCase("buy"))
			if(lines[1].equalsIgnoreCase("inhand"))
				type = TransactionType.BUY_INHAND;
			else if(lines[1].equalsIgnoreCase("manual"))
				type = TransactionType.BUY_MANUAL_PENDING;
		if(type == TransactionType.NONE)
		{
			System.out.println("Invalid transaction attempted: " + lines[0] + " | " + lines[1]);
			return null;
		}

		//If inhand, get the proper values
		if(type == TransactionType.BUY_INHAND || type == TransactionType.SELL_INHAND)
		{
			ItemStack inHand = player.getItemInHand();
			int blockId = inHand.getTypeId();
			int amount = inHand.getAmount();

			if(blockId == 0)
			{
				chat.playerMsg(player, Economy.CHATTITLE, "You must have an item in your hand to do that.", false);
				return null;
			}
			
			short durability = 0;
			for (int i = 0; i < Transaction.duraBlocks.length; i++)
				if (blockId == Transaction.duraBlocks[i])
				{
					durability = inHand.getDurability();
					break;
				}
			String absoluteBlockId = blockId + "-" + durability;
			if(!handler.blockExists(absoluteBlockId))
			{
				chat.playerMsg(player, Economy.CHATTITLE, "That block is not compatable with the RTD Economy.", false);
				return null;
			}
			
			//Make sure the item is buyable/sellable
			BlockData bd = handler.getBlockData(absoluteBlockId);
			if ((!bd.canBeBought() && type.isBuyOrder()) || (!bd.canBeSold() && type.isSellOrder()))
			{
				chat.playerMsg(player, Economy.CHATTITLE, "That block cannot be "+(type.isBuyOrder()?"bought":"sold")+".", false);					
				return null;
			}
			
			return new Transaction(player, type, blockId, amount, durability);
		}

		return new Transaction(player, type);
	}
	
	/****** BEGIN ACTUAL TRANSACTION WRAPPER ******/
	private Player player;
	private TransactionType type;
	private int blockId, amount;
	private short durability;
	private long quote, timestamp;

	private Transaction(Player p, TransactionType t)
	{
		player = p;
		type = t;
		renewTimestamp();
	}

	private Transaction(Player p, TransactionType t, int bId, int a, short d)
	{
		this(p, t);
		blockId = bId;
		amount = a;
		durability = d;
	}

	private void renewTimestamp()
	{
		timestamp = System.currentTimeMillis() + 30000;
	}

	public boolean hasExpired()
	{
		return timestamp < System.currentTimeMillis();
	}

	public Player getPlayer()
	{
		return player;
	}

	public TransactionType getType()
	{
		return type;
	}

	public void setToManualBuyPending()
	{
		type = TransactionType.BUY_MANUAL_PENDING;
		renewTimestamp();
	}
	
	public boolean isReady()
	{
		return type != TransactionType.BUY_MANUAL_PENDING;
	}

	/**
	 * This is not the fully qualified id which is used in the database and configuration data structure.
	 * @see getAbsoluteBlockId()
	 * @return The id of this block...duh..
	 */
	public int getBlockId()
	{
		return blockId;
	}

	public String getAbsoluteBlockId()
	{
		return blockId + "-" + durability;
	}
	
	public int getAmount()
	{
		return amount;
	}

	public short getDurability()
	{
		return durability;
	}

	public boolean update(int bId, int a, short d)
	{
		if(type != TransactionType.BUY_MANUAL_PENDING && type != TransactionType.BUY_MANUAL_READY)
			return false;
		if(Material.getMaterial(bId) != null && a > 0 && a <= MAX_AMOUNT)
		{
			type = TransactionType.BUY_MANUAL_READY;
			blockId = bId;
			amount = a;
			durability = d;
			renewTimestamp();
			return true;
		}
		return false;
	}

	public boolean matches(Transaction t)
	{
		if(player.getEntityId() != t.getPlayer().getEntityId())
			return false;

		switch(t.getType())
		{
			case BUY_MANUAL_PENDING:
			case BUY_MANUAL_READY:
				if (type != TransactionType.BUY_MANUAL_PENDING && type != TransactionType.BUY_MANUAL_READY)
					return false;
				break;
			default:
				if (type != t.getType()
					|| !getAbsoluteBlockId().equals(t.getAbsoluteBlockId())
					|| amount != t.getAmount()
					|| durability != t.getDurability())
					return false;
				break;
		}
		
		return true;
	}

	public long getQuote()
	{
		return quote;
	}

	public void setQuote(long q)
	{
		quote = q;
		renewTimestamp();
	}
}

package com.rtdgaming.economy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kilandor.chat.Chat;
import com.rtdgaming.credits.Credits;
import com.rtdgaming.economy.Transaction.TransactionType;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Singleton responsible for processing buy and sell orders and calculating block worth
 */
public class TransactionHandler
{
	/* BEGIN SINGLETON PATTERN */
	private static TransactionHandler instance;
	public static TransactionHandler getInstance()
	{
		if(instance == null)
			instance = new TransactionHandler();
		return instance;
	}
	private TransactionHandler() {}
	/* END SINGLETON PATTERN */
	
	private int totalTransactions, totalQuotes;
	private boolean initialized;
	private Economy plugin;
	private SQL sql;
        private NumberFormat formatter;
	private ConfigurationParser configParser;
	private TreeMap<String, BlockData> blocksConfig = new TreeMap<String, BlockData>();
	private HashMap<String, Long> usingSign = new HashMap<String, Long>();
	private HashMap<String, Transaction> transactions = new HashMap<String, Transaction>();

	public void initialize(Economy instance)
	{
		if(initialized)
                    return;
		plugin = instance;
		configParser = ConfigurationParser.getInstance();
		loadBlocksConfiguration();
		initialized = true;
		sql = SQL.getInstance();
                formatter = new DecimalFormat("#,###,###,###,###,###,###");
	}
	/*
	 * Reload and reset everything
	 */
	public void reload()
	{
		transactions.clear();
		usingSign.clear();
		loadBlocksConfiguration();
	}
	
	public void loadBlocksConfiguration() {
		blocksConfig = configParser.getBlockData();
		for (String s : blocksConfig.keySet()) {
			blocksConfig.get(s).finalize(this);
		}
	}

	public boolean blockExists(String absoluteBlockId)
	{
		return blocksConfig.containsKey(absoluteBlockId);
	}

	/**
	 * @return A RANDOM block data object.
	 */
	public BlockData getBlockData()
	{
		Random rand = new Random();
		return blocksConfig.get((String)blocksConfig.keySet().toArray()[rand.nextInt(blocksConfig.size())]);
	}

	public BlockData getBlockData(String absoluteBlockId)
	{
		return blocksConfig.get(absoluteBlockId);
	}

	public boolean isUsingSign(String playerName)
	{
		return usingSign.containsKey(playerName) && usingSign.get(playerName) > System.currentTimeMillis();
	}

	public TransactionType getTransactionType(String playerName)
	{
		if(transactions.containsKey(playerName))
			return transactions.get(playerName).getType();
		return TransactionType.NONE;
	}

	/**
	 * Process a transaction request from a player clicking on a sign
	 * @param isSell Is this a sell transaction?
	 * @param player
	 * @param lines
	 */
	public void process(Transaction t)
	{
		String playerName = t.getPlayer().getName();

		usingSign.put(playerName, System.currentTimeMillis() + 2000);

		if(transactions.containsKey(playerName))
		{
			Transaction savedTransaction = transactions.get(playerName);
			if(!savedTransaction.matches(t))
			{
				if(!savedTransaction.hasExpired())
					plugin.getChat().playerMsg(t.getPlayer(), Economy.CHATTITLE, "Your transaction session has changed. See the new info below.", false);
				transactions.remove(playerName);
			}
			else if(savedTransaction.getType() == TransactionType.BUY_MANUAL_PENDING)
			{
				plugin.getChat().playerMsg(t.getPlayer(), Economy.CHATTITLE, "{red}Stop:{white} You still need to specify what you want to buy!", false);
				explainManualBuy(t.getPlayer());
				return;
			}
			else if(savedTransaction.getQuote() <= 0)
			{
				plugin.getChat().playerMsg(t.getPlayer(), Economy.CHATTITLE, "{red}Error:{white} We're sorry, something broke.  Fixing...Done!", false);
				transactions.remove(playerName);
				return;
			}
			else if(!savedTransaction.isReady())
			{
				plugin.getChat().playerMsg(t.getPlayer(), Economy.CHATTITLE, "Your transaction needs more input. See the help below.", false);
				explainManualBuy(t.getPlayer());
				return;
			}
			else
			{
				//Do the transaction
				switch (performTransaction(savedTransaction))
				{
				case 0:
					//SUCCESS!!
					totalTransactions++;
					transactions.remove(playerName);
					return;
				case 1:
					//Non-matching quotes (out of date transaction?)
					plugin.getChat().playerMsg(t.getPlayer(), Economy.CHATTITLE, "{red}Warning:{white} That block's value has changed. See the new info below.", false);
					calculateValue(savedTransaction);
					return;
				case -1:
					//Any error that occurred was handled already
					return;
				}				
			}
		}
		/** POST CONDITION OF THIS IF-STATEMENT: This player has NO transaction objects in the "transactions" hashmap! **/
		if(transactions.containsKey(playerName))
			throw new RuntimeException("HERP DERP I FUCKED UP!");

		if(t.getType() == TransactionType.BUY_MANUAL_PENDING)
			explainManualBuy(t.getPlayer());
		else
			calculateValue(t);
		transactions.put(playerName, t);
	}

	public void updateTransaction(String playerName, int blockId, int amount, short durability)
	{
		if(!transactions.containsKey(playerName))
			return;
		Transaction t = transactions.get(playerName);
		Chat chat = plugin.getChat();
		String absoluteBlockId = blockId + "-" + durability;
		BlockData d = blocksConfig.get(absoluteBlockId);
		chat.playerMsg(t.getPlayer(), Economy.CHATTITLE, "Block Chosen : " + (d == null ? "{red}NOT SUPPORTED" : "{blue}" + d.getName()), false);
		chat.playerMsg(t.getPlayer(), Economy.CHATTITLE, "Amount Chosen : " + amount + "(" + (amount <= 0 || amount > Transaction.MAX_AMOUNT ? "{red}INVALID" : "{green}VALID") + "{white})", false);
		
		boolean error = false;
		if (d != null && ((t.getType().isSellOrder() && !d.canBeSold()) ||
				(t.getType().isBuyOrder() && !d.canBeBought())))
		{
			chat.playerMsg(t.getPlayer(), Economy.CHATTITLE, "{orange}Unfortunately{white} the block you have selected cannot be used in this transaction.", false);
			error = true;
		}
		
		if(d != null && !error && t.update(blockId, amount, durability))
			calculateValue(t);
		else
		{
			if (!error)
				chat.playerMsg(t.getPlayer(), Economy.CHATTITLE, "Please fix any errors and try again.", false);
			t.setToManualBuyPending();
		}
			
	}

	private void calculateValue(Transaction t)
	{
		BlockData blockData = blocksConfig.get(t.getAbsoluteBlockId());
		long value = blockData.getValue(t.getAmount(), t.getType().isBuyOrder());
		if(value <= 0)
			plugin.getChat().playerMsg(t.getPlayer(), Economy.CHATTITLE, "{red}Error{white}: Sell value 0 or lower.", false);
		else
		{
			String message = "";
			message += (t.getType().isSellOrder() ? "{green}Sell" : "{red}Buy") + "{white} value of ";
			message += "{lightpurple}" + t.getAmount() + " {blue}" + blockData.getName() + "{white} is ";
			message += "{gold}" + formatter.format(value) + "{white} credits.";

			Chat c = plugin.getChat();
			Player p = t.getPlayer();
			c.playerMsg(p, Economy.CHATTITLE, "[Q{lightpurple}" + totalQuotes + "{white}] " + message, false);
			c.playerMsg(p, Economy.CHATTITLE, "Right click on the sign again to confirm.", false);
			if(++totalQuotes < 0)
				totalQuotes = 0;
		}
		t.setQuote(value);
	}

	public void explainManualBuy(Player p)
	{
		Random rand = new Random();
		BlockData d = getBlockData();
		int amount = (int) Math.pow(2, rand.nextInt(10));

		String message = "An example: \"/buy " + d.getId() + " " + amount + "\" will buy " + amount + " " + d.getName() + " blocks.  512 count limit!";
		plugin.getChat().playerMsg(p, Economy.CHATTITLE, message, false);
	}

	private int performTransaction(Transaction t)
	{
		if(t.getQuote() != blocksConfig.get(t.getAbsoluteBlockId()).getValue(t.getAmount(), t.getType().isBuyOrder()))
			return 1;
		boolean result = t.getType().isSellOrder() ? sell(t) : buy(t);
		return result ? 0 : -1;
	}

	//TODO: Refactor when we allow selling of inventory
	private boolean sell(Transaction t)
	{
            //Get all the data about this transaction into local variables
            Player player = t.getPlayer();
            String blockId = t.getAbsoluteBlockId();
            BlockData blockData = blocksConfig.get(blockId);
            int qty = t.getAmount();
            long value = t.getQuote();
            int maxQty = blockData.getMaxQuantity();
            int blockQty = sql.sQuantity(blockId) + qty;
            if(blockQty > maxQty)
                blockQty = maxQty;

            //Check for integer overflow so that players don't lose their money
            long currentCredits = Credits.getCredits(player.getName());
            if (currentCredits + value < currentCredits)
            {
                    //OVERFLOW!  We cannot allow this player to sell anything!
                    plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{red}Sorry! {white}You are too {gold}rich{white}...", false);
                    return false;
            }
            else if (!Credits.modifyCredits(player.getName(), value))
            {
                    //We couldn't give the player their money D:
                    plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{red}Error{white}: Unable to modify credits.", false);
                    return false;
            }

            //At this point the player has acquired the credits for the transaction.
            player.setItemInHand(null); //No moar itemz fer u!!1
            sql.uQuantity(blockId, blockQty);
            plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{green}Success! {white}Sold {lightpurple}" + qty + " {blue}" + blockData.getName() + "{white} for {gold}" + formatter.format(value) + " {white}credits", false);
            return true;
	}

	private boolean buy(Transaction t)
	{
            //Get all the data about this transaction into local variables
            Player player = t.getPlayer();
            int blockId = t.getBlockId();
            String absoluteBlockId = t.getAbsoluteBlockId();
            BlockData blockData = blocksConfig.get(absoluteBlockId);
            int origQty = t.getAmount();
            long origValue = t.getQuote();
            short durability = t.getDurability();

            //Check if they have the monies
            if(Credits.getCredits(player.getName()) - origValue < 0)
            {
                    plugin.getChat().playerMsg(player, Economy.CHATTITLE, "You {red}don't have enough credits{white} to buy that.", false);
                    return false;
            }
            else if(!Credits.modifyCredits(player.getName(), -origValue))
            {
                    plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{red}Error{white}: Unable to modify credits. Try again in a moment.", false);
                    return false;
            }

            //Try and give them what they requested
            int amountGiven = give(player, blockId, origQty, durability);
            int refundQty = origQty - amountGiven;
            long trueValue;
            if (refundQty == 0)
            {
                //The value of their order is simple if they got everything
                trueValue = origValue;
            }
            else
            {
                //Well, now we need to refund them credits since they didn't get the full order.
                long refundValue = blockData.getValue(refundQty, t.getType().isBuyOrder());
                String refundValueString = formatter.format(refundValue);
                if (!Credits.modifyCredits(player.getName(), refundValue))
                {
                    plugin.getChat().globalMsg(Economy.CHATTITLE, "{red}Error{white}: Unable to modify credits of "+player.getName()+" (+"+refundValueString+").", false);
                    plugin.getChat().globalMsg(Economy.CHATTITLE, "{red}Error{white}: Please contact an admin!", false);
                    return false;
                }
                else if (refundQty == origQty)
                {
                    plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{red}Failure! {white}You have no space in your inventory.", false);
                    return false;
                }
                else
                    plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{orange}Warning{white}: {gold}" + refundValueString + "{white} credits were refunded because you don't have enough inventory space.", false);
                
                //Don't forget to set the value of the order
                trueValue = origValue - refundValue;
            }
            
            //Setup the new entry for how many are in the database
            int minQty = blockData.getMinQuantity();
            if(minQty <= -1)
                    throw new RuntimeException("Bad minimum quantity for blockID(" + absoluteBlockId + ").");
            int blockQty = sql.sQuantity(absoluteBlockId) - amountGiven;
            if(blockQty < minQty)
                    blockQty = minQty;
            sql.uQuantity(absoluteBlockId, blockQty);
            
            /*********************************************/
            /** Everything went better than expected :) **/
            /*********************************************/
            String totalValueString = formatter.format(trueValue);
            plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{green}Success! {white}Bought {lightpurple}" + amountGiven + " {blue}" + blockData.getName() + "{white} for {gold}" + totalValueString + " {white}credits.", false);
            
            //TODO: Terrible way to update the players inventory, fix it!
            player.updateInventory();
            return true;
	}

	/**
	 * Gives a player some blocks.
	 * @return How many blocks the player was given.
	 */
	private int give(Player player, int id, int qty, short durability)
	{
		Material block = Material.getMaterial(id);
		if(Material.getMaterial(id) == null)
		{
			plugin.getChat().playerMsg(player, Economy.CHATTITLE, "{red}Error:{white} Invalid item id {lightpurple}" + id, false);
			return 0;
		}

		int amountToGive = qty;
		int stackSize = block.getMaxStackSize();
		while(amountToGive > 0)
		{
			ItemStack itemStack = new ItemStack(id);
			int thisStackSize = stackSize <= amountToGive ? stackSize : amountToGive;
			itemStack.setAmount(thisStackSize);
			if (durability != 0)
				itemStack.setDurability(durability);
			amountToGive -= thisStackSize;

			//Check if the player's inventory is full
			HashMap<Integer, ItemStack> extra = player.getInventory().addItem(new ItemStack[] { itemStack });
			if(extra != null && !extra.isEmpty())
			{
				//Should only have one stack, but I'm looping it anyway
				for(Iterator<ItemStack> it = extra.values().iterator(); it.hasNext();)
					amountToGive += it.next().getAmount();
				break;
			}
		}
		return qty - amountToGive;
	}
	public void printBlockConfig() {
		String toPrint = "Name, ID, aID, buyable, sellable, level, formula, produces, minQ, maxQ, minP, maxP\n";
		for (Map.Entry<String, BlockData> entry : blocksConfig.entrySet()) {
			BlockData b = entry.getValue();
			toPrint += b.getName() + ", "
					+ b.getId() + ", "
					+ b.getAbsoluteId()+ ", "
					+ b.canBeBought() + ", "
					+ b.canBeSold() + ", "
					+ b.getLevel() + ", "
					+ b.getFormulaString() + ", "
					+ b.getProduction() + ", "
					+ b.getMinQuantity() + ", "
					+ b.getMaxQuantity() + ", "
					+ b.getMinPrice() + ", "
					+ b.getMaxPrice()
					+ "\n";
		}
		System.out.println("\nBegin Block Configuration\n-----\n\n" + toPrint + "\n\n-----\nEnd Block Configuration\n");
	}
}

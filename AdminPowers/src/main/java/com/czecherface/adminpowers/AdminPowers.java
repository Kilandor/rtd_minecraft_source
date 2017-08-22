package com.czecherface.adminpowers;

import java.util.Timer;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.kilandor.chat.Chat;

import com.czecherface.adminpowers.powers._Interaction;
import com.czecherface.adminpowers.powers._Power;
import com.czecherface.adminpowers.powers._PowerEnum;
import java.util.ArrayList;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class AdminPowers extends JavaPlugin {

    public static Logger log;
    public static AdminPowers ap;
    /* Objects for hooked events */
    //private PlayerCommands playerCommands;
    //private PlayerJoin playerJoin;
    private EntityListener entityListener;
    private BlockListener blockListener;
    private PlayerListener playerListener;
    private final Timer timer = new Timer();
    //Stores admin power data per player
    private PowerTracker tracker;
    /* Other objects */
    private Chat chat;
    public World defaultWorld;
    @SuppressWarnings("unused")
    private Server server;

    @Override
    public void onEnable() {
        log = Logger.getLogger("Minecraft");
        chat = new Chat(this.getServer());

        initializeVariables();
        registerHooks();

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version "
                + pdfFile.getVersion() + " is enabled!");
    }

    public void initializeVariables() {
        //The core stuff
        ap = this;
        server = getServer();

        //Create all the listeners
        entityListener = new EntityListener(this);
        blockListener = new BlockListener(this);
        playerListener = new PlayerListener(this);

        //Set up all the worlds
        defaultWorld = this.getServer().getWorlds().get(0);
        tracker = new PowerTracker();

        timer.scheduleAtFixedRate(TimerHandler.getInstance(), 1000, 1000);
    }

    public void registerHooks() {
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
        
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Event.Priority.Normal, this);
        
        pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Event.Priority.Normal, this);
    }

    public void onDisable() {
        timer.cancel();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player invoker = null;
        if (sender instanceof Player) {
            invoker = (Player) sender;
        } else {
            return false;
        }

        if (!sender.isOp() || !cmd.getName().equalsIgnoreCase("ap")) {
            return false;
        }

        //See if we should remove their powers per request
        if (args.length < 1) {
            if (tracker.playerHasPowers(invoker.getName())) {
                tracker.removePlayerPowers(invoker.getName());
                chat.playerMsg(invoker, Constants.TITLE, "Your admin powers were removed.", false);
                invoker.playEffect(invoker.getLocation(), Effect.CLICK2, 0);
            } else {
                chat.playerMsg(invoker, Constants.TITLE, "{red}/ap{white} alone removes your powers; try {green}/ap help{white} instead.", false);
            }
            return true;
        }

        //Display the help dialog
        if (args[0].equalsIgnoreCase("help")) {
            int page = 0;
            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                }
                printHelpMessage(invoker, page);
                return true;
            }

            printHelpMessage(invoker, 0);
            return true;
        }

        //Get our data into the correct form for processing the command
        String action = args[0];
        String[] params = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            params[i - 1] = args[i];
        }

        //Alright, find which adminpower they selected
        for (_PowerEnum powerEnum : _PowerEnum.values()) {
            if (!powerEnum.matchesCommand(action)) {
                continue;
            }

            if (powerEnum.isDisabled())
            {
                chat.playerMsg(invoker, Constants.TITLE, "Sorry, but that command is disabled.", false);
                return false;
            }
            
            _Power p = powerEnum.getNewInstance();
            if (p == null)
            {
                chat.playerMsg(invoker, Constants.TITLE, "Please report this error to an admin:", false);
                chat.playerMsg(invoker, Constants.TITLE, "{red}Cannot generate new instance of {darkred}" + powerEnum.toString() + "{red}!", false);
                return false;
            }
            
            //Play a clicking sound as feedback to the player that the command was processed
            invoker.playEffect(invoker.getLocation(), Effect.CLICK2, 0);
            
            String errorMsg = p.setData(invoker, params);
            //Note_A1: I postpone an error message here to use it as an indicator later for an override message on Interaction.NONE powers.
            if (errorMsg != null && p.getAcceptedInteraction() != _Interaction.NONE) {
                chat.playerMsg(invoker, Constants.TITLE, errorMsg, false);
                return false;
            }

            if (p.getAcceptedInteraction() == _Interaction.NONE) {
                //Note_A2: Here is where I am using that error message as an override like I mentioned above in a comment
                if (errorMsg != null) {
                    chat.playerMsg(invoker, Constants.TITLE, errorMsg, false);
                } else {
                    chat.playerMsg(invoker, Constants.TITLE, "Command (" + p.getEnum().getName() + ") issued successfully.", false);
                }
                return true;
            }
            
            tracker.applyPlayerPowers(invoker.getName(), p);
            chat.playerMsg(invoker, Constants.TITLE, p.getStatus(), false);
            return true;
        }
        return false;
    }

    private void printHelpMessage(Player p, int page) {
        int max = getMaxPageNumber();
        if (page > max) {
            page = max;
            chat.playerMsg(p, Constants.TITLE, "The max page # is {green}" + max + "{white}. Selecting it instead.", false);
        } else if (page < 0) {
            chat.playerMsg(p, Constants.TITLE, "The min page # is {green}0{white}. Selecting it instead.", false);
            page = 0;
        }

        _PowerEnum[] powers = _PowerEnum.values();
        int test = page == max ? powers.length : Constants.TEXT_LINES_PER_PAGE * (page + 1);
        for (int i = Constants.TEXT_LINES_PER_PAGE * page; i < test; i++) {
            chat.playerMsg(p, Constants.TITLE, powers[i].getHelpString(), false);
        }
        if (page != max) {
            chat.playerMsg(p, Constants.TITLE, "Type {green}/ap help " + (page + 1) + "{white} for more commands.", false);
        }
    }

    public long getCurTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public Chat getChat() {
        return chat;
    }

    
    <X> X[] getPowers(Class<X> powerType) {
        return tracker.getPowers(powerType);
    }
    
    public _Power getPlayerPower(String player) {
        return tracker.getPlayerPowers(player);
    }
    
    public void removePlayerPower(String player) {
        tracker.removePlayerPowers(player);
    }

    public Player getPlayer(String realName) {
        return getServer().getPlayer(realName);
    }

    private int getMaxPageNumber() {
        return _PowerEnum.length / Constants.TEXT_LINES_PER_PAGE; //rounds up
    }

    public static String blockToString(Block b) {
        return blockToString(b, false);
    }

    public static String blockToString(Block b, boolean full) {
        Location l = b.getLocation();
        if (full) {
            return b.getType() + " (" + b.getTypeId() + "-" + b.getData() + ") [" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "]";
        }
        return b.getType() + "(" + b.getTypeId() + ")[" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "]";
    }
}

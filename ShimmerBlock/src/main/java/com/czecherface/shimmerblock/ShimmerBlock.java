package com.czecherface.shimmerblock;

import java.io.File;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.kilandor.chat.Chat;

public class ShimmerBlock extends JavaPlugin {

    public static final String VERSION = "1.1";
    public static final String CHATTITLE = "[{blue}ShimmerBlock{white}] ";
    public static final Logger log = Logger.getLogger("Minecraft");
    private Configuration config;
    private Chat chat;
    private LinkedList<Player> allowed;
    private PlayerInteract playerInteract;
    private BlockEvent blockEvent;

    public void onEnable() {
        System.out.println("ShimmerBlock version " + VERSION + " is enabled!");
        chat = new Chat(this.getServer());
        config = new Configuration(new File(this.getDataFolder(), "shimmerblock.cfg"));
        config.load();

        SQL.initialize(this, config.getNode("mysql"));

        allowed = new LinkedList<Player>();
        playerInteract = new PlayerInteract(this);
        blockEvent = new BlockEvent(this, chat);
        registerHooks();
    }

    public void onDisable() {
        SQL.destroy();
    }

    public void registerHooks() {
        PluginManager pm = getServer().getPluginManager();
        //pm.registerEvent(Event.Type.PLAYER_COMMAND, playerCommands, Priority.Monitor, this);
        //pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityDamaged, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerInteract, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockEvent, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockEvent, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, blockEvent, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PISTON_RETRACT, blockEvent, Event.Priority.Normal, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("shimmerblock")) {
            return false;
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (allowed.contains(player)) {
            allowed.remove(player);
            chat.playerMsg(player, ShimmerBlock.CHATTITLE, "Linking turned {red}OFF{white}.", false);
        } else {
            allowed.add(player);
            chat.playerMsg(player, ShimmerBlock.CHATTITLE, "Linking turned {green}ON{white}.", false);
        }
        return true;
    }

    public boolean playerAllowed(Player p) {
        return allowed.contains(p);
    }

    public Chat getChat() {
        return chat;
    }
}

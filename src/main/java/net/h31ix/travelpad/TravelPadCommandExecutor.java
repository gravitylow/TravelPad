package net.h31ix.travelpad;

import net.h31ix.travelpad.api.Pad;
import net.h31ix.travelpad.api.TravelPadManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TravelPadCommandExecutor implements CommandExecutor {
    private Travelpad plugin;
    private TravelPadManager manager;
    
    public TravelPadCommandExecutor(Travelpad plugin)
    {
        this.plugin = plugin;
        this.manager = plugin.manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        if (!(cs instanceof Player))
        {
            System.out.println("Travelpad commands can only be used in-game");
        }
        else
        {
            Player player = (Player)cs;
            if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("identify") || args[0].equalsIgnoreCase("i"))
                {         
                    Pad pad = manager.getPadAt(player.getLocation());
                    if (pad != null)
                    {
                        player.sendMessage(ChatColor.GREEN+"You are at the pad named "+ChatColor.WHITE+pad.getName());
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+"You ar not standing on a registered TravelPad!");
                    }
                }             
                else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d"))
                {
                    if (plugin.getPads(player) > 1)
                    {
                        player.sendMessage(ChatColor.RED+"You have more than one portal, please delete them by name instead.");
                    }
                    else
                    {
                        if (player.hasPermission("travelpad.delete"))
                        {                       
                            if (plugin.hasPad(player))
                            {
                                Object[] pads = manager.getPadsFrom(player).toArray();
                                ((Pad)pads[0]).delete();
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED+"You don't have a TravelPad!");
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
                        }
                    }
                }
            }
            else if (args.length == 2)
            {
                if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp"))
                {    
                    if (player.hasPermission("travelpad.teleport"))
                    {
                        Pad pad = manager.getPadAt(player.getLocation());
                        if (pad != null)
                        {
                            if (plugin.doesPadExist(args[1]))
                            {
                                Location loc = manager.getPad(args[1]).getLocation();
                                plugin.teleport(player, loc);
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED+"That pad doesn't seem to exist...");
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+"You must be standing on a pad to do that.");
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
                    }
                }                   
                if (args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("n"))
                {
                    if (player.hasPermission("travelpad.name"))
                    {
                        if (manager.nameIsValid(args[1]))
                        {
                            String name = args[1];
                            boolean set = plugin.namePad(player, name);
                            if (set)
                            {
                                player.sendMessage(ChatColor.GREEN+"Named your pad "+ChatColor.WHITE+name);
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED+"You don't have a pad waiting to be named!");
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+"That name is already in use..");
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
                    }                    
                }
                else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d"))
                {
                    if (player.hasPermission("travelpad.delete.any"))
                    {
                        if (plugin.doesPadExist(args[1]))
                        {
                            manager.deletePad(manager.getPad(args[1]));
                            player.sendMessage(ChatColor.GREEN+"Removed pad "+ChatColor.WHITE+args[1]);
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+"Couldn't find that pad.");
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
                    }                    
                }
            }
            else
            {
                player.sendMessage(ChatColor.GREEN+"/travelpad [name/n] name");
                player.sendMessage(ChatColor.GREEN+"/travelpad [identify/i]");
                player.sendMessage(ChatColor.GREEN+"/travelpad [delete/d] [name]");
                player.sendMessage(ChatColor.GREEN+"/travelpad [teleport/tp] name");
            }
        }
        return true;
    }
}

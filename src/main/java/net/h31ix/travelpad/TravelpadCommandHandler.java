package net.h31ix.travelpad;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TravelpadCommandHandler implements CommandExecutor {
    Main plugin;
    
    public TravelpadCommandHandler(Main plugin)
    {
        this.plugin = plugin;
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
                    String name = plugin.getPortal(player.getLocation());
                    if (name != null)
                    {
                        player.sendMessage(ChatColor.GREEN+"You are at the pad named "+ChatColor.WHITE+name);
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+"You ar not standing on a registered TravelPad!");
                    }
                }             
                else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d"))
                {
                    if (player.hasPermission("travelpad.infinite"))
                    {
                        player.sendMessage(ChatColor.RED+"You have infinite portal access, please delete portal by name instead.");
                    }
                    else
                    {
                        if (player.hasPermission("travelpad.delete"))
                        {                       
                            if (plugin.hasPortal(player))
                            {
                                String name = plugin.getPlayersPortal(player);
                                if (name != null)
                                {
                                    plugin.removePad(name);
                                    player.sendMessage(ChatColor.GREEN+"Removed your TravelPad!");
                                }
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
                        String name = plugin.getPortal(player.getLocation());
                        if (name != null)
                        {     
                            if (plugin.doesPadExist(args[1]))
                            {
                                Location loc = plugin.getCoords(args[1]);
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
                        if (plugin.nameIsValid(args[1]))
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
                            plugin.removePad(args[1]);
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

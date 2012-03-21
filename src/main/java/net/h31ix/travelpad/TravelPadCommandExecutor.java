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
    private LangManager l;
    
    public TravelPadCommandExecutor(Travelpad plugin)
    {
        this.plugin = plugin;
        this.manager = plugin.manager;
        this.l = plugin.l;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        if (!(cs instanceof Player))
        {
            System.out.println(l.command_deny_console());
        }
        else
        {
            Player player = (Player)cs;
            if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("identify") || args[0].equalsIgnoreCase("i"))
                {         
                    Pad pad = plugin.getPadAt(player.getLocation());
                    if (pad != null)
                    {
                        player.sendMessage(ChatColor.GREEN+l.identify_found_message()+ChatColor.WHITE+" "+pad.getName());
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+l.identify_notfound_message());
                    }
                }             
                else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d"))
                {
                    if (plugin.getPads(player) > 1)
                    {
                        player.sendMessage(ChatColor.RED+l.delete_deny_multi());
                    }
                    else
                    {               
                        if (plugin.hasPad(player))
                        {
                            Object[] pads = manager.getPadsFrom(player).toArray();
                            manager.deletePad((Pad)pads[0]);
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+l.delete_deny_noportal());
                        }
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.GREEN+"/travelpad [name/n]");
                    player.sendMessage(ChatColor.GREEN+"/travelpad [identify/i]");
                    player.sendMessage(ChatColor.GREEN+"/travelpad [delete/d]");
                    player.sendMessage(ChatColor.GREEN+"/travelpad [teleport/tp");                    
                }
            }
            else if (args.length == 2)
            {
                if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp"))
                {    
                    if (player.hasPermission("travelpad.teleport") || player.hasPermission("travelpad.tp"))
                    {
                        Pad pad = plugin.getPadAt(player.getLocation());
                        if (pad != null)
                        {
                            if (plugin.doesPadExist(args[1]))
                            {
                                if (plugin.canTeleport(player))
                                {
                                    Location loc = manager.getPad(args[1]).getTeleportLocation();
                                    plugin.teleport(player, loc);
                                }
                                else
                                {
                                    player.sendMessage(ChatColor.RED+"Not enough money!");
                                }
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED+l.teleport_deny_notfound());
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+l.teleport_deny_loc());
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+l.command_deny_permission());
                    }
                }  
                else if (args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("n"))
                {
                    if (manager.nameIsValid(args[1]))
                    {
                        String name = args[1];
                        boolean set = plugin.namePad(player, name);
                        if (set)
                        {
                            player.sendMessage(ChatColor.GREEN+l.name_message()+ChatColor.WHITE+" "+name);
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+l.name_deny_nopad());
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+l.name_deny_inuse());
                    }                  
                }
                else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d"))
                {
                    if (plugin.doesPadExist(args[1]))
                    {
                        if (!manager.getPad(args[1]).getOwner().equalsIgnoreCase(player.getName()))
                        {
                            if (player.hasPermission("travelpad.delete.all") || player.hasPermission("travelpad.delete.any"))
                            {
                                manager.deletePad(manager.getPad(args[1]));
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED+l.command_deny_permission());
                            }
                        }
                        else
                        {
                                manager.deletePad(manager.getPad(args[1]));
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+l.delete_deny_notfound());
                    }
                }
                else
                {
                    player.sendMessage(ChatColor.GREEN+"/travelpad [name/n]");
                    player.sendMessage(ChatColor.GREEN+"/travelpad [identify/i]");
                    player.sendMessage(ChatColor.GREEN+"/travelpad [delete/d]");
                    player.sendMessage(ChatColor.GREEN+"/travelpad [teleport/tp");                    
                }                
            }
            else
            {
                player.sendMessage(ChatColor.GREEN+"/travelpad [name/n]");
                player.sendMessage(ChatColor.GREEN+"/travelpad [identify/i]");
                player.sendMessage(ChatColor.GREEN+"/travelpad [delete/d]");
                player.sendMessage(ChatColor.GREEN+"/travelpad [teleport/tp");
            }
        }
        return true;
    }
}

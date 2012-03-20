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
                if (args[0].equalsIgnoreCase(l.command_identify()) || args[0].equalsIgnoreCase(l.command_identify_shortcut()))
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
                else if (args[0].equalsIgnoreCase(l.command_delete()) || args[0].equalsIgnoreCase(l.command_delete_shortcut()))
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
            }
            else if (args.length == 2)
            {
                if (args[0].equalsIgnoreCase(l.command_teleport()) || args[0].equalsIgnoreCase(l.command_teleport_shortcut()))
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
                                    if (plugin.isWhitelisted(player, pad))
                                    {
                                        Location loc = manager.getPad(args[1]).getTeleportLocation();
                                        plugin.teleport(player, loc);
                                    }
                                    else
                                    {
                                        player.sendMessage(ChatColor.RED+"You are not whitelisted!");
                                    }
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
                else if (args[0].equalsIgnoreCase(l.command_name()) || args[0].equalsIgnoreCase(l.command_name_shortcut()))
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
                else if (args[0].equalsIgnoreCase(l.command_delete()) || args[0].equalsIgnoreCase(l.command_delete_shortcut()))
                {
                    if (player.hasPermission("travelpad.delete.all") || player.hasPermission("travelpad.delete.any") || manager.getPadsFrom(player).size() > 1)
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
                                    player.sendMessage(ChatColor.RED+l.delete_deny_notfound());
                                }
                            }
                            else
                            {
                                    manager.deletePad(manager.getPad(args[1]));
                                    player.sendMessage(ChatColor.GREEN+l.delete_approve()+" "+ChatColor.WHITE+args[1]);
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+l.delete_deny_notfound());
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+l.command_deny_permission());
                    }                    
                }
            }
            else if (args.length == 3)
            {
                if (args[0].equalsIgnoreCase(l.command_whitelist()) || args[0].equalsIgnoreCase(l.command_whitelist_shortcut()))
                {
                    if (plugin.doesPadExist(args[1]))
                    {
                        Pad pad = manager.getPad(args[1]);
                        if (args[2].equalsIgnoreCase("on"))
                        {
                            pad.setWhitelisted(true); 
                            player.sendMessage(ChatColor.GREEN+"Whitelist -> on");
                        }
                        else if (args[2].equalsIgnoreCase("off"))
                        {
                            pad.setWhitelisted(false); 
                            player.sendMessage(ChatColor.RED+"Whitelist -> off");
                        }
                        else
                        {
                            pad.addWhitelist(args[2]);
                            player.sendMessage(ChatColor.GREEN+"Added "+args[2]);
                        }
                    } 
                    else
                    {
                        player.sendMessage(ChatColor.RED+l.delete_deny_notfound());
                    }
                }
                if (args[0].equalsIgnoreCase(l.command_unwhitelist()) || args[0].equalsIgnoreCase(l.command_unwhitelist_shortcut()))
                {
                    if (plugin.doesPadExist(args[1]))
                    {
                        Pad pad = manager.getPad(args[1]);
                        if (pad.isWhitelisted())
                        {
                            boolean b = pad.removeWhitelist(args[2]);
                            if (b)
                            {
                                player.sendMessage(ChatColor.GREEN+"Removed "+args[2]);
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED+"Not whitelisted!");
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+"Whitelist not on.");
                        }
                    } 
                    else
                    {
                        player.sendMessage(ChatColor.RED+l.delete_deny_notfound());
                    }
                }                
            }
            else
            {
                player.sendMessage(ChatColor.GREEN+"/travelpad ["+l.command_name()+"/"+l.command_name_shortcut()+"]");
                player.sendMessage(ChatColor.GREEN+"/travelpad ["+l.command_identify()+"/"+l.command_identify_shortcut()+"]");
                player.sendMessage(ChatColor.GREEN+"/travelpad ["+l.command_delete()+"/"+l.command_delete_shortcut()+"]");
                player.sendMessage(ChatColor.GREEN+"/travelpad ["+l.command_teleport()+"/"+l.command_teleport_shortcut()+"]");
            }
        }
        return true;
    }
}

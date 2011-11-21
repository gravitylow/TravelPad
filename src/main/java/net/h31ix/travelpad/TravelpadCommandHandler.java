package net.h31ix.travelpad;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TravelpadCommandHandler implements CommandExecutor {
	private final Travelpad plugin;
	
	public TravelpadCommandHandler(Travelpad plugin){
		this.plugin = plugin;
	}
        
        @Override
	public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
            Player player = (Player)cs;
            if(cmd.getName().equalsIgnoreCase("travelpad")) {
                if (args.length < 1)
                {
                   player.sendMessage(ChatColor.AQUA + "Commands:"); 
                   player.sendMessage(ChatColor.AQUA + "/travelpad Identify - identifies the current pad you are standing on.");
                   player.sendMessage(ChatColor.AQUA + "/travelpad Name [name] - names the current pad you are standing on.");
                }
                else if (args[0].equalsIgnoreCase("identify")) { 
                    Location location = player.getLocation();
                    int x = (int)location.getX();
                    int y = (int)location.getX();
                    int z = (int)location.getX();
                    String name = plugin.searchCoords(x,y,z);
                    if (name!= null) {
                        player.sendMessage(ChatColor.AQUA + "You are standing on the portal named "+name);
                    }
                    else {
                        player.sendMessage(ChatColor.AQUA + "You are not standing on a registered TravelPad"); 
                    }
                }
                else if (args[0].equalsIgnoreCase("name")) { 
                    if (args.length == 2)
                    {
                    Location location = player.getLocation();
                    int x = (int)location.getX();
                    int y = (int)location.getX();
                    int z = (int)location.getX();
                    String name = plugin.searchPlayerPortal(x,y,z);
                    if (name!= null) {
                        boolean store = plugin.storeName(player,x,y,z,args[1]);
                        if (store == true) {
                            player.sendMessage(ChatColor.AQUA + "Registered this TravelPad with the name "+args[1]);
                        }
                        else {
                            player.sendMessage(ChatColor.AQUA + "That is not your TravelPad to register!");
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.AQUA + "You are not standing on a registered Travel Pad"); 
                    }
                 }
                    else {
                        player.sendMessage(ChatColor.AQUA + "Usage: /travelpad Name [name]");
                    }   
            }
                else if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length == 1)
                            {
                                Location location = player.getLocation(); 
                                final int x = (int)location.getX();
                                final int y = (int)location.getX();
                                final int z = (int)location.getX();  
                                String name = plugin.searchPlayerPortal(x, y, z);
                                if (name != null)
                               {
                               String safenick = player.getName();
                               if (!name.equalsIgnoreCase(safenick))
                               {
                                   player.sendMessage(ChatColor.AQUA + "That portal is not registered to you!");
                               }
                               else
                               {
                                   plugin.removePortal(player,x,y,z);
                                   player.sendMessage(ChatColor.AQUA + "TravelPad unregistered.");
                               }
                          }
                    }
                }
            }
                
            return true;
        }
    }

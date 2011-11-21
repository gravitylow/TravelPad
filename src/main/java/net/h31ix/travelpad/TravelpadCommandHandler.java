package net.h31ix.travelpad;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
                    int y = (int)location.getY()-1;
                    int z = (int)location.getZ()-1;
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
                    int y = (int)location.getY()-1;
                    int z = (int)location.getZ()-1;
                    World world = player.getWorld();
                    String name = plugin.searchPlayerPortal(x,y,z);
                    System.out.println(x+" "+y+" "+z);
                    if (name!= null) {
                        boolean store = plugin.storeName(player,x,y,z,args[1],world);
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
                                int x = (int)location.getX();
                                int y = (int)location.getY()-1;
                                int z = (int)location.getZ()-1;
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
                else if (args[0].equalsIgnoreCase("tp")) {
                    Player tpplayer = (Player)cs;
                    if (args.length == 2)
                    {
                    String to = args[1];
                    int x = plugin.searchNameX(to);
                    int y = plugin.searchNameY(to);
                    int z = plugin.searchNameZ(to);
                    if (x!=0 && y!=0 && z!=0)
                    {
                        Location plocation = player.getLocation(); 
                        int xx = (int)plocation.getX();
                        int yy = (int)plocation.getY()-1;
                        int zz = (int)plocation.getZ()-1;
                        String name = plugin.searchPlayerPortal(xx, yy, zz);
                        if (name != null)
                        {
                        Inventory inv = player.getInventory();
                        ItemStack item = new ItemStack(Material.ENDER_PEARL, 1);
                        if (inv.contains(item))
                        {
                        inv.remove(item);
                        World world = plugin.getWorld(to);
                        Location location = new Location(world,x,(y+1),z);
                        tpplayer.teleport(location);
                        player.sendMessage(ChatColor.AQUA + "Woosh! You have arrived at "+to+". The price of your trip was 1 Enderman Pearl.");
                        }
                        else 
                            {
                                player.sendMessage(ChatColor.AQUA + "You must have 1 Enderman Pearl in your inventory to teleport!");
                            }                        
                        }
                        else
                        {
                        player.sendMessage(ChatColor.AQUA + "You must be on a travel pad to use that command!");    
                        }
                    }
                    }
                    else
                    {
                    player.sendMessage(ChatColor.AQUA + "Usage: /travelpad tp [name]");    
                    }
                }
            }
                
            return true;
        }
    }

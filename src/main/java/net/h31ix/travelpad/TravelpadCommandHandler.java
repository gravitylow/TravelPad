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
                   player.sendMessage(ChatColor.RED + "Commands:"); 
                   player.sendMessage(ChatColor.GREEN + "/travelpad Identify");
                   player.sendMessage(ChatColor.BLUE + "Identifies the current pad you are standing on.");
                   player.sendMessage(ChatColor.GREEN + "/travelpad Name [name]");
                   player.sendMessage(ChatColor.BLUE + "Names your created pad.");
                   player.sendMessage(ChatColor.GREEN + "/travelpad tp [name]");
                   player.sendMessage(ChatColor.BLUE + "Teleports your player to the specified travelpad."); 
                   player.sendMessage(ChatColor.GREEN + "/travelpad delete");
                   player.sendMessage(ChatColor.BLUE + "Deletes the travelpad you are standing on, if its yours.");                     
                }
                else if (args[0].equalsIgnoreCase("identify")) {
                    boolean perm = plugin.hasPermission(player, "identify");
                    if (perm == true)
                    {
                    Location location = player.getLocation();
                    int x = (int)location.getX();
                    int y = (int)location.getY();
                    int z = (int)location.getZ();
                    String name = plugin.searchPortalByCoords(x, y, z);
                    if (name!= null) {
                        player.sendMessage(ChatColor.GREEN + "You are standing on the portal named "+name);
                    }
                    else {
                        player.sendMessage(ChatColor.GREEN + "You are not standing on a registered TravelPad"); 
                    }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED + "You dont have that permission.");
                    }
                }
                else if (args[0].equalsIgnoreCase("name")) { 
                    if (args.length == 2)
                    {
                    boolean perm = plugin.hasPermission(player, "name");
                    if (perm == true)
                    {
                    if (plugin.hasPortal(player) == true)
                    {
                    if (plugin.isNamed(player) == true) {
                        boolean store = plugin.storeName(player,args[1]);
                        if (store == true) {
                            player.sendMessage(ChatColor.GREEN + "Registered this TravelPad with the name "+args[1]);
                        }
                        else {
                            player.sendMessage(ChatColor.GREEN + "That is not your TravelPad to register!");
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.GREEN + "You are not standing on a registered Travel Pad"); 
                    }
                }
            }
                else
                {
                    player.sendMessage(ChatColor.RED + "You dont have that permission.");
                }
            }
                    else {
                        player.sendMessage(ChatColor.GREEN + "Usage: /travelpad Name [name]");
                    }
            }
                else if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length == 1)
                            {
                                Location location = player.getLocation(); 
                                int x = (int)location.getX();
                                int y = (int)location.getY();
                                int z = (int)location.getZ();
                                String onportal = plugin.searchPortalByCoords(x, y, z);
                                int xx = plugin.getCoordsX(onportal);
                                int yy = plugin.getCoordsY(onportal);
                                int zz = plugin.getCoordsZ(onportal);
                                if (onportal != null)
                                {
                               String safenick = player.getName();
                               if (!(plugin.getOwner(onportal)).equalsIgnoreCase(safenick))
                               {
                                   player.sendMessage(ChatColor.RED + "That portal is not registered to you!");
                               }
                               else
                               {
                                   boolean perm = plugin.hasPermission(player, "delete");
                                   if (perm == true)
                                   {
                                   plugin.removePortal(onportal);
                                   Location newloc = new Location(player.getWorld(),xx,yy,zz);
                                   newloc.getBlock().setType(Material.AIR);                                
                                   player.sendMessage(ChatColor.GREEN + "TravelPad unregistered.");
                                    if (plugin.rc == true)
                                    {
                                            plugin.refund(player);
                                            player.sendMessage(ChatColor.GREEN + "You have been refunded "+ChatColor.WHITE+plugin.returncharge);

                                    }                                     
                                   }
                                   else
                                   {
                                       player.sendMessage(ChatColor.RED + "You dont have that permission.");
                                   }
                               }
                          }
                                else
                                {
                                    player.sendMessage(ChatColor.RED+"It doesn't look like you are on a registered TravelPad!");
                                }
                    }
                    else if (args.length == 2)
                    {
                    String port = args[1];
                    int xx = plugin.getCoordsX(port);
                    int yy = plugin.getCoordsY(port);
                    int zz = plugin.getCoordsZ(port);
                    boolean perm = plugin.hasPermission(player, "delete.all");
                    if (perm == true)
                    {
                    if (xx!=0 && yy!=0 && zz!=0)
                    {
                        Location newloc = new Location(plugin.getWorld(args[1]),xx,yy,zz);
                        plugin.removePortal(port);
                        newloc.getBlock().setType(Material.AIR);                      
                        player.sendMessage(ChatColor.GREEN + "TravelPad unregistered.");                        
                    }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED + "You dont have that permission.");
                    }
                }
           }
                else if (args[0].equalsIgnoreCase("tp")) {
                    Player tpplayer = (Player)cs;
                    if (args.length == 2)
                    {
                    String to = args[1];
                    int x = plugin.getCoordsX(to);
                    int y = plugin.getCoordsY(to);
                    int z = plugin.getCoordsZ(to);
                    if (x!=0 && y!=0 && z!=0)
                    {
                        Location plocation = player.getLocation(); 
                        int xx = (int)plocation.getX();
                        int yy = (int)plocation.getY();
                        int zz = (int)plocation.getZ();
                        String onportal = plugin.searchPortalByCoords(xx, yy, zz);
                        if (onportal != null)
                        {
                        Inventory inv = player.getInventory();
                        ItemStack item = new ItemStack(Material.ENDER_PEARL, 1);
                        World world = plugin.getWorld(to);
                        if (plugin.checkEnderSetting() == true)
                        {
                            if (inv.contains(item))
                            {
                        boolean take = plugin.checkTakeSetting();
                        if (take == true)
                            {
                            inv.remove(item);
                            }
                        if (world!=null)
                        {
                            Location location = new Location(world,x,(y+1),z);
                            tpplayer.teleport(location);
                            player.sendMessage(ChatColor.GREEN + "Woosh! You have arrived at "+to+".");
                            if (take == true)
                            {
                                player.sendMessage(ChatColor.GREEN + "The price of your trip was 1 Enderman Pearl.");
                            }
                        }
                        }
                        else 
                            {
                                player.sendMessage(ChatColor.GREEN + "You must have 1 Enderman Pearl in your inventory to teleport!");
                            }                        
                        }
                        else
                        {
                           Location location = new Location(world,x,(y+1),z);
                            tpplayer.teleport(location);
                            player.sendMessage(ChatColor.GREEN + "Woosh! You have arrived at "+to+".");                            
                        }
                        }
                        else
                        {
                        player.sendMessage(ChatColor.GREEN + "You must be on a travel pad to use that command!");    
                        }
                    }
                    else
                        {
                        player.sendMessage(ChatColor.GREEN + "That TravelPad doesn't exist!");    
                        }
                    }
                    else
                    {
                    player.sendMessage(ChatColor.GREEN + "Usage: /travelpad tp [name]");    
                    }
                }
            }
                
            return true;
        }
    }

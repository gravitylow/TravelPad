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
                   player.sendMessage(ChatColor.AQUA + "/travelpad Identify");
                   player.sendMessage(ChatColor.BLUE + "Identifies the current pad you are standing on.");
                   player.sendMessage(ChatColor.AQUA + "/travelpad Name [name]");
                   player.sendMessage(ChatColor.BLUE + "Names the current pad you are standing on.");
                   player.sendMessage(ChatColor.AQUA + "/travelpad tp [name]");
                   player.sendMessage(ChatColor.BLUE + "Teleports your player to the specified travelpad."); 
                   player.sendMessage(ChatColor.AQUA + "/travelpad delete");
                   player.sendMessage(ChatColor.BLUE + "Deletes the travelpad you are standing on, if its yours.");                     
                }
                else if (args[0].equalsIgnoreCase("identify")) {
                    boolean perm = plugin.hasPermission(player, "identify");
                    if (perm == true)
                    {
                    Location location = player.getLocation();
                    int x = (int)location.getX();
                    int y = (int)location.getY()-1;
                    int z = (int)location.getZ();
                    String name = plugin.searchCoords(x,y,z);
                    if (name!= null) {
                        player.sendMessage(ChatColor.AQUA + "You are standing on the portal named "+name);
                    }
                    else {
                        player.sendMessage(ChatColor.AQUA + "You are not standing on a registered TravelPad"); 
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
                    int x = plugin.searchPadX(player);
                    int y = plugin.searchPadY(player);
                    int z = plugin.searchPadZ(player);
                    if (x!=0 && y!=0 && z!= 0)
                    {
                    World world = player.getWorld();
                    String name = plugin.searchPlayerPortal(x,y,z);
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
            }
                else
                {
                    player.sendMessage(ChatColor.RED + "You dont have that permission.");
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
                                int z = (int)location.getZ();
                                String name = plugin.searchPlayerPortal(x, y, z);
                                if (name != null)
                               {
                               String safenick = player.getName();
                               if (!name.equalsIgnoreCase(safenick))
                               {
                                   player.sendMessage(ChatColor.RED + "That portal is not registered to you!");
                               }
                               else
                               {
                                   boolean perm = plugin.hasPermission(player, "delete");
                                   if (perm == true)
                                   {
                                   String tpname = plugin.searchCoords(x, y, z);
                                   System.out.println(tpname);
                                   plugin.removePortal(player,x,y,z);
                                   Location newloc = new Location(player.getWorld(),x,y,z);
                                   newloc.getBlock().setType(Material.AIR);
                                   player.sendMessage(ChatColor.AQUA + "TravelPad unregistered.");
                                   }
                                   else
                                   {
                                       player.sendMessage(ChatColor.RED + "You dont have that permission.");
                                   }
                               }
                          }
                    }
                    else if (args.length == 2)
                    {
                    String port = args[1];
                    int xx = plugin.searchNameX(port);
                    int yy = plugin.searchNameY(port);
                    int zz = plugin.searchNameZ(port);
                    boolean perm = plugin.hasPermission(player, "delete.all");
                    if (perm == true)
                    {
                    if (xx!=0 && yy!=0 && zz!=0)
                    {
                        Location newloc = new Location(plugin.getWorld(args[1]),xx,yy,zz);
                        plugin.removePortal(player,xx,yy,zz);
                        newloc.getBlock().setType(Material.AIR);
                        player.sendMessage(ChatColor.AQUA + "TravelPad unregistered.");
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
                    int x = plugin.searchNameX(to);
                    int y = plugin.searchNameY(to);
                    int z = plugin.searchNameZ(to);
                    if (x!=0 && y!=0 && z!=0)
                    {
                        Location plocation = player.getLocation(); 
                        int xx = (int)plocation.getX();
                        int yy = (int)plocation.getY()-1;
                        int zz = (int)plocation.getZ();
                        String name = plugin.searchPlayerPortal(xx, yy, zz);
                        if (name != null)
                        {
                        Inventory inv = player.getInventory();
                        ItemStack item = new ItemStack(Material.ENDER_PEARL, 1);
                        if (inv.contains(item))
                        {
                        boolean take = plugin.checkEnderSetting();
                        if (take == true)
                        {
                        inv.remove(item);
                        }
                        World world = plugin.getWorld(to);
                        Location location = new Location(world,x,(y+1),z);
                        tpplayer.teleport(location);
                        player.sendMessage(ChatColor.AQUA + "Woosh! You have arrived at "+to+".");
                        if (take == true)
                        {
                        player.sendMessage(ChatColor.AQUA + "The price of your trip was 1 Enderman Pearl.");
                        }
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
                    else
                        {
                        player.sendMessage(ChatColor.AQUA + "That TravelPad doesn't exist!");    
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

package net.h31ix.travelpad.api;

import java.util.ArrayList;
import java.util.List;
import net.h31ix.travelpad.Configuration;
import net.h31ix.travelpad.Globals;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * <p>
 * Gives access to various TravelPad methods of checking and plugin backend work.
 */

public class PadManager {
    List list;
    Globals global = new Globals();
    Configuration config = global.config;
   
    public PadManager()
    {
    }
    
    /**
     * Get the number of pads a certain player has
     *
     * @param  player  Player to check 
     * @return Amount of portals a player has
     */          
    public int getAmount(Player player)
    {
        int amount = 0;
        for (int i=0;i<list.size();i++)
        {
            if(((Pad)list.get(i)).getOwner().equalsIgnoreCase(player.getName()))
            {
                amount++;
            }
        }
        return amount;
    }
    
    public void createPad(final Player owner, final Location location)
    {
        if (list == null)
        {
            list = new ArrayList();
        }
        list.add((int)location.getX()+"/"+(int)location.getY()+"/"+(int)location.getZ()+"/"+location.getWorld().getName()+"/"+owner.getName());
        config.saveUnv(list);
        if (config.getMake() != 0)
        {
            global.charge(owner);
        }
        final Block block = location.getWorld().getBlockAt(location);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("TravelPad"), new Runnable() 
        {
            public void run() 
            {
                isNamed(owner, owner.getWorld(), (int)location.getX(), (int)location.getX(), (int)location.getX());
            }
        },      600L);
        block.getRelative(BlockFace.EAST).setType(Material.STEP);
        block.getRelative(BlockFace.WEST).setType(Material.STEP);
        block.getRelative(BlockFace.NORTH).setType(Material.STEP);
        block.getRelative(BlockFace.SOUTH).setType(Material.STEP);
        block.getRelative(BlockFace.UP).setType(Material.WATER);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("TravelPad"), new Runnable() 
        {
            public void run() 
            {
                block.getRelative(BlockFace.UP).setType(Material.AIR);
            }
        }, 10L);
        owner.sendMessage(ChatColor.GREEN + "You have just created a TravelPad!");
        owner.sendMessage(ChatColor.GREEN + "Plaase use "+ChatColor.WHITE+"/travelpad name [name]"+ChatColor.GREEN+" to name this pad.");                               
    }
    
    /**
     * Get the list of all pads on the server
     *
     * @return List of TravelPads
     */          
    public List getPads()
    {
        return list;
    }
    
    /**
     * Get all the pads that a player has
     *
     * @return The list of pads that a player has
     */          
    public List getPortals(Player player)
    {
        List playerPads = new ArrayList();
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if(pad.getOwner().equalsIgnoreCase(player.getName()))
            {
                playerPads.add(pad);
            }
        }
        return playerPads;
    }
    
    /**
     * Check if a pad name is already in use
     *
     * @return true if the name is valid, false if it is in use.
     */          
    public boolean nameIsValid(String name)
    {
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if (pad.getName().equalsIgnoreCase("name"))
            {
                return false;
            }
        } 
        return true;
    } 
    
    /**
     * Get the pad stored at the specified Location. Note that this refers to the location of the center.
     *
     * @return Pad stored at that location, null if there is not one there.
     */       
    public Pad getPadAt(Location location)
    {
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if (pad.getLocation() == location)
            {
                return pad;
            }
        }
        return null;
    }
    
    /**
     * Get a pad by it's name
     *
     * @return Pad stored by that name, null if there is not one.
     */       
    public Pad getPad(String name)
    {
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if (pad.getName().equalsIgnoreCase("name"))
            {
                return pad;
            }
        } 
        return null;
    }
    
    /**
     * Check if a pad is named, it will be deleted and expired if not.
     *
     * @param  player  The player who owns the portal
     * @param  world  World in which the portal resides
     * @param  player  The player who owns the portal
     * @param  x  X Location of portal
     * @param  y  Y Location of portal
     * @param  z  Z Location of portal
     */      
    public void isNamed(Player player, World world, int x, int y, int z)
    {
        List l = config.getUnv();
        if (l.contains(x+"/"+y+"/"+z+"/"+world.getName()+"/"+player.getName()))
        {
            new PadManager().getPadAt(new Location(world,x,y,z)).delete();
        }
    }      
    
}

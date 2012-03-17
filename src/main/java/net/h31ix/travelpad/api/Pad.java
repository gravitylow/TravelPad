package net.h31ix.travelpad.api;

import java.util.ArrayList;
import java.util.List;
import net.h31ix.travelpad.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * <p>
 * Defines a new TravelPad on the map, this is only used after a pad has a name.
 */

public class Pad {
    
    private Location location = null;
    private String owner = null;
    private String name = null;
    private boolean protect = false;
    List whitelist = null;   
    Configuration config = new Configuration();
    LangManager l = new LangManager();

    public Pad(Location location, String owner, String name, boolean protect)
    {
        this.location = location;
        this.owner = owner;
        this.name = name;
        this.protect = protect;
    }

    /**
     * Get the location of the pad
     *
     * @return  location  Location of the obsidian center of the pad
     */       
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Get the owner of the pad
     *
     * @return owner Player who owns the pad's name
     */         
    public String getOwner()
    {
        return owner;
    }
    
    /**
     * Get the name of the pad
     *
     * @return  name  Name of the pad
     */      
    public String getName()
    {
        return name;
    }
    
    /**
     * Find if the pad is whitelisted or not
     *
     * @return  protect  True if whitelisted, false if not.
     */      
    public boolean isWhitelisted()
    {
        return protect;
    }
    
    /**
     * Get the list of whitelisted players
     *
     * @return  whitelist  List of whitelisted players, null if there is no whitelist.
     */         
    public List getWhitelist()
    {
        return whitelist;
    }
    
    /**
     * (Re)Name the pad
     *
     * @param  name  Name of the pad
     */      
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Set if the pad is whitelisted
     *
     * @param  protect  True if whitelist is on, false if off.
     */        
    public void setWhitelisted(boolean protect)
    {
        this.protect = protect;
    }
    
    /**
     * Set the list of allowed users who can use this pad
     *
     * @param  whitelist  List of whitelisted players
     */        
    public void setWhitelist(List whitelist)
    {
        this.whitelist = whitelist;
    }
    
    /**
     * Add a player to the whitelist
     *
     * @param  player  Player to be added
     */        
    public void addWhitelist(String player)
    {
        if (whitelist == null)
        {
            whitelist = new ArrayList();
        }
        whitelist.add(player);
    }
    
    /**
     * Create a new, named pad.
     */        
    public void create()
    {
        config.removePad(new UnnamedPad(location, Bukkit.getPlayer(owner)));
        config.addPad(this);
    }
    
    /**
     * Remove the portal from existence
     */            
    public void delete()
    {
        config.removePad(this);
        Player player = Bukkit.getPlayer(owner);
        if (player != null)
        {
            player.sendMessage(ChatColor.RED+l.delete_approve()+ChatColor.WHITE+name);
            double returnValue = config.deleteAmount;
            if (returnValue != 0)
            {
                //globals.refund(owner);
            }        
        }
        World world = location.getWorld();
        Block block = world.getBlockAt(location);
        block.setType(Material.AIR);
        block.getRelative(BlockFace.EAST).setType(Material.AIR);
        block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
        block.getRelative(BlockFace.NORTH).setType(Material.AIR);
        block.getRelative(BlockFace.WEST).setType(Material.AIR);   
        ItemStack i = new ItemStack(Material.OBSIDIAN, 1);
        ItemStack e = new ItemStack(Material.BRICK, 4);
        world.dropItemNaturally(block.getLocation(), i);
        world.dropItemNaturally(block.getLocation(), e);     
    }
    
}

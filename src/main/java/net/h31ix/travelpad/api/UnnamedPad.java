package net.h31ix.travelpad.api;

import net.h31ix.travelpad.Configuration;
import net.h31ix.travelpad.Globals;
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
 * Defines a new Unnamed TravelPad on the map, this is only used before a pad is named by the player.
 */

public class UnnamedPad {
    
    private Location location = null;
    private Player owner = null;
    private Globals globals = new Globals();
    private Configuration config = globals.config;

    public UnnamedPad(Location location, Player owner)
    {
        this.location = location;
        this.owner = owner;
        config.load();
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
    public Player getOwner()
    {
        return owner;
    }
    
    /**
     * Remove the portal from existance
     */            
    public void delete()
    {
        owner.sendMessage(ChatColor.RED+"TravelPad expired because it was not named.");
        double returnValue = config.getReturn();
        if (returnValue != 0)
        {
            globals.refund(owner);
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

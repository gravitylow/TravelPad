package net.h31ix.travelpad.api;

import net.h31ix.travelpad.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * <p>
 * Defines a new Unnamed TravelPad on the map, this is only used before a pad is named by the player.
 */

public class UnnamedPad {
    
    private Location location = null;
    private Player owner = null;
    Configuration config = new Configuration();
    LangManager l = new LangManager();

    public UnnamedPad(Location location, Player owner)
    {
        this.location = location;
        this.owner = owner;
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
     * Register the pad as a new, unnamed pad.
     */        
    public void create()
    {
        config.addUnv(this);
        final Server server = Bukkit.getServer();
        final Plugin plugin = server.getPluginManager().getPlugin("TravelPad");
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
        {
            public void run() 
            {
                if(config.isUnv(UnnamedPad.this))
                {
                    delete();
                }
            }
        },      600L);
        final Block block = location.getBlock();
        block.getRelative(BlockFace.EAST).setType(Material.STEP);
        block.getRelative(BlockFace.WEST).setType(Material.STEP);
        block.getRelative(BlockFace.NORTH).setType(Material.STEP);
        block.getRelative(BlockFace.SOUTH).setType(Material.STEP);
        block.getRelative(BlockFace.UP).setType(Material.WATER);
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
        {
            public void run() 
            {
                block.getRelative(BlockFace.UP).setType(Material.AIR);
            }
        }, 10L);
        owner.sendMessage(ChatColor.GREEN + l.create_approve_1());
        owner.sendMessage(ChatColor.GREEN + l.create_approve_2());           
    }
 
    /**
     * Remove the pad "gracefully" (no blocks dropping)
     * This occurs naturally when a pad is named
     */     
    public void name()
    {
        config.removePad(this);
    }
    
    /**
     * Remove the pad from existence 
     * This occurs naturally when a pad is named
     */            
    public void delete()
    {
        config.removePad(this);
        owner.sendMessage(ChatColor.RED+l.pad_expire());      
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


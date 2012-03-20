package net.h31ix.travelpad.api;

import java.util.ArrayList;
import java.util.List;
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

public class TravelPadManager {
    private List<Pad> padList;
    private List<UnnamedPad> unvList;
    public Configuration config = new Configuration();
    final Server server = Bukkit.getServer();
    final Plugin plugin;
    LangManager l = new LangManager();
    
    public TravelPadManager(Plugin plugin)
    {
        this.plugin = plugin;
        padList = config.getPads();
        unvList = config.getUnnamedPads(); 
    }
    /**
     * Update the list of pads
     */        
    public void update()
    {
        padList = config.getPads();
        unvList = config.getUnnamedPads();      
    }
    
    /**
     * Create a new, unnamed pad
     *
     * @param  location  Location of the center of the pad
     * @param  player  Player who should own this pad
     */       
    public void createPad(final Location location, Player player)
    {
        update();
        final UnnamedPad pad = new UnnamedPad(location,player);
        config.addUnv(pad);
        final Player owner = pad.getOwner();
        server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
        {
            public void run() 
            {
                if(config.isUnv(pad))
                {
                    config.removePad(pad);
                    owner.sendMessage(ChatColor.RED+l.pad_expire());      
                    deleteBlocks(location);     
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
        update();
    }
    
    /**
     * Remove an Unnamed Pad
     *
     * @param  pad  UnnamedPad to be deleted
     */     
    public void deleteUnnamedPad(UnnamedPad pad)
    {
        update();
        Location location = pad.getLocation();
        config.removePad(pad);
        pad.getOwner().sendMessage(ChatColor.RED+l.pad_expire()); 
        deleteBlocks(location);
        update();
    }
    
    /**
     * Clean up all the blocks around a pad after it has been deleted
     *
     * @param  location  Location of pad to be destroyed
     */      
    public void deleteBlocks(Location location)
    {
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
    
    /**
     * Change an unnamed pad into a named, operational pad
     *
     * @param  pad  Unnamed pad to be changed
     * @param  name  The name of the pad
     */        
    public void switchPad(UnnamedPad pad, String name)
    {
        config.removePad(pad);
        config.addPad(new Pad(pad.getLocation(), pad.getOwner().getName(), name, false));
        update();
    }
    
    /**
     * Remove a Pad
     *
     * @param  pad  Pad to be deleted
     */       
    public void deletePad(Pad pad)
    {
        update();
        config.removePad(pad);
        Player player = Bukkit.getPlayer(pad.getOwner());
        if (player != null)
        {
            player.sendMessage(ChatColor.RED+l.delete_approve()+" "+ChatColor.WHITE+pad.getName());
            double returnValue = config.deleteAmount;
            if (returnValue != 0)
            {
                //globals.refund(owner);
            }        
        }
        Location location = pad.getLocation();
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
        update();
    }
    
    /**
     * Check if a name is already in use
     *
     * @param  name  Name to be checked
     */       
    public boolean nameIsValid(String name)
    {
        update();
        for(Pad pad : padList)
        {
            if (pad.getName().equalsIgnoreCase(name))
            {
                return false;
            }
        } 
        return true;
    }
    
    /**
     * Get a pad by it's name
     *
     * @param  name  Pad's name to be found
     * @return Pad if found, null if no pad by that name
     */       
    public Pad getPad(String name)
    {
        update();
        for(Pad pad : padList)
        {
            if (pad.getName().equalsIgnoreCase(name))
            {
                return pad;
            }
        }  
        return null;
    }
    
    /**
     * Get a pad by it's location
     *
     * @param  location  Pad's location to be found
     * @return Pad if found, null if no pad at that location
     */      
    public Pad getPadAt(Location location)
    {
        update();
        for(Pad pad : padList)
        {
            int x = (int)pad.getLocation().getX();
            int y = (int)pad.getLocation().getY();
            int z = (int)pad.getLocation().getZ();
            int xx = (int)location.getX();
            int yy = (int)location.getY();
            int zz = (int)location.getZ();            
            if (x == xx && y == yy && z == zz)
            {
                return pad;
            }
        }
        return null;
    }
    
    /**
     * Get an Unnamed Pad by it's location
     *
     * @param  location  Unnamed Pad's location to be found
     * @return Unnamed Pad if found, null if no pad by that name
     */      
    public UnnamedPad getUnnamedPadAt(Location location)
    {
        update();
        for(UnnamedPad pad : unvList)
        {
            int x = (int)pad.getLocation().getX();
            int y = (int)pad.getLocation().getY();
            int z = (int)pad.getLocation().getZ();
            int xx = (int)location.getX();
            int yy = (int)location.getY();
            int zz = (int)location.getZ();            
            if (x == xx && y == yy && z == zz)
            {
                return pad;
            }
        }
        return null;
    }   
    
    /**
     * Get all the pads that a player owns
     *
     * @param  player  Player to search for
     * @return Set of pads that the player owns, null if they have none.
     */      
    public List<Pad> getPadsFrom(Player player)
    {
        update();
        List<Pad> list = new ArrayList<Pad>();
        for(Pad pad : padList)
        {
            if (pad.getOwner().equalsIgnoreCase(player.getName()))
            {
                list.add(pad);
            }
        }
        return list;
    }  
    
    /**
     * Get all the unnamed pads that a player owns
     *
     * @param  player  Player to search for
     * @return Set of unnamed pads that the player owns, null if they have none.
     */        
    public List<UnnamedPad> getUnnamedPadsFrom(Player player)
    {
        update();
        List<UnnamedPad> list = new ArrayList<UnnamedPad>();
        for(UnnamedPad pad : unvList)
        {
            if (pad.getOwner() == player)
            {
                list.add(pad);
            }
        }
        return list;
    }     
    
    /**
     * Get all registered pads
     *
     * @return Set of pads that exists
     */        
    public List<Pad> getPads()
    {
        return padList;
    }
    
    /**
     * Get all unregistered pads
     *
     * @return Set of pads that are awaiting naming
     */       
    public List<UnnamedPad> getUnnamedPads()
    {
        update();
        return unvList;
    }    
    
}

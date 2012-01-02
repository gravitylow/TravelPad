package net.h31ix.travelpad;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class TravelpadBlockListener extends BlockListener {
    private final Travelpad plugin;
    
    public TravelpadBlockListener(Travelpad plugin) {
    this.plugin = plugin;
    } 
     
    @Override
    public void onBlockPlace (BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        Location location = block.getLocation();
        final int x = (int)location.getX();
        final int y = (int)location.getY();
        final int z = (int)location.getZ();        
        if(event.getBlock().getType() == Material.OBSIDIAN)
        {
            if (block.getRelative(BlockFace.EAST).getType() == Material.BRICK && block.getRelative(BlockFace.WEST).getType() == Material.BRICK && block.getRelative(BlockFace.NORTH).getType() == Material.BRICK && block.getRelative(BlockFace.SOUTH).getType() == Material.BRICK)
            {
                boolean perm = plugin.hasPermission(player, "create");
                if (perm == true)
                {
                if (plugin.checkPad("SELECT * FROM "+plugin.dbName()+" WHERE player='"+player.getName()+"'", player) != true)
                {
                plugin.addPad("INSERT INTO "+plugin.dbName()+" (id, player, x, y, z, name, world) VALUES ('0', '"+player.getName()+"', '"+x+"', '"+y+"', '"+z+"','NULL', '"+player.getWorld().getName()+"')");
                        
                //plugin.createPad(player, x, y, z);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    plugin.checkNamed(player);
                    }
                },      600L);
                block.getRelative(BlockFace.EAST).setType(Material.STEP);
                block.getRelative(BlockFace.WEST).setType(Material.STEP);
                block.getRelative(BlockFace.NORTH).setType(Material.STEP);
                block.getRelative(BlockFace.SOUTH).setType(Material.STEP);
                block.getRelative(BlockFace.UP).setType(Material.WATER);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    block.getRelative(BlockFace.UP).setType(Material.AIR);
                    }
                }, 10L);
                player.sendMessage(ChatColor.AQUA + "You have just created a TravelPad!");
                player.sendMessage(ChatColor.BLUE + "You must name this travel pad before it can be used.");
                player.sendMessage(ChatColor.BLUE + "To name it, type "+ChatColor.GREEN +"/travelpad name [name]");
                player.sendMessage(ChatColor.BLUE + "If you do not register it within 30 seconds it will be deleted!");
                }
                else
                {
                player.sendMessage(ChatColor.AQUA + "Sorry, but you already have a TravelPad.");
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
            String name = plugin.searchPortalByCoords(x, y, z);
            if (name!= null)
                {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.AQUA + "You cannot place blocks on a travelpad!");
                }
        }
    }
    
    @Override
    public void onBlockBreak (BlockBreakEvent event) {
           Player player = event.getPlayer();
           Location location = event.getBlock().getLocation(); 
           final int x = (int)location.getX();
           final int y = (int)location.getY();
           final int z = (int)location.getZ();  
        if(event.getBlock().getType() == Material.OBSIDIAN)
        {
           String name = plugin.searchPortalByCoords(x, y, z);
           String owner = plugin.getOwner(name);
           if (name != null)
           {
           String safenick = player.getName();
           if (!owner.equalsIgnoreCase(safenick))
           {
               event.setCancelled(true);
               player.sendMessage(ChatColor.AQUA + "That portal is not registered to you!");
               }
           else
           {
               plugin.removePortal(name);
               player.sendMessage(ChatColor.AQUA + "TravelPad unregistered.");
           }
               }
        }
        else if(event.getBlock().getType() == Material.STEP)
        {
        String name = plugin.searchPortalByCoords(x, y, z);    
        if (name != null)
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.AQUA + "You cannot break portals without unregistering them first.");
            player.sendMessage(ChatColor.AQUA + "To unregister a portal, stand on it and type /travelpad delete");
            player.sendMessage(ChatColor.AQUA + "Or simply break the obsidian center.");
        }
        }
    }
}


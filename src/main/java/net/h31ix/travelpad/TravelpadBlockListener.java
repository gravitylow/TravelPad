package net.h31ix.travelpad;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class TravelpadBlockListener implements Listener {
    Main plugin;

    public TravelpadBlockListener(Main plugin) 
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        Player player = event.getPlayer();        
        if (block.getType() == Material.OBSIDIAN)
        {
            String owner = plugin.checkPortal(block.getLocation());
            if (owner != null)
            {
                if (player.getName().equals(owner))
                {
                    String name = plugin.getPortal(block.getLocation());
                    if (name != null)
                    {
                        plugin.removePad(name);
                        player.sendMessage(ChatColor.GREEN+"Removed your TravelPad!");
                    }
                }
                else
                {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED+"That is not your TravelPad!");
                }
            }
        }
        else if (block.getType() == Material.STEP)
        {
            String owner = plugin.getPortal(block.getLocation());
            if (owner != null)
            {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED+"You cannot remove parts of a travelpad!");
                player.sendMessage(ChatColor.RED+"To delete it break the obsidian or type "+ChatColor.WHITE+"/travelpad delete");
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (plugin.getPortal(block.getLocation()) != null)
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED+"You cannot obstruct a TravelPad!");
        }
        else
        {
            if (block.getType() == Material.OBSIDIAN)
            {
                if (block.getRelative(BlockFace.EAST).getType() == Material.BRICK && block.getRelative(BlockFace.WEST).getType() == Material.BRICK && block.getRelative(BlockFace.NORTH).getType() == Material.BRICK && block.getRelative(BlockFace.SOUTH).getType() == Material.BRICK)
                {
                    if (player.hasPermission("travelpad.create"))
                    {
                        if (!plugin.hasPortal(player))
                        {
                            if (plugin.canBuyPortal(player))
                            {
                                final Block b = block;
                                final Player p = player;
                                plugin.addPad(player,block);
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
                                {
                                    public void run() 
                                    {
                                        plugin.isNamed(p, p.getWorld(), (int)b.getLocation().getX(), (int)b.getLocation().getY(), (int)b.getLocation().getZ());
                                    }
                                },      600L);
                                block.getRelative(BlockFace.EAST).setType(Material.STEP);
                                block.getRelative(BlockFace.WEST).setType(Material.STEP);
                                block.getRelative(BlockFace.NORTH).setType(Material.STEP);
                                block.getRelative(BlockFace.SOUTH).setType(Material.STEP);
                                block.getRelative(BlockFace.UP).setType(Material.WATER);
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() 
                                {
                                    public void run() 
                                    {
                                        b.getRelative(BlockFace.UP).setType(Material.AIR);
                                    }
                                }, 10L);
                                player.sendMessage(ChatColor.GREEN + "You have just created a TravelPad!");
                                player.sendMessage(ChatColor.GREEN + "Plaase use "+ChatColor.WHITE+"/travelpad name [name]"+ChatColor.GREEN+" to name this pad.");                       
                            }
                            else
                            {
                                player.sendMessage(ChatColor.RED+"You cannot afford that.");
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.RED+"You already have a pad! Delete it with "+ChatColor.WHITE+"/travelpad delete");
                        }
                    }
                    else
                    {
                        player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
                    }
                }
            }
        }
    } 
}

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

public class TravelpadBlockListener extends BlockListener {
    private final Travelpad plugin;
    
    public TravelpadBlockListener(Travelpad plugin) {
    this.plugin = plugin;
    } 
     
    @Override
    public void onBlockPlace (BlockPlaceEvent event) {
        if(event.getBlock().getType() == Material.OBSIDIAN)
        {
            final Block block = event.getBlock();
            if (block.getRelative(BlockFace.EAST).getType() == Material.BRICK && block.getRelative(BlockFace.WEST).getType() == Material.BRICK && block.getRelative(BlockFace.NORTH).getType() == Material.BRICK && block.getRelative(BlockFace.SOUTH).getType() == Material.BRICK)
            {
                final Player player = event.getPlayer();
                if (plugin.searchPads(player) != true)
                {
                Location location = block.getLocation();
                final int x = (int)location.getX();
                final int y = (int)location.getX();
                final int z = (int)location.getX();
                plugin.createPad(player, x, y, z);
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    plugin.checkNamed(player,x,y,z);
                    }
                },  0x3e8L);
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
                System.out.println(player.getName() + " created a travel pad at "+block.getLocation());
                player.sendMessage(ChatColor.AQUA + "You have just created a TravelPad!");
                player.sendMessage(ChatColor.BLUE + "You must name this travel pad before it can be used.");
                player.sendMessage(ChatColor.BLUE + "To name it, stand on top of the obsidian center and type ");
                player.sendMessage(ChatColor.GREEN + "/travelpad name [name]");
                }
                else
                {
                player.sendMessage(ChatColor.AQUA + "Sorry, but you already have a TravelPad.");
                }
            }
        }
    }
    
    @Override
    public void onBlockBreak (BlockBreakEvent event) {
        Block block = event.getBlock();
        if(event.getBlock().getType() == Material.OBSIDIAN)
        {
           Location location = event.getBlock().getLocation(); 
        }
    }
 }


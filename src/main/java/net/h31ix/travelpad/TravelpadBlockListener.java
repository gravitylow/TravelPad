package net.h31ix.travelpad;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TravelPadBlockListener implements Listener {
    
    private Travelpad plugin;
    
    public TravelPadBlockListener(Travelpad plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlayer(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        if (block.getType() == Material.OBSIDIAN)
        {
            if (block.getRelative(BlockFace.EAST).getType() == Material.BRICK && block.getRelative(BlockFace.WEST).getType() == Material.BRICK && block.getRelative(BlockFace.NORTH).getType() == Material.BRICK && block.getRelative(BlockFace.SOUTH).getType() == Material.BRICK)
            {
                Player player = event.getPlayer();
                if (plugin.canCreate(player))
                {
                    plugin.manager.createPad(block.getLocation(), player);
                }
            }
        }
    }
    
}

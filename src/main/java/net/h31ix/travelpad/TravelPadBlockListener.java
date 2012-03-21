package net.h31ix.travelpad;

import net.h31ix.travelpad.api.Pad;
import net.h31ix.travelpad.api.TravelPadManager;
import net.h31ix.travelpad.api.UnnamedPad;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TravelPadBlockListener implements Listener {
    
    private Travelpad plugin;
    private TravelPadManager manager;
    private LangManager l;
    
    public TravelPadBlockListener(Travelpad plugin)
    {
        this.plugin = plugin;
        manager = plugin.manager;
        l = plugin.l;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.OBSIDIAN)
            {
                if (block.getRelative(BlockFace.EAST).getType() == Material.BRICK && block.getRelative(BlockFace.WEST).getType() == Material.BRICK && block.getRelative(BlockFace.NORTH).getType() == Material.BRICK && block.getRelative(BlockFace.SOUTH).getType() == Material.BRICK)
                {
                    Player player = event.getPlayer();
                    if (plugin.canCreate(player))
                    {
                        plugin.create(block.getLocation(), player);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlock();
        Location location = block.getLocation();
        Pad cpad = plugin.getPadAt(location);
        UnnamedPad upad = plugin.getUnnamedPadAt(location);
        if (cpad != null || upad != null)
        {
            event.setCancelled(true);
        }      
        else
        {
            if (block.getType() == Material.OBSIDIAN)
            {
                if (block.getRelative(BlockFace.EAST).getType() == Material.BRICK && block.getRelative(BlockFace.WEST).getType() == Material.BRICK && block.getRelative(BlockFace.NORTH).getType() == Material.BRICK && block.getRelative(BlockFace.SOUTH).getType() == Material.BRICK)
                {
                    Player player = event.getPlayer();
                    if (plugin.canCreate(player))
                    {
                        plugin.create(block.getLocation(), player);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() == Material.OBSIDIAN)
        {
            Pad pad = manager.getPadAt(block.getLocation());
            UnnamedPad upad = plugin.getUnnamedPadAt(block.getLocation());
            if (pad != null)
            {
                if (pad.getOwner().equalsIgnoreCase(player.getName()))
                {
                    plugin.delete(pad);
                }
                else
                {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED+l.command_deny_permission());
                }
            }
            else if (upad != null)
            {
                event.setCancelled(true);
            }
        }
        else
        {
            Location location = block.getLocation();
            Pad pad = plugin.getPadAt(location);
            UnnamedPad upad = plugin.getUnnamedPadAt(location);
            if (pad != null || upad != null)
            {
                event.setCancelled(true);
            }
        }
    }
    
}

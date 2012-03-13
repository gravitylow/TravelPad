package net.h31ix.travelpad.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * <p>
 * Called when a player makes the shape of a pad
 */

public class PadCreationAttemptEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Location location;
    private boolean cancel;
    
    /**
     * Called when a player makes the shape of a pad, this may not actually create a travelpad if:
     * <p> 
     * The player does not have permission, the player does not have enough money, or the player has reached their max portal limit.
     */         
    public PadCreationAttemptEvent(Player player, Location location)
    {
        this.player = player;
        this.location = location;
    }
    
    /**
     * Get the player who triggered the event
     *
     * @return Player who built the pad
     */         
    public Player getPlayer()
    {
        return player;
    }
    
    /**
     * Get the location where the obsidian center was placed to form a pad
     *
     * @return Location of portal
     */         
    public Location getLocation()
    {
        return location;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }    
    
}

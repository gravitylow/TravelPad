package net.h31ix.travelpad.event;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * <p>
 * Called when a player successfully triggers a new Pad creation
 */

public class PadCreationEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Location location;
    private boolean cancel;
    private double cost = 0;
    private Item item = null;
    
    public PadCreationEvent(Player player, Location location, double cost, Item item)
    {
        this.player = player;
        this.location = location;
        this.cost = cost;
        this.item = item;
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
    
    /**
     * Get the charge of the portal creation
     *
     * @return Charge of creation, 0 if no charge.
     */         
    public double getCost()
    {
        return cost;
    }
    
    /**
     * Set the cost of the portal creation
     *
     * @param  cost  Cost of creation
     */         
    public void setCost(double cost)
    {
        this.cost = cost;
    }
    
    /**
     * Get item required for the player to be teleported
     *
     * @return Item required, null if no item is required.
     */         
    public Item getItem()
    {
        return item;
    }
    
    /**
     * Set the item required for the player to be teleported
     *
     * @param  item  Item required for teleportation
     */         
    public void setItem(Item item)
    {
        this.item = item;
    }
    
    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }    
    
}


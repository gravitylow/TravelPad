package net.h31ix.travelpad.event;

import net.h31ix.travelpad.api.Pad;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class TravelPadTeleportEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Pad to;
    private Pad from;
    private boolean cancel;
    private Player player;
 
    public TravelPadTeleportEvent(Pad to, Pad from, Player player) {
        this.to = to;
        this.from = from;
        this.player = player;
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
    
    public Pad getTo()
    {
        return to;
    }
    
    public void setTo(Pad pad)
    {
        this.to = pad;
    } 
    
    public Pad getFrom()
    {
        return from;
    }
    
    public Player getPlayer()
    {
        return player;
    }
}
    
    
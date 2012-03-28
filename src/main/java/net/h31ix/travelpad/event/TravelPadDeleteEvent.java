package net.h31ix.travelpad.event;

import net.h31ix.travelpad.api.Pad;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class TravelPadDeleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Pad pad;
    private boolean cancel;
 
    public TravelPadDeleteEvent(Pad pad) {
        this.pad = pad;
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
    
    public Pad getPad()
    {
        return pad;
    }
}
    
    
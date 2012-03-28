package net.h31ix.travelpad.event;

import net.h31ix.travelpad.api.UnnamedPad;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class TravelPadCreateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private UnnamedPad pad;
    private boolean cancel;
 
    public TravelPadCreateEvent(UnnamedPad pad) {
        this.pad = pad;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    public UnnamedPad getPad()
    {
        return pad;
    }
}
    
    
package net.h31ix.travelpad.event;

import net.h31ix.travelpad.api.UnnamedPad;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class TravelPadNameEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private UnnamedPad pad;
    private boolean cancel;
    private String name;
 
    public TravelPadNameEvent(UnnamedPad pad, String name) {
        this.pad = pad;
        this.name = name;
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
    
    public UnnamedPad getPad()
    {
        return pad;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}
    
    
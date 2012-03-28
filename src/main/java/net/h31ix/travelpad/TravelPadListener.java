package net.h31ix.travelpad;

import net.h31ix.travelpad.api.Configuration;
import net.h31ix.travelpad.event.TravelPadExpireEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TravelPadListener implements Listener {
    
    private Travelpad plugin;
    private Configuration config;
    
    public TravelPadListener(Travelpad plugin)
    {
        this.plugin = plugin;
        config = plugin.config;
    }
    
    @EventHandler
    public void onPadExpire(TravelPadExpireEvent event)
    {
        if (config.economyEnabled)
        {
            plugin.refundNoCreate(event.getPad().getOwner());
        }
    }
}

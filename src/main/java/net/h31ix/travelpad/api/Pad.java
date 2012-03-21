package net.h31ix.travelpad.api;

import org.bukkit.Location;

/**
 * <p>
 * Defines a new TravelPad on the map, this is only used after a pad has a name.
 */

public class Pad {
    
    private Location location = null;
    private String owner = null;
    private String name = null;

    public Pad(Location location, String owner, String name)
    {
        this.location = location;
        this.owner = owner;
        this.name = name;
    }

    /**
     * Get the location of the pad
     *
     * @return  location  Location of the obsidian center of the pad
     */       
    public Location getLocation()
    {
        return location;
    }

    /**
     * Get the location of the pad that is safe for a player to teleport to
     *
     * @return  location  Safe teleport location
     */     
    public Location getTeleportLocation()
    {
        return new Location(location.getWorld(), location.getX(), location.getY()+2, location.getZ());
    }
    
    /**
     * Get the owner of the pad
     *
     * @return owner Player who owns the pad's name
     */         
    public String getOwner()
    {
        return owner;
    }
    
    /**
     * Get the name of the pad
     *
     * @return  name  Name of the pad
     */      
    public String getName()
    {
        return name;
    }
    
    /**
     * (Re)Name the pad
     *
     * @param  name  Name of the pad
     */      
    public void setName(String name)
    {
        this.name = name;
    }
}

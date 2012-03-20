package net.h31ix.travelpad.api;

import com.massivecraft.factions.Faction;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

/**
 * <p>
 * Defines a new TravelPad on the map, this is only used after a pad has a name.
 */

public class Pad {
    
    private Location location = null;
    private String owner = null;
    private String name = null;
    private boolean protect = false;
    private List<String> whitelist = null;   
    private Faction faction;

    public Pad(Location location, String owner, String name, boolean protect)
    {
        this.location = location;
        this.owner = owner;
        this.name = name;
        this.protect = protect;
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
     * Find if the pad is whitelisted or not
     *
     * @return  protect  True if whitelisted, false if not.
     */      
    public boolean isWhitelisted()
    {
        return protect;
    }
    
    /**
     * Get the list of whitelisted players
     *
     * @return  whitelist  List of whitelisted players, null if there is no whitelist.
     */         
    public List getWhitelist()
    {
        return whitelist;
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
    
    /**
     * Set if the pad is whitelisted
     *
     * @param  protect  True if whitelist is on, false if off.
     */        
    public void setWhitelisted(boolean protect)
    {
        this.protect = protect;
    }
    
    /**
     * Set the list of allowed users who can use this pad
     *
     * @param  whitelist  List of whitelisted players
     */        
    public void setWhitelist(List whitelist)
    {
        this.whitelist = whitelist;
    }
    
    /**
     * Add a player to the whitelist
     *
     * @param  player  Player to be added
     */        
    public void addWhitelist(String player)
    {
        if (whitelist == null)
        {
            whitelist = new ArrayList<String>();
        }
        whitelist.add(player);
    }
    
    /**
     * Remove a player from the whitelist
     *
     * @param  player  Player to be removed
     * @return True if player is removed, false if not on list
     */        
    public boolean removeWhitelist(String player)
    {
        if (whitelist == null)
        {
            whitelist = new ArrayList<String>();
        }
        for(String s : whitelist)
        {
            if (s.equalsIgnoreCase(player))
            {
                whitelist.remove(s);
                return true;
            }
        }
        return false;
    } 
    
    /**
     * Find if a player is on the whitelist.
     *
     * @param  player  Player to be checked
     * @return True if whitelisted, false if not.
     */     
    public boolean isOnWhitelist(String player)
    {
        for(String s : whitelist)
        {
            if (s.equalsIgnoreCase(player))
            {
                return true;
            }
        }
        return false;
    }
    
    public void setFaction(Faction faction)
    {
        this.faction = faction;
    }
    
    public Faction getFaction()
    {
        return faction;
    }
}

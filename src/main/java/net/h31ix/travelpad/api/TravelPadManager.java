package net.h31ix.travelpad.api;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TravelPadManager {
    private Pad[] padList;
    private UnnamedPad[] unvList;
    private Configuration config = new Configuration();
    
    public TravelPadManager()
    {
        padList = config.getPads();
        unvList = config.getUnnamedPads();     
    }
    /**
     * Update the list of pads
     */        
    public void update()
    {
        padList = config.getPads();
        unvList = config.getUnnamedPads();       
        if (padList == null)
        {
            padList = new Pad[0];
        }
        if (unvList == null)
        {
            unvList = new UnnamedPad[0];
        }        
    }
    
    /**
     * Create a new, unnamed pad
     *
     * @param  location  Location of the center of the pad
     * @param  player  Player who should own this pad
     */       
    public void createPad(Location location, Player player)
    {
        update();
        new UnnamedPad(location,player).create();
    }
    
    /**
     * Remove an Unnamed Pad
     *
     * @param  pad  UnnamedPad to be deleted
     */     
    public void deletePad(UnnamedPad pad)
    {
        update();
        pad.delete();
    }
    
    /**
     * Remove a Pad
     *
     * @param  pad  Pad to be deleted
     */       
    public void deletePad(Pad pad)
    {
        update();
        pad.delete();
    }
    
    /**
     * Check if a name is already in use
     *
     * @param  name  Name to be checked
     */       
    public boolean nameIsValid(String name)
    {
        update();
        for(Pad pad : padList)
        {
            if (pad.getName().equalsIgnoreCase(name))
            {
                return false;
            }
        } 
        return true;
    }
    
    /**
     * Get a pad by it's name
     *
     * @param  name  Pad's name to be found
     * @return Pad if found, null if no pad by that name
     */       
    public Pad getPad(String name)
    {
        update();
        for(Pad pad : padList)
        {
            if (pad.getName().equalsIgnoreCase(name))
            {
                return pad;
            }
        }  
        return null;
    }
    
    /**
     * Get a pad by it's location
     *
     * @param  location  Pad's location to be found
     * @return Pad if found, null if no pad at that location
     */      
    public Pad getPadAt(Location location)
    {
        update();
        for(Pad pad : padList)
        {
            if (pad.getLocation() == location)
            {
                return pad;
            }
        }
        return null;
    }
    
    /**
     * Get an Unnamed Pad by it's location
     *
     * @param  location  Unnamed Pad's location to be found
     * @return Unnamed Pad if found, null if no pad by that name
     */      
    public UnnamedPad getUnnamedPadAt(Location location)
    {
        update();
        for(UnnamedPad pad : unvList)
        {
            if (pad.getLocation() == location)
            {
                return pad;
            }
        }
        return null;
    }   
    
    /**
     * Get all the pads that a player owns
     *
     * @param  player  Player to search for
     * @return Set of pads that the player owns, null if they have none.
     */      
    public Set<Pad> getPadsFrom(Player player)
    {
        update();
        Set<Pad> set = new HashSet<Pad>();
        for(Pad pad : padList)
        {
            if (pad.getOwner().equalsIgnoreCase(player.getName()))
            {
                set.add(pad);
            }
        }
        return set;
    }  
    
    /**
     * Get all the unnamed pads that a player owns
     *
     * @param  player  Player to search for
     * @return Set of unnamed pads that the player owns, null if they have none.
     */        
    public Set<UnnamedPad> getUnnamedPadsFrom(Player player)
    {
        update();
        Set<UnnamedPad> set = new HashSet<UnnamedPad>();
        for(UnnamedPad pad : unvList)
        {
            if (pad.getOwner() == player)
            {
                set.add(pad);
            }
        }
        return set;
    }     
    
    /**
     * Get all registered pads
     *
     * @return Set of pads that exists
     */        
    public Set<Pad> getPads()
    {
        update();
        Set<Pad> pads = new HashSet<Pad>();
        for(Pad pad : padList)
        {
            pads.add(pad);
        }
        return pads;
    }
    
    /**
     * Get all unregistered pads
     *
     * @return Set of pads that are awaiting naming
     */       
    public Set<UnnamedPad> getUnnamedPads()
    {
        update();
        Set<UnnamedPad> pads = new HashSet<UnnamedPad>();
        for(UnnamedPad pad : unvList)
        {
            pads.add(pad);
        }
        return pads;
    }    
    
}

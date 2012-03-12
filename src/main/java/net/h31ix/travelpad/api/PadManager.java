package net.h31ix.travelpad.api;

import java.util.ArrayList;
import java.util.List;
import net.h31ix.travelpad.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PadManager {
    List list;
    
    /**
     * Initialize the PadManager
     *
     * @param plugin The TravelPad plugin
     */          
    public PadManager(Main plugin)
    {
        this.list = plugin.padList;
    }
    
    /**
     * Get the number of pads a certain player has
     *
     * @param  player  Player to check 
     * @return Amount of portals a player has
     */          
    public int getAmount(Player player)
    {
        int amount = 0;
        for (int i=0;i<list.size();i++)
        {
            if(((Pad)list.get(i)).getOwner().equalsIgnoreCase(player.getName()))
            {
                amount++;
            }
        }
        return amount;
    }
    
    /**
     * Get the list of all pads on the server
     *
     * @return List of TravelPads
     */          
    public List getPads()
    {
        return list;
    }
    
    /**
     * Get all the pads that a player has
     *
     * @return The list of pads that a player has
     */          
    public List getPortals(Player player)
    {
        List playerPads = new ArrayList();
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if(pad.getOwner().equalsIgnoreCase(player.getName()))
            {
                playerPads.add(pad);
            }
        }
        return playerPads;
    }
    
    /**
     * Check if a pad name is already in use
     *
     * @return true if the name is valid, false if it is in use.
     */          
    public boolean nameIsValid(String name)
    {
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if (pad.getName().equalsIgnoreCase("name"))
            {
                return false;
            }
        } 
        return true;
    } 
    
    /**
     * Get the pad stored at the specified Location. Note that this refers to the location of the center.
     *
     * @return Pad stored at that location, null if there is not one there.
     */       
    public Pad getPadAt(Location location)
    {
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if (pad.getLocation() == location)
            {
                return pad;
            }
        }
        return null;
    }
    
    /**
     * Get a pad by it's name
     *
     * @return Pad stored by that name, null if there is not one.
     */       
    public Pad getPad(String name)
    {
        for (int i=0;i<list.size();i++)
        {
            Pad pad = ((Pad)list.get(i));
            if (pad.getName().equalsIgnoreCase("name"))
            {
                return pad;
            }
        } 
        return null;
    }
    
}

package net.h31ix.travelpad.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Configuration {
    
    private File configFile = new File("plugins/TravelPad/config.yml");
    private FileConfiguration config;
    private File padsFile = new File("plugins/TravelPad/pads.yml");
    private FileConfiguration pads;    
    
    public boolean requireItem = false;
    public boolean takeItem = false;
    public int itemID = 0;
    
    public boolean chargeCreate = false;
    public double createAmount = 0;
    public boolean refundDelete = false;
    public double deleteAmount = 0;
    
    public boolean chargeTeleport = false;
    public double teleportAmount = 0;
    
    public boolean economyEnabled = false;
    
    private List<Pad> padList;
    private List<UnnamedPad> unvList;
    
    public Configuration()
    {
        pads = YamlConfiguration.loadConfiguration(padsFile);
        config = YamlConfiguration.loadConfiguration(configFile);  
        load();
        requireItem = config.getBoolean("Teleportation Options.Require item");
        if (requireItem)
        {
            takeItem = config.getBoolean("Teleportation Options.Take item");
            itemID = config.getInt("Teleportation Options.Item ID"); 
        }
        chargeCreate = config.getBoolean("Portal Options.Charge on creation");
        refundDelete = config.getBoolean("Portal Options.Refund on deletion");
        chargeTeleport = config.getBoolean("Teleportation Options.Charge player");
        if (chargeCreate || refundDelete || chargeTeleport)
        {
            economyEnabled = true;
        }
        if (chargeCreate)
        {
            createAmount = config.getDouble("Portal Options.Creation charge"); 
        }
        if (refundDelete)
        {
            deleteAmount = config.getDouble("Portal Options.Deletion return"); 
        }
        if (chargeTeleport)
        {
            teleportAmount = config.getDouble("Teleportation Options.Charge amount");
        }         
    }
    
    public List getPads()
    {
        return padList;
    }
    
    public List getUnnamedPads()
    {
        return unvList;
    }
    
    public int getAllowedPads(Player player)
    {
        if (player.hasPermission("travelpad.infinite"))
        {
            return -1;
        }
        else
        {
            String s = config.getString("Player Options."+player.getName().toLowerCase()+".Max");
            if (s != null)
            {
                return Integer.parseInt(s);
            }
            else
            {
                return 1;
            }
        }
    }
    
    public boolean isUnv(UnnamedPad pad)
    {
        for(UnnamedPad upad : unvList)
        {
            if (upad.getOwner().equals(pad.getOwner()))
            {
                return true;
            }
        }
        return false;
    }
    
    public void addUnv(UnnamedPad pad)
    {
        unvList.add(pad);
        save();
    }
    
    public void addPad(Pad pad)
    {
        padList.add(pad);
        save();
    }
    
    public void removePad(Pad pad)
    {
        List<Pad> tempList = padList;
        Pad found = null;
        for (Pad upad : tempList)
        {
            if (upad.getLocation().equals(pad.getLocation()))
            {
                found = upad;
            }
        }
        padList.remove(found);
        save();
    }
    
    public void removePad(UnnamedPad pad)
    {
        List<UnnamedPad> tempList = unvList;
        UnnamedPad found = null;
        for (UnnamedPad upad : tempList)
        {
            if (upad.getOwner().equals(pad.getOwner()))
            {
                found = upad;
            }
        }
        unvList.remove(found);
        save();
    }
    
    public void load()
    {
        List list = pads.getList("pads");
        padList = new ArrayList<Pad>();
        if (list != null)
        {
            for (int i=0;i<list.size();i++)
            {
                String [] pad = ((String)list.get(i)).split("/");
                String name = pad[0];
                double x = Integer.parseInt(pad[1]);
                double y = Integer.parseInt(pad[2]);
                double z = Integer.parseInt(pad[3]);
                World world = Bukkit.getServer().getWorld(pad[4]);
                String player = pad[5];
                boolean protect = Boolean.parseBoolean(pad[6]);
                Pad pad2 = new Pad(new Location(world,x,y,z),player,name,protect);
                if (protect)
                {
                    pad2.setWhitelist(pads.getList("Pad Options."+pad2.getName()+".Whitelist"));
                }                
                padList.add(pad2);
            }      
        }
        list = pads.getList("unv");
        unvList = new ArrayList<UnnamedPad>();
        if (list != null)
        {
            for (int i=0;i<list.size();i++)
            {
                String [] pad = ((String)list.get(i)).split("/");
                double x = Integer.parseInt(pad[0]);
                double y = Integer.parseInt(pad[1]);
                double z = Integer.parseInt(pad[2]);
                World world = Bukkit.getServer().getWorld(pad[3]);
                String player = pad[4];
                unvList.add(new UnnamedPad(new Location(world,x,y,z),Bukkit.getPlayer(player)));
            }      
        }   
    }
    
    public void save()
    {
        List padListString = new ArrayList<String>();
        for (int i=0;i<padList.size();i++)
        {
            Pad pad = (Pad)padList.get(i);
            Location loc = pad.getLocation();
            padListString.add(pad.getName()+"/"+(int)loc.getX()+"/"+(int)loc.getY()+"/"+(int)loc.getZ()+"/"+loc.getWorld().getName()+"/"+pad.getOwner()+"/"+pad.isWhitelisted());
            if (pad.isWhitelisted())
            {
                pads.set("Pad Options."+pad.getName()+".Whitelist", pad.getWhitelist());
            }
        }
        pads.set("pads", padListString);
        padListString = new ArrayList<String>();
        for (int i=0;i<unvList.size();i++)
        {
            UnnamedPad pad = (UnnamedPad)unvList.get(i);
            Location loc = pad.getLocation();
            padListString.add((int)loc.getX()+"/"+(int)loc.getY()+"/"+(int)loc.getZ()+"/"+loc.getWorld().getName()+"/"+pad.getOwner().getName());
        }
        pads.set("unv", padListString);
        try {
            pads.save(padsFile);
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        load();
    }
}
package net.h31ix.travelpad;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {
    private File configFile = new File("plugins/TravelPad/config.yml");
    private FileConfiguration config;
    private File padsFile = new File("plugins/TravelPad/pads.yml");
    private FileConfiguration pads;   
    private boolean mc;
    private boolean rc;
    private boolean tc;
    private boolean ri;
    private int rii = 0;
    private double mcc = 0;
    private double rcc = 0;
    private double tcc = 0;    
    
    public Configuration()
    {
    }
    
    public void load()
    {
        pads = YamlConfiguration.loadConfiguration(padsFile);
        config = YamlConfiguration.loadConfiguration(configFile);
        ri = config.getBoolean("Teleportation Options.Require item");
        if (ri)
        {
            rii = config.getInt("Teleportation Options.Item ID"); 
        }
        mc = config.getBoolean("Portal Options.Charge on creation");
        rc = config.getBoolean("Portal Options.Refund on deletion");
        tc = config.getBoolean("Teleportation Options.Charge player");
        if (mc)
        {
            mcc = config.getDouble("Portal Options.Creation charge"); 
        }
        if (rc)
        {
            rcc = config.getDouble("Portal Options.Deletion return"); 
        }
        if (tc)
        {
            tcc = config.getDouble("Teleportation Options.Charge amount");
        }        
    }
    
    public List getPads()
    {
        return pads.getList("pads");
    }
    
    public void savePads(List list)
    {
        pads.set("pads", list);
        savePadsFile();
    }
    
    public List getUnv()
    {
        return pads.getList("unv");
    }
    
    public void saveUnv(List list)
    {
        pads.set("unv", list);
        savePadsFile();
    }
    
    public double getMake()
    {
        return mcc;
    }
    
    public double getReturn()
    {
        return rcc;
    } 
    
    public double getTeleport()
    {
        return tcc;
    }  
    
    public int getItem()
    {
        return rii;
    }
    
    private void savePadsFile()
    {
        try {
            pads.save(padsFile);
        } catch (IOException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

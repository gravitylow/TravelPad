package net.h31ix.travelpad.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
    
    public Configuration()
    {
        pads = YamlConfiguration.loadConfiguration(padsFile);
        config = YamlConfiguration.loadConfiguration(configFile);   
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
        List padList = new ArrayList();
        List list = pads.getList("pads");
        for(int i=0;i<list.size();i++)
        {
            String [] pad = ((String)list.get(i)).split("/");
            String name = pad[0];
            double x = Integer.parseInt(pad[1]);
            double y = Integer.parseInt(pad[2]);
            double z = Integer.parseInt(pad[3]);
            World world = Bukkit.getServer().getWorld(pad[4]);
            String player = pad[5];
            padList.add(new Pad(new Location(world,x,y,z),player,name,false));
        }
        return padList;
    }
       
}

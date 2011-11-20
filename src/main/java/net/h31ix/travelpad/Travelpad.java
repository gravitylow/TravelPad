package net.h31ix.travelpad;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class Travelpad extends JavaPlugin {
    private TravelpadBlockListener blockListener = new TravelpadBlockListener(this); 
    private Configuration config;

    public void onDisable() {
        // TODO: Place any custom disable code here.
        System.out.println(this + " is now disabled!");
    }

    public void onEnable() {
    new File("plugins/TravelPad").mkdir();
    File configFile = new File("plugins/TravelPad/config.yml");
	if(!configFile.exists()) {
            try {
            configFile.createNewFile();
		} catch(Exception e) {
            }
        }
    config = getConfiguration();     
    getCommand("travelpad").setExecutor(new TravelpadCommandHandler(this));
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvent(org.bukkit.event.Event.Type.BLOCK_PLACE, blockListener, org.bukkit.event.Event.Priority.Low, this);
    }
    
    public String searchCoords(int x, int y, int z) {
       String name = config.getString("Coordinates."+x+"."+(y-1)+"."+z+".player");
       System.out.println(name);
       return name;
    }    
    
    public boolean storeName(Player player, int x, int y, int z, String name)
    {
        String safenick = player.getName();
        String register = config.getString("Coordinates."+x+"."+(y-1)+"."+z+".player");
        if (register.equalsIgnoreCase(safenick))
        {
            config.setProperty("Names."+name+".x", x);
            config.setProperty("Names."+name+".y", y);
            config.setProperty("Names."+name+".z", z);
            config.save();
            return true; 
        }
        else
        {
            return false;
        }
    }
    public void createPad(Player player, int x, int y, int z) {
        String safenick = player.getName();
        config.setProperty("Player's pads."+safenick+".location.x", x);
        config.setProperty("Player's pads."+safenick+".location.y", y);
        config.setProperty("Player's pads."+safenick+".location.z", z);
        config.setProperty("Coordinates."+x+"."+(y-1)+"."+z+".player", safenick);
        config.save();
    }
    
    public boolean searchPads(Player player) {
       String safenick = player.getName();
       String check = config.getString("Player's pads."+safenick);
       System.out.println(check);
       if (check == null)
       {
           return false;
       }
       else
       {
           return true;
       }
    }
}

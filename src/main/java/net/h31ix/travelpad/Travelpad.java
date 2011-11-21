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
    private boolean named;

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
    pluginManager.registerEvent(org.bukkit.event.Event.Type.BLOCK_BREAK, blockListener, org.bukkit.event.Event.Priority.Low, this);
    }
    
    public String searchCoords(int x, int y, int z) {
       String name = config.getString("Coordinates."+x+"."+(y-1)+"."+z+".name");
       return name;
    }  
    
    public String searchPlayerPortal(int x, int y, int z) {
       String name = config.getString("Coordinates."+x+"."+(y-1)+"."+z+".player");
       return name;
    }    
    
    public void checkNamed(Player player, int x, int y, int z)
    {
        System.out.println("Checking named");
        if (named != true)
        {
         System.out.println("named false");   
        removePortal(player,x,y,z);
        player.sendMessage(ChatColor.AQUA + "Your TravelPad has expired because it was not named.");
        }
        System.out.println("named true");
    }
    
    public void removePortal(Player player, int x, int y, int z)
    {
        String safenick = player.getName();
        String name = config.getString("Coordinates."+x+"."+(y-1)+"."+z+".name");
        if (name != null)
        {
        config.removeProperty("Names."+name);
        }
        config.removeProperty("Player's pads."+safenick);
        config.removeProperty("Coordinates."+x+"."+(y-1)+"."+z+".player");
        config.removeProperty("Coordinates."+x+"."+(y-1)+"."+z);
        config.removeProperty("Coordinates."+x+"."+(y-1));
        config.removeProperty("Coordinates."+x);        
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
            config.setProperty("Coordinates."+x+"."+(y-1)+"."+z+".name", name);
            config.save();
            named = true;
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

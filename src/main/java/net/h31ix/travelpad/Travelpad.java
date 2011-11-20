package net.h31ix.travelpad;

import java.io.File;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvent(org.bukkit.event.Event.Type.BLOCK_PLACE, blockListener, org.bukkit.event.Event.Priority.Low, this);
    }
    
    
    public void createBlock(Player player, Block block) {
        String safenick = player.getName();
        config.setProperty("Player's pads."+safenick+".block", block);
        config.save();
    }    
    
    public void createPad(Player player, Location location) {
        String safenick = player.getName();
        config.setProperty("Player's pads."+safenick+".location.x", location.getX());
        config.setProperty("Player's pads."+safenick+".location.y", location.getY());
        config.setProperty("Player's pads."+safenick+".location.z", location.getZ());
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

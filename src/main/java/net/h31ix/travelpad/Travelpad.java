package net.h31ix.travelpad;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
public class Travelpad extends JavaPlugin {
    private TravelpadBlockListener blockListener = new TravelpadBlockListener(this); 
    private Configuration config;
    private boolean named;
    private File configFile;

    public void onDisable() {
        System.out.println(this + " is now disabled!");
    }

    public void onEnable() {
    new File("plugins/TravelPad").mkdir();
    configFile = new File("plugins/TravelPad/config.yml");
	if(!configFile.exists()) {
            makeConfig();
        }
    config = getConfiguration();     
    getCommand("travelpad").setExecutor(new TravelpadCommandHandler(this));
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvent(org.bukkit.event.Event.Type.BLOCK_PLACE, blockListener, org.bukkit.event.Event.Priority.Low, this);
    pluginManager.registerEvent(org.bukkit.event.Event.Type.BLOCK_BREAK, blockListener, org.bukkit.event.Event.Priority.Low, this);
    }
    
    public void makeConfig() {
    try {
        //Create a new blank config file
        configFile.createNewFile();
        } catch(Exception a) {
            System.out.println("[TravelPad] Generated a new config file");
        }
    config = getConfiguration(); 
    //Set all the config defaults, if they are not already set.
    if (configFile.length()==0) {
        config.setHeader("#TravelPad configuration file");
        config.setProperty("Take ender pearl on tp", "true");
        config.save(); 		
	}
    }
    
    public boolean checkEnderSetting()
    {
        String result = config.getString("Take ender pearl on tp");
        if (result.equalsIgnoreCase("true"))
                {
                    return true;
                }
        else
        {
            return false;
        }
    }
    
    public int searchNameX(String name)
    {
        int x = 0;
        String xstring = config.getString("Names."+name+".x");
        if (xstring != null)
        {
        x = Integer.parseInt(config.getString("Names."+name+".x"));    
        }
        return x;
    }
    
    public int searchNameY(String name)
    {
        int y = 0;
        String ystring = config.getString("Names."+name+".y");
        if (ystring != null)
        {
        y = Integer.parseInt(config.getString("Names."+name+".y"));    
        }
        return y;
    }
    
    public int searchNameZ(String name)
    {
        int z = 0;
        String zstring = config.getString("Names."+name+".z");
        if (zstring != null)
        {
        z = Integer.parseInt(config.getString("Names."+name+".z"));    
        }
        return z;
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
        if (named != true)
        {  
        removePortal(player,x,y,z);
        player.sendMessage(ChatColor.AQUA + "Your TravelPad has expired because it was not named.");
        }
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
    
    public boolean hasPermission(Player player, String permission)
    {
        if (player.hasPermission("travelpad."+permission))
        {
            return true;
        }
        else if (player.isOp())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean storeName(Player player, int x, int y, int z, String name, World world)
    {
        String safenick = player.getName();
        String register = config.getString("Coordinates."+x+"."+(y-1)+"."+z+".player");
        String worldname = world.getName();
        if (register.equalsIgnoreCase(safenick))
        {
            config.setProperty("Names."+name+".x", x);
            config.setProperty("Names."+name+".y", y);
            config.setProperty("Names."+name+".z", z);
            config.setProperty("Names."+name+".world", worldname);
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
    
    public World getWorld(String name) {
        World world = getServer().getWorld(config.getString("Names."+name+".world"));
        return world;
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

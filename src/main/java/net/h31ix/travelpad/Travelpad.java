package net.h31ix.travelpad;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
public class Travelpad extends JavaPlugin {
    private TravelpadBlockListener blockListener = new TravelpadBlockListener(this); 
    private Configuration config;
    private boolean named;
    private File configFile;
    private String host;
    private String user;
    private String pass;
    private String port;
    private String database;
    private String urlfinal;
    private ResultSet rs = null;
    private Statement stmt;
    Connection conn;

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
        config.load();
        host = config.getString("MySQLSettings.Hostname");
        user = config.getString("MySQLSettings.Username");
        pass = config.getString("MySQLSettings.Password");
        port = config.getString("MySQLSettings.Port");
        database = config.getString("MySQLSettings.Database");
        urlfinal = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            conn = DriverManager.getConnection(urlfinal, user, pass);
            System.out.println("[TravelPad] MySQL Connection Made!");
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[TravelPad] MySQL Connection FAILED.");
            pluginManager.disablePlugin(this);
        }
    }
    
    public boolean hasPortal(Player player)
    {
        String playername = null;
           try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE player='"+player.getName()+"'");
            while (rs.next())
            {
            playername = rs.getString("player");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
           if (playername == null)
           {
               return false;
           }
           else
           {
               return true;
           }
    }    
    
    public void addPad(String query)
    {
        try {
            PreparedStatement sampleQueryStatement = conn.prepareStatement(query);
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean checkPad(String query)
    {
        String playername = null;
       try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next())
            {
            playername = rs.getString("player");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
       if (playername != null)
       {
           return true;
       }
       else
       {
           return false;
       }
    }
    
    public void makeConfig() {
    try {
        //Create a new blank config file
        configFile.createNewFile();
        } catch(Exception a) {
            System.out.println("[TravelPad] Error generating a new config file!");
        }
    config = getConfiguration(); 
    //Set all the config defaults, if they are not already set.
    if (configFile.length()==0) {
        config.setHeader("#TravelPad configuration file");
        config.setProperty("Take ender pearl on tp", "true");
        config.setProperty("MySQLSettings.Username", "myusername");
        config.setProperty("MySQLSettings.Password", "mypassword");
        config.setProperty("MySQLSettings.Database", "bans");
        config.setProperty("MySQLSettings.Hostname", "localhost");
        config.setProperty("MySQLSettings.Port", "3306");        
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
    
    public boolean isNamed(Player player)
    {
        String name = null;
        String safenick = player.getName();
        try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE player='"+safenick+"'");
                
                while (rs.next())
                {
                name = rs.getString("player");
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
           if (name == null)
           {
               return false;
           }
           else
           {
               return true;
           }    
    }
    
    public void checkNamed(Player player)
    {
        if (named != true)
        {
            try {
            PreparedStatement sampleQueryStatement = conn.prepareStatement("DELETE FROM TravelPads WHERE player='"+player.getName()+"'");
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
            player.sendMessage("removed");
            } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        named = false;
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
    
    public boolean storeName(Player player, String name)
    {
       String safenick = player.getName();
       String playername = null;
       try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE player='"+safenick+"'");
            while (rs.next())
            {
            playername = rs.getString("player");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (playername.equalsIgnoreCase(player.getName()))
        {
                    try {
            PreparedStatement sampleQueryStatement = conn.prepareStatement("UPDATE TravelPads SET name='"+name+"' WHERE player='"+safenick+"'");
                    
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
            } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            }
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
      
}

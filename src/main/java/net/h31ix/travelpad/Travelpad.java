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
import org.bukkit.block.Block;
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
            System.out.println("[TravelPad] MySQL Connection Established!");
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
    
    public int getCoordsX (String name)
    {
        int x = 0;
        try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE name='"+name+"'");
                
                while (rs.next())
                {
                x = rs.getInt("x");
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return x;
    }
    
    public int getCoordsY (String name)
    {
        int y = 0;
        try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE name='"+name+"'");
                
                while (rs.next())
                {
                y = rs.getInt("y");
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return y;
    }
    
    public int getCoordsZ (String name)
    {
        int z = 0;
        try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE name='"+name+"'");
                
                while (rs.next())
                {
                z = rs.getInt("z");
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return z;
    } 
    
    public String searchPortalByCoords(int x, int y, int z)
    {
        String name = null;
         try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE x BETWEEN '"+(x-2)+"' AND '"+(x+2)+"' AND y BETWEEN '"+(y-2)+"' AND '"+(y+2)+"' AND z BETWEEN '"+(z-2)+"' AND '"+(z+2)+"'");                         
                while (rs.next())
                {
                name = rs.getString("name");
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return name;
    }
    
        public String getOwner(String name)
    {
        String player = null;
         try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE name='"+name+"'");                         
                while (rs.next())
                {
                player = rs.getString("player");
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return player;
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
        int x = 0;
        int y = 0;
        int z = 0;
        World world = null;
                try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE player='"+player.getName()+"'");               
                while (rs.next())
                {
                x = rs.getInt("x");
                y = rs.getInt("y");
                z = rs.getInt("z");
                world = getServer().getWorld(rs.getString("world"));
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
            Location loc = new Location(world,x,y,z);
            Block block = loc.getBlock();
            block.setType(Material.AIR);
            block.getRelative(BlockFace.EAST).setType(Material.AIR);
            block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
            block.getRelative(BlockFace.NORTH).setType(Material.AIR);
            block.getRelative(BlockFace.WEST).setType(Material.AIR);
                
            try {
            PreparedStatement sampleQueryStatement = conn.prepareStatement("DELETE FROM TravelPads WHERE player='"+player.getName()+"'");
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
            player.sendMessage(ChatColor.AQUA + "Your portal has expired because you did not name it!");
            } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        named = false;
    }
    
    public void removePortal(String name)
    {
        int x = 0;
        int y = 0;
        int z = 0;
        World world = null;
                try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE name='"+name+"'");               
                while (rs.next())
                {
                x = rs.getInt("x");
                y = rs.getInt("y");
                z = rs.getInt("z");
                world = getServer().getWorld(rs.getString("world"));
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
            try {
            PreparedStatement sampleQueryStatement = conn.prepareStatement("DELETE FROM TravelPads WHERE name='"+name+"'");
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
            Location loc = new Location(world,x,y,z);
            Block block = loc.getBlock();
            block.getRelative(BlockFace.EAST).setType(Material.AIR);
            block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
            block.getRelative(BlockFace.NORTH).setType(Material.AIR);
            block.getRelative(BlockFace.WEST).setType(Material.AIR);
            } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            }        
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
        String worldname = null;
        World world = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM TravelPads WHERE name='"+name+"'");
            while (rs.next())
            {
            worldname = rs.getString("world");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (worldname != null)
        {
        world = getServer().getWorld(worldname);
        }
        return world;
    }
      
}

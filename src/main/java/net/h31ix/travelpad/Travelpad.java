package net.h31ix.travelpad;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

public class Travelpad extends JavaPlugin {
    private FileConfiguration config;           
    File configFile = new File("plugins/TravelPad/config.yml");
    private boolean named;
    private String host;
    private String user;
    private String pass;
    private String port;
    private String database;
    private String urlfinal;
    private String table;
    private ResultSet rs = null;
    private Statement stmt;
    public double makecharge = 0;    
    public double returncharge = 0;  
    public boolean mc = false;
    public boolean rc = false;
    public static Economy economy = null;
    Connection conn;

    @Override
    public void onDisable() {
        System.out.println(this + " is now disabled!");
    }

    @Override
    public void onEnable() {
    new File("plugins/TravelPad").mkdir();
    if(!configFile.exists()) 
    {
        saveDefaultConfig();
    }
    config = getConfig();    
    if (config.getString("Economy Charges.Creation charge") == null)
    {
        System.out.println("[TravelPad] Your config is out of date. Renaming and replacing it.");
        configFile.renameTo(new File ("plugins/TravelPad/config_old.yml"));
        saveDefaultConfig();
    }
    getCommand("travelpad").setExecutor(new TravelpadCommandHandler(this));
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new TravelpadBlockListener(this), this);
        mc = config.getBoolean("Economy Charges.Charge on creation");
        rc = config.getBoolean("Economy Charges.Refund on deletion");
        makecharge = config.getDouble("Economy Charges.Creation Charge");  
        returncharge = config.getDouble("Economy Charges.Deletion Return");  
        if (mc == true || rc == true)
        {
            if (getServer().getPluginManager().getPlugin("Vault") != null)
            {
                setupEconomy();
            }
            else
            {
                System.out.println("[TravelPad] Economy charges are ON in the config but you don't have Vault");
                System.out.println("[TravelPad] Please download Vault to use TravelPad's economic function.");
                mc = false;
                rc = false;
            }
            if (!economy.hasBankSupport())
            {
                System.out.println("[TravelPad] Vault was found, but no economy plugin was found.");
                mc = false;
                rc = false;
            }
                
        }        
        host = config.getString("MySQLSettings.Hostname");
        user = config.getString("MySQLSettings.Username");
        pass = config.getString("MySQLSettings.Password");
        port = config.getString("MySQLSettings.Port");
        database = config.getString("MySQLSettings.Database");
        table = config.getString("MySQLSettings.Table");
        urlfinal = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            conn = DriverManager.getConnection(urlfinal, user, pass);
            System.out.println("[TravelPad] MySQL Connection Established!");
            final DatabaseMetaData dbm = conn.getMetaData();
            if (!dbm.getTables(null, null, config.getString("MySQLSettings.Table"), null).next()) {
                stmt = conn.createStatement();
                System.out.println("[TravelPad] Creating a table now..");
                stmt.execute("CREATE TABLE  `"+config.getString("MySQLSettings.Table")+"` (`id` INT( 10 ) NOT NULL AUTO_INCREMENT ,`player` VARCHAR( 32 ) NOT NULL ,`x` VARCHAR( 5 ) NOT NULL ,`y` VARCHAR( 5 ) NOT NULL ,`z` VARCHAR( 5 ) NOT NULL ,`name` VARCHAR( 32 ) NOT NULL ,`world` VARCHAR( 32 ) NOT NULL ,PRIMARY KEY (  `id` ));");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[TravelPad] MySQL Connection FAILED.");
            pluginManager.disablePlugin(this);
        }
        System.out.println(this+" is now enabled!");
    }
    
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }    
    
    public String dbName()
    {
        return table;
    }
    
    public boolean hasPortal(Player player)
    {
        String playername = null;
           try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE player='"+player.getName()+"'");
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
    
    public boolean canBuyPortal(Player player)
    {
        double balance = economy.getBalance(player.getName());
        if (balance >= makecharge)
        {           
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void charge(Player player)
    {
        economy.withdrawPlayer(player.getName(), makecharge);
    }
    
    public void refund(Player player)
    {
        economy.depositPlayer(player.getName(), returncharge);
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
    
    public boolean checkPad(String query, Player player)
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
           if (player.hasPermission("travelpad.infinite") || player.isOp())
           {
               return false;
           }
           else {
               return true;
           }
       }
       else
       {
           return false;
       }
    }
    
    public boolean checkTakeSetting()
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
    
    public boolean checkEnderSetting()
    {
        String result = config.getString("Require ender pearl on tp");
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE name='"+name+"'");
                
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE name='"+name+"'");
                
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE name='"+name+"'");
                
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE x BETWEEN "+(x-2)+" AND "+(x+2)+" AND y BETWEEN "+(y-4)+" AND "+(y+4)+" AND z BETWEEN "+(z-2)+" AND "+(z+2)+"");                         
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE name='"+name+"'");                         
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE player='"+safenick+"'");
                
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE player='"+player.getName()+"'");               
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
            PreparedStatement sampleQueryStatement = conn.prepareStatement("DELETE FROM "+table+" WHERE player='"+player.getName()+"'");
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
            player.sendMessage(ChatColor.GREEN + "Your TravelPad has expired because you did not name it!");
                    if (rc == true)
                    {
                            refund(player);
                            player.sendMessage(ChatColor.GREEN + "You have been refunded "+ChatColor.WHITE+returncharge);
                            
                    }                
            ItemStack i = new ItemStack(Material.OBSIDIAN, 1);
            ItemStack e = new ItemStack(Material.BRICK, 4);
            world.dropItemNaturally(loc, i);
            world.dropItemNaturally(loc, e);          
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
                rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE name='"+name+"'");               
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
            PreparedStatement sampleQueryStatement = conn.prepareStatement("DELETE FROM "+table+" WHERE name='"+name+"'");
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
            Location loc = new Location(world,x,y,z);
            Block block = loc.getBlock();
            block.getRelative(BlockFace.EAST).setType(Material.AIR);
            block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
            block.getRelative(BlockFace.NORTH).setType(Material.AIR);
            block.getRelative(BlockFace.WEST).setType(Material.AIR);
            ItemStack i = new ItemStack(Material.OBSIDIAN, 1);
            ItemStack e = new ItemStack(Material.BRICK, 4);
            world.dropItemNaturally(loc, i);
            world.dropItemNaturally(loc, e);             
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
            rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE player='"+safenick+"'");
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
            PreparedStatement sampleQueryStatement = conn.prepareStatement("UPDATE "+table+" SET name='"+name+"' WHERE player='"+safenick+"'");
                    
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
            rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE name='"+name+"'");
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

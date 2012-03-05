package net.h31ix.travelpad;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    private ArrayList portals = new ArrayList();

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
    if (config.getString("Economy Charges.Creation Charge") == null)
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
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS `"+config.getString("MySQLSettings.Table")+"` (`id` INT( 10 ) NOT NULL AUTO_INCREMENT ,`player` VARCHAR( 32 ) NOT NULL ,`x` VARCHAR( 5 ) NOT NULL ,`y` VARCHAR( 5 ) NOT NULL ,`z` VARCHAR( 5 ) NOT NULL ,`name` VARCHAR( 32 ) NOT NULL ,`world` VARCHAR( 32 ) NOT NULL ,PRIMARY KEY (  `id` ));");
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[TravelPad] MySQL Connection FAILED.");
            pluginManager.disablePlugin(this);
        }     
        downloadPortals();        
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public void connect()
    {
            try {
                if (conn.isClosed())
                {
                    try {
                        conn = null;
                        conn = DriverManager.getConnection(urlfinal, user, pass);
                        stmt = conn.createStatement();
                    } catch (SQLException ex) {
                        Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void disconnect()
    {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean hasPortal(Player player)
    {    
        connect();
        String playername = null;
           try {
            rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE player='"+player.getName()+"'");
            while (rs.next())
            {
            playername = rs.getString("player");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
           disconnect();
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
        connect();
        try {
            PreparedStatement sampleQueryStatement = conn.prepareStatement(query);
            sampleQueryStatement.executeUpdate();
            sampleQueryStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
        disconnect();
    }
    
    public boolean checkPad(String query, Player player)
    {
        connect();
        String playername = null;
       try {
            rs = stmt.executeQuery(query);
            while (rs.next())
            {
            playername = rs.getString("player");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
       disconnect();
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
    
    public String getCoords (String name)
    {
        for (int i=0;i!=portals.size();i++)
        {
            String l = (String)portals.get(i);
            String [] l2 = l.split("-");
            String n = l2[3];
            if (name.equalsIgnoreCase(n))
            {
                return l;
            }
        }  
        return null;
    }
    
    public void downloadPortals()
    {
         try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM "+table);                         
                while (rs.next())
                {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String name = rs.getString("name");
                portals.add(x+"-"+y+"-"+z+"-"+name);
                }
        } catch (SQLException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public String searchPortalByCoords(int x, int y, int z)
    {
        String name = null;
        for (int i=0;i!=portals.size();i++)
        {
            String l = (String)portals.get(i);
            String [] l2 = l.split("-");
            int xx = Integer.parseInt(l2[0]);
            int yy = Integer.parseInt(l2[1]);
            int zz = Integer.parseInt(l2[2]);
            if (x <= xx+2 && x >= xx-2 && y <= yy+2 && y >= yy-2 && z <= zz+2 && z >= zz-2)
            {
                name = l2[3];
            }
        }
        return name;        
    }
    
        public String getOwner(String name)
    {
        connect();
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
        disconnect();
        return player;
    }
       
    
    public boolean isNamed(Player player)
    {
        connect();
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
        disconnect();
           if (name == null)
           {
               return false;
           }
           else
           {
               return true;
           }    
    }
    
    public boolean isValidName(String name)
    {
        for (int i=0;i!=portals.size();i++)
        {
            String l = (String)portals.get(i);
            String [] l2 = l.split("-");
            String x = l2[3];
            System.out.println("checking if it is "+x);
            if (x.equalsIgnoreCase(name))
            {
                System.out.println("already");
                return false;
            }
        } 
        return true;
    }
    
    public void checkNamed(Player player)
    {
        connect();
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
            disconnect();
        }
        named = false;
    }
    
    public void removePortal(String name)
    {
        connect();
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
            portals.remove(x+"-"+y+"-"+z+"-"+name);
            disconnect();
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
       connect();
       String safenick = player.getName();
       String playername = null;
       int x = 0;
       int y = 0;
       int z = 0;
       try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE player='"+safenick+"'");
            while (rs.next())
            {
            playername = rs.getString("player");
                x = rs.getInt("x");
                y = rs.getInt("y");
                z = rs.getInt("z");            
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
            disconnect();
            portals.add(x+"-"+y+"-"+z+"-"+name);
            named = true;
            return true; 
        }
        else
        {
            return false;
        }
    }
    
    public World getWorld(String name) {
        connect();
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
        disconnect();
        if (worldname != null)
        {
        world = getServer().getWorld(worldname);
        }
        return world;
    }
      
}

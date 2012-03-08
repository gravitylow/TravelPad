package net.h31ix.travelpad;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private File configFile = new File("plugins/TravelPad/config.yml");
    private FileConfiguration config;
    private File padsFile = new File("plugins/TravelPad/pads.yml");
    private FileConfiguration pads;
    private boolean mc;
    private boolean rc;
    private boolean tc;
    private boolean ri;
    private Economy economy;
    private int rii;
    private double mcc;
    private double rcc;
    private double tcc;
    private List portals;
    private boolean debug = false;
    
    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
    try {
        Metrics metrics = new Metrics(this);
        metrics.start();
    } catch (IOException e) {
        debug("[TravelPad] Issue with Metrics..");
    }       
        
        getCommand("travelpad").setExecutor(new TravelpadCommandHandler(this));
        if (!configFile.exists())
        {
            saveDefaultConfig();
        }
        if (!padsFile.exists())
        {
            try {
                padsFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        pads = YamlConfiguration.loadConfiguration(padsFile);
        config = getConfig();
        portals = pads.getList("pads");
        if (config.getBoolean("Storage Options.Convert"))
        {
            System.out.println("[TravelPad] You are running in convert mode, this will connect to your SQL Database, convert it into flatfile, and then continue with normal operation.");
            System.out.println("[TravelPad] Attempting to establish SQL Connection...");
            convert();
        }
        ri = config.getBoolean("Teleportation Options.Require item");
        if (ri)
        {
            rii = config.getInt("Teleportation Options.Item ID"); 
        }
        mc = config.getBoolean("Portal Options.Charge on creation");
        rc = config.getBoolean("Portal Options.Refund on deletion");
        tc = config.getBoolean("Teleportation Options.Charge player");
        if (mc||rc||tc)
        {
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
                tc = false;
            }
            if (!economy.hasBankSupport())
            {
                System.out.println("[TravelPad] Vault was found, but no economy plugin was found.");
                mc = false;
                rc = false;
                tc = false;
            }              
        }          
        getServer().getPluginManager().registerEvents(new TravelpadBlockListener(this), this);
        portals = pads.getList("pads");
        if (portals == null)
        {
            portals = new ArrayList();
        }
    }
    
    public void convert()
    {
        String host = config.getString("MySQLSettings.Hostname");
        String user = config.getString("MySQLSettings.Username");
        String pass = config.getString("MySQLSettings.Password");
        String port = config.getString("MySQLSettings.Port");
        String database = config.getString("MySQLSettings.Database");
        String table = config.getString("MySQLSettings.Table");
        String urlfinal = "jdbc:mysql://" + host + ":" + port + "/" + database;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(urlfinal, user, pass);
            stmt = conn.createStatement();
            System.out.println("[TravelPad] MySQL Connection Established!");
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[TravelPad] MySQL Connection FAILED.");
            getServer().getPluginManager().disablePlugin(this);
        }         
        try {
            System.out.println("[TravelPad] Now downloading all pads into flatfile...");
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+table+" WHERE 1");
            while (rs.next())
            {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String name = rs.getString("name");
                String world = rs.getString("world");
                String player = rs.getString("player");
                if (portals == null)
                {
                    portals = new ArrayList();
                }
                portals.add(name+"/"+x+"/"+y+"/"+z+"/"+world+"/"+player);
            }          
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        pads.set("pads", portals);
        config.set("Storage Options.Convert", false);
        save();
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("[TravelPad] Done converting! Continuing as normal.");
    }
    
    private void debug(String msg)
    {
        if (debug)
        {
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "[TPDB]: "+msg);
        }
    }
    
    private void save()
    {
        try {
            pads.save(padsFile);
            config.save(configFile);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        debug("Saved files");
    }
    
    public double getRandom()
    {       
        int x = (int)(2*Math.random())+1;
        double e = (4*Math.random())+1;
        if (x == 2)
        {
            e = 0-e;
        }
        return e;
    }
    
    public void teleport(Player player, Location loc)
    {
        boolean tp = true;
        boolean take = false;
        boolean found = false;
        ItemStack s = null;
        if (ri)
        {
            s = new ItemStack(Material.getMaterial(rii), 1);
            for (int i=0;i<player.getInventory().getContents().length;i++)
            {
                if (player.getInventory().getContents()[i] != null)
                {
                    if(player.getInventory().getContents()[i].getType().name().equals(Material.getMaterial(rii).name()))
                    {
                        if (config.getBoolean("Teleportation Options.Take item"))
                        {
                            take = true;
                        } 
                        found = true;
                    }
                }
            }
            if (found == false)
            {
                player.sendMessage(ChatColor.RED+"You must have one "+(Material.getMaterial(rii)).name().toLowerCase().replaceAll("_", "")+" to travel!");
                tp = false;
            }
        }
        if (tc && tp)
        {
            if (canTeleport(player))
            {
                chargeTP(player);  
            }
            else
            {
                player.sendMessage(ChatColor.RED+"You cannot afford that!");
                tp = false;
            }
        }
        if (take && tp)
        {
            player.getInventory().removeItem(s);
            player.sendMessage(ChatColor.GOLD+"One "+(Material.getMaterial(rii)).name().toLowerCase().replaceAll("_", "")+" has been collected from you for travel.");
        }
        if (tp)
        {
            for (int i=0;i!=32;i++)
            {
                player.getWorld().playEffect(player.getLocation().add(getRandom(), getRandom(), getRandom()), Effect.SMOKE, 3);
            }
            player.teleport(loc);      
            player.sendMessage(ChatColor.GREEN+"The wind rushes through your hair...");            
            for (int i=0;i!=32;i++)
            {
                player.getWorld().playEffect(loc.add(getRandom(), getRandom(), getRandom()), Effect.SMOKE, 3);
            }
        }
    }
    
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        debug("Attempted to setup economy");
        return (economy != null);
    }      
    
    public boolean hasPortal(Player player)
    {
        if (player.hasPermission("travelpad.infinite"))
        {
            return false;
        }
        else
        {
            for (int i=0;i<portals.size();i++)
            {
                String currentPad = (String)portals.get(i);
                String [] pad = currentPad.split("/");
                if (pad[5].equalsIgnoreCase(player.getName()))
                {
                    debug("Player "+player.getName()+" does have a portal already");
                    return true;
                }
            }
        return false;
        }    
    }
    
    public String getPlayersPortal(Player player)
    {
        for (int i=0;i<portals.size();i++)
        {
            String currentPad = (String)portals.get(i);
            String [] pad = currentPad.split("/");
            if (pad[5].equalsIgnoreCase(player.getName()))
            {
                return pad[0];
            }
        }
        return null;
    }
    
    public boolean canBuyPortal(Player player)
    {
        if (player.hasPermission("travelpad.nopay"))
        {
            return true;
        }
        else if (mc == false && tc == false && rc == false)
        {
            return true;
        }
        else
        {
            double balance = economy.getBalance(player.getName());
            debug(player.getName()+" has "+balance+" money");
            if (balance >= mcc)
            {           
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    public boolean canTeleport(Player player)
    {
        if (player.hasPermission("travelpad.nopay"))
        {
            return true;
        }
        else if (mc == false && tc == false && rc == false)
        {
            return true;
        }        
        double balance = economy.getBalance(player.getName());
        debug(player.getName()+" has "+balance+" money");
        if (balance >= tcc)
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
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), mcc);
            player.sendMessage(ChatColor.GOLD+"You were charged "+mcc);
            debug("Charged player "+mcc);
        }
    }
    
    public void chargeTP(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), tcc);
            player.sendMessage(ChatColor.GOLD+"You were charged "+mcc);
            debug("Charged player "+tcc);
        }
    }    
    
    public void refund(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {        
            economy.depositPlayer(player.getName(), rcc);
            player.sendMessage(ChatColor.GOLD+"You were refunded "+rcc);
            debug("Charged player "+rcc);
        }
    } 
    
    public boolean nameIsValid(String name)
    {
        for (int i=0;i<portals.size();i++)
        {
            String currentPad = (String)portals.get(i);
            String [] pad = currentPad.split("/");
            if (pad[0].equalsIgnoreCase(name))
            {
                debug("Checking if portal name is "+pad[0]);
                debug("Name is invalid");
                return false;
            }
        } 
        return true;
    }
    
    public String checkPortal(Location loc)
    {
        int x = (int)loc.getX();
        int y = (int)loc.getY();
        int z = (int)loc.getZ();        
        for (int i=0;i<portals.size();i++)
        {
            String currentPad = (String)portals.get(i);
            String [] pad = currentPad.split("/");
            int x1 = Integer.parseInt(pad[1]);
            int y1 = Integer.parseInt(pad[2]);
            int z1 = Integer.parseInt(pad[3]);
            if (x == x1 && y == y1 && z == z1)
            {
                return pad[5];
            }
        }  
        return null;        
    }
    
    public Location getCoords(String name)
    {
        for (int i=0;i<portals.size();i++)
        {
            String currentPad = (String)portals.get(i);
            String [] pad = currentPad.split("/");
            if (name.equals(pad[0]))
            {
                return new Location(getServer().getWorld(pad[4]), Double.parseDouble(pad[1]), Double.parseDouble(pad[2])+1, Double.parseDouble(pad[3]));
            }
        }  
        return null;              
    }
    public String getPortal(Location loc)
    {
        int x = (int)loc.getX();
        int y = (int)loc.getY();
        int z = (int)loc.getZ();   
        if (portals != null)
        {
            for (int i=0;i<portals.size();i++)
            {
                String currentPad = (String)portals.get(i);
                String [] pad = currentPad.split("/");
                int x1 = Integer.parseInt(pad[1]);
                int y1 = Integer.parseInt(pad[2]);
                int z1 = Integer.parseInt(pad[3]);
                if (x1 <= x+2 && x1 >= x-2 && y1 <= y+2 && y1 >= y-2 && z1 <= z+2 && z1 >= z-2)
                {
                    return pad[0];
                }
            }  
        }
        return null;
    }
    
    public boolean doesPadExist(String name)
    {
        for (int i=0;i<portals.size();i++)
        {
            String currentPad = (String)portals.get(i);
            String [] pad = currentPad.split("/");
            if (pad[0].equalsIgnoreCase(name))
            {
                return true;
            }
        }  
        return false;
    }
    
    public void addPad(Player player, Block block)
    {
        Location loc = block.getLocation();
        List list = pads.getList("unv");
        debug("Adding a new pad, the list is:");
        if (list == null)
        {
            list = new ArrayList();
        }
        list.add((int)loc.getX()+"/"+(int)loc.getY()+"/"+(int)loc.getZ()+"/"+block.getWorld().getName()+"/"+player.getName());
        pads.set("unv", list);
        debug("set unv to add "+(int)loc.getX()+"/"+(int)loc.getY()+"/"+(int)loc.getZ()+"/"+player.getName());
        save();
        if (mc)
        {
            charge(player);
        }
    }
    
    public boolean namePad(Player player, String name)
    {
        List list = pads.getList("unv");
        if (list != null)
        {
            for (int i=0;i<list.size();i++)
            {
                String currentPad = (String)list.get(i);
                String [] pad = currentPad.split("/");
                debug("Checking if "+pad[4]+" is "+player.getName());
                if (pad[4].equalsIgnoreCase(player.getName()))
                {
                    list.remove(i);
                    pads.set("unv", list);
                    debug("removed "+currentPad+"from temp portal list");
                    if (portals == null)
                    {
                        portals = new ArrayList();
                    }
                    portals.add(name+"/"+pad[0]+"/"+pad[1]+"/"+pad[2]+"/"+pad[3]+"/"+pad[4]);
                    pads.set("pads", portals);
                    debug("named "+player.getName()+"'s portal to "+name);
                    save();
                    return true;
                }
            } 
        }
        return false;
    }
    
    public void isNamed(Player player, World world, int x, int y, int z)
    {
        List list = pads.getList("unv");
        debug("making sure that the travelpad has been named,");
        debug("checking if inv contains "+x+"/"+y+"/"+z+"/"+player.getName());
        if (list.contains(x+"/"+y+"/"+z+"/"+world.getName()+"/"+player.getName()))
        {
            debug(player.getName()+"'s pad is unnamed");
            removePadUnv(x,y,z);
        }
        else
        {
            debug(player.getName()+"'s pad is named");
        }
    }
    
    public void removePadUnv(int x, int y, int z)
    {
        List list = pads.getList("unv");
        System.out.println(list.size());
        for (int i=0;i<list.size();i++)
        {
            System.out.println("Getting "+i);
            String currentPad = (String)list.get(i);
            String [] pad = currentPad.split("/");
            if (Integer.parseInt(pad[0]) == x && Integer.parseInt(pad[1]) == y && Integer.parseInt(pad[2]) == z)
            {
                list.remove(i);
                pads.set("unv", list);
                Player player = getServer().getPlayer(pad[4]);
                player.sendMessage(ChatColor.RED+"TravelPad expired because it was not named.");
                if (rc)
                {
                    refund(player);
                }
                removeBlocks(getServer().getWorld(pad[3]), Integer.parseInt(pad[0]), Integer.parseInt(pad[1]), Integer.parseInt(pad[2]));
            }
        } 
        save();      
    }  
    
    public void removeBlocks(World world, int x, int y, int z)
    {
        Block block = world.getBlockAt(x,y,z);
        block.setType(Material.AIR);
        block.getRelative(BlockFace.EAST).setType(Material.AIR);
        block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
        block.getRelative(BlockFace.NORTH).setType(Material.AIR);
        block.getRelative(BlockFace.WEST).setType(Material.AIR);   
        ItemStack i = new ItemStack(Material.OBSIDIAN, 1);
        ItemStack e = new ItemStack(Material.BRICK, 4);
        world.dropItemNaturally(block.getLocation(), i);
        world.dropItemNaturally(block.getLocation(), e);           
    }
    
    public void removePad(String name)
    {
        for (int i=0;i<portals.size();i++)
        {
            String currentPad = (String)portals.get(i);
            String [] pad = currentPad.split("/");
            if (pad[0].equalsIgnoreCase(name))
            {
                portals.remove(i);
                pads.set("pads", portals);
                removeBlocks(getServer().getWorld(pad[4]), Integer.parseInt(pad[1]), Integer.parseInt(pad[2]), Integer.parseInt(pad[3]));
                if (rc)
                {
                    Player player = getServer().getPlayer(pad[5]);
                    refund(player);
                }
            }
        } 
        save();
    }
}


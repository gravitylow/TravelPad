package net.h31ix.travelpad;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.h31ix.travelpad.api.Configuration;
import net.h31ix.travelpad.api.Pad;
import net.h31ix.travelpad.api.TravelPadManager;
import net.h31ix.travelpad.api.UnnamedPad;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Travelpad extends JavaPlugin {
    public Configuration config;
    public TravelPadManager manager;
    public LangManager l;
    private Economy economy;
    
    
    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        if(!new File("plugins/TravelPad/config.yml").exists())
        {
            saveDefaultConfig();
        }
        if (!new File("plugins/TravelPad/pads.yml").exists())
        {
            try {
                new File("plugins/TravelPad/pads.yml").createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!new File("plugins/TravelPad/lang.yml").exists())
        {
            saveResource("lang.yml",false);
        }       
        manager = new TravelPadManager(this);
        config = manager.config;
        l = new LangManager();
        if (config.economyEnabled)
        {
            setupEconomy();
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TravelPadBlockListener(this), this);
        pm.registerEvents(new TravelPadListener(this), this);
        getCommand("travelpad").setExecutor(new TravelPadCommandExecutor(this));   
        try {
            new Metrics(this).start();
        } catch (IOException ex) {
            Logger.getLogger(Travelpad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean namePad(Player player, String name)
    {
        Object[] pads = manager.getUnnamedPadsFrom(player).toArray();
        if (pads.length == 0)
        {
            return false;
        }
        else
        {
            UnnamedPad pad = ((UnnamedPad)pads[0]);
            manager.switchPad(pad,name);
            return true;
        }
    }
    
    public Pad getPadAt(Location location)
    {
        List<Pad> list = manager.getPads();
        for (Pad pad : list)
        {
            int x = (int)pad.getLocation().getX();
            int y = (int)pad.getLocation().getY();
            int z = (int)pad.getLocation().getZ();
            int xx = (int)location.getX();
            int yy = (int)location.getY();
            int zz = (int)location.getZ();
            if (x <= xx+2 && x >= xx-2 && y <= yy+2 && y >= yy-2 && z <= zz+2 && z >= zz-2)
            {
                return pad;
            }   
        }
        return null;
    }
    
    public UnnamedPad getUnnamedPadAt(Location location)
    {
        List<UnnamedPad> list = manager.getUnnamedPads();
        for (UnnamedPad pad : list)
        {
            int x = (int)pad.getLocation().getX();
            int y = (int)pad.getLocation().getY();
            int z = (int)pad.getLocation().getZ();
            int xx = (int)location.getX();
            int yy = (int)location.getY();
            int zz = (int)location.getZ();
            if (x <= xx+2 && x >= xx-2 && y <= yy+2 && y >= yy-2 && z <= zz+2 && z >= zz-2)
            {
                return pad;
            }   
        }
        return null;        
    }
    
    public boolean hasPad(Player player)
    {
        List<Pad> pads = manager.getPadsFrom(player);
        if (pads.size() > 0)    
        {
            return true;
        }
        else
        {
            return false;
        }
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
    
    public void delete(Pad pad)
    {
        double returnValue = config.deleteAmount;
        if (returnValue != 0)
        {
            refund(getServer().getPlayer(pad.getOwner()));
        }        
        manager.deletePad(pad);
    }
    
    public void create(Location location, Player player)
    {
        double createValue = config.createAmount;
        if (createValue != 0)
        {
            charge(player);
        }         
        manager.createPad(location, player); 
    }
    
    public void teleport(Player player, Location loc)
    {
        boolean tp = true;
        boolean take = false;
        boolean found = false;
        ItemStack s = null;
        if (config.requireItem)
        {
            s = new ItemStack(Material.getMaterial(config.itemID), 1);
            for (int i=0;i<player.getInventory().getContents().length;i++)
            {
                if (player.getInventory().getContents()[i] != null)
                {
                    if(player.getInventory().getContents()[i].getType().name().equals(s.getType().name()))
                    {
                        if (config.takeItem)
                        {
                            take = true;
                        } 
                        found = true;
                    }
                }
            }
            if (found == false)
            {
                player.sendMessage(ChatColor.RED+l.travel_deny_item()+" "+s.getType().name().toLowerCase().replaceAll("_", ""));
                tp = false;
            }
        }
        if (config.chargeTeleport && tp)
        {
            if (canTeleport(player))
            {
                chargeTP(player);  
            }
            else
            {
                player.sendMessage(ChatColor.RED+l.travel_deny_money());
                tp = false;
            }
        }
        if (take && tp)
        {
            player.getInventory().removeItem(s);
            player.sendMessage(ChatColor.GOLD+s.getType().name().toLowerCase().replaceAll("_", "")+" "+l.travel_approve_item());
        }
        if (tp)
        {
            for (int i=0;i!=32;i++)
            {
                player.getWorld().playEffect(player.getLocation().add(getRandom(), getRandom(), getRandom()), Effect.SMOKE, 3);
            }
            player.teleport(loc);      
            player.sendMessage(ChatColor.GREEN+l.travel_message());            
            for (int i=0;i!=32;i++)
            {
                player.getWorld().playEffect(loc.add(getRandom(), getRandom(), getRandom()), Effect.SMOKE, 3);
            }
        }
    }
    
    public void charge(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), config.createAmount);
            player.sendMessage(ChatColor.GOLD+l.charge_message()+" "+config.createAmount);
        }
    }
    
    public void chargeTP(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), config.teleportAmount);
            player.sendMessage(ChatColor.GOLD+l.charge_message()+" "+config.teleportAmount);
        }
    }    
    
    public void refund(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {        
            economy.depositPlayer(player.getName(), config.deleteAmount);
            player.sendMessage(ChatColor.GOLD+l.refund_message()+" "+config.deleteAmount);
        }
    }   
    
    public void refundNoCreate(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {        
            economy.depositPlayer(player.getName(), config.createAmount);
            player.sendMessage(ChatColor.GOLD+l.refund_message()+" "+config.deleteAmount);
        }
    }       
    
    public boolean canTeleport(Player player)
    {
        if (player.hasPermission("travelpad.nopay"))
        {
            return true;
        }
        else if (config.economyEnabled == false)
        {
            return true;
        }        
        double balance = economy.getBalance(player.getName());
        if (balance >= config.teleportAmount)
        {           
            return true;
        }
        else
        {
            return false;
        }
    } 
    
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }      
    
    public boolean doesPadExist(String name)
    {
        return manager.getPad(name) != null;
    }
    
    public int getPads(Player player)
    {
        List<Pad> pads = manager.getPadsFrom(player);
        int has = 0;
        if (pads != null)
        {
            has = pads.size();
        }
        return has;
    }
    
    public boolean canCreate(Player player)
    {
        if (player.hasPermission("travelpad.create") || player.isOp())
        {
            List<UnnamedPad> upads = manager.getUnnamedPadsFrom(player);
            if (!upads.isEmpty())
            {  
                player.sendMessage(ChatColor.RED+l.create_deny_waiting());
                return false;
            }   
            if (config.economyEnabled)
            {
                if (!(economy.getBalance(player.getName()) >= config.createAmount))
                {
                    player.sendMessage(ChatColor.RED+"Not enough money!");
                    return false;
                }
            }
            int allow = config.getAllowedPads(player);
            List<Pad> pads = manager.getPadsFrom(player);
            int has = 0;
            if (pads != null)
            {
                has = pads.size();
            }
            if (allow < 0 || allow > has)
            {
                return true;
            }
            else
            {
                player.sendMessage(ChatColor.RED+l.create_deny_max());
                return false;
            }
        }
        else
        {
            player.sendMessage(ChatColor.RED+l.command_deny_permission());
           return false; 
        }
    }
}


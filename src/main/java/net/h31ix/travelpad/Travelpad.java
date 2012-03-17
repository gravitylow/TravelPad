package net.h31ix.travelpad;

import java.io.File;
import java.io.IOException;
import java.util.Set;
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
    public TravelPadManager manager = new TravelPadManager();
    private Economy economy;
    
    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        getCommand("travelpad").setExecutor(new TravelPadCommandExecutor(this));
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
        config = new Configuration();
        if (config.economyEnabled)
        {
            setupEconomy();
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TravelPadBlockListener(this), this);
    }
    
    public boolean namePad(Player player, String name)
    {
        if (manager.getUnnamedPadsFrom(player) != null)
        {
            Object[] pads = manager.getUnnamedPadsFrom(player).toArray();
            UnnamedPad pad = ((UnnamedPad)pads[0]);
            config.addPad(new Pad(pad.getLocation(), player.getName(), name, false));
            pad.name();
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean hasPad(Player player)
    {
        Set<Pad> pads = manager.getPadsFrom(player);
        if (pads != null)    
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
    
    public void teleport(Player player, Location loc)
    {
        boolean tp = true;
        boolean take = false;
        boolean found = false;
        boolean fac = true;
        if (getServer().getPluginManager().getPlugin("Factions") != null)
        {
            //if ()
        }
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
                player.sendMessage(ChatColor.RED+"You must have one "+s.getType().name().toLowerCase().replaceAll("_", "")+" to travel!");
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
                player.sendMessage(ChatColor.RED+"You cannot afford that!");
                tp = false;
            }
        }
        if (take && tp)
        {
            player.getInventory().removeItem(s);
            player.sendMessage(ChatColor.GOLD+"One "+s.getType().name().toLowerCase().replaceAll("_", "")+" has been collected from you for travel.");
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
    
    public void charge(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), config.createAmount);
            player.sendMessage(ChatColor.GOLD+"You were charged "+config.createAmount);
        }
    }
    
    @Deprecated    
    public void chargeTP(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), config.teleportAmount);
            player.sendMessage(ChatColor.GOLD+"You were charged "+config.teleportAmount);
        }
    }    
    
    public void refund(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {        
            economy.depositPlayer(player.getName(), config.deleteAmount);
            player.sendMessage(ChatColor.GOLD+"You were refunded "+config.deleteAmount);
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
        Set<Pad> pads = manager.getPadsFrom(player);
        int has = 0;
        if (pads != null)
        {
            has = pads.size();
        }
        return has;
    }
    
    public boolean canCreate(Player player)
    {
        //TODO: ADD ECONOMY CHECKS
        if (player.hasPermission("travelpad.create"))
        {
            Set<UnnamedPad> upads = manager.getUnnamedPadsFrom(player);
            if (!upads.isEmpty())
            {     
                return false;
            }   
            else
            {
                player.sendMessage(ChatColor.RED+"You already have a pad waiting to be named...");
            }
            int allow = config.getAllowedPads(player);
            Set<Pad> pads = manager.getPadsFrom(player);
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
                player.sendMessage(ChatColor.RED+"You already have "+has+" pads!");
                return false;
            }
        }
        else
        {
           return false; 
        }
    }
}


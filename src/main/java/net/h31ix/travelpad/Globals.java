package net.h31ix.travelpad;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Globals {
    
    public Configuration config = new Configuration();
    public boolean debug = false;
    public Economy economy = null;    
    
    public Globals()
    {
        
    }
    
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
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
    
    public void charge(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), config.getMake());
            player.sendMessage(ChatColor.GOLD+"You were charged "+config.getMake());
        }
    }
    
    public void chargeTP(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {
            economy.withdrawPlayer(player.getName(), config.getTeleport());
            player.sendMessage(ChatColor.GOLD+"You were charged "+config.getTeleport());
        }
    }    
    
    public void refund(Player player)
    {
        if (!player.hasPermission("travelpad.nopay"))
        {        
            economy.depositPlayer(player.getName(), config.getReturn());
            player.sendMessage(ChatColor.GOLD+"You were refunded "+config.getReturn());
        }
    }   
    
}

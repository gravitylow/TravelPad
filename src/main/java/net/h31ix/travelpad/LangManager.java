package net.h31ix.travelpad;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LangManager {
    private FileConfiguration lang = YamlConfiguration.loadConfiguration(new File("plugins/TravelPad/lang.yml"));
   
    public String travel_deny_item()
    {
        return lang.getString("travel_deny_item");
    }
    
    public String travel_deny_money()
    {
        return lang.getString("travel_deny_money");
    }
    
    public String travel_approve_item()
    {
        return lang.getString("travel_approve_item");
    }    
    
    public String travel_message()
    {
        return lang.getString("travel_message");
    }   
    
    public String charge_message()
    {
        return lang.getString("charge_message");
    }    
    
    public String refund_message()
    {
        return lang.getString("refund_message");
    }     
    
    public String create_deny_waiting()
    {
        return lang.getString("create_deny_waiting");
    }      
    
    public String create_deny_max()
    {
        return lang.getString("create_deny_max");
    }
    
    public String identify_found_message()
    {
        return lang.getString("identify_found_message");
    }
    
    public String identify_notfound_message()
    {
        return lang.getString("identify_notfound_message");
    }    
    
    public String delete_deny_multi()
    {
        return lang.getString("delete_deny_multi");
    }   
    
    public String delete_deny_noportal()
    {
        return lang.getString("delete_deny_noportal");
    }    
    
    public String delete_deny_notfound()
    {
        return lang.getString("delete_deny_notfound");
    }     
    
    public String delete_approve()
    {
        return lang.getString("delete_approve");
    }  
    
    public String command_deny_permission()
    {
        return lang.getString("command_deny_permission");
    }    
    
    public String teleport_deny_notfound()
    {
        return lang.getString("teleport_deny_notfound");
    }   
    
    public String teleport_deny_loc()
    {
        return lang.getString("teleport_deny_loc");
    }    
    
    public String name_message()
    {
        return lang.getString("name_message");
    }     
    
    public String name_deny_nopad()
    {
        return lang.getString("name_deny_nopad");
    }     
    
    public String name_deny_inuse()
    {
        return lang.getString("name_deny_inuse");
    }    
    
    public String command_identify()
    {
        return lang.getString("command_identify");
    }   
    
    public String command_identify_shortcut()
    {
        return lang.getString("command_identify_shortcut");
    }    
    
    public String command_delete()
    {
        return lang.getString("command_delete");
    }     
    
    public String command_delete_shortcut()
    {
        return lang.getString("command_delete_shortcut");
    }   
    
    public String command_teleport()
    {
        return lang.getString("command_teleport");
    }    
    
    public String command_teleport_shortcut()
    {
        return lang.getString("command_teleport_shortcut");
    }   
    
    public String command_name()
    {
        return lang.getString("command_name");
    }    
    
    public String command_name_shortcut()
    {
        return lang.getString("command_name_shortcut");
    }     
    
    public String command_whitelist()
    {
        return lang.getString("command_whitelist");
    }    
    
    public String command_whitelist_shortcut()
    {
        return lang.getString("command_whitelist_shortcut");
    } 
    
    public String command_unwhitelist()
    {
        return lang.getString("command_unwhitelist");
    }    
    
    public String command_unwhitelist_shortcut()
    {
        return lang.getString("command_unwhitelist_shortcut");
    }     
    
    public String create_approve_1()
    {
        return lang.getString("create_approve_1");
    }    
    
    public String create_approve_2()
    {
        return lang.getString("create_approve_2");
    }        
    
    public String command_deny_console()
    {
        return lang.getString("command_deny_console");
    }     
    
    public String pad_expire()
    {
        return lang.getString("pad_expire");
    }      
}

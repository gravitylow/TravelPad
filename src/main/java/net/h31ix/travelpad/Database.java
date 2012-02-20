package net.h31ix.travelpad;

/*
 * TravelPad for Bukkit
 * 
 * @author The1Domo
 * 
 * H31IX.NET
 */

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private Connection conn     = null;

    public Database(String address, String username, String password, String database)
    {
        try{
            //Create the database connection
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn        =  DriverManager.getConnection("jdbc:mysql://"+address+"/"+database+"?autoReconnect=true",username,password);
        }
        catch(Exception e) {
            System.out.println("Database error: "+ e.getMessage() +" - "+e.getLocalizedMessage());
            System.exit(-1);
        }
    }
    
    public void close()
    {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ResultSet query(String query) {
         try {
             //Execute the query
             Statement stat     = conn.createStatement();
             return stat.executeQuery(query);
        }
        catch(Exception e) {
            System.out.println("Database warning: "+e.getMessage());
            return null;
        }
     }

     public ResultSet query(String query, String... argList) {
         try {
             //Execute the query
             PreparedStatement prepStat     = conn.prepareStatement(query);

             int counter = 1;
             for(String arg : argList) {
                 prepStat.setString(counter, arg);
                 counter++;
             }

             return prepStat.executeQuery();
        }
        catch(Exception e) {
            System.out.println("Database warning: "+e.getMessage());
            return null;
        }
     }

     public boolean execute(String query) {
         try {
             //Execute the query
             Statement stat     = conn.createStatement();
             return stat.execute(query);
        }
        catch(Exception e) {
            System.out.println("Database warning: "+e.getMessage());
            return false;
        }
     }

     public boolean execute(String query, String... argList) {
         try {
             //Execute the query
             PreparedStatement prepStat     = conn.prepareStatement(query);

             int counter = 1;
             for(String arg : argList) {
                 prepStat.setString(counter, arg);
                 counter++;
             }

             return prepStat.execute();
        }
        catch(Exception e) {
            System.out.println("Database warning: "+e.getMessage());
            return false;
        }
     }

}

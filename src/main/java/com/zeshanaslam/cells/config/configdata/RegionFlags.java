 package com.zeshanaslam.cells.config.configdata;
 
 import com.sk89q.worldguard.protection.regions.ProtectedRegion;
 import com.zeshanaslam.cells.Main;
 import com.zeshanaslam.cells.config.configdata.cells.Cell;
 import java.util.HashMap;
 import org.bukkit.Bukkit;
 import org.bukkit.command.CommandSender;
 
 public class RegionFlags
 {
   public int priority;
   public HashMap<String, String> flags;
   
   public RegionFlags(Main main) {
     this.priority = main.getConfig().getInt("Region.Priority");
     
     this.flags = new HashMap<>();
     for (String key : main.getConfig().getConfigurationSection("Region.DefaultFlags").getKeys(false)) {
       String value = main.getConfig().getString("Region.DefaultFlags." + key + ".Value");
       
       this.flags.put(key, value);
     } 
   }
   
   public void addRegionFlags(ProtectedRegion protectedRegion, Cell cell, String world) {
     for (String flag : this.flags.keySet()) {
       String value = this.flags.get(flag);
       value = value.replace("%cellname%", cell.name);
       
       Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "region flag " + protectedRegion.getId() + " -w " + world + " " + flag + " " + value);
     } 
   }
 }



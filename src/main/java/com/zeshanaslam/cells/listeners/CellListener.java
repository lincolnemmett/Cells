 package com.zeshanaslam.cells.listeners;
 
 import com.zeshanaslam.cells.Main;
 import com.zeshanaslam.cells.config.configdata.cells.Cell;
 import com.zeshanaslam.cells.utils.CellHelpers;
 import java.util.List;
 import org.bukkit.Location;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.PlayerJoinEvent;
 import org.bukkit.event.player.PlayerRespawnEvent;
 
 
 public class CellListener
   implements Listener
 {
   private Main main;
   private CellHelpers cellHelpers;
   
   public CellListener(Main plugin) {
     this.main = plugin;
     this.cellHelpers = new CellHelpers(this.main);
   }
   
   @EventHandler
   public void onDie(PlayerRespawnEvent event) {
     Player player = event.getPlayer();
     Location location = getHome(player);
     if (location == null) {
       return;
     }
     event.setRespawnLocation(location);
   }
   
   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
     Player player = event.getPlayer();
     Location location = getHome(player);
     if (location == null) {
       return;
     }
     player.teleport(location);
   }
   
   private Location getHome(Player player) {
     List<Cell> cells = this.cellHelpers.getPlayerCells(player);
     if (!cells.isEmpty()) {
       Cell cell = cells.get(0);
       
       if (cell.home != null) {
         return cell.home.getLocation();
       }
     } 
     
     return null;
   }
 }


